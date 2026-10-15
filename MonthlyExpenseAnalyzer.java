import java.util.Scanner;

/**
 * Monthly Expense & Budget Analyzer
 * 
 * A beginner-friendly Java console application to track monthly income,
 * budget, and categorical expenses, and evaluate budget status using if-else conditions.
 */
public class MonthlyExpenseAnalyzer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================================");
        System.out.println("     MONTHLY EXPENSE & BUDGET ANALYZER");
        System.out.println("=================================================");

        // Step 1: Input Monthly Income
        System.out.print("Enter your Monthly Income (₹): ");
        double monthlyIncome = scanner.nextDouble();

        // Step 2: Input Monthly Budget
        System.out.print("Enter your Monthly Budget (₹): ");
        double monthlyBudget = scanner.nextDouble();

        // Step 3: Input Number of Expenses
        System.out.println("\n-------------------------------------------------");
        System.out.print("How many expenses would you like to enter? ");
        int numberOfExpenses = scanner.nextInt();

        double totalExpenses = 0.0;

        // Categories available:
        // 1. Food, 2. Travel, 3. Education, 4. Shopping,
        // 5. Entertainment, 6. Bills, 7. Other
        String[] categories = new String[numberOfExpenses];
        double[] amounts = new double[numberOfExpenses];

        System.out.println("\nAvailable Categories: Food, Travel, Education, Shopping, Entertainment, Bills, Other");
        System.out.println("-------------------------------------------------");

        for (int i = 0; i < numberOfExpenses; i++) {
            System.out.println("\n[Expense #" + (i + 1) + "]");
            System.out.print("Enter Category: ");
            categories[i] = scanner.next();

            System.out.print("Enter Amount Spent (₹): ");
            amounts[i] = scanner.nextDouble();

            // Arithmetic operator to calculate total expenses
            totalExpenses = totalExpenses + amounts[i];
        }

        // Step 4: Calculate Remaining Balance
        // Formula: Remaining Balance = Monthly Income - Total Expenses
        double remainingBalance = monthlyIncome - totalExpenses;

        // Step 5: Conditional Budget Analysis
        String budgetStatus;
        if (totalExpenses < monthlyBudget) {
            budgetStatus = "You are within your budget.";
        } else if (totalExpenses == monthlyBudget) {
            budgetStatus = "You have reached your budget limit.";
        } else {
            budgetStatus = "Warning! You have exceeded your budget.";
        }

        // Step 6: Display Final Summary Output
        System.out.println("\n=================================================");
        System.out.println("                 SUMMARY REPORT                  ");
        System.out.println("=================================================");
        System.out.printf("Monthly Income   : ₹%,.2f\n", monthlyIncome);
        System.out.printf("Monthly Budget   : ₹%,.2f\n", monthlyBudget);
        System.out.printf("Total Expenses   : ₹%,.2f\n", totalExpenses);
        System.out.printf("Remaining Balance: ₹%,.2f\n", remainingBalance);
        System.out.println("Budget Status    : " + budgetStatus);
        System.out.println("=================================================");

        // Display category-wise expense breakdown
        if (numberOfExpenses > 0) {
            System.out.println("\n--- Expense Itemization ---");
            for (int i = 0; i < numberOfExpenses; i++) {
                System.out.printf("%d. %-15s : ₹%,.2f\n", (i + 1), categories[i], amounts[i]);
            }
            System.out.println("---------------------------");
        }

        System.out.println("\nThank you for using Monthly Expense & Budget Analyzer!");
        scanner.close();
    }
}
