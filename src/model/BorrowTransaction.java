package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowTransaction {
    public static final int LOAN_DAYS = 7;          // books are due after 7 days
    public static final double FINE_PER_DAY = 5.0;  // 5 pesos for every late day

    private int transactionId;
    private int borrowerId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;     // null = the book has not been returned yet
    private double fineAmount;

    // Display-only fields (filled by a JOIN query)
    private String borrowerName;
    private String bookTitle;

    public BorrowTransaction() { }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getBorrowerId() { return borrowerId; }
    public void setBorrowerId(int borrowerId) { this.borrowerId = borrowerId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) {
        if (borrowDate != null && dueDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Due date cannot be before borrow date.");
        }
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) {
        if (fineAmount < 0) {
            throw new IllegalArgumentException("Fine cannot be negative.");
        }
        this.fineAmount = fineAmount;
    }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    // ---------- Business logic: overdue detection and fine ----------

    public boolean isReturned() { return returnDate != null; }

    // Overdue = not returned yet AND today is after the due date
    public boolean isOverdue() {
        return !isReturned() && LocalDate.now().isAfter(dueDate);
    }

    // Days late = (return date, or today if not yet returned) minus due date
    public long getDaysLate() {
        LocalDate endDate = isReturned() ? returnDate : LocalDate.now();
        long days = ChronoUnit.DAYS.between(dueDate, endDate);
        return Math.max(days, 0);
    }

    // Fine = days late x fine per day
    public double computeFine() {
        return getDaysLate() * FINE_PER_DAY;
    }
}
