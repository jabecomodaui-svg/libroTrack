package dao;

import model.Borrower;
import model.BorrowTransaction;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private static final String BASE_SELECT =
            "SELECT t.*, b.full_name AS borrower_name, k.title AS book_title "
          + "FROM borrow_transactions t "
          + "JOIN borrowers b ON t.borrower_id = b.borrower_id "
          + "JOIN books k ON t.book_id = k.book_id ";

    private BorrowTransaction mapRow(ResultSet rs) throws SQLException {
        BorrowTransaction t = new BorrowTransaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setBorrowerId(rs.getInt("borrower_id"));
        t.setBookId(rs.getInt("book_id"));
        t.setBorrowDate(rs.getDate("borrow_date").toLocalDate());   // set before the due date
        t.setDueDate(rs.getDate("due_date").toLocalDate());
        Date returned = rs.getDate("return_date");
        if (returned != null) {
            t.setReturnDate(returned.toLocalDate());
        }
        t.setFineAmount(rs.getDouble("fine_amount"));
        t.setBorrowerName(rs.getString("borrower_name"));
        t.setBookTitle(rs.getString("book_title"));
        return t;
    }

    private List<BorrowTransaction> runQuery(String sql) throws SQLException {
        List<BorrowTransaction> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Books that are borrowed right now (return_date is still empty)
    public List<BorrowTransaction> getCurrentlyBorrowed() throws SQLException {
        return runQuery(BASE_SELECT + "WHERE t.return_date IS NULL ORDER BY t.due_date");
    }

    // Overdue = not returned AND due date is before today
    public List<BorrowTransaction> getOverdue() throws SQLException {
        return runQuery(BASE_SELECT + "WHERE t.return_date IS NULL AND t.due_date < CURDATE() ORDER BY t.due_date");
    }

    // Most borrowed books: each row is {title, number of times borrowed}
    public List<Object[]> getMostBorrowed() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT k.title, COUNT(*) AS times FROM borrow_transactions t "
                   + "JOIN books k ON t.book_id = k.book_id "
                   + "GROUP BY k.book_id, k.title ORDER BY times DESC, k.title LIMIT 10";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Object[] { rs.getString("title"), rs.getInt("times") });
            }
        }
        return rows;
    }

    // How many books does this borrower have right now?
    public int countCurrentlyBorrowed(int borrowerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrow_transactions WHERE borrower_id = ? AND return_date IS NULL";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowerId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // BORROW a book.
    // Throws IllegalStateException with a readable message if the rules are broken.
    public void borrowBook(int borrowerId, int bookId) throws SQLException {
        // Rule 1: the borrower must not exceed his/her limit (Student 3, Faculty 5)
        Borrower borrower = new BorrowerDAO().getById(borrowerId);
        int limit = borrower.getBorrowLimit();          // polymorphism
        if (countCurrentlyBorrowed(borrowerId) >= limit) {
            throw new IllegalStateException(borrower.getFullName() + " already reached the limit of "
                    + limit + " books.");
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);    // do both steps together, or none
            try {
                // Rule 2: a copy must be available. This lowers the copies and checks them in one step.
                String sqlBook = "UPDATE books SET available_copies = available_copies - 1 "
                               + "WHERE book_id = ? AND available_copies > 0";
                try (PreparedStatement ps = con.prepareStatement(sqlBook)) {
                    ps.setInt(1, bookId);
                    if (ps.executeUpdate() == 0) {
                        throw new IllegalStateException("No copies of this book are available.");
                    }
                }

                LocalDate today = LocalDate.now();
                String sqlInsert = "INSERT INTO borrow_transactions (borrower_id, book_id, borrow_date, due_date) "
                                 + "VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                    ps.setInt(1, borrowerId);
                    ps.setInt(2, bookId);
                    ps.setDate(3, Date.valueOf(today));
                    ps.setDate(4, Date.valueOf(today.plusDays(BorrowTransaction.LOAN_DAYS)));
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // RETURN a book. Saves the return date and the fine. Returns the fine amount.
    public double returnBook(int transactionId) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int bookId = 0;
                BorrowTransaction t = new BorrowTransaction();

                String sqlFind = "SELECT book_id, borrow_date, due_date FROM borrow_transactions "
                               + "WHERE transaction_id = ? AND return_date IS NULL";
                try (PreparedStatement ps = con.prepareStatement(sqlFind)) {
                    ps.setInt(1, transactionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("This book was already returned.");
                        }
                        bookId = rs.getInt("book_id");
                        t.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                        t.setDueDate(rs.getDate("due_date").toLocalDate());
                    }
                }

                t.setReturnDate(LocalDate.now());
                double fine = t.computeFine();       // days late x fine per day

                String sqlReturn = "UPDATE borrow_transactions SET return_date = ?, fine_amount = ? "
                                 + "WHERE transaction_id = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlReturn)) {
                    ps.setDate(1, Date.valueOf(t.getReturnDate()));
                    ps.setDouble(2, fine);
                    ps.setInt(3, transactionId);
                    ps.executeUpdate();
                }

                String sqlBook = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlBook)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }
                con.commit();
                return fine;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
