import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CO6 {
    static class Expense {
        int id;
        String title;
        double amount;
        Expense(int id, String title, double amount) {
            this.id = id;
            this.title = title;
            this.amount = amount;
        }
    }

    static double monthlyIncome = 50000;
    static double monthlyBudget = 30000;
    static List<Expense> expenses = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        expenses.add(new Expense(1, "Grocery Store", 4500));
        expenses.add(new Expense(2, "Metro Card", 2800));

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String page = "<html><body><h1>Monthly Expense & Budget Analyzer</h1>"
                        + "<p>Income: " + monthlyIncome + "</p>"
                        + "<p>Budget: " + monthlyBudget + "</p>"
                        + "<p>Expenses recorded: " + expenses.size() + "</p>"
                        + "</body></html>";
                byte[] bytes = page.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(bytes);
                }
            }
        });

        server.start();
        System.out.println("Server running at http://localhost:8080");
    }
}

