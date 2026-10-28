package model;

import view.AuthorPanel;
import view.BookPanel;
import view.BorrowReturnPanel;
import view.BorrowerPanel;
import view.CategoryPanel;
import view.MainFrame;
import view.ReportPanel;

public class Librarian extends User {
    @Override
    public String getRole() { return "LIBRARIAN"; }

    // The Librarian sees the working screens of the library
    @Override
    public void openDashboard() {
        MainFrame frame = new MainFrame(this);
        frame.addTab("Books", new BookPanel(true));
        frame.addTab("Authors", new AuthorPanel());
        frame.addTab("Categories", new CategoryPanel());
        frame.addTab("Borrowers", new BorrowerPanel());
        frame.addTab("Borrow / Return", new BorrowReturnPanel());
        frame.addTab("Reports", new ReportPanel());
        frame.setVisible(true);
    }
}
