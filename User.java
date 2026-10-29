// User.java
// Base class for system users. Demonstrates encapsulation (private fields)
// and inheritance (Admin and Member extend this class).
// This is a new separate file — it does not change admin.java or members.java.

public class User {
    private int id;
    private String username;
    private String passwordHash;   // never store plain text password

    public User() {
        this.id = -1;
        this.username = "";
        this.passwordHash = "";
    }

    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User[id=" + id + ", username=" + username + "]";
    }
}
