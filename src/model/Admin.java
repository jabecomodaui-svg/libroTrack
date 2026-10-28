package model;

import view.LibrarianPanel;
import view.MainFrame;
import view.ReportPanel;

public class Admin extends User {
    @Override
    public String getRole() { return "ADMIN"; }

    // The Admin sees: librarian accounts + reports
    @Override
    public void openDashboard() {
        MainFrame frame = new MainFrame(this);
        frame.addTab("Librarian Accounts", new LibrarianPanel());
        frame.addTab("Reports", new ReportPanel());
        frame.setVisible(true);
    }
}
