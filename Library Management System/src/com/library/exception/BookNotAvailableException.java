package com.library.exception;

/**
 * Custom Exception: BookNotAvailableException
 * Thrown when an operation attempts to issue a book that is currently checked out or inactive.
 * Demonstrates:
 * - Subclassing user-defined exceptions (Unit 3)
 */
public class BookNotAvailableException extends LibraryException {
    private final int bookId;

    public BookNotAvailableException(int bookId) {
        super("Book with ID " + bookId + " is currently NOT available for issue.");
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }
}
