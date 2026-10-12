import exception.InsufficientStockException;
import exception.InvalidQuantityException;

/** Simulates order validation and stock deduction; it does not place real orders. */
public class OrderProcessor {
    private final String productName;
    private int availableStock;

    public OrderProcessor(String productName, int availableStock) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (availableStock < 0) {
            throw new IllegalArgumentException("Available stock cannot be negative.");
        }
        this.productName = productName;
        this.availableStock = availableStock;
    }

    public void processOrder(int quantity) throws InsufficientStockException {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero.");
        }
        if (quantity > availableStock) {
            throw new InsufficientStockException(
                    "Insufficient stock for " + productName + ": requested " + quantity
                            + ", available " + availableStock + ".");
        }
        availableStock -= quantity;
        System.out.println("Processed simulated order for " + quantity + " " + productName
                + "(s); remaining stock: " + availableStock);
    }

    public void processOrder(String quantityText)
            throws InsufficientStockException {
        final int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException cause) {
            throw new InvalidQuantityException("Quantity must be a whole number.", cause);
        }
        processOrder(quantity);
    }
}
