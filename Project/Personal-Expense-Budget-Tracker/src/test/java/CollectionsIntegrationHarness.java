import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.exception.InvalidTransactionAmountException;
import com.hcltraining.model.Account;
import com.hcltraining.model.Transaction;
import com.hcltraining.service.FinanceService;

/** Dependency-free checks for Day 7 PEBT FR1–FR3 and console recovery. */
public class CollectionsIntegrationHarness {
    public static void main(String[] args) throws Exception {
        verifyAccountAndTransactionOperations();
        verifyMenuRecovery();
        System.out.println("PASS: FR1 account creation, FR2 transaction CRUD, FR3 balances, IDs, and menu recovery");
    }

    private static void verifyAccountAndTransactionOperations() throws Exception {
        FinanceService finance = new FinanceService();
        Account account = finance.createAccount("Everyday", "Checking", new BigDecimal("100.00"));
        Account secondAccount = finance.createAccount("Reserve", "Savings", new BigDecimal("25.00"));
        require(account.getId() != secondAccount.getId(), "Accounts need distinct IDs");
        requireMoney("100.00", account.getBalance(), "Opening balance initializes current balance");

        Transaction income = new Transaction(1, account.getId(), "INCOME", "Salary",
                new BigDecimal("20.00"), LocalDate.now(), "Pay");
        finance.addTransaction(income);
        requireMoney("120.00", account.getBalance(), "Income updates running balance");

        Transaction expense = new Transaction(2, account.getId(), "EXPENSE", "Food",
                new BigDecimal("30.00"), LocalDate.now(), "Lunch");
        finance.addTransaction(expense);
        requireMoney("90.00", account.getBalance(), "Expense updates running balance");
        try {
            finance.addTransaction(new Transaction(2, account.getId(), "INCOME", "Other",
                    new BigDecimal("1.00"), LocalDate.now(), "Duplicate ID"));
            throw new AssertionError("Duplicate transaction ID must be rejected");
        } catch (IllegalArgumentException expected) {
            require(finance.getTransactions().size() == 2, "Duplicate must not enter ledger");
        }

        Transaction editedExpense = new Transaction(2, account.getId(), "EXPENSE", "Food",
                new BigDecimal("50.00"), LocalDate.now(), "Edited lunch");
        finance.editTransaction(2, editedExpense);
        requireMoney("70.00", account.getBalance(), "Edit recalculates running balance");
        try {
            finance.editTransaction(2, new Transaction(2, account.getId(), "EXPENSE", "Food",
                    new BigDecimal("121.00"), LocalDate.now(), "Invalid edit"));
            throw new AssertionError("Edit that overspends must be rejected");
        } catch (InsufficientFundsException expected) {
            requireMoney("70.00", account.getBalance(), "Rejected edit must preserve balance");
            requireMoney("50.00", finance.getTransaction(2).getAmount(),
                    "Rejected edit must preserve transaction");
        }

        finance.deleteTransaction(1);
        requireMoney("50.00", account.getBalance(), "Delete recalculates from opening balance");
        require(finance.getTransactions().size() == 1, "Delete removes transaction from list");
        try {
            finance.addTransaction(new Transaction(1, account.getId(), "INCOME", "Salary",
                    new BigDecimal("1.00"), LocalDate.now(), "Reused deleted ID"));
            throw new AssertionError("Deleted transaction IDs must not be reused");
        } catch (IllegalArgumentException expected) {
            require(finance.getTransactions().size() == 1,
                    "Reused deleted ID must not enter the ledger");
        }

        try {
            finance.addTransaction(new Transaction(3, account.getId(), "EXPENSE", "Travel",
                    new BigDecimal("51.00"), LocalDate.now(), "Overspend"));
            throw new AssertionError("Insufficient funds must reject transaction");
        } catch (InsufficientFundsException expected) {
            requireMoney("50.00", account.getBalance(), "Rejected add must preserve balance");
        }

        try {
            new Transaction(4, account.getId(), "EXPENSE", "Food", BigDecimal.ZERO,
                    LocalDate.now(), "Invalid zero");
            throw new AssertionError("Zero transaction amount must be rejected");
        } catch (InvalidTransactionAmountException expected) {
            // Expected business-rule rejection.
        }

        finance.enqueueTransaction(new Transaction(3, account.getId(), "INCOME", "Gift",
                new BigDecimal("5.00"), LocalDate.now(), "Queued income"));
        finance.enqueueTransaction(new Transaction(4, account.getId(), "EXPENSE", "Food",
                new BigDecimal("20.00"), LocalDate.now(), "Queued expense"));
        finance.processPendingTransactions();
        requireMoney("35.00", account.getBalance(), "FIFO queued transactions update balance");
    }

    private static void verifyMenuRecovery() {
        String input = "1\nDay 7\nChecking\n100.00\n"
                + "2\nmanage\n1\n2\nEXPENSE\n0\nFood\nZero amount\n"
                + "1\n2\nEXPENSE\n20.00\nFood\nLunch\n"
                + "5\n3\n2\n9\n";
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        java.io.InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        try (PrintStream output = new PrintStream(captured)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(output);
            Main.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        String result = captured.toString();
        require(result.contains("Transaction amount must be greater than zero."),
                "Menu should report invalid transaction amount");
        require(result.contains("Added transaction 1. Running balance: 80.00"),
                "Menu should continue with the valid transaction after the error");
        require(result.contains("Account 2 current balance: 80.00"),
                "FR3 balance should be available from the menu");
        require(result.contains("Exiting application..."), "Menu should continue through exit");
    }

    private static void requireMoney(String expected, BigDecimal actual, String message) {
        require(actual.compareTo(new BigDecimal(expected)) == 0, message
                + " (expected " + expected + ", got " + actual + ")");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
