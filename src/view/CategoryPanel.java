package view;

import dao.CategoryDAO;
import model.Category;
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

public class CategoryPanel extends JPanel implements Refreshable {
    private CategoryDAO dao = new CategoryDAO();
    private List<Category> categories = new ArrayList<>();   // same order as the table rows

    private JTextField txtName = new JTextField(20);
    private DefaultTableModel model = UI.readOnlyModel("ID", "Category Name");
    private JTable table = new JTable(model);

    public CategoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Category name:"));
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
                txtName.setText(categories.get(row).getCategoryName());
            }
        });

        btnAdd.addActionListener(e -> addCategory());
        btnUpdate.addActionListener(e -> updateCategory());
        btnDelete.addActionListener(e -> deleteCategory());
        btnClear.addActionListener(e -> clearForm());

        refresh();
    }

    @Override
    public void refresh() {
        try {
            categories = dao.getAll();
            model.setRowCount(0);
            for (Category c : categories) {
                model.addRow(new Object[] { c.getCategoryId(), c.getCategoryName() });
            }
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void addCategory() {
        try {
            Category c = new Category();
            c.setCategoryName(txtName.getText());
            dao.add(c);
            clearForm();
            refresh();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Could not add. The category may already exist.\n" + ex.getMessage());
        }
    }

    private void updateCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select a category in the table first.");
            return;
        }
        try {
            Category c = categories.get(row);
            c.setCategoryName(txtName.getText());
            dao.update(c);
            clearForm();
            refresh();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void deleteCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select a category in the table first.");
            return;
        }
        if (!UI.confirm("Delete this category?")) {
            return;
        }
        try {
            dao.delete(categories.get(row).getCategoryId());
            clearForm();
            refresh();
        } catch (SQLException ex) {
            UI.error("Cannot delete. Some books still use this category.");
        }
    }

    private void clearForm() {
        txtName.setText("");
        table.clearSelection();
    }
}
