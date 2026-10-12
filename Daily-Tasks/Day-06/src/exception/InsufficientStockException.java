package exception;

/** Checked failure raised when an order exceeds available inventory. */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}
