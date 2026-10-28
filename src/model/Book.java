package model;

import java.time.Year;

public class Book {
    private int bookId;
    private String isbn;
    private String title;
    private int authorId;
    private int categoryId;
    private String publisher;
    private int yearPublished;
    private int totalCopies;
    private int availableCopies;
    private String shelfLocation;

    // Display-only fields (filled by a JOIN query)
    private String authorName;
    private String categoryName;

    public Book() { }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty.");
        }
        this.isbn = isbn.trim();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.title = title.trim();
    }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getYearPublished() { return yearPublished; }
    public void setYearPublished(int yearPublished) {
        if (yearPublished < 1000 || yearPublished > Year.now().getValue()) {
            throw new IllegalArgumentException("Enter a valid year (1000 up to this year).");
        }
        this.yearPublished = yearPublished;
    }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Copies cannot be negative.");
        }
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative.");
        }
        this.availableCopies = availableCopies;
    }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public boolean isAvailable() { return availableCopies > 0; }

    @Override
    public String toString() { return title + " (" + availableCopies + " available)"; }
}
