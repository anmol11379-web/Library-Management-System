# Project Statement - Library Management System

---

## 1. Problem Statement

Academic institutions, colleges, and departmental libraries often face challenges in tracking library assets, circulation workflows, book availability, and student borrowing histories. 

Manual record-keeping using registers or spreadsheets leads to several recurring problems:
* Difficulty in tracking real-time book availability across departments.
* Misplaced books and untracked overdue loans.
* Inaccurate or delayed fine calculations for late returns.
* Lack of historical transaction records for students.
* Human error in maintaining student contact details and membership records.

Many existing enterprise library systems are bloated, require dedicated web servers and cloud microservices, and are difficult to deploy for lightweight departmental or local desktop use. 

There is a clear need for a reliable, modular, and easy-to-use **Library Management System** built with Core Java and SQLite (via JDBC) that automates student records, book catalog inventory, book issues/returns, overdue fine calculation, and summary reporting without requiring external servers or complex configuration.

---

## 2. Scope of the Project

The Smart Campus Library Management System covers the core transactional and administrative lifecycle of an academic library:

* **Student Profile Management**: Enrolling students, validating email and phone numbers, and maintaining academic department and year-of-study records.
* **Book Catalog & Inventory**: Storing book records with details such as title, author, edition, ISBN, and genre classification, while maintaining real-time availability statuses.
* **Circulation Management**: Handling book checkout (issue) and check-in (return) workflows with atomic database transactions, automatic due-date tracking, and dynamic late-fine calculation.
* **Authentication & Role-Based Access Control**: Providing secure console login and registration with separate permission levels for Administrators, Librarians, and Students.
* **Background Activity Auditing**: Asynchronously logging transactions using a multithreaded daemon worker and thread-safe synchronized queues.
* **Reporting & File Handling**: Generating and reading summary reports saved as text files using character streams, tracking recent operations using a LIFO Stack, and compiling genre-wise inventory breakdown matrices.
* **Data Persistence**: Persistently storing all records in an embedded SQLite database using JDBC with externalized connection properties (`db.properties`).

### Boundaries & Exclusions
* The system is developed as a standalone, console-based desktop application.
* Cloud hosting, web frontends, and external payment gateway integrations are outside the scope of this project.

---

## 3. Target Users

The system is designed with role-based access for three primary user groups:

### 1. Administrators (`ADMIN`)
* Full system access across all modules.
* Manage user accounts, student records, and book catalog inventory.
* Monitor background threads and inspect audit logs.
* Generate and view system-wide executive summary reports.

### 2. Librarians (`LIBRARIAN`)
* Add, update, search, and manage books in the library catalog.
* Register new students and update student contact information.
* Process book issue and return operations.
* Assess late return penalties and collect fines.

### 3. Students (`STUDENT`)
* Search catalog inventory and verify book availability.
* Check personal borrowing history and active book loans.
* Review due dates and fine amounts.

---

## 4. High-Level Features

### 4.1 Authentication & User Role Module
* Console-based user login and registration portal.
* Role-based access control (`ADMIN`, `LIBRARIAN`, `STUDENT`) with tailored menu options.
* Session tracking displaying active username and role across all screens.
* Pre-configured test accounts for quick demonstration.

### 4.2 Student Management Module
* Add new students with format validation for email addresses and phone numbers.
* View all enrolled students in a formatted tabular display.
* Search students by numeric student ID or by name/department keyword.
* Update student contact information and year of study.

### 4.3 Book Catalog Management Module
* Add new books with title, author, edition, ISBN, and academic genre classification (`BookGenre` enum).
* View complete book inventory with live availability status (`YES` / `NO`).
* Search books by unique item ID or by title/author keywords.
* Update book metadata and edition details.
* Safe book deletion with protection against deleting currently issued books.

### 4.4 Circulation & Fine Management Module
* Issue available books to students with customizable loan duration (default 14 days).
* Enforce availability checks with custom exception handling (`BookNotAvailableException`).
* Return books with automatic overdue day detection.
* Overloaded fine calculation engine supporting standard rates (₹5/day) or custom rates.
* Student borrowing history lookup listing all past and active transactions.
* Atomic database transactions with commit and rollback protection.

### 4.5 Multithreading & Concurrency Module
* Background daemon thread (`AuditLogThread`) running asynchronously.
* Thread-safe synchronized message queues to log events without blocking user interaction.
* Thread diagnostic monitor showing thread state, ID, priority, and daemon status.

### 4.6 Reporting & File I/O Module
* Export executive summary reports to `library_summary_report.txt` using character-oriented streams (`BufferedWriter` and `PrintWriter`).
* Read and display persisted reports directly in the console using `BufferedReader` and `FileReader`.
* 2-D array matrix compiling total and available book counts per genre.
* LIFO activity tracking using `java.util.Stack` displaying recent operations in reverse chronological order.
