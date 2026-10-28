package model;

public class Student extends Borrower {
    @Override
    public int getBorrowLimit() { return 3; }

    @Override
    public String getBorrowerType() { return "STUDENT"; }
}
