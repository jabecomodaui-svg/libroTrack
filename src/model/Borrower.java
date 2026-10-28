package model;

// PARENT class. Student and Faculty extend it.
public abstract class Borrower {
    private int borrowerId;
    private String fullName;
    private String contact;
    private String address;

    public Borrower() { }

    public int getBorrowerId() { return borrowerId; }
    public void setBorrowerId(int borrowerId) { this.borrowerId = borrowerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.fullName = fullName.trim();
    }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Every child MUST define these (this is what makes polymorphism work)
    public abstract int getBorrowLimit();
    public abstract String getBorrowerType();

    @Override
    public String toString() { return fullName + " (" + getBorrowerType() + ")"; }
}
