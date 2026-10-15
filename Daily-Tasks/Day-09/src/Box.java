/** A numeric box demonstrating a bounded type parameter. */
public final class Box<T extends Number> {
    private final T value;

    public Box(T value) {
        if (value == null) throw new IllegalArgumentException("Box value is required.");
        this.value = value;
    }

    public T value() { return value; }
    public double doubleValue() { return value.doubleValue() * 2; }
}
