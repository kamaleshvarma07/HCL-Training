import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GenericToolkit {
    public static void main(String[] args) throws IOException {
        Pair<String, Integer> pair = new Pair<>("Notebook", 3);
        Box<Integer> box = new Box<>(12);
        System.out.println("Pair: " + pair.first() + " x " + pair.second());
        System.out.println("Box number: " + box.value() + "; doubled=" + box.doubleValue());
        System.out.println("Maximum: " + max(7, 12));

        List<Integer> source = List.of(2, 4, 6);
        List<Number> destination = new ArrayList<>();
        copyNumbers(source, destination);
        System.out.println("Copied numbers (PECS): " + destination);

        Path csv = args.length == 0 ? Path.of("products.csv") : Path.of(args[0]);
        System.out.println("Products:");
        try (BufferedReader reader = Files.newBufferedReader(csv)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] fields = line.split(",", -1);
                if (fields.length != 2 || fields[0].isBlank()) {
                    throw new IOException("Invalid product row: " + line);
                }
                try {
                    System.out.println("- " + fields[0].trim() + ": " +
                            new java.math.BigDecimal(fields[1].trim()));
                } catch (NumberFormatException invalidPrice) {
                    throw new IOException("Invalid product price in row: " + line, invalidPrice);
                }
            }
        }

        try (TracedResource outer = new TracedResource("outer");
             TracedResource inner = new TracedResource("inner")) {
            System.out.println("Resources acquired: " + outer.name() + ", " + inner.name());
        }
    }

    public static <T extends Comparable<T>> T max(T left, T right) {
        if (left == null || right == null) throw new IllegalArgumentException("Values are required.");
        return left.compareTo(right) >= 0 ? left : right;
    }

    /** Producer uses extends; destination consumer uses super. */
    public static void copyNumbers(List<? extends Number> source, List<? super Number> destination) {
        destination.addAll(source);
    }
}
