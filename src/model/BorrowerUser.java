package model;

import view.BookPanel;
import view.MainFrame;

public class BorrowerUser extends User {
    @Override
    public String getRole() { return "BORROWER"; }

    // A borrower can only search books (read-only)
    @Override
    public void openDashboard() {
        MainFrame frame = new MainFrame(this);
        frame.addTab("Search Books", new BookPanel(false));
        frame.setVisible(true);
    }
}
