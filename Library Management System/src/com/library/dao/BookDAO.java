package com.library.dao;

import com.library.model.Book;
import com.library.model.BookGenre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: BookDAO
 * Demonstrates:
 * - JDBC CRUD operations (Unit 5)
 * - PreparedStatements and parameterized queries (Unit 5)
 * - Method overloading in query retrieval (findById vs findByTitle) (Unit 2)
 * - Java Collections Framework (ArrayList, List) (Unit 4)
 */
public class BookDAO {

    public boolean insert(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, available, isbn, genre, edition) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.isAvailable() ? 1 : 0);
            ps.setString(4, book.getIsbn());
            ps.setString(5, book.getGenre().name());
            ps.setInt(6, book.getEdition());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setItemId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, available = ?, isbn = ?, genre = ?, edition = ? WHERE item_id = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.isAvailable() ? 1 : 0);
            ps.setString(4, book.getIsbn());
            ps.setString(5, book.getGenre().name());
            ps.setInt(6, book.getEdition());
            ps.setInt(7, book.getItemId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateAvailability(int bookId, boolean available) throws SQLException {
        String sql = "UPDATE books SET available = ? WHERE item_id = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, available ? 1 : 0);
            ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE item_id = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    // Overloaded search method 1: By ID (Unit 2 Polymorphism/Overloading)
    public Book findById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE item_id = ?";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        }
        return null;
    }

    // Overloaded search method 2: By Title or Keyword (Unit 2 Polymorphism/Overloading)
    public List<Book> findByTitle(String query) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? ORDER BY item_id ASC";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String wildcard = "%" + query.toLowerCase() + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBook(rs));
                }
            }
        }
        return list;
    }

    public List<Book> findAll() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY item_id ASC";
        Connection conn = DatabaseManager.getInstance().getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToBook(rs));
            }
        }
        return list;
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("item_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("available") == 1,
                rs.getString("isbn"),
                BookGenre.fromString(rs.getString("genre")),
                rs.getInt("edition")
        );
    }
}
