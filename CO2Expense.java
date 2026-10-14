import java.util.Scanner;

public class CO2Expense {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        double budget, food, travel, shopping, gym;
        double total, remaining;

        System.out.print("Enter monthly budget: ");
        budget = sc.nextDouble();

        System.out.print("Enter food expenses: ");
        food = sc.nextDouble();

        System.out.print("Enter travel expenses: ");
        travel = sc.nextDouble();

        System.out.print("Enter shopping expenses: ");
        shopping = sc.nextDouble();

        System.out.print("Enter gym expenses: ");
        gym = sc.nextDouble();

        total = food + travel + shopping + gym;
        remaining = budget - total;

        System.out.println("\n--- Monthly Expense Report ---");

        System.out.println("Food: " +   food);
        System.out.println("Travel: " +  travel);
        System.out.println("Shopping: " +  shopping);
        System.out.println("Gym: " +  gym);

        System.out.println("Total Expenses: " +  total);

        // Conditional Statements
        if (total < budget) {

            System.out.println("Good! You saved your monthly budget.");
            System.out.println("Saved Amount: Rs." + remaining);

        } else if (total == budget) {

            System.out.println("You used your full monthly budget.");

        } else {

            System.out.println("You exceeded your monthly budget.");
            System.out.println("Extra Spending: Rs." + (-remaining));
        }

        // Loop
        System.out.println("\n--- Monthly Budget Tips ---");

        for (int i = 1; i <= 3; i++) {

            if (i == 1) {
                System.out.println("Tip 1: Reduce unnecessary shopping.");
            } else if (i == 2) {
                System.out.println("Tip 2: Track your food expenses.");
            } else {
                System.out.println("Tip 3: Save money every month.");
            }
        }

        sc.close();
    }
}