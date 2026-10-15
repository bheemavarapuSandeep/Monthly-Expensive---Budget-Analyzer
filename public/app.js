/**
 * Monthly Expense & Budget Analyzer - Frontend Controller
 * Connects with Java Backend HTTP REST API with offline fallback.
 */

// App State
let state = {
    monthlyIncome: 50000,
    monthlyBudget: 30000,
    expenses: [
        { id: 1, title: "Grocery Store", category: "Food", amount: 4500, date: new Date().toISOString().split('T')[0] },
        { id: 2, title: "Metro Card & Fuel", category: "Travel", amount: 2800, date: new Date().toISOString().split('T')[0] },
        { id: 3, title: "Online Course Books", category: "Education", amount: 3200, date: new Date().toISOString().split('T')[0] },
        { id: 4, title: "Electricity & Wi-Fi", category: "Bills", amount: 4100, date: new Date().toISOString().split('T')[0] },
        { id: 5, title: "Weekend Movie & Snacks", category: "Entertainment", amount: 1600, date: new Date().toISOString().split('T')[0] },
        { id: 6, title: "Casual Wear", category: "Shopping", amount: 3500, date: new Date().toISOString().split('T')[0] }
    ],
    isBackendOnline: false
};

const CATEGORY_COLORS = {
    "Food": "#EA580C",
    "Travel": "#0284C7",
    "Education": "#0D9488",
    "Shopping": "#DB2777",
    "Entertainment": "#7C3AED",
    "Bills": "#CA8A04",
    "Other": "#64748B"
};

const CATEGORY_ICONS = {
    "Food": "🍔",
    "Travel": "✈️",
    "Education": "🎓",
    "Shopping": "🛍️",
    "Entertainment": "🎬",
    "Bills": "💡",
    "Other": "📦"
};

// Initialize Application
document.addEventListener('DOMContentLoaded', () => {
    // Set default date to today
    document.getElementById('exp-date').value = new Date().toISOString().split('T')[0];
    
    // Fetch data from Java backend
    fetchDataFromBackend();
});

/**
 * Fetch all financial state from Java backend API
 */
async function fetchDataFromBackend() {
    try {
        const response = await fetch('/api/data', { cache: 'no-store' });
        if (response.ok) {
            const data = await response.json();
            state.monthlyIncome = Number(data.monthlyIncome) || 0;
            state.monthlyBudget = Number(data.monthlyBudget) || 0;
            state.expenses = Array.isArray(data.expenses) ? data.expenses : [];
            state.isBackendOnline = true;
        }
    } catch (err) {
        console.warn("Java Backend not detected, running in high-performance browser mode:", err);
        state.isBackendOnline = false;
    }
    renderUI();
}

/**
 * Core Calculations and UI Rendering
 */
function renderUI() {
    // 1. Calculate Total Expenses using arithmetic addition
    const totalExpenses = state.expenses.reduce((sum, item) => sum + Number(item.amount), 0);

    // 2. Calculate Remaining Balance: Income - Total Expenses
    const remainingBalance = state.monthlyIncome - totalExpenses;

    // 3. Conditional Budget Status Logic
    let budgetStatus = "";
    let statusClass = "status-success";
    let statusIconSvg = `<polyline points="20 6 9 17 4 12"></polyline>`;

    if (totalExpenses < state.monthlyBudget) {
        budgetStatus = "You are within your budget.";
        statusClass = "status-success";
        statusIconSvg = `<polyline points="20 6 9 17 4 12"></polyline>`;
    } else if (totalExpenses === state.monthlyBudget) {
        budgetStatus = "You have reached your budget limit.";
        statusClass = "status-warning";
        statusIconSvg = `<circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line>`;
    } else {
        budgetStatus = "Warning! You have exceeded your budget.";
        statusClass = "status-danger";
        statusIconSvg = `<polygon points="7.86 2 16.14 2 22 7.86 22 16.14 16.14 22 7.86 22 2 16.14 2 7.86 7.86 2"></polygon><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line>`;
    }

    // Update Status Banner
    const banner = document.getElementById('status-banner');
    banner.className = `status-banner ${statusClass}`;
    document.getElementById('budget-status-text').textContent = budgetStatus;
    document.getElementById('status-icon').innerHTML = `<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">${statusIconSvg}</svg>`;

    // Budget percentage meter
    const budgetPct = state.monthlyBudget > 0 ? (totalExpenses / state.monthlyBudget) * 100 : 0;
    const progressFill = document.getElementById('budget-progress-bar');
    progressFill.style.width = `${Math.min(budgetPct, 100)}%`;
    
    if (budgetPct > 100) {
        progressFill.style.backgroundColor = 'var(--color-danger)';
    } else if (budgetPct >= 90) {
        progressFill.style.backgroundColor = 'var(--color-warning)';
    } else {
        progressFill.style.backgroundColor = 'var(--color-success)';
    }
    document.getElementById('budget-percentage-text').textContent = `${budgetPct.toFixed(1)}%`;

    // Update KPI Metric Cards
    document.getElementById('disp-income').textContent = formatCurrency(state.monthlyIncome);
    document.getElementById('disp-budget').textContent = formatCurrency(state.monthlyBudget);
    document.getElementById('disp-expenses').textContent = formatCurrency(totalExpenses);
    document.getElementById('disp-balance').textContent = formatCurrency(remainingBalance);
    document.getElementById('disp-expense-count').textContent = `${state.expenses.length} recorded items`;

    // Balance Card Styling
    const balanceCard = document.querySelector('.balance-card');
    const balanceBadge = document.getElementById('balance-badge');
    if (remainingBalance < 0) {
        balanceBadge.className = 'metric-badge';
        balanceBadge.style.background = 'rgba(225, 29, 72, 0.12)';
        balanceBadge.style.color = '#E11D48';
        balanceBadge.style.border = '1px solid rgba(225, 29, 72, 0.25)';
        balanceBadge.innerHTML = 'Deficit';
    } else {
        balanceBadge.className = 'metric-badge green';
        balanceBadge.style.background = '';
        balanceBadge.style.color = '';
        balanceBadge.style.border = '';
        balanceBadge.innerHTML = `
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
            </svg>
            Surplus`;
    }

    // Render Category Breakdown Bars
    renderCategoryBars(totalExpenses);

    // Render Expense Table
    renderExpenses();
}

/**
 * Render Category Breakdown Progress Bars
 */
function renderCategoryBars(totalExpenses) {
    const container = document.getElementById('category-bars');
    container.innerHTML = '';

    const categories = ["Food", "Travel", "Education", "Shopping", "Entertainment", "Bills", "Other"];
    
    // Group totals
    const categoryTotals = {};
    categories.forEach(c => categoryTotals[c] = 0);
    state.expenses.forEach(e => {
        if (categoryTotals[e.category] !== undefined) {
            categoryTotals[e.category] += Number(e.amount);
        } else {
            categoryTotals["Other"] = (categoryTotals["Other"] || 0) + Number(e.amount);
        }
    });

    categories.forEach(cat => {
        const amt = categoryTotals[cat] || 0;
        const pct = totalExpenses > 0 ? ((amt / totalExpenses) * 100).toFixed(1) : 0;
        const color = CATEGORY_COLORS[cat] || '#9CA3AF';
        const icon = CATEGORY_ICONS[cat] || '📦';

        const item = document.createElement('div');
        item.className = 'cat-bar-item';
        item.innerHTML = `
            <div class="cat-bar-info">
                <span class="cat-bar-name">
                    <span>${icon}</span>
                    <span>${cat}</span>
                </span>
                <span>${formatCurrency(amt)} (${pct}%)</span>
            </div>
            <div class="cat-bar-track">
                <div class="cat-bar-fill" style="width: ${pct}%; background-color: ${color};"></div>
            </div>
        `;
        container.appendChild(item);
    });
}

/**
 * Render Expenses Table with filtering
 */
function renderExpenses() {
    const tbody = document.getElementById('expenses-table-body');
    const emptyState = document.getElementById('empty-state');
    const filterCat = document.getElementById('filter-category').value;

    const filtered = filterCat === 'All' 
        ? state.expenses 
        : state.expenses.filter(e => e.category === filterCat);

    tbody.innerHTML = '';

    if (filtered.length === 0) {
        emptyState.classList.remove('hidden');
    } else {
        emptyState.classList.add('hidden');
        filtered.forEach(item => {
            const tr = document.createElement('tr');
            const catClass = `cat-${item.category.toLowerCase()}`;
            const icon = CATEGORY_ICONS[item.category] || '📦';

            tr.innerHTML = `
                <td style="color: var(--text-secondary); font-size: 0.82rem;">${item.date || 'Today'}</td>
                <td style="font-weight: 600;">${escapeHtml(item.title)}</td>
                <td>
                    <span class="cat-tag ${catClass}">
                        <span>${icon}</span>
                        <span>${item.category}</span>
                    </span>
                </td>
                <td class="text-right expense-amount-cell">- ${formatCurrency(item.amount)}</td>
                <td class="text-center">
                    <button class="btn-delete-row" title="Delete Expense" onclick="handleDeleteExpense(${item.id})">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="3 6 5 6 21 6"></polyline>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        </svg>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

/**
 * Handle Add Expense Form Submit
 */
async function handleAddExpense(e) {
    e.preventDefault();
    const title = document.getElementById('exp-title').value.trim();
    const category = document.getElementById('exp-category').value;
    const amount = parseFloat(document.getElementById('exp-amount').value);
    const date = document.getElementById('exp-date').value || new Date().toISOString().split('T')[0];

    if (!title || isNaN(amount) || amount <= 0) {
        showToast("Please enter a valid description and amount.");
        return;
    }

    if (state.isBackendOnline) {
        try {
            const res = await fetch('/api/expense', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title, category, amount: amount.toString(), date })
            });
            if (res.ok) {
                await fetchDataFromBackend();
                showToast(`Added "${title}" to ${category}`);
                document.getElementById('expense-form').reset();
                document.getElementById('exp-date').value = new Date().toISOString().split('T')[0];
                return;
            }
        } catch (err) {
            console.warn("Backend add failed, saving locally:", err);
        }
    }

    // Fallback local save
    const newId = state.expenses.length > 0 ? Math.max(...state.expenses.map(x => x.id)) + 1 : 1;
    state.expenses.unshift({ id: newId, title, category, amount, date });
    renderUI();
    showToast(`Added "${title}" to ${category}`);
    document.getElementById('expense-form').reset();
    document.getElementById('exp-date').value = new Date().toISOString().split('T')[0];
}

/**
 * Handle Delete Expense
 */
async function handleDeleteExpense(id) {
    if (state.isBackendOnline) {
        try {
            const res = await fetch(`/api/expense?id=${id}`, { method: 'DELETE' });
            if (res.ok) {
                await fetchDataFromBackend();
                showToast("Expense removed.");
                return;
            }
        } catch (err) {
            console.warn("Backend delete failed, removing locally:", err);
        }
    }

    // Local fallback
    state.expenses = state.expenses.filter(e => e.id !== id);
    renderUI();
    showToast("Expense removed.");
}

/**
 * Modal Handling for Income & Budget
 */
function openBudgetModal() {
    document.getElementById('cfg-income').value = state.monthlyIncome;
    document.getElementById('cfg-budget').value = state.monthlyBudget;
    document.getElementById('budget-modal').classList.remove('hidden');
}

function closeBudgetModal() {
    document.getElementById('budget-modal').classList.add('hidden');
}

async function handleUpdateFinances(e) {
    e.preventDefault();
    const income = parseFloat(document.getElementById('cfg-income').value) || 0;
    const budget = parseFloat(document.getElementById('cfg-budget').value) || 0;

    if (state.isBackendOnline) {
        try {
            await fetch('/api/income', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ amount: income.toString() })
            });
            await fetch('/api/budget', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ amount: budget.toString() })
            });
            await fetchDataFromBackend();
            closeBudgetModal();
            showToast("Income and Budget updated!");
            return;
        } catch (err) {
            console.warn("Backend update failed, applying locally:", err);
        }
    }

    state.monthlyIncome = income;
    state.monthlyBudget = budget;
    closeBudgetModal();
    renderUI();
    showToast("Income and Budget updated!");
}

/**
 * Reset All Data
 */
async function resetAllData() {
    if (!confirm("Are you sure you want to reset all income, budget, and expense records?")) {
        return;
    }

    if (state.isBackendOnline) {
        try {
            await fetch('/api/reset', { method: 'POST' });
            await fetchDataFromBackend();
            showToast("All data has been reset.");
            return;
        } catch (err) {
            console.warn("Backend reset failed:", err);
        }
    }

    state.monthlyIncome = 0;
    state.monthlyBudget = 0;
    state.expenses = [];
    renderUI();
    showToast("All data has been reset.");
}

/**
 * Helpers
 */
function formatCurrency(num) {
    return '₹' + Number(num || 0).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function escapeHtml(text) {
    const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
    return (text || '').replace(/[&<>"']/g, m => map[m]);
}

function showToast(msg) {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.innerHTML = `
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--color-primary)" stroke-width="2.5">
            <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
        <span>${escapeHtml(msg)}</span>
    `;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(15px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
