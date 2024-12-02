// Book Class
// Single Responsibility Principle (SRP): Manages only book actions
class Book {
    private final int id;
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true; // Books are available by default
    }

    // Getters and setters for controlled access to book properties
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (InputValidator.isStringValid(title)) {
            this.title = title;
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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
        return "Book ID: " + id + ", Title: " + title + ", Author: " + author +
                ", Available: " + (isAvailable ? "Yes" : "No");
    }
}
