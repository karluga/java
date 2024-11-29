import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Main Class
// Adheres to Dependency Inversion Principle: Depends on abstraction (LibraryService), not implementation.
public class LibraryManagementSystem {
    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Borrow Book");
            System.out.println("5. Return Book");
            System.out.println("6. Search Books");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    libraryService.addBook(title, author); // Delegates to LibraryService.
                }
                case 2 -> {
                    System.out.print("Enter book ID to update: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter new author: ");
                    String author = scanner.nextLine();
                    libraryService.updateBook(id, title, author); // Delegates to LibraryService.
                }
                case 3 -> {
                    System.out.print("Enter book ID to delete: ");
                    int id = scanner.nextInt();
                    libraryService.deleteBook(id); // Delegates to LibraryService.
                }
                case 4 -> {
                    System.out.print("Enter book ID to borrow: ");
                    int id = scanner.nextInt();
                    libraryService.borrowBook(id); // Delegates to LibraryService.
                }
                case 5 -> {
                    System.out.print("Enter book ID to return: ");
                    int id = scanner.nextInt();
                    libraryService.returnBook(id); // Delegates to LibraryService.
                }
                case 6 -> {
                    System.out.print("Enter keyword to search: ");
                    String keyword = scanner.nextLine();
                    libraryService.searchBooks(keyword); // Delegates to LibraryService.
                }
                case 7 -> {
                    System.out.println("Exiting system. Goodbye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again!");
            }
        }
    }
}
