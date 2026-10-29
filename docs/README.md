# LibroTrack — Library Management System

A simple, beginner-friendly Java Swing desktop application connected to a MySQL
database for managing a school library: books, student members, reservations, and
in-library book usage (time-in / time-out).

## Project title and client profile
- **Title:** LibroTrack — Library Management System
- **Client:** A school library that manages books, student members, reservations,
  and book usage. Books are for use inside the library only, so the system tracks
  who uses each book and when it is returned to its place.

## Technology stack
- Java (Java SE)
- Java Swing (desktop GUI)
- MySQL (MariaDB-compatible)
- JDBC (MySQL Connector/J, included as `mysql.jar`)
- Visual Studio Code
- Git / GitHub

No frameworks such as Spring Boot or Hibernate are used.

## Features

### Admin (admin.java)
- Admin login / logout — password verified against a PBKDF2 hash in the database
- Dashboard — totals for books, available copies, members, usage, reservations
- Manage books — add, edit, delete, search
- Manage members — view, register, update, delete (protected if active usage/reservations exist)
- Manage reservations — view, approve, decline
- Book usage monitoring — mark books returned (time-out)
- Reports — Books report, Student Members report, Book Usage History

### Member (members.java)
- Member sign up — account created with a hashed password
- Member login / logout — only Active accounts can log in
- View and search books — browse available copies
- Book reservation — request a book (Pending until admin approves)
- My reservations — check reservation status
- Borrow / return — time-in and time-out for approved reservations
- Book usage history — view your own usage records

## Project structure
```
libroTrack/
├── admin.java            # Admin window: login, dashboard, books, members,
│                         # reservations, usage, reports
├── members.java          # Member window: login, signup, dashboard, browse,
│                         # reserve, borrow/return, my reservations, usage
├── database_connection.java   # JDBC connection to MySQL
├── mysql.jar             # MySQL JDBC driver (included in repo)
├── library_management_system.sql   # Database setup script (schema + sample data)
├── docs/                 # All documentation files
│   ├── README.md         # This file (overview, setup, how to run)
│   ├── PROJECT_DOCUMENTATION.md    # Requirements, client profile, solution
│   ├── DATABASE_DESIGN.md          # Tables, keys, ERD, setup
│   ├── OOP_DOCUMENTATION.md        # OOP principles with examples from this project
│   └── basaha_ni.md          # Original project documentation
└── .vscode/settings.json   # VS Code references mysql.jar
```

## Prerequisites
- Java JDK (tested with JDK 25)
- MySQL Server or MariaDB (tested with MariaDB 11.8)
- The `mysql.jar` JDBC driver (already included in this repository)

## Database setup
1. Start MySQL/MariaDB.
2. Create the database and tables using the provided script:

   ```bash
   mysql -u YOUR_USERNAME -p library_management_system < library_management_system.sql
   ```

   Or run the script in MySQL Workbench / phpMyAdmin / the mysql client.
3. Verify the tables:

   ```sql
   USE library_management_system;
   SHOW TABLES;
   ```

   Expected tables: `admins`, `books`, `members`, `reservations`, `book_usage`.
4. See `library_management_system.sql` for the sample accounts (clearly labeled as
   demo/sample — change the passwords before real use).

## Configure the connection
Open `database_connection.java` and set `URL`, `USERNAME`, and `PASSWORD` for your
local MySQL computer. The URL must point to `library_management_system`.

> **Security:** `database_connection.java` in this repository previously contained a
> real database password. Treat it as exposed: change your MySQL password and keep
> your credentials on your own computer only. Never commit real passwords to GitHub.
> The sample passwords in `library_management_system.sql` are for demonstration only.

## Compilation and execution
From the project folder (where `mysql.jar` is located):

```bash
javac -cp "mysql.jar" admin.java members.java database_connection.java
```

Run the Admin side:

```bash
java -cp "mysql.jar:." admin
```

Run the Member side:

```bash
java -cp "mysql.jar:." members
```

On Windows, replace `:` with `;` in the classpath:
```bash
javac -cp "mysql.jar" admin.java members.java database_connection.java
java -cp "mysql.jar;." admin
```

## How to test the main flows
1. **Admin login:** run `admin`, log in with an admin account from the database.
2. **Member sign up:** run `members`, create a new student account.
3. **Member login:** log in with the student ID and password.
4. **Reservation:** the member selects a book and clicks Reserve; status becomes Pending.
5. **Admin approval:** the admin opens Reservations, selects the Pending request, and clicks Approve.
6. **Borrow (time-in):** the member opens My Reservations and clicks Borrow Book (Time In).
7. **Return (time-out):** the admin opens Book Usage and clicks Mark Returned, or the member clicks Return Book (Time Out).

## Troubleshooting
- **"Database connection failed!"**: Make sure MySQL is running, the database
  `library_management_system` exists, and `USERNAME`/`PASSWORD` in
  `database_connection.java` match your local MySQL setup.
- **ClassNotFoundException / driver errors**: confirm `mysql.jar` is on the
  classpath when compiling and running (see `.vscode/settings.json` for VS Code).
- **Duplicate student/username errors**: those IDs already exist; use a different ID.
- **Cannot delete book/member**: the record has related reservations or usage history.

## Related documentation
- `docs/PROJECT_DOCUMENTATION.md` — title, client profile, problem, solution, requirements
- `docs/DATABASE_DESIGN.md` — schema, keys, ERD, setup instructions
- `docs/OOP_DOCUMENTATION.md` — encapsulation, inheritance, polymorphism, abstraction
- `docs/basaha_ni.md` — original project documentation
