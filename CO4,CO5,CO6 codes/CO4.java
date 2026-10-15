import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CO4 {
    static class Expense {
        double amount;
        Expense(double amount) { this.amount = amount; }
    }

    static List<Expense> expenses = new ArrayList<>();
    static double monthlyIncome = 50000;
    static double monthlyBudget = 30000;

    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            double total = 0;
            for (Expense e : expenses) total += e.amount;

            String status = total < monthlyBudget ? "Within Budget"
                    : total == monthlyBudget ? "Limit Reached" : "Budget Exceeded";
            double balance = monthlyIncome - total;

            String json = "{\"totalExpenses\":" + total
                    + ",\"remainingBalance\":" + balance
                    + ",\"status\":\"" + status + "\"}";

            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        }
    }

    public static void main(String[] args) {
        expenses.add(new Expense(4500));
        expenses.add(new Expense(2800));
        System.out.println("CO4: DataHandler is ready to be registered with HttpServer.");
    }
}
