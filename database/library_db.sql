-- LibroTrack: Library Management System
-- WARNING: this script DELETES and recreates library_db (fresh start).

DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db;
USE library_db;

-- ================= TABLES =================

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN','LIBRARIAN','BORROWER') NOT NULL
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE authors (
    author_id INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(100) NOT NULL
);

CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(150) NOT NULL,
    author_id INT NOT NULL,
    category_id INT NOT NULL,
    publisher VARCHAR(100),
    year_published INT NOT NULL,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    shelf_location VARCHAR(30),
    FOREIGN KEY (author_id) REFERENCES authors(author_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE borrowers (
    borrower_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    full_name VARCHAR(100) NOT NULL,
    borrower_type ENUM('STUDENT','FACULTY') NOT NULL,
    contact VARCHAR(30),
    address VARCHAR(150),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE borrow_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    borrower_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    fine_amount DECIMAL(8,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (borrower_id) REFERENCES borrowers(borrower_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- ================= SAMPLE DATA =================

INSERT INTO users (username, password, full_name, role) VALUES
('admin',    'admin123', 'Head Librarian',  'ADMIN'),
('lib1',     'lib123',   'Maria Santos',    'LIBRARIAN'),
('student1', 'stud123',  'Juan Dela Cruz',  'BORROWER');

INSERT INTO categories (category_name) VALUES ('Life of Rizal'), ('Math'), ('English'), ('Filipino');

INSERT INTO authors (author_name) VALUES
('Dr. Rafael Palma'), ('Harle Mae Quinto'), ('Vince Sigma Salazar'), ('Henry Fundalism');

INSERT INTO books (isbn, title, author_id, category_id, publisher, year_published, total_copies, available_copies, shelf_location) VALUES
('9710800001',    'Noli Me Tangere',          1, 4, 'Anvil',    1887, 3, 2, 'A-01'),
('9710800002',    'El Filibusterismo',        1, 4, 'Anvil',    1891, 2, 2, 'A-02'),
('9780553380163', 'Algebra',  2, 2, 'Bantam',   1988, 2, 2, 'B-04'),
('9780345539434', 'Wikang Filipino',                   4, 2, 'Ballantine', 1980, 2, 1, 'B-05'),
('9789715420006', 'Advanced Verb Mechanics', 3, 1, 'Anvil', 1961, 1, 1, 'C-01');

INSERT INTO borrowers (user_id, full_name, borrower_type, contact, address) VALUES
(3,    'Juan Dela Cruz',  'STUDENT', '09171234567', 'Pavia, Iloilo'),
(NULL, 'Prof. Ana Reyes', 'FACULTY', '09179876543', 'Iloilo City');

-- Sample transactions (dates are relative to today so the demo always works)
-- 1) Juan borrowed Noli 20 days ago, due 13 days ago, NOT returned  -> OVERDUE
-- 2) Prof. Ana borrowed Cosmos 2 days ago, due in 5 days, NOT returned -> on time
-- 3) Prof. Ana borrowed Noli 30 days ago and returned it on time (history for the "most borrowed" report)
INSERT INTO borrow_transactions (borrower_id, book_id, borrow_date, due_date, return_date, fine_amount) VALUES
(1, 1, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 13 DAY), NULL, 0),
(2, 4, DATE_SUB(CURDATE(), INTERVAL 2 DAY),  DATE_ADD(CURDATE(), INTERVAL 5 DAY),  NULL, 0),
(2, 1, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 23 DAY), DATE_SUB(CURDATE(), INTERVAL 25 DAY), 0);
