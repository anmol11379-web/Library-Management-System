package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.DatabaseManager;
import com.library.dao.StudentDAO;
import com.library.dao.TransactionDAO;
import com.library.dao.UserDAO;
import com.library.exception.AuthenticationException;
import com.library.exception.BookNotAvailableException;
import com.library.exception.LibraryException;
import com.library.exception.RecordNotFoundException;
import com.library.model.Book;
import com.library.model.Student;
import com.library.model.Transaction;
import com.library.model.TransactionStatus;
import com.library.model.User;
import com.library.model.UserRole;
import com.library.thread.AuditLogThread;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Service Layer: LibraryService
 * Coordinates operations between DAOs, Business rules, and Thread logger.
 * Demonstrates:
 * - Interface implementation (Manageable) (Unit 2)
 * - Method Overloading (calculateFine, searchBook, searchStudent) (Unit 2)
 * - 'final' constants (Unit 2)
 * - Java Collections Framework: ArrayList and Stack (Unit 4)
 * - Exception Handling (throw, throws, custom exceptions) (Unit 3)
 * - Database Transactions with JDBC commit/rollback (Unit 5)
 * - 2D Array usage for category breakdown (Unit 4)
 */
public class LibraryService implements Manageable<Book> {
    // Final constants (Unit 2)
    public static final double DEFAULT_FINE_PER_DAY = 5.0; // ₹5 per day
    public static final int STANDARD_LOAN_DAYS = 14;

    private final StudentDAO studentDAO;
    private final BookDAO bookDAO;
    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;
    private final AuditLogThread auditLogger;

    // Java Stack Collection for tracking recent operations (Unit 4: Java Stack)
    private final Stack<String> recentActivityStack;

    public LibraryService(AuditLogThread auditLogger) {
        this.studentDAO = new StudentDAO();
        this.bookDAO = new BookDAO();
        this.transactionDAO = new TransactionDAO();
        this.userDAO = new UserDAO();
        this.auditLogger = auditLogger;
        this.recentActivityStack = new Stack<>();
    }

    // ==========================================
    // 1. STUDENT MANAGEMENT
    // ==========================================

    public boolean registerStudent(Student student) throws SQLException {
        boolean success = studentDAO.insert(student);
        if (success) {
            String msg = "Registered new student: " + student.getName() + " (ID: " + student.getId() + ")";
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);
        }
        return success;
    }

    public boolean updateStudent(Student student) throws SQLException, RecordNotFoundException {
        Student existing = studentDAO.findById(student.getId());
        if (existing == null) {
            throw new RecordNotFoundException("Student", student.getId());
        }
        boolean success = studentDAO.update(student);
        if (success) {
            String msg = "Updated details for student ID: " + student.getId();
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);
        }
        return success;
    }

    // Overloaded searchStudent method 1: By ID (Unit 2 Method Overloading)
    public Student searchStudent(int id) throws SQLException, RecordNotFoundException {
        Student s = studentDAO.findById(id);
        if (s == null) {
            throw new RecordNotFoundException("Student", id);
        }
        return s;
    }

    // Overloaded searchStudent method 2: By Name/Department string (Unit 2 Method Overloading)
    public List<Student> searchStudent(String query) throws SQLException {
        return studentDAO.searchByNameOrDept(query);
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    // ==========================================
    // 2. BOOK MANAGEMENT (Implements Manageable<Book>)
    // ==========================================

    public boolean addBook(Book book) throws SQLException {
        boolean success = bookDAO.insert(book);
        if (success) {
            String msg = "Added new book: '" + book.getTitle() + "' (ID: " + book.getItemId() + ")";
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);
        }
        return success;
    }

    public boolean updateBook(Book book) throws SQLException, RecordNotFoundException {
        Book existing = bookDAO.findById(book.getItemId());
        if (existing == null) {
            throw new RecordNotFoundException("Book", book.getItemId());
        }
        boolean success = bookDAO.update(book);
        if (success) {
            String msg = "Updated book ID: " + book.getItemId() + " (" + book.getTitle() + ")";
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);
        }
        return success;
    }

    public boolean removeBook(int bookId) throws SQLException, RecordNotFoundException, LibraryException {
        Book existing = bookDAO.findById(bookId);
        if (existing == null) {
            throw new RecordNotFoundException("Book", bookId);
        }
        if (!existing.isAvailable()) {
            throw new LibraryException("Cannot remove Book ID " + bookId + " because it is currently issued to a student.");
        }
        boolean success = bookDAO.delete(bookId);
        if (success) {
            String msg = "Removed book: '" + existing.getTitle() + "' (ID: " + bookId + ")";
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);
        }
        return success;
    }

    // Manageable interface implementation
    @Override
    public Book searchById(int id) throws SQLException, RecordNotFoundException {
        return searchBook(id);
    }

    // Overloaded searchBook method 1: By ID (Unit 2)
    public Book searchBook(int id) throws SQLException, RecordNotFoundException {
        Book b = bookDAO.findById(id);
        if (b == null) {
            throw new RecordNotFoundException("Book", id);
        }
        return b;
    }

    // Overloaded searchBook method 2: By Title or Keyword (Unit 2)
    public List<Book> searchBook(String titleQuery) throws SQLException {
        return bookDAO.findByTitle(titleQuery);
    }

    @Override
    public List<Book> getAll() throws SQLException {
        return bookDAO.findAll();
    }

    @Override
    public void displayAll() throws SQLException {
        List<Book> books = getAll();
        if (books.isEmpty()) {
            System.out.println("No books found in library catalogue.");
        } else {
            System.out.println("-------------------------------------------------------------------------------------------------------");
            System.out.printf("%-6s | %-30s | %-20s | %-12s | %-16s | %s%n", "ID", "Title", "Author", "Available", "Genre", "ISBN");
            System.out.println("-------------------------------------------------------------------------------------------------------");
            for (Book b : books) {
                System.out.printf("%-6d | %-30s | %-20s | %-12s | %-16s | %s%n",
                        b.getItemId(),
                        truncate(b.getTitle(), 30),
                        truncate(b.getAuthor(), 20),
                        (b.isAvailable() ? "YES" : "NO"),
                        b.getGenre().getCode(),
                        b.getIsbn());
            }
            System.out.println("-------------------------------------------------------------------------------------------------------");
        }
    }

    // ==========================================
    // 3. ISSUE AND RETURN MANAGEMENT
    // ==========================================

    /**
     * Issues a book to a student with database transaction atomicity (Unit 5).
     */
    public Transaction issueBook(int studentId, int bookId, int loanDays)
            throws SQLException, RecordNotFoundException, BookNotAvailableException {
        // Validate student exists
        Student student = searchStudent(studentId);

        // Validate book exists and is available
        Book book = searchBook(bookId);
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(bookId);
        }

        Connection conn = DatabaseManager.getInstance().getConnection();
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false); // Begin transaction

            Transaction txn = new Transaction(studentId, bookId, loanDays > 0 ? loanDays : STANDARD_LOAN_DAYS);
            transactionDAO.insert(txn);

            bookDAO.updateAvailability(bookId, false);

            conn.commit(); // Commit transaction

            String msg = String.format("Issued Book #%d ('%s') to Student #%d (%s) - Due: %s",
                    bookId, book.getTitle(), studentId, student.getName(), txn.getDueDate());
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);

            return txn;
        } catch (SQLException e) {
            conn.rollback(); // Rollback on error
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    /**
     * Returns a book, calculates fine if overdue, updates database records.
     */
    public Transaction returnBook(int studentId, int bookId, LocalDate actualReturnDate)
            throws SQLException, RecordNotFoundException, LibraryException {
        // Verify student & book exist
        searchStudent(studentId);
        Book book = searchBook(bookId);

        // Locate active transaction
        Transaction txn = transactionDAO.findActiveIssue(bookId, studentId);
        if (txn == null) {
            throw new RecordNotFoundException("Active Issue Record", "Student " + studentId + " & Book " + bookId);
        }

        LocalDate returnDate = (actualReturnDate != null) ? actualReturnDate : LocalDate.now();
        txn.setReturnDate(returnDate);

        // Calculate days late
        long daysLate = ChronoUnit.DAYS.between(txn.getDueDate(), returnDate);
        double fine = 0.0;
        if (daysLate > 0) {
            fine = calculateFine(daysLate); // Overloaded method call
            txn.setStatus(TransactionStatus.OVERDUE);
        } else {
            txn.setStatus(TransactionStatus.RETURNED);
        }
        txn.setFineAmount(fine);

        Connection conn = DatabaseManager.getInstance().getConnection();
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            transactionDAO.update(txn);
            bookDAO.updateAvailability(bookId, true);

            conn.commit();

            String msg = String.format("Returned Book #%d ('%s') from Student #%d - Days Late: %d, Fine: ₹%.2f",
                    bookId, book.getTitle(), studentId, Math.max(0, daysLate), fine);
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);

            return txn;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    // Overloaded fine calculation 1: Standard daily rate (Unit 2 Method Overloading)
    public double calculateFine(long daysLate) {
        return calculateFine(daysLate, DEFAULT_FINE_PER_DAY);
    }

    // Overloaded fine calculation 2: Custom daily rate (Unit 2 Method Overloading)
    public double calculateFine(long daysLate, double ratePerDay) {
        if (daysLate <= 0) return 0.0;
        return daysLate * ratePerDay;
    }

    public boolean isBookAvailable(int bookId) throws SQLException, RecordNotFoundException {
        Book book = searchBook(bookId);
        return book.isAvailable();
    }

    public List<Transaction> getStudentBorrowingHistory(int studentId) throws SQLException, RecordNotFoundException {
        searchStudent(studentId); // Verify student exists
        return transactionDAO.findByStudentId(studentId);
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        return transactionDAO.findAll();
    }

    // ==========================================
    // 4. ACTIVITY STACK & STATISTICS
    // ==========================================

    /**
     * Inspect recent operations using Stack (Unit 4: Java Stack).
     */
    public List<String> getRecentActivities(int count) {
        List<String> result = new ArrayList<>();
        Stack<String> temp = new Stack<>();
        int collected = 0;

        // Pop elements to view most recent first
        while (!recentActivityStack.isEmpty() && collected < count) {
            String item = recentActivityStack.pop();
            result.add(item);
            temp.push(item);
            collected++;
        }

        // Restore elements back to original stack
        while (!temp.isEmpty()) {
            recentActivityStack.push(temp.pop());
        }

        return result;
    }

    /**
     * Generates a 2D Array summarizing genre-wise inventory (Unit 4: 2D Arrays).
     * Row format: [Genre Name, Total Books, Available Books]
     */
    public String[][] getGenreStatisticsMatrix() throws SQLException {
        List<Book> books = getAll();
        com.library.model.BookGenre[] genres = com.library.model.BookGenre.values();
        String[][] matrix = new String[genres.length][3];

        for (int i = 0; i < genres.length; i++) {
            com.library.model.BookGenre g = genres[i];
            int total = 0;
            int available = 0;
            for (Book b : books) {
                if (b.getGenre() == g) {
                    total++;
                    if (b.isAvailable()) available++;
                }
            }
            matrix[i][0] = g.getDisplayName();
            matrix[i][1] = String.valueOf(total);
            matrix[i][2] = String.valueOf(available);
        }
        return matrix;
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }

    // ==========================================
    // 5. USER AUTHENTICATION & MANAGEMENT
    // ==========================================

    public User login(String username, String password) throws AuthenticationException, SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty.");
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            String failMsg = "Failed login attempt for username: '" + username + "'";
            recentActivityStack.push(failMsg);
            auditLogger.recordEvent(failMsg);
            throw new AuthenticationException("Invalid username or password. Please try again.");
        }

        String successMsg = "User logged in: " + user.getUsername() + " (Role: " + user.getRole().getDisplayName() + ")";
        recentActivityStack.push(successMsg);
        auditLogger.recordEvent(successMsg);
        return user;
    }

    public boolean registerUser(User user) throws AuthenticationException, SQLException {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            throw new AuthenticationException("Password must be at least 4 characters long.");
        }
        if (userDAO.usernameExists(user.getUsername())) {
            throw new AuthenticationException("Username '" + user.getUsername() + "' is already taken.");
        }

        boolean success = userDAO.insert(user);
        if (success) {
            String msg = "Registered new user: " + user.getUsername() + " (" + user.getRole().getDisplayName() + ")";
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);
        }
        return success;
    }

    public void logout(User user) {
        if (user != null) {
            String msg = "User logged out: " + user.getUsername();
            recentActivityStack.push(msg);
            auditLogger.recordEvent(msg);
        }
    }

    public void seedDefaultUsers() throws SQLException {
        userDAO.seedDefaultUsers();
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }
}
