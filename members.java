
import java.awt.*; // Provides GUI layouts, fonts, colors, and graphics.
import java.security.MessageDigest; // Compares password hashes securely.
import java.security.SecureRandom; // Generates secure random values for password salts.
import java.sql.*; // Provides classes for database operations.
import java.time.LocalDate; // Gets the current date.
import java.util.Arrays; // Compares arrays and clears sensitive password data.
import java.util.Base64; // Encodes and decodes binary data as Base64 strings.
import javax.crypto.SecretKeyFactory; // Generates password hashes using a cryptographic algorithm.
import javax.crypto.spec.PBEKeySpec; // Sets the password, salt, and hashing parameters.
import javax.swing.*; // Provides GUI components such as buttons, labels, and windows.
import javax.swing.table.DefaultTableModel; // Manages data displayed in JTable.

public class members extends JFrame {                                         // Defines the student application window using JFrame.

    // ===== A. FORM FIELDS AND MEMBER SESSION =====
    // Login fields: student enters Student ID and password.
    JTextField loginStudentId;                                                // Declares a GUI component variable.
    JPasswordField loginPassword;                                             // Declares a GUI component variable.

    // Sign-up fields: information entered when a student registers.
    JTextField signupStudentId, signupFullName, signupCourse;                 // Declares a GUI component variable.
    JTextField signupYearLevel, signupSection, signupContact;                 // Declares a GUI component variable.
    JTextField signupAddress;                                                 // Declares a GUI component variable.

    JPasswordField signupPassword, signupConfirm;                             // Declares a GUI component variable.

    // Current logged-in student information, used to filter personal records.
    int memberId = -1;                                                        // Stores the numeric member id value.
    String studentId = "", fullName = "", course = "";                        // Stores the student id text value.
    String yearLevel = "", section = "", status = "";                         // Stores the year level text value.

    // Table model for this student's book usage history.
    DefaultTableModel model;                                                  // Declares a GUI component variable.

    // Table model and controls for browsing available books.
    DefaultTableModel booksModel;                                             // Declares a GUI component variable.
    JTable booksTable;                                                        // Declares a GUI component variable.
    JTextField bookSearchField;                                               // Declares a GUI component variable.

    // Table model for the current student's reservations.
    DefaultTableModel reservationsModel;                                      // Declares a GUI component variable.
    JTable reservationsTable;                                                 // Declares a GUI component variable.

    // ===== B. CONSTRUCTORS =====
    // Opens the login form with an empty Student ID field.
    public members() {                                                        // Constructor: opens the student login screen.
        showLogin("");                                                        // Executes this statement.
    }                                                                         // Ends the current block of code.

    // Opens login and pre-fills Student ID after registration when possible.
    public members(String studentId) {                                        // Constructor: opens login with the supplied Student ID prefilled.
        showLogin(studentId);                                                 // Executes this statement.
    }                                                                         // Ends the current block of code.

    // ===== 1. STUDENT LOGIN SCREEN =====
    // Purpose: display the Student ID/password fields and signup link.
    public void showLogin(String filledStudentId) {                           // Builds and displays the student login form.

        getContentPane().removeAll();                                         // Executes this statement.

        setTitle("Library Management System - Student Login");                // Executes this statement.
        setSize(380, 230);                                                    // Executes this statement.
        setLocationRelativeTo(null);                                          // Executes this statement.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);                    // Executes this statement.

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));              // Creates or assigns the panel GUI component.

        panel.setBorder(                                                      // Adds a border or padding around the component.
                BorderFactory.createEmptyBorder(20, 20, 20, 20)               // Creates spacing around the component.
        );                                                                    // Executes this statement.

        loginStudentId = new JTextField(filledStudentId);                     // Creates a text input field.
        loginPassword = new JPasswordField();                                 // Creates an input field that masks the typed password.

        JButton loginButton = new JButton("Login");                           // Creates or assigns the login button GUI component.
        JButton signupButton = new JButton("Sign Up");                        // Creates or assigns the signup button GUI component.

        panel.add(new JLabel("Student ID:"));                                 // Creates a text label in the interface.
        panel.add(loginStudentId);                                            // Adds a component to its parent panel or container.

        panel.add(new JLabel("Password:"));                                   // Creates a text label in the interface.
        panel.add(loginPassword);                                             // Adds a component to its parent panel or container.

        panel.add(loginButton);                                               // Adds a component to its parent panel or container.
        panel.add(signupButton);                                              // Adds a component to its parent panel or container.

        add(panel);                                                           // Executes this statement.

        loginButton.addActionListener(e -> loginStudent());                   // Connects this button or control to the code that runs when clicked.
        signupButton.addActionListener(e -> showSignup());                    // Connects this button or control to the code that runs when clicked.

        getRootPane().setDefaultButton(loginButton);                          // Executes this statement.

        revalidate();                                                         // Executes this statement.
        repaint();                                                            // Executes this statement.
        setVisible(true);                                                     // Executes this statement.
    }                                                                         // Ends the current block of code.

    // Purpose: validate the account, verify its password hash, then log in.
    public void loginStudent() {                                              // Checks the Student ID, account status, and password before login.

        String typedId = loginStudentId.getText().trim();                     // Stores the typed id text value.
        char[] password = loginPassword.getPassword();                        // Stores password characters in a character array (password).

        if (typedId.isEmpty() || password.length == 0) {                      // Checks whether required data is missing or an item is not selected.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Please enter your Student ID and password."        // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        String sql = "SELECT member_id, student_id, full_name, course, "      // Stores the SQL query used for this database operation.
                + "year_level, section, status, password_hash "               // Continues the expression or declaration for this operation.
                + "FROM members WHERE student_id = ?";                        // Executes this statement.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            try (PreparedStatement ps = con.prepareStatement(sql)) {          // Creates a prepared SQL statement and closes it automatically after use.

                ps.setString(1, typedId);                                     // Sets a text parameter in the prepared SQL query.

                try (ResultSet rs = ps.executeQuery()) {                      // Reads query results and closes the result set automatically.

                    if (!rs.next()) {                                         // Checks a condition before continuing.
                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this, "Invalid Student ID or password!"       // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.
                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.

                    String foundStatus = rs.getString("status");              // Stores the found status text value.
                    String storedHash = rs.getString("password_hash");        // Stores the stored hash text value.

                    if (!"Active".equals(foundStatus)) {                      // Checks whether two values match the required state.

                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this, "This account is not active."           // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.

                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.

                    if (storedHash == null) {                                 // Checks whether required data is missing or an item is not selected.

                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this,                                         // Continues the expression or declaration for this operation.
                                "This account has no password yet.\n"         // Continues the expression or declaration for this operation.
                                + "Please use Student Sign Up."               // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.

                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.

                    if (!verifyPassword(password, storedHash)) {              // Checks a condition before continuing.

                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this, "Invalid Student ID or password!"       // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.

                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.

                    // KEEP THE MEMBER DETAILS
                    memberId = rs.getInt("member_id");                        // Reads an integer value from a query result.
                    studentId = rs.getString("student_id");                   // Reads a text value from a query result.
                    fullName = rs.getString("full_name");                     // Reads a text value from a query result.
                    course = rs.getString("course");                          // Reads a text value from a query result.
                    yearLevel = rs.getString("year_level");                   // Reads a text value from a query result.
                    section = rs.getString("section");                        // Reads a text value from a query result.
                    status = foundStatus;                                     // Executes this statement.

                    showDashboard();                                          // Executes this statement.
                }                                                             // Ends the current block of code.
            }                                                                 // Ends the current block of code.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Login error: " + e.getMessage()                    // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

        } catch (Exception e) {                                               // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Could not check the password."                     // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

        } finally {                                                           // Begins this block of code.
            Arrays.fill(password, '\0');                                      // Clears sensitive password characters from the array.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // ===== 2. STUDENT SIGN UP SCREEN =====
    // Purpose: collect new student details and account password.
    public void showSignup() {                                                // Builds and displays the student registration form.

        getContentPane().removeAll();                                         // Executes this statement.

        setTitle("Library Management System - Student Sign Up");              // Executes this statement.
        setSize(420, 500);                                                    // Executes this statement.
        setLocationRelativeTo(null);                                          // Executes this statement.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);                    // Executes this statement.

        JPanel panel = new JPanel(new GridLayout(10, 2, 8, 8));               // Creates or assigns the panel GUI component.

        panel.setBorder(                                                      // Adds a border or padding around the component.
                BorderFactory.createEmptyBorder(15, 15, 15, 15)               // Creates spacing around the component.
        );                                                                    // Executes this statement.

        signupStudentId = new JTextField();                                   // Creates a text input field.
        signupFullName = new JTextField();                                    // Creates a text input field.
        signupCourse = new JTextField();                                      // Creates a text input field.
        signupYearLevel = new JTextField();                                   // Creates a text input field.
        signupSection = new JTextField();                                     // Creates a text input field.
        signupContact = new JTextField();                                     // Creates a text input field.
        signupAddress = new JTextField();                                     // Creates a text input field.

        signupPassword = new JPasswordField();                                // Creates an input field that masks the typed password.
        signupConfirm = new JPasswordField();                                 // Creates an input field that masks the typed password.

        JButton registerButton = new JButton("Sign Up");                      // Creates or assigns the register button GUI component.
        JButton backButton = new JButton("Back to Login");                    // Creates or assigns the back button GUI component.

        panel.add(new JLabel("Student ID:"));                                 // Creates a text label in the interface.
        panel.add(signupStudentId);                                           // Adds a component to its parent panel or container.

        panel.add(new JLabel("Full Name:"));                                  // Creates a text label in the interface.
        panel.add(signupFullName);                                            // Adds a component to its parent panel or container.

        panel.add(new JLabel("Course:"));                                     // Creates a text label in the interface.
        panel.add(signupCourse);                                              // Adds a component to its parent panel or container.

        panel.add(new JLabel("Year Level:"));                                 // Creates a text label in the interface.
        panel.add(signupYearLevel);                                           // Adds a component to its parent panel or container.

        panel.add(new JLabel("Section:"));                                    // Creates a text label in the interface.
        panel.add(signupSection);                                             // Adds a component to its parent panel or container.

        panel.add(new JLabel("Contact Number:"));                             // Creates a text label in the interface.
        panel.add(signupContact);                                             // Adds a component to its parent panel or container.

        panel.add(new JLabel("Address:"));                                    // Creates a text label in the interface.
        panel.add(signupAddress);                                             // Adds a component to its parent panel or container.

        panel.add(new JLabel("Password:"));                                   // Creates a text label in the interface.
        panel.add(signupPassword);                                            // Adds a component to its parent panel or container.

        panel.add(new JLabel("Confirm Password:"));                           // Creates a text label in the interface.
        panel.add(signupConfirm);                                             // Adds a component to its parent panel or container.

        panel.add(registerButton);                                            // Adds a component to its parent panel or container.
        panel.add(backButton);                                                // Adds a component to its parent panel or container.

        add(panel);                                                           // Executes this statement.

        registerButton.addActionListener(e -> register());                    // Connects this button or control to the code that runs when clicked.
        backButton.addActionListener(e -> showLogin(""));                     // Connects this button or control to the code that runs when clicked.

        revalidate();                                                         // Executes this statement.
        repaint();                                                            // Executes this statement.
        setVisible(true);                                                     // Executes this statement.
    }                                                                         // Ends the current block of code.

    // Purpose: validate signup fields and save the student in the members table.
    public void register() {                                                  // Validates the registration form and saves the new student account.

        String id = signupStudentId.getText().trim();                         // Stores the id text value.
        String name = signupFullName.getText().trim();                        // Stores the name text value.

        String courseText = signupCourse.getText().trim();                    // Stores the course text text value.
        String yearText = signupYearLevel.getText().trim();                   // Stores the year text text value.
        String sectionText = signupSection.getText().trim();                  // Stores the section text text value.
        String contactText = signupContact.getText().trim();                  // Stores the contact text text value.
        String addressText = signupAddress.getText().trim();                  // Stores the address text text value.

        char[] pass = signupPassword.getPassword();                           // Stores password characters in a character array (pass).
        char[] confirm = signupConfirm.getPassword();                         // Stores password characters in a character array (confirm).

        if (id.isEmpty() || name.isEmpty()                                    // Checks whether required data is missing or an item is not selected.
                || pass.length == 0 || confirm.length == 0) {                 // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Please fill in the required fields."               // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        if (!Arrays.equals(pass, confirm)) {                                  // Checks whether two values match the required state.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Passwords do not match."                           // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        if (pass.length < 8) {                                                // Checks a condition before continuing.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Password must have at least 8 characters."         // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        String sql = "INSERT INTO members "                                   // Stores the SQL query used for this database operation.
                + "(student_id, full_name, course, year_level, section, "     // Continues the expression or declaration for this operation.
                + "contact_number, address, registration_date, status, "      // Continues the expression or declaration for this operation.
                + "password_hash) "                                           // Continues the expression or declaration for this operation.
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active', ?)";             // Executes this statement.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            try (PreparedStatement ps = con.prepareStatement(                 // Creates a prepared SQL statement and closes it automatically after use.
                    sql, Statement.RETURN_GENERATED_KEYS)) {                  // Begins this block of code.

                ps.setString(1, id);                                          // Sets a text parameter in the prepared SQL query.
                ps.setString(2, name);                                        // Sets a text parameter in the prepared SQL query.
                ps.setString(3, courseText);                                  // Sets a text parameter in the prepared SQL query.
                ps.setString(4, yearText);                                    // Sets a text parameter in the prepared SQL query.
                ps.setString(5, sectionText);                                 // Sets a text parameter in the prepared SQL query.
                ps.setString(6, contactText);                                 // Sets a text parameter in the prepared SQL query.
                ps.setString(7, addressText);                                 // Sets a text parameter in the prepared SQL query.

                ps.setDate(8, java.sql.Date.valueOf(LocalDate.now()));        // Sets a date parameter in the prepared SQL query.

                // Store a password hash instead of the actual password.
                ps.setString(9, hashPassword(pass));                          // Sets a text parameter in the prepared SQL query.

                ps.executeUpdate();                                           // Runs an INSERT, UPDATE, or DELETE query.

                // GET THE NEW member_id
                int newMemberId = 0;                                          // Stores the numeric new member id value.

                try (ResultSet keys = ps.getGeneratedKeys()) {                // Reads query results and closes the result set automatically.

                    if (keys.next()) {                                        // Checks a condition before continuing.
                        newMemberId = keys.getInt(1);                         // Reads an integer value from a query result.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this,                                                 // Continues the expression or declaration for this operation.
                        "Registration successful! Welcome to the library."    // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.

                clearSignupFields();                                          // Executes this statement.

                if (newMemberId > 0) {                                        // Checks a condition before continuing.

                    // GO STRAIGHT TO THE DASHBOARD
                    memberId = newMemberId;                                   // Executes this statement.
                    studentId = id;                                           // Executes this statement.
                    fullName = name;                                          // Executes this statement.
                    course = courseText;                                      // Executes this statement.
                    yearLevel = yearText;                                     // Executes this statement.
                    section = sectionText;                                    // Executes this statement.
                    status = "Active";                                        // Executes this statement.

                    showDashboard();                                          // Executes this statement.

                } else {                                                      // Begins this block of code.

                    showLogin(id);                                            // Executes this statement.
                }                                                             // Ends the current block of code.
            }                                                                 // Ends the current block of code.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Registration failed. Check if the Student ID "           // Continues the expression or declaration for this operation.
                    + "is already registered.\n" + e.getMessage()             // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

        } catch (Exception e) {                                               // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Could not secure the password."                    // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

        } finally {                                                           // Begins this block of code.
            Arrays.fill(pass, '\0');                                          // Clears sensitive password characters from the array.
            Arrays.fill(confirm, '\0');                                       // Clears sensitive password characters from the array.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // ===== 3. MEMBER DASHBOARD SCREEN =====
    // Purpose: show books, reservations, and this student's usage history.
    public void showDashboard() {                                             // Builds the dashboard for the logged-in student.

        getContentPane().removeAll();                                         // Executes this statement.

        setTitle("Library Management System - Member Dashboard");             // Executes this statement.
        setSize(900, 600);                                                    // Executes this statement.
        setLocationRelativeTo(null);                                          // Executes this statement.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);                    // Executes this statement.

        // HEADER
        JLabel welcomeLabel = new JLabel(                                     // Creates or assigns the welcome label GUI component.
                "Welcome, " + fullName + "!",                                 // Continues the expression or declaration for this operation.
                SwingConstants.CENTER                                         // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));               // Executes this statement.
        welcomeLabel.setBorder(                                               // Adds a border or padding around the component.
                BorderFactory.createEmptyBorder(15, 5, 5, 5)                  // Creates spacing around the component.
        );                                                                    // Executes this statement.

        // STUDENT INFORMATION
        JPanel infoPanel = new JPanel(                                        // Creates or assigns the info panel GUI component.
                new GridLayout(1, 4, 10, 10)                                  // Arranges components in rows and columns.
        );                                                                    // Executes this statement.

        infoPanel.setBorder(                                                  // Adds a border or padding around the component.
                BorderFactory.createEmptyBorder(5, 20, 10, 20)                // Creates spacing around the component.
        );                                                                    // Executes this statement.

        infoPanel.add(createInfo("Student ID", studentId));                   // Adds a component to its parent panel or container.
        infoPanel.add(createInfo("Course", course));                          // Adds a component to its parent panel or container.
        infoPanel.add(createInfo("Year Level", yearLevel));                   // Adds a component to its parent panel or container.
        infoPanel.add(createInfo("Section", section));                        // Adds a component to its parent panel or container.

        // ===== AVAILABLE BOOKS TABLE =====
        booksModel = new DefaultTableModel() {                                // Begins this block of code.
            @Override                                                         // Continues the expression or declaration for this operation.
            public boolean isCellEditable(int row, int column) {              // Defines the is cell editable method.
                return false;                                                 // Returns a value to the method caller.
            }                                                                 // Ends the current block of code.
        };                                                                    // Executes this statement.

        booksModel.setColumnIdentifiers(new Object[]{                         // Defines the column headings for the table model.
            "book_id", "Title", "Author", "Category", "Publisher", "Year",    // Continues the expression or declaration for this operation.
            "Total Copies", "Available Copies", "In Use / Held"               // Continues the expression or declaration for this operation.
        });                                                                   // Executes this statement.

        booksTable = new JTable(booksModel);                                  // Creates a table for displaying records.
        booksTable.setRowHeight(24);                                          // Sets the height of each table row.
        booksTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);                 // Controls how the table columns adjust to the window width.

        // HIDE book_id (COLUMN 0) - INTERNAL USE ONLY
        booksTable.removeColumn(booksTable.getColumnModel().getColumn(0));    // Hides an internal ID column from the visible table.

        // SEARCH BAR
        JPanel searchPanel = new JPanel(                                      // Creates or assigns the search panel GUI component.
                new FlowLayout(FlowLayout.LEFT, 8, 8)                         // Arranges components in a row, wrapping when needed.
        );                                                                    // Executes this statement.

        bookSearchField = new JTextField(18);                                 // Creates a text input field.

        JButton searchButton = new JButton("Search");                         // Creates or assigns the search button GUI component.
        JButton showAllButton = new JButton("Show All");                      // Creates or assigns the show all button GUI component.
        JButton reserveButton = new JButton("Reserve Selected Book");         // Creates or assigns the reserve button GUI component.

        searchPanel.add(new JLabel("Search:"));                               // Creates a text label in the interface.
        searchPanel.add(bookSearchField);                                     // Adds a component to its parent panel or container.
        searchPanel.add(searchButton);                                        // Adds a component to its parent panel or container.
        searchPanel.add(showAllButton);                                       // Adds a component to its parent panel or container.
        searchPanel.add(reserveButton);                                       // Adds a component to its parent panel or container.

        JPanel booksPanel = new JPanel(new BorderLayout(5, 5));               // Creates or assigns the books panel GUI component.
        booksPanel.setBorder(                                                 // Adds a border or padding around the component.
                BorderFactory.createEmptyBorder(5, 10, 5, 10)                 // Creates spacing around the component.
        );                                                                    // Executes this statement.
        booksPanel.add(searchPanel, BorderLayout.NORTH);                      // Adds a component to its parent panel or container.
        booksPanel.add(new JScrollPane(booksTable), BorderLayout.CENTER);     // Adds scrolling to the contained component.

        // ===== MY RESERVATIONS TABLE =====
        reservationsModel = new DefaultTableModel() {                         // Begins this block of code.
            @Override                                                         // Continues the expression or declaration for this operation.
            public boolean isCellEditable(int row, int column) {              // Defines the is cell editable method.
                return false;                                                 // Returns a value to the method caller.
            }                                                                 // Ends the current block of code.
        };                                                                    // Executes this statement.

        reservationsModel.setColumnIdentifiers(new Object[]{                  // Defines the column headings for the table model.
            "reservation_id", "Book Title", "Date", "Status", "Admin Note"    // Continues the expression or declaration for this operation.
        });                                                                   // Executes this statement.

        reservationsTable = new JTable(reservationsModel);                    // Creates a table for displaying records.
        reservationsTable.setRowHeight(24);                                   // Sets the height of each table row.
        reservationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);          // Controls how the table columns adjust to the window width.

        // HIDE reservation_id (COLUMN 0) - INTERNAL USE ONLY
        reservationsTable.removeColumn(                                       // Hides an internal ID column from the visible table.
                reservationsTable.getColumnModel().getColumn(0)               // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        JButton cancelButton = new JButton("Cancel Reservation");             // Creates or assigns the cancel button GUI component.
        JButton borrowButton = new JButton("Borrow Book (Time In)");          // Creates or assigns the borrow button GUI component.
        JButton returnButton = new JButton("Return Book (Time Out)");         // Creates or assigns the return button GUI component.
        JButton reservationsRefreshButton = new JButton("Refresh");           // Creates or assigns the reservations refresh button GUI component.

        JPanel reservationsButtonPanel = new JPanel(                          // Creates or assigns the reservations button panel GUI component.
                new FlowLayout(FlowLayout.LEFT, 8, 8)                         // Arranges components in a row, wrapping when needed.
        );                                                                    // Executes this statement.

        reservationsButtonPanel.add(borrowButton);                            // Adds a component to its parent panel or container.
        reservationsButtonPanel.add(returnButton);                            // Adds a component to its parent panel or container.
        reservationsButtonPanel.add(cancelButton);                            // Adds a component to its parent panel or container.
        reservationsButtonPanel.add(reservationsRefreshButton);               // Adds a component to its parent panel or container.

        JLabel reservationsHint = new JLabel(                                 // Creates or assigns the reservations hint GUI component.
                "<html>Pending = waiting for admin approval &nbsp;·&nbsp; "   // Continues the expression or declaration for this operation.
                + "Approved = get the book at the library, then click Borrow"  // Continues the expression or declaration for this operation.
                + "<br>Borrowed = in use &nbsp;·&nbsp; "                      // Continues the expression or declaration for this operation.
                + "Completed = returned</html>"                               // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        reservationsHint.setBorder(                                           // Adds a border or padding around the component.
                BorderFactory.createEmptyBorder(5, 5, 5, 5)                   // Creates spacing around the component.
        );                                                                    // Executes this statement.

        JPanel reservationsPanel = new JPanel(new BorderLayout(5, 5));        // Creates or assigns the reservations panel GUI component.

        reservationsPanel.setBorder(                                          // Adds a border or padding around the component.
                BorderFactory.createEmptyBorder(5, 10, 5, 10)                 // Creates spacing around the component.
        );                                                                    // Executes this statement.
        reservationsPanel.add(                                                // Adds a component to its parent panel or container.
                reservationsButtonPanel, BorderLayout.NORTH                   // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.
        reservationsPanel.add(                                                // Adds a component to its parent panel or container.
                new JScrollPane(reservationsTable), BorderLayout.CENTER       // Adds scrolling to the contained component.
        );                                                                    // Executes this statement.
        reservationsPanel.add(reservationsHint, BorderLayout.SOUTH);          // Adds a component to its parent panel or container.

        // ===== USAGE TABLE =====
        model = new DefaultTableModel() {                                     // Begins this block of code.
            @Override                                                         // Continues the expression or declaration for this operation.
            public boolean isCellEditable(int row, int column) {              // Defines the is cell editable method.
                return false;                                                 // Returns a value to the method caller.
            }                                                                 // Ends the current block of code.
        };                                                                    // Executes this statement.

        model.setColumnIdentifiers(new Object[]{                              // Defines the column headings for the table model.
            "Book Title", "Date", "Time In", "Time Out", "Status"             // Continues the expression or declaration for this operation.
        });                                                                   // Executes this statement.

        JTable table = new JTable(model);                                     // Creates or assigns the table GUI component.
        table.setRowHeight(24);                                               // Sets the height of each table row.

        JPanel usagePanel = new JPanel(new BorderLayout(5, 5));               // Creates or assigns the usage panel GUI component.
        usagePanel.setBorder(                                                 // Adds a border or padding around the component.
                BorderFactory.createEmptyBorder(5, 10, 5, 10)                 // Creates spacing around the component.
        );                                                                    // Executes this statement.
        usagePanel.add(new JScrollPane(table), BorderLayout.CENTER);          // Adds scrolling to the contained component.

        // ===== TABS =====
        JTabbedPane tabs = new JTabbedPane();                                 // Creates or assigns the tabs GUI component.

        tabs.addTab("Available Books", booksPanel);                           // Adds a tab to the tabbed pane.
        tabs.addTab("My Reservations", reservationsPanel);                    // Adds a tab to the tabbed pane.
        tabs.addTab("My Usage History", usagePanel);                          // Adds a tab to the tabbed pane.

        // REFRESH RESERVATIONS WHEN THE TAB IS OPENED
        tabs.addChangeListener(e -> {                                         // Runs code when the selected tab changes.

            if (tabs.getSelectedIndex() == 1) {                               // Checks a condition before continuing.
                loadMyReservations();                                         // Executes this statement.
            }                                                                 // Ends the current block of code.
        });                                                                   // Executes this statement.

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));              // Creates or assigns the header panel GUI component.

        headerPanel.add(welcomeLabel, BorderLayout.NORTH);                    // Adds a component to its parent panel or container.
        headerPanel.add(infoPanel, BorderLayout.CENTER);                      // Adds a component to its parent panel or container.

        // BUTTONS
        JPanel buttonPanel = new JPanel(                                      // Creates or assigns the button panel GUI component.
                new FlowLayout(FlowLayout.CENTER, 10, 10)                     // Arranges components in a row, wrapping when needed.
        );                                                                    // Executes this statement.

        JButton refreshButton = new JButton("Refresh");                       // Creates or assigns the refresh button GUI component.
        JButton logoutButton = new JButton("Logout");                         // Creates or assigns the logout button GUI component.

        buttonPanel.add(refreshButton);                                       // Adds a component to its parent panel or container.
        buttonPanel.add(logoutButton);                                        // Adds a component to its parent panel or container.

        // LAYOUT
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));              // Creates or assigns the main panel GUI component.
        mainPanel.add(headerPanel, BorderLayout.NORTH);                       // Adds a component to its parent panel or container.
        mainPanel.add(tabs, BorderLayout.CENTER);                             // Adds a component to its parent panel or container.
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);                       // Adds a component to its parent panel or container.

        add(mainPanel);                                                       // Executes this statement.

        // ACTIONS
        searchButton.addActionListener(e ->                                   // Connects this button or control to the code that runs when clicked.
                loadBooks(bookSearchField.getText().trim())                   // Removes extra spaces from the beginning and end of the input.
        );                                                                    // Executes this statement.

        showAllButton.addActionListener(e -> {                                // Connects this button or control to the code that runs when clicked.

            bookSearchField.setText("");                                      // Executes this statement.
            loadBooks("");                                                    // Executes this statement.
        });                                                                   // Executes this statement.

        reserveButton.addActionListener(e -> reserveBook());                  // Connects this button or control to the code that runs when clicked.
        borrowButton.addActionListener(e -> borrowBook());                    // Connects this button or control to the code that runs when clicked.
        returnButton.addActionListener(e -> returnBook());                    // Connects this button or control to the code that runs when clicked.
        cancelButton.addActionListener(e -> cancelReservation());             // Connects this button or control to the code that runs when clicked.

        reservationsRefreshButton.addActionListener(e ->                      // Connects this button or control to the code that runs when clicked.
                loadMyReservations()                                          // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        refreshButton.addActionListener(e -> {                                // Connects this button or control to the code that runs when clicked.

            loadBooks();                                                      // Executes this statement.
            loadMyReservations();                                             // Executes this statement.
            loadUsage();                                                      // Executes this statement.
        });                                                                   // Executes this statement.

        logoutButton.addActionListener(e -> {                                 // Connects this button or control to the code that runs when clicked.

            memberId = -1;                                                    // Executes this statement.
            fullName = "";                                                    // Executes this statement.

            showLogin("");                                                    // Executes this statement.
        });                                                                   // Executes this statement.

        loadBooks();                                                          // Executes this statement.
        loadMyReservations();                                                 // Executes this statement.
        loadUsage();                                                          // Executes this statement.

        revalidate();                                                         // Executes this statement.
        repaint();                                                            // Executes this statement.
        setVisible(true);                                                     // Executes this statement.
    }                                                                         // Ends the current block of code.

    // ===== 4. DASHBOARD DISPLAY HELPERS =====
    // Purpose: create one student-information card for the dashboard header.
    public JPanel createInfo(String label, String value) {                    // Creates a small information card for the dashboard.

        JPanel box = new JPanel(new GridLayout(2, 1));                        // Creates or assigns the box GUI component.

        box.setBorder(                                                        // Adds a border or padding around the component.
                BorderFactory.createLineBorder(Color.GRAY)                    // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        JLabel titleLabel = new JLabel(                                       // Creates or assigns the title label GUI component.
                label, SwingConstants.CENTER                                  // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));                 // Executes this statement.

        JLabel valueLabel = new JLabel(                                       // Creates or assigns the value label GUI component.
                value == null ? "-" : value,                                  // Continues the expression or declaration for this operation.
                SwingConstants.CENTER                                         // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        box.add(titleLabel);                                                  // Adds a component to its parent panel or container.
        box.add(valueLabel);                                                  // Adds a component to its parent panel or container.

        return box;                                                           // Returns a value to the method caller.
    }                                                                         // Ends the current block of code.

    // ===== 5. AVAILABLE BOOKS: LOAD AND SEARCH =====
    // Purpose: load books using the current search text, or all books.
    public void loadBooks() {                                                 // Loads available books and applies an optional search filter.

        loadBooks(bookSearchField == null                                     // Continues the expression or declaration for this operation.
                ? ""                                                          // Continues the expression or declaration for this operation.
                : bookSearchField.getText().trim());                          // Removes extra spaces from the beginning and end of the input.
    }                                                                         // Ends the current block of code.

    // Purpose: get available book details from MySQL and optionally filter them.
    public void loadBooks(String keyword) {                                   // Loads available books and applies an optional search filter.

        if (booksModel == null) {                                             // Checks whether required data is missing or an item is not selected.
            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        boolean hasKeyword = keyword != null && !keyword.isEmpty();           // Stores whether has keyword is true or false.

        String sql = "SELECT book_id, title, author, category, publisher, "   // Stores the SQL query used for this database operation.
                + "publication_year, quantity, available_copies "             // Continues the expression or declaration for this operation.
                + "FROM books ";                                              // Executes this statement.

        if (hasKeyword) {                                                     // Checks a condition before continuing.
            sql = sql + "WHERE title LIKE ? OR author LIKE ? "                // Continues the expression or declaration for this operation.
                    + "OR category LIKE ? OR publisher LIKE ? "               // Continues the expression or declaration for this operation.
                    + "OR isbn LIKE ? ";                                      // Executes this statement.
        }                                                                     // Ends the current block of code.

        sql = sql + "ORDER BY title";                                         // Executes this statement.

        booksModel.setRowCount(0);                                            // Clears existing table rows before loading fresh results.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            try (PreparedStatement ps = con.prepareStatement(sql)) {          // Creates a prepared SQL statement and closes it automatically after use.

                if (hasKeyword) {                                             // Checks a condition before continuing.

                    String like = "%" + keyword + "%";                        // Stores the like text value.

                    for (int i = 1; i <= 5; i++) {                            // Begins this block of code.
                        ps.setString(i, like);                                // Sets a text parameter in the prepared SQL query.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                try (ResultSet result = ps.executeQuery()) {                  // Reads query results and closes the result set automatically.

                    while (result.next()) {                                   // Repeats the block for each row returned by the query.

                        booksModel.addRow(new Object[]{                       // Adds a record to the table model.
                            result.getInt("book_id"),                         // Reads an integer value from a query result.
                            result.getString("title"),                        // Reads a text value from a query result.
                            result.getString("author"),                       // Reads a text value from a query result.
                            result.getString("category"),                     // Reads a text value from a query result.
                            result.getString("publisher"),                    // Reads a text value from a query result.
                            yearText(result),                                 // Continues the expression or declaration for this operation.
                            result.getInt("quantity"),                        // Reads an integer value from a query result.
                            result.getInt("available_copies"),                // Reads an integer value from a query result.
                            result.getInt("quantity")                         // Reads an integer value from a query result.
                                    - result.getInt("available_copies")       // Reads an integer value from a query result.
                        });                                                   // Executes this statement.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.
            }                                                                 // Ends the current block of code.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Could not load books.\n" + e.getMessage()                // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // ===== 6. BOOK RESERVATION WORKFLOW =====
    // Purpose: convert selected JTable row to its correct data-model row.
    private int selectedModelRow(JTable table) {                              // Converts the selected table row to its underlying model row.

        int viewRow = table.getSelectedRow();                                 // Stores the numeric view row value.

        if (viewRow < 0) {                                                    // Checks a condition before continuing.
            return -1;                                                        // Returns a value to the method caller.
        }                                                                     // Ends the current block of code.

        return table.convertRowIndexToModel(viewRow);                         // Returns a value to the method caller.
    }                                                                         // Ends the current block of code.

    // Purpose: let the student request a book and save a Pending reservation.
    public void reserveBook() {                                               // Creates a reservation for the selected available book.

        int row = selectedModelRow(booksTable);                               // Stores the numeric row value.

        if (row < 0) {                                                        // Checks a condition before continuing.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Please select a book first."                       // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        int bookId = Integer.parseInt(                                        // Stores the numeric book id value.
                String.valueOf(booksModel.getValueAt(row, 0))                 // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        String title = String.valueOf(                                        // Stores the title text value.
                booksModel.getValueAt(row, 1)                                 // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        int available = Integer.parseInt(                                     // Stores the numeric available value.
                String.valueOf(booksModel.getValueAt(row, 7))                 // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        if (available < 1) {                                                  // Checks a condition before continuing.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Sorry, wala na sang available copies ang \""             // Continues the expression or declaration for this operation.
                    + title + "\"."                                           // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        String checkSql = "SELECT COUNT(*) FROM reservations "                // Stores the SQL query used for this database operation.
                + "WHERE member_id = ? AND book_id = ? "                      // Continues the expression or declaration for this operation.
                + "AND status IN ('Pending','Approved','Borrowed')";          // Executes this statement.

        String insertSql = "INSERT INTO reservations "                        // Stores the SQL query used for this database operation.
                + "(book_id, member_id, reservation_date, status) "           // Continues the expression or declaration for this operation.
                + "VALUES (?, ?, ?, 'Pending')";                              // Executes this statement.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            // RE-CHECK AVAILABILITY IN THE DATABASE (THE TABLE MAY BE STALE)
            try (PreparedStatement ps = con.prepareStatement(                 // Creates a prepared SQL statement and closes it automatically after use.
                    "SELECT available_copies FROM books "                     // Continues the expression or declaration for this operation.
                    + "WHERE book_id = ?")) {                                 // Begins this block of code.

                ps.setInt(1, bookId);                                         // Sets a numeric parameter in the prepared SQL query.

                try (ResultSet rs = ps.executeQuery()) {                      // Reads query results and closes the result set automatically.

                    if (!rs.next()                                            // Checks a condition before continuing.
                            || rs.getInt("available_copies") < 1) {           // Reads an integer value from a query result.

                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this,                                         // Continues the expression or declaration for this operation.
                                "Sorry, wala na sang available copies ang \""  // Continues the expression or declaration for this operation.
                                + title + "\"."                               // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.

                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.
            }                                                                 // Ends the current block of code.

            // DUPLICATE GUARD
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {     // Creates a prepared SQL statement and closes it automatically after use.

                ps.setInt(1, memberId);                                       // Sets a numeric parameter in the prepared SQL query.
                ps.setInt(2, bookId);                                         // Sets a numeric parameter in the prepared SQL query.

                try (ResultSet rs = ps.executeQuery()) {                      // Reads query results and closes the result set automatically.

                    if (rs.next() && rs.getInt(1) > 0) {                      // Checks a condition before continuing.

                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this,                                         // Continues the expression or declaration for this operation.
                                "May reservation ka na para sa book na ito."  // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.

                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.
            }                                                                 // Ends the current block of code.

            try (PreparedStatement ps = con.prepareStatement(insertSql)) {    // Creates a prepared SQL statement and closes it automatically after use.

                ps.setInt(1, bookId);                                         // Sets a numeric parameter in the prepared SQL query.
                ps.setInt(2, memberId);                                       // Sets a numeric parameter in the prepared SQL query.
                ps.setDate(3, Date.valueOf(LocalDate.now()));                 // Sets a date parameter in the prepared SQL query.

                ps.executeUpdate();                                           // Runs an INSERT, UPDATE, or DELETE query.
            }                                                                 // Ends the current block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Reservation sent! Wait for the admin to approve it.\n"   // Continues the expression or declaration for this operation.
                    + "Once approved, get the book at the library and "       // Continues the expression or declaration for this operation.
                    + "click \"Borrow Book (Time In)\"."                      // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            loadMyReservations();                                             // Executes this statement.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Could not reserve the book.\n" + e.getMessage()          // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // ===== 7. BOOK USE: TIME IN AND TIME OUT =====
    // Purpose: create a usage record for an approved reservation.
    public void borrowBook() {                                                // Starts an approved reservation and records the book usage time-in.

        int row = selectedModelRow(reservationsTable);                        // Stores the numeric row value.

        if (row < 0) {                                                        // Checks a condition before continuing.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Please select a reservation first."                // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        int reservationId = Integer.parseInt(                                 // Stores the numeric reservation id value.
                String.valueOf(reservationsModel.getValueAt(row, 0))          // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        String shownStatus = String.valueOf(                                  // Stores the shown status text value.
                reservationsModel.getValueAt(row, 3)                          // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        if (!"Approved".equals(shownStatus)) {                                // Checks whether two values match the required state.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Only approved reservations can be borrowed.\n"           // Continues the expression or declaration for this operation.
                    + "This one is " + shownStatus + "."                      // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        int answer = JOptionPane.showConfirmDialog(                           // Stores the numeric answer value.
                this,                                                         // Continues the expression or declaration for this operation.
                "Nakuha mo na ang book sa library?\n\n"                       // Continues the expression or declaration for this operation.
                + "Click Yes to start using it (Time In).",                   // Continues the expression or declaration for this operation.
                "Borrow Book",                                                // Continues the expression or declaration for this operation.
                JOptionPane.YES_NO_OPTION                                     // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        if (answer != JOptionPane.YES_OPTION) {                               // Checks a condition before continuing.
            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        String selectSql = "SELECT book_id, status FROM reservations "        // Stores the SQL query used for this database operation.
                + "WHERE reservation_id = ? AND member_id = ? FOR UPDATE";    // Executes this statement.

        String updateSql = "UPDATE reservations SET status = 'Borrowed' "     // Stores the SQL query used for this database operation.
                + "WHERE reservation_id = ? AND status = 'Approved'";         // Executes this statement.

        String usageSql = "INSERT INTO book_usage "                           // Stores the SQL query used for this database operation.
                + "(book_id, member_id, reservation_id, usage_date, "         // Continues the expression or declaration for this operation.
                + "time_in, status) "                                         // Continues the expression or declaration for this operation.
                + "VALUES (?, ?, ?, CURDATE(), CURTIME(), 'Using')";          // Executes this statement.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            con.setAutoCommit(false);                                         // Begins a transaction so related changes succeed or fail together.

            try {                                                             // Starts a protected operation that may throw an exception.

                int bookId;                                                   // Stores the numeric book id value.
                String status;                                                // Stores the status text value.

                try (PreparedStatement ps =                                   // Creates a prepared SQL statement and closes it automatically after use.
                             con.prepareStatement(selectSql)) {               // Prepares a parameterized SQL statement.

                    ps.setInt(1, reservationId);                              // Sets a numeric parameter in the prepared SQL query.
                    ps.setInt(2, memberId);                                   // Sets a numeric parameter in the prepared SQL query.

                    try (ResultSet rs = ps.executeQuery()) {                  // Reads query results and closes the result set automatically.

                        if (!rs.next()) {                                     // Checks a condition before continuing.

                            con.rollback();                                   // Cancels uncommitted database changes after an error or failed check.

                            JOptionPane.showMessageDialog(                    // Displays a message or error to the user.
                                    this, "Reservation not found."            // Continues the expression or declaration for this operation.
                            );                                                // Executes this statement.

                            return;                                           // Stops this method because there is nothing else to do.
                        }                                                     // Ends the current block of code.

                        bookId = rs.getInt("book_id");                        // Reads an integer value from a query result.
                        status = rs.getString("status");                      // Reads a text value from a query result.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                if (!"Approved".equals(status)) {                             // Checks whether two values match the required state.

                    con.rollback();                                           // Cancels uncommitted database changes after an error or failed check.

                    JOptionPane.showMessageDialog(                            // Displays a message or error to the user.
                            this,                                             // Continues the expression or declaration for this operation.
                            "This reservation is already " + status + "."     // Continues the expression or declaration for this operation.
                    );                                                        // Executes this statement.

                    return;                                                   // Stops this method because there is nothing else to do.
                }                                                             // Ends the current block of code.

                try (PreparedStatement ps =                                   // Creates a prepared SQL statement and closes it automatically after use.
                             con.prepareStatement(updateSql)) {               // Prepares a parameterized SQL statement.

                    ps.setInt(1, reservationId);                              // Sets a numeric parameter in the prepared SQL query.

                    if (ps.executeUpdate() == 0) {                            // Checks a condition before continuing.

                        con.rollback();                                       // Cancels uncommitted database changes after an error or failed check.

                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this,                                         // Continues the expression or declaration for this operation.
                                "Could not start the book usage. "            // Continues the expression or declaration for this operation.
                                + "Please refresh and try again."             // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.

                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                try (PreparedStatement ps =                                   // Creates a prepared SQL statement and closes it automatically after use.
                             con.prepareStatement(usageSql)) {                // Prepares a parameterized SQL statement.

                    ps.setInt(1, bookId);                                     // Sets a numeric parameter in the prepared SQL query.
                    ps.setInt(2, memberId);                                   // Sets a numeric parameter in the prepared SQL query.
                    ps.setInt(3, reservationId);                              // Sets a numeric parameter in the prepared SQL query.

                    ps.executeUpdate();                                       // Runs an INSERT, UPDATE, or DELETE query.
                }                                                             // Ends the current block of code.

                con.commit();                                                 // Saves all changes in the current database transaction.

                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this,                                                 // Continues the expression or declaration for this operation.
                        "Book borrowed! Ingat sa libro.\n"                    // Continues the expression or declaration for this operation.
                        + "Click \"Return Book (Time Out)\" when you "        // Continues the expression or declaration for this operation.
                        + "are done."                                         // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.

                loadMyReservations();                                         // Executes this statement.
                loadUsage();                                                  // Executes this statement.

            } catch (SQLException e) {                                        // Begins this block of code.

                con.rollback();                                               // Cancels uncommitted database changes after an error or failed check.

                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this,                                                 // Continues the expression or declaration for this operation.
                        "Could not borrow the book.\n" + e.getMessage()       // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.

            } finally {                                                       // Begins this block of code.
                con.setAutoCommit(true);                                      // Restores automatic database transaction commits.
            }                                                                 // Ends the current block of code.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Database error.\n" + e.getMessage()                // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // Purpose: close the usage record, complete the reservation, and restore availability.
    public void returnBook() {                                                // Records time-out and completes an active reservation.

        int row = selectedModelRow(reservationsTable);                        // Stores the numeric row value.

        if (row < 0) {                                                        // Checks a condition before continuing.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Please select a reservation first."                // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        int reservationId = Integer.parseInt(                                 // Stores the numeric reservation id value.
                String.valueOf(reservationsModel.getValueAt(row, 0))          // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        String shownStatus = String.valueOf(                                  // Stores the shown status text value.
                reservationsModel.getValueAt(row, 3)                          // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        if (!"Borrowed".equals(shownStatus)) {                                // Checks whether two values match the required state.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Only borrowed books can be returned.\n"                  // Continues the expression or declaration for this operation.
                    + "This one is " + shownStatus + "."                      // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        int answer = JOptionPane.showConfirmDialog(                           // Stores the numeric answer value.
                this,                                                         // Continues the expression or declaration for this operation.
                "Ibalik mo na ang book sa library?",                          // Continues the expression or declaration for this operation.
                "Return Book",                                                // Continues the expression or declaration for this operation.
                JOptionPane.YES_NO_OPTION                                     // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        if (answer != JOptionPane.YES_OPTION) {                               // Checks a condition before continuing.
            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        String selectSql = "SELECT book_id, status FROM reservations "        // Stores the SQL query used for this database operation.
                + "WHERE reservation_id = ? AND member_id = ? FOR UPDATE";    // Executes this statement.

        String usageSql = "UPDATE book_usage "                                // Stores the SQL query used for this database operation.
                + "SET time_out = CURTIME(), status = 'Returned' "            // Continues the expression or declaration for this operation.
                + "WHERE reservation_id = ? AND member_id = ? "               // Continues the expression or declaration for this operation.
                + "AND status = 'Using'";                                     // Executes this statement.

        String updateSql = "UPDATE reservations SET status = 'Completed' "    // Stores the SQL query used for this database operation.
                + "WHERE reservation_id = ? AND status = 'Borrowed'";         // Executes this statement.

        String bookSql = "UPDATE books SET available_copies = "               // Stores the SQL query used for this database operation.
                + "LEAST(quantity, available_copies + 1) "                    // Continues the expression or declaration for this operation.
                + "WHERE book_id = ?";                                        // Executes this statement.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            con.setAutoCommit(false);                                         // Begins a transaction so related changes succeed or fail together.

            try {                                                             // Starts a protected operation that may throw an exception.

                int bookId;                                                   // Stores the numeric book id value.
                String status;                                                // Stores the status text value.

                try (PreparedStatement ps =                                   // Creates a prepared SQL statement and closes it automatically after use.
                             con.prepareStatement(selectSql)) {               // Prepares a parameterized SQL statement.

                    ps.setInt(1, reservationId);                              // Sets a numeric parameter in the prepared SQL query.
                    ps.setInt(2, memberId);                                   // Sets a numeric parameter in the prepared SQL query.

                    try (ResultSet rs = ps.executeQuery()) {                  // Reads query results and closes the result set automatically.

                        if (!rs.next()) {                                     // Checks a condition before continuing.

                            con.rollback();                                   // Cancels uncommitted database changes after an error or failed check.

                            JOptionPane.showMessageDialog(                    // Displays a message or error to the user.
                                    this, "Reservation not found."            // Continues the expression or declaration for this operation.
                            );                                                // Executes this statement.

                            return;                                           // Stops this method because there is nothing else to do.
                        }                                                     // Ends the current block of code.

                        bookId = rs.getInt("book_id");                        // Reads an integer value from a query result.
                        status = rs.getString("status");                      // Reads a text value from a query result.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                if (!"Borrowed".equals(status)) {                             // Checks whether two values match the required state.

                    con.rollback();                                           // Cancels uncommitted database changes after an error or failed check.

                    JOptionPane.showMessageDialog(                            // Displays a message or error to the user.
                            this,                                             // Continues the expression or declaration for this operation.
                            "This reservation is already " + status + "."     // Continues the expression or declaration for this operation.
                    );                                                        // Executes this statement.

                    return;                                                   // Stops this method because there is nothing else to do.
                }                                                             // Ends the current block of code.

                try (PreparedStatement ps =                                   // Creates a prepared SQL statement and closes it automatically after use.
                             con.prepareStatement(usageSql)) {                // Prepares a parameterized SQL statement.

                    ps.setInt(1, reservationId);                              // Sets a numeric parameter in the prepared SQL query.
                    ps.setInt(2, memberId);                                   // Sets a numeric parameter in the prepared SQL query.

                    if (ps.executeUpdate() == 0) {                            // Checks a condition before continuing.

                        con.rollback();                                       // Cancels uncommitted database changes after an error or failed check.

                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this,                                         // Continues the expression or declaration for this operation.
                                "No active book usage found. "                // Continues the expression or declaration for this operation.
                                + "Please ask the admin for help."            // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.

                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                try (PreparedStatement ps =                                   // Creates a prepared SQL statement and closes it automatically after use.
                             con.prepareStatement(updateSql)) {               // Prepares a parameterized SQL statement.

                    ps.setInt(1, reservationId);                              // Sets a numeric parameter in the prepared SQL query.
                    ps.executeUpdate();                                       // Runs an INSERT, UPDATE, or DELETE query.
                }                                                             // Ends the current block of code.

                try (PreparedStatement ps =                                   // Creates a prepared SQL statement and closes it automatically after use.
                             con.prepareStatement(bookSql)) {                 // Prepares a parameterized SQL statement.

                    ps.setInt(1, bookId);                                     // Sets a numeric parameter in the prepared SQL query.
                    ps.executeUpdate();                                       // Runs an INSERT, UPDATE, or DELETE query.
                }                                                             // Ends the current block of code.

                con.commit();                                                 // Saves all changes in the current database transaction.

                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this,                                                 // Continues the expression or declaration for this operation.
                        "Book returned. Salamat!\n"                           // Continues the expression or declaration for this operation.
                        + "The copy is available again."                      // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.

                loadMyReservations();                                         // Executes this statement.
                loadBooks();                                                  // Executes this statement.
                loadUsage();                                                  // Executes this statement.

            } catch (SQLException e) {                                        // Begins this block of code.

                con.rollback();                                               // Cancels uncommitted database changes after an error or failed check.

                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this,                                                 // Continues the expression or declaration for this operation.
                        "Could not return the book.\n" + e.getMessage()       // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.

            } finally {                                                       // Begins this block of code.
                con.setAutoCommit(true);                                      // Restores automatic database transaction commits.
            }                                                                 // Ends the current block of code.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Database error.\n" + e.getMessage()                // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // ===== 8. MY RESERVATIONS AND CANCELLATION =====
    // Purpose: display only reservations belonging to the logged-in student.
    public void loadMyReservations() {                                        // Loads only the logged-in student’s reservations.

        if (reservationsModel == null || memberId == -1) {                    // Checks whether required data is missing or an item is not selected.
            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        String sql = "SELECT r.reservation_id, b.title, "                     // Stores the SQL query used for this database operation.
                + "r.reservation_date, r.status, r.admin_note "               // Continues the expression or declaration for this operation.
                + "FROM reservations r "                                      // Continues the expression or declaration for this operation.
                + "JOIN books b ON r.book_id = b.book_id "                    // Continues the expression or declaration for this operation.
                + "WHERE r.member_id = ? "                                    // Continues the expression or declaration for this operation.
                + "ORDER BY r.created_at DESC";                               // Executes this statement.

        reservationsModel.setRowCount(0);                                     // Clears existing table rows before loading fresh results.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            try (PreparedStatement ps = con.prepareStatement(sql)) {          // Creates a prepared SQL statement and closes it automatically after use.

                ps.setInt(1, memberId);                                       // Sets a numeric parameter in the prepared SQL query.

                try (ResultSet result = ps.executeQuery()) {                  // Reads query results and closes the result set automatically.

                    while (result.next()) {                                   // Repeats the block for each row returned by the query.

                        reservationsModel.addRow(new Object[]{                // Adds a record to the table model.
                            result.getInt("reservation_id"),                  // Reads an integer value from a query result.
                            result.getString("title"),                        // Reads a text value from a query result.
                            result.getDate("reservation_date"),               // Reads a date value from a query result.
                            result.getString("status"),                       // Reads a text value from a query result.
                            result.getString("admin_note")                    // Reads a text value from a query result.
                        });                                                   // Executes this statement.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.
            }                                                                 // Ends the current block of code.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Could not load your reservations.\n" + e.getMessage()    // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // Purpose: cancel a reservation only while Pending or Approved.
    public void cancelReservation() {                                         // Cancels an eligible reservation and restores a held copy if needed.

        int row = selectedModelRow(reservationsTable);                        // Stores the numeric row value.

        if (row < 0) {                                                        // Checks a condition before continuing.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Please select a reservation first."                // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        int reservationId = Integer.parseInt(                                 // Stores the numeric reservation id value.
                String.valueOf(reservationsModel.getValueAt(row, 0))          // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        String shownStatus = String.valueOf(                                  // Stores the shown status text value.
                reservationsModel.getValueAt(row, 3)                          // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        if ("Declined".equals(shownStatus)                                    // Checks whether two values match the required state.
                || "Cancelled".equals(shownStatus)) {                         // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "This reservation is already " + shownStatus + "."        // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        int answer = JOptionPane.showConfirmDialog(                           // Stores the numeric answer value.
                this,                                                         // Continues the expression or declaration for this operation.
                "Cancel this reservation?",                                   // Continues the expression or declaration for this operation.
                "Confirm",                                                    // Continues the expression or declaration for this operation.
                JOptionPane.YES_NO_OPTION                                     // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        if (answer != JOptionPane.YES_OPTION) {                               // Checks a condition before continuing.
            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        String selectSql = "SELECT book_id, status FROM reservations "        // Stores the SQL query used for this database operation.
                + "WHERE reservation_id = ? AND member_id = ? FOR UPDATE";    // Executes this statement.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            con.setAutoCommit(false);                                         // Begins a transaction so related changes succeed or fail together.

            try {                                                             // Starts a protected operation that may throw an exception.

                int bookId;                                                   // Stores the numeric book id value.
                String status;                                                // Stores the status text value.

                try (PreparedStatement ps =                                   // Creates a prepared SQL statement and closes it automatically after use.
                             con.prepareStatement(selectSql)) {               // Prepares a parameterized SQL statement.

                    ps.setInt(1, reservationId);                              // Sets a numeric parameter in the prepared SQL query.
                    ps.setInt(2, memberId);                                   // Sets a numeric parameter in the prepared SQL query.

                    try (ResultSet rs = ps.executeQuery()) {                  // Reads query results and closes the result set automatically.

                        if (!rs.next()) {                                     // Checks a condition before continuing.

                            con.rollback();                                   // Cancels uncommitted database changes after an error or failed check.

                            JOptionPane.showMessageDialog(                    // Displays a message or error to the user.
                                    this, "Reservation not found."            // Continues the expression or declaration for this operation.
                            );                                                // Executes this statement.

                            return;                                           // Stops this method because there is nothing else to do.
                        }                                                     // Ends the current block of code.

                        bookId = rs.getInt("book_id");                        // Reads an integer value from a query result.
                        status = rs.getString("status");                      // Reads a text value from a query result.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                if (!"Pending".equals(status)                                 // Checks whether two values match the required state.
                        && !"Approved".equals(status)) {                      // Begins this block of code.

                    con.rollback();                                           // Cancels uncommitted database changes after an error or failed check.

                    JOptionPane.showMessageDialog(                            // Displays a message or error to the user.
                            this,                                             // Continues the expression or declaration for this operation.
                            "This reservation is already " + status + "."     // Continues the expression or declaration for this operation.
                    );                                                        // Executes this statement.

                    return;                                                   // Stops this method because there is nothing else to do.
                }                                                             // Ends the current block of code.

                try (PreparedStatement ps = con.prepareStatement(             // Creates a prepared SQL statement and closes it automatically after use.
                        "UPDATE reservations SET status = 'Cancelled' "       // Continues the expression or declaration for this operation.
                        + "WHERE reservation_id = ? "                         // Continues the expression or declaration for this operation.
                        + "AND status IN ('Pending','Approved')")) {          // Begins this block of code.

                    ps.setInt(1, reservationId);                              // Sets a numeric parameter in the prepared SQL query.

                    if (ps.executeUpdate() == 0) {                            // Checks a condition before continuing.

                        con.rollback();                                       // Cancels uncommitted database changes after an error or failed check.

                        JOptionPane.showMessageDialog(                        // Displays a message or error to the user.
                                this,                                         // Continues the expression or declaration for this operation.
                                "Could not cancel this reservation. "         // Continues the expression or declaration for this operation.
                                + "Please refresh and try again."             // Continues the expression or declaration for this operation.
                        );                                                    // Executes this statement.

                        return;                                               // Stops this method because there is nothing else to do.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                // GIVE BACK THE HELD COPY
                if ("Approved".equals(status)) {                              // Checks whether two values match the required state.

                    try (PreparedStatement ps = con.prepareStatement(         // Creates a prepared SQL statement and closes it automatically after use.
                            "UPDATE books SET available_copies = "            // Continues the expression or declaration for this operation.
                            + "LEAST(quantity, available_copies + 1) "        // Continues the expression or declaration for this operation.
                            + "WHERE book_id = ?")) {                         // Begins this block of code.

                        ps.setInt(1, bookId);                                 // Sets a numeric parameter in the prepared SQL query.
                        ps.executeUpdate();                                   // Runs an INSERT, UPDATE, or DELETE query.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.

                con.commit();                                                 // Saves all changes in the current database transaction.

                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Reservation cancelled."                        // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.

                loadMyReservations();                                         // Executes this statement.
                loadBooks();                                                  // Executes this statement.

            } catch (SQLException e) {                                        // Begins this block of code.

                con.rollback();                                               // Cancels uncommitted database changes after an error or failed check.

                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this,                                                 // Continues the expression or declaration for this operation.
                        "Could not cancel the reservation.\n"                 // Continues the expression or declaration for this operation.
                        + e.getMessage()                                      // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.

            } finally {                                                       // Begins this block of code.
                con.setAutoCommit(true);                                      // Restores automatic database transaction commits.
            }                                                                 // Ends the current block of code.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this, "Database error.\n" + e.getMessage()                // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // ===== 9. USAGE HISTORY AND FORM HELPERS =====
    // Purpose: display the year in a short format if MySQL returns a date-like value.
    private String yearText(ResultSet result) throws SQLException {           // Formats the publication year as a short year value.

        String year = result.getString("publication_year");                   // Stores the year text value.

        if (year != null && year.length() > 4) {                              // Checks a condition before continuing.
            year = year.substring(0, 4);                                      // Executes this statement.
        }                                                                     // Ends the current block of code.

        return year;                                                          // Returns a value to the method caller.
    }                                                                         // Ends the current block of code.

    // Purpose: list this student's own usage history.
    public void loadUsage() {                                                 // Loads the logged-in student’s book usage history.

        if (model == null || memberId == -1) {                                // Checks whether required data is missing or an item is not selected.
            return;                                                           // Stops this method because there is nothing else to do.
        }                                                                     // Ends the current block of code.

        String sql = "SELECT b.title AS title, "                              // Stores the SQL query used for this database operation.
                + "u.usage_date AS usage_date, "                              // Continues the expression or declaration for this operation.
                + "u.time_in AS time_in, "                                    // Continues the expression or declaration for this operation.
                + "u.time_out AS time_out, "                                  // Continues the expression or declaration for this operation.
                + "u.status AS status "                                       // Continues the expression or declaration for this operation.
                + "FROM book_usage u "                                        // Continues the expression or declaration for this operation.
                + "JOIN books b ON u.book_id = b.book_id "                    // Continues the expression or declaration for this operation.
                + "WHERE u.member_id = ? "                                    // Continues the expression or declaration for this operation.
                + "ORDER BY u.usage_date DESC, u.time_in DESC";               // Executes this statement.

        model.setRowCount(0);                                                 // Clears existing table rows before loading fresh results.

        try (Connection con = database_connection.connect()) {                // Starts an operation that may fail; database resources are closed automatically.

            if (con == null) {                                                // Checks whether required data is missing or an item is not selected.
                JOptionPane.showMessageDialog(                                // Displays a message or error to the user.
                        this, "Database connection failed!"                   // Continues the expression or declaration for this operation.
                );                                                            // Executes this statement.
                return;                                                       // Stops this method because there is nothing else to do.
            }                                                                 // Ends the current block of code.

            try (PreparedStatement ps = con.prepareStatement(sql)) {          // Creates a prepared SQL statement and closes it automatically after use.

                ps.setInt(1, memberId);                                       // Sets a numeric parameter in the prepared SQL query.

                try (ResultSet result = ps.executeQuery()) {                  // Reads query results and closes the result set automatically.

                    while (result.next()) {                                   // Repeats the block for each row returned by the query.

                        model.addRow(new Object[]{                            // Adds a record to the table model.
                            result.getString("title"),                        // Reads a text value from a query result.
                            result.getDate("usage_date"),                     // Reads a date value from a query result.
                            result.getTime("time_in"),                        // Reads a time value from a query result.
                            result.getTime("time_out"),                       // Reads a time value from a query result.
                            result.getString("status")                        // Reads a text value from a query result.
                        });                                                   // Executes this statement.
                    }                                                         // Ends the current block of code.
                }                                                             // Ends the current block of code.
            }                                                                 // Ends the current block of code.

        } catch (SQLException e) {                                            // Begins this block of code.

            JOptionPane.showMessageDialog(                                    // Displays a message or error to the user.
                    this,                                                     // Continues the expression or declaration for this operation.
                    "Could not load your book usage.\n" + e.getMessage()      // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // Purpose: clear all signup fields after successful registration.
    public void clearSignupFields() {                                         // Clears the student registration inputs.

        signupStudentId.setText("");                                          // Executes this statement.
        signupFullName.setText("");                                           // Executes this statement.
        signupCourse.setText("");                                             // Executes this statement.
        signupYearLevel.setText("");                                          // Executes this statement.
        signupSection.setText("");                                            // Executes this statement.
        signupContact.setText("");                                            // Executes this statement.
        signupAddress.setText("");                                            // Executes this statement.
        signupPassword.setText("");                                           // Executes this statement.
        signupConfirm.setText("");                                            // Executes this statement.
    }                                                                         // Ends the current block of code.

    // ===== 10. PASSWORD SECURITY =====
    // Purpose: hash passwords before storing them in MySQL; never save plain text.
    private String hashPassword(char[] pass) throws Exception {               // Creates a salted PBKDF2 hash for a password.

        int rounds = 120000;                                                  // Stores the numeric rounds value.
        byte[] salt = new byte[16];                                           // Stores binary data in salt.

        new SecureRandom().nextBytes(salt);                                   // Generates cryptographically secure random bytes.

        PBEKeySpec spec = new PBEKeySpec(                                     // Configures the password, salt, iteration count, and derived-key size.
                pass, salt, rounds, 256                                       // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        try {                                                                 // Starts a protected operation that may throw an exception.

            SecretKeyFactory factory = SecretKeyFactory.getInstance(          // Creates the object that derives a password hash.
                    "PBKDF2WithHmacSHA256"                                    // Continues the expression or declaration for this operation.
            );                                                                // Executes this statement.

            byte[] hash = factory.generateSecret(spec).getEncoded();          // Stores binary data in hash.

            return "pbkdf2$" + rounds + "$"                                   // Returns a value to the method caller.
                    + Base64.getEncoder().encodeToString(salt) + "$"          // Encodes bytes as text so they can be stored in the database.
                    + Base64.getEncoder().encodeToString(hash);               // Encodes bytes as text so they can be stored in the database.

        } finally {                                                           // Begins this block of code.
            spec.clearPassword();                                             // Clears the password stored inside the hashing specification.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // Purpose: verify the entered password against its stored PBKDF2 hash.
    private boolean verifyPassword(char[] password, String stored)            // Checks an entered password against its stored PBKDF2 hash.
            throws Exception {                                                // Begins this block of code.

        String[] parts = stored.split("\\$");                                 // Executes this statement.

        if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {                // Checks whether two values match the required state.
            return false;                                                     // Returns a value to the method caller.
        }                                                                     // Ends the current block of code.

        int rounds = Integer.parseInt(parts[1]);                              // Stores the numeric rounds value.
        byte[] salt = Base64.getDecoder().decode(parts[2]);                   // Stores binary data in salt.
        byte[] expected = Base64.getDecoder().decode(parts[3]);               // Stores binary data in expected.

        PBEKeySpec spec = new PBEKeySpec(                                     // Configures the password, salt, iteration count, and derived-key size.
                password, salt, rounds, expected.length * 8                   // Continues the expression or declaration for this operation.
        );                                                                    // Executes this statement.

        try {                                                                 // Starts a protected operation that may throw an exception.

            byte[] actual = SecretKeyFactory                                  // Stores binary data in actual.
                    .getInstance("PBKDF2WithHmacSHA256")                      // Continues the expression or declaration for this operation.
                    .generateSecret(spec)                                     // Derives the password hash using the configured algorithm.
                    .getEncoded();                                            // Executes this statement.

            // constant-time comparison
            return MessageDigest.isEqual(expected, actual);                   // Returns a value to the method caller.

        } finally {                                                           // Begins this block of code.
            spec.clearPassword();                                             // Clears the password stored inside the hashing specification.
        }                                                                     // Ends the current block of code.
    }                                                                         // Ends the current block of code.

    // ===== 11. PROGRAM STARTUP =====
    // Purpose: open the Student Login when this file is run directly.
    public static void main(String[] args) {                                  // Starts the Swing application on the event-dispatch thread.

        SwingUtilities.invokeLater(() -> new members());                      // Starts the GUI on Swing’s event-dispatch thread.
    }                                                                         // Ends the current block of code.
}                                                                             // Ends the current block of code.