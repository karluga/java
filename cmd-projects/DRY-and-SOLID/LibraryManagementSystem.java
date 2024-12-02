import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; // Map, HashMap, Scanner

// Main Class
// Uses Dependency Inversion Principle (DIP)
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
            scanner.nextLine();

            // Every option delegates to LibraryService
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter book author: ");
                    String author = scanner.nextLine();
                    libraryService.addBook(title, author);
                }
                case 2 -> {
                    System.out.print("Enter book ID to update: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter new author: ");
                    String author = scanner.nextLine();
                    libraryService.updateBook(id, title, author);
                }
                case 3 -> {
                    System.out.print("Enter book ID to delete: ");
                    int id = scanner.nextInt();
                    libraryService.deleteBook(id);
                }
                case 4 -> {
                    System.out.print("Enter book ID to borrow: ");
                    int id = scanner.nextInt();
                    libraryService.borrowBook(id);
                }
                case 5 -> {
                    System.out.print("Enter book ID to return: ");
                    int id = scanner.nextInt();
                    libraryService.returnBook(id);
                }
                case 6 -> {
                    System.out.print("Enter keyword to search: ");
                    String keyword = scanner.nextLine();
                    libraryService.searchBooks(keyword);
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
