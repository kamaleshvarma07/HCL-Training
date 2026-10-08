public class SampleData {

    public static void main(String[] args) {

        double[] weeklyExpenses = {
            200.0, 150.0, 300.0, 100.0,
            250.0, 400.0, 180.0
        };

        System.out.println("Weekly Expenses:");

        for (int i = 0; i < weeklyExpenses.length; i++) {
            System.out.println("Day " + (i + 1) + ": ₹" + weeklyExpenses[i]);
        }
    }
}