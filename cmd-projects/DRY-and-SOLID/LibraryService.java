// LibraryService Class
// Uses Single Responsibility Principle (SRP): Focuses on managing library operations.
class LibraryService {
    private final Map<Integer, Book> books = new HashMap<>();
    private int bookCounter = 1; // Keeps track of unique book IDs.

    // Adds a new book to the library.
    public void addBook(String title, String author) {
        if (InputValidator.isStringValid(title) && InputValidator.isStringValid(author)) {
            Book book = new Book(bookCounter++, title, author);
            books.put(book.getId(), book);
            System.out.println("Book added successfully: " + book);
        } else {
            System.out.println("Invalid title or author!");
        }
    }

    // Updates an existing book's details.
    public void updateBook(int id, String title, String author) {
        Book book = books.get(id);
        if (book != null) {
            book.setTitle(title);
            book.setAuthor(author);
            System.out.println("Book updated successfully: " + book);
        } else {
            System.out.println("Book not found!");
        }
    }

    // Deletes a book from the library.
    public void deleteBook(int id) {
        if (books.remove(id) != null) {
            System.out.println("Book deleted successfully.");
        } else {
            System.out.println("Book not found!");
        }
    }

    // Borrows a book if available.
    public void borrowBook(int id) {
        Book book = books.get(id);
        if (book != null && book.isAvailable()) {
            book.setAvailable(false); // Updates availability status.
            System.out.println("You borrowed: " + book);
        } else {
            System.out.println("Book not available!");
        }
    }

    // Returns a borrowed book.
    public void returnBook(int id) {
        Book book = books.get(id);
        if (book != null && !book.isAvailable()) {
            book.setAvailable(true); // Updates availability status.
            System.out.println("You returned: " + book);
        } else {
            System.out.println("Book not borrowed or not found!");
        }
    }

    // Searches for books by a keyword in the title or author.
    public void searchBooks(String keyword) {
        boolean found = false;
        // DRY: Common search logic ensures consistency across all searches.
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found matching the keyword!");
        }
    }
}
