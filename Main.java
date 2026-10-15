
import java.util.ArrayList;
import java.util.Scanner;

class Expense {
    String category;
    double amount;

    Expense(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    void display() {
        System.out.println(category + " : Rs. " + amount);
    }
}

class BudgetAnalyzer {
    ArrayList<Expense> expenses = new ArrayList<>();
    double budget;

    BudgetAnalyzer(double budget) {
        this.budget = budget;
    }

    void addExpense(String category, double amount) {
        expenses.add(new Expense(category, amount));
        System.out.println("Expense added successfully!");
    }

    double calculateTotal() {
        double total = 0;

        for (Expense e : expenses) {
            total += e.amount;
        }

        return total;
    }

    void displayExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }

        System.out.println("\n--- Monthly Expenses ---");

        for (Expense e : expenses) {
            e.display();
        }

        System.out.println("Total Expense: Rs. "
                + calculateTotal());
    }

    void analyzeBudget() {
        double total = calculateTotal();
        double balance = budget - total;

        System.out.println("\n--- Budget Analysis ---");
        System.out.println("Monthly Budget: Rs. " + budget);
        System.out.println("Total Expense: Rs. " + total);

        if (balance > 0) {
            System.out.println("Remaining Budget: Rs. "
                    + balance);
            System.out.println("Status: Within Budget");
        } else if (balance == 0) {
            System.out.println("Remaining Budget: Rs. 0");
            System.out.println("Status: Budget Fully Used");
        } else {
            System.out.println("Overspending: Rs. "
                    + (-balance));
            System.out.println("Status: Over Budget");
        }

        if (budget > 0) {
            double percentage = (total / budget) * 100;
            System.out.printf("Budget Used: %.2f%%%n",
                    percentage);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Monthly Budget: Rs. ");
        double budget = sc.nextDouble();

        if (budget <= 0) {
            System.out.println(
                    "Budget must be greater than zero.");
            sc.close();
            return;
        }

        BudgetAnalyzer analyzer =
                new BudgetAnalyzer(budget);

        int choice;

        do {
            System.out.println("\n=== Monthly Expense "
                    + "and Budget Analysis ===");
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. Analyze Budget");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    sc.nextLine();

                    System.out.print(
                            "Enter Expense Category: ");
                    String category = sc.nextLine();

                    System.out.print(
                            "Enter Expense Amount: Rs. ");
                    double amount = sc.nextDouble();

                    if (category.trim().isEmpty()
                            || amount <= 0) {
                        System.out.println(
                                "Enter valid expense details.");
                    } else {
                        analyzer.addExpense(
                                category, amount);
                    }
                    break;

                case 2:
                    analyzer.displayExpenses();
                    break;

                case 3:
                    analyzer.analyzeBudget();
                    break;

                case 4:
                    System.out.println(
                            "Thank you for using the project!");
                    break;

                default:
                    System.out.println(
                            "Invalid choice. Try again.");
            }

        } while (choice != 4);

        sc.close();
    }
}

