# LibroTrack — OOP Documentation

Object-Oriented Programming principles explained using the actual code in this
project (`admin.java`, `members.java`, `database_connection.java`).

---

## 1. Encapsulation

**What it is:** keeping an object's data and internal workings hidden, and
exposing only what is needed. Think of a capsule: the outside sees only the
shell, not what is inside.

**In this project:**

- **Database credentials are hidden.** In `database_connection.java`, `URL`,
  `USERNAME`, and `PASSWORD` are declared `private static final`. Other classes
  cannot read or change them directly; they can only call `connect()`.

```java
private static final String URL =
        "jdbc:mysql://localhost:3306/library_management_system";
private static final String USERNAME = "database";
private static final String PASSWORD = "...";
public static Connection connect() { ... }
```

- **Passwords stay in memory only briefly.** Login and registration read the
  password into a `char[]`, use it, then overwrite it with `\0` so it does not
  linger in memory:

```java
char[] pass = regPassword.getPassword();
// ... use the password ...
Arrays.fill(pass, '\0');   // clears sensitive data from memory
```

- **Security logic is hidden behind small private helpers.** `admin.java`
  hashes and verifies passwords with private methods (`hashPassword`,
  `verifyPassword`, `passwordMatches`). The rest of the class does not need to
  know how PBKDF2 works.

```java
private String hashPassword(char[] pass) throws Exception { ... }
private boolean verifyPassword(char[] password, String stored) throws Exception { ... }
private boolean passwordMatches(String typed, String stored) { ... }
```

- **Table selection state is guarded.** `selectedBookId`, `selectedMemberId`,
  `selectedReservationId`, and `selectedUsageId` start at `-1` (meaning "none
  selected") and are only changed inside the table selection listeners.

**Why it matters:** if you change how passwords are hashed, you only edit the
private helper — nothing else in the program breaks.

---

## 2. Inheritance

**What it is:** one class reuses and extends another class. The new class
("subclass") gets the fields and methods of the existing class ("superclass")
and can add its own.

**In this project:** both windows extend `javax.swing.JFrame`.

`admin.java`:
```java
public class admin extends JFrame { ... }
```

`members.java`:
```java
public class members extends JFrame { ... }
```

**What this gives us:** `admin` and `members` automatically get everything a
window needs — `setTitle(...)`, `setSize(...)`, `setLocationRelativeTo(null)`,
`setVisible(true)`, `getContentPane()`, `add(...)`, `setDefaultCloseOperation(...)`.
The students did not have to build a window from scratch; they inherited it
from `JFrame` and specialized it into an Admin window and a Member window.

**Real-world analogy:** a `JFrame` is like a generic empty room. `admin`
and `members` are that same room, but one is furnished as an office and the
other as a classroom.

---

## 3. Polymorphism

**What it is:** the same name or interface can behave differently depending on
the actual object. Example: "press the power button" does different things on a
TV, a microwave, and a computer.

**In this project:**

### a) Method overriding — table editing
Each screen creates a `DefaultTableModel` but overrides `isCellEditable` so
users cannot accidentally type into table cells:

```java
booksModel = new DefaultTableModel() {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
```

`booksModel`, `membersModel`, `reservationsModel`, `usageModel`, and `model`
are all `DefaultTableModel` objects, but each anonymous subclass decides its
own editability.

### b) Method overloading — `loadBooks`
In `members.java`, `loadBooks` exists in two forms:

```java
public void loadBooks() { ... }                              // all books
public void loadBooks(String keyword) { ... }                // filtered search
```

The no-argument version simply calls the keyword version with the current
search text. Same name, different behavior based on the arguments.

### c) Programming to interfaces (JDBC)
The code declares variables as JDBC interfaces, not MySQL-specific classes:

```java
Connection con = database_connection.connect();
PreparedStatement ps = con.prepareStatement(sql);
ResultSet rs = ps.executeQuery();
```

`Connection`, `PreparedStatement`, and `ResultSet` are interfaces. The actual
MySQL implementations are hidden inside the JDBC driver (`mysql.jar`). If the
driver changes, the Java code does not need to change.

### d) One action per button
Each button's lambda does something different when clicked — `approveButton`
approves, `declineButton` declines, `logoutButton` logs out — but they are all
handled through the same `addActionListener` mechanism.

**Why it matters:** the program can treat different objects uniformly (as a
table model, as a JDBC connection, as a button action) while each one behaves
in its own way.

---

## 4. Abstraction

**What it is:** showing only the essential features and hiding the complicated
details. Example: you drive a car using the steering wheel and pedals; you do
not need to know how the engine or brakes work internally.

**In this project:**

### a) `database_connection.connect()`
The rest of the application calls one method and gets a working database
connection. It does not see the JDBC URL, the driver loading, the username, the
password, or the error handling:

```java
Connection con = database_connection.connect();
if (con == null) {
    JOptionPane.showMessageDialog(this, "Database connection failed!");
    return;
}
```

The admin and member screens are abstracted away from *how* the connection is
made.

### b) JDBC interfaces
`Connection`, `PreparedStatement`, and `ResultSet` define what operations are
possible (`prepareStatement`, `executeQuery`, `next`, `getInt`, etc.) without
revealing how MySQL executes them.

### c) GUI screens as methods
`showDashboard`, `showManageBooks`, `showReservations`, `showBookUsage`, and
`showReports` hide the Swing layout details. The dashboard menu buttons just
call `showManageBooks()` — the caller does not need to know about `JPanel`,
`GridLayout`, or `BorderLayout`.

### d) Helper methods
`getCount(String sql)` hides opening a connection, running a count query, and
returning the number:

```java
public int getCount(String sql) { ... }
```

The dashboard calls `getCount("SELECT COUNT(*) FROM books")` and gets a number
back.

**Why it matters:** a beginner working on the admin menu does not need to
understand JDBC internals to add a new screen — they just call `connect()` and
build the Swing layout.

---

## 5. Class diagram (based on the actual implementation)

```text
+-------------------+        uses          +-------------------+
| database_connection|------------------->|   connect(): Connection  |
+-------------------+        static      +-------------------+
        | static                                           ^
        |                                                  |
        |                                                  |
        v                                                  v
+-------------------+                                +-------------------+
|      admin        |                                |     members       |
+-------------------+                                +-------------------+
| - usernameField   |                                | - loginStudentId  |
| - passwordField   |                                | - loginPassword   |
| - adminName       |                                | - memberId        |
| - membersTable    |                                | - studentId       |
| - booksTable      |                                | - fullName        |
| - reservationsTable|                               | - course, ...     |
| - usageTable      |                                | - booksTable      |
| - selected*Id = -1|                                | - reservationsTable|
+-------------------+                                | - model           |
| + admin()         |                                | + members()       |
| + showLogin()     |                                | + showLogin()     |
| + loginAdmin()    |                                | + loginStudent()  |
| + registerAdmin() |                                | + register()      |
| + showDashboard() |                                | + showDashboard() |
| + showManageBooks()|                               | + reserveBook()   |
| + addBook()       |                                | + borrowBook()    |
| + updateBook()    |                                | + returnBook()    |
| + deleteBook()    |                                | + cancelReservation()|
| + searchBook()    |                                | + loadBooks()     |
| + showManageMembers()|                               + loadOverloaded   |
| + addMember()     |                                | + loadMyReservations()|
| + updateMember()  |                                | + loadUsage()     |
| + deleteMember()  |                                | + hashPassword()  |
| + searchMember()  |                                | + verifyPassword()|
| + showReservations()|                               + main()          |
| + approveReservation()|                               +-----------------+
| + declineReservation()|
| + showBookUsage()   |
| + markUsageReturned()|
| + showReports()     |
| + loadReport()      |
| + createCard()      |
| + getCount()        |
| + main()            |
+-------------------+        uses          +-------------------+
        | static                                           ^
        |                                                  |
        +-------------------- connect() -------------------+
```

**Legend:**
- `-` = private field or method (encapsulation)
- `+` = public field or method
- `extends` (not drawn above for simplicity): both `admin` and `members` extend `javax.swing.JFrame`
- `uses`: `admin` and `members` both call `database_connection.connect()` (dependency)

**Inheritance in the diagram:** `admin extends JFrame` and `members extends JFrame`.

---

## 6. How to explain this to your instructor

- **Encapsulation:** "We keep database credentials and password hashing private. Passwords are stored in `char[]` and wiped from memory after use."
- **Inheritance:** "Both `admin` and `members` extend `JFrame`, so they inherit all window behavior instead of building windows from scratch."
- **Polymorphism:** "We override `isCellEditable` in table models, overload `loadBooks` with and without a keyword, and use JDBC interfaces so the code works with the driver implementation hidden."
- **Abstraction:** "`database_connection.connect()` hides how the database connection is made; `getCount()` hides the whole count-query process; `showManageBooks()` hides the Swing layout."



