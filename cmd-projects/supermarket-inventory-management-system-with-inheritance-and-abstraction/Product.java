
abstract class Product {
    protected String productId;
    protected String name;
    protected double price;
    protected int quantity;

    public Product(String productId, String name, double price, int quantity) {
        this.productId = productId.trim();
        this.name = name.trim();
        this.price = price;
        this.quantity = Math.max(quantity, 0); // Ensures quantity is non-negative
    }

    public abstract String getProductInfo();
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }
    public void restock(int amount) {
        if (amount > 0) {
            quantity += amount;
            System.out.println("Restocked " + amount + " units. Total quantity: " + quantity);
        } else {
            System.out.println("Invalid restock amount.");
        }
    }

    public String getProductId() {
        return productId;
    }
}

class PerishableProduct extends Product {
    private String expiryDate;

    public PerishableProduct(String productId, String name, double price, int quantity, String expiryDate) {
        super(productId, name, price, quantity);
        this.expiryDate = expiryDate;
    }

    @Override
    public String getProductInfo() {
        return "Perishable Product [ID: " + productId + ", Name: " + name + ", Price: " + String.format("%.2f", price).replace(".", ",") + 
               ", Quantity: " + quantity + ", Expiry Date: " + expiryDate + "]";
    }
}

class NonPerishableProduct extends Product {
    private String warrantyPeriod;

    public NonPerishableProduct(String productId, String name, double price, int quantity, String warrantyPeriod) {
        super(productId, name, price, quantity);
        this.warrantyPeriod = warrantyPeriod;
    }

    @Override
    public String getProductInfo() {
        return "Non-Perishable Product [ID: " + productId + ", Name: " + name + ", Price: " + String.format("%.2f", price).replace(".", ",") + 
               ", Quantity: " + quantity + ", Warranty Period: " + warrantyPeriod + "]";
    }
}
