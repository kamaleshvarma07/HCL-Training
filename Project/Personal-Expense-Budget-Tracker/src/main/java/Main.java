import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int choice;

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
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("Create Account selected.");
                    break;

                case 2:
                    System.out.println("Manage Transactions selected.");
                    break;

                case 3:
                    System.out.println("View Account Balance selected.");
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
}
