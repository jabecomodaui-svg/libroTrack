
import java.awt.*; // Provides GUI layouts, fonts, colors, and other visual components.
import java.security.MessageDigest; // Compares password hashes securely.
import java.security.SecureRandom; // Generates random values for password hashing.
import java.sql.*; // Provides classes for MySQL database operations.
import java.time.LocalDate; // Gets the current date.
import java.util.Arrays; // Compares arrays and clears password data from memory.
import java.util.Base64; // Converts binary data into Base64 strings.

import javax.crypto.SecretKeyFactory; // Generates password hashes.
import javax.crypto.spec.PBEKeySpec; // Supplies password hashing settings.
import javax.swing.*; // Provides GUI components such as buttons, labels, and windows.
import javax.swing.table.DefaultTableModel; // Manages data displayed in JTable.

public class admin extends JFrame {  // Declares the Admin window class and inherits JFrame window features.

    // =========================================================
    // 1. GENERAL AND LOGIN FIELDS
    // Purpose: Store Admin Login and Registration form variables.
    // =========================================================

    JTextField usernameField;       // Stores the Admin username input field.
    JPasswordField passwordField;   // Stores the Admin password input field.

    JTextField regFullName;         // Stores the Admin's full name input field.
    JTextField regUsername;         // Stores the new Admin username input field.
    JPasswordField regPassword;    // Stores the new Admin password input field.
    JPasswordField regConfirm;     // Stores the confirmation password input field.

    String adminName = "";          // Stores the name of the currently logged-in Admin.

    // =========================================================
    // 2. MANAGE MEMBERS FIELDS
    // Purpose: Store input fields and selected member information.
    // =========================================================

    JTextField memberStudentId;       // Stores the Student ID input field.
    JTextField memberFullName;        // Stores the Full Name input field.
    JTextField memberCourse;          // Stores the Course input field.

    JTextField memberYearLevel;       // Stores the Year Level input field.
    JTextField memberSection;         // Stores the Section input field.
    JTextField memberContact;         // Stores the Contact Number input field.

    JTextField memberAddress;         // Stores the Address input field.
    JTextField memberRegistrationDate; // Stores the Registration Date input field.

    JPasswordField memberPassword;    // Stores the password input field for a member.
    JComboBox<String> memberStatusBox; // Allows selection of Active or Inactive status.

    JTable membersTable;              // Displays student member records in a table.
    DefaultTableModel membersModel;   // Stores and manages the data displayed in the table.

    int selectedMemberId = -1;        // Stores the selected Member ID; -1 means no member is selected.
    // =========================================================
    // 3. MANAGE BOOKS FIELDS
    // Purpose: Store book input fields and selected book information.
    // =========================================================

    JTextField bookIsbn;       // Stores the ISBN input field of the book.
    JTextField bookTitle;      // Stores the title input field of the book.
    JTextField bookAuthor;     // Stores the author's name input field.
    JTextField bookCategory;   // Stores the category input field of the book.

    JTextField bookPublisher;  // Stores the publisher's name input field.
    JTextField bookYear;       // Stores the publication year of the book.
    JTextField bookQuantity;   // Stores the total number of book copies.
    JTextField bookAvailable;  // Stores the number of available book copies.

    JTable booksTable;         // Displays book records in a table.
    DefaultTableModel booksModel; // Stores and manages the data displayed in the books table.

    int selectedBookId = -1;   // Stores the selected Book ID; -1 means no book is selected.

    // =========================================================
    // 4. RESERVATION FIELDS
    // Purpose: Store the reservation table, current filter, and selected reservation.
    // =========================================================

    JTable reservationsTable;             // Displays reservation records in a table.
    DefaultTableModel reservationsModel;  // Stores and manages the reservation table data.

    String reservationFilter = "Pending"; // Filters reservations by status; defaults to Pending.

    int selectedReservationId = -1;       // Stores the selected Reservation ID; -1 means no reservation is selected.

    // =========================================================
    // 5. BOOK USAGE FIELDS
    // Purpose: Store usage records and selected usage ID.
    // =========================================================
    JTable usageTable;                    // Displays book usage records in a table.
    DefaultTableModel usageModel;         // Stores and manages the data shown in the usage table.
    String usageFilter = "Using";         // Filters usage records; shows currently used books by default.
    int selectedUsageId = -1;             // Stores the selected usage ID; -1 means no record is selected.

    // =========================================================
    // 6. START APPLICATION
    // Purpose: Open the Admin Login screen.
    // =========================================================
    public admin() {                                                              // Declares the admin method.
        showLogin();                                                              // Performs the operation described by this statement.
    }                                                                             // Ends this code block.

    // =========================================================
    // 7. ADMIN LOGIN SCREEN
    // Purpose: Show username/password inputs and login/register buttons.
    // =========================================================
    public void showLogin() {                                                     // Declares the showLogin method.
        getContentPane().removeAll();                                             // Removes existing components before building the next screen.

        setTitle("Library Management System - Admin Login");                      // Sets the title displayed in the window title bar.
        setSize(400, 320);                                                        // Sets the window or component dimensions.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                           // Sets what happens when the user closes this window.
        setLocationRelativeTo(null);                                              // Positions the window relative to the specified component or screen.

        // Main panel with border layout to center the form.
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));                  // Creates a panel to group and arrange GUI components.
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));     // Sets padding around the panel.

        // Form panel: labels on the left, text fields on the right.
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));              // Creates a panel for username/password rows.
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));        // Adds bottom spacing before buttons.

        usernameField = new JTextField();                                         // Creates a text input field for user-entered text.
        passwordField = new JPasswordField();                                     // Creates a password input field that masks typed characters.

        formPanel.add(new JLabel("Username:"));                                   // Creates a label to display text in the GUI.
        formPanel.add(usernameField);                                             // Adds this component to its parent container or panel.
        formPanel.add(new JLabel("Password:"));                                   // Creates a label to display text in the GUI.
        formPanel.add(passwordField);                                             // Adds this component to its parent container or panel.

        mainPanel.add(formPanel, BorderLayout.CENTER);                            // Adds the form to the center of the main panel.

        // Button panel: full-width Login and Register buttons.
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));             // Stacks buttons vertically, full width.

        JButton loginButton = new JButton("Login");                               // Creates a button labeled 'Login'.
        JButton registerButton = new JButton("Register");                         // Creates a button labeled 'Register'.

        loginButton.setPreferredSize(new Dimension(0, 40));                       // Sets a comfortable button height.
        registerButton.setPreferredSize(new Dimension(0, 40));                    // Sets a comfortable button height.

        buttonPanel.add(loginButton);                                             // Adds login button to the panel.
        buttonPanel.add(registerButton);                                          // Adds register button to the panel.

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);                           // Adds buttons to the bottom of the main panel.

        add(mainPanel);                                                           // Adds the main panel to the window.

        loginButton.addActionListener(e -> loginAdmin());                         // Registers an event handler for user interaction.
        registerButton.addActionListener(e -> showAdminRegister());               // Registers an event handler for user interaction.
        getRootPane().setDefaultButton(loginButton);                              // Makes this button activate when Enter is pressed.

        revalidate();                                                             // Recalculates the layout after GUI components change.
        repaint();                                                                // Redraws the GUI so the latest changes appear.
        setVisible(true);                                                         // Makes the window or component visible.
    }                                                                             // Ends this code block.

    // =========================================================
    // 8. CHECK ADMIN LOGIN
    // Purpose: Find the Admin by username and verify the password.
    // Supports PBKDF2 hashes and temporarily supports old plain text
    // passwords for backward compatibility.
    // =========================================================
    public void loginAdmin() {                                                    // Declares the loginAdmin method.
        String username = usernameField.getText().trim();                         // Reads the current text entered in the input field.
        char[] passwordChars = passwordField.getPassword();                       // Stores password characters in a character array.
        String password = new String(passwordChars);                              // Stores a text value in a variable.

        if (username.isEmpty() || passwordChars.length == 0) {                    // Checks this condition before continuing: username.isEmpty() || passwordChars.length == 0.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Please enter your username and password."              // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            Arrays.fill(passwordChars, '\0');                                     // Overwrites the character array to clear sensitive password data from memory.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String sql = "SELECT full_name, password FROM admins WHERE username = ?"; // Builds the SQL query that will be executed against MySQL.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                ps.setString(1, username);                                        // Binds a text value to a parameter in the SQL statement.

                try (ResultSet rs = ps.executeQuery()) {                          // Starts a try block; resources are closed automatically when declared here.
                    if (!rs.next() || !passwordMatches(password, rs.getString("password"))) { // Checks this condition before continuing: !rs.next() || !passwordMatches(password, rs.getString("password")).
                        JOptionPane.showMessageDialog(                            // Displays a message to the user.
                                this, "Invalid username or password!"             // Continues the current operation or expression.
                        );                                                        // Performs the operation described by this statement.
                        return;                                                   // Exits this method without returning a value.
                    }                                                             // Ends this code block.

                    adminName = rs.getString("full_name");                        // Reads a text value from the current database row.
                    showDashboard(adminName);                                     // Performs the operation described by this statement.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage()); // Displays a message to the user.
        } finally {                                                               // Continues the current operation or expression.
            Arrays.fill(passwordChars, '\0');                                     // Overwrites the character array to clear sensitive password data from memory.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // =========================================================
    // 9. ADMIN REGISTRATION SCREEN
    // Purpose: Open a form for creating an Admin account.
    // =========================================================
    public void showAdminRegister() {                                             // Declares the showAdminRegister method.
        getContentPane().removeAll();                                             // Removes existing components before building the next screen.

        setTitle("Library Management System - Admin Register");                   // Sets the title displayed in the window title bar.
        setSize(400, 300);                                                        // Sets the window or component dimensions.
        setLocationRelativeTo(null);                                              // Positions the window relative to the specified component or screen.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                           // Sets what happens when the user closes this window.

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));                  // Creates a panel to group and arrange GUI components.
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));         // Sets the border or padding around the component.

        regFullName = new JTextField();                                           // Creates a text input field for user-entered text.
        regUsername = new JTextField();                                           // Creates a text input field for user-entered text.
        regPassword = new JPasswordField();                                       // Creates a password input field that masks typed characters.
        regConfirm = new JPasswordField();                                        // Creates a password input field that masks typed characters.

        JButton registerButton = new JButton("Register");                         // Creates a button labeled 'Register'.
        JButton backButton = new JButton("Back to Login");                        // Creates a button labeled 'Back to Login'.

        panel.add(new JLabel("Full Name:"));                                      // Creates a label to display text in the GUI.
        panel.add(regFullName);                                                   // Adds this component to its parent container or panel.
        panel.add(new JLabel("Username:"));                                       // Creates a label to display text in the GUI.
        panel.add(regUsername);                                                   // Adds this component to its parent container or panel.
        panel.add(new JLabel("Password:"));                                       // Creates a label to display text in the GUI.
        panel.add(regPassword);                                                   // Adds this component to its parent container or panel.
        panel.add(new JLabel("Confirm Password:"));                               // Creates a label to display text in the GUI.
        panel.add(regConfirm);                                                    // Adds this component to its parent container or panel.
        panel.add(registerButton);                                                // Adds this component to its parent container or panel.
        panel.add(backButton);                                                    // Adds this component to its parent container or panel.

        add(panel);                                                               // Performs the operation described by this statement.

        registerButton.addActionListener(e -> registerAdmin());                   // Registers an event handler for user interaction.
        backButton.addActionListener(e -> showLogin());                           // Registers an event handler for user interaction.
        getRootPane().setDefaultButton(registerButton);                           // Makes this button activate when Enter is pressed.

        revalidate();                                                             // Recalculates the layout after GUI components change.
        repaint();                                                                // Redraws the GUI so the latest changes appear.
        setVisible(true);                                                         // Makes the window or component visible.
    }                                                                             // Ends this code block.

    // =========================================================
    // 10. SAVE NEW ADMIN
    // Purpose: Validate registration fields and store the password hash.
    // =========================================================
    public void registerAdmin() {                                                 // Declares the registerAdmin method.
        String fullName = regFullName.getText().trim();                           // Reads the current text entered in the input field.
        String username = regUsername.getText().trim();                           // Reads the current text entered in the input field.
        char[] pass = regPassword.getPassword();                                  // Stores password characters in a character array.
        char[] confirm = regConfirm.getPassword();                                // Stores password characters in a character array.

        if (fullName.isEmpty() || username.isEmpty()                              // Checks this condition before continuing: fullName.isEmpty() || username.isEmpty(.
                || pass.length == 0 || confirm.length == 0) {                     // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Please fill in all the fields."); // Displays a message to the user.
            Arrays.fill(pass, '\0');                                              // Overwrites the character array to clear sensitive password data from memory.
            Arrays.fill(confirm, '\0');                                           // Overwrites the character array to clear sensitive password data from memory.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        if (!Arrays.equals(pass, confirm)) {                                      // Checks this condition before continuing: !Arrays.equals(pass, confirm).
            JOptionPane.showMessageDialog(this, "Passwords do not match.");       // Displays a message to the user.
            Arrays.fill(pass, '\0');                                              // Overwrites the character array to clear sensitive password data from memory.
            Arrays.fill(confirm, '\0');                                           // Overwrites the character array to clear sensitive password data from memory.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        if (pass.length < 8) {                                                    // Checks this condition before continuing: pass.length < 8.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Password must have at least 8 characters."             // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            Arrays.fill(pass, '\0');                                              // Overwrites the character array to clear sensitive password data from memory.
            Arrays.fill(confirm, '\0');                                           // Overwrites the character array to clear sensitive password data from memory.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String sql = "INSERT INTO admins (full_name, username, password) VALUES (?, ?, ?)"; // Builds the SQL query that will be executed against MySQL.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                ps.setString(1, fullName);                                        // Binds a text value to a parameter in the SQL statement.
                ps.setString(2, username);                                        // Binds a text value to a parameter in the SQL statement.
                ps.setString(3, hashPassword(pass));                              // Binds a text value to a parameter in the SQL statement.
                ps.executeUpdate();                                               // Executes an INSERT, UPDATE, or DELETE query.

                JOptionPane.showMessageDialog(                                    // Displays a message to the user.
                        this, "Admin account created! You can now log in."        // Continues the current operation or expression.
                );                                                                // Performs the operation described by this statement.
                showLogin();                                                      // Performs the operation described by this statement.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this,                                                         // Continues the current operation or expression.
                    "Could not register. The username may already be taken.\n"    // Adds the next piece of the SQL query or text string.
                    + e.getMessage()                                              // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
        } catch (Exception e) {                                                   // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not secure the password."); // Displays a message to the user.
        } finally {                                                               // Continues the current operation or expression.
            Arrays.fill(pass, '\0');                                              // Overwrites the character array to clear sensitive password data from memory.
            Arrays.fill(confirm, '\0');                                           // Overwrites the character array to clear sensitive password data from memory.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // =========================================================
    // 11. PASSWORD SECURITY HELPERS
    // Purpose: Hash new passwords and verify saved hashes during login.
    // =========================================================
    private boolean passwordMatches(String typed, String stored) {                // Declares the passwordMatches method.
        if (stored == null) {                                                     // Checks this condition before continuing: stored == null.
            return false;                                                         // Returns this value to the caller.
        }                                                                         // Ends this code block.

        if (stored.startsWith("pbkdf2$")) {                                       // Checks this condition before continuing: stored.startsWith("pbkdf2$").
            try {                                                                 // Starts a try block; resources are closed automatically when declared here.
                return verifyPassword(typed.toCharArray(), stored);               // Returns this value to the caller.
            } catch (Exception e) {                                               // Continues the current operation or expression.
                return false;                                                     // Returns this value to the caller.
            }                                                                     // Ends this code block.
        }                                                                         // Ends this code block.

        // Legacy compatibility for accounts created before password hashing.
        // Remove this fallback after all old passwords have been migrated.
        return stored.equals(typed);                                              // Returns this value to the caller.
    }                                                                             // Ends this code block.

    private String hashPassword(char[] pass) throws Exception {                   // Declares the hashPassword method.
        int rounds = 120000;                                                      // Stores an integer value in a variable.
        byte[] salt = new byte[16];                                               // Stores binary data in a byte array.
        new SecureRandom().nextBytes(salt);                                       // Generates cryptographically secure random data.

        PBEKeySpec spec = new PBEKeySpec(pass, salt, rounds, 256);                // Prepares password and hashing parameters for PBKDF2.
        try {                                                                     // Starts a try block; resources are closed automatically when declared here.
            SecretKeyFactory factory = SecretKeyFactory.getInstance(              // Uses the configured cryptographic algorithm to derive a password hash.
                    "PBKDF2WithHmacSHA256"                                        // Adds the next piece of the SQL query or text string.
            );                                                                    // Performs the operation described by this statement.
            byte[] hash = factory.generateSecret(spec).getEncoded();              // Stores binary data in a byte array.

            return "pbkdf2$" + rounds + "$"                                       // Returns this value to the caller.
                    + Base64.getEncoder().encodeToString(salt) + "$"              // Encodes binary salt or hash bytes as printable text.
                    + Base64.getEncoder().encodeToString(hash);                   // Encodes binary salt or hash bytes as printable text.
        } finally {                                                               // Continues the current operation or expression.
            spec.clearPassword();                                                 // Performs the operation described by this statement.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    private boolean verifyPassword(char[] password, String stored) throws Exception { // Declares the verifyPassword method.
        String[] parts = stored.split("\\$");                                     // Performs the operation described by this statement.
        if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {                    // Checks this condition before continuing: parts.length != 4 || !"pbkdf2".equals(parts[0]).
            return false;                                                         // Returns this value to the caller.
        }                                                                         // Ends this code block.

        int rounds = Integer.parseInt(parts[1]);                                  // Stores an integer value in a variable.
        byte[] salt = Base64.getDecoder().decode(parts[2]);                       // Decodes saved Base64 text back into binary bytes.
        byte[] expected = Base64.getDecoder().decode(parts[3]);                   // Decodes saved Base64 text back into binary bytes.

        PBEKeySpec spec = new PBEKeySpec(                                         // Prepares password and hashing parameters for PBKDF2.
                password, salt, rounds, expected.length * 8                       // Continues the current operation or expression.
        );                                                                        // Performs the operation described by this statement.

        try {                                                                     // Starts a try block; resources are closed automatically when declared here.
            byte[] actual = SecretKeyFactory                                      // Uses the configured cryptographic algorithm to derive a password hash.
                    .getInstance("PBKDF2WithHmacSHA256")                          // Continues the current operation or expression.
                    .generateSecret(spec)                                         // Continues the current operation or expression.
                    .getEncoded();                                                // Performs the operation described by this statement.
            return MessageDigest.isEqual(expected, actual);                       // Returns this value to the caller.
        } finally {                                                               // Continues the current operation or expression.
            spec.clearPassword();                                                 // Performs the operation described by this statement.
            Arrays.fill(password, '\0');                                          // Overwrites the character array to clear sensitive password data from memory.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // =========================================================
    // 12. ADMIN DASHBOARD
    // Purpose: Show system totals and open the application modules.
    // =========================================================
    public void showDashboard(String name) {                                      // Declares the showDashboard method.
        adminName = name;                                                         // Performs the operation described by this statement.
        getContentPane().removeAll();                                             // Removes existing components before building the next screen.

        setTitle("Library Management System - Dashboard");                        // Sets the title displayed in the window title bar.
        setSize(1000, 600);                                                       // Sets the window or component dimensions.
        setLocationRelativeTo(null);                                              // Positions the window relative to the specified component or screen.

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));                  // Creates a panel to group and arrange GUI components.

        JLabel welcomeLabel = new JLabel("Welcome, " + name, SwingConstants.CENTER); // Creates a label to display text in the GUI.
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));                   // Sets the font style and size for the component.
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));     // Sets the border or padding around the component.
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);                          // Adds this component to its parent container or panel.

        // Dashboard cards: counts are read from the database.
        JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 15, 15));           // Creates a panel to group and arrange GUI components.
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Sets the border or padding around the component.

        summaryPanel.add(createCard("Total Books", getCount("SELECT COUNT(*) FROM books"))); // Adds this component to its parent container or panel.
        summaryPanel.add(createCard("Available Copies", getCount(                 // Adds this component to its parent container or panel.
                "SELECT COALESCE(SUM(available_copies), 0) FROM books")));        // Contains SQL instructions for reading or changing database records.
        summaryPanel.add(createCard("Total Members", getCount("SELECT COUNT(*) FROM members"))); // Adds this component to its parent container or panel.
        summaryPanel.add(createCard("Currently In Use", getCount(                 // Adds this component to its parent container or panel.
                "SELECT COUNT(*) FROM book_usage WHERE status = 'Using'")));      // Contains SQL instructions for reading or changing database records.
        summaryPanel.add(createCard("Returned Today", getCount(                   // Adds this component to its parent container or panel.
                "SELECT COUNT(*) FROM book_usage WHERE status = 'Returned' AND usage_date = CURDATE()"))); // Contains SQL instructions for reading or changing database records.
        summaryPanel.add(createCard("Pending Reservations", getCount(             // Adds this component to its parent container or panel.
                "SELECT COUNT(*) FROM reservations WHERE status = 'Pending'")));  // Contains SQL instructions for reading or changing database records.
        summaryPanel.add(createCard("Approved Reservations", getCount(            // Adds this component to its parent container or panel.
                "SELECT COUNT(*) FROM reservations WHERE status = 'Approved'"))); // Contains SQL instructions for reading or changing database records.
        summaryPanel.add(createCard("Total Usage Records", getCount("SELECT COUNT(*) FROM book_usage"))); // Adds this component to its parent container or panel.

        mainPanel.add(summaryPanel, BorderLayout.CENTER);                         // Adds this component to its parent container or panel.

        // Main navigation buttons.
        JPanel menuPanel = new JPanel(new GridLayout(2, 4, 10, 10));              // Creates a panel to group and arrange GUI components.
        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));      // Sets the border or padding around the component.

        JButton booksButton = new JButton("Manage Books");                        // Creates a button labeled 'Manage Books'.
        JButton membersButton = new JButton("Manage Members");                    // Creates a button labeled 'Manage Members'.
        JButton reservationsButton = new JButton("Reservations");                 // Creates a button labeled 'Reservations'.
        JButton usageButton = new JButton("Book Usage");                          // Creates a button labeled 'Book Usage'.
        JButton reportsButton = new JButton("Reports");                           // Creates a button labeled 'Reports'.
        JButton refreshButton = new JButton("Refresh Dashboard");                 // Creates a button labeled 'Refresh Dashboard'.
        JButton logoutButton = new JButton("Logout");                             // Creates a button labeled 'Logout'.

        menuPanel.add(booksButton);                                               // Adds this component to its parent container or panel.
        menuPanel.add(membersButton);                                             // Adds this component to its parent container or panel.
        menuPanel.add(reservationsButton);                                        // Adds this component to its parent container or panel.
        menuPanel.add(usageButton);                                               // Adds this component to its parent container or panel.
        menuPanel.add(reportsButton);                                             // Adds this component to its parent container or panel.
        menuPanel.add(refreshButton);                                             // Adds this component to its parent container or panel.
        menuPanel.add(logoutButton);                                              // Adds this component to its parent container or panel.
        mainPanel.add(menuPanel, BorderLayout.SOUTH);                             // Adds this component to its parent container or panel.
        add(mainPanel);                                                           // Performs the operation described by this statement.

        // Connect each button to its module.
        booksButton.addActionListener(e -> showManageBooks());                    // Registers an event handler for user interaction.
        membersButton.addActionListener(e -> showManageMembers());                // Registers an event handler for user interaction.
        reservationsButton.addActionListener(e -> showReservations());            // Registers an event handler for user interaction.
        usageButton.addActionListener(e -> showBookUsage());                      // Registers an event handler for user interaction.
        reportsButton.addActionListener(e -> showReports());                      // Registers an event handler for user interaction.
        refreshButton.addActionListener(e -> showDashboard(name));                // Registers an event handler for user interaction.

        logoutButton.addActionListener(e -> {                                     // Registers an event handler for user interaction.
            int choice = JOptionPane.showConfirmDialog(                           // Asks the user to confirm an action.
                    this, "Are you sure you want to logout?",                     // Continues the current operation or expression.
                    "Confirm Logout", JOptionPane.YES_NO_OPTION                   // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            if (choice == JOptionPane.YES_OPTION) {                               // Checks this condition before continuing: choice == JOptionPane.YES_OPTION.
                showLogin();                                                      // Performs the operation described by this statement.
            }                                                                     // Ends this code block.
        });                                                                       // Closes the event handler or method call.

        revalidate();                                                             // Recalculates the layout after GUI components change.
        repaint();                                                                // Redraws the GUI so the latest changes appear.
        setVisible(true);                                                         // Makes the window or component visible.
    }                                                                             // Ends this code block.

    // =========================================================
    // 13. MANAGE BOOKS SCREEN
    // Purpose: Display book form and table, with CRUD/search buttons.
    // =========================================================
    public void showManageBooks() {                                               // Declares the showManageBooks method.
        getContentPane().removeAll();                                             // Removes existing components before building the next screen.
        setTitle("Library Management System - Manage Books");                     // Sets the title displayed in the window title bar.
        setSize(1150, 700);                                                       // Sets the window or component dimensions.
        setLocationRelativeTo(null);                                              // Positions the window relative to the specified component or screen.

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));                  // Creates a panel to group and arrange GUI components.
        JPanel headerPanel = new JPanel(new BorderLayout());                      // Creates a panel to group and arrange GUI components.

        JLabel headerLabel = new JLabel("Manage Books");                          // Creates a label to display text in the GUI.
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));                    // Sets the font style and size for the component.
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));    // Sets the border or padding around the component.
        JButton backButton = new JButton("Back to Dashboard");                    // Creates a button labeled 'Back to Dashboard'.
        headerPanel.add(headerLabel, BorderLayout.WEST);                          // Adds this component to its parent container or panel.
        headerPanel.add(backButton, BorderLayout.EAST);                           // Adds this component to its parent container or panel.
        mainPanel.add(headerPanel, BorderLayout.NORTH);                           // Adds this component to its parent container or panel.

        // Book information fields.
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 8, 8));                // Creates a panel to group and arrange GUI components.
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));       // Sets the border or padding around the component.

        bookIsbn = new JTextField();                                              // Creates a text input field for user-entered text.
        bookTitle = new JTextField();                                             // Creates a text input field for user-entered text.
        bookAuthor = new JTextField();                                            // Creates a text input field for user-entered text.
        bookCategory = new JTextField();                                          // Creates a text input field for user-entered text.
        bookPublisher = new JTextField();                                         // Creates a text input field for user-entered text.
        bookYear = new JTextField();                                              // Creates a text input field for user-entered text.
        bookQuantity = new JTextField("1");                                       // Creates a text input field for user-entered text.
        bookAvailable = new JTextField("1");                                      // Creates a text input field for user-entered text.

        formPanel.add(new JLabel("ISBN:")); formPanel.add(bookIsbn);              // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Title:")); formPanel.add(bookTitle);            // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Author:")); formPanel.add(bookAuthor);          // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Category:")); formPanel.add(bookCategory);      // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Publisher:")); formPanel.add(bookPublisher);    // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Publication Year:")); formPanel.add(bookYear);  // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Quantity:")); formPanel.add(bookQuantity);      // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Available Copies:")); formPanel.add(bookAvailable); // Creates a label to display text in the GUI.

        // Book actions.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));   // Creates a panel to group and arrange GUI components.
        JButton addButton = new JButton("Add Book");                              // Creates a button labeled 'Add Book'.
        JButton updateButton = new JButton("Update Book");                        // Creates a button labeled 'Update Book'.
        JButton deleteButton = new JButton("Delete Book");                        // Creates a button labeled 'Delete Book'.
        JButton searchButton = new JButton("Search Book");                        // Creates a button labeled 'Search Book'.
        JButton showAllButton = new JButton("Show All");                          // Creates a button labeled 'Show All'.
        JButton clearButton = new JButton("Clear Fields");                        // Creates a button labeled 'Clear Fields'.
        JButton refreshButton = new JButton("Refresh");                           // Creates a button labeled 'Refresh'.

        buttonPanel.add(addButton); buttonPanel.add(updateButton);                // Adds this component to its parent container or panel.
        buttonPanel.add(deleteButton); buttonPanel.add(searchButton);             // Adds this component to its parent container or panel.
        buttonPanel.add(showAllButton); buttonPanel.add(clearButton);             // Adds this component to its parent container or panel.
        buttonPanel.add(refreshButton);                                           // Adds this component to its parent container or panel.

        booksModel = new DefaultTableModel() {                                    // Creates a table model to store the rows and columns shown in the table.
            @Override                                                             // Marks this method as an implementation that overrides a parent method.
            public boolean isCellEditable(int row, int column) { return false; }  // Declares the isCellEditable method.
        };                                                                        // Ends this statement.
        booksModel.setColumnIdentifiers(new Object[]{                             // Defines the column headings for the table.
            "Book ID", "ISBN", "Title", "Author", "Category",                     // Continues the current operation or expression.
            "Publisher", "Year", "Quantity", "Available"                          // Adds the next piece of the SQL query or text string.
        });                                                                       // Closes the event handler or method call.

        booksTable = new JTable(booksModel);                                      // Creates a table for displaying records.
        booksTable.setRowHeight(24);                                              // Sets the height of each table row.
        booksTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);                     // Sets how the table adjusts its column widths.
        JScrollPane scrollPane = new JScrollPane(booksTable);                     // Adds scrolling support to the enclosed component.

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));                 // Creates a panel to group and arrange GUI components.
        contentPanel.add(formPanel, BorderLayout.NORTH);                          // Adds this component to its parent container or panel.
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);                        // Adds this component to its parent container or panel.
        contentPanel.add(scrollPane, BorderLayout.CENTER);                        // Adds this component to its parent container or panel.
        mainPanel.add(contentPanel, BorderLayout.CENTER);                         // Adds this component to its parent container or panel.
        add(mainPanel);                                                           // Performs the operation described by this statement.

        // Button actions.
        addButton.addActionListener(e -> addBook());                              // Registers an event handler for user interaction.
        updateButton.addActionListener(e -> updateBook());                        // Registers an event handler for user interaction.
        deleteButton.addActionListener(e -> deleteBook());                        // Registers an event handler for user interaction.
        searchButton.addActionListener(e -> searchBook());                        // Registers an event handler for user interaction.
        showAllButton.addActionListener(e -> loadBooks());                        // Registers an event handler for user interaction.
        clearButton.addActionListener(e -> clearBookFields());                    // Registers an event handler for user interaction.
        refreshButton.addActionListener(e -> loadBooks());                        // Registers an event handler for user interaction.
        backButton.addActionListener(e -> showDashboard(adminName));              // Registers an event handler for user interaction.

        // Selecting a row fills the form.
        booksTable.getSelectionModel().addListSelectionListener(e -> {            // Continues the current operation or expression.
            if (e.getValueIsAdjusting()) return;                                  // Checks this condition before continuing: if (e.getValueIsAdjusting()) return;.
            int row = booksTable.getSelectedRow();                                // Stores an integer value in a variable.
            if (row < 0) return;                                                  // Checks this condition before continuing: if (row < 0) return;.

            selectedBookId = Integer.parseInt(String.valueOf(booksModel.getValueAt(row, 0))); // Reads a value from the selected table row and column.
            bookIsbn.setText(cellText(row, 1));                                   // Updates the text displayed in this input field or label.
            bookTitle.setText(cellText(row, 2));                                  // Updates the text displayed in this input field or label.
            bookAuthor.setText(cellText(row, 3));                                 // Updates the text displayed in this input field or label.
            bookCategory.setText(cellText(row, 4));                               // Updates the text displayed in this input field or label.
            bookPublisher.setText(cellText(row, 5));                              // Updates the text displayed in this input field or label.
            bookYear.setText(cellText(row, 6));                                   // Updates the text displayed in this input field or label.
            bookQuantity.setText(cellText(row, 7));                               // Updates the text displayed in this input field or label.
            bookAvailable.setText(cellText(row, 8));                              // Updates the text displayed in this input field or label.
        });                                                                       // Closes the event handler or method call.

        loadBooks();                                                              // Performs the operation described by this statement.
        revalidate();                                                             // Recalculates the layout after GUI components change.
        repaint();                                                                // Redraws the GUI so the latest changes appear.
        setVisible(true);                                                         // Makes the window or component visible.
    }                                                                             // Ends this code block.

    // Helper: safely read a cell as text.
    private String cellText(int row, int column) {                                // Declares the cellText method.
        Object value = booksModel.getValueAt(row, column);                        // Reads a value from the selected table row and column.
        return value == null ? "" : value.toString();                             // Returns this value to the caller.
    }                                                                             // Ends this code block.

    // MySQL YEAR may be returned as a year/date string. Show only YYYY.
    private String yearText(ResultSet result) throws SQLException {               // Declares the yearText method.
        String year = result.getString("publication_year");                       // Reads a text value from the current database row.
        if (year != null && year.length() > 4) year = year.substring(0, 4);       // Checks this condition before continuing: if (year != null && year.length() > 4) year = year.substring(0, 4);.
        return year;                                                              // Returns this value to the caller.
    }                                                                             // Ends this code block.

    // Load all books from MySQL into the table.
    public void loadBooks() {                                                     // Declares the loadBooks method.
        String sql = "SELECT book_id, isbn, title, author, category, publisher, " // Builds the SQL query that will be executed against MySQL.
                + "publication_year, quantity, available_copies "                 // Adds the next piece of the SQL query or text string.
                + "FROM books ORDER BY title";                                    // Adds the next piece of the SQL query or text string.

        booksModel.setRowCount(0);                                                // Clears existing table rows before loading fresh data.
        selectedBookId = -1;                                                      // Performs the operation described by this statement.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (Statement statement = con.createStatement();                     // Starts a try block; resources are closed automatically when declared here.
                 ResultSet result = statement.executeQuery(sql)) {                // Continues the current operation or expression.
                while (result.next()) {                                           // Moves to the next row in the result set, if one exists.
                    booksModel.addRow(new Object[]{                               // Adds a record to the table displayed in the GUI.
                        result.getInt("book_id"), result.getString("isbn"),       // Reads a text value from the current database row.
                        result.getString("title"), result.getString("author"),    // Reads a text value from the current database row.
                        result.getString("category"), result.getString("publisher"), // Reads a text value from the current database row.
                        yearText(result), result.getInt("quantity"),              // Reads an integer value from the current database row.
                        result.getInt("available_copies")                         // Reads an integer value from the current database row.
                    });                                                           // Closes the event handler or method call.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not load books.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Add a book to the database.
    public void addBook() {                                                       // Declares the addBook method.
        String isbn = bookIsbn.getText().trim();                                  // Reads the current text entered in the input field.
        String title = bookTitle.getText().trim();                                // Reads the current text entered in the input field.
        String author = bookAuthor.getText().trim();                              // Reads the current text entered in the input field.

        if (title.isEmpty() || author.isEmpty()) {                                // Checks this condition before continuing: title.isEmpty() || author.isEmpty().
            JOptionPane.showMessageDialog(this, "Title and Author are required."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        int quantity, available, year;                                            // Declares an integer variable.
        try {                                                                     // Starts a try block; resources are closed automatically when declared here.
            quantity = Integer.parseInt(bookQuantity.getText().trim());           // Reads the current text entered in the input field.
            available = Integer.parseInt(bookAvailable.getText().trim());         // Reads the current text entered in the input field.
            year = bookYear.getText().trim().isEmpty()                            // Reads the current text entered in the input field.
                    ? 0 : Integer.parseInt(bookYear.getText().trim());            // Reads the current text entered in the input field.
        } catch (NumberFormatException e) {                                       // Continues the current operation or expression.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Publication Year, Quantity and Available Copies must be numbers." // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        if (quantity < 1) {                                                       // Checks this condition before continuing: quantity < 1.
            JOptionPane.showMessageDialog(this, "Quantity must be at least 1.");  // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.
        if (available < 0 || available > quantity) {                              // Checks this condition before continuing: available < 0 || available > quantity.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Available Copies must be between 0 and Quantity."      // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String sql = "INSERT INTO books (isbn, title, author, category, publisher, " // Builds the SQL query that will be executed against MySQL.
                + "publication_year, quantity, available_copies) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; // Adds the next piece of the SQL query or text string.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                if (isbn.isEmpty()) ps.setNull(1, Types.VARCHAR);                 // Checks this condition before continuing: if (isbn.isEmpty()) ps.setNull(1, Types.VARCHAR);.
                else ps.setString(1, isbn);                                       // Binds a text value to a parameter in the SQL statement.

                ps.setString(2, title);                                           // Binds a text value to a parameter in the SQL statement.
                ps.setString(3, author);                                          // Binds a text value to a parameter in the SQL statement.
                ps.setString(4, bookCategory.getText().trim());                   // Reads the current text entered in the input field.
                ps.setString(5, bookPublisher.getText().trim());                  // Reads the current text entered in the input field.
                if (year == 0) ps.setNull(6, Types.INTEGER);                      // Checks this condition before continuing: if (year == 0) ps.setNull(6, Types.INTEGER);.
                else ps.setInt(6, year);                                          // Binds an integer value to a parameter in the SQL statement.
                ps.setInt(7, quantity);                                           // Binds an integer value to a parameter in the SQL statement.
                ps.setInt(8, available);                                          // Binds an integer value to a parameter in the SQL statement.
                ps.executeUpdate();                                               // Executes an INSERT, UPDATE, or DELETE query.

                JOptionPane.showMessageDialog(this, "Book added successfully!");  // Displays a message to the user.
                clearBookFields();                                                // Performs the operation described by this statement.
                loadBooks();                                                      // Performs the operation described by this statement.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Could not add book. Check if the ISBN already exists.\n" // Continues the current operation or expression.
                    + e.getMessage()                                              // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Update the selected book.
    public void updateBook() {                                                    // Declares the updateBook method.
        if (selectedBookId == -1) {                                               // Checks this condition before continuing: selectedBookId == -1.
            JOptionPane.showMessageDialog(this, "Please select a book in the table first."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String isbn = bookIsbn.getText().trim();                                  // Reads the current text entered in the input field.
        String title = bookTitle.getText().trim();                                // Reads the current text entered in the input field.
        String author = bookAuthor.getText().trim();                              // Reads the current text entered in the input field.
        if (title.isEmpty() || author.isEmpty()) {                                // Checks this condition before continuing: title.isEmpty() || author.isEmpty().
            JOptionPane.showMessageDialog(this, "Title and Author are required."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        int quantity, available, year;                                            // Declares an integer variable.
        try {                                                                     // Starts a try block; resources are closed automatically when declared here.
            quantity = Integer.parseInt(bookQuantity.getText().trim());           // Reads the current text entered in the input field.
            available = Integer.parseInt(bookAvailable.getText().trim());         // Reads the current text entered in the input field.
            year = bookYear.getText().trim().isEmpty()                            // Reads the current text entered in the input field.
                    ? 0 : Integer.parseInt(bookYear.getText().trim());            // Reads the current text entered in the input field.
        } catch (NumberFormatException e) {                                       // Continues the current operation or expression.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Publication Year, Quantity and Available Copies must be numbers." // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        if (quantity < 1 || available < 0 || available > quantity) {              // Checks this condition before continuing: quantity < 1 || available < 0 || available > quantity.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Quantity must be at least 1, and Available Copies must be 0 to Quantity." // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String sql = "UPDATE books SET isbn=?, title=?, author=?, category=?, publisher=?, " // Builds the SQL query that will be executed against MySQL.
                + "publication_year=?, quantity=?, available_copies=? WHERE book_id=?"; // Adds the next piece of the SQL query or text string.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                if (isbn.isEmpty()) ps.setNull(1, Types.VARCHAR);                 // Checks this condition before continuing: if (isbn.isEmpty()) ps.setNull(1, Types.VARCHAR);.
                else ps.setString(1, isbn);                                       // Binds a text value to a parameter in the SQL statement.
                ps.setString(2, title);                                           // Binds a text value to a parameter in the SQL statement.
                ps.setString(3, author);                                          // Binds a text value to a parameter in the SQL statement.
                ps.setString(4, bookCategory.getText().trim());                   // Reads the current text entered in the input field.
                ps.setString(5, bookPublisher.getText().trim());                  // Reads the current text entered in the input field.
                if (year == 0) ps.setNull(6, Types.INTEGER);                      // Checks this condition before continuing: if (year == 0) ps.setNull(6, Types.INTEGER);.
                else ps.setInt(6, year);                                          // Binds an integer value to a parameter in the SQL statement.
                ps.setInt(7, quantity);                                           // Binds an integer value to a parameter in the SQL statement.
                ps.setInt(8, available);                                          // Binds an integer value to a parameter in the SQL statement.
                ps.setInt(9, selectedBookId);                                     // Binds an integer value to a parameter in the SQL statement.

                int rows = ps.executeUpdate();                                    // Executes an INSERT, UPDATE, or DELETE query.
                if (rows > 0) {                                                   // Checks this condition before continuing: rows > 0.
                    JOptionPane.showMessageDialog(this, "Book updated successfully!"); // Displays a message to the user.
                    clearBookFields();                                            // Performs the operation described by this statement.
                    loadBooks();                                                  // Performs the operation described by this statement.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not update book.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Delete selected book only if it has no usage/reservation history.
    public void deleteBook() {                                                    // Declares the deleteBook method.
        if (selectedBookId == -1) {                                               // Checks this condition before continuing: selectedBookId == -1.
            JOptionPane.showMessageDialog(this, "Please select a book in the table first."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            String usageSql = "SELECT COUNT(*) FROM book_usage WHERE book_id = ?"; // Builds the SQL query that will be executed against MySQL.
            try (PreparedStatement ps = con.prepareStatement(usageSql)) {         // Starts a try block; resources are closed automatically when declared here.
                ps.setInt(1, selectedBookId);                                     // Binds an integer value to a parameter in the SQL statement.
                try (ResultSet rs = ps.executeQuery()) {                          // Starts a try block; resources are closed automatically when declared here.
                    if (rs.next() && rs.getInt(1) > 0) {                          // Checks this condition before continuing: rs.next() && rs.getInt(1) > 0.
                        JOptionPane.showMessageDialog(                            // Displays a message to the user.
                                this, "This book has usage records and cannot be deleted." // Continues the current operation or expression.
                        );                                                        // Performs the operation described by this statement.
                        return;                                                   // Exits this method without returning a value.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.

            String reservationSql = "SELECT COUNT(*) FROM reservations WHERE book_id = ?"; // Builds the SQL query that will be executed against MySQL.
            try (PreparedStatement ps = con.prepareStatement(reservationSql)) {   // Starts a try block; resources are closed automatically when declared here.
                ps.setInt(1, selectedBookId);                                     // Binds an integer value to a parameter in the SQL statement.
                try (ResultSet rs = ps.executeQuery()) {                          // Starts a try block; resources are closed automatically when declared here.
                    if (rs.next() && rs.getInt(1) > 0) {                          // Checks this condition before continuing: rs.next() && rs.getInt(1) > 0.
                        JOptionPane.showMessageDialog(                            // Displays a message to the user.
                                this, "This book has reservation records and cannot be deleted." // Continues the current operation or expression.
                        );                                                        // Performs the operation described by this statement.
                        return;                                                   // Exits this method without returning a value.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.

            int choice = JOptionPane.showConfirmDialog(                           // Asks the user to confirm an action.
                    this, "Delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            if (choice != JOptionPane.YES_OPTION) return;                         // Checks this condition before continuing: if (choice != JOptionPane.YES_OPTION) return;.

            try (PreparedStatement ps = con.prepareStatement(                     // Starts a try block; resources are closed automatically when declared here.
                    "DELETE FROM books WHERE book_id = ?")) {                     // Contains SQL instructions for reading or changing database records.
                ps.setInt(1, selectedBookId);                                     // Binds an integer value to a parameter in the SQL statement.
                if (ps.executeUpdate() > 0) {                                     // Checks this condition before continuing: ps.executeUpdate() > 0.
                    JOptionPane.showMessageDialog(this, "Book deleted.");         // Displays a message to the user.
                    clearBookFields();                                            // Performs the operation described by this statement.
                    loadBooks();                                                  // Performs the operation described by this statement.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not delete book.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Search books by title, author or ISBN.
    public void searchBook() {                                                    // Declares the searchBook method.
        String keyword = JOptionPane.showInputDialog(this, "Enter Title, Author or ISBN:"); // Prompts the user to enter a value.
        if (keyword == null || keyword.trim().isEmpty()) return;                  // Checks this condition before continuing: if (keyword == null || keyword.trim().isEmpty()) return;.

        String sql = "SELECT book_id, isbn, title, author, category, publisher, " // Builds the SQL query that will be executed against MySQL.
                + "publication_year, quantity, available_copies FROM books "      // Adds the next piece of the SQL query or text string.
                + "WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? ORDER BY title"; // Adds the next piece of the SQL query or text string.

        booksModel.setRowCount(0);                                                // Clears existing table rows before loading fresh data.
        selectedBookId = -1;                                                      // Performs the operation described by this statement.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                String pattern = "%" + keyword.trim() + "%";                      // Stores a text value in a variable.
                ps.setString(1, pattern);                                         // Binds a text value to a parameter in the SQL statement.
                ps.setString(2, pattern);                                         // Binds a text value to a parameter in the SQL statement.
                ps.setString(3, pattern);                                         // Binds a text value to a parameter in the SQL statement.

                try (ResultSet result = ps.executeQuery()) {                      // Starts a try block; resources are closed automatically when declared here.
                    while (result.next()) {                                       // Moves to the next row in the result set, if one exists.
                        booksModel.addRow(new Object[]{                           // Adds a record to the table displayed in the GUI.
                            result.getInt("book_id"), result.getString("isbn"),   // Reads a text value from the current database row.
                            result.getString("title"), result.getString("author"), // Reads a text value from the current database row.
                            result.getString("category"), result.getString("publisher"), // Reads a text value from the current database row.
                            yearText(result), result.getInt("quantity"),          // Reads an integer value from the current database row.
                            result.getInt("available_copies")                     // Reads an integer value from the current database row.
                        });                                                       // Closes the event handler or method call.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.

            if (booksModel.getRowCount() == 0) {                                  // Checks this condition before continuing: booksModel.getRowCount() == 0.
                JOptionPane.showMessageDialog(this, "No book found.");            // Displays a message to the user.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Search failed.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Clear the Manage Books fields.
    public void clearBookFields() {                                               // Declares the clearBookFields method.
        selectedBookId = -1;                                                      // Performs the operation described by this statement.
        bookIsbn.setText("");                                                     // Updates the text displayed in this input field or label.
        bookTitle.setText("");                                                    // Updates the text displayed in this input field or label.
        bookAuthor.setText("");                                                   // Updates the text displayed in this input field or label.
        bookCategory.setText("");                                                 // Updates the text displayed in this input field or label.
        bookPublisher.setText("");                                                // Updates the text displayed in this input field or label.
        bookYear.setText("");                                                     // Updates the text displayed in this input field or label.
        bookQuantity.setText("1");                                                // Updates the text displayed in this input field or label.
        bookAvailable.setText("1");                                               // Updates the text displayed in this input field or label.
        booksTable.clearSelection();                                              // Clears the current table-row selection.
    }                                                                             // Ends this code block.

    // =========================================================
    // 14. MANAGE MEMBERS SCREEN
    // Purpose: Add, update, delete, search and display student members.
    // =========================================================
    public void showManageMembers() {                                             // Declares the showManageMembers method.
        getContentPane().removeAll();                                             // Removes existing components before building the next screen.
        setTitle("Library Management System - Manage Members");                   // Sets the title displayed in the window title bar.
        setSize(1100, 700);                                                       // Sets the window or component dimensions.
        setLocationRelativeTo(null);                                              // Positions the window relative to the specified component or screen.

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));                  // Creates a panel to group and arrange GUI components.
        JPanel headerPanel = new JPanel(new BorderLayout());                      // Creates a panel to group and arrange GUI components.
        JLabel headerLabel = new JLabel("Manage Members");                        // Creates a label to display text in the GUI.
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));                    // Sets the font style and size for the component.
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));    // Sets the border or padding around the component.
        JButton backButton = new JButton("Back to Dashboard");                    // Creates a button labeled 'Back to Dashboard'.
        headerPanel.add(headerLabel, BorderLayout.WEST);                          // Adds this component to its parent container or panel.
        headerPanel.add(backButton, BorderLayout.EAST);                           // Adds this component to its parent container or panel.
        mainPanel.add(headerPanel, BorderLayout.NORTH);                           // Adds this component to its parent container or panel.

        // Member information form.
        JPanel formPanel = new JPanel(new GridLayout(5, 4, 8, 8));                // Creates a panel to group and arrange GUI components.
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));       // Sets the border or padding around the component.

        memberStudentId = new JTextField();                                       // Creates a text input field for user-entered text.
        memberFullName = new JTextField();                                        // Creates a text input field for user-entered text.
        memberCourse = new JTextField();                                          // Creates a text input field for user-entered text.
        memberYearLevel = new JTextField();                                       // Creates a text input field for user-entered text.
        memberSection = new JTextField();                                         // Creates a text input field for user-entered text.
        memberContact = new JTextField();                                         // Creates a text input field for user-entered text.
        memberAddress = new JTextField();                                         // Creates a text input field for user-entered text.
        memberRegistrationDate = new JTextField(LocalDate.now().toString());      // Creates a text input field for user-entered text.
        memberPassword = new JPasswordField();                                    // Creates a password input field that masks typed characters.
        memberPassword.setToolTipText(                                            // Adds a hint shown when the user hovers over the component.
                "Required when adding. Leave blank on update to keep existing password." // Adds the next piece of the SQL query or text string.
        );                                                                        // Performs the operation described by this statement.
        memberStatusBox = new JComboBox<>(new String[]{"Active", "Inactive"});    // Creates a drop-down list of selectable options.

        formPanel.add(new JLabel("Student ID:")); formPanel.add(memberStudentId); // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Full Name:")); formPanel.add(memberFullName);   // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Course:")); formPanel.add(memberCourse);        // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Year Level:")); formPanel.add(memberYearLevel); // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Section:")); formPanel.add(memberSection);      // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Contact Number:")); formPanel.add(memberContact); // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Address:")); formPanel.add(memberAddress);      // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Registration Date:")); formPanel.add(memberRegistrationDate); // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Status:")); formPanel.add(memberStatusBox);     // Creates a label to display text in the GUI.
        formPanel.add(new JLabel("Password:")); formPanel.add(memberPassword);    // Creates a label to display text in the GUI.

        // Member actions.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));   // Creates a panel to group and arrange GUI components.
        JButton addButton = new JButton("Add Member");                            // Creates a button labeled 'Add Member'.
        JButton updateButton = new JButton("Update Member");                      // Creates a button labeled 'Update Member'.
        JButton deleteButton = new JButton("Delete Member");                      // Creates a button labeled 'Delete Member'.
        JButton searchButton = new JButton("Search Member");                      // Creates a button labeled 'Search Member'.
        JButton showAllButton = new JButton("Show All");                          // Creates a button labeled 'Show All'.
        JButton clearButton = new JButton("Clear Fields");                        // Creates a button labeled 'Clear Fields'.
        JButton refreshButton = new JButton("Refresh");                           // Creates a button labeled 'Refresh'.

        buttonPanel.add(addButton); buttonPanel.add(updateButton);                // Adds this component to its parent container or panel.
        buttonPanel.add(deleteButton); buttonPanel.add(searchButton);             // Adds this component to its parent container or panel.
        buttonPanel.add(showAllButton); buttonPanel.add(clearButton);             // Adds this component to its parent container or panel.
        buttonPanel.add(refreshButton);                                           // Adds this component to its parent container or panel.

        membersModel = new DefaultTableModel() {                                  // Creates a table model to store the rows and columns shown in the table.
            @Override                                                             // Marks this method as an implementation that overrides a parent method.
            public boolean isCellEditable(int row, int column) { return false; }  // Declares the isCellEditable method.
        };                                                                        // Ends this statement.
        membersModel.setColumnIdentifiers(new Object[]{                           // Defines the column headings for the table.
            "Member ID", "Student ID", "Full Name", "Course", "Year Level",       // Continues the current operation or expression.
            "Section", "Contact Number", "Address", "Registration Date", "Status" // Adds the next piece of the SQL query or text string.
        });                                                                       // Closes the event handler or method call.

        membersTable = new JTable(membersModel);                                  // Creates a table for displaying records.
        membersTable.setRowHeight(24);                                            // Sets the height of each table row.
        membersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);                   // Sets how the table adjusts its column widths.
        JScrollPane scrollPane = new JScrollPane(membersTable);                   // Adds scrolling support to the enclosed component.

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));                 // Creates a panel to group and arrange GUI components.
        contentPanel.add(formPanel, BorderLayout.NORTH);                          // Adds this component to its parent container or panel.
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);                        // Adds this component to its parent container or panel.
        contentPanel.add(scrollPane, BorderLayout.CENTER);                        // Adds this component to its parent container or panel.
        mainPanel.add(contentPanel, BorderLayout.CENTER);                         // Adds this component to its parent container or panel.
        add(mainPanel);                                                           // Performs the operation described by this statement.

        // Member button actions.
        addButton.addActionListener(e -> addMember());                            // Registers an event handler for user interaction.
        updateButton.addActionListener(e -> updateMember());                      // Registers an event handler for user interaction.
        deleteButton.addActionListener(e -> deleteMember());                      // Registers an event handler for user interaction.
        searchButton.addActionListener(e -> searchMember());                      // Registers an event handler for user interaction.
        showAllButton.addActionListener(e -> loadMembers());                      // Registers an event handler for user interaction.
        clearButton.addActionListener(e -> clearMemberFields());                  // Registers an event handler for user interaction.
        refreshButton.addActionListener(e -> loadMembers());                      // Registers an event handler for user interaction.
        backButton.addActionListener(e -> showDashboard(adminName));              // Registers an event handler for user interaction.

        // Selecting a member fills the form.
        membersTable.getSelectionModel().addListSelectionListener(e -> {          // Continues the current operation or expression.
            if (e.getValueIsAdjusting()) return;                                  // Checks this condition before continuing: if (e.getValueIsAdjusting()) return;.
            int row = membersTable.getSelectedRow();                              // Stores an integer value in a variable.
            if (row < 0) return;                                                  // Checks this condition before continuing: if (row < 0) return;.

            selectedMemberId = Integer.parseInt(String.valueOf(membersModel.getValueAt(row, 0))); // Reads a value from the selected table row and column.
            memberStudentId.setText(tableText(membersModel, row, 1));             // Updates the text displayed in this input field or label.
            memberFullName.setText(tableText(membersModel, row, 2));              // Updates the text displayed in this input field or label.
            memberCourse.setText(tableText(membersModel, row, 3));                // Updates the text displayed in this input field or label.
            memberYearLevel.setText(tableText(membersModel, row, 4));             // Updates the text displayed in this input field or label.
            memberSection.setText(tableText(membersModel, row, 5));               // Updates the text displayed in this input field or label.
            memberContact.setText(tableText(membersModel, row, 6));               // Updates the text displayed in this input field or label.
            memberAddress.setText(tableText(membersModel, row, 7));               // Updates the text displayed in this input field or label.
            memberRegistrationDate.setText(tableText(membersModel, row, 8));      // Updates the text displayed in this input field or label.
            memberStatusBox.setSelectedItem(tableText(membersModel, row, 9));     // Selects the specified option in the drop-down list.
            memberPassword.setText("");                                           // Updates the text displayed in this input field or label.
        });                                                                       // Closes the event handler or method call.

        loadMembers();                                                            // Performs the operation described by this statement.
        revalidate();                                                             // Recalculates the layout after GUI components change.
        repaint();                                                                // Redraws the GUI so the latest changes appear.
        setVisible(true);                                                         // Makes the window or component visible.
    }                                                                             // Ends this code block.

    // Helper: read a table cell safely when its database value is NULL.
    private String tableText(DefaultTableModel model, int row, int column) {      // Declares the tableText method.
        Object value = model.getValueAt(row, column);                             // Reads a value from the selected table row and column.
        return value == null ? "" : value.toString();                             // Returns this value to the caller.
    }                                                                             // Ends this code block.

    // Load all student member records.
    public void loadMembers() {                                                   // Declares the loadMembers method.
        if (membersModel == null) return;                                         // Checks this condition before continuing: if (membersModel == null) return;.
        membersModel.setRowCount(0);                                              // Clears existing table rows before loading fresh data.
        selectedMemberId = -1;                                                    // Performs the operation described by this statement.

        String sql = "SELECT member_id, student_id, full_name, course, year_level, " // Builds the SQL query that will be executed against MySQL.
                + "section, contact_number, address, registration_date, status "  // Adds the next piece of the SQL query or text string.
                + "FROM members ORDER BY full_name";                              // Adds the next piece of the SQL query or text string.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (Statement statement = con.createStatement();                     // Starts a try block; resources are closed automatically when declared here.
                 ResultSet result = statement.executeQuery(sql)) {                // Continues the current operation or expression.
                while (result.next()) {                                           // Moves to the next row in the result set, if one exists.
                    membersModel.addRow(new Object[]{                             // Adds a record to the table displayed in the GUI.
                        result.getInt("member_id"), result.getString("student_id"), // Reads a text value from the current database row.
                        result.getString("full_name"), result.getString("course"), // Reads a text value from the current database row.
                        result.getString("year_level"), result.getString("section"), // Reads a text value from the current database row.
                        result.getString("contact_number"), result.getString("address"), // Reads a text value from the current database row.
                        result.getDate("registration_date"), result.getString("status") // Reads a text value from the current database row.
                    });                                                           // Closes the event handler or method call.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not load members.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Add a member record and securely hash the initial password.
    public void addMember() {                                                     // Declares the addMember method.
        if (memberStudentId.getText().trim().isEmpty()                            // Checks this condition before continuing: memberStudentId.getText().trim().isEmpty(.
                || memberFullName.getText().trim().isEmpty()) {                   // Reads the current text entered in the input field.
            JOptionPane.showMessageDialog(this, "Student ID and Full Name are required."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        char[] pass = memberPassword.getPassword();                               // Stores password characters in a character array.
        if (pass.length < 8) {                                                    // Checks this condition before continuing: pass.length < 8.
            Arrays.fill(pass, '\0');                                              // Overwrites the character array to clear sensitive password data from memory.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Password must have at least 8 characters."             // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String passwordHash;                                                      // Declares a text variable.
        try {                                                                     // Starts a try block; resources are closed automatically when declared here.
            passwordHash = hashPassword(pass);                                    // Performs the operation described by this statement.
        } catch (Exception e) {                                                   // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not secure the password."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        } finally {                                                               // Continues the current operation or expression.
            Arrays.fill(pass, '\0');                                              // Overwrites the character array to clear sensitive password data from memory.
        }                                                                         // Ends this code block.

        String sql = "INSERT INTO members (student_id, full_name, course, year_level, " // Builds the SQL query that will be executed against MySQL.
                + "section, contact_number, address, registration_date, status, password_hash) " // Adds the next piece of the SQL query or text string.
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";                        // Adds the next piece of the SQL query or text string.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                ps.setString(1, memberStudentId.getText().trim());                // Reads the current text entered in the input field.
                ps.setString(2, memberFullName.getText().trim());                 // Reads the current text entered in the input field.
                ps.setString(3, memberCourse.getText().trim());                   // Reads the current text entered in the input field.
                ps.setString(4, memberYearLevel.getText().trim());                // Reads the current text entered in the input field.
                ps.setString(5, memberSection.getText().trim());                  // Reads the current text entered in the input field.
                ps.setString(6, memberContact.getText().trim());                  // Reads the current text entered in the input field.
                ps.setString(7, memberAddress.getText().trim());                  // Reads the current text entered in the input field.
                ps.setDate(8, java.sql.Date.valueOf(                              // Binds a date value to a parameter in the SQL statement.
                        LocalDate.parse(memberRegistrationDate.getText().trim()))); // Reads the current text entered in the input field.
                ps.setString(9, memberStatusBox.getSelectedItem().toString());    // Binds a text value to a parameter in the SQL statement.
                ps.setString(10, passwordHash);                                   // Binds a text value to a parameter in the SQL statement.
                ps.executeUpdate();                                               // Executes an INSERT, UPDATE, or DELETE query.

                JOptionPane.showMessageDialog(this, "Member added successfully!"); // Displays a message to the user.
                clearMemberFields();                                              // Performs the operation described by this statement.
                loadMembers();                                                    // Performs the operation described by this statement.
            }                                                                     // Ends this code block.
        } catch (java.time.format.DateTimeParseException e) {                     // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Registration Date must be YYYY-MM-DD."); // Displays a message to the user.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    this, "Could not add member. Check if Student ID already exists.\n" // Continues the current operation or expression.
                    + e.getMessage()                                              // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Update member information. Leave Password blank to keep the old password.
    public void updateMember() {                                                  // Declares the updateMember method.
        if (selectedMemberId == -1) {                                             // Checks this condition before continuing: selectedMemberId == -1.
            JOptionPane.showMessageDialog(this, "Please select a member in the table first."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String sql = "UPDATE members SET student_id=?, full_name=?, course=?, year_level=?, " // Builds the SQL query that will be executed against MySQL.
                + "section=?, contact_number=?, address=?, registration_date=?, status=? " // Adds the next piece of the SQL query or text string.
                + "WHERE member_id=?";                                            // Adds the next piece of the SQL query or text string.

        char[] newPass = memberPassword.getPassword();                            // Stores password characters in a character array.
        String newHash = null;                                                    // Stores a text value in a variable.

        try {                                                                     // Starts a try block; resources are closed automatically when declared here.
            if (newPass.length > 0) {                                             // Checks this condition before continuing: newPass.length > 0.
                if (newPass.length < 8) {                                         // Checks this condition before continuing: newPass.length < 8.
                    JOptionPane.showMessageDialog(this, "New password must have at least 8 characters."); // Displays a message to the user.
                    return;                                                       // Exits this method without returning a value.
                }                                                                 // Ends this code block.
                newHash = hashPassword(newPass);                                  // Performs the operation described by this statement.
            }                                                                     // Ends this code block.
        } catch (Exception e) {                                                   // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not secure the new password."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        } finally {                                                               // Continues the current operation or expression.
            Arrays.fill(newPass, '\0');                                           // Overwrites the character array to clear sensitive password data from memory.
        }                                                                         // Ends this code block.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                ps.setString(1, memberStudentId.getText().trim());                // Reads the current text entered in the input field.
                ps.setString(2, memberFullName.getText().trim());                 // Reads the current text entered in the input field.
                ps.setString(3, memberCourse.getText().trim());                   // Reads the current text entered in the input field.
                ps.setString(4, memberYearLevel.getText().trim());                // Reads the current text entered in the input field.
                ps.setString(5, memberSection.getText().trim());                  // Reads the current text entered in the input field.
                ps.setString(6, memberContact.getText().trim());                  // Reads the current text entered in the input field.
                ps.setString(7, memberAddress.getText().trim());                  // Reads the current text entered in the input field.
                ps.setDate(8, java.sql.Date.valueOf(                              // Binds a date value to a parameter in the SQL statement.
                        LocalDate.parse(memberRegistrationDate.getText().trim()))); // Reads the current text entered in the input field.
                ps.setString(9, memberStatusBox.getSelectedItem().toString());    // Binds a text value to a parameter in the SQL statement.
                ps.setInt(10, selectedMemberId);                                  // Binds an integer value to a parameter in the SQL statement.

                int rows = ps.executeUpdate();                                    // Executes an INSERT, UPDATE, or DELETE query.
                if (rows > 0) {                                                   // Checks this condition before continuing: rows > 0.
                    if (newHash != null) {                                        // Checks this condition before continuing: newHash != null.
                        try (PreparedStatement passPs = con.prepareStatement(     // Starts a try block; resources are closed automatically when declared here.
                                "UPDATE members SET password_hash = ? WHERE member_id = ?")) { // Contains SQL instructions for reading or changing database records.
                            passPs.setString(1, newHash);                         // Binds a text value to a parameter in the SQL statement.
                            passPs.setInt(2, selectedMemberId);                   // Binds an integer value to a parameter in the SQL statement.
                            passPs.executeUpdate();                               // Executes an INSERT, UPDATE, or DELETE query.
                        }                                                         // Ends this code block.
                    }                                                             // Ends this code block.
                    JOptionPane.showMessageDialog(this, "Member updated successfully!"); // Displays a message to the user.
                    clearMemberFields();                                          // Performs the operation described by this statement.
                    loadMembers();                                                // Performs the operation described by this statement.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (java.time.format.DateTimeParseException e) {                     // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Registration Date must be YYYY-MM-DD."); // Displays a message to the user.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not update member.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Delete member and associated history, but only when no active usage/reservation exists.
    public void deleteMember() {                                                  // Declares the deleteMember method.
        if (selectedMemberId == -1) {                                             // Checks this condition before continuing: selectedMemberId == -1.
            JOptionPane.showMessageDialog(this, "Please select a member in the table first."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String activeSql = "SELECT "                                              // Builds the SQL query that will be executed against MySQL.
                + "(SELECT COUNT(*) FROM book_usage WHERE member_id = ? AND status = 'Using') AS active_usage, " // Adds the next piece of the SQL query or text string.
                + "(SELECT COUNT(*) FROM reservations WHERE member_id = ? "       // Adds the next piece of the SQL query or text string.
                + "AND status IN ('Pending','Approved','Borrowed')) AS active_reservations"; // Adds the next piece of the SQL query or text string.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            int activeUsage;                                                      // Declares an integer variable.
            int activeReservations;                                               // Declares an integer variable.
            try (PreparedStatement ps = con.prepareStatement(activeSql)) {        // Starts a try block; resources are closed automatically when declared here.
                ps.setInt(1, selectedMemberId);                                   // Binds an integer value to a parameter in the SQL statement.
                ps.setInt(2, selectedMemberId);                                   // Binds an integer value to a parameter in the SQL statement.
                try (ResultSet result = ps.executeQuery()) {                      // Starts a try block; resources are closed automatically when declared here.
                    result.next();                                                // Moves to the next row in the result set, if one exists.
                    activeUsage = result.getInt("active_usage");                  // Reads an integer value from the current database row.
                    activeReservations = result.getInt("active_reservations");    // Reads an integer value from the current database row.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.

            if (activeUsage > 0 || activeReservations > 0) {                      // Checks this condition before continuing: activeUsage > 0 || activeReservations > 0.
                JOptionPane.showMessageDialog(                                    // Displays a message to the user.
                        this, "This member still has an active reservation or book usage.\n" // Continues the current operation or expression.
                        + "Finish, return, or cancel it first."                   // Adds the next piece of the SQL query or text string.
                );                                                                // Performs the operation described by this statement.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            int choice = JOptionPane.showConfirmDialog(                           // Asks the user to confirm an action.
                    this,                                                         // Continues the current operation or expression.
                    "Delete this member? Their reservation and usage history will also be deleted.", // Continues the current operation or expression.
                    "Confirm Delete", JOptionPane.YES_NO_OPTION                   // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
            if (choice != JOptionPane.YES_OPTION) return;                         // Checks this condition before continuing: if (choice != JOptionPane.YES_OPTION) return;.

            con.setAutoCommit(false);                                             // Begins manual transaction control so related database changes can be committed together.
            try {                                                                 // Starts a try block; resources are closed automatically when declared here.
                try (PreparedStatement ps = con.prepareStatement(                 // Starts a try block; resources are closed automatically when declared here.
                        "DELETE FROM book_usage WHERE member_id = ?")) {          // Contains SQL instructions for reading or changing database records.
                    ps.setInt(1, selectedMemberId);                               // Binds an integer value to a parameter in the SQL statement.
                    ps.executeUpdate();                                           // Executes an INSERT, UPDATE, or DELETE query.
                }                                                                 // Ends this code block.
                try (PreparedStatement ps = con.prepareStatement(                 // Starts a try block; resources are closed automatically when declared here.
                        "DELETE FROM reservations WHERE member_id = ?")) {        // Contains SQL instructions for reading or changing database records.
                    ps.setInt(1, selectedMemberId);                               // Binds an integer value to a parameter in the SQL statement.
                    ps.executeUpdate();                                           // Executes an INSERT, UPDATE, or DELETE query.
                }                                                                 // Ends this code block.

                int rows;                                                         // Declares an integer variable.
                try (PreparedStatement ps = con.prepareStatement(                 // Starts a try block; resources are closed automatically when declared here.
                        "DELETE FROM members WHERE member_id = ?")) {             // Contains SQL instructions for reading or changing database records.
                    ps.setInt(1, selectedMemberId);                               // Binds an integer value to a parameter in the SQL statement.
                    rows = ps.executeUpdate();                                    // Executes an INSERT, UPDATE, or DELETE query.
                }                                                                 // Ends this code block.

                con.commit();                                                     // Saves all successful changes in the current database transaction.
                if (rows > 0) {                                                   // Checks this condition before continuing: rows > 0.
                    JOptionPane.showMessageDialog(this, "Member deleted.");       // Displays a message to the user.
                    clearMemberFields();                                          // Performs the operation described by this statement.
                    loadMembers();                                                // Performs the operation described by this statement.
                }                                                                 // Ends this code block.
            } catch (SQLException e) {                                            // Continues the current operation or expression.
                con.rollback();                                                   // Reverses uncommitted changes if the transaction fails.
                JOptionPane.showMessageDialog(this, "Could not delete member.\n" + e.getMessage()); // Displays a message to the user.
            } finally {                                                           // Continues the current operation or expression.
                con.setAutoCommit(true);                                          // Restores automatic commit mode for future database operations.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not delete member.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Search members by Student ID or full name.
    public void searchMember() {                                                  // Declares the searchMember method.
        String keyword = JOptionPane.showInputDialog(this, "Enter Student ID or Full Name:"); // Prompts the user to enter a value.
        if (keyword == null || keyword.trim().isEmpty()) return;                  // Checks this condition before continuing: if (keyword == null || keyword.trim().isEmpty()) return;.

        String sql = "SELECT member_id, student_id, full_name, course, year_level, section, " // Builds the SQL query that will be executed against MySQL.
                + "contact_number, address, registration_date, status FROM members " // Adds the next piece of the SQL query or text string.
                + "WHERE student_id LIKE ? OR full_name LIKE ? ORDER BY full_name"; // Adds the next piece of the SQL query or text string.

        membersModel.setRowCount(0);                                              // Clears existing table rows before loading fresh data.
        selectedMemberId = -1;                                                    // Performs the operation described by this statement.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                String pattern = "%" + keyword.trim() + "%";                      // Stores a text value in a variable.
                ps.setString(1, pattern);                                         // Binds a text value to a parameter in the SQL statement.
                ps.setString(2, pattern);                                         // Binds a text value to a parameter in the SQL statement.

                try (ResultSet result = ps.executeQuery()) {                      // Starts a try block; resources are closed automatically when declared here.
                    while (result.next()) {                                       // Moves to the next row in the result set, if one exists.
                        membersModel.addRow(new Object[]{                         // Adds a record to the table displayed in the GUI.
                            result.getInt("member_id"), result.getString("student_id"), // Reads a text value from the current database row.
                            result.getString("full_name"), result.getString("course"), // Reads a text value from the current database row.
                            result.getString("year_level"), result.getString("section"), // Reads a text value from the current database row.
                            result.getString("contact_number"), result.getString("address"), // Reads a text value from the current database row.
                            result.getDate("registration_date"), result.getString("status") // Reads a text value from the current database row.
                        });                                                       // Closes the event handler or method call.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.

            if (membersModel.getRowCount() == 0) {                                // Checks this condition before continuing: membersModel.getRowCount() == 0.
                JOptionPane.showMessageDialog(this, "No member found.");          // Displays a message to the user.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Search failed.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Clear member form and reset selection.
    public void clearMemberFields() {                                             // Declares the clearMemberFields method.
        selectedMemberId = -1;                                                    // Performs the operation described by this statement.
        memberStudentId.setText("");                                              // Updates the text displayed in this input field or label.
        memberFullName.setText("");                                               // Updates the text displayed in this input field or label.
        memberCourse.setText("");                                                 // Updates the text displayed in this input field or label.
        memberYearLevel.setText("");                                              // Updates the text displayed in this input field or label.
        memberSection.setText("");                                                // Updates the text displayed in this input field or label.
        memberContact.setText("");                                                // Updates the text displayed in this input field or label.
        memberAddress.setText("");                                                // Updates the text displayed in this input field or label.
        memberRegistrationDate.setText(LocalDate.now().toString());               // Updates the text displayed in this input field or label.
        memberStatusBox.setSelectedIndex(0);                                      // Selects the specified option in the drop-down list.
        memberPassword.setText("");                                               // Updates the text displayed in this input field or label.
        membersTable.clearSelection();                                            // Clears the current table-row selection.
    }                                                                             // Ends this code block.

    // =========================================================
    // 15. RESERVATIONS SCREEN
    // Purpose: View reservations and approve or decline pending requests.
    // =========================================================
    public void showReservations() {                                              // Declares the showReservations method.
        getContentPane().removeAll();                                             // Removes existing components before building the next screen.
        setTitle("Library Management System - Reservations");                     // Sets the title displayed in the window title bar.
        setSize(1100, 650);                                                       // Sets the window or component dimensions.
        setLocationRelativeTo(null);                                              // Positions the window relative to the specified component or screen.

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));                  // Creates a panel to group and arrange GUI components.
        JPanel headerPanel = new JPanel(new BorderLayout());                      // Creates a panel to group and arrange GUI components.
        JLabel headerLabel = new JLabel("Book Reservations");                     // Creates a label to display text in the GUI.
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));                    // Sets the font style and size for the component.
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));    // Sets the border or padding around the component.
        JButton backButton = new JButton("Back to Dashboard");                    // Creates a button labeled 'Back to Dashboard'.
        headerPanel.add(headerLabel, BorderLayout.WEST);                          // Adds this component to its parent container or panel.
        headerPanel.add(backButton, BorderLayout.EAST);                           // Adds this component to its parent container or panel.
        mainPanel.add(headerPanel, BorderLayout.NORTH);                           // Adds this component to its parent container or panel.

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));   // Creates a panel to group and arrange GUI components.
        JButton pendingButton = new JButton("Show Pending");                      // Creates a button labeled 'Show Pending'.
        JButton approvedButton = new JButton("Show Approved");                    // Creates a button labeled 'Show Approved'.
        JButton borrowedButton = new JButton("Show In Use");                      // Creates a button labeled 'Show In Use'.
        JButton allButton = new JButton("Show All");                              // Creates a button labeled 'Show All'.
        JButton approveButton = new JButton("Approve");                           // Creates a button labeled 'Approve'.
        JButton declineButton = new JButton("Decline");                           // Creates a button labeled 'Decline'.
        JButton refreshButton = new JButton("Refresh");                           // Creates a button labeled 'Refresh'.
        buttonPanel.add(pendingButton); buttonPanel.add(approvedButton);          // Adds this component to its parent container or panel.
        buttonPanel.add(borrowedButton); buttonPanel.add(allButton);              // Adds this component to its parent container or panel.
        buttonPanel.add(approveButton); buttonPanel.add(declineButton);           // Adds this component to its parent container or panel.
        buttonPanel.add(refreshButton);                                           // Adds this component to its parent container or panel.

        reservationsModel = new DefaultTableModel() {                             // Creates a table model to store the rows and columns shown in the table.
            @Override                                                             // Marks this method as an implementation that overrides a parent method.
            public boolean isCellEditable(int row, int column) { return false; }  // Declares the isCellEditable method.
        };                                                                        // Ends this statement.
        reservationsModel.setColumnIdentifiers(new Object[]{                      // Defines the column headings for the table.
            "ID", "Student ID", "Student Name", "Book Title", "Date", "Status", "Note" // Adds the next piece of the SQL query or text string.
        });                                                                       // Closes the event handler or method call.

        reservationsTable = new JTable(reservationsModel);                        // Creates a table for displaying records.
        reservationsTable.setRowHeight(24);                                       // Sets the height of each table row.
        reservationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);              // Sets how the table adjusts its column widths.
        JScrollPane scrollPane = new JScrollPane(reservationsTable);              // Adds scrolling support to the enclosed component.

        JLabel hintLabel = new JLabel(                                            // Creates a label to display text in the GUI.
                "Reservation flow: Pending → Approved → Borrowed → Completed"     // Adds the next piece of the SQL query or text string.
        );                                                                        // Performs the operation described by this statement.
        hintLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));         // Sets the border or padding around the component.

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));                 // Creates a panel to group and arrange GUI components.
        contentPanel.add(buttonPanel, BorderLayout.NORTH);                        // Adds this component to its parent container or panel.
        contentPanel.add(scrollPane, BorderLayout.CENTER);                        // Adds this component to its parent container or panel.
        contentPanel.add(hintLabel, BorderLayout.SOUTH);                          // Adds this component to its parent container or panel.
        mainPanel.add(contentPanel, BorderLayout.CENTER);                         // Adds this component to its parent container or panel.
        add(mainPanel);                                                           // Performs the operation described by this statement.

        pendingButton.addActionListener(e -> {                                    // Registers an event handler for user interaction.
            reservationFilter = "Pending";                                        // Performs the operation described by this statement.
            loadReservations("Pending");                                          // Performs the operation described by this statement.
        });                                                                       // Closes the event handler or method call.
        approvedButton.addActionListener(e -> {                                   // Registers an event handler for user interaction.
            reservationFilter = "Approved";                                       // Performs the operation described by this statement.
            loadReservations("Approved");                                         // Performs the operation described by this statement.
        });                                                                       // Closes the event handler or method call.
        borrowedButton.addActionListener(e -> {                                   // Registers an event handler for user interaction.
            reservationFilter = "Borrowed";                                       // Performs the operation described by this statement.
            loadReservations("Borrowed");                                         // Performs the operation described by this statement.
        });                                                                       // Closes the event handler or method call.
        allButton.addActionListener(e -> {                                        // Registers an event handler for user interaction.
            reservationFilter = null;                                             // Performs the operation described by this statement.
            loadReservations(null);                                               // Performs the operation described by this statement.
        });                                                                       // Closes the event handler or method call.
        refreshButton.addActionListener(e -> loadReservations(reservationFilter)); // Registers an event handler for user interaction.
        approveButton.addActionListener(e -> approveReservation());               // Registers an event handler for user interaction.
        declineButton.addActionListener(e -> declineReservation());               // Registers an event handler for user interaction.
        backButton.addActionListener(e -> showDashboard(adminName));              // Registers an event handler for user interaction.

        reservationsTable.getSelectionModel().addListSelectionListener(e -> {     // Continues the current operation or expression.
            if (e.getValueIsAdjusting()) return;                                  // Checks this condition before continuing: if (e.getValueIsAdjusting()) return;.
            int row = reservationsTable.getSelectedRow();                         // Stores an integer value in a variable.
            if (row < 0) return;                                                  // Checks this condition before continuing: if (row < 0) return;.
            selectedReservationId = Integer.parseInt(                             // Continues the current operation or expression.
                    String.valueOf(reservationsModel.getValueAt(row, 0))          // Reads a value from the selected table row and column.
            );                                                                    // Performs the operation described by this statement.
        });                                                                       // Closes the event handler or method call.

        reservationFilter = "Pending";                                            // Performs the operation described by this statement.
        loadReservations("Pending");                                              // Performs the operation described by this statement.
        revalidate();                                                             // Recalculates the layout after GUI components change.
        repaint();                                                                // Redraws the GUI so the latest changes appear.
        setVisible(true);                                                         // Makes the window or component visible.
    }                                                                             // Ends this code block.

    // Load reservation rows. A null filter means show all reservations.
    public void loadReservations(String statusFilter) {                           // Declares the loadReservations method.
        String sql = "SELECT r.reservation_id, m.student_id, m.full_name, b.title, " // Builds the SQL query that will be executed against MySQL.
                + "r.reservation_date, r.status, r.admin_note FROM reservations r " // Adds the next piece of the SQL query or text string.
                + "JOIN members m ON r.member_id = m.member_id "                  // Adds the next piece of the SQL query or text string.
                + "JOIN books b ON r.book_id = b.book_id ";                       // Adds the next piece of the SQL query or text string.

        if (statusFilter != null) sql += "WHERE r.status = ? ";                   // Checks this condition before continuing: if (statusFilter != null) sql += "WHERE r.status = ? ";.
        sql += "ORDER BY r.created_at DESC";                                      // Performs the operation described by this statement.

        reservationsModel.setRowCount(0);                                         // Clears existing table rows before loading fresh data.
        selectedReservationId = -1;                                               // Performs the operation described by this statement.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                if (statusFilter != null) ps.setString(1, statusFilter);          // Checks this condition before continuing: if (statusFilter != null) ps.setString(1, statusFilter);.

                try (ResultSet result = ps.executeQuery()) {                      // Starts a try block; resources are closed automatically when declared here.
                    while (result.next()) {                                       // Moves to the next row in the result set, if one exists.
                        reservationsModel.addRow(new Object[]{                    // Adds a record to the table displayed in the GUI.
                            result.getInt("reservation_id"), result.getString("student_id"), // Reads a text value from the current database row.
                            result.getString("full_name"), result.getString("title"), // Reads a text value from the current database row.
                            result.getDate("reservation_date"), result.getString("status"), // Reads a text value from the current database row.
                            result.getString("admin_note")                        // Reads a text value from the current database row.
                        });                                                       // Closes the event handler or method call.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not load reservations.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Approve a pending reservation and hold one available copy.
    public void approveReservation() {                                            // Declares the approveReservation method.
        if (selectedReservationId == -1) {                                        // Checks this condition before continuing: selectedReservationId == -1.
            JOptionPane.showMessageDialog(this, "Please select a reservation first."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String checkSql = "SELECT r.book_id, r.status, b.available_copies "       // Builds the SQL query that will be executed against MySQL.
                + "FROM reservations r JOIN books b ON r.book_id = b.book_id "    // Adds the next piece of the SQL query or text string.
                + "WHERE r.reservation_id = ? FOR UPDATE";                        // Adds the next piece of the SQL query or text string.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            con.setAutoCommit(false);                                             // Begins manual transaction control so related database changes can be committed together.
            try {                                                                 // Starts a try block; resources are closed automatically when declared here.
                int bookId, available;                                            // Declares an integer variable.
                String status;                                                    // Declares a text variable.
                try (PreparedStatement ps = con.prepareStatement(checkSql)) {     // Starts a try block; resources are closed automatically when declared here.
                    ps.setInt(1, selectedReservationId);                          // Binds an integer value to a parameter in the SQL statement.
                    try (ResultSet rs = ps.executeQuery()) {                      // Starts a try block; resources are closed automatically when declared here.
                        if (!rs.next()) {                                         // Checks this condition before continuing: !rs.next().
                            con.rollback();                                       // Reverses uncommitted changes if the transaction fails.
                            JOptionPane.showMessageDialog(this, "Reservation not found."); // Displays a message to the user.
                            return;                                               // Exits this method without returning a value.
                        }                                                         // Ends this code block.
                        bookId = rs.getInt("book_id");                            // Reads an integer value from the current database row.
                        status = rs.getString("status");                          // Reads a text value from the current database row.
                        available = rs.getInt("available_copies");                // Reads an integer value from the current database row.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.

                if (!"Pending".equals(status)) {                                  // Checks this condition before continuing: !"Pending".equals(status).
                    con.rollback();                                               // Reverses uncommitted changes if the transaction fails.
                    JOptionPane.showMessageDialog(this, "This reservation is already " + status + "."); // Displays a message to the user.
                    return;                                                       // Exits this method without returning a value.
                }                                                                 // Ends this code block.
                if (available < 1) {                                              // Checks this condition before continuing: available < 1.
                    con.rollback();                                               // Reverses uncommitted changes if the transaction fails.
                    JOptionPane.showMessageDialog(this, "No available copies for this book."); // Displays a message to the user.
                    return;                                                       // Exits this method without returning a value.
                }                                                                 // Ends this code block.

                try (PreparedStatement ps = con.prepareStatement(                 // Starts a try block; resources are closed automatically when declared here.
                        "UPDATE reservations SET status = 'Approved' WHERE reservation_id = ?")) { // Contains SQL instructions for reading or changing database records.
                    ps.setInt(1, selectedReservationId);                          // Binds an integer value to a parameter in the SQL statement.
                    ps.executeUpdate();                                           // Executes an INSERT, UPDATE, or DELETE query.
                }                                                                 // Ends this code block.
                try (PreparedStatement ps = con.prepareStatement(                 // Starts a try block; resources are closed automatically when declared here.
                        "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?")) { // Contains SQL instructions for reading or changing database records.
                    ps.setInt(1, bookId);                                         // Binds an integer value to a parameter in the SQL statement.
                    ps.executeUpdate();                                           // Executes an INSERT, UPDATE, or DELETE query.
                }                                                                 // Ends this code block.

                con.commit();                                                     // Saves all successful changes in the current database transaction.
                JOptionPane.showMessageDialog(                                    // Displays a message to the user.
                        this,                                                     // Continues the current operation or expression.
                        "Reservation approved. One copy is held for the student.\n" // Adds the next piece of the SQL query or text string.
                        + "The student must get the book inside the library."     // Adds the next piece of the SQL query or text string.
                );                                                                // Performs the operation described by this statement.
                loadReservations(reservationFilter);                              // Performs the operation described by this statement.
            } catch (SQLException e) {                                            // Continues the current operation or expression.
                con.rollback();                                                   // Reverses uncommitted changes if the transaction fails.
                JOptionPane.showMessageDialog(this, "Could not approve reservation.\n" + e.getMessage()); // Displays a message to the user.
            } finally {                                                           // Continues the current operation or expression.
                con.setAutoCommit(true);                                          // Restores automatic commit mode for future database operations.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Database error.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Decline a pending reservation and optionally record the reason.
    public void declineReservation() {                                            // Declares the declineReservation method.
        if (selectedReservationId == -1) {                                        // Checks this condition before continuing: selectedReservationId == -1.
            JOptionPane.showMessageDialog(this, "Please select a reservation first."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        String note = JOptionPane.showInputDialog(this, "Reason for declining (optional):"); // Prompts the user to enter a value.
        if (note == null) return;                                                 // Checks this condition before continuing: if (note == null) return;.

        String sql = "UPDATE reservations SET status = 'Declined', admin_note = ? " // Builds the SQL query that will be executed against MySQL.
                + "WHERE reservation_id = ? AND status = 'Pending'";              // Adds the next piece of the SQL query or text string.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                ps.setString(1, note.trim());                                     // Binds a text value to a parameter in the SQL statement.
                ps.setInt(2, selectedReservationId);                              // Binds an integer value to a parameter in the SQL statement.
                int rows = ps.executeUpdate();                                    // Executes an INSERT, UPDATE, or DELETE query.

                if (rows > 0) {                                                   // Checks this condition before continuing: rows > 0.
                    JOptionPane.showMessageDialog(this, "Reservation declined."); // Displays a message to the user.
                    loadReservations(reservationFilter);                          // Performs the operation described by this statement.
                } else {                                                          // Continues the current operation or expression.
                    JOptionPane.showMessageDialog(this, "Only pending reservations can be declined."); // Displays a message to the user.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not decline reservation.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // =========================================================
    // 16. BOOK USAGE SCREEN
    // Purpose: Display usage records and let the Admin mark a book returned.
    // =========================================================
    public void showBookUsage() {                                                 // Declares the showBookUsage method.
        getContentPane().removeAll();                                             // Removes existing components before building the next screen.
        setTitle("Library Management System - Book Usage");                       // Sets the title displayed in the window title bar.
        setSize(1100, 650);                                                       // Sets the window or component dimensions.
        setLocationRelativeTo(null);                                              // Positions the window relative to the specified component or screen.

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));                  // Creates a panel to group and arrange GUI components.
        JPanel headerPanel = new JPanel(new BorderLayout());                      // Creates a panel to group and arrange GUI components.
        JLabel headerLabel = new JLabel("Book Usage (Borrow / Return)");          // Creates a label to display text in the GUI.
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));                    // Sets the font style and size for the component.
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));    // Sets the border or padding around the component.
        JButton backButton = new JButton("Back to Dashboard");                    // Creates a button labeled 'Back to Dashboard'.
        headerPanel.add(headerLabel, BorderLayout.WEST);                          // Adds this component to its parent container or panel.
        headerPanel.add(backButton, BorderLayout.EAST);                           // Adds this component to its parent container or panel.
        mainPanel.add(headerPanel, BorderLayout.NORTH);                           // Adds this component to its parent container or panel.

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));   // Creates a panel to group and arrange GUI components.
        JButton inUseButton = new JButton("Show In Use");                         // Creates a button labeled 'Show In Use'.
        JButton returnedButton = new JButton("Show Returned");                    // Creates a button labeled 'Show Returned'.
        JButton allButton = new JButton("Show All");                              // Creates a button labeled 'Show All'.
        JButton returnButton = new JButton("Mark Returned");                      // Creates a button labeled 'Mark Returned'.
        JButton refreshButton = new JButton("Refresh");                           // Creates a button labeled 'Refresh'.
        buttonPanel.add(inUseButton); buttonPanel.add(returnedButton);            // Adds this component to its parent container or panel.
        buttonPanel.add(allButton); buttonPanel.add(returnButton); buttonPanel.add(refreshButton); // Adds this component to its parent container or panel.

        usageModel = new DefaultTableModel() {                                    // Creates a table model to store the rows and columns shown in the table.
            @Override                                                             // Marks this method as an implementation that overrides a parent method.
            public boolean isCellEditable(int row, int column) { return false; }  // Declares the isCellEditable method.
        };                                                                        // Ends this statement.
        usageModel.setColumnIdentifiers(new Object[]{                             // Defines the column headings for the table.
            "ID", "Student ID", "Student Name", "Book Title", "Date",             // Continues the current operation or expression.
            "Time In", "Time Out", "Status"                                       // Adds the next piece of the SQL query or text string.
        });                                                                       // Closes the event handler or method call.

        usageTable = new JTable(usageModel);                                      // Creates a table for displaying records.
        usageTable.setRowHeight(24);                                              // Sets the height of each table row.
        usageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);                     // Sets how the table adjusts its column widths.
        JScrollPane scrollPane = new JScrollPane(usageTable);                     // Adds scrolling support to the enclosed component.

        JLabel hintLabel = new JLabel(                                            // Creates a label to display text in the GUI.
                "Using = book is in use  |  Returned = book is back in the library" // Adds the next piece of the SQL query or text string.
        );                                                                        // Performs the operation described by this statement.
        hintLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));         // Sets the border or padding around the component.

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));                 // Creates a panel to group and arrange GUI components.
        contentPanel.add(buttonPanel, BorderLayout.NORTH);                        // Adds this component to its parent container or panel.
        contentPanel.add(scrollPane, BorderLayout.CENTER);                        // Adds this component to its parent container or panel.
        contentPanel.add(hintLabel, BorderLayout.SOUTH);                          // Adds this component to its parent container or panel.
        mainPanel.add(contentPanel, BorderLayout.CENTER);                         // Adds this component to its parent container or panel.
        add(mainPanel);                                                           // Performs the operation described by this statement.

        inUseButton.addActionListener(e -> {                                      // Registers an event handler for user interaction.
            usageFilter = "Using";                                                // Performs the operation described by this statement.
            loadBookUsage("Using");                                               // Performs the operation described by this statement.
        });                                                                       // Closes the event handler or method call.
        returnedButton.addActionListener(e -> {                                   // Registers an event handler for user interaction.
            usageFilter = "Returned";                                             // Performs the operation described by this statement.
            loadBookUsage("Returned");                                            // Performs the operation described by this statement.
        });                                                                       // Closes the event handler or method call.
        allButton.addActionListener(e -> {                                        // Registers an event handler for user interaction.
            usageFilter = null;                                                   // Performs the operation described by this statement.
            loadBookUsage(null);                                                  // Performs the operation described by this statement.
        });                                                                       // Closes the event handler or method call.
        refreshButton.addActionListener(e -> loadBookUsage(usageFilter));         // Registers an event handler for user interaction.
        returnButton.addActionListener(e -> markUsageReturned());                 // Registers an event handler for user interaction.
        backButton.addActionListener(e -> showDashboard(adminName));              // Registers an event handler for user interaction.

        usageTable.getSelectionModel().addListSelectionListener(e -> {            // Continues the current operation or expression.
            if (e.getValueIsAdjusting()) return;                                  // Checks this condition before continuing: if (e.getValueIsAdjusting()) return;.
            int row = usageTable.getSelectedRow();                                // Stores an integer value in a variable.
            if (row < 0) return;                                                  // Checks this condition before continuing: if (row < 0) return;.
            selectedUsageId = Integer.parseInt(String.valueOf(usageModel.getValueAt(row, 0))); // Reads a value from the selected table row and column.
        });                                                                       // Closes the event handler or method call.

        usageFilter = "Using";                                                    // Performs the operation described by this statement.
        loadBookUsage("Using");                                                   // Performs the operation described by this statement.
        revalidate();                                                             // Recalculates the layout after GUI components change.
        repaint();                                                                // Redraws the GUI so the latest changes appear.
        setVisible(true);                                                         // Makes the window or component visible.
    }                                                                             // Ends this code block.

    // Load usage records. A null filter means show every record.
    public void loadBookUsage(String statusFilter) {                              // Declares the loadBookUsage method.
        String sql = "SELECT u.usage_id, m.student_id, m.full_name, b.title, "    // Builds the SQL query that will be executed against MySQL.
                + "u.usage_date, u.time_in, u.time_out, u.status "                // Adds the next piece of the SQL query or text string.
                + "FROM book_usage u JOIN members m ON u.member_id = m.member_id " // Adds the next piece of the SQL query or text string.
                + "JOIN books b ON u.book_id = b.book_id ";                       // Adds the next piece of the SQL query or text string.

        if (statusFilter != null) sql += "WHERE u.status = ? ";                   // Checks this condition before continuing: if (statusFilter != null) sql += "WHERE u.status = ? ";.
        sql += "ORDER BY u.usage_date DESC, u.time_in DESC";                      // Performs the operation described by this statement.

        usageModel.setRowCount(0);                                                // Clears existing table rows before loading fresh data.
        selectedUsageId = -1;                                                     // Performs the operation described by this statement.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.
            try (PreparedStatement ps = con.prepareStatement(sql)) {              // Starts a try block; resources are closed automatically when declared here.
                if (statusFilter != null) ps.setString(1, statusFilter);          // Checks this condition before continuing: if (statusFilter != null) ps.setString(1, statusFilter);.
                try (ResultSet result = ps.executeQuery()) {                      // Starts a try block; resources are closed automatically when declared here.
                    while (result.next()) {                                       // Moves to the next row in the result set, if one exists.
                        usageModel.addRow(new Object[]{                           // Adds a record to the table displayed in the GUI.
                            result.getInt("usage_id"), result.getString("student_id"), // Reads a text value from the current database row.
                            result.getString("full_name"), result.getString("title"), // Reads a text value from the current database row.
                            result.getDate("usage_date"), result.getTime("time_in"), // Reads a date value from the current database row.
                            result.getTime("time_out"), result.getString("status") // Reads a text value from the current database row.
                        });                                                       // Closes the event handler or method call.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Could not load book usage.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // Mark the selected active usage as returned and increase available copies.
    public void markUsageReturned() {                                             // Declares the markUsageReturned method.
        if (selectedUsageId == -1) {                                              // Checks this condition before continuing: selectedUsageId == -1.
            JOptionPane.showMessageDialog(this, "Please select a usage record first."); // Displays a message to the user.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        int answer = JOptionPane.showConfirmDialog(                               // Asks the user to confirm an action.
                this, "Mark this book as returned?", "Confirm", JOptionPane.YES_NO_OPTION // Continues the current operation or expression.
        );                                                                        // Performs the operation described by this statement.
        if (answer != JOptionPane.YES_OPTION) return;                             // Checks this condition before continuing: if (answer != JOptionPane.YES_OPTION) return;.

        String selectSql = "SELECT book_id, reservation_id, status FROM book_usage WHERE usage_id = ?"; // Contains SQL instructions for reading or changing database records.
        String usageSql = "UPDATE book_usage SET time_out = CURTIME(), status = 'Returned' " // Builds the SQL query that will be executed against MySQL.
                + "WHERE usage_id = ? AND status = 'Using'";                      // Adds the next piece of the SQL query or text string.
        String bookSql = "UPDATE books SET available_copies = "                   // Builds the SQL query that will be executed against MySQL.
                + "LEAST(quantity, available_copies + 1) WHERE book_id = ?";      // Adds the next piece of the SQL query or text string.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(this, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.

            con.setAutoCommit(false);                                             // Begins manual transaction control so related database changes can be committed together.
            try {                                                                 // Starts a try block; resources are closed automatically when declared here.
                int bookId;                                                       // Declares an integer variable.
                Integer reservationId;                                            // Performs the operation described by this statement.
                String status;                                                    // Declares a text variable.

                try (PreparedStatement ps = con.prepareStatement(selectSql)) {    // Starts a try block; resources are closed automatically when declared here.
                    ps.setInt(1, selectedUsageId);                                // Binds an integer value to a parameter in the SQL statement.
                    try (ResultSet rs = ps.executeQuery()) {                      // Starts a try block; resources are closed automatically when declared here.
                        if (!rs.next()) {                                         // Checks this condition before continuing: !rs.next().
                            con.rollback();                                       // Reverses uncommitted changes if the transaction fails.
                            JOptionPane.showMessageDialog(this, "Usage record not found."); // Displays a message to the user.
                            return;                                               // Exits this method without returning a value.
                        }                                                         // Ends this code block.
                        bookId = rs.getInt("book_id");                            // Reads an integer value from the current database row.
                        status = rs.getString("status");                          // Reads a text value from the current database row.
                        Object value = rs.getObject("reservation_id");            // Reads a database value without assuming its data type.
                        reservationId = value == null ? null : ((Number) value).intValue(); // Performs the operation described by this statement.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.

                if (!"Using".equals(status)) {                                    // Checks this condition before continuing: !"Using".equals(status).
                    con.rollback();                                               // Reverses uncommitted changes if the transaction fails.
                    JOptionPane.showMessageDialog(this, "This book is already " + status + "."); // Displays a message to the user.
                    return;                                                       // Exits this method without returning a value.
                }                                                                 // Ends this code block.

                try (PreparedStatement ps = con.prepareStatement(usageSql)) {     // Starts a try block; resources are closed automatically when declared here.
                    ps.setInt(1, selectedUsageId);                                // Binds an integer value to a parameter in the SQL statement.
                    if (ps.executeUpdate() == 0) {                                // Checks this condition before continuing: ps.executeUpdate() == 0.
                        con.rollback();                                           // Reverses uncommitted changes if the transaction fails.
                        JOptionPane.showMessageDialog(this, "Could not update the usage record."); // Displays a message to the user.
                        return;                                                   // Exits this method without returning a value.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.

                try (PreparedStatement ps = con.prepareStatement(bookSql)) {      // Starts a try block; resources are closed automatically when declared here.
                    ps.setInt(1, bookId);                                         // Binds an integer value to a parameter in the SQL statement.
                    ps.executeUpdate();                                           // Executes an INSERT, UPDATE, or DELETE query.
                }                                                                 // Ends this code block.

                if (reservationId != null) {                                      // Checks this condition before continuing: reservationId != null.
                    try (PreparedStatement ps = con.prepareStatement(             // Starts a try block; resources are closed automatically when declared here.
                            "UPDATE reservations SET status = 'Completed' "       // Adds the next piece of the SQL query or text string.
                            + "WHERE reservation_id = ? AND status = 'Borrowed'")) { // Adds the next piece of the SQL query or text string.
                        ps.setInt(1, reservationId);                              // Binds an integer value to a parameter in the SQL statement.
                        ps.executeUpdate();                                       // Executes an INSERT, UPDATE, or DELETE query.
                    }                                                             // Ends this code block.
                }                                                                 // Ends this code block.

                con.commit();                                                     // Saves all successful changes in the current database transaction.
                JOptionPane.showMessageDialog(this, "Book marked as returned. Copy is available again."); // Displays a message to the user.
                loadBookUsage(usageFilter);                                       // Performs the operation described by this statement.
            } catch (SQLException e) {                                            // Continues the current operation or expression.
                con.rollback();                                                   // Reverses uncommitted changes if the transaction fails.
                JOptionPane.showMessageDialog(this, "Could not mark book as returned.\n" + e.getMessage()); // Displays a message to the user.
            } finally {                                                           // Continues the current operation or expression.
                con.setAutoCommit(true);                                          // Restores automatic commit mode for future database operations.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(this, "Database error.\n" + e.getMessage()); // Displays a message to the user.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // =========================================================
    // 17. REPORTS
    // Purpose: Display Books, Student Members and Book Usage reports.
    // =========================================================
    public void showReports() {                                                   // Declares the showReports method.
        JFrame reportFrame = new JFrame("Library Reports");                       // Performs the operation described by this statement.
        reportFrame.setSize(900, 500);                                            // Sets the window or component dimensions.
        reportFrame.setLocationRelativeTo(this);                                  // Positions the window relative to the specified component or screen.
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);            // Sets what happens when the user closes this window.
        reportFrame.setLayout(new BorderLayout(10, 10));                          // Sets a border layout for placing components around the panel.

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));            // Creates a panel to group and arrange GUI components.
        String[] reportOptions = {                                                // Continues the current operation or expression.
            "Books Report", "Student Members Report", "Book Usage History"        // Adds the next piece of the SQL query or text string.
        };                                                                        // Ends this statement.
        JComboBox<String> reportBox = new JComboBox<>(reportOptions);             // Creates a drop-down list of selectable options.
        JButton loadButton = new JButton("Load Report");                          // Creates a button labeled 'Load Report'.
        topPanel.add(new JLabel("Select Report:"));                               // Creates a label to display text in the GUI.
        topPanel.add(reportBox);                                                  // Adds this component to its parent container or panel.
        topPanel.add(loadButton);                                                 // Adds this component to its parent container or panel.
        reportFrame.add(topPanel, BorderLayout.NORTH);                            // Adds this component to its parent container or panel.

        DefaultTableModel model = new DefaultTableModel() {                       // Creates a table model to store the rows and columns shown in the table.
            @Override                                                             // Marks this method as an implementation that overrides a parent method.
            public boolean isCellEditable(int row, int column) { return false; }  // Declares the isCellEditable method.
        };                                                                        // Ends this statement.
        JTable reportTable = new JTable(model);                                   // Creates a table for displaying records.
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);                    // Sets how the table adjusts its column widths.
        reportTable.setRowHeight(25);                                             // Sets the height of each table row.
        reportFrame.add(new JScrollPane(reportTable), BorderLayout.CENTER);       // Adds scrolling support to the enclosed component.

        loadButton.addActionListener(e -> loadReport(                             // Registers an event handler for user interaction.
                reportBox.getSelectedItem().toString(), model, reportFrame));     // Performs the operation described by this statement.

        reportFrame.setVisible(true);                                             // Makes the window or component visible.
        loadReport("Books Report", model, reportFrame);                           // Performs the operation described by this statement.
    }                                                                             // Ends this code block.

    // Load the report selected in the dropdown.
    public void loadReport(String reportType, DefaultTableModel model, JFrame reportFrame) { // Declares the loadReport method.
        String sql;                                                               // Declares a text variable.

        if ("Books Report".equals(reportType)) {                                  // Checks this condition before continuing: "Books Report".equals(reportType).
            sql = "SELECT book_id AS 'Book ID', isbn AS ISBN, title AS Title, "   // Contains SQL instructions for reading or changing database records.
                    + "author AS Author, category AS Category, quantity AS Quantity, " // Adds the next piece of the SQL query or text string.
                    + "available_copies AS 'Available Copies' FROM books ORDER BY title"; // Adds the next piece of the SQL query or text string.
        } else if ("Student Members Report".equals(reportType)) {                 // Continues the current operation or expression.
            sql = "SELECT member_id AS 'Member ID', student_id AS 'Student ID', " // Contains SQL instructions for reading or changing database records.
                    + "full_name AS 'Full Name', course AS Course, year_level AS 'Year Level', " // Adds the next piece of the SQL query or text string.
                    + "section AS Section, contact_number AS 'Contact Number', status AS Status " // Adds the next piece of the SQL query or text string.
                    + "FROM members ORDER BY full_name";                          // Adds the next piece of the SQL query or text string.
        } else if ("Book Usage History".equals(reportType)) {                     // Continues the current operation or expression.
            sql = "SELECT u.usage_id AS 'Usage ID', m.student_id AS 'Student ID', " // Contains SQL instructions for reading or changing database records.
                    + "m.full_name AS 'Student Name', b.title AS 'Book Title', "  // Adds the next piece of the SQL query or text string.
                    + "u.usage_date AS Date, u.time_in AS 'Time In', u.time_out AS 'Time Out', " // Adds the next piece of the SQL query or text string.
                    + "u.status AS Status FROM book_usage u "                     // Adds the next piece of the SQL query or text string.
                    + "JOIN books b ON u.book_id = b.book_id "                    // Adds the next piece of the SQL query or text string.
                    + "JOIN members m ON u.member_id = m.member_id "              // Adds the next piece of the SQL query or text string.
                    + "ORDER BY u.usage_date DESC, u.time_in DESC";               // Adds the next piece of the SQL query or text string.
        } else {                                                                  // Continues the current operation or expression.
            return;                                                               // Exits this method without returning a value.
        }                                                                         // Ends this code block.

        model.setRowCount(0);                                                     // Clears existing table rows before loading fresh data.
        model.setColumnCount(0);                                                  // Performs the operation described by this statement.

        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) {                                                    // Checks this condition before continuing: con == null.
                JOptionPane.showMessageDialog(reportFrame, "Database connection failed!"); // Displays a message to the user.
                return;                                                           // Exits this method without returning a value.
            }                                                                     // Ends this code block.
            try (Statement statement = con.createStatement();                     // Starts a try block; resources are closed automatically when declared here.
                 ResultSet result = statement.executeQuery(sql)) {                // Continues the current operation or expression.
                ResultSetMetaData meta = result.getMetaData();                    // Performs the operation described by this statement.
                int columns = meta.getColumnCount();                              // Stores an integer value in a variable.

                for (int i = 1; i <= columns; i++) {                              // Repeats this block over the specified range or collection.
                    model.addColumn(meta.getColumnLabel(i));                      // Performs the operation described by this statement.
                }                                                                 // Ends this code block.
                while (result.next()) {                                           // Moves to the next row in the result set, if one exists.
                    Object[] row = new Object[columns];                           // Performs the operation described by this statement.
                    for (int i = 1; i <= columns; i++) {                          // Repeats this block over the specified range or collection.
                        row[i - 1] = result.getObject(i);                         // Reads a database value without assuming its data type.
                    }                                                             // Ends this code block.
                    model.addRow(row);                                            // Adds a record to the table displayed in the GUI.
                }                                                                 // Ends this code block.
                if (model.getRowCount() == 0) {                                   // Checks this condition before continuing: model.getRowCount() == 0.
                    JOptionPane.showMessageDialog(reportFrame, "No records found for this report."); // Displays a message to the user.
                }                                                                 // Ends this code block.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            JOptionPane.showMessageDialog(                                        // Displays a message to the user.
                    reportFrame,                                                  // Continues the current operation or expression.
                    "Could not load report. Check the database tables.\n\n" + e.getMessage(), // Continues the current operation or expression.
                    "Report Error", JOptionPane.ERROR_MESSAGE                     // Continues the current operation or expression.
            );                                                                    // Performs the operation described by this statement.
        }                                                                         // Ends this code block.
    }                                                                             // Ends this code block.

    // =========================================================
    // 18. SHARED DASHBOARD AND DATABASE HELPERS
    // Purpose: Create the summary cards and read counts from MySQL.
    // =========================================================
    public JPanel createCard(String title, int count) {                           // Declares the createCard method.
        JPanel card = new JPanel(new GridLayout(2, 1));                           // Creates a panel to group and arrange GUI components.
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));               // Sets the border or padding around the component.

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);             // Creates a label to display text in the GUI.
        JLabel countLabel = new JLabel(String.valueOf(count), SwingConstants.CENTER); // Creates a label to display text in the GUI.
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));                     // Sets the font style and size for the component.
        countLabel.setFont(new Font("Arial", Font.BOLD, 24));                     // Sets the font style and size for the component.
        card.add(titleLabel);                                                     // Adds this component to its parent container or panel.
        card.add(countLabel);                                                     // Adds this component to its parent container or panel.
        return card;                                                              // Returns this value to the caller.
    }                                                                             // Ends this code block.

    public int getCount(String sql) {                                             // Declares the getCount method.
        try (Connection con = database_connection.connect()) {                    // Starts a try block; resources are closed automatically when declared here.
            if (con == null) return 0;                                            // Checks this condition before continuing: if (con == null) return 0;.

            try (Statement statement = con.createStatement();                     // Starts a try block; resources are closed automatically when declared here.
                 ResultSet result = statement.executeQuery(sql)) {                // Continues the current operation or expression.
                if (result.next()) return result.getInt(1);                       // Checks this condition before continuing: if (result.next()) return result.getInt(1);.
            }                                                                     // Ends this code block.
        } catch (SQLException e) {                                                // Continues the current operation or expression.
            System.out.println("Dashboard error: " + e.getMessage());             // Performs the operation described by this statement.
        }                                                                         // Ends this code block.
        return 0;                                                                 // Returns this value to the caller.
    }                                                                             // Ends this code block.

    // =========================================================
    // 19. PROGRAM START
    // Purpose: Launch the application on Swing's event thread.
    // =========================================================
    public static void main(String[] args) {                                      // Declares the main method.
        SwingUtilities.invokeLater(() -> new admin());                            // Starts the GUI on Swing’s event-dispatch thread.
    }                                                                             // Ends this code block.
}                                                                                 // Ends this code block.