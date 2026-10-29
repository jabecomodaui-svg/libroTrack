# Library Management System

## 1. Project Overview

The Library Management System is designed to simplify the management of library books and student members. It helps administrators manage book records, student accounts, reservations, and book usage.

Books are intended for use inside the library only and must not be taken outside. The system helps monitor book usage and record when students start and finish using books.

## 2. Project Files

- **`.vscode/`** – Contains configuration files used by Visual Studio Code.
- **`mysql.jar`** – The MySQL JDBC driver that allows the Java application to connect and communicate with the MySQL database.
- **`database_connection.java`** – Contains the Java code responsible for connecting the application to the database.

### Why Is `mysql.jar` Required?

The `mysql.jar` file is required because it provides the MySQL JDBC driver that allows the Java application to communicate with the MySQL database.

Although `database_connection.java` contains the connection code, the application also needs the JDBC driver to establish the database connection. They have different purposes: one provides the connection logic, while the other enables Java to communicate with MySQL.

The `mysql.jar` file is already included in this repository, so team members do not need to download it separately.

## 3. Problems Addressed by the System

The Library Management System addresses the following problems:

### 3.1. Manual Book Recording

Manually recording books can make it difficult and time-consuming to search for books, check available copies, and update the inventory.

### 3.2. Difficulty Monitoring Students

Without an organized system, it can be difficult to track which students use particular books and when they start and finish using them.

### 3.3. Inaccurate Book Availability

Without a centralized database, administrators may have difficulty determining whether books are available or currently being used by other students.

### 3.4. Manual Student Registration

Registering and managing student members through handwritten records can be time-consuming and difficult to maintain.

### 3.5. Time-Consuming Report Generation

Preparing reports about books, registered students, reservations, and book usage history can take a significant amount of time when done manually.

## 4. Proposed Solution

The Library Management System simplifies the management of books and student members through a computerized system. It allows administrators to record books, manage student accounts, check book availability, monitor book usage, and manage reservations.

The system also records book usage times and helps administrators monitor whether books are being used inside the library. Its reporting features help keep library records organized and make information easier to access.

## 5. Admin Features

1. **Admin Login** – Allows administrators to log in securely using a username and password.
2. **Dashboard** – Displays a summary of books, registered members, reservations, and book usage.
3. **Manage Books** – Allows administrators to add, edit, delete, and search for books.
4. **Manage Members** – Allows administrators to view, register, update, and manage student members.
5. **Manage Reservations** – Allows administrators to view, approve, or decline book reservations.
6. **Book Usage Monitoring** – Allows administrators to monitor book usage and record students' time-in and time-out.
7. **Reports** – Allows administrators to view and generate reports about books, members, reservations, and book usage.
8. **Admin Logout** – Allows administrators to log out securely after using the system.

## 6. Member Features

1. **Member Sign Up** – Allows students to create an account.
2. **Member Login** – Allows students to log in using their account credentials.
3. **View Books** – Allows students to view and search for available books.
4. **Book Reservation** – Allows students to submit reservation requests for books intended for library use.
5. **View Reservations** – Allows students to check the status of their reservation requests.
6. **Book Usage History** – Allows students to view their book usage records, including time-in and time-out.
7. **Manage Profile** – Allows students to view and update their personal information, if this feature is implemented in the application.
8. **Member Logout** – Allows students to log out securely after using the system.

## 7. Admin and Member Communication

The administrator and student members communicate through the application and a shared MySQL database. The database stores information about books, member accounts, reservations, and book usage.

### 7.1. Admin Responsibilities

- Manage books and book availability.
- View and manage registered members.
- Review and approve or decline reservations.
- Monitor book usage and time-in/time-out records.
- Generate library reports.

### 7.2. Member Responsibilities

- Create an account and log in.
- View and search for available books.
- Submit book reservation requests.
- Check reservation statuses.
- View book usage history.

### 7.3. Role of the MySQL Database

- Stores book and member records.
- Saves reservation requests and status updates.
- Stores book usage records.
- Provides updated information to the application based on each user's permissions.

### 7.4. Communication Flow

```text
           MEMBER
              |
              | Submits a reservation request
              v
      MYSQL DATABASE
              |
              | Stores the reservation
              v
            ADMIN
              |
              | Reviews the request
              | Approves or declines it
              v
      MYSQL DATABASE
              |
              | Saves the updated status
              v
           MEMBER
```

**Note:** The shared database allows the administrator and members to access updated information. This does not automatically provide direct messaging or real-time notifications.

## 8. Database Setup

The system uses MySQL as its database management system.

**Database Name:** `library_management_system`

The database contains five tables:

- `admins`
- `books`
- `members`
- `reservations`
- `book_usage`

### 8.1. Create the Database

Run the following SQL commands in your MySQL terminal:

```sql
CREATE DATABASE IF NOT EXISTS library_management_system;

USE library_management_system;
```

### 8.2. Create the Admins Table

Stores administrator account information.

```sql
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 8.3. Create the Books Table

Stores book information and availability.

```sql
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(150) NOT NULL,
    category VARCHAR(100),
    publisher VARCHAR(150),
    publication_year YEAR,
    quantity INT NOT NULL DEFAULT 0,
    available_copies INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 8.4. Create the Members Table

Stores student member information and account details.

```sql
CREATE TABLE IF NOT EXISTS members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    course VARCHAR(100),
    year_level VARCHAR(20),
    section VARCHAR(50),
    contact_number VARCHAR(30),
    address VARCHAR(255),
    registration_date DATE,
    status VARCHAR(30) DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password_hash VARCHAR(255)
);
```

### 8.5. Create the Reservations Table

Stores book reservation requests and their statuses.

```sql
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    reservation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM(
        'Pending',
        'Approved',
        'Borrowed',
        'Declined',
        'Cancelled',
        'Completed'
    ) NOT NULL DEFAULT 'Pending',
    admin_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_reservations_book
        FOREIGN KEY (book_id)
        REFERENCES books(book_id),

    CONSTRAINT fk_reservations_member
        FOREIGN KEY (member_id)
        REFERENCES members(member_id)
);
```

### 8.6. Create the Book Usage Table

Records when students start and finish using books inside the library.

```sql
CREATE TABLE IF NOT EXISTS book_usage (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    reservation_id INT NULL,
    usage_date DATE NOT NULL,
    time_in TIME,
    time_out TIME,
    status VARCHAR(30),

    CONSTRAINT fk_book_usage_book
        FOREIGN KEY (book_id)
        REFERENCES books(book_id),

    CONSTRAINT fk_book_usage_member
        FOREIGN KEY (member_id)
        REFERENCES members(member_id),

    CONSTRAINT fk_book_usage_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservations(reservation_id)
        ON DELETE SET NULL
);
```

### 8.7. Verify the Database Tables

Run the following commands to verify that the tables have been created:

```sql
USE library_management_system;

SHOW TABLES;
```

The expected tables are:

```text
admins
books
book_usage
members
reservations
```

## 9. Configure the Java Database Connection

Open `database_connection.java` and configure the connection settings for your local MySQL Server.

Example:

```java
String url =
    "jdbc:mysql://localhost:3306/library_management_system";

String username = "root";
String password = "YOUR_LOCAL_MYSQL_PASSWORD";
```

Replace `YOUR_LOCAL_MYSQL_PASSWORD` with your own MySQL password.

**Security Note:** Do not commit your real database password to GitHub. Each team member should use their own local database credentials.

## 10. Team Setup Instructions

Each team member should complete the following steps:

1. Clone the repository from GitHub.
2. Install Java JDK and MySQL Server.
3. Start the MySQL Server.
4. Create the `library_management_system` database and its tables using the SQL commands above.
5. Configure `database_connection.java` with their local MySQL credentials.
6. Compile and run the Java application using the included `mysql.jar` JDBC driver.

**Important:** The SQL commands above provide a proposed database structure based on the documented column names. Confirm that the column types, constraints, and status values match your existing Java code before using them. The SQL setup creates the tables but does not copy your existing database records.
