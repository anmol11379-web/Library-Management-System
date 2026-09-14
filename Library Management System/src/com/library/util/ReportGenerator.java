package com.library.util;

import com.library.model.Book;
import com.library.model.Student;
import com.library.model.Transaction;
import com.library.service.LibraryService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReportGenerator Utility
 * Demonstrates:
 * - Character-oriented streams (Unit 4)
 * - Java Reader/Writer (Unit 4)
 * - BufferedReader and BufferedWriter (Unit 4)
 * - PrintWriter for formatted output (Unit 4)
 * - Try-with-resources exception handling (Unit 3)
 */
public class ReportGenerator {
    private static final String DEFAULT_REPORT_FILE = "library_summary_report.txt";

    /**
     * Generates a comprehensive text report using BufferedWriter and PrintWriter.
     */
    public static void generateReport(LibraryService service, String filePath) throws IOException, SQLException {
        File file = new File(filePath != null ? filePath : DEFAULT_REPORT_FILE);

        List<Student> students = service.getAllStudents();
        List<Book> books = service.getAll();
        List<Transaction> transactions = service.getAllTransactions();
        String[][] statsMatrix = service.getGenreStatisticsMatrix();

        int totalBooks = books.size();
        long availableBooks = books.stream().filter(Book::isAvailable).count();
        long issuedBooks = totalBooks - availableBooks;
        double totalFines = transactions.stream().mapToDouble(Transaction::getFineAmount).sum();

        // Character-oriented streams: FileWriter wrapped in BufferedWriter and PrintWriter (Unit 4)
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {

            pw.println("=========================================================================");
            pw.println("                  SMART CAMPUS LIBRARY MANAGEMENT SYSTEM                 ");
            pw.println("                           EXECUTIVE SUMMARY REPORT                      ");
            pw.println("=========================================================================");
            pw.println("Generated On: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pw.println();

            pw.println("-------------------------------------------------------------------------");
            pw.println("1. OVERALL INVENTORY & CIRCULATION");
            pw.println("-------------------------------------------------------------------------");
            pw.printf("Total Registered Students : %d%n", students.size());
            pw.printf("Total Books in Catalog    : %d%n", totalBooks);
            pw.printf("Currently Available Books : %d%n", availableBooks);
            pw.printf("Currently Issued Books    : %d%n", issuedBooks);
            pw.printf("Total Transactions Logged : %d%n", transactions.size());
            pw.printf("Total Late Fines Assessed : ₹%.2f%n", totalFines);
            pw.println();

            pw.println("-------------------------------------------------------------------------");
            pw.println("2. GENRE-WISE INVENTORY MATRIX (2-D ARRAY BREAKDOWN)");
            pw.println("-------------------------------------------------------------------------");
            pw.printf("%-35s | %-12s | %s%n", "Genre Category", "Total Count", "Available");
            pw.println("-------------------------------------------------------------------------");
            for (String[] row : statsMatrix) {
                pw.printf("%-35s | %-12s | %s%n", row[0], row[1], row[2]);
            }
            pw.println();

            pw.println("-------------------------------------------------------------------------");
            pw.println("3. ACTIVE & RECENT BORROWING RECORDS");
            pw.println("-------------------------------------------------------------------------");
            if (transactions.isEmpty()) {
                pw.println("No circulation records found.");
            } else {
                for (Transaction t : transactions) {
                    pw.printf("Txn #%d | Student #%d | Book #%d | Issued: %s | Due: %s | Return: %s | Fine: ₹%.2f | Status: %s%n",
                            t.getTransactionId(), t.getStudentId(), t.getBookId(),
                            t.getIssueDate(), t.getDueDate(),
                            (t.getReturnDate() != null ? t.getReturnDate() : "N/A"),
                            t.getFineAmount(), t.getStatus());
                }
            }
            pw.println();
            pw.println("=========================================================================");
            pw.println("                            END OF REPORT                                ");
            pw.println("=========================================================================");
        }
    }

    /**
     * Reads a report file character-by-character / line-by-line using BufferedReader.
     * Demonstrates Unit 4 Character-oriented stream reading.
     */
    public static String readReport(String filePath) throws IOException {
        File file = new File(filePath != null ? filePath : DEFAULT_REPORT_FILE);
        if (!file.exists()) {
            return "Report file does not exist yet. Please generate the report first.";
        }

        StringBuilder sb = new StringBuilder();
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
