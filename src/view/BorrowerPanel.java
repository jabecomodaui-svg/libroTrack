package view;

import dao.BorrowerDAO;
import model.Borrower;
import util.UI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

public class BorrowerPanel extends JPanel implements Refreshable {
    private BorrowerDAO dao = new BorrowerDAO();
    private List<Borrower> borrowers = new ArrayList<>();   // same order as the table rows

    private JTextField txtName = new JTextField();
    private JComboBox<String> cboType = new JComboBox<>(new String[] { "STUDENT", "FACULTY" });
    private JTextField txtContact = new JTextField();
    private JTextField txtAddress = new JTextField();

    private DefaultTableModel model = UI.readOnlyModel("ID", "Full Name", "Type", "Borrow Limit", "Contact", "Address");
    private JTable table = new JTable(model);

    public BorrowerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Form: 2 rows x 4 columns (label, field, label, field)
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.add(new JLabel("Full name:"));   form.add(txtName);
        form.add(new JLabel("Type:"));        form.add(cboType);
        form.add(new JLabel("Contact:"));     form.add(txtContact);
        form.add(new JLabel("Address:"));     form.add(txtAddress);

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
                Borrower b = borrowers.get(row);
                txtName.setText(b.getFullName());
                cboType.setSelectedItem(b.getBorrowerType());
                txtContact.setText(b.getContact());
                txtAddress.setText(b.getAddress());
            }
        });

        btnAdd.addActionListener(e -> addBorrower());
        btnUpdate.addActionListener(e -> updateBorrower());
        btnDelete.addActionListener(e -> deleteBorrower());
        btnClear.addActionListener(e -> clearForm());

        refresh();
    }

    @Override
    public void refresh() {
        try {
            borrowers = dao.getAll();
            model.setRowCount(0);
            for (Borrower b : borrowers) {
                // b.getBorrowLimit() gives 3 for a Student and 5 for a Faculty (polymorphism)
                model.addRow(new Object[] { b.getBorrowerId(), b.getFullName(), b.getBorrowerType(),
                                            b.getBorrowLimit(), b.getContact(), b.getAddress() });
            }
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    // Builds a Student or Faculty from what is typed in the form
    private Borrower readForm() {
        Borrower b = BorrowerDAO.create((String) cboType.getSelectedItem());
        b.setFullName(txtName.getText());
        b.setContact(txtContact.getText().trim());
        b.setAddress(txtAddress.getText().trim());
        return b;
    }

    private void addBorrower() {
        try {
            dao.add(readForm());
            clearForm();
            refresh();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void updateBorrower() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select a borrower in the table first.");
            return;
        }
        try {
            Borrower b = readForm();
            b.setBorrowerId(borrowers.get(row).getBorrowerId());
            dao.update(b);
            clearForm();
            refresh();
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void deleteBorrower() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select a borrower in the table first.");
            return;
        }
        if (!UI.confirm("Delete this borrower?")) {
            return;
        }
        try {
            dao.delete(borrowers.get(row).getBorrowerId());
            clearForm();
            refresh();
        } catch (SQLException ex) {
            UI.error("Cannot delete. This borrower has borrowing records.");
        }
    }

    private void clearForm() {
        txtName.setText("");
        cboType.setSelectedIndex(0);
        txtContact.setText("");
        txtAddress.setText("");
        table.clearSelection();
    }
}
