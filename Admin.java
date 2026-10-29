// Admin.java
// Represents a library administrator. Extends User (inheritance).
// This is a new separate file — it does not change the existing admin.java GUI.

public class Admin extends User {
    private String fullName;

    public Admin() {
        super();
        this.fullName = "";
    }

    public Admin(int id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash);
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Admin[id=" + getId() + ", username=" + getUsername()
                + ", fullName=" + fullName + "]";
    }
}
