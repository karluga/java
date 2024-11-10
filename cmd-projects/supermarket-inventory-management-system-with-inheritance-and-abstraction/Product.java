import java.time.LocalDate;

abstract class Product {
    protected String productId;
    protected String name;
    protected double price;
    protected int quantity;

    public Product(String productId, String name, double price, int quantity) {
        this.productId = productId.trim();
        this.name = name.trim();
        this.price = price;
        this.quantity = quantity;
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
    private LocalDate warrantyStart;
    private LocalDate warrantyEnd;

    public NonPerishableProduct(String productId, String name, double price, int quantity, LocalDate warrantyStart, LocalDate warrantyEnd) {
        super(productId, name, price, quantity);
        this.warrantyStart = warrantyStart;
        this.warrantyEnd = warrantyEnd;
    }

    @Override
    public String getProductInfo() {
        String info = "Non-Perishable Product [ID: " + productId + ", Name: " + name + ", Price: " + String.format("%.2f", price).replace(".", ",") + 
                      ", Quantity: " + quantity;
        
        if (warrantyStart != null && warrantyEnd != null) {
            info += ", Warranty Period: " + warrantyStart + " to " + warrantyEnd;
        }
        
        info += "]";
        return info;
    }
}
