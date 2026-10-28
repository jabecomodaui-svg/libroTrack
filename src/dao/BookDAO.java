package dao;

import model.Book;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Same SELECT is reused by every read method. JOIN gives us the author and category names.
    private static final String BASE_SELECT =
            "SELECT k.*, a.author_name, c.category_name FROM books k "
          + "JOIN authors a ON k.author_id = a.author_id "
          + "JOIN categories c ON k.category_id = c.category_id ";

    // Turns one database row into one Book object
    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId(rs.getInt("book_id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthorId(rs.getInt("author_id"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setPublisher(rs.getString("publisher"));
        b.setYearPublished(rs.getInt("year_published"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setShelfLocation(rs.getString("shelf_location"));
        b.setAuthorName(rs.getString("author_name"));
        b.setCategoryName(rs.getString("category_name"));
        return b;
    }

    private List<Book> runQuery(String sql, String keyword) throws SQLException {
        List<Book> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (keyword != null) {
                ps.setString(1, "%" + keyword + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // READ: all books
    public List<Book> getAll() throws SQLException {
        return runQuery(BASE_SELECT + "ORDER BY k.title", null);
    }

    // READ: only books that still have copies (used when borrowing)
    public List<Book> getAvailable() throws SQLException {
        return runQuery(BASE_SELECT + "WHERE k.available_copies > 0 ORDER BY k.title", null);
    }

    // SEARCH by title, author, ISBN or category
    public List<Book> search(String searchBy, String keyword) throws SQLException {
        String column;
        switch (searchBy) {
            case "Title":  column = "k.title"; break;
            case "Author": column = "a.author_name"; break;
            case "ISBN":   column = "k.isbn"; break;
            default:       column = "c.category_name"; break;
        }
        return runQuery(BASE_SELECT + "WHERE " + column + " LIKE ? ORDER BY k.title", keyword);
    }

    // CREATE
    public void add(Book b) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author_id, category_id, publisher, "
                   + "year_published, total_copies, available_copies, shelf_location) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getIsbn());
            ps.setString(2, b.getTitle());
            ps.setInt(3, b.getAuthorId());
            ps.setInt(4, b.getCategoryId());
            ps.setString(5, b.getPublisher());
            ps.setInt(6, b.getYearPublished());
            ps.setInt(7, b.getTotalCopies());
            ps.setInt(8, b.getAvailableCopies());
            ps.setString(9, b.getShelfLocation());
            ps.executeUpdate();
        }
    }

    // UPDATE
    // available_copies = new total minus the copies that are still borrowed
    public void update(Book b) throws SQLException {
        String sql = "UPDATE books SET isbn = ?, title = ?, author_id = ?, category_id = ?, "
                   + "publisher = ?, year_published = ?, total_copies = ?, shelf_location = ?, "
                   + "available_copies = GREATEST(? - (SELECT COUNT(*) FROM borrow_transactions "
                   + "WHERE book_id = ? AND return_date IS NULL), 0) "
                   + "WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getIsbn());
            ps.setString(2, b.getTitle());
            ps.setInt(3, b.getAuthorId());
            ps.setInt(4, b.getCategoryId());
            ps.setString(5, b.getPublisher());
            ps.setInt(6, b.getYearPublished());
            ps.setInt(7, b.getTotalCopies());
            ps.setString(8, b.getShelfLocation());
            ps.setInt(9, b.getTotalCopies());
            ps.setInt(10, b.getBookId());
            ps.setInt(11, b.getBookId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        }
    }
}
