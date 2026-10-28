package view;

import dao.AuthorDAO;
import model.Author;
import util.UI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthorPanel extends JPanel implements Refreshable {
    private AuthorDAO dao = new AuthorDAO();
    private List<Author> authors = new ArrayList<>();   // same order as the table rows

    private JTextField txtName = new JTextField(20);
    private DefaultTableModel model = UI.readOnlyModel("ID", "Author Name");
    private JTable table = new JTable(model);

    public AuthorPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Author name:"));
        top.add(txtName);
        top.add(btnAdd);
        top.add(btnUpdate);
        top.add(btnDelete);
        top.add(btnClear);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // When a row is clicked, show it in the text field
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                txtName.setText(authors.get(row).getAuthorName());
            }
        });

        btnAdd.addActionListener(e -> addAuthor());
        btnUpdate.addActionListener(e -> updateAuthor());
        btnDelete.addActionListener(e -> deleteAuthor());
        btnClear.addActionListener(e -> clearForm());

        refresh();
    }

    @Override
    public void refresh() {
        try {
            authors = dao.getAll();
            model.setRowCount(0);
            for (Author c : authors) {
                model.addRow(new Object[] { c.getAuthorId(), c.getAuthorName() });
            }
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void addAuthor() {
        try {
            Author c = new Author();
            c.setAuthorName(txtName.getText());
            dao.add(c);
            clearForm();
            refresh();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Could not add. The author may already exist.\n" + ex.getMessage());
        }
    }

    private void updateAuthor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select an author in the table first.");
            return;
        }
        try {
            Author c = authors.get(row);
            c.setAuthorName(txtName.getText());
            dao.update(c);
            clearForm();
            refresh();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void deleteAuthor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select an author in the table first.");
            return;
        }
        if (!UI.confirm("Delete this author?")) {
            return;
        }
        try {
            dao.delete(authors.get(row).getAuthorId());
            clearForm();
            refresh();
        } catch (SQLException ex) {
            UI.error("Cannot delete. Some books still use this author.");
        }
    }

    private void clearForm() {
        txtName.setText("");
        table.clearSelection();
    }
}
