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
            if (!sc.hasNext()) {
                System.out.println("End of input. Exiting ATM.");
                sc.close();
                return;
            }

            if (!sc.hasNextInt()) {
                sc.next();
                System.out.println("Incorrect PIN.");
                continue;
            }

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
            Integer enteredChoice = readInt(sc, "Enter your choice: ");
            if (enteredChoice == null) {
                System.out.println("End of input. Exiting ATM.");
                choice = 5;
                break;
            }
            choice = enteredChoice;

            switch (choice) {

                case 1:
                    System.out.println("Current Balance: ₹" + balance);
                    break;

                case 2:
                    Double enteredDeposit = readDouble(sc, "Enter deposit amount: ");
                    if (enteredDeposit == null) {
                        System.out.println("End of input. Exiting ATM.");
                        choice = 5;
                        break;
                    }
                    double deposit = enteredDeposit;

                    if (!Double.isFinite(deposit) || deposit <= 0) {
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
                    Double enteredWithdrawal = readDouble(sc, "Enter withdrawal amount: ");
                    if (enteredWithdrawal == null) {
                        System.out.println("End of input. Exiting ATM.");
                        choice = 5;
                        break;
                    }
                    double withdrawal = enteredWithdrawal;

                    if (!Double.isFinite(withdrawal) || withdrawal <= 0) {
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
                        for (String transaction : transactions) {
                            if (transaction != null) {
                                System.out.println(transaction);
                            }
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

    private static Integer readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNext()) {
                return null;
            }
            if (sc.hasNextInt()) {
                return sc.nextInt();
            }
            sc.next();
            System.out.println("Invalid input. Please enter a whole number.");
        }
    }

    private static Double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNext()) {
                return null;
            }
            if (sc.hasNextDouble()) {
                return sc.nextDouble();
            }
            sc.next();
            System.out.println("Invalid input. Please enter a numeric amount.");
        }
    }
}
