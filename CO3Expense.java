
public class CO3Expense {

    // Method to calculate total
    static double calculateTotal(double[] expenses) {

        double total = 0;

        for (int i = 0; i < expenses.length; i++) {
            total = total + expenses[i];
        }

        return total;
    }

    // Method to display message
    static void checkSavings(double budget, double total) {

        if (total < budget) {

            double saved = budget - total;

            System.out.println("Good! You saved your monthly budget.");
            System.out.println("Saved Amount: ₹" + saved);

        } else if (total == budget) {

            System.out.println("You used your full monthly budget.");

        } else {

            System.out.println("You exceeded your monthly budget.");
        }
    }

    public static void main(String[] args) {

        double budget = 10000;

        // 1D Array
        String[] items = {
            "Food",
            "Travel",
            "Shopping",
            "Gym"
        };

        double[] expenses = {
            3000,
            1500,
            2000,
            1000
        };

        System.out.println("--- Monthly Expenses ---");

        for (int i = 0; i < items.length; i++) {

            System.out.println(items[i] + ": Rs." + expenses[i]);
        }

        double total = calculateTotal(expenses);

        System.out.println("Total Expenses: Rs." + total);

        checkSavings(budget, total);

        // 2D Array
        double[][] monthlyExpenses = {
            {3000, 1500, 2000, 1000},
            {2500, 1200, 1500, 1000}
        };

        System.out.println("\n--- Two Month Expenses ---");

        for (int i = 0; i < monthlyExpenses.length; i++) {

            System.out.println("Month " + (i + 1));

            for (int j = 0; j < monthlyExpenses[i].length; j++) {

                System.out.println(items[j] + ": Rs."
                        + monthlyExpenses[i][j]);
            }
        }
    }
}