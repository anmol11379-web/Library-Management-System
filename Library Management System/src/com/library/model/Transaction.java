package com.library.model;

import java.time.LocalDate;

/**
 * Model Class: Transaction
 * Represents a book borrowing record.
 * Demonstrates:
 * - Encapsulation (Unit 2)
 * - Enum integration (TransactionStatus)
 * - Date tracking and fine calculation state
 */
public class Transaction {
    private int transactionId;
    private int studentId;
    private int bookId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;
    private TransactionStatus status;

    public Transaction() {
        this.transactionId = 0;
        this.studentId = 0;
        this.bookId = 0;
        this.issueDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(14);
        this.returnDate = null;
        this.fineAmount = 0.0;
        this.status = TransactionStatus.ISSUED;
    }

    public Transaction(int transactionId, int studentId, int bookId, LocalDate issueDate,
                       LocalDate dueDate, LocalDate returnDate, double fineAmount, TransactionStatus status) {
        this.transactionId = transactionId;
        this.studentId = studentId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    // Overloaded convenience constructor for new issues
    public Transaction(int studentId, int bookId, int loanDays) {
        this();
        this.studentId = studentId;
        this.bookId = bookId;
        this.issueDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(loanDays > 0 ? loanDays : 14);
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Txn #%d | Student ID: %-4d | Book ID: %-4d | Issued: %s | Due: %s | Returned: %-10s | Fine: ₹%.2f | Status: %s",
                transactionId, studentId, bookId, issueDate, dueDate,
                (returnDate != null ? returnDate.toString() : "Not Yet"), fineAmount, status);
    }
}
