package view;

import dao.UserDAO;
import model.Librarian;
import model.User;
import util.UI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// ADMIN ONLY: add, edit, delete and view librarian accounts.
public class LibrarianPanel extends JPanel implements Refreshable {
    private UserDAO dao = new UserDAO();
    private List<User> librarians = new ArrayList<>();   // same order as the table rows

    private JTextField txtFullName = new JTextField();
    private JTextField txtUsername = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();

    private DefaultTableModel model = UI.readOnlyModel("ID", "Full Name", "Username");
    private JTable table = new JTable(model);

    public LibrarianPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel form = new JPanel(new GridLayout(1, 6, 8, 8));
        form.add(new JLabel("Full name:"));  form.add(txtFullName);
        form.add(new JLabel("Username:"));   form.add(txtUsername);
        form.add(new JLabel("Password:"));   form.add(txtPassword);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                User u = librarians.get(row);
                txtFullName.setText(u.getFullName());
                txtUsername.setText(u.getUsername());
                txtPassword.setText(u.getPassword());
            }
        });

        btnAdd.addActionListener(e -> addLibrarian());
        btnUpdate.addActionListener(e -> updateLibrarian());
        btnDelete.addActionListener(e -> deleteLibrarian());
        btnClear.addActionListener(e -> clearForm());

        refresh();
    }

    @Override
    public void refresh() {
        try {
            librarians = dao.getLibrarians();
            model.setRowCount(0);
            for (User u : librarians) {
                model.addRow(new Object[] { u.getUserId(), u.getFullName(), u.getUsername() });
            }
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private User readForm() {
        User u = new Librarian();
        u.setFullName(txtFullName.getText());
        u.setUsername(txtUsername.getText());
        u.setPassword(new String(txtPassword.getPassword()));
        return u;
    }

    private void addLibrarian() {
        try {
            dao.addUser(readForm());
            clearForm();
            refresh();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Could not add. The username may already be taken.");
        }
    }

    private void updateLibrarian() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select a librarian in the table first.");
            return;
        }
        try {
            User u = readForm();
            u.setUserId(librarians.get(row).getUserId());
            dao.updateUser(u);
            clearForm();
            refresh();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Could not update. The username may already be taken.");
        }
    }

    private void deleteLibrarian() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select a librarian in the table first.");
            return;
        }
        if (!UI.confirm("Delete this librarian account?")) {
            return;
        }
        try {
            dao.deleteUser(librarians.get(row).getUserId());
            clearForm();
            refresh();
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtFullName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        table.clearSelection();
    }
}
