import java.util.Scanner;

public class ATM {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        final int CORRECT_PIN = 1234;
        double balance = 5000.0;
        boolean authenticated = false;

        String[] transactions = new String[5];
        int transactionCount = 0;

        // 3 PIN attempts
        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.print("Enter PIN: ");
            int pin = sc.nextInt();

            if (pin == CORRECT_PIN) {
                System.out.println("Login successful!");
                authenticated = true;
                break;
            } else {
                System.out.println("Incorrect PIN.");
            }
        }

        if (!authenticated) {
            System.out.println("Account blocked. Too many incorrect attempts.");
            sc.close();
            return;
        }

        // ATM menu
        int choice;

        do {
            System.out.println("\n===== ATM MENU =====");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Mini Statement");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("Current Balance: ₹" + balance);
                    break;

                case 2:
                    System.out.print("Enter deposit amount: ");
                    double deposit = sc.nextDouble();

                    if (deposit <= 0) {
                        System.out.println("Invalid amount.");
                        continue;
                    }

                    balance += deposit;

                    if (transactionCount < transactions.length) {
                        transactions[transactionCount++] =
                                "Deposited: ₹" + deposit;
                    }

                    System.out.println("Deposit successful.");
                    break;

                case 3:
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawal = sc.nextDouble();

                    if (withdrawal <= 0) {
                        System.out.println("Invalid amount.");
                        continue;
                    }

                    if (withdrawal > balance) {
                        System.out.println("Insufficient balance.");
                        continue;
                    }

                    balance -= withdrawal;

                    if (transactionCount < transactions.length) {
                        transactions[transactionCount++] =
                                "Withdrawn: ₹" + withdrawal;
                    }

                    System.out.println("Withdrawal successful.");
                    break;

                case 4:
                    System.out.println("\n===== MINI STATEMENT =====");

                    if (transactionCount == 0) {
                        System.out.println("No transactions.");
                    } else {
                        for (int i = 0; i < transactionCount; i++) {
                            System.out.println(transactions[i]);
                        }
                    }

                    break;

                case 5:
                    System.out.println("Thank you for using the ATM!");
                    break;

                default:
                    System.out.println("Invalid choice.");
                    continue;
            }

        } while (choice != 5);

        sc.close();
    }
}