// Basic version of the task

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Main_1 {
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
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

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
                    System.out.println("Exiting the Library Management System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }

    static void addBook(ArrayList<HashMap<String, String>> categoryList, String category) {
        HashMap<String, String> book = new HashMap<>();
        System.out.println("\nEnter details for the " + category + " book:");
        System.out.print("Author Name: ");
        book.put("Author", scanner.nextLine());
        System.out.print("Year of Publishing: ");
        book.put("Year", scanner.nextLine());
        System.out.print("Title: ");
        book.put("Title", scanner.nextLine());
        System.out.print("Price: ");
        book.put("Price", scanner.nextLine());
        
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
}
