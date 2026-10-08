public class MonthlyUsageAnalyser {

    // Slab constants
    public static final int SLAB_1_LIMIT = 100;
    public static final int SLAB_2_LIMIT = 200;

    public static void main(String[] args) {

        // 12 months of usage
        int[] monthlyUsage = {
            120, 150, 180, 220,
            250, 300, 280, 190,
            160, 140, 130, 110
        };

        int total = 0;
        int max = monthlyUsage[0];
        int min = monthlyUsage[0];

        // Total, maximum and minimum
        for (int usage : monthlyUsage) {
            total += usage;

            if (usage > max) {
                max = usage;
            }

            if (usage < min) {
                min = usage;
            }
        }

        // Cast to double to get decimal average
        double average = (double) total / monthlyUsage.length;

        // Ternary operator for grade
        char grade = average >= 200 ? 'A' : average >= 150 ? 'B' : 'C';

        System.out.println("===== Monthly Usage Analyser =====");
        System.out.println("Total Usage   : " + total);
        System.out.println("Average Usage : " + average);
        System.out.println("Maximum Usage : " + max);
        System.out.println("Minimum Usage : " + min);
        System.out.println("Grade         : " + grade);

        // Long to prevent integer overflow
        long largeUsage = 2_000_000_000L;
        long additionalUsage = 2_000_000_000L;
        long combinedUsage = largeUsage + additionalUsage;

        System.out.println("Large Usage Total: " + combinedUsage);

        // 2-D array for 3 houses
        int[][] houseUsage = {
            {120, 150, 180},
            {200, 220, 250},
            {100, 130, 160}
        };

        System.out.println("\n===== Usage of 3 Houses =====");

        for (int house = 0; house < houseUsage.length; house++) {
            System.out.print("House " + (house + 1) + ": ");

            for (int usage : houseUsage[house]) {
                System.out.print(usage + " ");
            }

            System.out.println();
        }
    }
}