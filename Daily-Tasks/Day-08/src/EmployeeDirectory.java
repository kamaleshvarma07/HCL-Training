import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/** Standalone Day 8 example: Map choices and Comparable/Comparator sorting. */
public class EmployeeDirectory {
    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee(104, "Nina", "Engineering", 112_000),
                new Employee(101, "Omar", "Finance", 98_000),
                new Employee(103, "Priya", "Engineering", 125_000),
                new Employee(102, "Luca", "Finance", 98_000),
                new Employee(105, "Maya", "Engineering", 105_000),
                new Employee(106, "Eli", "Support", 90_000));

        // HashMap is best here because lookups/frequency counts need no key order.
        Map<Integer, Employee> byId = new HashMap<>();
        Map<String, Integer> departmentCounts = new HashMap<>();
        for (Employee employee : employees) {
            byId.put(employee.id(), employee);
            departmentCounts.merge(employee.department(), 1, Integer::sum);
        }
        System.out.println("ID 103 lookup: " + byId.get(103).name());
        System.out.println("Department counts: " + new TreeMap<>(departmentCounts));

        // LinkedHashMap keeps the first-seen order when a report needs predictable display.
        Map<String, String> firstSeen = new LinkedHashMap<>();
        employees.forEach(employee -> firstSeen.putIfAbsent(employee.department(), employee.name()));
        System.out.println("First employee by department: " + firstSeen);

        // TreeMap sorts department keys naturally for a readable grouped report.
        Map<String, List<Employee>> byDepartment = new TreeMap<>();
        for (Employee employee : employees) {
            byDepartment.computeIfAbsent(employee.department(), ignored -> new ArrayList<>()).add(employee);
        }
        System.out.println("Department report: " + byDepartment.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().stream()
                        .map(Employee::name).toList())
                .toList());

        List<Employee> naturalOrder = new ArrayList<>(employees);
        naturalOrder.sort(null); // Comparable supplies the natural employee-ID order.
        System.out.println("Comparable by ID: " + naturalOrder.stream().map(Employee::id).toList());

        Comparator<Employee> salaryThenName = Comparator.comparingDouble(Employee::salary).reversed()
                .thenComparing(Employee::name);
        System.out.println("Top 5 salaries: " + employees.stream().sorted(salaryThenName).limit(5)
                .map(employee -> employee.name() + "=$" + (int) employee.salary()).toList());

        demonstrateHashContract();
    }

    private static void demonstrateHashContract() {
        Set<BrokenEmployeeKey> broken = new HashSet<>();
        broken.add(new BrokenEmployeeKey(7));
        System.out.println("Broken equals/hashCode lookup: " + broken.contains(new BrokenEmployeeKey(7)));
        Set<EmployeeKey> fixed = new HashSet<>();
        fixed.add(new EmployeeKey(7));
        System.out.println("Fixed equals/hashCode lookup: " + fixed.contains(new EmployeeKey(7)));
    }

    private static final class Employee implements Comparable<Employee> {
        private final int id;
        private final String name;
        private final String department;
        private final double salary;

        private Employee(int id, String name, String department, double salary) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        int id() { return id; }
        String name() { return name; }
        String department() { return department; }
        double salary() { return salary; }

        @Override
        public int compareTo(Employee other) {
            return Integer.compare(id, other.id);
        }
    }

    private static final class BrokenEmployeeKey {
        private final int id;
        private BrokenEmployeeKey(int id) { this.id = id; }
        @Override public boolean equals(Object other) {
            return other instanceof BrokenEmployeeKey key && id == key.id;
        }
        // Deliberate bug: equals says equal IDs, but Object.hashCode remains identity based.
    }

    private static final class EmployeeKey {
        private final int id;
        private EmployeeKey(int id) { this.id = id; }
        @Override public boolean equals(Object other) {
            return other instanceof EmployeeKey key && id == key.id;
        }
        @Override public int hashCode() { return Objects.hash(id); }
    }
}
