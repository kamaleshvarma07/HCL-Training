import exception.InsufficientStockException;
import exception.InvalidQuantityException;

public class Main {
    public static void main(String[] args) {
        OrderProcessor processor = new OrderProcessor("notebook", 5);
        demonstrate(processor, "success", () -> processor.processOrder("2"));
        demonstrate(processor, "invalid quantity", () -> processor.processOrder("many"));
        demonstrate(processor, "insufficient stock", () -> processor.processOrder(8));
    }

    private static void demonstrate(OrderProcessor processor, String scenario,
                                    OrderAction action) {
        System.out.println("\nScenario: " + scenario);
        try {
            action.run();
        } catch (InvalidQuantityException | InsufficientStockException failure) {
            System.out.println("Order failed: " + failure.getMessage());
            failure.printStackTrace(System.out);
        } finally {
            System.out.println("AUDIT: completed order attempt for " + scenario);
        }
    }

    @FunctionalInterface
    private interface OrderAction {
        void run() throws InsufficientStockException;
    }
}
