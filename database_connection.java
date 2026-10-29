import java.sql.Connection;    // Represents an active connection between Java and the database.
import java.sql.DriverManager; // Creates a database connection using the URL and credentials.
import java.sql.SQLException;  // Represents errors that occur during database operations.

public class database_connection { // Defines the class used by the other Java files to connect to MySQL.

    // ==================================================
    // 1. DATABASE CONNECTION SETTINGS
    // Purpose: Store the MySQL URL and login credentials.
    // ==================================================
    private static final String URL = // Declares a constant containing the database connection address.
            "jdbc:mysql://localhost:3306/library_management_system"; // Connects to this MySQL database on the local computer.

    private static final String USERNAME = "database"; // Stores the MySQL username used by this application.
    private static final String PASSWORD = "IlisiNi2026!"; // Stores the MySQL password; replace it with your actual password.

    // ==================================================
    // 2. CONNECT TO MYSQL
    // Purpose: Open and return a connection for other Java files.
    // Returns: Connection when successful, or null if it fails.
    // ==================================================
    public static Connection connect() { // Defines a method any class can call to connect to the database.

        try { // Starts a block where a database error can be caught.
            Connection con = DriverManager.getConnection( // Requests a new database connection.
                    URL, // Provides the database URL.
                    USERNAME, // Provides the MySQL username.
                    PASSWORD // Provides the MySQL password.
            ); // Finishes creating the connection.

            System.out.println("Database connected successfully!"); // Prints a success message in the terminal.
            return con; // Gives the open connection back to the code that called this method.

        } catch (SQLException e) { // Handles errors such as an incorrect password or unavailable MySQL server.
            System.out.println("Database connection failed!"); // Prints a general connection failure message.
            System.out.println(e.getMessage()); // Prints the specific database error for troubleshooting.
            return null; // Tells the calling code that no connection was created.
        } // Ends the error-handling block.
    } // Ends the connect method.

    // ==================================================
    // 3. TEST DATABASE CONNECTION
    // Purpose: Allow this file to run on its own to check the connection.
    // ==================================================
    public static void main(String[] args) { // Defines the starting point when this file is run directly.

        Connection con = connect(); // Calls the connect method to test the database connection.

        if (con != null) { // Checks whether the connection was successful before trying to close it.
            try { // Starts a block where a closing error can be caught.
                con.close(); // Closes the database connection to release resources.
                System.out.println("Database connection closed."); // Confirms that the connection was closed.
            } catch (SQLException e) { // Handles any error that occurs while closing the connection.
                System.out.println("Could not close connection:"); // Prints a message if closing failed.
                System.out.println(e.getMessage()); // Prints details about the closing error.
            } // Ends the closing error-handling block.
        } // Ends the check for a successful connection.
    } // Ends the main method.
} // Ends the database_connection class.
