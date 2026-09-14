package com.library.model;

/**
 * Abstract Base Class: Person
 * Demonstrates:
 * - Abstract classes and methods (Unit 2)
 * - Encapsulation (private fields, public getters/setters)
 * - Constructors and 'this' keyword
 * - Inheritance base for Student
 */
public abstract class Person {
    private int id;
    private String name;
    private String email;
    private String phone;

    // Default constructor
    public Person() {
        this.id = 0;
        this.name = "Unknown";
        this.email = "N/A";
        this.phone = "N/A";
    }

    // Parameterized constructor
    public Person(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id > 0) {
            this.id = id;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Abstract method to be overridden by derived classes (Unit 2)
    public abstract String getRoleDetails();

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %-18s | Email: %-22s | Phone: %s", id, name, email, phone);
    }
}
