package dao;

import model.Admin;
import model.Borrower;
import model.BorrowerUser;
import model.Librarian;
import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Creates the right child object for a role, but returns it as a User (polymorphism)
    private User createUser(String role) {
        switch (role) {
            case "ADMIN":     return new Admin();
            case "LIBRARIAN": return new Librarian();
            default:          return new BorrowerUser();
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = createUser(rs.getString("role"));
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        return u;
    }

    // LOGIN: returns the matching user, or null if the username/password is wrong
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // READ: all librarian accounts
    public List<User> getLibrarians() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'LIBRARIAN' ORDER BY full_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // CREATE (the role comes from the object itself: u.getRole())
    public void addUser(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getRole());
            ps.executeUpdate();
        }
    }

    // UPDATE
    public void updateUser(User u) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, full_name = ? WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setInt(4, u.getUserId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // BORROWER REGISTRATION: creates the login account AND the borrower record together.
    // If one insert fails, both are cancelled (rollback).
    public void registerBorrower(User user, Borrower borrower) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int newUserId;
                String sqlUser = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getFullName());
                    ps.setString(4, user.getRole());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        newUserId = keys.getInt(1);
                    }
                }

                String sqlBorrower = "INSERT INTO borrowers (user_id, full_name, borrower_type, contact, address) "
                                   + "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlBorrower)) {
                    ps.setInt(1, newUserId);
                    ps.setString(2, borrower.getFullName());
                    ps.setString(3, borrower.getBorrowerType());
                    ps.setString(4, borrower.getContact());
                    ps.setString(5, borrower.getAddress());
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
