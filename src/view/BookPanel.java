package view;

import dao.AuthorDAO;
import dao.BookDAO;
import dao.CategoryDAO;
import model.Author;
import model.Book;
import model.Category;
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

// Used by TWO roles:
//   new BookPanel(true)  -> Librarian: search + add/edit/delete
//   new BookPanel(false) -> Borrower : search only (the form is hidden)
public class BookPanel extends JPanel implements Refreshable {
    private BookDAO bookDao = new BookDAO();
    private AuthorDAO authorDao = new AuthorDAO();
    private CategoryDAO categoryDao = new CategoryDAO();
    private boolean canEdit;
    private List<Book> books = new ArrayList<>();   // same order as the table rows

    // Search controls
    private JComboBox<String> cboSearchBy = new JComboBox<>(new String[] { "Title", "Author", "ISBN", "Category" });
    private JTextField txtSearch = new JTextField(20);

    // Form fields
    private JTextField txtIsbn = new JTextField();
    private JTextField txtTitle = new JTextField();
    private JComboBox<Author> cboAuthor = new JComboBox<>();
    private JComboBox<Category> cboCategory = new JComboBox<>();
    private JTextField txtPublisher = new JTextField();
    private JTextField txtYear = new JTextField();
    private JTextField txtCopies = new JTextField();
    private JTextField txtShelf = new JTextField();

    private DefaultTableModel model = UI.readOnlyModel("ID", "ISBN", "Title", "Author", "Category",
            "Publisher", "Year", "Total Copies", "Available", "Shelf");
    private JTable table = new JTable(model);

    public BookPanel(boolean canEdit) {
        this.canEdit = canEdit;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ----- Search bar + table (center) -----
        JButton btnSearch = new JButton("Search");
        JButton btnShowAll = new JButton("Show All");
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Search by:"));
        searchBar.add(cboSearchBy);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnShowAll);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(searchBar, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> searchBooks());
        btnShowAll.addActionListener(e -> { txtSearch.setText(""); loadBooks(); });

        // ----- Form + buttons (top) - only for the Librarian -----
        if (canEdit) {
            add(buildFormPanel(), BorderLayout.NORTH);

            table.getSelectionModel().addListSelectionListener(e -> {
                int row = table.getSelectedRow();
                if (!e.getValueIsAdjusting() && row >= 0) {
                    fillForm(books.get(row));
                }
            });
        }

        refresh();
    }

    private JPanel buildFormPanel() {
        // 4 rows x 4 columns (label, field, label, field)
        JPanel form = new JPanel(new GridLayout(4, 4, 8, 8));
        form.add(new JLabel("ISBN:"));            form.add(txtIsbn);
        form.add(new JLabel("Title:"));           form.add(txtTitle);
        form.add(new JLabel("Author:"));          form.add(cboAuthor);
        form.add(new JLabel("Category:"));        form.add(cboCategory);
        form.add(new JLabel("Publisher:"));       form.add(txtPublisher);
        form.add(new JLabel("Year published:"));  form.add(txtYear);
        form.add(new JLabel("Number of copies:")); form.add(txtCopies);
        form.add(new JLabel("Shelf location:"));  form.add(txtShelf);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearForm());

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(form, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    // ---------------- Loading data ----------------

    @Override
    public void refresh() {
        if (canEdit) {
            loadCombos();    // authors/categories may have changed in another tab
        }
        loadBooks();
    }

    private void loadCombos() {
        try {
            cboAuthor.removeAllItems();
            for (Author a : authorDao.getAll()) {
                cboAuthor.addItem(a);
            }
            cboCategory.removeAllItems();
            for (Category c : categoryDao.getAll()) {
                cboCategory.addItem(c);
            }
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void loadBooks() {
        try {
            showInTable(bookDao.getAll());
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void searchBooks() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }
        try {
            showInTable(bookDao.search((String) cboSearchBy.getSelectedItem(), keyword));
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }

    private void showInTable(List<Book> list) {
        books = list;
        model.setRowCount(0);
        for (Book b : books) {
            model.addRow(new Object[] { b.getBookId(), b.getIsbn(), b.getTitle(), b.getAuthorName(),
                    b.getCategoryName(), b.getPublisher(), b.getYearPublished(),
                    b.getTotalCopies(), b.getAvailableCopies(), b.getShelfLocation() });
        }
    }

    // ---------------- Form helpers ----------------

    // Copies the selected book into the form fields
    private void fillForm(Book b) {
        txtIsbn.setText(b.getIsbn());
        txtTitle.setText(b.getTitle());
        txtPublisher.setText(b.getPublisher());
        txtYear.setText(String.valueOf(b.getYearPublished()));
        txtCopies.setText(String.valueOf(b.getTotalCopies()));
        txtShelf.setText(b.getShelfLocation());
        for (int i = 0; i < cboAuthor.getItemCount(); i++) {
            if (cboAuthor.getItemAt(i).getAuthorId() == b.getAuthorId()) {
                cboAuthor.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < cboCategory.getItemCount(); i++) {
            if (cboCategory.getItemAt(i).getCategoryId() == b.getCategoryId()) {
                cboCategory.setSelectedIndex(i);
            }
        }
    }

    // Builds a Book from the form. The setters check the values (encapsulation).
    private Book readForm() {
        Author author = (Author) cboAuthor.getSelectedItem();
        Category category = (Category) cboCategory.getSelectedItem();
        if (author == null || category == null) {
            throw new IllegalArgumentException("Add an author and a category first.");
        }
        Book b = new Book();
        b.setIsbn(txtIsbn.getText());
        b.setTitle(txtTitle.getText());
        b.setAuthorId(author.getAuthorId());
        b.setCategoryId(category.getCategoryId());
        b.setPublisher(txtPublisher.getText().trim());
        b.setYearPublished(Integer.parseInt(txtYear.getText().trim()));
        b.setTotalCopies(Integer.parseInt(txtCopies.getText().trim()));
        b.setShelfLocation(txtShelf.getText().trim());
        return b;
    }

    private void clearForm() {
        txtIsbn.setText("");
        txtTitle.setText("");
        txtPublisher.setText("");
        txtYear.setText("");
        txtCopies.setText("");
        txtShelf.setText("");
        table.clearSelection();
    }

    // ---------------- Buttons ----------------

    private void addBook() {
        try {
            Book b = readForm();
            b.setAvailableCopies(b.getTotalCopies());   // a new book has all copies available
            bookDao.add(b);
            clearForm();
            loadBooks();
        } catch (NumberFormatException ex) {
            UI.error("Year and number of copies must be whole numbers.");
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Could not add the book. The ISBN may already exist.\n" + ex.getMessage());
        }
    }

    private void updateBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select a book in the table first.");
            return;
        }
        try {
            Book b = readForm();
            b.setBookId(books.get(row).getBookId());
            bookDao.update(b);
            clearForm();
            loadBooks();
        } catch (NumberFormatException ex) {
            UI.error("Year and number of copies must be whole numbers.");
        } catch (IllegalArgumentException ex) {
            UI.error(ex.getMessage());
        } catch (SQLException ex) {
            UI.error("Could not update the book. The ISBN may already exist.\n" + ex.getMessage());
        }
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UI.error("Select a book in the table first.");
            return;
        }
        if (!UI.confirm("Delete this book?")) {
            return;
        }
        try {
            bookDao.delete(books.get(row).getBookId());
            clearForm();
            loadBooks();
        } catch (SQLException ex) {
            UI.error("Cannot delete. This book has borrowing records.");
        }
    }
}
