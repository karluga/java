// Improved version with added:
//   Book class for encapsulation

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;

class Book {
    private HashMap<String, String> bookDetails = new HashMap<>();

    public Book(String author, String year, String title, double price) {
        bookDetails.put("Author", author);
        bookDetails.put("Year", year);
        bookDetails.put("Title", title);
        bookDetails.put("Price", String.valueOf(price));
    }

    public String getDetail(String key) {
        return bookDetails.getOrDefault(key, "N/A");
    }

    public void setDetail(String key, String value) {
        bookDetails.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("%-20s | %-4s | %-30s | $%-6s", 
            bookDetails.get("Author"), 
            bookDetails.get("Year"), 
            bookDetails.get("Title"), 
            bookDetails.get("Price")
        );
    }
}

class Library {
    private List<Book> scienceBooks = new ArrayList<>();
    private List<Book> technologyBooks = new ArrayList<>();
    private List<Book> novelBooks = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void addBook(String category) {
        System.out.println("\nEnter details for the " + category + " book:");

        System.out.print("Author Name: ");
        String author = scanner.nextLine().trim();

        System.out.print("Year of Publishing: ");
        String year;
        while (true) {
            year = scanner.nextLine().trim();
            if (year.matches("\\d{4}")) break;
            System.out.print("Invalid input. Please enter a 4-digit year: ");
        }

        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Price: ");
        double price;
        while (true) {
            try {
                price = Double.parseDouble(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a valid price: ");
            }
        }

        Book book = new Book(author, year, title, price);
        addBookToCategory(book, category);
        System.out.println("Book added successfully to " + category + " category.");
    }

    private void addBookToCategory(Book book, String category) {
        switch (category.toLowerCase()) {
            case "science":
                scienceBooks.add(book);
                break;
            case "technology":
                technologyBooks.add(book);
                break;
            case "novels":
                novelBooks.add(book);
                break;
        }
    }

    public void printInventory() {
        System.out.println("\nLibrary Inventory:");
        printCategory("Science Books", scienceBooks);
        printCategory("Technology Books", technologyBooks);
        printCategory("Novel Books", novelBooks);
    }

    private void printCategory(String category, List<Book> books) {
        System.out.println("\n" + category + ":");
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        System.out.printf("%-20s | %-4s | %-30s | %-6s %n", "Author Name", "Year", "Title", "Price");
        System.out.println("--------------------------------------------------------------");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void searchBook() {
        System.out.print("\nEnter book title to search: ");
        String title = scanner.nextLine().toLowerCase();
        boolean found = false;

        for (Book book : getAllBooks()) {
            if (book.getDetail("Title").toLowerCase().contains(title)) {
                System.out.println("\nBook Found:");
                System.out.println(book);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No book found with the given title.");
        }
    }

    private List<Book> getAllBooks() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.addAll(scienceBooks);
        allBooks.addAll(technologyBooks);
        allBooks.addAll(novelBooks);
        return allBooks;
    }
}

class Main_3 {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nLibrary Management System Menu:");
            System.out.println("1. Add Science Book");
            System.out.println("2. Add Technology Book");
            System.out.println("3. Add Novel Book");
            System.out.println("4. Print Library Inventory");
            System.out.println("5. Search Book by Title");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {
                case 1:
                    library.addBook("Science");
                    break;
                case 2:
                    library.addBook("Technology");
                    break;
                case 3:
                    library.addBook("Novels");
                    break;
                case 4:
                    library.printInventory();
                    break;
                case 5:
                    library.searchBook();
                    break;
                case 6:
                    System.out.println("Exiting the Library Management System. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
    }
}
