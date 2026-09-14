package com.library.exception;

/**
 * Custom Exception: AuthenticationException
 * Thrown when user authentication fails due to invalid credentials,
 * duplicate registrations, or unauthorized access attempts.
 * Demonstrates:
 * - Subclassing user-defined exceptions (Unit 3: Exception Handling)
 */
public class AuthenticationException extends LibraryException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
