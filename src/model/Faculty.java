package model;

public class Faculty extends Borrower {
    @Override
    public int getBorrowLimit() { return 5; }

    @Override
    public String getBorrowerType() { return "FACULTY"; }
}
