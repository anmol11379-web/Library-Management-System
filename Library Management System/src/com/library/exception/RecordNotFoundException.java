package com.library.exception;

/**
 * Custom Exception: RecordNotFoundException
 * Thrown when an entity (Student, Book, or Transaction) cannot be located in the database.
 * Demonstrates:
 * - Specific checked exception throwing (Unit 3)
 */
public class RecordNotFoundException extends LibraryException {
    private final String entityType;
    private final Object identifier;

    public RecordNotFoundException(String entityType, Object identifier) {
        super(entityType + " with identifier '" + identifier + "' was not found.");
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public String getEntityType() {
        return entityType;
    }

    public Object getIdentifier() {
        return identifier;
    }
}
