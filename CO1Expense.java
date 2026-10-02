import java.util.Scanner;

public class CO1Expense {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        double budget, food, travel, shopping, gym;

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

        double total = food + travel + shopping + gym;
        double remaining = budget - total;

        System.out.println("\n--- Monthly Expense Report ---");

        System.out.println("Total Expenses: Rs." + total);
        System.out.println("Remaining Budget: Rs." + remaining);

        sc.close();
    }
}