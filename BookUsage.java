// BookUsage.java
// Represents a book usage record (time-in / time-out).
// This is a new separate file — it does not change admin.java or members.java.

import java.time.LocalDate;
import java.time.LocalTime;

public class BookUsage {
    private int usageId;
    private int bookId;
    private int memberId;
    private Integer reservationId;   // can be null
    private LocalDate usageDate;
    private LocalTime timeIn;
    private LocalTime timeOut;       // null until returned
    private String status;           // "Using" or "Returned"

    public BookUsage() {
        this.usageId = -1;
        this.bookId = -1;
        this.memberId = -1;
        this.reservationId = null;
        this.usageDate = LocalDate.now();
        this.timeIn = null;
        this.timeOut = null;
        this.status = "Using";
    }

    public BookUsage(int usageId, int bookId, int memberId,
                     Integer reservationId, LocalDate usageDate,
                     LocalTime timeIn, LocalTime timeOut, String status) {
        this.usageId = usageId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.reservationId = reservationId;
        this.usageDate = usageDate;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.status = status;
    }

    public int getUsageId() {
        return usageId;
    }

    public void setUsageId(int usageId) {
        this.usageId = usageId;
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

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isUsing() {
        return "Using".equalsIgnoreCase(status);
    }

    public boolean isReturned() {
        return "Returned".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "BookUsage[id=" + usageId + ", bookId=" + bookId
                + ", memberId=" + memberId + ", status=" + status + "]";
    }
}
