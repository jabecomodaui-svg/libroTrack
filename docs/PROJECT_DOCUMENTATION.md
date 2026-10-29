# LibroTrack — Library Management System
## Project Documentation

---

## 1. Project title
**LibroTrack — Library Management System**

A simple, secure, beginner-friendly Java Swing desktop application for a school
library, connected to a MySQL database through JDBC.

---

## 2. Client profile
**Client:** A school library.

**What the library manages:**
- Books (for use inside the library only)
- Student member accounts
- Book reservation requests
- Book usage inside the library (time-in and time-out)

**Why a system is needed:** the library currently records books, student
registrations, reservations, and usage manually, which makes it hard to search
books, check available copies, know who is using a book, and prepare reports.

---

## 3. Problem statement
Manual library management causes:
1. Slow and error-prone book recording and searching.
2. Difficulty tracking which student uses which book and when.
3. Inaccurate information on available copies.
4. Time-consuming student registration and record keeping.
5. Slow report preparation for books, members, reservations, and usage.

---

## 4. Proposed solution
LibroTrack computerizes library management with two roles:

- **Admin:** manages books, members, reservations, book usage, and reports.
- **Member (student):** signs up, logs in, browses books, reserves books, checks
  reservation status, records time-in/time-out, and views personal usage history.

The admin and members share one MySQL database (`library_management_system`).
Members can only access their own records; admin-only functions are enforced in
the application logic.

---

## 5. Functional requirements

The status below reflects the **actual, verified implementation** in this
repository.

### Admin features
| # | Feature | Status |
|---|---------|--------|
| 1 | Admin login and logout | Implemented |
| 2 | Dashboard with system statistics | Implemented |
| 3 | Manage books (add, edit, delete, search) | Implemented |
| 4 | Manage members (view, register, update, manage) | Implemented |
| 5 | Manage reservations (view, approve, decline) | Implemented |
| 6 | Book usage monitoring (time-in / time-out) | Implemented |
| 7 | Reports (books, members, usage history) | Implemented |

### Member features
| # | Feature | Status |
|---|---------|--------|
| 1 | Member sign up | Implemented |
| 2 | Member login and logout | Implemented |
| 3 | View books (browse and search) | Implemented |
| 4 | Check availability (available copies) | Implemented |
| 5 | Book reservation | Implemented |
| 6 | View reservations (own statuses) | Implemented |
| 7 | Book usage history (own records) | Implemented |
| 8 | Manage profile | Not implemented — the existing code does not include a profile-update screen for members |

### Expected admin–member workflow
1. Member submits a reservation → saved as `Pending` in MySQL.
2. Admin views the reservation → approves or declines → status saved.
3. Member checks the updated reservation status.
4. Approved reservation → member borrows (time-in) → book usage recorded.
5. Member finishes → time-out recorded → copy available again.

---

## 6. Technology stack
- Java (Java SE), Java Swing for the desktop GUI
- MySQL / MariaDB database: `library_management_system`
- JDBC with the included `mysql.jar` driver
- Visual Studio Code, Git and GitHub

No Spring Boot, Hibernate, REST APIs, or other frameworks are used, and the
application remains a desktop application (not converted to a web app).

---

## 7. System overview
Two entry points launch two separate windows:

- `admin.java` → `main()` starts the Admin Login window. After login the admin
  reaches the Dashboard and can open Manage Books, Manage Members, Reservations,
  Book Usage, and Reports.
- `members.java` → `main()` starts the Member Login window. New students use
  Sign Up. After login the member reaches the Member Dashboard with Available
  Books, My Reservations, and usage history.

Database connection is handled by `database_connection.java` (`connect()` returns
a `java.sql.Connection` or `null` on failure). All database access uses
`PreparedStatement` with parameters. Passwords are stored as PBKDF2 hashes, never
plain text.
