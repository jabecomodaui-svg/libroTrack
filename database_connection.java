import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database_connection {

    // Database connection details
    private static final String URL =
            "jdbc:mysql://localhost:3306/library_management_system";

    private static final String USERNAME = "database";
    private static final String PASSWORD = "IlisiNi2026!";

    // Connect to database
    public static Connection connect() {

        try {

            Connection con = DriverManager.getConnection(
                    URL,
                    USERNAME,
                    PASSWORD
            );

            System.out.println("Database connected successfully!");

            return con;

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());

            return null;
        }
    }

    // Run this file alone to test the connection
    public static void main(String[] args) {

        Connection con = connect();

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}