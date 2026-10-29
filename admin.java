
import java.awt.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class admin extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;

    // ADMIN REGISTER FIELDS
    JTextField regFullName, regUsername;
    JPasswordField regPassword, regConfirm;

    // LOGGED-IN ADMIN
    String adminName = "";

    // MANAGE MEMBERS FIELDS
    JTextField memberStudentId, memberFullName, memberCourse;
    JTextField memberYearLevel, memberSection, memberContact;
    JTextField memberAddress, memberRegistrationDate;
    JPasswordField memberPassword;
    JComboBox<String> memberStatusBox;

    JTable membersTable;
    DefaultTableModel membersModel;

    // member_id of the selected row (-1 = wala)
    int selectedMemberId = -1;

    // MANAGE BOOKS FIELDS
    JTextField bookIsbn, bookTitle, bookAuthor, bookCategory;
    JTextField bookPublisher, bookYear, bookQuantity, bookAvailable;

    JTable booksTable;
    DefaultTableModel booksModel;

    // book_id of the selected row (-1 = wala)
    int selectedBookId = -1;

    // MANAGE RESERVATIONS FIELDS
    JTable reservationsTable;
    DefaultTableModel reservationsModel;
    String reservationFilter = "Pending";

    // reservation_id of the selected row (-1 = wala)
    int selectedReservationId = -1;

    // BOOK USAGE FIELDS
    JTable usageTable;
    DefaultTableModel usageModel;
    String usageFilter = "Using";

    // usage_id of the selected row (-1 = wala)
    int selectedUsageId = -1;

    public admin() {
        showLogin();
    }

    // ADMIN LOGIN
    public void showLogin() {

        getContentPane().removeAll();

        setTitle("Library Management System - Admin Login");
        setSize(380, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(new JLabel(""));
        panel.add(loginButton);

        panel.add(new JLabel(""));
        panel.add(registerButton);

        add(panel);

        loginButton.addActionListener(e -> loginAdmin());
        registerButton.addActionListener(e -> showAdminRegister());

        getRootPane().setDefaultButton(loginButton);

        revalidate();
        repaint();
        setVisible(true);
    }

    // CHECK ADMIN LOGIN
    public void loginAdmin() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please enter your username and password."
            );

            return;
        }

        String sql = "SELECT full_name, password FROM admins "
                   + "WHERE username = ?";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next() || !passwordMatches(
                            password, rs.getString("password"))) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Invalid username or password!"
                        );

                        return;
                    }

                    String name = rs.getString("full_name");
                    adminName = name;
                    showDashboard(name);
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login error: " + e.getMessage()
            );
        }
    }

    // ===== ADMIN REGISTER SCREEN =====
    public void showAdminRegister() {

        getContentPane().removeAll();

        setTitle("Library Management System - Admin Register");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        regFullName = new JTextField();
        regUsername = new JTextField();
        regPassword = new JPasswordField();
        regConfirm = new JPasswordField();

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        panel.add(new JLabel("Full Name:"));
        panel.add(regFullName);

        panel.add(new JLabel("Username:"));
        panel.add(regUsername);

        panel.add(new JLabel("Password:"));
        panel.add(regPassword);

        panel.add(new JLabel("Confirm Password:"));
        panel.add(regConfirm);

        panel.add(registerButton);
        panel.add(backButton);

        add(panel);

        registerButton.addActionListener(e -> registerAdmin());
        backButton.addActionListener(e -> showLogin());

        getRootPane().setDefaultButton(registerButton);

        revalidate();
        repaint();
        setVisible(true);
    }

    // SAVE THE NEW ADMIN
    public void registerAdmin() {

        String fullName = regFullName.getText().trim();
        String username = regUsername.getText().trim();

        char[] pass = regPassword.getPassword();
        char[] confirm = regConfirm.getPassword();

        if (fullName.isEmpty() || username.isEmpty()
                || pass.length == 0 || confirm.length == 0) {

            JOptionPane.showMessageDialog(
                    this, "Please fill in all the fields."
            );

            return;
        }

        if (!Arrays.equals(pass, confirm)) {

            JOptionPane.showMessageDialog(
                    this, "Passwords do not match."
            );

            return;
        }

        if (pass.length < 8) {

            JOptionPane.showMessageDialog(
                    this, "Password must have at least 8 characters."
            );

            return;
        }

        String sql = "INSERT INTO admins "
                + "(full_name, username, password) VALUES (?, ?, ?)";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, fullName);
                ps.setString(2, username);
                ps.setString(3, hashPassword(pass));

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this, "Admin account created! You can now log in."
                );

                showLogin();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not register. The username may already "
                    + "be taken.\n" + e.getMessage()
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this, "Could not secure the password."
            );

        } finally {
            Arrays.fill(pass, '\0');
            Arrays.fill(confirm, '\0');
        }
    }

    // CHECK A PASSWORD AGAINST THE STORED VALUE
    private boolean passwordMatches(String typed, String stored) {

        if (stored == null) {
            return false;
        }

        // NEW ADMINS: PBKDF2 HASH
        if (stored.startsWith("pbkdf2$")) {

            try {
                return verifyPassword(typed.toCharArray(), stored);
            } catch (Exception e) {
                return false;
            }
        }

        // OLD ADMINS: PLAINTEXT (KEEP THEM WORKING)
        return stored.equals(typed);
    }

    // CREATE A PBKDF2 HASH
    private String hashPassword(char[] pass) throws Exception {

        int rounds = 120000;
        byte[] salt = new byte[16];

        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                pass, salt, rounds, 256
        );

        try {

            SecretKeyFactory factory = SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256"
            );

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return "pbkdf2$" + rounds + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } finally {
            spec.clearPassword();
        }
    }

    // COMPARE TYPED PASSWORD WITH THE STORED PBKDF2 HASH
    private boolean verifyPassword(char[] password, String stored)
            throws Exception {

        String[] parts = stored.split("\\$");

        if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {
            return false;
        }

        int rounds = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expected = Base64.getDecoder().decode(parts[3]);

        PBEKeySpec spec = new PBEKeySpec(
                password, salt, rounds, expected.length * 8
        );

        try {

            byte[] actual = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            // constant-time comparison
            return MessageDigest.isEqual(expected, actual);

        } finally {
            spec.clearPassword();
        }
    }

    // ADMIN DASHBOARD
    public void showDashboard(String name) {

        adminName = name;

        getContentPane().removeAll();

        setTitle("Library Management System - Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // HEADER
        JLabel welcomeLabel = new JLabel(
                "Welcome, " + name,
                SwingConstants.CENTER
        );

        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 5, 5, 5)
        );

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // DASHBOARD SUMMARY
        JPanel summaryPanel = new JPanel(
                new GridLayout(2, 4, 15, 15)
        );

        summaryPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        summaryPanel.add(createCard(
                "Total Books",
                getCount("SELECT COUNT(*) FROM books")
        ));

        summaryPanel.add(createCard(
                "Available Copies",
                getCount(
                        "SELECT COALESCE(SUM(available_copies), 0) "
                      + "FROM books"
                )
        ));

        summaryPanel.add(createCard(
                "Total Members",
                getCount("SELECT COUNT(*) FROM members")
        ));

        summaryPanel.add(createCard(
                "Currently In Use",
                getCount(
                        "SELECT COUNT(*) FROM book_usage "
                      + "WHERE status = 'Using'"
                )
        ));

        summaryPanel.add(createCard(
                "Returned Today",
                getCount(
                        "SELECT COUNT(*) FROM book_usage "
                      + "WHERE status = 'Returned' "
                      + "AND usage_date = CURDATE()"
                )
        ));

        summaryPanel.add(createCard(
                "Pending Reservations",
                getCount(
                        "SELECT COUNT(*) FROM reservations "
                      + "WHERE status = 'Pending'"
                )
        ));

        summaryPanel.add(createCard(
                "Approved Reservations",
                getCount(
                        "SELECT COUNT(*) FROM reservations "
                      + "WHERE status = 'Approved'"
                )
        ));

        summaryPanel.add(createCard(
                "Total Usage Records",
                getCount("SELECT COUNT(*) FROM book_usage")
        ));

        mainPanel.add(summaryPanel, BorderLayout.CENTER);

        // MENU BUTTONS
        JPanel menuPanel = new JPanel(
                new GridLayout(2, 4, 10, 10)
        );

        menuPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10)
        );

        JButton booksButton = new JButton("Manage Books");
        JButton membersButton = new JButton("Manage Members");
        JButton reservationsButton = new JButton("Reservations");
        JButton usageButton = new JButton("Book Usage");
        JButton reportsButton = new JButton("Reports");
        JButton refreshButton = new JButton("Refresh Dashboard");
        JButton logoutButton = new JButton("Logout");

        menuPanel.add(booksButton);
        menuPanel.add(membersButton);
        menuPanel.add(reservationsButton);
        menuPanel.add(usageButton);
        menuPanel.add(reportsButton);
        menuPanel.add(refreshButton);
        menuPanel.add(logoutButton);

        mainPanel.add(menuPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // BUTTON ACTIONS
        booksButton.addActionListener(e -> showManageBooks());

        membersButton.addActionListener(e -> showManageMembers());

        reservationsButton.addActionListener(e -> showReservations());

        usageButton.addActionListener(e -> showBookUsage());

        // OPEN REPORTS WINDOW
        reportsButton.addActionListener(e -> showReports());

        // REFRESH DASHBOARD
        refreshButton.addActionListener(e -> showDashboard(name));

        // LOGOUT
        logoutButton.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                showLogin();
            }
        });

        revalidate();
        repaint();
        setVisible(true);
    }

    // MANAGE BOOKS SCREEN
    public void showManageBooks() {

        getContentPane().removeAll();

        setTitle("Library Management System - Manage Books");
        setSize(1150, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // HEADER WITH BACK BUTTON
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Manage Books");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 5, 10)
        );

        JButton backButton = new JButton("Back to Dashboard");

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // FORM FIELDS
        JPanel formPanel = new JPanel(
                new GridLayout(4, 4, 8, 8)
        );

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        );

        bookIsbn = new JTextField();
        bookTitle = new JTextField();
        bookAuthor = new JTextField();
        bookCategory = new JTextField();
        bookPublisher = new JTextField();
        bookYear = new JTextField();
        bookQuantity = new JTextField("1");
        bookAvailable = new JTextField("1");

        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(bookIsbn);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(bookTitle);

        formPanel.add(new JLabel("Author:"));
        formPanel.add(bookAuthor);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(bookCategory);

        formPanel.add(new JLabel("Publisher:"));
        formPanel.add(bookPublisher);
        formPanel.add(new JLabel("Publication Year:"));
        formPanel.add(bookYear);

        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(bookQuantity);
        formPanel.add(new JLabel("Available Copies:"));
        formPanel.add(bookAvailable);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        JButton addButton = new JButton("Add Book");
        JButton updateButton = new JButton("Update Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton searchButton = new JButton("Search Book");
        JButton showAllButton = new JButton("Show All");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(showAllButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        // TABLE
        booksModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksModel.setColumnIdentifiers(new Object[]{
            "Book ID", "ISBN", "Title", "Author", "Category",
            "Publisher", "Year", "Quantity", "Available"
        });

        booksTable = new JTable(booksModel);
        booksTable.setRowHeight(24);
        booksTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(booksTable);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // BUTTON ACTIONS
        addButton.addActionListener(e -> addBook());
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        searchButton.addActionListener(e -> searchBook());
        showAllButton.addActionListener(e -> loadBooks());
        clearButton.addActionListener(e -> clearBookFields());
        refreshButton.addActionListener(e -> loadBooks());
        backButton.addActionListener(e -> showDashboard(adminName));

        // CLICK A ROW TO FILL THE FORM
        booksTable.getSelectionModel().addListSelectionListener(e -> {

            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = booksTable.getSelectedRow();

            if (row < 0) {
                return;
            }

            selectedBookId = Integer.parseInt(
                    String.valueOf(booksModel.getValueAt(row, 0))
            );

            bookIsbn.setText(cellText(row, 1));
            bookTitle.setText(cellText(row, 2));
            bookAuthor.setText(cellText(row, 3));
            bookCategory.setText(cellText(row, 4));
            bookPublisher.setText(cellText(row, 5));
            bookYear.setText(cellText(row, 6));
            bookQuantity.setText(cellText(row, 7));
            bookAvailable.setText(cellText(row, 8));
        });

        loadBooks();

        revalidate();
        repaint();
        setVisible(true);
    }

    // READ A TABLE CELL AS TEXT (null becomes empty)
    private String cellText(int row, int column) {

        Object value = booksModel.getValueAt(row, column);

        return value == null ? "" : value.toString();
    }

    // YEAR COLUMNS COME BACK AS A DATE (2024-01-01); KEEP ONLY 2024
    private String yearText(ResultSet result) throws SQLException {

        String year = result.getString("publication_year");

        if (year != null && year.length() > 4) {
            year = year.substring(0, 4);
        }

        return year;
    }

    // LOAD ALL BOOKS
    public void loadBooks() {

        String sql = "SELECT book_id, isbn, title, author, category, "
                + "publisher, publication_year, quantity, "
                + "available_copies FROM books ORDER BY title";

        booksModel.setRowCount(0);
        selectedBookId = -1;

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (Statement statement = con.createStatement();
                 ResultSet result = statement.executeQuery(sql)) {

                while (result.next()) {

                    booksModel.addRow(new Object[]{
                        result.getInt("book_id"),
                        result.getString("isbn"),
                        result.getString("title"),
                        result.getString("author"),
                        result.getString("category"),
                        result.getString("publisher"),
                        yearText(result),
                        result.getInt("quantity"),
                        result.getInt("available_copies")
                    });
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load books.\n" + e.getMessage()
            );
        }
    }

    // ADD BOOK
    public void addBook() {

        String isbn = bookIsbn.getText().trim();
        String title = bookTitle.getText().trim();
        String author = bookAuthor.getText().trim();

        if (title.isEmpty() || author.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Title and Author are required."
            );

            return;
        }

        int quantity;
        int available;
        int year;

        try {

            quantity = Integer.parseInt(
                    bookQuantity.getText().trim()
            );

            available = Integer.parseInt(
                    bookAvailable.getText().trim()
            );

            year = bookYear.getText().trim().isEmpty()
                    ? 0
                    : Integer.parseInt(bookYear.getText().trim());

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Publication Year, Quantity and Available "
                    + "Copies must be numbers."
            );

            return;
        }

        if (quantity < 1) {

            JOptionPane.showMessageDialog(
                    this, "Quantity must be at least 1."
            );

            return;
        }

        if (available < 0 || available > quantity) {

            JOptionPane.showMessageDialog(
                    this,
                    "Available Copies must be between 0 and Quantity."
            );

            return;
        }

        String sql = "INSERT INTO books "
                + "(isbn, title, author, category, publisher, "
                + "publication_year, quantity, available_copies) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                if (isbn.isEmpty()) {
                    ps.setNull(1, Types.VARCHAR);
                } else {
                    ps.setString(1, isbn);
                }

                ps.setString(2, title);
                ps.setString(3, author);
                ps.setString(4, bookCategory.getText().trim());
                ps.setString(5, bookPublisher.getText().trim());

                if (year == 0) {
                    ps.setNull(6, Types.INTEGER);
                } else {
                    ps.setInt(6, year);
                }

                ps.setInt(7, quantity);
                ps.setInt(8, available);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this, "Book added successfully!"
                );

                clearBookFields();
                loadBooks();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not add book. Check if the ISBN "
                    + "already exists.\n" + e.getMessage()
            );
        }
    }

    // UPDATE BOOK
    public void updateBook() {

        if (selectedBookId == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a book in the table first."
            );

            return;
        }

        String isbn = bookIsbn.getText().trim();
        String title = bookTitle.getText().trim();
        String author = bookAuthor.getText().trim();

        if (title.isEmpty() || author.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Title and Author are required."
            );

            return;
        }

        int quantity;
        int available;
        int year;

        try {

            quantity = Integer.parseInt(
                    bookQuantity.getText().trim()
            );

            available = Integer.parseInt(
                    bookAvailable.getText().trim()
            );

            year = bookYear.getText().trim().isEmpty()
                    ? 0
                    : Integer.parseInt(bookYear.getText().trim());

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Publication Year, Quantity and Available "
                    + "Copies must be numbers."
            );

            return;
        }

        if (quantity < 1) {

            JOptionPane.showMessageDialog(
                    this, "Quantity must be at least 1."
            );

            return;
        }

        if (available < 0 || available > quantity) {

            JOptionPane.showMessageDialog(
                    this,
                    "Available Copies must be between 0 and Quantity."
            );

            return;
        }

        String sql = "UPDATE books SET "
                + "isbn = ?, title = ?, author = ?, category = ?, "
                + "publisher = ?, publication_year = ?, quantity = ?, "
                + "available_copies = ? WHERE book_id = ?";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                if (isbn.isEmpty()) {
                    ps.setNull(1, Types.VARCHAR);
                } else {
                    ps.setString(1, isbn);
                }

                ps.setString(2, title);
                ps.setString(3, author);
                ps.setString(4, bookCategory.getText().trim());
                ps.setString(5, bookPublisher.getText().trim());

                if (year == 0) {
                    ps.setNull(6, Types.INTEGER);
                } else {
                    ps.setInt(6, year);
                }

                ps.setInt(7, quantity);
                ps.setInt(8, available);
                ps.setInt(9, selectedBookId);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(
                            this, "Book updated successfully!"
                    );

                    clearBookFields();
                    loadBooks();
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not update book.\n" + e.getMessage()
            );
        }
    }

    // DELETE BOOK
    public void deleteBook() {

        if (selectedBookId == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a book in the table first."
            );

            return;
        }

        String countSql = "SELECT COUNT(*) FROM book_usage "
                + "WHERE book_id = ?";

        String deleteSql = "DELETE FROM books WHERE book_id = ?";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            // DO NOT DELETE A BOOK WITH USAGE HISTORY
            try (PreparedStatement countPs =
                         con.prepareStatement(countSql)) {

                countPs.setInt(1, selectedBookId);

                try (ResultSet result = countPs.executeQuery()) {

                    if (result.next() && result.getInt(1) > 0) {

                        JOptionPane.showMessageDialog(
                                this,
                                "This book has usage records and "
                                + "cannot be deleted."
                        );

                        return;
                    }
                }
            }

            // DO NOT DELETE A BOOK WITH RESERVATION RECORDS
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM reservations "
                    + "WHERE book_id = ?")) {

                ps.setInt(1, selectedBookId);

                try (ResultSet result = ps.executeQuery()) {

                    if (result.next() && result.getInt(1) > 0) {

                        JOptionPane.showMessageDialog(
                                this,
                                "This book has reservation records and "
                                + "cannot be deleted."
                        );

                        return;
                    }
                }
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Delete this book?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            try (PreparedStatement ps =
                         con.prepareStatement(deleteSql)) {

                ps.setInt(1, selectedBookId);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(
                            this, "Book deleted."
                    );

                    clearBookFields();
                    loadBooks();
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete book.\n" + e.getMessage()
            );
        }
    }

    // SEARCH BOOK
    public void searchBook() {

        String keyword = JOptionPane.showInputDialog(
                this, "Enter Title, Author or ISBN:"
        );

        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String sql = "SELECT book_id, isbn, title, author, category, "
                + "publisher, publication_year, quantity, "
                + "available_copies FROM books "
                + "WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? "
                + "ORDER BY title";

        booksModel.setRowCount(0);
        selectedBookId = -1;

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                String pattern = "%" + keyword.trim() + "%";

                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);

                try (ResultSet result = ps.executeQuery()) {

                    while (result.next()) {

                        booksModel.addRow(new Object[]{
                            result.getInt("book_id"),
                            result.getString("isbn"),
                            result.getString("title"),
                            result.getString("author"),
                            result.getString("category"),
                            result.getString("publisher"),
                            yearText(result),
                            result.getInt("quantity"),
                            result.getInt("available_copies")
                        });
                    }
                }
            }

            if (booksModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this, "No book found."
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Search failed.\n" + e.getMessage()
            );
        }
    }

    // CLEAR BOOK FORM
    public void clearBookFields() {

        selectedBookId = -1;

        bookIsbn.setText("");
        bookTitle.setText("");
        bookAuthor.setText("");
        bookCategory.setText("");
        bookPublisher.setText("");
        bookYear.setText("");
        bookQuantity.setText("1");
        bookAvailable.setText("1");

        booksTable.clearSelection();
    }

    // MANAGE MEMBERS SCREEN
    public void showManageMembers() {

        getContentPane().removeAll();

        setTitle("Library Management System - Manage Members");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // HEADER WITH BACK BUTTON
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Manage Members");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 5, 10)
        );

        JButton backButton = new JButton("Back to Dashboard");

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // FORM FIELDS
        JPanel formPanel = new JPanel(
                new GridLayout(5, 4, 8, 8)
        );

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        );

        memberStudentId = new JTextField();
        memberFullName = new JTextField();
        memberCourse = new JTextField();
        memberYearLevel = new JTextField();
        memberSection = new JTextField();
        memberContact = new JTextField();
        memberAddress = new JTextField();
        memberRegistrationDate = new JTextField(
                LocalDate.now().toString()
        );

        memberPassword = new JPasswordField();
        memberPassword.setToolTipText(
                "Required when adding. Leave blank when updating to "
                + "keep the current password."
        );

        memberStatusBox = new JComboBox<>(
                new String[]{"Active", "Inactive"}
        );

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(memberStudentId);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(memberFullName);

        formPanel.add(new JLabel("Course:"));
        formPanel.add(memberCourse);
        formPanel.add(new JLabel("Year Level:"));
        formPanel.add(memberYearLevel);

        formPanel.add(new JLabel("Section:"));
        formPanel.add(memberSection);
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(memberContact);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(memberAddress);
        formPanel.add(new JLabel("Registration Date:"));
        formPanel.add(memberRegistrationDate);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(memberStatusBox);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(memberPassword);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        JButton addButton = new JButton("Add Member");
        JButton updateButton = new JButton("Update Member");
        JButton deleteButton = new JButton("Delete Member");
        JButton searchButton = new JButton("Search Member");
        JButton showAllButton = new JButton("Show All");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(showAllButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        // TABLE
        membersModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        membersModel.setColumnIdentifiers(new Object[]{
            "Member ID", "Student ID", "Full Name", "Course",
            "Year Level", "Section", "Contact Number", "Address",
            "Registration Date", "Status"
        });

        membersTable = new JTable(membersModel);
        membersTable.setRowHeight(24);
        membersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(membersTable);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // BUTTON ACTIONS
        addButton.addActionListener(e -> addMember());
        updateButton.addActionListener(e -> updateMember());
        deleteButton.addActionListener(e -> deleteMember());
        searchButton.addActionListener(e -> searchMember());
        showAllButton.addActionListener(e -> loadMembers());
        clearButton.addActionListener(e -> clearMemberFields());
        refreshButton.addActionListener(e -> loadMembers());
        backButton.addActionListener(e -> showDashboard(adminName));

        // CLICK A ROW TO FILL THE FORM
        membersTable.getSelectionModel().addListSelectionListener(e -> {

            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = membersTable.getSelectedRow();

            if (row < 0) {
                return;
            }

            selectedMemberId = Integer.parseInt(
                    String.valueOf(membersModel.getValueAt(row, 0))
            );

            memberStudentId.setText(
                    String.valueOf(membersModel.getValueAt(row, 1))
            );
            memberFullName.setText(
                    String.valueOf(membersModel.getValueAt(row, 2))
            );
            memberCourse.setText(
                    String.valueOf(membersModel.getValueAt(row, 3))
            );
            memberYearLevel.setText(
                    String.valueOf(membersModel.getValueAt(row, 4))
            );
            memberSection.setText(
                    String.valueOf(membersModel.getValueAt(row, 5))
            );
            memberContact.setText(
                    String.valueOf(membersModel.getValueAt(row, 6))
            );
            memberAddress.setText(
                    String.valueOf(membersModel.getValueAt(row, 7))
            );
            memberRegistrationDate.setText(
                    String.valueOf(membersModel.getValueAt(row, 8))
            );
            memberStatusBox.setSelectedItem(
                    String.valueOf(membersModel.getValueAt(row, 9))
            );

            memberPassword.setText("");
        });

        loadMembers();

        revalidate();
        repaint();
        setVisible(true);
    }

    // LOAD ALL MEMBERS
    public void loadMembers() {

        String sql = "SELECT member_id, student_id, full_name, "
                + "course, year_level, section, contact_number, "
                + "address, registration_date, status "
                + "FROM members ORDER BY full_name";

        membersModel.setRowCount(0);
        selectedMemberId = -1;

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (Statement statement = con.createStatement();
                 ResultSet result = statement.executeQuery(sql)) {

                while (result.next()) {

                    membersModel.addRow(new Object[]{
                        result.getInt("member_id"),
                        result.getString("student_id"),
                        result.getString("full_name"),
                        result.getString("course"),
                        result.getString("year_level"),
                        result.getString("section"),
                        result.getString("contact_number"),
                        result.getString("address"),
                        result.getDate("registration_date"),
                        result.getString("status")
                    });
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load members.\n" + e.getMessage()
            );
        }
    }

    // ADD MEMBER
    public void addMember() {

        if (memberStudentId.getText().trim().isEmpty()
                || memberFullName.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Student ID and Full Name are required."
            );

            return;
        }

        String sql = "INSERT INTO members "
                + "(student_id, full_name, course, year_level, section, "
                + "contact_number, address, registration_date, status, "
                + "password_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        char[] pass = memberPassword.getPassword();

        if (pass.length < 8) {

            Arrays.fill(pass, '\0');

            JOptionPane.showMessageDialog(
                    this,
                    "Password must have at least 8 characters so the "
                    + "member can log in."
            );

            return;
        }

        String passwordHash;

        try {
            passwordHash = hashPassword(pass);
        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this, "Could not secure the password."
            );

            return;

        } finally {
            Arrays.fill(pass, '\0');
        }

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, memberStudentId.getText().trim());
                ps.setString(2, memberFullName.getText().trim());
                ps.setString(3, memberCourse.getText().trim());
                ps.setString(4, memberYearLevel.getText().trim());
                ps.setString(5, memberSection.getText().trim());
                ps.setString(6, memberContact.getText().trim());
                ps.setString(7, memberAddress.getText().trim());

                ps.setDate(8, java.sql.Date.valueOf(
                        LocalDate.parse(
                                memberRegistrationDate.getText().trim()
                        )
                ));

                ps.setString(
                        9, memberStatusBox.getSelectedItem().toString()
                );

                ps.setString(10, passwordHash);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Member added successfully!\n"
                        + "Give this password to the student."
                );

                clearMemberFields();
                loadMembers();
            }

        } catch (java.time.format.DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration Date must be YYYY-MM-DD."
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not add member. Check if the Student ID "
                    + "is already registered.\n" + e.getMessage()
            );
        }
    }

    // UPDATE MEMBER
    public void updateMember() {

        if (selectedMemberId == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a member in the table first."
            );

            return;
        }

        String sql = "UPDATE members SET "
                + "student_id = ?, full_name = ?, course = ?, "
                + "year_level = ?, section = ?, contact_number = ?, "
                + "address = ?, registration_date = ?, status = ? "
                + "WHERE member_id = ?";

        char[] newPass = memberPassword.getPassword();
        String newHash = null;

        try {

            if (newPass.length > 0) {

                if (newPass.length < 8) {

                    JOptionPane.showMessageDialog(
                            this,
                            "New password must have at least "
                            + "8 characters."
                    );

                    return;
                }

                newHash = hashPassword(newPass);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this, "Could not secure the new password."
            );

            return;

        } finally {
            Arrays.fill(newPass, '\0');
        }

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, memberStudentId.getText().trim());
                ps.setString(2, memberFullName.getText().trim());
                ps.setString(3, memberCourse.getText().trim());
                ps.setString(4, memberYearLevel.getText().trim());
                ps.setString(5, memberSection.getText().trim());
                ps.setString(6, memberContact.getText().trim());
                ps.setString(7, memberAddress.getText().trim());

                ps.setDate(8, java.sql.Date.valueOf(
                        LocalDate.parse(
                                memberRegistrationDate.getText().trim()
                        )
                ));

                ps.setString(
                        9, memberStatusBox.getSelectedItem().toString()
                );

                ps.setInt(10, selectedMemberId);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    if (newHash != null) {

                        try (PreparedStatement passPs =
                                     con.prepareStatement(
                                "UPDATE members SET password_hash = ? "
                                + "WHERE member_id = ?")) {

                            passPs.setString(1, newHash);
                            passPs.setInt(2, selectedMemberId);
                            passPs.executeUpdate();
                        }
                    }

                    JOptionPane.showMessageDialog(
                            this, "Member updated successfully!"
                    );

                    clearMemberFields();
                    loadMembers();
                }
            }

        } catch (java.time.format.DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration Date must be YYYY-MM-DD."
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not update member.\n" + e.getMessage()
            );
        }
    }

    // DELETE MEMBER
    public void deleteMember() {

        if (selectedMemberId == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a member in the table first."
            );

            return;
        }

        String activeSql = "SELECT "
                + "(SELECT COUNT(*) FROM book_usage "
                + " WHERE member_id = ? AND status = 'Using') "
                + "AS active_usage, "
                + "(SELECT COUNT(*) FROM reservations "
                + " WHERE member_id = ? "
                + " AND status IN ('Pending','Approved','Borrowed')) "
                + "AS active_reservations";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            // DO NOT DELETE A MEMBER WITH ACTIVE BOOKS
            int activeUsage;
            int activeReservations;

            try (PreparedStatement ps =
                         con.prepareStatement(activeSql)) {

                ps.setInt(1, selectedMemberId);
                ps.setInt(2, selectedMemberId);

                try (ResultSet result = ps.executeQuery()) {

                    result.next();

                    activeUsage = result.getInt("active_usage");
                    activeReservations =
                            result.getInt("active_reservations");
                }
            }

            if (activeUsage > 0 || activeReservations > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "This member still has an active reservation "
                        + "or a borrowed book.\n"
                        + "Please finish, return, or cancel it first."
                );

                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Delete this member? Their reservation and usage "
                    + "history will also be deleted.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            con.setAutoCommit(false);

            try {

                // REMOVE HISTORY FIRST (FOREIGN KEYS)
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM book_usage WHERE member_id = ?")) {

                    ps.setInt(1, selectedMemberId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM reservations WHERE member_id = ?")) {

                    ps.setInt(1, selectedMemberId);
                    ps.executeUpdate();
                }

                int rows;

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM members WHERE member_id = ?")) {

                    ps.setInt(1, selectedMemberId);
                    rows = ps.executeUpdate();
                }

                con.commit();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(
                            this, "Member deleted."
                    );

                    clearMemberFields();
                    loadMembers();
                }

            } catch (SQLException e) {

                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Could not delete member.\n" + e.getMessage()
                );

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete member.\n" + e.getMessage()
            );
        }
    }

    // SEARCH MEMBER
    public void searchMember() {

        String keyword = JOptionPane.showInputDialog(
                this, "Enter Student ID or Full Name:"
        );

        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String sql = "SELECT member_id, student_id, full_name, "
                + "course, year_level, section, contact_number, "
                + "address, registration_date, status "
                + "FROM members "
                + "WHERE student_id LIKE ? OR full_name LIKE ? "
                + "ORDER BY full_name";

        membersModel.setRowCount(0);
        selectedMemberId = -1;

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                String pattern = "%" + keyword.trim() + "%";

                ps.setString(1, pattern);
                ps.setString(2, pattern);

                try (ResultSet result = ps.executeQuery()) {

                    while (result.next()) {

                        membersModel.addRow(new Object[]{
                            result.getInt("member_id"),
                            result.getString("student_id"),
                            result.getString("full_name"),
                            result.getString("course"),
                            result.getString("year_level"),
                            result.getString("section"),
                            result.getString("contact_number"),
                            result.getString("address"),
                            result.getDate("registration_date"),
                            result.getString("status")
                        });
                    }
                }
            }

            if (membersModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this, "No member found."
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Search failed.\n" + e.getMessage()
            );
        }
    }

    // CLEAR MEMBER FORM
    public void clearMemberFields() {

        selectedMemberId = -1;

        memberStudentId.setText("");
        memberFullName.setText("");
        memberCourse.setText("");
        memberYearLevel.setText("");
        memberSection.setText("");
        memberContact.setText("");
        memberAddress.setText("");
        memberRegistrationDate.setText(
                LocalDate.now().toString()
        );
        memberStatusBox.setSelectedIndex(0);
        memberPassword.setText("");

        membersTable.clearSelection();
    }

    // ===== RESERVATIONS SCREEN =====
    public void showReservations() {

        getContentPane().removeAll();

        setTitle("Library Management System - Reservations");
        setSize(1100, 650);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Book Reservations");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 5, 10)
        );

        JButton backButton = new JButton("Back to Dashboard");

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        JButton pendingButton = new JButton("Show Pending");
        JButton approvedButton = new JButton("Show Approved");
        JButton borrowedButton = new JButton("Show In Use");
        JButton allButton = new JButton("Show All");
        JButton approveButton = new JButton("Approve");
        JButton declineButton = new JButton("Decline");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(pendingButton);
        buttonPanel.add(approvedButton);
        buttonPanel.add(borrowedButton);
        buttonPanel.add(allButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(declineButton);
        buttonPanel.add(refreshButton);

        // TABLE
        reservationsModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationsModel.setColumnIdentifiers(new Object[]{
            "ID", "Student ID", "Student Name", "Book Title",
            "Date", "Status", "Note"
        });

        reservationsTable = new JTable(reservationsModel);
        reservationsTable.setRowHeight(24);
        reservationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);

        JLabel hintLabel = new JLabel(
                "Flow: Pending → Approve → member clicks Borrow at the "
                + "library → Borrowed → member clicks Return → Completed"
        );

        hintLabel.setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(hintLabel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // ACTIONS
        pendingButton.addActionListener(e -> {
            reservationFilter = "Pending";
            loadReservations("Pending");
        });

        approvedButton.addActionListener(e -> {
            reservationFilter = "Approved";
            loadReservations("Approved");
        });

        borrowedButton.addActionListener(e -> {
            reservationFilter = "Borrowed";
            loadReservations("Borrowed");
        });

        allButton.addActionListener(e -> {
            reservationFilter = null;
            loadReservations(null);
        });

        refreshButton.addActionListener(e ->
                loadReservations(reservationFilter)
        );

        approveButton.addActionListener(e -> approveReservation());
        declineButton.addActionListener(e -> declineReservation());
        backButton.addActionListener(e -> showDashboard(adminName));

        // CLICK A ROW TO SELECT
        reservationsTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    int row = reservationsTable.getSelectedRow();

                    if (row < 0) {
                        return;
                    }

                    selectedReservationId = Integer.parseInt(
                            String.valueOf(
                                    reservationsModel.getValueAt(row, 0)
                            )
                    );
                });

        reservationFilter = "Pending";
        loadReservations("Pending");

        revalidate();
        repaint();
        setVisible(true);
    }

    // LOAD RESERVATIONS (null = tanan)
    public void loadReservations(String statusFilter) {

        String sql = "SELECT r.reservation_id, m.student_id, "
                + "m.full_name, b.title, r.reservation_date, "
                + "r.status, r.admin_note "
                + "FROM reservations r "
                + "JOIN members m ON r.member_id = m.member_id "
                + "JOIN books b ON r.book_id = b.book_id ";

        if (statusFilter != null) {
            sql = sql + "WHERE r.status = ? ";
        }

        sql = sql + "ORDER BY r.created_at DESC";

        reservationsModel.setRowCount(0);
        selectedReservationId = -1;

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                if (statusFilter != null) {
                    ps.setString(1, statusFilter);
                }

                try (ResultSet result = ps.executeQuery()) {

                    while (result.next()) {

                        reservationsModel.addRow(new Object[]{
                            result.getInt("reservation_id"),
                            result.getString("student_id"),
                            result.getString("full_name"),
                            result.getString("title"),
                            result.getDate("reservation_date"),
                            result.getString("status"),
                            result.getString("admin_note")
                        });
                    }
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load reservations.\n" + e.getMessage()
            );
        }
    }

    // APPROVE RESERVATION (HOLD ONE COPY)
    public void approveReservation() {

        if (selectedReservationId == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a reservation first."
            );

            return;
        }

        String checkSql = "SELECT r.book_id, r.status, "
                + "b.available_copies "
                + "FROM reservations r "
                + "JOIN books b ON r.book_id = b.book_id "
                + "WHERE r.reservation_id = ? FOR UPDATE";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            con.setAutoCommit(false);

            try {

                int bookId;
                String status;
                int available;

                try (PreparedStatement ps =
                             con.prepareStatement(checkSql)) {

                    ps.setInt(1, selectedReservationId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            con.rollback();
                            JOptionPane.showMessageDialog(
                                    this, "Reservation not found."
                            );
                            return;
                        }

                        bookId = rs.getInt("book_id");
                        status = rs.getString("status");
                        available = rs.getInt("available_copies");
                    }
                }

                if (!"Pending".equals(status)) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "This reservation is already " + status + "."
                    );

                    return;
                }

                if (available < 1) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            this, "No available copies for this book."
                    );

                    return;
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE reservations SET status = 'Approved' "
                        + "WHERE reservation_id = ?")) {

                    ps.setInt(1, selectedReservationId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE books "
                        + "SET available_copies = available_copies - 1 "
                        + "WHERE book_id = ?")) {

                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Reservation approved. One copy is now held "
                        + "for the student.\n\n"
                        + "The student can now get the book at the "
                        + "library and click \"Borrow Book (Time In)\"."
                );

                loadReservations(reservationFilter);

            } catch (SQLException e) {

                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Could not approve the reservation.\n"
                        + e.getMessage()
                );

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this, "Database error.\n" + e.getMessage()
            );
        }
    }

    // DECLINE RESERVATION
    public void declineReservation() {

        if (selectedReservationId == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a reservation first."
            );

            return;
        }

        String note = JOptionPane.showInputDialog(
                this, "Reason for declining (optional):"
        );

        if (note == null) {
            return;
        }

        String sql = "UPDATE reservations "
                + "SET status = 'Declined', admin_note = ? "
                + "WHERE reservation_id = ? AND status = 'Pending'";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, note.trim());
                ps.setInt(2, selectedReservationId);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    JOptionPane.showMessageDialog(
                            this, "Reservation declined."
                    );

                    loadReservations(reservationFilter);

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Only pending reservations can be declined."
                    );
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not decline the reservation.\n"
                    + e.getMessage()
            );
        }
    }

    // ===== BOOK USAGE SCREEN (BORROW / RETURN) =====
    public void showBookUsage() {

        getContentPane().removeAll();

        setTitle("Library Management System - Book Usage");
        setSize(1100, 650);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Book Usage (Borrow / Return)");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 5, 10)
        );

        JButton backButton = new JButton("Back to Dashboard");

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        JButton inUseButton = new JButton("Show In Use");
        JButton returnedButton = new JButton("Show Returned");
        JButton allButton = new JButton("Show All");
        JButton returnButton = new JButton("Mark Returned");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(inUseButton);
        buttonPanel.add(returnedButton);
        buttonPanel.add(allButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(refreshButton);

        // TABLE
        usageModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usageModel.setColumnIdentifiers(new Object[]{
            "ID", "Student ID", "Student Name", "Book Title",
            "Date", "Time In", "Time Out", "Status"
        });

        usageTable = new JTable(usageModel);
        usageTable.setRowHeight(24);
        usageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(usageTable);

        JLabel hintLabel = new JLabel(
                "Using = book is with the student  ·  "
                + "Returned = book is back in the library  ·  "
                + "Mark Returned is the admin fallback if the student "
                + "forgets to click Return"
        );

        hintLabel.setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(hintLabel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // ACTIONS
        inUseButton.addActionListener(e -> {
            usageFilter = "Using";
            loadBookUsage("Using");
        });

        returnedButton.addActionListener(e -> {
            usageFilter = "Returned";
            loadBookUsage("Returned");
        });

        allButton.addActionListener(e -> {
            usageFilter = null;
            loadBookUsage(null);
        });

        refreshButton.addActionListener(e ->
                loadBookUsage(usageFilter)
        );

        returnButton.addActionListener(e -> markUsageReturned());
        backButton.addActionListener(e -> showDashboard(adminName));

        // CLICK A ROW TO SELECT
        usageTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    int row = usageTable.getSelectedRow();

                    if (row < 0) {
                        return;
                    }

                    selectedUsageId = Integer.parseInt(
                            String.valueOf(
                                    usageModel.getValueAt(row, 0)
                            )
                    );
                });

        usageFilter = "Using";
        loadBookUsage("Using");

        revalidate();
        repaint();
        setVisible(true);
    }

    // LOAD BOOK USAGE RECORDS (null = tanan)
    public void loadBookUsage(String statusFilter) {

        String sql = "SELECT u.usage_id, m.student_id, m.full_name, "
                + "b.title, u.usage_date, u.time_in, u.time_out, "
                + "u.status "
                + "FROM book_usage u "
                + "JOIN members m ON u.member_id = m.member_id "
                + "JOIN books b ON u.book_id = b.book_id ";

        if (statusFilter != null) {
            sql = sql + "WHERE u.status = ? ";
        }

        sql = sql + "ORDER BY u.usage_date DESC, u.time_in DESC";

        usageModel.setRowCount(0);
        selectedUsageId = -1;

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                if (statusFilter != null) {
                    ps.setString(1, statusFilter);
                }

                try (ResultSet result = ps.executeQuery()) {

                    while (result.next()) {

                        usageModel.addRow(new Object[]{
                            result.getInt("usage_id"),
                            result.getString("student_id"),
                            result.getString("full_name"),
                            result.getString("title"),
                            result.getDate("usage_date"),
                            result.getTime("time_in"),
                            result.getTime("time_out"),
                            result.getString("status")
                        });
                    }
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load book usage.\n" + e.getMessage()
            );
        }
    }

    // ADMIN FALLBACK: MARK A BOOK AS RETURNED
    public void markUsageReturned() {

        if (selectedUsageId == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a usage record first."
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Mark this book as returned?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        String selectSql = "SELECT book_id, reservation_id, status "
                + "FROM book_usage WHERE usage_id = ?";

        String usageSql = "UPDATE book_usage "
                + "SET time_out = CURTIME(), status = 'Returned' "
                + "WHERE usage_id = ? AND status = 'Using'";

        String bookSql = "UPDATE books SET available_copies = "
                + "LEAST(quantity, available_copies + 1) "
                + "WHERE book_id = ?";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            con.setAutoCommit(false);

            try {

                int bookId;
                Integer reservationId;
                String status;

                try (PreparedStatement ps =
                             con.prepareStatement(selectSql)) {

                    ps.setInt(1, selectedUsageId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {

                            con.rollback();

                            JOptionPane.showMessageDialog(
                                    this, "Usage record not found."
                            );

                            return;
                        }

                        bookId = rs.getInt("book_id");
                        status = rs.getString("status");

                        Object reservationValue =
                                rs.getObject("reservation_id");

                        reservationId = reservationValue == null
                                ? null
                                : ((Number) reservationValue).intValue();
                    }
                }

                if (!"Using".equals(status)) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "This book is already " + status + "."
                    );

                    return;
                }

                try (PreparedStatement ps =
                             con.prepareStatement(usageSql)) {

                    ps.setInt(1, selectedUsageId);

                    if (ps.executeUpdate() == 0) {

                        con.rollback();

                        JOptionPane.showMessageDialog(
                                this,
                                "Could not update the usage record."
                        );

                        return;
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(bookSql)) {

                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                if (reservationId != null) {

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE reservations SET status = 'Completed' "
                            + "WHERE reservation_id = ? "
                            + "AND status = 'Borrowed'")) {

                        ps.setInt(1, reservationId);
                        ps.executeUpdate();
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Book marked as returned. "
                        + "The copy is available again."
                );

                loadBookUsage(usageFilter);

            } catch (SQLException e) {

                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Could not mark the book as returned.\n"
                        + e.getMessage()
                );

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this, "Database error.\n" + e.getMessage()
            );
        }
    }

    // REPORTS WINDOW
    public void showReports() {

        JFrame reportFrame = new JFrame("Library Reports");

        reportFrame.setSize(900, 500);
        reportFrame.setLocationRelativeTo(this);
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reportFrame.setLayout(new BorderLayout(10, 10));

        // REPORT OPTIONS
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel reportLabel = new JLabel("Select Report:");

        String[] reportOptions = {
            "Books Report",
            "Student Members Report",
            "Book Usage History"
        };

        JComboBox<String> reportBox =
                new JComboBox<>(reportOptions);

        JButton loadButton = new JButton("Load Report");

        topPanel.add(reportLabel);
        topPanel.add(reportBox);
        topPanel.add(loadButton);

        reportFrame.add(topPanel, BorderLayout.NORTH);

        // REPORT TABLE
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable reportTable = new JTable(model);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        reportTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(reportTable);

        reportFrame.add(scrollPane, BorderLayout.CENTER);

        // LOAD SELECTED REPORT
        loadButton.addActionListener(e -> {

            String selectedReport =
                    (String) reportBox.getSelectedItem();

            loadReport(selectedReport, model, reportFrame);
        });

        reportFrame.setVisible(true);

        // LOAD BOOKS REPORT BY DEFAULT
        loadReport("Books Report", model, reportFrame);
    }

    // LOAD DATA FROM MYSQL
    public void loadReport(
            String reportType,
            DefaultTableModel model,
            JFrame reportFrame) {

        String sql;

        if ("Books Report".equals(reportType)) {

            sql = "SELECT "
                + "book_id AS 'Book ID', "
                + "isbn AS ISBN, "
                + "title AS Title, "
                + "author AS Author, "
                + "category AS Category, "
                + "quantity AS Quantity, "
                + "available_copies AS 'Available Copies' "
                + "FROM books ORDER BY title";

        } else if ("Student Members Report".equals(reportType)) {

            sql = "SELECT "
                + "member_id AS 'Member ID', "
                + "student_id AS 'Student ID', "
                + "full_name AS 'Full Name', "
                + "course AS Course, "
                + "year_level AS 'Year Level', "
                + "section AS Section, "
                + "contact_number AS 'Contact Number', "
                + "status AS Status "
                + "FROM members ORDER BY full_name";

        } else if ("Book Usage History".equals(reportType)) {

            sql = "SELECT "
                + "u.usage_id AS 'Usage ID', "
                + "m.student_id AS 'Student ID', "
                + "m.full_name AS 'Student Name', "
                + "b.title AS 'Book Title', "
                + "u.usage_date AS Date, "
                + "u.time_in AS 'Time In', "
                + "u.time_out AS 'Time Out', "
                + "u.status AS Status "
                + "FROM book_usage u "
                + "JOIN books b ON u.book_id = b.book_id "
                + "JOIN members m ON u.member_id = m.member_id "
                + "ORDER BY u.usage_date DESC, u.time_in DESC";

        } else {
            return;
        }

        // CLEAR OLD REPORT
        model.setRowCount(0);
        model.setColumnCount(0);

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        reportFrame,
                        "Database connection failed!"
                );
                return;
            }

            try (Statement statement = con.createStatement();
                 ResultSet result = statement.executeQuery(sql)) {

                ResultSetMetaData meta = result.getMetaData();
                int columnCount = meta.getColumnCount();

                // ADD TABLE HEADERS
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(meta.getColumnLabel(i));
                }

                // ADD DATABASE RECORDS
                while (result.next()) {

                    Object[] row = new Object[columnCount];

                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = result.getObject(i);
                    }

                    model.addRow(row);
                }

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(
                            reportFrame,
                            "No records found for this report."
                    );
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    reportFrame,
                    "Could not load report.\n"
                    + "Please check your database tables.\n\n"
                    + e.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // DASHBOARD CARD
    public JPanel createCard(String title, int count) {

        JPanel card = new JPanel(new GridLayout(2, 1));

        card.setBorder(
                BorderFactory.createLineBorder(Color.GRAY)
        );

        JLabel titleLabel = new JLabel(
                title,
                SwingConstants.CENTER
        );

        JLabel countLabel = new JLabel(
                String.valueOf(count),
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        countLabel.setFont(new Font("Arial", Font.BOLD, 24));

        card.add(titleLabel);
        card.add(countLabel);

        return card;
    }

    // GET COUNT FROM DATABASE
    public int getCount(String sql) {

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                return 0;
            }

            try (Statement statement = con.createStatement();
                 ResultSet result = statement.executeQuery(sql)) {

                if (result.next()) {
                    return result.getInt(1);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Dashboard error: " + e.getMessage()
            );
        }

        return 0;
    }

    // START PROGRAM
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new admin());
    }
}
