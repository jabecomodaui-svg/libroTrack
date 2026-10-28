package dao;

import model.Author;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {

    public List<Author> getAll() throws SQLException {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT * FROM authors ORDER BY author_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Author(rs.getInt("author_id"), rs.getString("author_name")));
            }
        }
        return list;
    }

    public void add(Author a) throws SQLException {
        String sql = "INSERT INTO authors (author_name) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAuthorName());
            ps.executeUpdate();
        }
    }

    public void update(Author a) throws SQLException {
        String sql = "UPDATE authors SET author_name = ? WHERE author_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAuthorName());
            ps.setInt(2, a.getAuthorId());
            ps.executeUpdate();
        }
    }

    public void delete(int authorId) throws SQLException {
        String sql = "DELETE FROM authors WHERE author_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            ps.executeUpdate();
        }
    }
}
