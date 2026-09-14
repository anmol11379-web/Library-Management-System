package com.library.main;

import com.library.dao.DatabaseManager;
import com.library.exception.AuthenticationException;
import com.library.exception.BookNotAvailableException;
import com.library.exception.LibraryException;
import com.library.exception.RecordNotFoundException;
import com.library.model.Book;
import com.library.model.BookGenre;
import com.library.model.Student;
import com.library.model.Transaction;
import com.library.model.User;
import com.library.model.UserRole;
import com.library.service.LibraryService;
import com.library.thread.AuditLogThread;
import com.library.util.InputValidator;
import com.library.util.ReportGenerator;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application: LibraryApp
 * Demonstrates:
 * - Authentication & Session Management: Login, Registration, Logout, Exit
 * - Flow Control: switch statements, while loops, if-else, break, continue (Unit 1)
 * - Object-Oriented Principles: Polymorphism, dynamic dispatch, encapsulation (Unit 2)
 * - Exception Handling: Multi-catch and structured error reporting (Unit 3)
 * - Multithreading: Thread lifecycle control (start, join) (Unit 3)
 * - Character Streams & File I/O (Unit 4)
 * - Collections & Stack operations (Unit 4)
 * - JDBC integration (Unit 5)
 */
public class LibraryApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static AuditLogThread auditLogger;
    private static LibraryService service;
    private static User currentUser = null;

    public static void main(String[] args) {
        // Start background auditing thread (Unit 3: Multithreading)
        auditLogger = new AuditLogThread();
        auditLogger.start();

        // Initialize business service
        service = new LibraryService(auditLogger);

        // Seed initial sample data and default user credentials if database is fresh
        seedInitialDataIfEmpty();

        System.out.println("===============================================================");
        System.out.println("    WELCOME TO THE SMART CAMPUS LIBRARY MANAGEMENT SYSTEM      ");
        System.out.println("===============================================================");

        boolean appRunning = true;
        while (appRunning) {
            printAuthPortalMenu();
            int choice = InputValidator.readInt(scanner, "Enter your choice (0-2): ");

            switch (choice) {
                case 1:
                    handleLogin();
                    if (currentUser != null) {
                        handleAuthenticatedSession();
                    }
                    break;
                case 2:
                    handleUserRegistration();
                    break;
                case 0:
                    appRunning = false;
                    shutdownSystem();
                    break;
                default:
                    System.out.println("(!) Invalid option. Please select 1, 2, or 0.");
            }
        }
        scanner.close();
    }

    private static void printAuthPortalMenu() {
        System.out.println("\n============= AUTHENTICATION PORTAL =============");
        System.out.println("1. Login");
        System.out.println("2. Register New User Account");
        System.out.println("0. Exit System");
        System.out.println("=================================================");
    }

    private static void handleLogin() {
        System.out.println("\n--- USER LOGIN ---");
        System.out.println("(Default accounts: admin/admin123, librarian/lib123, student/student123)");
        String username = InputValidator.readNonEmptyString(scanner, "Username: ");
        String password = InputValidator.readNonEmptyString(scanner, "Password: ");

        try {
            currentUser = service.login(username, password);
            System.out.println("\n[SUCCESS] Login successful! Welcome, " + currentUser.getFullName() + 
                    " (" + currentUser.getRole().getDisplayName() + ").");
        } catch (AuthenticationException e) {
            System.out.println("\n[AUTH ERROR] " + e.getMessage());
            currentUser = null;
        } catch (SQLException e) {
            System.err.println("\n[DB ERROR] Database error during authentication: " + e.getMessage());
            currentUser = null;
        }
    }

    private static void handleUserRegistration() {
        System.out.println("\n--- REGISTER NEW USER ---");
        String username = InputValidator.readNonEmptyString(scanner, "Enter desired username: ");
        String password = InputValidator.readNonEmptyString(scanner, "Enter password (min 4 chars): ");
        String fullName = InputValidator.readNonEmptyString(scanner, "Enter full name: ");

        System.out.println("Select Role:");
        System.out.println("1. Student");
        System.out.println("2. Librarian");
        System.out.println("3. Administrator");
        int roleChoice = InputValidator.readInt(scanner, "Role choice (1-3): ");
        UserRole role;
        switch (roleChoice) {
            case 2:
                role = UserRole.LIBRARIAN;
                break;
            case 3:
                role = UserRole.ADMIN;
                break;
            default:
                role = UserRole.STUDENT;
                break;
        }

        User newUser = new User(username, password, fullName, role);
        try {
            boolean created = service.registerUser(newUser);
            if (created) {
                System.out.println("\n[SUCCESS] User registered successfully! You can now log in with username '" + username + "'.");
            } else {
                System.out.println("\n[ERROR] Failed to register user.");
            }
        } catch (AuthenticationException e) {
            System.out.println("\n[REGISTRATION ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("\n[DB ERROR] Database error during registration: " + e.getMessage());
        }
    }

    private static void handleAuthenticatedSession() {
        boolean inSession = true;
        while (inSession) {
            printMainMenu();
            int choice = InputValidator.readInt(scanner, "Enter your choice (0-6): ");

            switch (choice) {
                case 1:
                    handleStudentMenu();
                    break;
                case 2:
                    handleBookMenu();
                    break;
                case 3:
                    handleIssueReturnMenu();
                    break;
                case 4:
                    handleAnalyticsAndIOMenu();
                    break;
                case 5:
                    handleAuditThreadMenu();
                    break;
                case 6:
                    handleLogout();
                    inSession = false;
                    break;
                case 0:
                    handleLogout();
                    inSession = false;
                    shutdownSystem();
                    System.exit(0);
                    break;
                default:
                    System.out.println("(!) Invalid option. Please select a valid number from the menu.");
            }
        }
    }

    private static void handleLogout() {
        if (currentUser != null) {
            service.logout(currentUser);
            System.out.println("\n[LOGOUT] User '" + currentUser.getUsername() + "' logged out successfully.");
            currentUser = null;
        }
    }

    private static void shutdownSystem() {
        System.out.println("\nShutting down Library Management System...");
        if (auditLogger != null) {
            auditLogger.stopAuditor();
        }
        DatabaseManager.getInstance().closeConnection();
        System.out.println("Cleanup complete. Goodbye!");
    }

    private static void printMainMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("1. Student Management");
        System.out.println("2. Book Management");
        System.out.println("3. Issue & Return Management");
        System.out.println("4. Reports");
        System.out.println("5. Audit Log & Thread Monitor");
        System.out.println("6. Logout");
        System.out.println("0. Exit System");
        System.out.println("---------------------------------------------");
    }

    // =========================================================================
    // 1. STUDENT MANAGEMENT SUBMENU
    // =========================================================================
    private static void handleStudentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [1] Student Management ---");
            System.out.println("1. Add New Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student (by ID or Name)");
            System.out.println("4. Update Student Details");
            System.out.println("0. Back to Main Menu");
            System.out.println("------------------------------");

            int choice = InputValidator.readInt(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1:
                        addStudentFlow();
                        break;
                    case 2:
                        viewAllStudentsFlow();
                        break;
                    case 3:
                        searchStudentFlow();
                        break;
                    case 4:
                        updateStudentFlow();
                        break;
                    case 0:
                        back = true;
                        break;
                    default:
                        System.out.println("(!) Invalid option.");
                }
            } catch (SQLException e) {
                System.out.println("(!) Database error: " + e.getMessage());
            } catch (RecordNotFoundException e) {
                System.out.println("(!) " + e.getMessage());
            }
        }
    }

    private static void addStudentFlow() throws SQLException {
        System.out.println("\n-> Adding New Student");
        String name = InputValidator.readNonEmptyString(scanner, "Enter Full Name: ");
        String email = InputValidator.readEmail(scanner, "Enter Student Email: ");
        String phone = InputValidator.readPhone(scanner, "Enter Phone Number: ");
        String dept = InputValidator.readNonEmptyString(scanner, "Enter Department (e.g. CSE, ECE, MECH): ");
        int year = InputValidator.readPositiveInt(scanner, "Enter Year of Study (1-5): ");

        Student student = new Student(0, name, email, phone, dept, year);
        boolean added = service.registerStudent(student);
        if (added) {
            System.out.println("[✓] Student successfully registered with ID: " + student.getId());
        } else {
            System.out.println("(!) Could not register student.");
        }
    }

    private static void viewAllStudentsFlow() throws SQLException {
        System.out.println("\n-> All Registered Students");
        List<Student> list = service.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No students currently registered.");
            return;
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-25s | %-12s | %-10s | %s%n", "ID", "Name", "Email", "Phone", "Dept", "Year");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (Student s : list) {
            System.out.printf("%-5d | %-20s | %-25s | %-12s | %-10s | Year %d%n",
                    s.getId(), s.getName(), s.getEmail(), s.getPhone(), s.getDepartment(), s.getYearOfStudy());
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    private static void searchStudentFlow() throws SQLException, RecordNotFoundException {
        System.out.println("\n-> Search Student Options:");
        System.out.println("1. Search by Student ID");
        System.out.println("2. Search by Name/Department");
        int subChoice = InputValidator.readInt(scanner, "Choose search mode: ");

        if (subChoice == 1) {
            int id = InputValidator.readPositiveInt(scanner, "Enter Student ID: ");
            Student s = service.searchStudent(id); // Overloaded search 1
            System.out.println("\n[✓] Student Found:");
            System.out.println(s);
            System.out.println("Role Details: " + s.getRoleDetails());
        } else if (subChoice == 2) {
            String q = InputValidator.readNonEmptyString(scanner, "Enter Name or Dept Keyword: ");
            List<Student> results = service.searchStudent(q); // Overloaded search 2
            if (results.isEmpty()) {
                System.out.println("No students matched keyword: " + q);
            } else {
                System.out.println("\n[✓] Matching Students (" + results.size() + " found):");
                for (Student s : results) {
                    System.out.println(s);
                }
            }
        } else {
            System.out.println("(!) Invalid search option.");
        }
    }

    private static void updateStudentFlow() throws SQLException, RecordNotFoundException {
        System.out.println("\n-> Update Student Details");
        int id = InputValidator.readPositiveInt(scanner, "Enter ID of student to update: ");
        Student s = service.searchStudent(id);

        System.out.println("Current details: " + s);
        System.out.println("Enter new values (press Enter to keep existing):");

        System.out.print("New Name [" + s.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) s.setName(name);

        System.out.print("New Email [" + s.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) s.setEmail(email);

        System.out.print("New Phone [" + s.getPhone() + "]: ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) s.setPhone(phone);

        System.out.print("New Department [" + s.getDepartment() + "]: ");
        String dept = scanner.nextLine().trim();
        if (!dept.isEmpty()) s.setDepartment(dept);

        System.out.print("New Year of Study [" + s.getYearOfStudy() + "]: ");
        String yrStr = scanner.nextLine().trim();
        if (!yrStr.isEmpty()) {
            try {
                int yr = Integer.parseInt(yrStr);
                s.setYearOfStudy(yr);
            } catch (NumberFormatException ignored) {}
        }

        service.updateStudent(s);
        System.out.println("[✓] Student details successfully updated!");
    }

    // =========================================================================
    // 2. BOOK MANAGEMENT SUBMENU
    // =========================================================================
    private static void handleBookMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [2] Book Management ---");
            System.out.println("1. Add New Book");
            System.out.println("2. View All Books (Polymorphic Catalog)");
            System.out.println("3. Search Book (by ID or Title)");
            System.out.println("4. Update Book Details");
            System.out.println("5. Remove Book");
            System.out.println("0. Back to Main Menu");
            System.out.println("---------------------------");

            int choice = InputValidator.readInt(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1:
                        addBookFlow();
                        break;
                    case 2:
                        service.displayAll(); // Interface Manageable<Book> method
                        break;
                    case 3:
                        searchBookFlow();
                        break;
                    case 4:
                        updateBookFlow();
                        break;
                    case 5:
                        removeBookFlow();
                        break;
                    case 0:
                        back = true;
                        break;
                    default:
                        System.out.println("(!) Invalid option.");
                }
            } catch (SQLException e) {
                System.out.println("(!) Database error: " + e.getMessage());
            } catch (LibraryException e) {
                System.out.println("(!) " + e.getMessage());
            }
        }
    }

    private static void addBookFlow() throws SQLException {
        System.out.println("\n-> Adding New Book");
        String title = InputValidator.readNonEmptyString(scanner, "Enter Title: ");
        String author = InputValidator.readNonEmptyString(scanner, "Enter Author: ");
        String isbn = InputValidator.readNonEmptyString(scanner, "Enter ISBN (e.g. 978-0134685991): ");
        int edition = InputValidator.readPositiveInt(scanner, "Enter Edition (1, 2, etc.): ");

        System.out.println("Select Genre:");
        BookGenre[] genres = BookGenre.values();
        for (int i = 0; i < genres.length; i++) {
            System.out.printf("  %d. %s (%s)%n", (i + 1), genres[i].getDisplayName(), genres[i].getCode());
        }
        int gChoice = InputValidator.readInt(scanner, "Choose Genre (1-" + genres.length + "): ");
        BookGenre selectedGenre = (gChoice >= 1 && gChoice <= genres.length) ? genres[gChoice - 1] : BookGenre.GENERAL;

        Book book = new Book(0, title, author, true, isbn, selectedGenre, edition);
        boolean added = service.addBook(book);
        if (added) {
            System.out.println("[✓] Book successfully added with ID: " + book.getItemId());
        } else {
            System.out.println("(!) Could not add book.");
        }
    }

    private static void searchBookFlow() throws SQLException, RecordNotFoundException {
        System.out.println("\n-> Search Book Options:");
        System.out.println("1. Search by Book ID");
        System.out.println("2. Search by Title / Keyword");
        int subChoice = InputValidator.readInt(scanner, "Choose search mode: ");

        if (subChoice == 1) {
            int id = InputValidator.readPositiveInt(scanner, "Enter Book ID: ");
            Book b = service.searchBook(id);
            System.out.println("\n[✓] Book Found:");
            System.out.println(b);
            System.out.println("Item Category: " + b.getItemType());
        } else if (subChoice == 2) {
            String kw = InputValidator.readNonEmptyString(scanner, "Enter Title or Author keyword: ");
            List<Book> list = service.searchBook(kw);
            if (list.isEmpty()) {
                System.out.println("No books matched keyword: " + kw);
            } else {
                System.out.println("\n[✓] Matching Books (" + list.size() + " found):");
                for (Book b : list) {
                    System.out.println(b);
                }
            }
        } else {
            System.out.println("(!) Invalid option.");
        }
    }

    private static void updateBookFlow() throws SQLException, RecordNotFoundException {
        System.out.println("\n-> Update Book Details");
        int id = InputValidator.readPositiveInt(scanner, "Enter ID of book to update: ");
        Book b = service.searchBook(id);

        System.out.println("Current details: " + b);
        System.out.println("Enter new values (press Enter to keep existing):");

        System.out.print("New Title [" + b.getTitle() + "]: ");
        String title = scanner.nextLine().trim();
        if (!title.isEmpty()) b.setTitle(title);

        System.out.print("New Author [" + b.getAuthor() + "]: ");
        String author = scanner.nextLine().trim();
        if (!author.isEmpty()) b.setAuthor(author);

        System.out.print("New ISBN [" + b.getIsbn() + "]: ");
        String isbn = scanner.nextLine().trim();
        if (!isbn.isEmpty()) b.setIsbn(isbn);

        System.out.print("New Edition [" + b.getEdition() + "]: ");
        String edStr = scanner.nextLine().trim();
        if (!edStr.isEmpty()) {
            try {
                int ed = Integer.parseInt(edStr);
                b.setEdition(ed);
            } catch (NumberFormatException ignored) {}
        }

        service.updateBook(b);
        System.out.println("[✓] Book details successfully updated!");
    }

    private static void removeBookFlow() throws SQLException, RecordNotFoundException, LibraryException {
        System.out.println("\n-> Remove Book");
        int id = InputValidator.readPositiveInt(scanner, "Enter ID of book to remove: ");
        Book b = service.searchBook(id);

        System.out.print("Are you sure you want to remove '" + b.getTitle() + "'? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            service.removeBook(id);
            System.out.println("[✓] Book removed successfully.");
        } else {
            System.out.println("Removal cancelled.");
        }
    }

    // =========================================================================
    // 3. ISSUE AND RETURN SUBMENU
    // =========================================================================
    private static void handleIssueReturnMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [3] Issue and Return Management ---");
            System.out.println("1. Issue a Book to a Student");
            System.out.println("2. Return a Book (with Late Fine Calculation)");
            System.out.println("3. Check Book Availability");
            System.out.println("4. Calculate Fine (Overloaded Method Calculator)");
            System.out.println("5. View Borrowing History by Student ID");
            System.out.println("6. View All Transactions");
            System.out.println("0. Back to Main Menu");
            System.out.println("---------------------------------------");

            int choice = InputValidator.readInt(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1:
                        issueBookFlow();
                        break;
                    case 2:
                        returnBookFlow();
                        break;
                    case 3:
                        checkAvailabilityFlow();
                        break;
                    case 4:
                        fineCalculatorFlow();
                        break;
                    case 5:
                        borrowingHistoryFlow();
                        break;
                    case 6:
                        viewAllTransactionsFlow();
                        break;
                    case 0:
                        back = true;
                        break;
                    default:
                        System.out.println("(!) Invalid option.");
                }
            } catch (SQLException e) {
                System.out.println("(!) Database error: " + e.getMessage());
            } catch (BookNotAvailableException e) {
                System.out.println("(!) Availability conflict: " + e.getMessage());
            } catch (LibraryException e) {
                System.out.println("(!) " + e.getMessage());
            }
        }
    }

    private static void issueBookFlow() throws SQLException, RecordNotFoundException, BookNotAvailableException {
        System.out.println("\n-> Issue Book");
        int studentId = InputValidator.readPositiveInt(scanner, "Enter Student ID: ");
        int bookId = InputValidator.readPositiveInt(scanner, "Enter Book ID: ");
        int loanDays = InputValidator.readPositiveInt(scanner, "Enter Loan Period in Days (Default 14): ");

        Transaction txn = service.issueBook(studentId, bookId, loanDays);
        System.out.println("\n[✓] Book issued successfully!");
        System.out.println(txn);
    }

    private static void returnBookFlow() throws SQLException, RecordNotFoundException, LibraryException {
        System.out.println("\n-> Return Book");
        int studentId = InputValidator.readPositiveInt(scanner, "Enter Student ID: ");
        int bookId = InputValidator.readPositiveInt(scanner, "Enter Book ID: ");

        System.out.println("Return date simulation options:");
        System.out.println("1. Return Today (" + LocalDate.now() + ")");
        System.out.println("2. Simulate Late Return (e.g. +5 days overdue to demonstrate fine)");
        int simChoice = InputValidator.readInt(scanner, "Choose option: ");

        LocalDate returnDate = LocalDate.now();
        if (simChoice == 2) {
            int extraDays = InputValidator.readPositiveInt(scanner, "Enter overdue days to simulate: ");
            // Look up active transaction to set past due date + extraDays
            Transaction txn = service.getAllTransactions().stream()
                    .filter(t -> t.getBookId() == bookId && t.getStudentId() == studentId && t.getReturnDate() == null)
                    .findFirst().orElse(null);
            if (txn != null) {
                returnDate = txn.getDueDate().plusDays(extraDays);
            } else {
                returnDate = LocalDate.now().plusDays(extraDays);
            }
        }

        Transaction completedTxn = service.returnBook(studentId, bookId, returnDate);
        System.out.println("\n[✓] Book returned successfully!");
        System.out.println(completedTxn);
        if (completedTxn.getFineAmount() > 0) {
            System.out.printf("(!) LATE RETURN DETECTED: A fine of ₹%.2f has been charged!%n", completedTxn.getFineAmount());
        } else {
            System.out.println("[✓] Book returned on time. No fine charged.");
        }
    }

    private static void checkAvailabilityFlow() throws SQLException, RecordNotFoundException {
        System.out.println("\n-> Check Book Availability");
        int bookId = InputValidator.readPositiveInt(scanner, "Enter Book ID: ");
        Book b = service.searchBook(bookId);

        System.out.println("Book: " + b.getTitle() + " by " + b.getAuthor());
        if (b.isAvailable()) {
            System.out.println("[✓] STATUS: AVAILABLE for issue.");
        } else {
            System.out.println("[✗] STATUS: CURRENTLY ISSUED / UNAVAILABLE.");
        }
    }

    private static void fineCalculatorFlow() {
        System.out.println("\n-> Standalone Fine Calculator");
        int lateDays = InputValidator.readPositiveInt(scanner, "Enter overdue days: ");

        System.out.println("1. Standard Rate (₹5.00/day)");
        System.out.println("2. Custom Rate per Day");
        int rateChoice = InputValidator.readInt(scanner, "Choose mode: ");

        if (rateChoice == 1) {
            double fine = service.calculateFine(lateDays);
            System.out.printf("[✓] Fine for %d days late @ ₹%.2f/day = ₹%.2f%n", lateDays, LibraryService.DEFAULT_FINE_PER_DAY, fine);
        } else if (rateChoice == 2) {
            int rate = InputValidator.readPositiveInt(scanner, "Enter custom rate in ₹: ");
            double fine = service.calculateFine(lateDays, rate);
            System.out.printf("[✓] Fine for %d days late @ ₹%d/day = ₹%.2f%n", lateDays, rate, fine);
        } else {
            System.out.println("(!) Invalid mode.");
        }
    }

    private static void borrowingHistoryFlow() throws SQLException, RecordNotFoundException {
        System.out.println("\n-> Student Borrowing History");
        int studentId = InputValidator.readPositiveInt(scanner, "Enter Student ID: ");
        Student s = service.searchStudent(studentId);
        List<Transaction> history = service.getStudentBorrowingHistory(studentId);

        System.out.println("\nCirculation History for: " + s.getName() + " (" + s.getDepartment() + ")");
        if (history.isEmpty()) {
            System.out.println("No borrowing records found for this student.");
        } else {
            System.out.println("---------------------------------------------------------------------------------------------------------");
            for (Transaction t : history) {
                System.out.println(t);
            }
            System.out.println("---------------------------------------------------------------------------------------------------------");
        }
    }

    private static void viewAllTransactionsFlow() throws SQLException {
        System.out.println("\n-> All Library Transactions");
        List<Transaction> list = service.getAllTransactions();
        if (list.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Transaction t : list) {
            System.out.println(t);
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }

    // =========================================================================
    // 4. REPORTS SUBMENU
    // =========================================================================
    private static void handleAnalyticsAndIOMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [4] Library Reports ---");
            System.out.println("1. Generate & Export Summary Report to Text File");
            System.out.println("2. Read & Display Report from Text File");
            System.out.println("3. Display 2-D Array Genre Breakdown Matrix");
            System.out.println("4. View Recent Operations Stack");
            System.out.println("0. Back to Main Menu");
            System.out.println("------------------------------------------------------");

            int choice = InputValidator.readInt(scanner, "Enter choice: ");
            try {
                switch (choice) {
                    case 1:
                        ReportGenerator.generateReport(service, "library_summary_report.txt");
                        System.out.println("[✓] Report successfully written to 'library_summary_report.txt'!");
                        break;
                    case 2:
                        System.out.println("\nReading report file:");
                        String content = ReportGenerator.readReport("library_summary_report.txt");
                        System.out.println(content);
                        break;
                    case 3:
                        System.out.println("\n-> 2-D Array Matrix: Inventory Breakdown by Genre:");
                        String[][] matrix = service.getGenreStatisticsMatrix();
                        System.out.println("-----------------------------------------------------------------");
                        System.out.printf("%-35s | %-12s | %s%n", "Genre Category", "Total Count", "Available");
                        System.out.println("-----------------------------------------------------------------");
                        for (String[] row : matrix) {
                            System.out.printf("%-35s | %-12s | %s%n", row[0], row[1], row[2]);
                        }
                        System.out.println("-----------------------------------------------------------------");
                        break;
                    case 4:
                        System.out.println("\n-> Recent Operations History (from LIFO java.util.Stack):");
                        List<String> activities = service.getRecentActivities(10);
                        if (activities.isEmpty()) {
                            System.out.println("Stack is currently empty.");
                        } else {
                            for (int i = 0; i < activities.size(); i++) {
                                System.out.printf("[%d] %s%n", (i + 1), activities.get(i));
                            }
                        }
                        break;
                    case 0:
                        back = true;
                        break;
                    default:
                        System.out.println("(!) Invalid option.");
                }
            } catch (IOException e) {
                System.out.println("(!) File I/O Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("(!) Database error: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // 5. AUDIT & MULTITHREADING MONITOR
    // =========================================================================
    private static void handleAuditThreadMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [5] Audit Log & Thread Monitor (Unit 3) ---");
            System.out.println("1. View Processed Audit Logs (Thread-Safe Synchronization)");
            System.out.println("2. Inspect Background Thread Lifecycle Status");
            System.out.println("0. Back to Main Menu");
            System.out.println("-----------------------------------------------");

            int choice = InputValidator.readInt(scanner, "Enter choice: ");
            switch (choice) {
                case 1:
                    List<String> audits = auditLogger.getRecentAudits();
                    System.out.println("\n-> Background Worker Thread Audit Logs (" + audits.size() + " entries):");
                    if (audits.isEmpty()) {
                        System.out.println("No logs recorded yet or pending background flush.");
                    } else {
                        for (String log : audits) {
                            System.out.println(log);
                        }
                    }
                    break;
                case 2:
                    System.out.println("\n-> Thread Monitor Inspection:");
                    System.out.println("Thread Name     : " + auditLogger.getName());
                    System.out.println("Thread ID       : " + auditLogger.threadId());
                    System.out.println("Thread State    : " + auditLogger.getState());
                    System.out.println("Is Alive?       : " + auditLogger.isAlive());
                    System.out.println("Is Daemon?      : " + auditLogger.isDaemon());
                    System.out.println("Thread Priority : " + auditLogger.getPriority());
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("(!) Invalid option.");
            }
        }
    }

    // =========================================================================
    // INITIAL SEED DATA
    // =========================================================================
    private static void seedInitialDataIfEmpty() {
        try {
            service.seedDefaultUsers();

            if (service.getAllStudents().isEmpty()) {
                Student s1 = new Student(0, "Aarav Sharma", "aarav.sharma@vitbhopal.ac.in", "9876543210", "CSE", 2);
                Student s2 = new Student(0, "Diya Patel", "diya.patel@vitbhopal.ac.in", "9876543211", "ECE", 3);
                Student s3 = new Student(0, "Rohan Verma", "rohan.verma@vitbhopal.ac.in", "9876543212", "MECH", 1);
                service.registerStudent(s1);
                service.registerStudent(s2);
                service.registerStudent(s3);
            }

            if (service.getAll().isEmpty()) {
                Book b1 = new Book(0, "Introduction to Java Programming", "Y. Daniel Liang", true, "978-0134611037", BookGenre.COMPUTER_SCIENCE, 11);
                Book b2 = new Book(0, "Java: The Complete Reference", "Herbert Schildt", true, "978-1260440232", BookGenre.COMPUTER_SCIENCE, 11);
                Book b3 = new Book(0, "Discrete Mathematics", "Kenneth Rosen", true, "978-0073383095", BookGenre.MATHEMATICS, 7);
                Book b4 = new Book(0, "Concepts of Modern Physics", "Arthur Beiser", true, "978-9351341857", BookGenre.PHYSICS, 6);
                Book b5 = new Book(0, "To Kill a Mockingbird", "Harper Lee", true, "978-0060935467", BookGenre.FICTION, 1);

                service.addBook(b1);
                service.addBook(b2);
                service.addBook(b3);
                service.addBook(b4);
                service.addBook(b5);
            }
        } catch (SQLException e) {
            System.err.println("Notice: Could not pre-seed data: " + e.getMessage());
        }
    }
}
