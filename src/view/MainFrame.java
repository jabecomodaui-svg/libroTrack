package view;

import model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;

// The main window after login. Each role adds its own tabs (see openDashboard in the model classes).
public class MainFrame extends JFrame {
    private JTabbedPane tabs = new JTabbedPane();

    public MainFrame(User user) {
        setTitle("LibroTrack - Library Management System");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Top bar: welcome text + logout button
        JLabel lblWelcome = new JLabel("Welcome, " + user.getFullName() + "  (" + user.getRole() + ")");
        JButton btnLogout = new JButton("Logout");
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        header.add(lblWelcome, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        // Reload the data of a tab whenever it is opened
        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();
            if (selected instanceof Refreshable) {
                ((Refreshable) selected).refresh();
            }
        });
    }

    public void addTab(String title, JPanel panel) {
        tabs.addTab(title, panel);
    }
}
