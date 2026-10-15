import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lightweight Built-in Java Web Server for Monthly Expense & Budget Analyzer.
 * Runs on standard Java SE with zero third-party dependencies.
 */
public class Server {

    private static final int PORT = 8080;
    private static final File PUBLIC_DIR = new File("public");

    // In-Memory Data Store
    private static double monthlyIncome = 50000.0;
    private static double monthlyBudget = 30000.0;
    private static final List<Expense> expenses = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    static class Expense {
        int id;
        String title;
        String category;
        double amount;
        String date;

        public Expense(int id, String title, String category, double amount, String date) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.amount = amount;
            this.date = date;
        }
    }

    public static void main(String[] args) throws IOException {
        // Initialize with default sample expenses for rich immediate demo
        initSampleData();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API Contexts
        server.createContext("/api/data", new DataHandler());
        server.createContext("/api/income", new IncomeHandler());
        server.createContext("/api/budget", new BudgetHandler());
        server.createContext("/api/expense", new ExpenseHandler());
        server.createContext("/api/reset", new ResetHandler());

        // Static Files Handler
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // default multithread executor
        server.start();

        System.out.println("=================================================");
        System.out.println("  MONTHLY EXPENSE & BUDGET ANALYZER - WEB SERVER");
        System.out.println("=================================================");
        System.out.println(" Server started successfully!");
        System.out.println(" Open your browser and navigate to:");
        System.out.println(" 👉 http://localhost:" + PORT);
        System.out.println("=================================================");
    }

    private static void initSampleData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        expenses.add(new Expense(idGenerator.getAndIncrement(), "Grocery Store", "Food", 4500.0, today));
        expenses.add(new Expense(idGenerator.getAndIncrement(), "Metro Card & Fuel", "Travel", 2800.0, today));
        expenses.add(new Expense(idGenerator.getAndIncrement(), "Online Course Books", "Education", 3200.0, today));
        expenses.add(new Expense(idGenerator.getAndIncrement(), "Electricity & Wi-Fi", "Bills", 4100.0, today));
        expenses.add(new Expense(idGenerator.getAndIncrement(), "Weekend Movie & Snacks", "Entertainment", 1600.0, today));
        expenses.add(new Expense(idGenerator.getAndIncrement(), "Casual Wear", "Shopping", 3500.0, today));
    }

    // --- Static File Handler ---
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File file = new File(PUBLIC_DIR, path.substring(1));
            if (!file.exists() || file.isDirectory()) {
                file = new File(PUBLIC_DIR, "index.html");
            }

            if (!file.exists()) {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
                return;
            }

            String mimeType = "text/plain";
            String lower = file.getName().toLowerCase();
            if (lower.endsWith(".html")) mimeType = "text/html; charset=UTF-8";
            else if (lower.endsWith(".css")) mimeType = "text/css; charset=UTF-8";
            else if (lower.endsWith(".js")) mimeType = "application/javascript; charset=UTF-8";
            else if (lower.endsWith(".json")) mimeType = "application/json; charset=UTF-8";
            else if (lower.endsWith(".svg")) mimeType = "image/svg+xml";
            else if (lower.endsWith(".png")) mimeType = "image/png";

            exchange.getResponseHeaders().set("Content-Type", mimeType);
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    // --- API: Get All Data ---
    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            synchronized (expenses) {
                double totalExpenses = 0.0;
                Map<String, Double> categoryMap = new LinkedHashMap<>();
                String[] allCategories = {"Food", "Travel", "Education", "Shopping", "Entertainment", "Bills", "Other"};
                for (String c : allCategories) {
                    categoryMap.put(c, 0.0);
                }

                for (Expense e : expenses) {
                    totalExpenses += e.amount;
                    categoryMap.put(e.category, categoryMap.getOrDefault(e.category, 0.0) + e.amount);
                }

                double remainingBalance = monthlyIncome - totalExpenses;

                // Conditional statement for Budget Status
                String budgetStatus;
                String statusLevel; // "success", "warning", "danger"
                if (totalExpenses < monthlyBudget) {
                    budgetStatus = "You are within your budget.";
                    statusLevel = "success";
                } else if (totalExpenses == monthlyBudget) {
                    budgetStatus = "You have reached your budget limit.";
                    statusLevel = "warning";
                } else {
                    budgetStatus = "Warning! You have exceeded your budget.";
                    statusLevel = "danger";
                }

                // Construct JSON response
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"monthlyIncome\":").append(monthlyIncome).append(",");
                json.append("\"monthlyBudget\":").append(monthlyBudget).append(",");
                json.append("\"totalExpenses\":").append(totalExpenses).append(",");
                json.append("\"remainingBalance\":").append(remainingBalance).append(",");
                json.append("\"budgetStatus\":\"").append(escapeJson(budgetStatus)).append("\",");
                json.append("\"statusLevel\":\"").append(statusLevel).append("\",");

                // Categories
                json.append("\"categoryTotals\":{");
                int cCount = 0;
                for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
                    if (cCount > 0) json.append(",");
                    json.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
                    cCount++;
                }
                json.append("},");

                // Expenses list
                json.append("\"expenses\":[");
                for (int i = 0; i < expenses.size(); i++) {
                    Expense e = expenses.get(i);
                    if (i > 0) json.append(",");
                    json.append("{")
                        .append("\"id\":").append(e.id).append(",")
                        .append("\"title\":\"").append(escapeJson(e.title)).append("\",")
                        .append("\"category\":\"").append(escapeJson(e.category)).append("\",")
                        .append("\"amount\":").append(e.amount).append(",")
                        .append("\"date\":\"").append(escapeJson(e.date)).append("\"")
                        .append("}");
                }
                json.append("]");
                json.append("}");

                sendJsonResponse(exchange, 200, json.toString());
            }
        }
    }

    // --- API: Update Income ---
    static class IncomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                if (map.containsKey("amount")) {
                    try {
                        monthlyIncome = Math.max(0, Double.parseDouble(map.get("amount")));
                        sendJsonResponse(exchange, 200, "{\"success\":true,\"monthlyIncome\":" + monthlyIncome + "}");
                        return;
                    } catch (NumberFormatException ignored) {}
                }
            }
            sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Invalid income amount\"}");
        }
    }

    // --- API: Update Budget ---
    static class BudgetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                if (map.containsKey("amount")) {
                    try {
                        monthlyBudget = Math.max(0, Double.parseDouble(map.get("amount")));
                        sendJsonResponse(exchange, 200, "{\"success\":true,\"monthlyBudget\":" + monthlyBudget + "}");
                        return;
                    } catch (NumberFormatException ignored) {}
                }
            }
            sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Invalid budget amount\"}");
        }
    }

    // --- API: Add or Delete Expense ---
    static class ExpenseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> map = parseJsonSimple(body);
                String title = map.getOrDefault("title", "Expense");
                String category = map.getOrDefault("category", "Other");
                String amountStr = map.getOrDefault("amount", "0");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = map.getOrDefault("date", sdf.format(new Date()));

                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Amount must be positive\"}");
                        return;
                    }

                    Expense newExpense;
                    synchronized (expenses) {
                        newExpense = new Expense(idGenerator.getAndIncrement(), title, category, amount, date);
                        expenses.add(0, newExpense); // add to top
                    }
                    sendJsonResponse(exchange, 201, "{\"success\":true,\"id\":" + newExpense.id + "}");
                    return;
                } catch (NumberFormatException ignored) {}
            } else if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("id=")) {
                    try {
                        int id = Integer.parseInt(query.substring(3));
                        synchronized (expenses) {
                            expenses.removeIf(e -> e.id == id);
                        }
                        sendJsonResponse(exchange, 200, "{\"success\":true}");
                        return;
                    } catch (Exception ignored) {}
                }
            }
            sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Bad request\"}");
        }
    }

    // --- API: Reset ---
    static class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            synchronized (expenses) {
                monthlyIncome = 0;
                monthlyBudget = 0;
                expenses.clear();
            }
            sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Data reset\"}");
        }
    }

    // --- Helper Methods ---
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static Map<String, String> parseJsonSimple(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        String clean = json.trim();
        if (clean.startsWith("{")) clean = clean.substring(1);
        if (clean.endsWith("}")) clean = clean.substring(0, clean.length() - 1);

        String[] pairs = clean.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("^\"|\"$", "");
                String value = kv[1].trim().replaceAll("^\"|\"$", "");
                map.put(key, value);
            }
        }
        return map;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
