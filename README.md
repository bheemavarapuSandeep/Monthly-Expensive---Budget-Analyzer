# 💰 Monthly Expense & Budget Analyzer

A comprehensive Java application for tracking monthly income, setting budgets, categorizing expenses, and analyzing financial health.

The project comes in **two modes**:
1. 🌐 **Web Application (Dashboard UI)** — Powered by a built-in lightweight Java HTTP server with an interactive web UI.
2. 💻 **Console Application (CLI)** — An interactive terminal-based scanner interface for quick command-line calculations.

---

## 🌐 1. Web Application (Localhost)

### 🔗 Localhost URL
Once running, open your web browser and go to:
**[http://localhost:8080](http://localhost:8080)**

---

### 🚀 How to Run the Web Application

#### Option A: One-Click Batch File (Windows)
Double-click:
```
run_web.bat
```
*(This will automatically compile the server, launch `http://localhost:8080` in your default browser, and start the backend).*

---

#### Option B: Via Terminal / PowerShell
Open your terminal in the project directory (`c:\Users\mudhi\OneDrive\Documents\Desktop\javaproject`) and execute:

```powershell
# Step 1: Compile the server
javac Server.java

# Step 2: Run the server
java Server
```

Once you see:
```
=================================================
  MONTHLY EXPENSE & BUDGET ANALYZER - WEB SERVER
=================================================
 Server started successfully!
 Open your browser and navigate to:
 👉 http://localhost:8080
=================================================
```
Open **[http://localhost:8080](http://localhost:8080)** in Chrome, Edge, or any browser.

To stop the server: Press `Ctrl + C` in the terminal.

---

## 💻 2. Console Application (CLI Mode)

If you want to use the pure terminal/command-line tool:

#### Option A: One-Click Batch File (Windows)
Double-click:
```
run_console.bat
```

---

#### Option B: Via Terminal / PowerShell
```powershell
# Compile and run
javac MonthlyExpenseAnalyzer.java
java MonthlyExpenseAnalyzer
```
*(Or in Java 11+: `java MonthlyExpenseAnalyzer.java`)*

---

## 📁 Project Structure

```
javaproject/
├── Server.java                   # Built-in Java HTTP server & REST API (Port 8080)
├── MonthlyExpenseAnalyzer.java   # Standalone console application
├── public/                       # Web frontend assets
│   ├── index.html                # Responsive web dashboard
│   ├── style.css                 # Modern CSS design system & animations
│   └── app.js                    # Dynamic UI logic, charts, and API interactions
├── run_web.bat                   # 1-click launcher for the Web Dashboard
├── run_console.bat               # 1-click launcher for the CLI Console app
└── README.md                     # Documentation & running instructions
```

---

## ⚙️ Requirements

- **Java Development Kit (JDK)**: Java 11 or newer (Java 17, 21, or 26 recommended).
- **No external dependencies**: Runs entirely using native Java standard libraries (`com.sun.net.httpserver`).
