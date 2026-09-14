package com.library.model;

/**
 * Abstract Base Class: LibraryItem
 * Demonstrates:
 * - Abstract class and abstract methods (Unit 2)
 * - Encapsulation (Unit 2)
 * - Base for polymorphism (dynamic method dispatch)
 */
public abstract class LibraryItem {
    private int itemId;
    private String title;
    private String author;
    private boolean available;

    public LibraryItem() {
        this.itemId = 0;
        this.title = "Untitled";
        this.author = "Anonymous";
        this.available = true;
    }

    public LibraryItem(int itemId, String title, String author, boolean available) {
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        if (itemId > 0) {
            this.itemId = itemId;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            this.author = author.trim();
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Abstract method defining item presentation
    public abstract String getItemType();

    @Override
    public String toString() {
        return String.format("ID: %d | Title: %-25s | Author: %-18s | Available: %s",
                itemId, title, author, (available ? "YES" : "NO"));
    }
}
