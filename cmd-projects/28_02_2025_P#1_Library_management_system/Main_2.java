// Improved version with added:
//   Error handling
//   Search option to find a book by title

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;

class Main_2 {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<HashMap<String, String>> scienceBooks = new ArrayList<>();
    static ArrayList<HashMap<String, String>> technologyBooks = new ArrayList<>();
    static ArrayList<HashMap<String, String>> novelBooks = new ArrayList<>();

    public static void main(String[] args) {
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
                    addBook(scienceBooks, "Science");
                    break;
                case 2:
                    addBook(technologyBooks, "Technology");
                    break;
                case 3:
                    addBook(novelBooks, "Novels");
                    break;
                case 4:
                    printInventory();
                    break;
                case 5:
                    searchBook();
                    break;
                case 6:
                    System.out.println("Exiting the Library Management System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
    }

    static void addBook(ArrayList<HashMap<String, String>> categoryList, String category) {
        HashMap<String, String> book = new HashMap<>();
        System.out.println("\nEnter details for the " + category + " book:");
        System.out.print("Author Name: ");
        book.put("Author", scanner.nextLine());

        System.out.print("Year of Publishing: ");
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.matches("\\d{4}")) {
                    book.put("Year", input);
                    break;
                } else {
                    System.out.print("Invalid input. Please enter a 4-digit year: ");
                }
            } catch (Exception e) {
                System.out.print("Invalid input. Please enter a 4-digit year: ");
            }
        }

        System.out.print("Title: ");
        book.put("Title", scanner.nextLine());

        System.out.print("Price: ");
        while (true) {
            try {
                book.put("Price", String.valueOf(Double.parseDouble(scanner.nextLine().trim())));
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid price: ");
            }
        }

        categoryList.add(book);
        System.out.println("Book added successfully to the " + category + " category.");
    }

    static void printInventory() {
        System.out.println("\nLibrary Inventory:");
        printCategory("Science Books", scienceBooks);
        printCategory("Technology Books", technologyBooks);
        printCategory("Novel Books", novelBooks);
    }

    static void printCategory(String category, ArrayList<HashMap<String, String>> books) {
        System.out.println("\n" + category + ":");
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        System.out.printf("%-20s | %-15s | %-30s | %-10s %n", "Author Name", "Year", "Title", "Price");
        System.out.println("-------------------------------------------------------------------------------");
        for (HashMap<String, String> book : books) {
            System.out.printf("%-20s | %-15s | %-30s | %-10s %n", book.get("Author"), book.get("Year"), book.get("Title"), book.get("Price"));
        }
    }

    static void searchBook() {
        System.out.print("\nEnter book title to search: ");
        String title = scanner.nextLine().toLowerCase();
        boolean found = false;
        
        for (HashMap<String, String> book : getAllBooks()) {
            if (book.get("Title").toLowerCase().contains(title)) {
                System.out.println("\nBook Found:");
                System.out.println("Author: " + book.get("Author"));
                System.out.println("Year: " + book.get("Year"));
                System.out.println("Title: " + book.get("Title"));
                System.out.println("Price: " + book.get("Price"));
                found = true;
            }
        }

        if (!found) {
            System.out.println("No book found with the given title.");
        }
    }

    static List<HashMap<String, String>> getAllBooks() {
        List<HashMap<String, String>> allBooks = new ArrayList<>();
        allBooks.addAll(scienceBooks);
        allBooks.addAll(technologyBooks);
        allBooks.addAll(novelBooks);
        return allBooks;
    }
}
