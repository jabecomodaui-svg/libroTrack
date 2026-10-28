package report;

import dao.TransactionDAO;
import model.BorrowTransaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrentlyBorrowedReport implements Report {
    @Override
    public String getName() { return "Currently Borrowed Books"; }

    @Override
    public String[] getColumns() {
        return new String[] { "Borrower", "Book", "Borrowed On", "Due Date", "Status" };
    }

    @Override
    public List<Object[]> getRows() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        for (BorrowTransaction t : new TransactionDAO().getCurrentlyBorrowed()) {
            String status = t.isOverdue() ? "OVERDUE" : "On time";
            rows.add(new Object[] { t.getBorrowerName(), t.getBookTitle(),
                                    t.getBorrowDate(), t.getDueDate(), status });
        }
        return rows;
    }

    @Override
    public String toString() { return getName(); }
}
