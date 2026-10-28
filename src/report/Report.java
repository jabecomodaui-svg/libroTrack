package report;

import java.sql.SQLException;
import java.util.List;

// An INTERFACE. Each report type implements it in its own way.
public interface Report {
    String getName();                              // shown in the drop-down list
    String[] getColumns();                         // table headings
    List<Object[]> getRows() throws SQLException;  // table data
}
