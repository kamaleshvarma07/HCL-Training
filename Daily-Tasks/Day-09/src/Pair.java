/** An immutable pair with independently typed values. */
public final class Pair<K, V> {
    private final K first;
    private final V second;

    public Pair(K first, V second) {
        if (first == null || second == null) throw new IllegalArgumentException("Pair values are required.");
        this.first = first;
        this.second = second;
    }

    public K first() { return first; }
    public V second() { return second; }
}
