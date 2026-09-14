package com.library.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Singleton Database Manager
 * Demonstrates:
 * - Singleton Design Pattern (Unit 2)
 * - Static Nested Class for DB Configuration (Unit 2)
 * - Specifying JDBC driver information externally via db.properties (Unit 5)
 * - Database Applications with JDBC (Connection, Statement) (Unit 5)
 * - Java I/O Byte Streams (FileInputStream) (Unit 4)
 * - Exception Handling (try-catch, throws) (Unit 3)
 */
public class DatabaseManager {
    // Singleton instance
    private static DatabaseManager instance;
    private Connection connection;
    private final DbConfig config;

    /**
     * Static Nested Class (Unit 2)
     * Encapsulates external database configuration parameters.
     */
    public static class DbConfig {
        private String driver;
        private String url;
        private String username;
        private String password;

        public DbConfig(String driver, String url, String username, String password) {
            this.driver = driver;
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public String getDriver() { return driver; }
        public String getUrl() { return url; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }

    // Private constructor enforcing Singleton pattern
    private DatabaseManager() {
        this.config = loadExternalConfig();
        initConnection();
        createTablesIfNotExist();
    }

    // Thread-safe Singleton accessor (Unit 2 & Unit 3 Synchronization)
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Loads JDBC driver configuration from external db.properties file.
     * Demonstrates Unit 4 I/O Stream & Unit 5 External JDBC configuration.
     */
    private DbConfig loadExternalConfig() {
        Properties props = new Properties();
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (InputStream input = new FileInputStream(propFile)) {
                props.load(input);
            } catch (IOException e) {
                System.err.println("Warning: Failed to read db.properties, falling back to default SQLite config: " + e.getMessage());
            }
        }

        String driver = props.getProperty("db.driver", "org.sqlite.JDBC");
        String url = props.getProperty("db.url", "jdbc:sqlite:library.db");
        String user = props.getProperty("db.username", "");
        String pass = props.getProperty("db.password", "");

        return new DbConfig(driver, url, user, pass);
    }

    private void initConnection() {
        try {
            // Dynamically load JDBC Driver class (Unit 5)
            Class.forName(config.getDriver());
            // Establish JDBC Connection
            this.connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
            // Enable foreign keys in SQLite
            try (Statement stmt = this.connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver class not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            this.connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        }
        return connection;
    }

    /**
     * Creates database tables (students, books, transactions) if they do not exist.
     * Demonstrates DDL execution using JDBC Statement (Unit 5).
     */
    private void createTablesIfNotExist() {
        try (Statement stmt = getConnection().createStatement()) {
            // 1. Students Table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS students (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name TEXT NOT NULL," +
                "  email TEXT NOT NULL UNIQUE," +
                "  phone TEXT NOT NULL," +
                "  department TEXT NOT NULL," +
                "  year_of_study INTEGER NOT NULL" +
                ");"
            );

            // 2. Books Table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS books (" +
                "  item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  title TEXT NOT NULL," +
                "  author TEXT NOT NULL," +
                "  available INTEGER NOT NULL DEFAULT 1," +
                "  isbn TEXT NOT NULL UNIQUE," +
                "  genre TEXT NOT NULL," +
                "  edition INTEGER NOT NULL DEFAULT 1" +
                ");"
            );

            // 3. Transactions Table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "  transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  student_id INTEGER NOT NULL," +
                "  book_id INTEGER NOT NULL," +
                "  issue_date TEXT NOT NULL," +
                "  due_date TEXT NOT NULL," +
                "  return_date TEXT," +
                "  fine_amount REAL DEFAULT 0.0," +
                "  status TEXT NOT NULL," +
                "  FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE," +
                "  FOREIGN KEY(book_id) REFERENCES books(item_id) ON DELETE CASCADE" +
                ");"
            );

            // 4. Users Table (Authentication)
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username TEXT NOT NULL UNIQUE," +
                "  password TEXT NOT NULL," +
                "  full_name TEXT NOT NULL," +
                "  role TEXT NOT NULL" +
                ");"
            );
        } catch (SQLException e) {
            System.err.println("Error initializing database schema: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
