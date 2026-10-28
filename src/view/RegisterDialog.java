package view;

import dao.BorrowerDAO;
import dao.UserDAO;
import model.Borrower;
import model.BorrowerUser;
import model.User;
import util.UI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

// Borrower registration: creates a login account + a borrower record.
public class RegisterDialog extends JDialog {
    private JTextField txtFullName = new JTextField();
    private JTextField txtUsername = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();
    private JComboBox<String> cboType = new JComboBox<>(new String[] { "STUDENT", "FACULTY" });
    private JTextField txtContact = new JTextField();
    private JTextField txtAddress = new JTextField();

    public RegisterDialog(JFrame parent) {
        super(parent, "Borrower Registration", true);   // true = modal (blocks the login window)
        setSize(380, 330);
        setLocationRelativeTo(parent);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        form.add(new JLabel("Full name:"));      form.add(txtFullName);
        form.add(new JLabel("Username:"));       form.add(txtUsername);
        form.add(new JLabel("Password:"));       form.add(txtPassword);
        form.add(new JLabel("I am a:"));         form.add(cboType);
        form.add(new JLabel("Contact no.:"));    form.add(txtContact);
        form.add(new JLabel("Address:"));        form.add(txtAddress);

        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(btnRegister);
        buttons.add(btnCancel);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        btnRegister.addActionListener(e -> register());
        btnCancel.addActionListener(e -> dispose());
    }

    private void register() {
        try {
            User user = new BorrowerUser();
            user.setUsername(txtUsername.getText());
            user.setPassword(new String(txtPassword.getPassword()));
            user.setFullName(txtFullName.getText());

            Borrower borrower = BorrowerDAO.create((String) cboType.getSelectedItem());
            borrower.setFullName(txtFullName.getText());
            borrower.setContact(txtContact.getText().trim());
            borrower.setAddress(txtAddress.getText().trim());

            new UserDAO().registerBorrower(user, borrower);
            UI.info("Registered! You can now log in.");
            dispose();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());          // validation message from the setters
        } catch (SQLException ex) {
            UI.error("Could not register. The username may already be taken.\n" + ex.getMessage());
        }
    }
}
