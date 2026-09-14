package com.library.service;

import com.library.exception.RecordNotFoundException;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface: Manageable
 * Demonstrates:
 * - Java Interfaces (Unit 2)
 * - Polymorphism through interface contracts
 * - Generic typing for clean abstraction
 */
public interface Manageable<T> {
    void displayAll() throws SQLException;
    T searchById(int id) throws SQLException, RecordNotFoundException;
    List<T> getAll() throws SQLException;
}
