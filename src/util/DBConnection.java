package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Opens a connection to MySQL. Every DAO class calls getConnection().
public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASS = "ChocoBells#2024";   // <-- CHANGE THIS

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Run this file alone to test the connection
    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            System.out.println("Connected to MySQL!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
