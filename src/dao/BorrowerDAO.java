package dao;

import model.Borrower;
import model.Faculty;
import model.Student;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowerDAO {

    // Creates a Student or a Faculty object, but returns it as a Borrower (polymorphism)
    public static Borrower create(String type) {
        if (type.equals("FACULTY")) {
            return new Faculty();
        }
        return new Student();
    }

    private Borrower mapRow(ResultSet rs) throws SQLException {
        Borrower b = create(rs.getString("borrower_type"));
        b.setBorrowerId(rs.getInt("borrower_id"));
        b.setFullName(rs.getString("full_name"));
        b.setContact(rs.getString("contact"));
        b.setAddress(rs.getString("address"));
        return b;
    }

    public List<Borrower> getAll() throws SQLException {
        List<Borrower> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowers ORDER BY full_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Borrower getById(int borrowerId) throws SQLException {
        String sql = "SELECT * FROM borrowers WHERE borrower_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public void add(Borrower b) throws SQLException {
        String sql = "INSERT INTO borrowers (full_name, borrower_type, contact, address) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getFullName());
            ps.setString(2, b.getBorrowerType());
            ps.setString(3, b.getContact());
            ps.setString(4, b.getAddress());
            ps.executeUpdate();
        }
    }

    public void update(Borrower b) throws SQLException {
        String sql = "UPDATE borrowers SET full_name = ?, borrower_type = ?, contact = ?, address = ? "
                   + "WHERE borrower_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getFullName());
            ps.setString(2, b.getBorrowerType());
            ps.setString(3, b.getContact());
            ps.setString(4, b.getAddress());
            ps.setInt(5, b.getBorrowerId());
            ps.executeUpdate();
        }
    }

    public void delete(int borrowerId) throws SQLException {
        String sql = "DELETE FROM borrowers WHERE borrower_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowerId);
            ps.executeUpdate();
        }
    }
}
