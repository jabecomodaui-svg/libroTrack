// Reservation.java
// Represents a book reservation request.
// This is a new separate file — it does not change admin.java or members.java.

import java.time.LocalDate;

public class Reservation {
    private int reservationId;
    private int bookId;
    private int memberId;
    private LocalDate reservationDate;
    private String status;   // Pending, Approved, Borrowed, Declined, Cancelled, Completed
    private String adminNote;

    public Reservation() {
        this.reservationId = -1;
        this.bookId = -1;
        this.memberId = -1;
        this.reservationDate = LocalDate.now();
        this.status = "Pending";
        this.adminNote = "";
    }

    public Reservation(int reservationId, int bookId, int memberId,
                       LocalDate reservationDate, String status, String adminNote) {
        this.reservationId = reservationId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.reservationDate = reservationDate;
        this.status = status;
        this.adminNote = adminNote;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return "Approved".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Reservation[id=" + reservationId + ", bookId=" + bookId
                + ", memberId=" + memberId + ", status=" + status + "]";
    }
}
