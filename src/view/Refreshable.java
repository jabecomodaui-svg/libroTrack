package view;

// Any screen that can reload its data implements this.
// MainFrame calls refresh() every time the user opens a tab.
public interface Refreshable {
    void refresh();
}
