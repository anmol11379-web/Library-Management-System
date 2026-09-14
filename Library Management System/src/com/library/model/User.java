package com.library.model;

/**
 * Model representing an authenticated system user.
 * Demonstrates:
 * - Java Unit 2: Encapsulation, constructors, getters & setters, toString() override
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private UserRole role;

    // Default constructor
    public User() {
    }

    // Parameterized constructor for creation before persistence (no ID yet)
    public User(String username, String password, String fullName, UserRole role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Full constructor including database primary key ID
    public User(int id, String username, String password, String fullName, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("User [ID: %d | Username: %s | Name: %s | Role: %s]",
                id, username, fullName, role.getDisplayName());
    }
}
