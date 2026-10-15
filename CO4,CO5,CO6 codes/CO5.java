import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CO5 {
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

    static List<Expense> expenses = new ArrayList<>();
    static int nextId = 1;

    static class ExpenseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response;
            int status;

            if ("POST".equalsIgnoreCase(method)) {
                String body = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);
                String title = "Expense";
                double amount = 0;

                try {
                    if (body.contains("\"title\"")) {
                        title = body.split("\"title\"\\s*:\\s*\"")[1].split("\"")[0];
                    }
                    if (body.contains("\"amount\"")) {
                        String value = body.split("\"amount\"\\s*:\\s*")[1]
                                .split("[,}]")[0].replace("\"", "").trim();
                        amount = Double.parseDouble(value);
                    }
                } catch (Exception e) {
                    amount = 0;
                }

                if (amount <= 0) {
                    status = 400;
                    response = "{\"success\":false,\"error\":\"Invalid amount\"}";
                } else {
                    expenses.add(new Expense(nextId++, title, amount));
                    status = 201;
                    response = "{\"success\":true}";
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                String query = exchange.getRequestURI().getQuery();
                boolean removed = false;
                if (query != null && query.startsWith("id=")) {
                    try {
                        int id = Integer.parseInt(query.substring(3));
                        removed = expenses.removeIf(e -> e.id == id);
                    } catch (NumberFormatException ignored) { }
                }
                status = removed ? 200 : 400;
                response = removed ? "{\"success\":true}"
                        : "{\"success\":false,\"error\":\"Invalid ID\"}";
            } else {
                status = 405;
                response = "{\"error\":\"Method not allowed\"}";
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("CO5: ExpenseHandler supports POST and DELETE requests.");
    }
}

