import java.util.ArrayList;
import java.util.Scanner;

class Item {
    String name;
    double price;
    int quantity;
    double discount;

    Item(String name, double price, int quantity, double discount) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }

    double getTotalPrice() {
        return (price * quantity) * (1 - (discount / 100));
    }

    public String toString() {
        return String.format("Item: %s, Price: %.2f, Quantity: %d, Discount: %.2f%%", name, price, quantity, discount);
    }
}

public class ShoppingCartSystem {
    private ArrayList<Item> cart = new ArrayList<>();

    public void addItem(String name, double price) {
        addItem(name, price, 1, 0.0);
    }

    public void addItem(String name, double price, int quantity) {
        addItem(name, price, quantity, 0.0);
    }

    public void addItem(String name, double price, int quantity, double discount) {
        cart.add(new Item(name, price, quantity, discount));
        System.out.println("Item added: " + name + ", Quantity: " + quantity + ", Discount: " + discount + "%");
    }

    public void viewCart() {
        for (Item item : cart) {
            System.out.println(item);
        }
    }

    public double calculateTotal() {
        double total = 0;
        for (Item item : cart) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public static void main(String[] args) {
        ShoppingCartSystem system = new ShoppingCartSystem();
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("Shopping Cart System:");
            System.out.println("1. Add Item (Name, Price)");
            System.out.println("2. Add Item (Name, Price, Quantity)");
            System.out.println("3. Add Item (Name, Price, Quantity, Discount)");
            System.out.println("4. View Cart");
            System.out.println("5. Calculate Total");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter item name: ");
                    String name1 = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price1 = scanner.nextDouble();
                    system.addItem(name1, price1);
                    break;

                case 2:
                    System.out.print("Enter item name: ");
                    String name2 = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price2 = scanner.nextDouble();
                    System.out.print("Enter quantity: ");
                    int quantity2 = scanner.nextInt();
                    system.addItem(name2, price2, quantity2);
                    break;

                case 3:
                    System.out.print("Enter item name: ");
                    String name3 = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price3 = scanner.nextDouble();
                    System.out.print("Enter quantity: ");
                    int quantity3 = scanner.nextInt();
                    System.out.print("Enter discount (%): ");
                    double discount3 = scanner.nextDouble();
                    system.addItem(name3, price3, quantity3, discount3);
                    break;

                case 4:
                    system.viewCart();
                    break;

                case 5:
                    System.out.printf("Total: $%.2f\n", system.calculateTotal());
                    break;

                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
}
