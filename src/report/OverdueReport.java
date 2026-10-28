package report;

import dao.TransactionDAO;
import model.BorrowTransaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OverdueReport implements Report {
    @Override
    public String getName() { return "Overdue Books"; }

    @Override
    public String[] getColumns() {
        return new String[] { "Borrower", "Book", "Due Date", "Days Late", "Fine So Far (PHP)" };
    }

    @Override
    public List<Object[]> getRows() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        for (BorrowTransaction t : new TransactionDAO().getOverdue()) {
            rows.add(new Object[] { t.getBorrowerName(), t.getBookTitle(), t.getDueDate(),
                                    t.getDaysLate(), String.format("%.2f", t.computeFine()) });
        }
        return rows;
    }

    @Override
    public String toString() { return getName(); }
}
