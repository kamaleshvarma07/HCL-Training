/** Small AutoCloseable resource that prints its name when released. */
public final class TracedResource implements AutoCloseable {
    private final String name;

    public TracedResource(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Resource name is required.");
        this.name = name;
    }

    public String name() { return name; }

    @Override public void close() { System.out.println("Closed: " + name); }
}
