import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.exception.InvalidTransactionAmountException;
import com.hcltraining.model.Account;
import com.hcltraining.model.Transaction;

/** Dependency-free focused checks for Day 6 business rules and menu recovery. */
public class BusinessRulesHarness {
    public static void main(String[] args) throws Exception {
        checkInvalidAmounts();
        checkInsufficientFunds();
        checkMenuRecovery();
        System.out.println("PASS: invalid amounts, insufficient funds, and menu recovery");
    }

    private static void checkInvalidAmounts() {
        expectInvalidAmount(() -> new Transaction(1, 1, "EXPENSE", "General", null,
                LocalDate.now(), "null amount"));
        expectInvalidAmount(() -> new Transaction(1, 1, "EXPENSE", "General",
                new BigDecimal("0.00"), LocalDate.now(), "zero amount"));
        expectInvalidAmount(() -> new Transaction(1, 1, "EXPENSE", "General",
                new BigDecimal("-1.00"), LocalDate.now(), "negative amount"));
        expectInvalidAmount(() -> Account.requirePositiveAmount(new BigDecimal("-1.00")));
    }

    private static void expectInvalidAmount(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Expected InvalidTransactionAmountException");
        } catch (InvalidTransactionAmountException expected) {
            // Expected business-rule rejection.
        }
    }

    private static void checkInsufficientFunds() throws Exception {
        Account account = new Account(1, "Test", "Checking", new BigDecimal("20.00"));
        try {
            account.withdraw(new BigDecimal("20.01"));
            throw new AssertionError("Expected InsufficientFundsException");
        } catch (InsufficientFundsException expected) {
            require(account.getBalance().compareTo(new BigDecimal("20.00")) == 0,
                    "Failed withdrawal must leave balance unchanged");
        }
        account.withdraw(new BigDecimal("5.00"));
        require(account.getBalance().compareTo(new BigDecimal("15.00")) == 0,
                "Successful withdrawal must update balance");
    }

    private static void checkMenuRecovery() {
        String input = "2\noops\n2\n0\n2\n9999.00\n2\n10.00\n9\n";
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
        require(result.contains("Enter a valid monetary amount."),
                "Menu should report malformed monetary input");
        require(result.contains("Transaction amount must be greater than zero."),
                "Menu should report invalid amount");
        require(result.contains("Insufficient funds:"), "Menu should report insufficient funds");
        require(result.contains("Simulated expense recorded. Remaining balance: 490.00"),
                "Menu should continue and process a later valid transaction");
        require(result.contains("Exiting application..."), "Menu should accept exit after failures");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
