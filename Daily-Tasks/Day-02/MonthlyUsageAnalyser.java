public class MonthlyUsageAnalyser {

    // Slab constants
    public static final int SLAB_1_LIMIT = 100;
    public static final int SLAB_2_LIMIT = 200;

    // Grade thresholds
    public static final int GRADE_A_THRESHOLD = 200;
    public static final int GRADE_B_THRESHOLD = 150;

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
        char grade = average >= GRADE_A_THRESHOLD ? 'A'
                : average >= GRADE_B_THRESHOLD ? 'B' : 'C';

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

        // Primitive ranges (boolean has two values: true and false)
        System.out.println("\n===== Primitive Ranges =====");
        System.out.println("byte: " + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE);
        System.out.println("short: " + Short.MIN_VALUE + " to " + Short.MAX_VALUE);
        System.out.println("int: " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE);
        System.out.println("long: " + Long.MIN_VALUE + " to " + Long.MAX_VALUE);
        System.out.println("float: " + (-Float.MAX_VALUE) + " to " + Float.MAX_VALUE);
        System.out.println("double: " + (-Double.MAX_VALUE) + " to " + Double.MAX_VALUE);
        System.out.println("char: " + (int) Character.MIN_VALUE + " to " + (int) Character.MAX_VALUE);
        System.out.println("boolean: true or false");

        // Narrowing cast discards the fractional part
        double decimalUsage = 9.75;
        int wholeUsage = (int) decimalUsage;
        System.out.println("Narrowing cast: (int) 9.75 = " + wholeUsage);

        // Multiplication has higher precedence than addition
        System.out.println("Precedence: 2 + 3 * 4 = " + (2 + 3 * 4));
        System.out.println("Parentheses: (2 + 3) * 4 = " + ((2 + 3) * 4));

        // Binary floating-point cannot represent every decimal exactly
        double decimalSum = 0.1 + 0.2;
        System.out.println("Floating-point: 0.1 + 0.2 = " + decimalSum);
        System.out.println("Is the sum exactly 0.3? " + (decimalSum == 0.3));
    }
}
