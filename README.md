# Library Management System

A simple Java-based library management system made to manage books, student records, book issue and return details, and basic reports.

The project is built using Core Java and uses SQLite with JDBC to store the data. It also includes multithreading, file handling, collections, exception handling, and other Java concepts used in the project.

---

## Table of Contents

1. [Project Title](#smart-campus-library-management-system)
2. [Overview of the Project](#overview-of-the-project)
3. [Features](#features)
4. [Technologies & Tools Used](#technologies--tools-used)
5. [Steps to Install & Run the Project](#steps-to-install--run-the-project)
6. [Instructions for Testing](#instructions-for-testing)
7. [Screenshots](#screenshots)
8. [Project Directory Structure](#project-directory-structure)

---

## Overview of the Project

The Smart Campus Library Management System is a console-based Java application designed to make basic library work easier to manage.

Instead of maintaining book records, student details, and issue/return information manually, the system keeps everything organized in a database.

### Problem

Managing a library manually can lead to problems such as:

* Keeping track of which books are available
* Maintaining student records
* Tracking issued and returned books
* Calculating late fines
* Keeping a record of previous transactions
* Managing different types of users

### Solution

This project provides a simple desktop-based solution for these tasks.

The application is written in Core Java and uses Object-Oriented Programming concepts. SQLite is used as the database, and JDBC is used to connect the Java application with the database.

The project is completely console-based, so it does not require a web server or any external database server.

### Users

The system has three types of users:

* **Admin:** Has access to all major features, including student management, book management, reports, and thread monitoring.
* **Librarian:** Can manage books and students and handle book issue and return operations.
* **Student:** Can view available books and check their borrowing history.

---

## Features

### 1. Login and User Roles

* Users can log in through the console.
* New users can also register.
* The system has three roles: `ADMIN`, `LIBRARIAN`, and `STUDENT`.
* Each role has different permissions.
* The currently logged-in user's name and role are shown in the menus.
* Some default accounts are already available for testing.

### 2. Student Management

The system can be used to:

* Add new students.
* Store student details such as name, email, phone number, department, and year of study.
* Validate email and phone number formats.
* Search students using their ID, name, or department.
* Update existing student details.

### 3. Book Management

The book section allows users to:

* Add new books.
* Assign books to different genres such as Computer Science, Mathematics, Physics, and Fiction.
* View the complete book list.
* Check whether a book is available or already issued.
* Search books using their ID, title, or author.
* Prevent deletion of a book if it is currently issued.

### 4. Book Issue and Return

The system handles the complete book issue and return process.

When a book is issued:

* The system first checks whether the book is available.
* A loan record is created.
* The book's availability is updated.
* The database transaction is handled using JDBC.

When a book is returned:

* The system checks the due date and return date.
* If the book is returned late, the fine is calculated automatically.
* The default fine is ₹5 per day, with support for custom fine rates.
* Students can also view their previous borrowing records.

### 5. Multithreading

The project also includes a separate thread for keeping track of system activities.

* `AuditLogThread` runs in the background.
* It records important transaction activities.
* A synchronized message queue is used so that multiple operations can be handled safely.
* The application also provides information about the thread, such as its state, ID, priority, and whether it is a daemon thread.

### 6. Reports and File Handling

The system can generate and read library reports using character streams.

* `BufferedWriter` and `PrintWriter` are used to create reports.
* `BufferedReader` and `FileReader` are used to read reports.
* Reports are saved in `library_summary_report.txt`.
* A 2-D array is used to show the number of total and available books for each genre.
* A `Stack` is used to keep track of recent system and user activities.

---

## Technologies & Tools Used

| Category            | Technology / Tool          | Purpose                                                 |
| ------------------- | -------------------------- | ------------------------------------------------------- |
| Language            | Java SE                    | Main programming language used for the project          |
| Database            | SQLite                     | Stores books, students, users, and transaction records  |
| Database Connection | JDBC                       | Connects Java with the SQLite database                  |
| Configuration       | `db.properties`            | Stores database connection settings                     |
| JDBC Driver         | `sqlite-jdbc-3.45.1.0.jar` | Allows Java to work with SQLite                         |
| Logging             | SLF4J                      | Used for application logging                            |
| Design Patterns     | Singleton, DAO             | Helps organize database and application code            |
| Testing             | `TestRunner`               | Runs automated tests for different parts of the project |
| Build/Run           | `javac`, `java`            | Used to compile and run the Java application            |

The project uses concepts such as:

* OOP
* Inheritance
* Generics
* Enums
* Collections
* Exception handling
* JDBC
* Multithreading
* File handling
* Interfaces
* Stack
* 2-D arrays

---

## Steps to Install & Run the Project

### Prerequisites

Before running the project, make sure you have:

* Java JDK 8 or above installed.
* Windows, Linux, or macOS.
* The required JAR files inside the `lib` folder.

You can check your Java installation using:

```bash
java -version
javac -version
```

No separate database server is required because the project uses SQLite.

---

### Step 1: Open the Project Folder

Clone the repository or open the project folder and navigate to it:

```bash
cd "Java Library Management System"
```

---

### Step 2: Compile the Project

Compile the Java files into the `bin` folder.

**Windows:**

```powershell
javac -cp "lib/*;src" -d bin src/com/library/model/*.java src/com/library/exception/*.java src/com/library/dao/*.java src/com/library/service/*.java src/com/library/thread/*.java src/com/library/util/*.java src/com/library/main/*.java src/com/library/test/*.java
```

**Linux/macOS:**

```bash
javac -cp "lib/*:src" -d bin src/com/library/model/*.java src/com/library/exception/*.java src/com/library/dao/*.java src/com/library/service/*.java src/com/library/thread/*.java src/com/library/util/*.java src/com/library/main/*.java src/com/library/test/*.java
```

---

### Step 3: Run the Application

After compiling, start the library management system.

**Windows:**

```powershell
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.library.main.LibraryApp
```

**Linux/macOS:**

```bash
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/*" com.library.main.LibraryApp
```

---

## Demo Login Details

The project already has a few accounts that can be used for testing.

| Username    | Password     | Role      | Access                            |
| ----------- | ------------ | --------- | --------------------------------- |
| `admin`     | `admin123`   | Admin     | Full access                       |
| `librarian` | `lib123`     | Librarian | Books, students, issue and return |
| `student`   | `student123` | Student   | View books and borrowing history  |

---

## Instructions for Testing

The project includes a separate `TestRunner` class that checks the main features of the system.

There are **17 automated tests** covering different parts of the application.

### Running the Tests

**Windows:**

```powershell
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" com.library.test.TestRunner
```

**Linux/macOS:**

```bash
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/*" com.library.test.TestRunner
```

### Tests Included

1. Registering and retrieving a student
2. Searching students
3. Updating student details
4. Adding and retrieving a book
5. Searching books
6. Checking book availability
7. Issuing a book
8. Handling an unavailable book using a custom exception
9. Returning a book and calculating the fine
10. Testing the activity stack
11. Testing the 2-D genre statistics array
12. Testing character-based file reading and writing
13. Testing multithreading and the audit log
14. Testing a valid login
15. Testing an invalid password
16. Testing new user registration
17. Testing logout and audit logging

---

## Screenshots

### 1. Login and User Roles

Shows the login screen, user authentication, role detection, and the main menu.

<img width="900" height="808" alt="image" src="https://github.com/user-attachments/assets/58d15c98-8777-4327-9137-386bf04beab7" />

### 2. Book Management

Shows the book list with details such as ID, title, author, availability, genre, and ISBN.

<img width="1065" height="647" alt="image" src="https://github.com/user-attachments/assets/bbac9f6c-d118-4769-b52e-56053237cb68" />


### 3. Book Issue and Return

Shows the process of issuing and returning a book, including overdue fine calculation.

<img width="900" height="831" alt="image" src="https://github.com/user-attachments/assets/970736f5-c27f-446e-9e36-a7c1cc837a3d" />


### 4. Reports and File Handling

Shows the generated report, genre-wise book statistics, and character stream file handling.

<img width="900" height="762" alt="image" src="https://github.com/user-attachments/assets/d110b7c2-25a1-44fe-ae56-0da364baf8e4" />


---

### Database Structure

The database contains four main tables:

* **STUDENT** – stores student information.
* **BOOK** – stores book details and availability.
* **TRANSACTION** – stores issue and return records.
* **USER** – stores login and role information.

```text
STUDENT
- id
- name
- email
- phone
- department
- year_of_study

BOOK
- item_id
- title
- author
- available
- isbn
- genre
- edition

TRANSACTION
- transaction_id
- student_id
- book_id
- issue_date
- due_date
- return_date
- fine_amount
- status

USER
- id
- username
- password
- full_name
- role
```

A student can have multiple transaction records, and each transaction is connected to a book.

---

## Project Directory Structure

```text
Java Library Management System/

├── bin/
│   └── Compiled Java files
│
├── lib/
│   ├── sqlite-jdbc-3.45.1.0.jar
│   ├── slf4j-api-1.7.36.jar
│   └── slf4j-simple-1.7.36.jar
│
├── screenshots/
│   ├── 01_automated_test_suite.png
│   ├── 02_auth_portal_login.png
│   ├── 03_book_catalog_inventory.png
│   ├── 04_circulation_issue_return.png
│   └── 05_executive_summary_report.png
│
├── scripts/
│   └── generate_screenshots.py
│
├── src/
│   └── com/
│       └── library/
│           │
│           ├── model/
│           │   ├── Person.java
│           │   ├── Student.java
│           │   ├── LibraryItem.java
│           │   ├── Book.java
│           │   ├── Transaction.java
│           │   ├── User.java
│           │   ├── BookGenre.java
│           │   ├── TransactionStatus.java
│           │   └── UserRole.java
│           │
│           ├── dao/
│           │   ├── DatabaseManager.java
│           │   ├── StudentDAO.java
│           │   ├── BookDAO.java
│           │   ├── TransactionDAO.java
│           │   └── UserDAO.java
│           │
│           ├── service/
│           │   ├── Manageable.java
│           │   └── LibraryService.java
│           │
│           ├── thread/
│           │   └── AuditLogThread.java
│           │
│           ├── exception/
│           │   ├── LibraryException.java
│           │   ├── AuthenticationException.java
│           │   ├── BookNotAvailableException.java
│           │   └── RecordNotFoundException.java
│           │
│           ├── util/
│           │   ├── InputValidator.java
│           │   └── ReportGenerator.java
│           │
│           ├── main/
│           │   └── LibraryApp.java
│           │
│           └── test/
│               └── TestRunner.java
│
├── db.properties
├── library.db
├── library_summary_report.txt
├── statement.md
└── README.md
```

---

## References

1. Herbert Schildt, *Java: The Complete Reference*, 11th Edition, Oracle Press / McGraw-Hill.
2. Cay S. Horstmann, *Core Java Volume I – Fundamentals*, 11th Edition, Pearson.
3. Oracle Java SE Documentation.
4. SQLite JDBC Driver Repository.
