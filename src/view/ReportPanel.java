package view;

import report.CurrentlyBorrowedReport;
import report.MostBorrowedReport;
import report.OverdueReport;
import report.Report;
import util.UI;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;

public class ReportPanel extends JPanel implements Refreshable {
    // The list holds Report objects of different types. We only use the Report interface.
    private JComboBox<Report> cboReport = new JComboBox<>(new Report[] {
            new CurrentlyBorrowedReport(), new OverdueReport(), new MostBorrowedReport() });

    private DefaultTableModel model = UI.readOnlyModel();
    private JTable table = new JTable(model);

    public ReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Choose a report:"));
        top.add(cboReport);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        cboReport.addActionListener(e -> refresh());
        refresh();
    }

    @Override
    public void refresh() {
        Report report = (Report) cboReport.getSelectedItem();
        try {
            model.setColumnIdentifiers(report.getColumns());   // POLYMORPHISM: each report gives its own columns
            model.setRowCount(0);
            for (Object[] row : report.getRows()) {            // ...and its own rows
                model.addRow(row);
            }
        } catch (SQLException ex) {
            UI.error("Database error: " + ex.getMessage());
        }
    }
}
