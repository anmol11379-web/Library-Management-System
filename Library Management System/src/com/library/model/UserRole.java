package com.library.model;

/**
 * Enumeration representing user authorization levels.
 * Demonstrates:
 * - Java Unit 2: Enums and constants
 */
public enum UserRole {
    ADMIN("Administrator"),
    LIBRARIAN("Librarian"),
    STUDENT("Student");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromString(String roleStr) {
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(roleStr) || role.displayName.equalsIgnoreCase(roleStr)) {
                return role;
            }
        }
        return STUDENT; // default fallback
    }
}
