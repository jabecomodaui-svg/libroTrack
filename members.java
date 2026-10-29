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

public class members extends JFrame {

    // LOGIN FIELDS
    JTextField loginStudentId;
    JPasswordField loginPassword;

    // SIGN UP FIELDS
    JTextField signupStudentId, signupFullName, signupCourse;
    JTextField signupYearLevel, signupSection, signupContact;
    JTextField signupAddress;

    JPasswordField signupPassword, signupConfirm;

    // MEMBER INFORMATION (after login)
    int memberId = -1;
    String studentId = "", fullName = "", course = "";
    String yearLevel = "", section = "", status = "";

    // USAGE TABLE
    DefaultTableModel model;

    // AVAILABLE BOOKS TABLE
    DefaultTableModel booksModel;
    JTable booksTable;
    JTextField bookSearchField;

    // MY RESERVATIONS TABLE
    DefaultTableModel reservationsModel;
    JTable reservationsTable;

    // EMPTY LOGIN FORM
    public members() {
        showLogin("");
    }

    // LOGIN FORM WITH STUDENT ID ALREADY FILLED
    public members(String studentId) {
        showLogin(studentId);
    }

    // ===== 1. STUDENT LOGIN SCREEN =====
    public void showLogin(String filledStudentId) {

        getContentPane().removeAll();

        setTitle("Library Management System - Student Login");
        setSize(380, 230);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        loginStudentId = new JTextField(filledStudentId);
        loginPassword = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");

        panel.add(new JLabel("Student ID:"));
        panel.add(loginStudentId);

        panel.add(new JLabel("Password:"));
        panel.add(loginPassword);

        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);

        loginButton.addActionListener(e -> loginStudent());
        signupButton.addActionListener(e -> showSignup());

        getRootPane().setDefaultButton(loginButton);

        revalidate();
        repaint();
        setVisible(true);
    }

    // CHECK STUDENT LOGIN
    public void loginStudent() {

        String typedId = loginStudentId.getText().trim();
        char[] password = loginPassword.getPassword();

        if (typedId.isEmpty() || password.length == 0) {

            JOptionPane.showMessageDialog(
                    this, "Please enter your Student ID and password."
            );

            return;
        }

        String sql = "SELECT member_id, student_id, full_name, course, "
                + "year_level, section, status, password_hash "
                + "FROM members WHERE student_id = ?";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, typedId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(
                                this, "Invalid Student ID or password!"
                        );
                        return;
                    }

                    String foundStatus = rs.getString("status");
                    String storedHash = rs.getString("password_hash");

                    if (!"Active".equals(foundStatus)) {

                        JOptionPane.showMessageDialog(
                                this, "This account is not active."
                        );

                        return;
                    }

                    if (storedHash == null) {

                        JOptionPane.showMessageDialog(
                                this,
                                "This account has no password yet.\n"
                                + "Please use Student Sign Up."
                        );

                        return;
                    }

                    if (!verifyPassword(password, storedHash)) {

                        JOptionPane.showMessageDialog(
                                this, "Invalid Student ID or password!"
                        );

                        return;
                    }

                    // KEEP THE MEMBER DETAILS
                    memberId = rs.getInt("member_id");
                    studentId = rs.getString("student_id");
                    fullName = rs.getString("full_name");
                    course = rs.getString("course");
                    yearLevel = rs.getString("year_level");
                    section = rs.getString("section");
                    status = foundStatus;

                    showDashboard();
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this, "Login error: " + e.getMessage()
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this, "Could not check the password."
            );

        } finally {
            Arrays.fill(password, '\0');
        }
    }

    // ===== 2. STUDENT SIGN UP SCREEN =====
    public void showSignup() {

        getContentPane().removeAll();

        setTitle("Library Management System - Student Sign Up");
        setSize(420, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(10, 2, 8, 8));

        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        signupStudentId = new JTextField();
        signupFullName = new JTextField();
        signupCourse = new JTextField();
        signupYearLevel = new JTextField();
        signupSection = new JTextField();
        signupContact = new JTextField();
        signupAddress = new JTextField();

        signupPassword = new JPasswordField();
        signupConfirm = new JPasswordField();

        JButton registerButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back to Login");

        panel.add(new JLabel("Student ID:"));
        panel.add(signupStudentId);

        panel.add(new JLabel("Full Name:"));
        panel.add(signupFullName);

        panel.add(new JLabel("Course:"));
        panel.add(signupCourse);

        panel.add(new JLabel("Year Level:"));
        panel.add(signupYearLevel);

        panel.add(new JLabel("Section:"));
        panel.add(signupSection);

        panel.add(new JLabel("Contact Number:"));
        panel.add(signupContact);

        panel.add(new JLabel("Address:"));
        panel.add(signupAddress);

        panel.add(new JLabel("Password:"));
        panel.add(signupPassword);

        panel.add(new JLabel("Confirm Password:"));
        panel.add(signupConfirm);

        panel.add(registerButton);
        panel.add(backButton);

        add(panel);

        registerButton.addActionListener(e -> register());
        backButton.addActionListener(e -> showLogin(""));

        revalidate();
        repaint();
        setVisible(true);
    }

    // SAVE THE NEW STUDENT
    public void register() {

        String id = signupStudentId.getText().trim();
        String name = signupFullName.getText().trim();

        String courseText = signupCourse.getText().trim();
        String yearText = signupYearLevel.getText().trim();
        String sectionText = signupSection.getText().trim();
        String contactText = signupContact.getText().trim();
        String addressText = signupAddress.getText().trim();

        char[] pass = signupPassword.getPassword();
        char[] confirm = signupConfirm.getPassword();

        if (id.isEmpty() || name.isEmpty()
                || pass.length == 0 || confirm.length == 0) {

            JOptionPane.showMessageDialog(
                    this, "Please fill in the required fields."
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

        String sql = "INSERT INTO members "
                + "(student_id, full_name, course, year_level, section, "
                + "contact_number, address, registration_date, status, "
                + "password_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active', ?)";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, courseText);
                ps.setString(4, yearText);
                ps.setString(5, sectionText);
                ps.setString(6, contactText);
                ps.setString(7, addressText);

                ps.setDate(8, java.sql.Date.valueOf(LocalDate.now()));

                // Store a password hash instead of the actual password.
                ps.setString(9, hashPassword(pass));

                ps.executeUpdate();

                // GET THE NEW member_id
                int newMemberId = 0;

                try (ResultSet keys = ps.getGeneratedKeys()) {

                    if (keys.next()) {
                        newMemberId = keys.getInt(1);
                    }
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Registration successful! Welcome to the library."
                );

                clearSignupFields();

                if (newMemberId > 0) {

                    // GO STRAIGHT TO THE DASHBOARD
                    memberId = newMemberId;
                    studentId = id;
                    fullName = name;
                    course = courseText;
                    yearLevel = yearText;
                    section = sectionText;
                    status = "Active";

                    showDashboard();

                } else {

                    showLogin(id);
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration failed. Check if the Student ID "
                    + "is already registered.\n" + e.getMessage()
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

    // ===== 3. MEMBER DASHBOARD SCREEN =====
    public void showDashboard() {

        getContentPane().removeAll();

        setTitle("Library Management System - Member Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // HEADER
        JLabel welcomeLabel = new JLabel(
                "Welcome, " + fullName + "!",
                SwingConstants.CENTER
        );

        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setBorder(
                BorderFactory.createEmptyBorder(15, 5, 5, 5)
        );

        // STUDENT INFORMATION
        JPanel infoPanel = new JPanel(
                new GridLayout(1, 4, 10, 10)
        );

        infoPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 20, 10, 20)
        );

        infoPanel.add(createInfo("Student ID", studentId));
        infoPanel.add(createInfo("Course", course));
        infoPanel.add(createInfo("Year Level", yearLevel));
        infoPanel.add(createInfo("Section", section));

        // ===== AVAILABLE BOOKS TABLE =====
        booksModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksModel.setColumnIdentifiers(new Object[]{
            "book_id", "Title", "Author", "Category", "Publisher", "Year",
            "Total Copies", "Available Copies", "In Use / Held"
        });

        booksTable = new JTable(booksModel);
        booksTable.setRowHeight(24);
        booksTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // HIDE book_id (COLUMN 0) - INTERNAL USE ONLY
        booksTable.removeColumn(booksTable.getColumnModel().getColumn(0));

        // SEARCH BAR
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        bookSearchField = new JTextField(18);

        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        JButton reserveButton = new JButton("Reserve Selected Book");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(bookSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        searchPanel.add(reserveButton);

        JPanel booksPanel = new JPanel(new BorderLayout(5, 5));
        booksPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
        booksPanel.add(searchPanel, BorderLayout.NORTH);
        booksPanel.add(new JScrollPane(booksTable), BorderLayout.CENTER);

        // ===== MY RESERVATIONS TABLE =====
        reservationsModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationsModel.setColumnIdentifiers(new Object[]{
            "reservation_id", "Book Title", "Date", "Status", "Admin Note"
        });

        reservationsTable = new JTable(reservationsModel);
        reservationsTable.setRowHeight(24);
        reservationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // HIDE reservation_id (COLUMN 0) - INTERNAL USE ONLY
        reservationsTable.removeColumn(
                reservationsTable.getColumnModel().getColumn(0)
        );

        JButton cancelButton = new JButton("Cancel Reservation");
        JButton borrowButton = new JButton("Borrow Book (Time In)");
        JButton returnButton = new JButton("Return Book (Time Out)");
        JButton reservationsRefreshButton = new JButton("Refresh");

        JPanel reservationsButtonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        reservationsButtonPanel.add(borrowButton);
        reservationsButtonPanel.add(returnButton);
        reservationsButtonPanel.add(cancelButton);
        reservationsButtonPanel.add(reservationsRefreshButton);

        JLabel reservationsHint = new JLabel(
                "<html>Pending = waiting for admin approval &nbsp;·&nbsp; "
                + "Approved = get the book at the library, then click Borrow"
                + "<br>Borrowed = in use &nbsp;·&nbsp; "
                + "Completed = returned</html>"
        );

        reservationsHint.setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );

        JPanel reservationsPanel = new JPanel(new BorderLayout(5, 5));

        reservationsPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
        reservationsPanel.add(
                reservationsButtonPanel, BorderLayout.NORTH
        );
        reservationsPanel.add(
                new JScrollPane(reservationsTable), BorderLayout.CENTER
        );
        reservationsPanel.add(reservationsHint, BorderLayout.SOUTH);

        // ===== USAGE TABLE =====
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.setColumnIdentifiers(new Object[]{
            "Book Title", "Date", "Time In", "Time Out", "Status"
        });

        JTable table = new JTable(model);
        table.setRowHeight(24);

        JPanel usagePanel = new JPanel(new BorderLayout(5, 5));
        usagePanel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
        usagePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== TABS =====
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Available Books", booksPanel);
        tabs.addTab("My Reservations", reservationsPanel);
        tabs.addTab("My Usage History", usagePanel);

        // REFRESH RESERVATIONS WHEN THE TAB IS OPENED
        tabs.addChangeListener(e -> {

            if (tabs.getSelectedIndex() == 1) {
                loadMyReservations();
            }
        });

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));

        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(infoPanel, BorderLayout.CENTER);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        JButton refreshButton = new JButton("Refresh");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);

        // LAYOUT
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ACTIONS
        searchButton.addActionListener(e ->
                loadBooks(bookSearchField.getText().trim())
        );

        showAllButton.addActionListener(e -> {

            bookSearchField.setText("");
            loadBooks("");
        });

        reserveButton.addActionListener(e -> reserveBook());
        borrowButton.addActionListener(e -> borrowBook());
        returnButton.addActionListener(e -> returnBook());
        cancelButton.addActionListener(e -> cancelReservation());

        reservationsRefreshButton.addActionListener(e ->
                loadMyReservations()
        );

        refreshButton.addActionListener(e -> {

            loadBooks();
            loadMyReservations();
            loadUsage();
        });

        logoutButton.addActionListener(e -> {

            memberId = -1;
            fullName = "";

            showLogin("");
        });

        loadBooks();
        loadMyReservations();
        loadUsage();

        revalidate();
        repaint();
        setVisible(true);
    }

    // SMALL BOX WITH A LABEL AND A VALUE
    public JPanel createInfo(String label, String value) {

        JPanel box = new JPanel(new GridLayout(2, 1));

        box.setBorder(
                BorderFactory.createLineBorder(Color.GRAY)
        );

        JLabel titleLabel = new JLabel(
                label, SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueLabel = new JLabel(
                value == null ? "-" : value,
                SwingConstants.CENTER
        );

        box.add(titleLabel);
        box.add(valueLabel);

        return box;
    }

    // LOAD ALL BOOKS (NO FILTER)
    public void loadBooks() {

        loadBooks(bookSearchField == null
                ? ""
                : bookSearchField.getText().trim());
    }

    // LOAD BOOKS, OPTIONALLY FILTERED BY A KEYWORD
    public void loadBooks(String keyword) {

        if (booksModel == null) {
            return;
        }

        boolean hasKeyword = keyword != null && !keyword.isEmpty();

        String sql = "SELECT book_id, title, author, category, publisher, "
                + "publication_year, quantity, available_copies "
                + "FROM books ";

        if (hasKeyword) {
            sql = sql + "WHERE title LIKE ? OR author LIKE ? "
                    + "OR category LIKE ? OR publisher LIKE ? "
                    + "OR isbn LIKE ? ";
        }

        sql = sql + "ORDER BY title";

        booksModel.setRowCount(0);

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                if (hasKeyword) {

                    String like = "%" + keyword + "%";

                    for (int i = 1; i <= 5; i++) {
                        ps.setString(i, like);
                    }
                }

                try (ResultSet result = ps.executeQuery()) {

                    while (result.next()) {

                        booksModel.addRow(new Object[]{
                            result.getInt("book_id"),
                            result.getString("title"),
                            result.getString("author"),
                            result.getString("category"),
                            result.getString("publisher"),
                            yearText(result),
                            result.getInt("quantity"),
                            result.getInt("available_copies"),
                            result.getInt("quantity")
                                    - result.getInt("available_copies")
                        });
                    }
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load books.\n" + e.getMessage()
            );
        }
    }

    // ROW INDEX IN THE MODEL (THE VIEW MAY HIDE COLUMNS)
    private int selectedModelRow(JTable table) {

        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            return -1;
        }

        return table.convertRowIndexToModel(viewRow);
    }

    // RESERVE THE SELECTED BOOK
    public void reserveBook() {

        int row = selectedModelRow(booksTable);

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this, "Please select a book first."
            );

            return;
        }

        int bookId = Integer.parseInt(
                String.valueOf(booksModel.getValueAt(row, 0))
        );

        String title = String.valueOf(
                booksModel.getValueAt(row, 1)
        );

        int available = Integer.parseInt(
                String.valueOf(booksModel.getValueAt(row, 7))
        );

        if (available < 1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Sorry, wala na sang available copies ang \""
                    + title + "\"."
            );

            return;
        }

        String checkSql = "SELECT COUNT(*) FROM reservations "
                + "WHERE member_id = ? AND book_id = ? "
                + "AND status IN ('Pending','Approved','Borrowed')";

        String insertSql = "INSERT INTO reservations "
                + "(book_id, member_id, reservation_date, status) "
                + "VALUES (?, ?, ?, 'Pending')";

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            // RE-CHECK AVAILABILITY IN THE DATABASE (THE TABLE MAY BE STALE)
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT available_copies FROM books "
                    + "WHERE book_id = ?")) {

                ps.setInt(1, bookId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()
                            || rs.getInt("available_copies") < 1) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Sorry, wala na sang available copies ang \""
                                + title + "\"."
                        );

                        return;
                    }
                }
            }

            // DUPLICATE GUARD
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {

                ps.setInt(1, memberId);
                ps.setInt(2, bookId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {

                        JOptionPane.showMessageDialog(
                                this,
                                "May reservation ka na para sa book na ito."
                        );

                        return;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(insertSql)) {

                ps.setInt(1, bookId);
                ps.setInt(2, memberId);
                ps.setDate(3, Date.valueOf(LocalDate.now()));

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Reservation sent! Wait for the admin to approve it.\n"
                    + "Once approved, get the book at the library and "
                    + "click \"Borrow Book (Time In)\"."
            );

            loadMyReservations();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not reserve the book.\n" + e.getMessage()
            );
        }
    }

    // START USING AN APPROVED BOOK (TIME IN)
    public void borrowBook() {

        int row = selectedModelRow(reservationsTable);

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this, "Please select a reservation first."
            );

            return;
        }

        int reservationId = Integer.parseInt(
                String.valueOf(reservationsModel.getValueAt(row, 0))
        );

        String shownStatus = String.valueOf(
                reservationsModel.getValueAt(row, 3)
        );

        if (!"Approved".equals(shownStatus)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only approved reservations can be borrowed.\n"
                    + "This one is " + shownStatus + "."
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Nakuha mo na ang book sa library?\n\n"
                + "Click Yes to start using it (Time In).",
                "Borrow Book",
                JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        String selectSql = "SELECT book_id, status FROM reservations "
                + "WHERE reservation_id = ? AND member_id = ? FOR UPDATE";

        String updateSql = "UPDATE reservations SET status = 'Borrowed' "
                + "WHERE reservation_id = ? AND status = 'Approved'";

        String usageSql = "INSERT INTO book_usage "
                + "(book_id, member_id, reservation_id, usage_date, "
                + "time_in, status) "
                + "VALUES (?, ?, ?, CURDATE(), CURTIME(), 'Using')";

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

                try (PreparedStatement ps =
                             con.prepareStatement(selectSql)) {

                    ps.setInt(1, reservationId);
                    ps.setInt(2, memberId);

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
                    }
                }

                if (!"Approved".equals(status)) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "This reservation is already " + status + "."
                    );

                    return;
                }

                try (PreparedStatement ps =
                             con.prepareStatement(updateSql)) {

                    ps.setInt(1, reservationId);

                    if (ps.executeUpdate() == 0) {

                        con.rollback();

                        JOptionPane.showMessageDialog(
                                this,
                                "Could not start the book usage. "
                                + "Please refresh and try again."
                        );

                        return;
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(usageSql)) {

                    ps.setInt(1, bookId);
                    ps.setInt(2, memberId);
                    ps.setInt(3, reservationId);

                    ps.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Book borrowed! Ingat sa libro.\n"
                        + "Click \"Return Book (Time Out)\" when you "
                        + "are done."
                );

                loadMyReservations();
                loadUsage();

            } catch (SQLException e) {

                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Could not borrow the book.\n" + e.getMessage()
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

    // RETURN THE BOOK (TIME OUT)
    public void returnBook() {

        int row = selectedModelRow(reservationsTable);

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this, "Please select a reservation first."
            );

            return;
        }

        int reservationId = Integer.parseInt(
                String.valueOf(reservationsModel.getValueAt(row, 0))
        );

        String shownStatus = String.valueOf(
                reservationsModel.getValueAt(row, 3)
        );

        if (!"Borrowed".equals(shownStatus)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only borrowed books can be returned.\n"
                    + "This one is " + shownStatus + "."
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Ibalik mo na ang book sa library?",
                "Return Book",
                JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        String selectSql = "SELECT book_id, status FROM reservations "
                + "WHERE reservation_id = ? AND member_id = ? FOR UPDATE";

        String usageSql = "UPDATE book_usage "
                + "SET time_out = CURTIME(), status = 'Returned' "
                + "WHERE reservation_id = ? AND member_id = ? "
                + "AND status = 'Using'";

        String updateSql = "UPDATE reservations SET status = 'Completed' "
                + "WHERE reservation_id = ? AND status = 'Borrowed'";

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
                String status;

                try (PreparedStatement ps =
                             con.prepareStatement(selectSql)) {

                    ps.setInt(1, reservationId);
                    ps.setInt(2, memberId);

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
                    }
                }

                if (!"Borrowed".equals(status)) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "This reservation is already " + status + "."
                    );

                    return;
                }

                try (PreparedStatement ps =
                             con.prepareStatement(usageSql)) {

                    ps.setInt(1, reservationId);
                    ps.setInt(2, memberId);

                    if (ps.executeUpdate() == 0) {

                        con.rollback();

                        JOptionPane.showMessageDialog(
                                this,
                                "No active book usage found. "
                                + "Please ask the admin for help."
                        );

                        return;
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(updateSql)) {

                    ps.setInt(1, reservationId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             con.prepareStatement(bookSql)) {

                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Book returned. Salamat!\n"
                        + "The copy is available again."
                );

                loadMyReservations();
                loadBooks();
                loadUsage();

            } catch (SQLException e) {

                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Could not return the book.\n" + e.getMessage()
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

    // LOAD THIS MEMBER'S RESERVATIONS
    public void loadMyReservations() {

        if (reservationsModel == null || memberId == -1) {
            return;
        }

        String sql = "SELECT r.reservation_id, b.title, "
                + "r.reservation_date, r.status, r.admin_note "
                + "FROM reservations r "
                + "JOIN books b ON r.book_id = b.book_id "
                + "WHERE r.member_id = ? "
                + "ORDER BY r.created_at DESC";

        reservationsModel.setRowCount(0);

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, memberId);

                try (ResultSet result = ps.executeQuery()) {

                    while (result.next()) {

                        reservationsModel.addRow(new Object[]{
                            result.getInt("reservation_id"),
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
                    "Could not load your reservations.\n" + e.getMessage()
            );
        }
    }

    // CANCEL A PENDING OR APPROVED RESERVATION
    public void cancelReservation() {

        int row = selectedModelRow(reservationsTable);

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this, "Please select a reservation first."
            );

            return;
        }

        int reservationId = Integer.parseInt(
                String.valueOf(reservationsModel.getValueAt(row, 0))
        );

        String shownStatus = String.valueOf(
                reservationsModel.getValueAt(row, 3)
        );

        if ("Declined".equals(shownStatus)
                || "Cancelled".equals(shownStatus)) {

            JOptionPane.showMessageDialog(
                    this,
                    "This reservation is already " + shownStatus + "."
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Cancel this reservation?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        String selectSql = "SELECT book_id, status FROM reservations "
                + "WHERE reservation_id = ? AND member_id = ? FOR UPDATE";

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

                try (PreparedStatement ps =
                             con.prepareStatement(selectSql)) {

                    ps.setInt(1, reservationId);
                    ps.setInt(2, memberId);

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
                    }
                }

                if (!"Pending".equals(status)
                        && !"Approved".equals(status)) {

                    con.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "This reservation is already " + status + "."
                    );

                    return;
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE reservations SET status = 'Cancelled' "
                        + "WHERE reservation_id = ? "
                        + "AND status IN ('Pending','Approved')")) {

                    ps.setInt(1, reservationId);

                    if (ps.executeUpdate() == 0) {

                        con.rollback();

                        JOptionPane.showMessageDialog(
                                this,
                                "Could not cancel this reservation. "
                                + "Please refresh and try again."
                        );

                        return;
                    }
                }

                // GIVE BACK THE HELD COPY
                if ("Approved".equals(status)) {

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE books SET available_copies = "
                            + "LEAST(quantity, available_copies + 1) "
                            + "WHERE book_id = ?")) {

                        ps.setInt(1, bookId);
                        ps.executeUpdate();
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this, "Reservation cancelled."
                );

                loadMyReservations();
                loadBooks();

            } catch (SQLException e) {

                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Could not cancel the reservation.\n"
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

    // YEAR COLUMNS COME BACK AS A DATE (2024-01-01); KEEP ONLY 2024
    private String yearText(ResultSet result) throws SQLException {

        String year = result.getString("publication_year");

        if (year != null && year.length() > 4) {
            year = year.substring(0, 4);
        }

        return year;
    }

    // LOAD THIS MEMBER'S BOOK USAGE
    public void loadUsage() {

        if (model == null || memberId == -1) {
            return;
        }

        String sql = "SELECT b.title AS title, "
                + "u.usage_date AS usage_date, "
                + "u.time_in AS time_in, "
                + "u.time_out AS time_out, "
                + "u.status AS status "
                + "FROM book_usage u "
                + "JOIN books b ON u.book_id = b.book_id "
                + "WHERE u.member_id = ? "
                + "ORDER BY u.usage_date DESC, u.time_in DESC";

        model.setRowCount(0);

        try (Connection con = database_connection.connect()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed!"
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, memberId);

                try (ResultSet result = ps.executeQuery()) {

                    while (result.next()) {

                        model.addRow(new Object[]{
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
                    "Could not load your book usage.\n" + e.getMessage()
            );
        }
    }

    // CLEAR THE SIGN UP FORM
    public void clearSignupFields() {

        signupStudentId.setText("");
        signupFullName.setText("");
        signupCourse.setText("");
        signupYearLevel.setText("");
        signupSection.setText("");
        signupContact.setText("");
        signupAddress.setText("");
        signupPassword.setText("");
        signupConfirm.setText("");
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

    // RUN WINDOW ALONE
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new members());
    }
}
