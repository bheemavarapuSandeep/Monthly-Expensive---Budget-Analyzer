
  import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class ExpenseMonthly {
    static class Expense {
        int id;
        String title;
        String category;
        double amount;
        String date;

        Expense(int id, String title, String category, double amount, String date) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.amount = amount;
            this.date = date;
        }
    }    
      static final List<Expense> expenses = new ArrayList<>();
    static final AtomicInteger nextId = new AtomicInteger(1);
    static double monthlyIncome = 50000;
    static double monthlyBudget = 30000;

    static double calculateTotal(double[] values) {
        double total = 0;
        for (double value : values) total += value;
        return total;
    }

    static String budgetStatus(double total) {
        if (total < monthlyBudget) return "Within Budget";
        if (total == monthlyBudget) return "Budget Limit Reached";
        return "Budget Exceeded";
    }

    static void runConsole(Scanner sc) {
        System.out.print("Enter monthly income: ");
        monthlyIncome = readNonNegative(sc);
        System.out.print("Enter monthly budget: ");
        monthlyBudget = readNonNegative(sc);
               String[] categories = {"Food", "Travel", "Shopping", "Gym"};
        double[] values = new double[categories.length];
        for (int i = 0; i < categories.length; i++) {
            System.out.print("Enter " + categories[i] + " expenses: ");
            values[i] = readNonNegative(sc);
        }

        double total = calculateTotal(values);
        double remaining = monthlyBudget - total;
        System.out.println("\n--- Monthly Expense Report ---");
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("%s: Rs.%.2f%n", categories[i], values[i]);
        }
        System.out.printf("Total Expenses: Rs.%.2f%n", total);
        System.out.printf("Remaining Budget: Rs.%.2f%n", remaining);
        System.out.printf("Remaining Income: Rs.%.2f%n", monthlyIncome - total);
        System.out.println("Budget Status: " + budgetStatus(total));

        if (total < monthlyBudget) {
            System.out.printf("Saved Amount: Rs.%.2f%n", remaining);
        } else if (total > monthlyBudget) {
            System.out.printf("Extra Spending: Rs.%.2f%n", total - monthlyBudget);
        }

        double[][] monthlyExpenses = {
            {values[0], values[1], values[2], values[3]},
            {2500, 1200, 1500, 1000}
        };
        System.out.println("\n--- Two-Month Expense View ---");
        for (int i = 0; i < monthlyExpenses.length; i++) {
            System.out.println("Month " + (i + 1));
            for (int j = 0; j < categories.length; j++) {
                System.out.printf("%s: Rs.%.2f%n", categories[j], monthlyExpenses[i][j]);
            }
        }
System.out.println("\n--- Monthly Budget Tips ---");
        String[] tips = {
            "Reduce unnecessary shopping.",
            "Track your food expenses.",
            "Save money every month."
        };
        for (int i = 0; i < tips.length; i++) {
            System.out.println("Tip " + (i + 1) + ": " + tips[i]);
        }
    }

    static double readNonNegative(Scanner sc) {
        while (true) {
            if (sc.hasNextDouble()) {
                double value = sc.nextDouble();
                if (value >= 0) return value;
            } else {
                sc.next();
            }
            System.out.print("Enter a valid non-negative number: ");
        }
    }

    static double totalExpenses() {
        synchronized (expenses) {
            double total = 0;
            for (Expense e : expenses) total += e.amount;
            return total;
        }
    }
    static void send(HttpExchange exchange, int code, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    static String jsonEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    static String field(String body, String key, String fallback) {
        String marker = "\"" + key + "\"";
        int keyPos = body.indexOf(marker);
        if (keyPos < 0) return fallback;
        int colon = body.indexOf(':', keyPos + marker.length());
        if (colon < 0) return fallback;
        int start = colon + 1;
        while (start < body.length() && Character.isWhitespace(body.charAt(start))) start++;
        if (start >= body.length()) return fallback;
        if (body.charAt(start) == '"') {
            int end = body.indexOf('"', start + 1);
            return end < 0 ? fallback : body.substring(start + 1, end);
        }
        int end = start;
        while (end < body.length() && body.charAt(end) != ',' && body.charAt(end) != '}') end++;
        return body.substring(start, end).trim();
        }

    static void startWebServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/data", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 405, "application/json", "{\"error\":\"Method not allowed\"}");
                return;
            }
            double total = totalExpenses();
            StringBuilder list = new StringBuilder("[");
            synchronized (expenses) {
                for (int i = 0; i < expenses.size(); i++) {
                    Expense e = expenses.get(i);
                    if (i > 0) list.append(',');
                    list.append("{\"id\":").append(e.id)
                        .append(",\"title\":\"").append(jsonEscape(e.title))
                        .append("\",\"category\":\"").append(jsonEscape(e.category))
                        .append("\",\"amount\":").append(e.amount)
                        .append(",\"date\":\"").append(jsonEscape(e.date)).append("\"}");
                }
            }
            list.append(']');
            String json = "{\"income\":" + monthlyIncome + ",\"budget\":" + monthlyBudget
                    + ",\"totalExpenses\":" + total + ",\"remainingBalance\":" + (monthlyIncome - total)
                    + ",\"status\":\"" + budgetStatus(total) + "\",\"expenses\":" + list + "}";
            send(exchange, 200, "application/json; charset=UTF-8", json);
        });
        server.createContext("/api/expense", exchange -> {
            String method = exchange.getRequestMethod();
            if ("POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                String title = field(body, "title", "Expense");
                String category = field(body, "category", "Other");
                String date = field(body, "date", "");
                double amount;
                try {
                    amount = Double.parseDouble(field(body, "amount", "0"));
                } catch (NumberFormatException ex) {
                    amount = 0;
                }
                if (amount <= 0) {
                    send(exchange, 400, "application/json", "{\"success\":false,\"error\":\"Amount must be positive\"}");
                    return;
                }
                 synchronized (expenses) {
                    expenses.add(new Expense(nextId.getAndIncrement(), title, category, amount, date));
                }
                send(exchange, 201, "application/json", "{\"success\":true}");
            } else if ("DELETE".equalsIgnoreCase(method)) {
                String query = exchange.getRequestURI().getQuery();
                int id = -1;
                try {
                    if (query != null && query.startsWith("id=")) id = Integer.parseInt(query.substring(3));
                } catch (NumberFormatException ignored) { }
                boolean removed;
                final int deleteId = id;
                synchronized (expenses) {
                    removed = expenses.removeIf(e -> e.id == deleteId);
                }
                send(exchange, removed ? 200 : 400, "application/json",
                        removed ? "{\"success\":true}" : "{\"success\":false,\"error\":\"Invalid ID\"}");
            } else {
                send(exchange, 405, "application/json", "{\"error\":\"Method not allowed\"}");
            }
        });
server.createContext("/api/income", exchange -> updateNumber(exchange, true));
        server.createContext("/api/budget", exchange -> updateNumber(exchange, false));
        server.createContext("/api/reset", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 405, "application/json", "{\"error\":\"Method not allowed\"}");
                return;
            }
            synchronized (expenses) { expenses.clear(); }
            send(exchange, 200, "application/json", "{\"success\":true}");
        });

        server.createContext("/", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 405, "text/plain; charset=UTF-8", "Method not allowed");
                return;
            }
            String page = "<!doctype html><html><head><meta charset='UTF-8'><title>Expense Monthly</title>"
                    + "<style>body{font-family:Arial;max-width:800px;margin:40px auto;padding:20px;background:#f4f6fb;color:#172033}"
                    + ".card{background:white;padding:20px;margin:12px 0;border-radius:12px;box-shadow:0 2px 8px #ddd}button{padding:8px 14px}</style></head><body>"
                      + "<h1>Monthly Expense &amp; Budget Analyzer</h1><div class='card' id='summary'>Loading...</div>"
                    + "<div class='card'><h2>Add Expense</h2><form id='f'><input name='title' placeholder='Title' required> "
                    + "<input name='category' placeholder='Category' value='Other'> <input name='amount' type='number' min='0.01' step='0.01' placeholder='Amount' required> "
                    + "<input name='date' type='date'> <button>Add</button></form></div><div class='card'><h2>Expenses</h2><ul id='items'></ul></div>"
                    + "<script>async function load(){let r=await fetch('/api/data'),d=await r.json();document.getElementById('summary').innerHTML=`Income: Rs.${d.income}<br>Budget: Rs.${d.budget}<br>Total expenses: Rs.${d.totalExpenses}<br>Remaining income: Rs.${d.remainingBalance}<br>Status: ${d.status}`;"
                    + "document.getElementById('items').innerHTML=d.expenses.map(e=>`<li>${e.title} (${e.category}) - Rs.${e.amount} <button onclick='del(${e.id})'>Delete</button></li>`).join('')||'No expenses yet';}"
                    + "document.getElementById('f').onsubmit=async e=>{e.preventDefault();let f=new FormData(e.target),d=Object.fromEntries(f.entries());d.amount=Number(d.amount);let r=await fetch('/api/expense',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(d)});if(!r.ok)alert('Check the amount');e.target.reset();load();};"
                    + "async function del(id){await fetch('/api/expense?id='+id,{method:'DELETE'});load()}load();</script></body></html>";
            send(exchange, 200, "text/html; charset=UTF-8", page);
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Web server running at http://localhost:8080");
    }

    static void updateNumber(HttpExchange exchange, boolean income) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "application/json", "{\"error\":\"Method not allowed\"}");
            return;
        }
        String body = readBody(exchange);
         double value;
        try {
            value = Double.parseDouble(field(body, "value", "-1"));
        } catch (NumberFormatException ex) {
            value = -1;
        }
        if (value < 0) {
            send(exchange, 400, "application/json", "{\"success\":false,\"error\":\"Invalid value\"}");
            return;
        }
        if (income) monthlyIncome = value; else monthlyBudget = value;
        send(exchange, 200, "application/json", "{\"success\":true}");
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("MONTHLY EXPENSE & BUDGET ANALYZER");
        System.out.println("1. Console mode");
        System.out.println("2. Web server mode");
        System.out.print("Choose mode: ");
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) {
             runConsole(sc);
        } else if (choice.equals("2")) {
            expenses.add(new Expense(nextId.getAndIncrement(), "Grocery Store", "Food", 4500, "2026-10-15"));
            expenses.add(new Expense(nextId.getAndIncrement(), "Metro Card", "Travel", 2800, "2026-10-15"));
            startWebServer();
        } else {
            System.out.println("Invalid choice. Run again and choose 1 or 2.");
        }
        sc.close();
    }
}

