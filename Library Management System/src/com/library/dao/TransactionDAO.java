package com.library.dao;

import com.library.model.Transaction;
import com.library.model.TransactionStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: TransactionDAO
 * Demonstrates:
 * - Complex JDBC operations (Unit 5)
 * - PreparedStatements & ResultSets
 * - Java Collections Framework (ArrayList, List) (Unit 4)
 * - Handling dates and status enums with relational mapping
 */
public class TransactionDAO {

    public boolean insert(Transaction txn) throws SQLException {
        String sql = "INSERT INTO transactions (student_id, book_id, issue_date, due_date, return_date, fine_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, txn.getStudentId());
            ps.setInt(2, txn.getBookId());
            ps.setString(3, txn.getIssueDate().toString());
            ps.setString(4, txn.getDueDate().toString());
            ps.setString(5, txn.getReturnDate() != null ? txn.getReturnDate().toString() : null);
            ps.setDouble(6, txn.getFineAmount());
            ps.setString(7, txn.getStatus().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        txn.setTransactionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Transaction txn) throws SQLException {
        String sql = "UPDATE transactions SET return_date = ?, fine_amount = ?, status = ? WHERE transaction_id = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txn.getReturnDate() != null ? txn.getReturnDate().toString() : null);
            ps.setDouble(2, txn.getFineAmount());
            ps.setString(3, txn.getStatus().name());
            ps.setInt(4, txn.getTransactionId());

            return ps.executeUpdate() > 0;
        }
    }

    public Transaction findById(int id) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaction(rs);
                }
            }
        }
        return null;
    }

    public Transaction findActiveIssue(int bookId, int studentId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE book_id = ? AND student_id = ? AND status = 'ISSUED' ORDER BY transaction_id DESC LIMIT 1";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaction(rs);
                }
            }
        }
        return null;
    }

    public List<Transaction> findByStudentId(int studentId) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE student_id = ? ORDER BY transaction_id DESC";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToTransaction(rs));
                }
            }
        }
        return list;
    }

    public List<Transaction> findAll() throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_id DESC";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
        }
        return list;
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        String retDateStr = rs.getString("return_date");
        LocalDate returnDate = (retDateStr != null && !retDateStr.isEmpty()) ? LocalDate.parse(retDateStr) : null;

        return new Transaction(
                rs.getInt("transaction_id"),
                rs.getInt("student_id"),
                rs.getInt("book_id"),
                LocalDate.parse(rs.getString("issue_date")),
                LocalDate.parse(rs.getString("due_date")),
                returnDate,
                rs.getDouble("fine_amount"),
                TransactionStatus.valueOf(rs.getString("status"))
        );
    }
}
