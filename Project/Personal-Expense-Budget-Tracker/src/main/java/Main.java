import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

import com.hcltraining.exception.InsufficientFundsException;
import com.hcltraining.exception.InvalidTransactionAmountException;
import com.hcltraining.model.Account;
import com.hcltraining.model.Transaction;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Account account = new Account(1, "Everyday", "Checking", new BigDecimal("500.00"));
        int choice = 0;

        do {
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

            System.out.print("Enter your choice: ");
            if (!sc.hasNextLine()) {
                System.out.println("End of input. Exiting application...");
                break;
            }
            String input = sc.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("Create Account selected.");
                    break;

                case 2:
                    manageExpense(sc, account);
                    break;

                case 3:
                    System.out.println("View Account Balance selected. Current balance: "
                            + account.getBalance());
                    break;

                case 4:
                    System.out.println("Manage Recurring Transactions selected.");
                    break;

                case 5:
                    System.out.println("Set Monthly Budget selected.");
                    break;

                case 6:
                    System.out.println("View Budget Alerts selected.");
                    break;

                case 7:
                    System.out.println("Import CSV selected.");
                    break;

                case 8:
                    System.out.println("View Spending Insights selected.");
                    break;

                case 9:
                    System.out.println("Exiting application...");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 9);

        sc.close();
    }

    private static void manageExpense(Scanner sc, Account account) {
        System.out.println("Manage Transactions selected. Enter simulated expense amount:");
        if (!sc.hasNextLine()) {
            System.out.println("No amount entered; returning to menu.");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(sc.nextLine().trim());
            Transaction transaction = new Transaction(1, account.getId(), "EXPENSE",
                    "General", amount, LocalDate.now(), "Menu expense");
            account.withdraw(transaction.getAmount());
            System.out.println("Simulated expense recorded. Remaining balance: "
                    + account.getBalance());
        } catch (NumberFormatException cause) {
            InvalidTransactionAmountException invalidAmount =
                    new InvalidTransactionAmountException("Enter a valid monetary amount.", cause);
            System.out.println(invalidAmount.getMessage());
        } catch (InvalidTransactionAmountException | InsufficientFundsException failure) {
            System.out.println(failure.getMessage());
        }
    }
}
