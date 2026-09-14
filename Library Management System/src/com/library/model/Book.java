package com.library.model;

/**
 * Derived Class: Book
 * Demonstrates:
 * - Inheritance (extends LibraryItem) (Unit 2)
 * - 'super' keyword calling base constructor and methods (Unit 2)
 * - Enum association (BookGenre) (Unit 2)
 * - Method overriding (@Override) (Unit 2)
 */
public class Book extends LibraryItem {
    private String isbn;
    private BookGenre genre;
    private int edition;

    public Book() {
        super();
        this.isbn = "000-0000000000";
        this.genre = BookGenre.GENERAL;
        this.edition = 1;
    }

    public Book(int itemId, String title, String author, boolean available, String isbn, BookGenre genre, int edition) {
        super(itemId, title, author, available);
        this.isbn = isbn;
        this.genre = genre != null ? genre : BookGenre.GENERAL;
        this.edition = edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn != null && !isbn.trim().isEmpty()) {
            this.isbn = isbn.trim();
        }
    }

    public BookGenre getGenre() {
        return genre;
    }

    public void setGenre(BookGenre genre) {
        this.genre = genre != null ? genre : BookGenre.GENERAL;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        if (edition >= 1) {
            this.edition = edition;
        }
    }

    @Override
    public String getItemType() {
        return "Book [" + genre.getDisplayName() + "]";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | ISBN: %-14s | Edition: %2d | Genre: %s",
                isbn, edition, genre.getCode());
    }
}
