package view;

import dao.UserDAO;
import model.User;
import util.UI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

// START THE PROGRAM HERE: run this file.
public class LoginFrame extends JFrame {
    private JTextField txtUsername = new JTextField(15);
    private JPasswordField txtPassword = new JPasswordField(15);
    private JButton btnLogin = new JButton("Login");
    private JButton btnRegister = new JButton("Register as Borrower");
    private UserDAO userDao = new UserDAO();

    public LoginFrame() {
        setTitle("LibroTrack - Login");
        setSize(380, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("LibroTrack Library System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        form.add(new JLabel("Username:"));
        form.add(txtUsername);
        form.add(new JLabel("Password:"));
        form.add(txtPassword);

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(btnLogin);
        buttons.add(btnRegister);

        setLayout(new BorderLayout());
        add(lblTitle, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnLogin);   

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> new RegisterDialog(this).setVisible(true));
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        try {
            User user = userDao.login(username, password);
            if (user == null) {
                UI.error("Wrong username or password.");
                return;
            }
            dispose();              // close the login window
            user.openDashboard();   // POLYMORPHISM: Admin, Librarian and BorrowerUser each open a different screen
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
