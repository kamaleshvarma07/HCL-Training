import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class LibraryWaitlist {
    private record HoldRequest(String reader, boolean faculty, int sequence) { }

    public static void main(String[] args) {
        // ArrayList keeps the catalogue in display order and offers fast indexed access.
        List<String> catalogue = new ArrayList<>(List.of(
                "Clean Code", "Effective Java", "The Hobbit", "Java Concurrency in Practice"));
        System.out.println("Catalogue (for-each):");
        for (String title : catalogue) {
            System.out.println("  " + title);
        }

        // ListIterator can safely add a catalogue entry while traversing the list.
        ListIterator<String> catalogueIterator = catalogue.listIterator();
        while (catalogueIterator.hasNext()) {
            if (catalogueIterator.next().equals("Effective Java")) {
                catalogueIterator.add("Effective Java (3rd edition)");
            }
        }
        System.out.println("ListIterator added a catalogue edition; entries=" + catalogue.size());

        // LinkedHashSet removes duplicate sign-ups but retains their arrival order.
        Set<String> registrations = new LinkedHashSet<>();
        registrations.add("reader-17");
        registrations.add("reader-24");
        registrations.add("reader-17");
        System.out.println("LinkedHashSet registrations: " + registrations);

        // ArrayDeque is a light FIFO Queue implementation for ordinary waitlist turns.
        Queue<String> waitlist = new ArrayDeque<>();
        waitlist.offer("reader-17");
        waitlist.offer("reader-24");
        System.out.println("ArrayDeque FIFO next: " + waitlist.poll());

        // PriorityQueue ranks faculty requests first, then preserves request sequence.
        Queue<HoldRequest> priorityRequests = new PriorityQueue<>((left, right) -> {
            int facultyOrder = Boolean.compare(right.faculty(), left.faculty());
            return facultyOrder != 0 ? facultyOrder
                    : Integer.compare(left.sequence(), right.sequence());
        });
        priorityRequests.offer(new HoldRequest("student-1", false, 1));
        priorityRequests.offer(new HoldRequest("faculty-1", true, 2));
        priorityRequests.offer(new HoldRequest("student-2", false, 3));
        System.out.println("PriorityQueue faculty-first order:");
        while (!priorityRequests.isEmpty()) {
            HoldRequest request = priorityRequests.poll();
            System.out.println("  " + request.reader() + " (faculty=" + request.faculty() + ")");
        }

        // HashSet is suitable for membership checks when order is irrelevant.
        Set<String> fastMembership = new HashSet<>(catalogue);
        System.out.println("HashSet contains 'The Hobbit': " + fastMembership.contains("The Hobbit"));

        // TreeSet is useful when unique titles should be returned in sorted order.
        Set<String> sortedTitles = new TreeSet<>(List.of("The Hobbit", "Clean Code", "Effective Java"));
        System.out.println("TreeSet sorted titles: " + sortedTitles);

        List<String> loans = new ArrayList<>(List.of(
                "loan-101: overdue", "loan-102: current", "loan-103: overdue"));
        Iterator<String> loanIterator = loans.iterator();
        while (loanIterator.hasNext()) {
            if (loanIterator.next().contains("overdue")) {
                // Iterator.remove is safe during traversal; List.remove here would not be.
                loanIterator.remove();
            }
        }
        System.out.println("Loans after safe Iterator.remove: " + loans);

        demonstrateConcurrentModificationException();
        printTimings();
    }

    private static void demonstrateConcurrentModificationException() {
        List<String> unsafeExample = new ArrayList<>(List.of("first", "insert-trigger", "last"));
        try {
            // Deliberately unsafe example on an isolated list; adding changes its structure.
            for (String value : unsafeExample) {
                if (value.equals("insert-trigger")) {
                    unsafeExample.add("added during for-each");
                }
            }
        } catch (ConcurrentModificationException expected) {
            System.out.println("Caught ConcurrentModificationException from unsafe for-each modification.");
        }
    }

    private static void printTimings() {
        int initialSize = 8_000;
        int frontInsertions = 2_000;
        int indexedReads = 4_000;

        long arrayInsert = timeFrontInsert(new ArrayList<>(), initialSize, frontInsertions);
        long linkedInsert = timeFrontInsert(new LinkedList<>(), initialSize, frontInsertions);
        long arrayIndexed = timeIndexedReads(new ArrayList<>(), indexedReads);
        long linkedIndexed = timeIndexedReads(new LinkedList<>(), indexedReads);

        System.out.println("\nArrayList vs LinkedList timings (one local run):");
        System.out.println("Operation | ArrayList (ms) | LinkedList (ms)");
        System.out.printf("front insertion (%d adds, size %d) | %.3f | %.3f%n",
                frontInsertions, initialSize, millis(arrayInsert), millis(linkedInsert));
        System.out.printf("indexed get (all %d positions) | %.3f | %.3f%n",
                indexedReads, millis(arrayIndexed), millis(linkedIndexed));
        System.out.println("Timing is illustrative; JVM warmup, machine load, and hardware change results.");
    }

    private static long timeFrontInsert(List<Integer> list, int initialSize, int insertions) {
        for (int i = 0; i < initialSize; i++) {
            list.add(i);
        }
        long start = System.nanoTime();
        for (int i = 0; i < insertions; i++) {
            list.add(0, i);
        }
        return System.nanoTime() - start;
    }

    private static long timeIndexedReads(List<Integer> list, int size) {
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        long checksum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            checksum += list.get(i);
        }
        long elapsed = System.nanoTime() - start;
        if (checksum < 0) {
            throw new AssertionError("Unexpected timing checksum");
        }
        return elapsed;
    }

    private static double millis(long nanos) {
        return nanos / 1_000_000.0;
    }
}
