// Book Class
// Single Responsibility Principle (SRP): Manages book-specific data and behavior only.
class Book {
    private final int id;
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true; // Books are available by default.
    }

    // Getters and setters allow controlled access to book properties.
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        // DRY: Reuses validation logic from InputValidator.
        if (InputValidator.isStringValid(title)) {
            this.title = title;
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        // DRY: Reuses validation logic from InputValidator.
        if (InputValidator.isStringValid(author)) {
            this.author = author;
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        // Provides a readable string representation of the book.
        return "Book ID: " + id + ", Title: " + title + ", Author: " + author +
                ", Available: " + (isAvailable ? "Yes" : "No");
    }
}
