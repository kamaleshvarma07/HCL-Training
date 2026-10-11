import java.math.BigDecimal;
import java.math.RoundingMode;

public class SampleData {

    public static void main(String[] args) {

        BigDecimal[] weeklyExpenses = {
            new BigDecimal("200.00"), new BigDecimal("150.00"),
            new BigDecimal("300.00"), new BigDecimal("100.00"),
            new BigDecimal("250.00"), new BigDecimal("400.00"),
            new BigDecimal("180.00")
        };

        System.out.println("Weekly Expenses:");

        for (int i = 0; i < weeklyExpenses.length; i++) {
            System.out.println("Day " + (i + 1) + ": ₹" + weeklyExpenses[i]);
        }
		

	BigDecimal total = BigDecimal.ZERO;

	for (BigDecimal expense : weeklyExpenses) {
		total = total.add(expense);
	}

	System.out.println("Total Weekly Expense: ₹" + total);
	
	BigDecimal average = total.divide(
			BigDecimal.valueOf(weeklyExpenses.length), 2, RoundingMode.HALF_UP);

	System.out.println("Average Daily Expense: ₹" + average);
    }
}
