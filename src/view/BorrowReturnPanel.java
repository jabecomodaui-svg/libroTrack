package view;

import dao.BookDAO;
import dao.BorrowerDAO;
import dao.TransactionDAO;
import model.Book;
import model.Borrower;
import model.BorrowTransaction;
import util.UI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Top: borrow a book.  Bottom: the books that are out right now, with a Return button.
public class BorrowReturnPanel extends JPanel implements Refreshable {
    private BorrowerDAO borrowerDao = new BorrowerDAO();
    private BookDAO bookDao = new BookDAO();
    private TransactionDAO transactionDao = new TransactionDAO();
    private List<BorrowTransaction> loans = new ArrayList<>();   // same order as the table rows

    private JComboBox<Borrower> cboBorrower = new JComboBox<>();
    private JComboBox<Book> cboBook = new JComboBox<>();

    private DefaultTableModel model = UI.readOnlyModel("ID", "Borrower", "Book", "Borrowed On",
            "Due Date", "Status", "Fine So Far (PHP)");
    private JTable table = new JTable(model);

    public BorrowReturnPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ----- Borrow section -----
        JButton btnBorrow = new JButton("Borrow");
        JPanel borrowBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        borrowBar.setBorder(BorderFactory.createTitledBorder("Borrow a book (due in "
                + BorrowTransaction.LOAN_DAYS + " days, fine PHP "
                + BorrowTransaction.FINE_PER_DAY + " per late day)"));
        borrowBar.add(new JLabel("Borrower:"));
        borrowBar.add(cboBorrower);
        borrowBar.add(new JLabel("Book:"));
        borrowBar.add(cboBook);
        borrowBar.add(btnBorrow);

        // ----- Return section -----
        JButton btnReturn = new JButton("Return Selected Book");
        JPanel returnBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        returnBar.add(btnReturn);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.setBorder(BorderFactory.createTitledBorder("Books currently borrowed"));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(returnBar, BorderLayout.SOUTH);

        add(borrowBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        btnBorrow.addActionListener(e -> borrowBook());
        btnReturn.addActionListener(e -> returnBook());

        refresh();
    }

    @Override
    public void refresh() {
        try {
            cboBorrower.removeAllItems();
            for (Borrower b : borrowerDao.getAll()) {
                cboBorrower.addItem(b);
            }
            cboBook.removeAllItems();
            for (Book b : bookDao.getAvailable()) {   // only books with copies left
                cboBook.addItem(b);
            }

            loans = transactionDao.getCurrentlyBorrowed();
            model.setRowCount(0);
            for (BorrowTransaction t : loans) {
                String status = t.isOverdue() ? "OVERDUE" : "On time";
                model.addRow(new Object[] { t.getTransactionId(), t.getBorrowerName(), t.getBookTitle(),
                        t.getBorrowDate(), t.getDueDate(), status, String.format("%.2f", t.computeFine()) });
            }
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void borrowBook() {
        Borrower borrower = (Borrower) cboBorrower.getSelectedItem();
        Book book = (Book) cboBook.getSelectedItem();
        if (borrower == null || book == null) {
            UI.error("Choose a borrower and a book first.");
            return;
        }
        try {
            transactionDao.borrowBook(borrower.getBorrowerId(), book.getBookId());
            UI.info("Done! The book is due in " + BorrowTransaction.LOAN_DAYS + " days.");
            refresh();
        } catch (IllegalStateException ex) {
            UI.error(ex.getMessage());    // e.g. "already reached the limit of 3 books"
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void returnBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select the borrowed book in the table first.");
            return;
        }
        try {
            double fine = transactionDao.returnBook(loans.get(row).getTransactionId());
            if (fine > 0) {
                UI.info("Book returned LATE. Fine to collect: PHP " + String.format("%.2f", fine));
            } else {
                UI.info("Book returned on time. No fine.");
            }
            refresh();
        } catch (IllegalStateException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }
}
