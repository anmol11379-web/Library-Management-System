package com.library.model;

/**
 * Derived Class: Student
 * Demonstrates:
 * - Inheritance (extends Person) (Unit 2)
 * - 'super' keyword calling base class constructor and methods (Unit 2)
 * - Method overriding (@Override getRoleDetails, toString) (Unit 2)
 * - Encapsulation (Unit 2)
 */
public class Student extends Person {
    private String department;
    private int yearOfStudy;

    // Default constructor chaining
    public Student() {
        super();
        this.department = "General";
        this.yearOfStudy = 1;
    }

    // Parameterized constructor invoking super()
    public Student(int id, String name, String email, String phone, String department, int yearOfStudy) {
        super(id, name, email, phone);
        this.department = department;
        this.yearOfStudy = yearOfStudy;
    }

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department != null && !department.trim().isEmpty()) {
            this.department = department.trim();
        }
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        if (yearOfStudy >= 1 && yearOfStudy <= 5) {
            this.yearOfStudy = yearOfStudy;
        }
    }

    // Method overriding of abstract method from Person (Unit 2)
    @Override
    public String getRoleDetails() {
        return "Student [Dept: " + department + ", Year: " + yearOfStudy + "]";
    }

    // Overriding toString() demonstrating polymorphism and super.toString()
    @Override
    public String toString() {
        return super.toString() + String.format(" | Dept: %-12s | Year: %d", department, yearOfStudy);
    }
}
