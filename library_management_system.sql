-- ================================================================
-- LibroTrack — Library Management System
-- Database initialization script
-- ================================================================
-- Database name: library_management_system
--
-- IMPORTANT: This script was created from the ACTUAL database schema
-- used by the Java application (admin.java, members.java). It does
-- NOT drop or overwrite existing tables. It uses CREATE TABLE IF NOT
-- EXISTS so it is safe to run on an existing database.
--
-- Sample data is for demonstration only. Passwords shown here are
-- SAMPLE passwords for a school project — change them before any
-- real use. Do not commit real passwords to GitHub.
-- ================================================================

-- Step 1: Create the database if it does not exist yet.
CREATE DATABASE IF NOT EXISTS library_management_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_uca1400_ai_ci;

USE library_management_system;

-- ================================================================
-- Step 2: Create tables (in dependency order).
-- ================================================================

-- Table: admins
-- Stores library administrator accounts.
CREATE TABLE IF NOT EXISTS admins (
    admin_id    INT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,      -- PBKDF2 hash, never plain text
    created_at  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Table: books
-- Stores library book records. ISBN is unique. available_copies
-- tracks how many copies are free for in-library use.
CREATE TABLE IF NOT EXISTS books (
    book_id          INT AUTO_INCREMENT PRIMARY KEY,
    isbn             VARCHAR(20)   UNIQUE,
    title            VARCHAR(200)  NOT NULL,
    author           VARCHAR(150)  NOT NULL,
    category         VARCHAR(100),
    publisher        VARCHAR(150),
    publication_year YEAR(4)        DEFAULT NULL,
    quantity         INT           NOT NULL DEFAULT 1,
    available_copies INT           NOT NULL DEFAULT 1,
    created_at       TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Table: members
-- Stores student member accounts. student_id is unique.
-- status is 'Active' or 'Inactive'; only Active members can log in.
-- password_hash stores a PBKDF2 hash (nullable for older accounts).
CREATE TABLE IF NOT EXISTS members (
    member_id         INT AUTO_INCREMENT PRIMARY KEY,
    student_id        VARCHAR(30)  NOT NULL UNIQUE,
    full_name         VARCHAR(100) NOT NULL,
    course            VARCHAR(100),
    year_level        VARCHAR(20),
    section           VARCHAR(50),
    contact_number    VARCHAR(20),
    address           VARCHAR(255),
    registration_date DATE         NOT NULL,
    status            ENUM('Active','Inactive') DEFAULT 'Active',
    created_at        TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    password_hash     VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB;

-- Table: reservations
-- Stores book reservation requests submitted by members.
-- An admin approves or declines each request.
-- status values: Pending, Approved, Borrowed, Declined, Cancelled, Completed
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id   INT AUTO_INCREMENT PRIMARY KEY,
    book_id          INT                                     NOT NULL,
    member_id        INT                                     NOT NULL,
    reservation_date DATE                                     NOT NULL,
    status           ENUM('Pending','Approved','Borrowed','Declined','Cancelled','Completed')
                                                               NOT NULL DEFAULT 'Pending',
    admin_note       VARCHAR(255)                            DEFAULT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservations_book
        FOREIGN KEY (book_id) REFERENCES books(book_id),
    CONSTRAINT fk_reservations_member
        FOREIGN KEY (member_id) REFERENCES members(member_id)
) ENGINE=InnoDB;

-- Table: book_usage
-- Records when a student starts (time_in) and finishes (time_out)
-- using a book inside the library. Linked to a reservation when
-- the reservation was approved first.
CREATE TABLE IF NOT EXISTS book_usage (
    usage_id     INT AUTO_INCREMENT PRIMARY KEY,
    book_id      INT                              NOT NULL,
    member_id    INT                              NOT NULL,
    reservation_id INT                             DEFAULT NULL,
    usage_date   DATE                             NOT NULL,
    time_in      TIME                             NOT NULL,
    time_out     TIME                             DEFAULT NULL,
    status       ENUM('Using','Returned')        DEFAULT 'Using',
    CONSTRAINT fk_book_usage_book
        FOREIGN KEY (book_id) REFERENCES books(book_id),
    CONSTRAINT fk_book_usage_member
        FOREIGN KEY (member_id) REFERENCES members(member_id),
    CONSTRAINT fk_book_usage_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- ================================================================
-- Step 3: Safe sample data (demonstration only).
-- ================================================================

-- Sample administrator account.
-- Username: admin   | Sample password: admin123  (CHANGE THIS)
-- Password hash generated with the same PBKDF2 algorithm used by admin.java.
INSERT INTO admins (full_name, username, password)
VALUES (
    'System Administrator',
    'admin',
    'pbkdf2$120000$tzbA+jOAVp6h4mYhvs816g==$cAHpgRLMRHBGl+HvCjT6TJKnvezl6rDqXwTixSgEDjk='
) ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);

-- Sample student member.
-- Student ID: STUDENT-001  | Sample password: member123  (CHANGE THIS)
INSERT INTO members (student_id, full_name, course, year_level, section,
                     contact_number, address, registration_date, status, password_hash)
VALUES (
    'STUDENT-001',
    'Juan Dela Cruz',
    'Bachelor of Science in Information Technology',
    '2',
    'A',
    '0912 345 6789',
    '123 Sample Street, Sample City',
    '2026-01-15',
    'Active',
    'pbkdf2$120000$gM1Yq/fibPM3l0mhpf03Kw==$oOj/ks3KxP5YIJdN90HY2Svk2Nt+j05BRA8gdLTjJEE='
) ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);

-- Sample books available for in-library use.
INSERT INTO books (isbn, title, author, category, publisher,
                   publication_year, quantity, available_copies)
VALUES
    ('978-0134685991', 'Clean Code: A Handbook of Agile Software Craftsmanship',
     'Robert C. Martin', 'Programming', 'Prentice Hall', 2008, 3, 3),
    ('978-0132350884', 'Clean Architecture: A Craftsman\'s Guide to Software Structure and Design',
     'Robert C. Martin', 'Programming', 'Prentice Hall', 2017, 2, 2),
    ('978-1449337711', 'Learning PHP, MySQL & JavaScript',
     'Robin Nixon', 'Web Development', "O'Reilly Media", 2014, 2, 2),
    ('978-0262033848', 'Introduction to Algorithms',
     'Thomas H. Cormen', 'Computer Science', 'The MIT Press', 2009, 4, 4),
    ('978-0134093413', 'Head First Java',
     'Kathy Sierra, Bert Bates', 'Programming', "O'Reilly Media", 2005, 2, 2)
ON DUPLICATE KEY UPDATE title = VALUES(title);

-- Sample reservations (valid relationships with books and members above).
-- reservation_id 1: Pending request by STUDENT-001 for book_id 1
-- reservation_id 2: Approved reservation by STUDENT-001 for book_id 2
INSERT INTO reservations (book_id, member_id, reservation_date, status, admin_note)
VALUES
    (1, 1, '2026-02-01', 'Pending', 'Waiting for librarian approval.'),
    (2, 1, '2026-02-02', 'Approved', 'Approved. Please get the book at the library counter.')
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Sample book usage records (valid relationships).
-- usage_id 1: Currently in use (book_id 2, member_id 1, reservation_id 2)
-- usage_id 2: Already returned (book_id 1, member_id 1)
INSERT INTO book_usage (book_id, member_id, reservation_id, usage_date, time_in, time_out, status)
VALUES
    (2, 1, 2, '2026-02-02', '08:00:00', NULL, 'Using'),
    (1, 1, NULL, '2026-01-20', '09:15:00', '10:45:00', 'Returned')
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- ================================================================
-- Step 4: Verification queries.
-- Run these after importing to confirm the setup:
--
--   USE library_management_system;
--   SHOW TABLES;
--   SELECT * FROM books;
--   SELECT * FROM members;
-- ================================================================
