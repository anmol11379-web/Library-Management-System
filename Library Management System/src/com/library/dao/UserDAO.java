package com.library.dao;

import com.library.model.User;
import com.library.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: UserDAO
 * Demonstrates:
 * - Database Applications with JDBC (Unit 5)
 * - PreparedStatements for safe credential queries (preventing SQL injection)
 * - ResultSets & processing database records
 * - Java Collections Framework (ArrayList, List interface) (Unit 4)
 * - Exception Handling (SQLException) (Unit 3)
 */
public class UserDAO {

    /**
     * Inserts a new user into the database.
     */
    public boolean insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername().trim().toLowerCase());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName().trim());
            ps.setString(4, user.getRole().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Authenticates a user with given username and password.
     * Returns User if credentials match, null otherwise.
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, full_name, role FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Checks if a username already exists.
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Finds a user by username.
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, full_name, role FROM users WHERE LOWER(username) = LOWER(?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all registered users.
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, role FROM users ORDER BY id ASC";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    /**
     * Seeds initial default accounts if no users exist.
     */
    public void seedDefaultUsers() throws SQLException {
        if (!usernameExists("admin")) {
            insert(new User("admin", "admin123", "Chief Administrator", UserRole.ADMIN));
        }
        if (!usernameExists("librarian")) {
            insert(new User("librarian", "lib123", "Campus Librarian", UserRole.LIBRARIAN));
        }
        if (!usernameExists("student")) {
            insert(new User("student", "student123", "Default Student Account", UserRole.STUDENT));
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String fullName = rs.getString("full_name");
        String roleStr = rs.getString("role");
        UserRole role = UserRole.fromString(roleStr);

        return new User(id, username, password, fullName, role);
    }
}
