package report;

import dao.TransactionDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MostBorrowedReport implements Report {
    @Override
    public String getName() { return "Most Borrowed Books"; }

    @Override
    public String[] getColumns() {
        return new String[] { "Rank", "Book", "Times Borrowed" };
    }

    @Override
    public List<Object[]> getRows() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        int rank = 1;
        for (Object[] row : new TransactionDAO().getMostBorrowed()) {
            rows.add(new Object[] { rank, row[0], row[1] });
            rank++;
        }
        return rows;
    }

    @Override
    public String toString() { return getName(); }
}
