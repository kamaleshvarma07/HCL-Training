import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.model.Account;
import com.hcltraining.model.Transaction;
import com.hcltraining.service.FinanceService;

/** Focused runtime checks for Day 8 recurring transactions, budgets, and alert boundaries. */
public class Day8BudgetIntegrationHarness {
    public static void main(String[] args) throws Exception {
        verifyRecurringTransactions();
        verifyMonthlyTotalsAndAlerts();
        System.out.println("PASS: recurring occurrences, month-end dates, monthly category totals, and exact 80%/100% alerts");
    }

    private static void verifyRecurringTransactions() throws Exception {
        FinanceService finance = new FinanceService();
        Account account = finance.createAccount("Bills", "Checking", new BigDecimal("1000.00"));
        finance.addRecurringRule(account.getId(), "EXPENSE", "Rent", new BigDecimal("100.00"),
                31, LocalDate.of(2026, 1, 31), "Monthly rent");
        List<Transaction> created = finance.applyRecurringTransactions(LocalDate.of(2026, 3, 31));
        require(created.size() == 3, "Should backfill each due month once");
        require(created.get(1).getDate().equals(LocalDate.of(2026, 2, 28)),
                "Day 31 should clamp to February month end");
        require(finance.applyRecurringTransactions(LocalDate.of(2026, 3, 31)).isEmpty(),
                "Reprocessing through the same date must not duplicate occurrences");
        requireMoney("700.00", account.getBalance(), "Recurring expenses use normal ledger balance rules");
    }

    private static void verifyMonthlyTotalsAndAlerts() throws Exception {
        FinanceService finance = new FinanceService();
        Account account = finance.createAccount("Daily", "Checking", new BigDecimal("1000.00"));
        YearMonth month = YearMonth.of(2026, 5);
        finance.setMonthlyBudget("Food", new BigDecimal("100.00"), month);
        addExpense(finance, account, 1, "Food", "79.99", LocalDate.of(2026, 5, 2));
        require(finance.getTriggeredBudgetAlerts("FOOD", month).isEmpty(),
                "Below 80 percent must not alert");
        addExpense(finance, account, 2, "Food", "0.01", LocalDate.of(2026, 5, 3));
        require(finance.getTriggeredBudgetAlerts("food", month).equals(List.of(80)),
                "Exact 80 percent must trigger only the warning");
        addExpense(finance, account, 3, "Food", "20.00", LocalDate.of(2026, 5, 4));
        require(finance.getTriggeredBudgetAlerts("Food", month).equals(List.of(80, 100)),
                "Exact 100 percent must trigger both thresholds");
        requireMoney("100.00", finance.getMonthlyCategoryTotal("Food", month),
                "Category totals must sum BigDecimal expenses for the requested month");
        require(finance.getMonthlyCategoryTotals(YearMonth.of(2026, 6)).isEmpty(),
                "Transactions from another month must not enter this month's totals");
    }

    private static void addExpense(FinanceService finance, Account account, int id,
                                   String category, String amount, LocalDate date)
            throws InsufficientFundsException {
        finance.addTransaction(new Transaction(id, account.getId(), "EXPENSE", category,
                new BigDecimal(amount), date, "Test expense"));
    }

    private static void requireMoney(String expected, BigDecimal actual, String message) {
        require(actual.compareTo(new BigDecimal(expected)) == 0, message);
    }
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
