package com.library.test;

import com.library.dao.DatabaseManager;
import com.library.exception.BookNotAvailableException;
import com.library.exception.LibraryException;
import com.library.exception.RecordNotFoundException;
import com.library.model.Book;
import com.library.model.BookGenre;
import com.library.model.Student;
import com.library.model.Transaction;
import com.library.service.LibraryService;
import com.library.thread.AuditLogThread;
import com.library.util.ReportGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Automated Test Runner
 * Validates all functional and syllabus requirements:
 * 1. Student Management CRUD & search overloading
 * 2. Book Management CRUD & search overloading
 * 3. Issue & Return with atomic transactions & fine calculation
 * 4. Exception handling (BookNotAvailableException, RecordNotFoundException)
 * 5. Multithreading & Thread-safe Synchronization
 * 6. Collections (ArrayList, Stack)
 * 7. Character-oriented File I/O (BufferedReader, BufferedWriter, PrintWriter)
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   RUNNING AUTOMATED VERIFICATION SUITE FOR LIBRARY SYSTEM    ");
        System.out.println("===============================================================");

        int passed = 0;
        int failed = 0;

        AuditLogThread auditor = new AuditLogThread();
        auditor.start();
        LibraryService service = new LibraryService(auditor);

        try {
            // Test 1: Student Management
            System.out.print("[TEST 1] Register Student & Retrieve ... ");
            String testEmail = "test.student." + System.currentTimeMillis() + "@vitbhopal.ac.in";
            Student s = new Student(0, "Test Student", testEmail, "9988776655", "CSE", 2);
            boolean regOk = service.registerStudent(s);
            Student retrievedStudent = service.searchStudent(s.getId());
            if (regOk && retrievedStudent != null && retrievedStudent.getName().equals("Test Student")) {
                System.out.println("PASSED (ID=" + s.getId() + ")");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 2: Overloaded Student Search
            System.out.print("[TEST 2] Overloaded Student Search (by keyword) ... ");
            List<Student> searchResults = service.searchStudent("CSE");
            if (!searchResults.isEmpty()) {
                System.out.println("PASSED (Found " + searchResults.size() + " students)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 3: Update Student
            System.out.print("[TEST 3] Update Student Details ... ");
            retrievedStudent.setDepartment("Data Science");
            service.updateStudent(retrievedStudent);
            Student updatedStudent = service.searchStudent(s.getId());
            if (updatedStudent.getDepartment().equals("Data Science")) {
                System.out.println("PASSED");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 4: Book Management
            System.out.print("[TEST 4] Add Book & Retrieve ... ");
            String testIsbn = "978-" + (System.currentTimeMillis() % 1000000000L);
            Book b = new Book(0, "Effective Java", "Joshua Bloch", true, testIsbn, BookGenre.COMPUTER_SCIENCE, 3);
            boolean bookOk = service.addBook(b);
            Book retrievedBook = service.searchBook(b.getItemId());
            if (bookOk && retrievedBook != null && retrievedBook.getTitle().equals("Effective Java")) {
                System.out.println("PASSED (ID=" + b.getItemId() + ")");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 5: Overloaded Book Search (by Title keyword)
            System.out.print("[TEST 5] Overloaded Book Search (by Title keyword) ... ");
            List<Book> booksFound = service.searchBook("Effective");
            if (!booksFound.isEmpty() && booksFound.get(0).getAuthor().equals("Joshua Bloch")) {
                System.out.println("PASSED");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 6: Book Availability Check
            System.out.print("[TEST 6] Check Book Availability before issue ... ");
            if (service.isBookAvailable(b.getItemId())) {
                System.out.println("PASSED (Available=true)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 7: Issue Book Transaction
            System.out.print("[TEST 7] Issue Book to Student ... ");
            Transaction txn = service.issueBook(s.getId(), b.getItemId(), 14);
            Book afterIssueBook = service.searchBook(b.getItemId());
            if (txn != null && !afterIssueBook.isAvailable()) {
                System.out.println("PASSED (Txn #" + txn.getTransactionId() + ", Available=false)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 8: Exception Handling: Issue unavailable book
            System.out.print("[TEST 8] Catch BookNotAvailableException on double issue ... ");
            try {
                service.issueBook(s.getId(), b.getItemId(), 14);
                System.out.println("FAILED (Exception was not thrown)");
                failed++;
            } catch (BookNotAvailableException e) {
                System.out.println("PASSED (" + e.getMessage() + ")");
                passed++;
            }

            // Test 9: Return Book with Overdue Fine Calculation
            System.out.print("[TEST 9] Return Book with 4 Overdue Days (Fine Calc) ... ");
            LocalDate overdueDate = txn.getDueDate().plusDays(4);
            Transaction retTxn = service.returnBook(s.getId(), b.getItemId(), overdueDate);
            Book afterReturnBook = service.searchBook(b.getItemId());
            double expectedFine = service.calculateFine(4); // 4 * 5 = 20.0
            if (retTxn.getFineAmount() == expectedFine && afterReturnBook.isAvailable()) {
                System.out.println("PASSED (Fine=₹" + retTxn.getFineAmount() + ", Available=true)");
                passed++;
            } else {
                System.out.printf("FAILED (Expected ₹%.2f, Got ₹%.2f)%n", expectedFine, retTxn.getFineAmount());
                failed++;
            }

            // Test 10: Collections - Java Stack LIFO activity check
            System.out.print("[TEST 10] Verify java.util.Stack activity tracking ... ");
            List<String> recentActs = service.getRecentActivities(5);
            if (!recentActs.isEmpty() && recentActs.get(0).contains("Returned Book")) {
                System.out.println("PASSED (Top of Stack: " + recentActs.get(0) + ")");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 11: 2-D Array Genre Breakdown Matrix
            System.out.print("[TEST 11] Generate 2-D Array Statistics Matrix ... ");
            String[][] matrix = service.getGenreStatisticsMatrix();
            if (matrix != null && matrix.length > 0 && matrix[0].length == 3) {
                System.out.println("PASSED (" + matrix.length + " genres processed)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 12: Character-oriented File I/O (Export and Read Report)
            System.out.print("[TEST 12] Character-oriented File I/O (Write & Read Report) ... ");
            String testReportFile = "test_summary_report.txt";
            ReportGenerator.generateReport(service, testReportFile);
            String reportText = ReportGenerator.readReport(testReportFile);
            File f = new File(testReportFile);
            if (f.exists() && reportText.contains("SMART CAMPUS LIBRARY MANAGEMENT SYSTEM") && reportText.contains("Total Late Fines Assessed")) {
                System.out.println("PASSED (Report File Size: " + f.length() + " bytes)");
                f.delete(); // Cleanup test artifact
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 13: Multithreading & Synchronization
            System.out.print("[TEST 13] Multithreading Worker & Synchronized Audit Logger ... ");
            Thread.sleep(300); // Allow thread time to process queued log entries
            List<String> audits = auditor.getRecentAudits();
            if (!audits.isEmpty() && auditor.isAlive()) {
                System.out.println("PASSED (Processed " + audits.size() + " audit events asynchronously)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 14: Authentication - Seed & Login
            System.out.print("[TEST 14] Authentication: Seed & Valid Login (admin/admin123) ... ");
            service.seedDefaultUsers();
            com.library.model.User adminUser = service.login("admin", "admin123");
            if (adminUser != null && adminUser.getRole() == com.library.model.UserRole.ADMIN) {
                System.out.println("PASSED (Logged in: " + adminUser.getFullName() + ")");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 15: Authentication - Invalid Credentials Rejection
            System.out.print("[TEST 15] Authentication: Invalid Password Rejection ... ");
            boolean authRejected = false;
            try {
                service.login("admin", "wrongpassword");
            } catch (com.library.exception.AuthenticationException ae) {
                authRejected = true;
            }
            if (authRejected) {
                System.out.println("PASSED (AuthenticationException caught correctly)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 16: Authentication - User Registration
            System.out.print("[TEST 16] Authentication: User Registration & Lookup ... ");
            String uniqueUser = "lib_test_" + System.currentTimeMillis();
            com.library.model.User newStaff = new com.library.model.User(
                    uniqueUser, "securePass99", "Assistant Librarian", com.library.model.UserRole.LIBRARIAN
            );
            boolean regUserOk = service.registerUser(newStaff);
            com.library.model.User loggedStaff = service.login(uniqueUser, "securePass99");
            if (regUserOk && loggedStaff != null && loggedStaff.getRole() == com.library.model.UserRole.LIBRARIAN) {
                System.out.println("PASSED (Registered & Authenticated user ID=" + loggedStaff.getId() + ")");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 17: Authentication - User Logout & Audit Tracking
            System.out.print("[TEST 17] Authentication: User Logout & Audit Stack ... ");
            service.logout(loggedStaff);
            List<String> userActivities = service.getRecentActivities(3);
            boolean logoutAudited = false;
            for (String act : userActivities) {
                if (act.contains("User logged out: " + uniqueUser)) {
                    logoutAudited = true;
                    break;
                }
            }
            if (logoutAudited) {
                System.out.println("PASSED (Logout tracked in LIFO Stack and Auditor)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            failed++;
        } finally {
            auditor.stopAuditor();
            DatabaseManager.getInstance().closeConnection();
        }

        System.out.println("===============================================================");
        System.out.printf("TEST SUMMARY: Total: %d | Passed: %d | Failed: %d%n", (passed + failed), passed, failed);
        System.out.println("===============================================================");
        if (failed == 0) {
            System.out.println("ALL SYSTEM VERIFICATION TESTS PASSED SUCCESSFULLY!");
        }
    }
}
