// Book.java
// Represents a library book. Demonstrates encapsulation (private fields
// with controlled access) and behavior (borrowCopy / returnCopy).
// This is a new separate file — it does not change admin.java or members.java.

public class Book {
    private int bookId;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private int publicationYear;
    private int quantity;
    private int availableCopies;

    public Book() {
        this.bookId = -1;
        this.isbn = "";
        this.title = "";
        this.author = "";
        this.category = "";
        this.publisher = "";
        this.publicationYear = 0;
        this.quantity = 1;
        this.availableCopies = 1;
    }

    public Book(int bookId, String isbn, String title, String author,
                String category, String publisher, int publicationYear,
                int quantity, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.quantity = quantity;
        this.availableCopies = availableCopies;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    // Controlled access: only decrement if a copy is available.
    public boolean borrowCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    // Controlled access: never exceed total quantity.
    public void returnCopy() {
        if (availableCopies < quantity) {
            availableCopies++;
        }
    }

    @Override
    public String toString() {
        return "Book[id=" + bookId + ", title=" + title
                + ", author=" + author + ", available=" + availableCopies + "]";
    }
}
