# LibroTrack — Database Design

Database: **`library_management_system`** (MySQL / MariaDB)

> **Verified design:** the tables, columns, data types, keys, and status values
> below were checked against the actual running database and the SQL queries
> used in `admin.java` and `members.java`. They match the real implementation.

Sample data and the full setup script are in `library_management_system.sql`.

---

## 1. Database overview

Five tables store everything the system needs:

- `admins` — library administrator accounts
- `books` — library book records and copy counts
- `members` — student member accounts
- `reservations` — book reservation requests and their status
- `book_usage` — in-library book usage (time-in / time-out)

---

## 2. Table descriptions and verified columns

### admins
Stores administrator login accounts.

| Column | Type | Null? | Key | Description |
|---|---|---|---|---|
| `admin_id` | INT | NO | PK | Auto-incremented administrator ID |
| `full_name` | VARCHAR(100) | NO | | Admin's full name |
| `username` | VARCHAR(50) | NO | UNIQUE | Admin login username |
| `password` | VARCHAR(255) | NO | | PBKDF2 password hash (never plain text) |
| `created_at` | TIMESTAMP | YES | default CURRENT_TIMESTAMP | Account creation time |

Java queries: `SELECT full_name, password FROM admins WHERE username = ?`
`INSERT INTO admins (full_name, username, password) VALUES (?, ?, ?)`

### books
Stores book records. A book may have multiple copies; `available_copies`
tracks how many copies are free for in-library use.

| Column | Type | Null? | Key | Description |
|---|---|---|---|---|
| `book_id` | INT | NO | PK | Auto-incremented book ID |
| `isbn` | VARCHAR(20) | YES | UNIQUE | Book ISBN |
| `title` | VARCHAR(200) | NO | | Book title |
| `author` | VARCHAR(150) | NO | | Book author |
| `category` | VARCHAR(100) | YES | | Book category |
| `publisher` | VARCHAR(150) | YES | | Book publisher |
| `publication_year` | YEAR(4) | YES | | Year published |
| `quantity` | INT | NO | default 1 | Total copies |
| `available_copies` | INT | NO | default 1 | Copies currently available |
| `created_at` | TIMESTAMP | YES | default CURRENT_TIMESTAMP | Record creation time |

Java queries: `SELECT book_id, isbn, title, author, category, publisher,
publication_year, quantity, available_copies FROM books ...`
`INSERT INTO books (isbn, title, author, category, publisher, publication_year,
quantity, available_copies) VALUES (?, ?, ?, ?, ?, ?, ?, ?)

### members
Stores student member accounts. `student_id` is unique. `status` is `Active`
or `Inactive`; only Active members can log in. `password_hash` is nullable so
older accounts without a hash still exist in the system.

| Column | Type | Null? | Key | Description |
|---|---|---|---|---|
| `member_id` | INT | NO | PK | Auto-incremented member ID |
| `student_id` | VARCHAR(30) | NO | UNIQUE | Student ID used for login |
| `full_name` | VARCHAR(100) | NO | | Student's full name |
| `course` | VARCHAR(100) | YES | | Student's course |
| `year_level` | VARCHAR(20) | YES | | Year level |
| `section` | VARCHAR(50) | YES | | Section |
| `contact_number` | VARCHAR(20) | YES | | Contact number |
| `address` | VARCHAR(255) | YES | | Address |
| `registration_date` | DATE | NO | | Registration date |
| `status` | ENUM('Active','Inactive') | YES | default 'Active' | Account status |
| `created_at` | TIMESTAMP | YES | default CURRENT_TIMESTAMP | Account creation time |
| `password_hash` | VARCHAR(255) | YES | | PBKDF2 password hash |

Java queries: `SELECT member_id, student_id, full_name, course, year_level,
section, contact_number, address, registration_date, status, password_hash
FROM members WHERE student_id = ?`
`INSERT INTO members (student_id, full_name, course, year_level, section,
contact_number, address, registration_date, status, password_hash) VALUES (...)`

### reservations
Stores reservation requests from members. An admin approves or declines each one.

| Column | Type | Null? | Key | Description |
|---|---|---|---|---|
| `reservation_id` | INT | NO | PK | Auto-incremented reservation ID |
| `book_id` | INT | NO | FK → books(book_id) | Reserved book |
| `member_id` | INT | NO | FK → members(member_id) | Member who reserved |
| `reservation_date` | DATE | NO | | Date of the request |
| `status` | ENUM('Pending','Approved','Borrowed','Declined','Cancelled','Completed') | NO | default 'Pending' | Reservation status |
| `admin_note` | VARCHAR(255) | YES | | Admin note (e.g. decline reason) |
| `created_at` | TIMESTAMP | NO | default CURRENT_TIMESTAMP | Request creation time |
| `updated_at` | TIMESTAMP | NO | default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last update time |

Java queries:
- Load: `SELECT r.reservation_id, m.student_id, m.full_name, b.title,
  r.reservation_date, r.status, r.admin_note FROM reservations r JOIN members m
  ON r.member_id = m.member_id JOIN books b ON r.book_id = b.book_id ...`
- Insert: `INSERT INTO reservations (book_id, member_id, reservation_date, status)
  VALUES (?, ?, ?, 'Pending')`
- Approve: `UPDATE reservations SET status = 'Approved' WHERE reservation_id = ?`
  plus `UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?`
- Decline: `UPDATE reservations SET status = 'Declined', admin_note = ? WHERE
  reservation_id = ? AND status = 'Pending'`

### book_usage
Records when a student starts using a book (`time_in`) and finishes
(`time_out`) inside the library.

| Column | Type | Null? | Key | Description |
|---|---|---|---|---|
| `usage_id` | INT | NO | PK | Auto-incremented usage ID |
| `book_id` | INT | NO | FK → books(book_id) | Book being used |
| `member_id` | INT | NO | FK → members(member_id) | Student using it |
| `reservation_id` | INT | YES | FK → reservations(reservation_id) | Related reservation (if any) |
| `usage_date` | DATE | NO | | Date of usage |
| `time_in` | TIME | NO | | Time the student started using the book |
| `time_out` | TIME | YES | | Time the student returned the book |
| `status` | ENUM('Using','Returned') | YES | default 'Using' | Usage status |

Java queries:
- Insert (borrow/time-in): `INSERT INTO book_usage (book_id, member_id,
  reservation_id, usage_date, time_in, status) VALUES (?, ?, ?, ?, ?, 'Using')`
- Return/time-out: `UPDATE book_usage SET time_out = CURTIME(), status = 'Returned'
  WHERE usage_id = ? AND status = 'Using'`
- Load: `SELECT u.usage_id, m.student_id, m.full_name, b.title, u.usage_date,
  u.time_in, u.time_out, u.status FROM book_usage u JOIN members m ON ... JOIN
  books b ON ...`

---

## 3. Primary keys and foreign keys

| Table | Primary key | Foreign keys |
|---|---|---|
| `admins` | `admin_id` | none |
| `books` | `book_id` | none |
| `members` | `member_id` | none |
| `reservations` | `reservation_id` | `book_id → books(book_id)`<br>`member_id → members(member_id)` |
| `book_usage` | `usage_id` | `book_id → books(book_id)`<br>`member_id → members(member_id)`<br>`reservation_id → reservations(reservation_id) ON DELETE SET NULL` |

**Relationships:**
- One book can have many reservations and many usage records.
- One member can have many reservations and many usage records.
- One reservation can have at most one usage record (enforced by a unique key on
  `reservation_id` in `book_usage`).

---

## 4. Entity relationship diagram (ERD)

```text
                    +-------------+                  +-------------+
                    |    books    |                  |   members   |
                    +-------------+                  +-------------+
                    | book_id PK  |                  | member_id PK|
                    | isbn UK     |                  | student_id UK|
                    | title       |                  | full_name   |
                    | author      |                  | course      |
                    | category    |                  | year_level  |
                    | publisher   |                  | section     |
                    | publication_year |          | contact_number|
                    | quantity    |                  | address     |
                    | available_copies |         | registration_date|
                    | created_at  |                  | status      |
                    +-------------+                  | created_at  |
                            ^                        | password_hash|
                            |                        +-------------+
                            | 1                     many            ^
                            |                                       |
                            |                                       |
                   +--------+---------+                  +----------+---------+
                   |   reservations   |                  |   book_usage       |
                   +------------------+                  +--------------------+
                   | reservation_id PK|                  | usage_id PK        |
                   | book_id FK       |------+           | book_id FK         |
                   | member_id FK     |------+           | member_id FK       |
                   | reservation_date |          |       | reservation_id FK  |
                   | status           |          |       | usage_date         |
                   | admin_note       |          |       | time_in            |
                   | created_at       |          |       | time_out           |
                   | updated_at       |          |       | status             |
                   +------------------+          |       +--------------------+
                          |                      |
                          |                      |
                          | many                 | many
                          |                      |
                          v                      v
                   (one reservation may have at most
                    one usage record — unique reservation_id in book_usage)
```

**Reading the diagram:**
- `books` 1 — many `reservations`: a book can be reserved many times.
- `books` 1 — many `book_usage`: a book can have many usage records.
- `members` 1 — many `reservations`: a student can make many reservations.
- `members` 1 — many `book_usage`: a student can have many usage records.
- `reservations` 1 — 0/1 `book_usage`: a reservation may have one usage record
  when the approved book is borrowed.

---

## 5. Status values used by the application

| Table | Column | Allowed values |
|---|---|---|
| `members` | `status` | `Active`, `Inactive` |
| `reservations` | `status` | `Pending`, `Approved`, `Borrowed`, `Declined`, `Cancelled`, `Completed` |
| `book_usage` | `status` | `Using`, `Returned` |

---

## 6. Setup and verification instructions

1. Start MySQL/MariaDB.
2. Import the script:

   ```bash
   mysql -u YOUR_USERNAME -p library_management_system < library_management_system.sql
   ```

   Or paste the contents of `library_management_system.sql` into MySQL Workbench,
   phpMyAdmin, or the `mysql` client.

3. Verify the tables:

   ```sql
   USE library_management_system;
   SHOW TABLES;
   ```

   Expected result:

   ```
   +---------------------------------+
   | Tables_in_library_management_system |
   +---------------------------------+
   | admins                          |
   | books                           |
   | book_usage                      |
   | members                         |
   | reservations                    |
   +---------------------------------+
   ```

4. Check sample data:

   ```sql
   SELECT * FROM books;
   SELECT * FROM members;
   ```

5. Configure `database_connection.java` with your local MySQL username and
   password so the URL points to `library_management_system`.

> The script uses `CREATE TABLE IF NOT EXISTS` and `ON DUPLICATE KEY UPDATE`, so
> it is safe to re-run without deleting existing data. It does NOT drop tables.


`
