package com.library.model;

/**
 * Enum: BookGenre
 * Demonstrates:
 * - Java enum Class (Unit 2)
 * - Java enum Constructor (Unit 2)
 * - Custom fields and getter methods in enum
 */
public enum BookGenre {
    COMPUTER_SCIENCE("CS", "Computer Science & Engineering"),
    MATHEMATICS("MATH", "Mathematics & Statistics"),
    PHYSICS("PHY", "Physical Sciences"),
    FICTION("FIC", "Literature & Fiction"),
    GENERAL("GEN", "General Reference");

    private final String code;
    private final String displayName;

    // Enum constructor (Unit 2)
    BookGenre(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Static helper method to match genre by string or code
    public static BookGenre fromString(String text) {
        if (text == null) return GENERAL;
        for (BookGenre g : BookGenre.values()) {
            if (g.name().equalsIgnoreCase(text.trim()) ||
                g.code.equalsIgnoreCase(text.trim()) ||
                g.displayName.equalsIgnoreCase(text.trim())) {
                return g;
            }
        }
        return GENERAL;
    }

    @Override
    public String toString() {
        return displayName + " (" + code + ")";
    }
}
