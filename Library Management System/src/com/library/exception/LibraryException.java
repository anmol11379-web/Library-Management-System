package com.library.exception;

/**
 * Base Custom Exception: LibraryException
 * Demonstrates:
 * - User-defined Exceptions (Unit 3)
 * - 'throw' and 'throws' keywords
 */
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
