import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Scanner;

import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.exception.InvalidTransactionAmountException;
import com.hcltraining.model.Account;
import com.hcltraining.model.Transaction;
import com.hcltraining.model.Budget;
import com.hcltraining.model.RecurringRule;
import com.hcltraining.service.FinanceService;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FinanceService finance = new FinanceService();
        // Retains the Day 6 sample account and quick-expense interaction for existing practice.
        finance.createAccount("Everyday", "Checking", new BigDecimal("500.00"));
        boolean running = true;

        while (running) {
            System.out.println("\n===== PERSONAL EXPENSE & BUDGET TRACKER =====");
            System.out.println("1. Create Account");
            System.out.println("2. Manage Transactions");
            System.out.println("3. View Account Balance");
            System.out.println("4. Manage Recurring Transactions");
            System.out.println("5. Set Monthly Budget");
            System.out.println("6. View Budget Alerts");
            System.out.println("7. Import CSV");
            System.out.println("8. View Spending Insights");
            System.out.println("9. Exit");

            String choiceText = prompt(sc, "Enter your choice: ");
            if (choiceText == null) {
                System.out.println("End of input. Exiting application...");
                break;
            }
            final int choice;
            try {
                choice = Integer.parseInt(choiceText.trim());
            } catch (NumberFormatException invalidChoice) {
                System.out.println("Invalid choice. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    createAccount(sc, finance);
                    break;
                case 2:
                    manageTransactions(sc, finance);
                    break;
                case 3:
                    viewBalance(sc, finance);
                    break;
                case 4:
                    manageRecurringTransactions(sc, finance);
                    break;
                case 5:
                    setMonthlyBudget(sc, finance);
                    break;
                case 6:
                    viewBudgetAlerts(sc, finance);
                    break;
                case 7:
                    System.out.println("Import CSV selected.");
                    break;
                case 8:
                    System.out.println("View Spending Insights selected.");
                    break;
                case 9:
                    System.out.println("Exiting application...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        sc.close();
    }

    private static void createAccount(Scanner sc, FinanceService finance) {
        try {
            String name = prompt(sc, "Account name: ");
            String type = prompt(sc, "Account type: ");
            BigDecimal openingBalance = promptMoney(sc, "Opening balance: ");
            if (name == null || type == null || openingBalance == null) {
                System.out.println("Account creation cancelled; returning to menu.");
                return;
            }
            Account account = finance.createAccount(name, type, openingBalance);
            System.out.println("Created account " + account.getId() + " with opening balance "
                    + account.getBalance() + ".");
        } catch (IllegalArgumentException failure) {
            System.out.println(failure.getMessage());
        }
    }

    private static void manageTransactions(Scanner sc, FinanceService finance) {
        String firstAction = prompt(sc,
                "Expense amount for account 1, or type 'manage' for transaction CRUD: ");
        if (firstAction == null) {
            return;
        }
        if (!firstAction.trim().equalsIgnoreCase("manage")) {
            try {
                BigDecimal amount = parseMoney(firstAction);
                Transaction transaction = finance.createAndAddTransaction(1, "EXPENSE",
                        "General", amount, "Menu expense");
                System.out.println("Simulated expense recorded. Remaining balance: "
                        + finance.getAccount(1).getBalance());
            } catch (InsufficientFundsException | IllegalArgumentException failure) {
                System.out.println(failure.getMessage());
            }
            return;
        }

        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Transactions ---");
            System.out.println("1. Add transaction");
            System.out.println("2. Edit transaction");
            System.out.println("3. Delete transaction");
            System.out.println("4. List transactions");
            System.out.println("5. Return to main menu");
            String choiceText = prompt(sc, "Choose transaction action: ");
            if (choiceText == null) {
                return;
            }
            final int action;
            try {
                action = Integer.parseInt(choiceText.trim());
            } catch (NumberFormatException invalidChoice) {
                System.out.println("Invalid transaction choice. Enter a number.");
                continue;
            }

            switch (action) {
                case 1 -> addTransaction(sc, finance);
                case 2 -> editTransaction(sc, finance);
                case 3 -> deleteTransaction(sc, finance);
                case 4 -> listTransactions(finance);
                case 5 -> managing = false;
                default -> System.out.println("Invalid transaction action.");
            }
        }
    }

    private static void addTransaction(Scanner sc, FinanceService finance) {
        try {
            int accountId = promptInt(sc, "Account ID: ");
            String type = prompt(sc, "Type (INCOME/EXPENSE): ");
            BigDecimal amount = promptMoney(sc, "Amount: ");
            String category = prompt(sc, "Category: ");
            String description = prompt(sc, "Description: ");
            if (type == null || amount == null || category == null || description == null) {
                System.out.println("Transaction cancelled; returning to transaction menu.");
                return;
            }
            Transaction transaction = finance.createAndAddTransaction(accountId, type,
                    category, amount, description);
            Account account = finance.getAccount(accountId);
            System.out.println("Added transaction " + transaction.getId()
                    + ". Running balance: " + account.getBalance());
        } catch (InsufficientFundsException | IllegalArgumentException failure) {
            System.out.println(failure.getMessage());
        }
    }

    private static void editTransaction(Scanner sc, FinanceService finance) {
        try {
            int transactionId = promptInt(sc, "Transaction ID to edit: ");
            Transaction current = finance.getTransaction(transactionId);
            String type = prompt(sc, "New type (INCOME/EXPENSE): ");
            BigDecimal amount = promptMoney(sc, "New amount: ");
            String category = prompt(sc, "New category: ");
            String description = prompt(sc, "New description: ");
            if (type == null || amount == null || category == null || description == null) {
                System.out.println("Edit cancelled; returning to transaction menu.");
                return;
            }
            Transaction replacement = new Transaction(current.getId(), current.getAccountId(),
                    type, category, amount, LocalDate.now(), description);
            finance.editTransaction(transactionId, replacement);
            System.out.println("Edited transaction " + transactionId + ". Running balance: "
                    + finance.getAccount(current.getAccountId()).getBalance());
        } catch (InsufficientFundsException | IllegalArgumentException failure) {
            System.out.println(failure.getMessage());
        }
    }

    private static void deleteTransaction(Scanner sc, FinanceService finance) {
        try {
            int transactionId = promptInt(sc, "Transaction ID to delete: ");
            Transaction current = finance.getTransaction(transactionId);
            finance.deleteTransaction(transactionId);
            System.out.println("Deleted transaction " + transactionId + ". Running balance: "
                    + finance.getAccount(current.getAccountId()).getBalance());
        } catch (InsufficientFundsException | IllegalArgumentException failure) {
            System.out.println(failure.getMessage());
        }
    }

    private static void listTransactions(FinanceService finance) {
        if (finance.getTransactions().isEmpty()) {
            System.out.println("No transactions.");
            return;
        }
        for (Transaction transaction : finance.getTransactions()) {
            System.out.println("ID=" + transaction.getId() + ", account="
                    + transaction.getAccountId() + ", type=" + transaction.getType()
                    + ", amount=" + transaction.getAmount() + ", category="
                    + transaction.getCategory());
        }
    }

    private static void viewBalance(Scanner sc, FinanceService finance) {
        try {
            int accountId = promptInt(sc, "Account ID: ");
            Account account = finance.getAccount(accountId);
            System.out.println("Account " + account.getId() + " current balance: "
                    + account.getBalance());
        } catch (IllegalArgumentException failure) {
            System.out.println(failure.getMessage());
        }
    }

    private static void manageRecurringTransactions(Scanner sc, FinanceService finance) {
        try {
            int accountId = promptInt(sc, "Account ID: ");
            String type = prompt(sc, "Type (INCOME/EXPENSE): ");
            BigDecimal amount = promptMoney(sc, "Amount: ");
            String category = prompt(sc, "Category: ");
            int day = promptInt(sc, "Day of month (1-31): ");
            String startText = prompt(sc, "Start date (YYYY-MM-DD): ");
            String description = prompt(sc, "Description: ");
            if (type == null || amount == null || category == null || startText == null
                    || description == null) {
                System.out.println("Recurring rule cancelled.");
                return;
            }
            RecurringRule rule = finance.addRecurringRule(accountId, type, category, amount,
                    day, LocalDate.parse(startText.trim()), description);
            System.out.println("Created recurring rule " + rule.getId() + ".");
            int count = finance.applyRecurringTransactions(LocalDate.now()).size();
            System.out.println("Applied " + count + " due occurrence(s); repeats are skipped.");
        } catch (InsufficientFundsException | java.time.DateTimeException
                 | IllegalArgumentException failure) {
            System.out.println(failure.getMessage());
        }
    }

    private static void setMonthlyBudget(Scanner sc, FinanceService finance) {
        try {
            String category = prompt(sc, "Budget category: ");
            BigDecimal limit = promptMoney(sc, "Monthly limit: ");
            String monthText = prompt(sc, "Month (YYYY-MM): ");
            if (category == null || limit == null || monthText == null) {
                System.out.println("Budget cancelled.");
                return;
            }
            Budget budget = finance.setMonthlyBudget(category, limit,
                    YearMonth.parse(monthText.trim()));
            System.out.println("Budget set: " + budget.getCategory() + " "+budget.getYear()+"-"
                    + String.format("%02d", budget.getMonth()) + " limit " + budget.getLimit());
        } catch (java.time.DateTimeException | IllegalArgumentException failure) {
            System.out.println(failure.getMessage());
        }
    }

    private static void viewBudgetAlerts(Scanner sc, FinanceService finance) {
        try {
            String monthText = prompt(sc, "Month (YYYY-MM): ");
            if (monthText == null) return;
            YearMonth month = YearMonth.parse(monthText.trim());
            for (Budget budget : finance.getBudgets()) {
                if (budget.getYear() == month.getYear() && budget.getMonth() == month.getMonthValue()) {
                    String category = budget.getCategory();
                    System.out.println(category + " spent "
                            + finance.getMonthlyCategoryTotal(category, month) + " / " + budget.getLimit()
                            + "; alerts=" + finance.getTriggeredBudgetAlerts(category, month));
                }
            }
        } catch (java.time.DateTimeException | IllegalArgumentException failure) {
            System.out.println(failure.getMessage());
        }
    }

    private static String prompt(Scanner sc, String label) {
        System.out.print(label);
        return sc.hasNextLine() ? sc.nextLine() : null;
    }

    private static int promptInt(Scanner sc, String label) {
        String text = prompt(sc, label);
        if (text == null) {
            throw new IllegalArgumentException("No value entered.");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException cause) {
            throw new IllegalArgumentException("Enter a whole number.", cause);
        }
    }

    private static BigDecimal promptMoney(Scanner sc, String label) {
        String text = prompt(sc, label);
        if (text == null) {
            return null;
        }
        return parseMoney(text);
    }

    private static BigDecimal parseMoney(String text) {
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException cause) {
            throw new InvalidTransactionAmountException("Enter a valid monetary amount.", cause);
        }
    }
}
