const API_BASE = "http://localhost:8181";
let token = localStorage.getItem("jwtToken");

// Auto-login if token exists
if (token) showDashboard();

// ─── Auth ───────────────────────────────────────────────────────────────────

async function register() {
    const name     = document.getElementById("name").value.trim();
    const email    = document.getElementById("email").value.trim();
    const mobile   = document.getElementById("mobile").value.trim();
    const password = document.getElementById("password").value;

    if (!email || !password) {
        showMsg("auth-msg", "Email and password are required.");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, mobile, password })
        });
        const text = await res.text();
        showMsg("auth-msg", text);
    } catch (err) {
        showMsg("auth-msg", "Connection error. Is the server running?");
    }
}

async function login() {
    const email    = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    if (!email || !password) {
        showMsg("auth-msg", "Email and password are required.");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (res.ok) {
            const data = await res.json();
            token = data.token;
            localStorage.setItem("jwtToken", token);
            showDashboard();
        } else {
            const msg = await res.text();
            showMsg("auth-msg", msg || "Login failed. Check your credentials.");
        }
    } catch (err) {
        showMsg("auth-msg", "Connection error. Is the server running?");
    }
}

function logout() {
    token = null;
    localStorage.removeItem("jwtToken");
    document.getElementById("auth-section").style.display = "block";
    document.getElementById("dashboard-section").style.display = "none";
    showMsg("auth-msg", "Logged out successfully.");
}

function showDashboard() {
    document.getElementById("auth-section").style.display = "none";
    document.getElementById("dashboard-section").style.display = "block";
    checkBalance();
}

// ─── Auth Helper ─────────────────────────────────────────────────────────────

async function fetchWithAuth(url, options = {}) {
    options.headers = {
        ...options.headers,
        "Authorization": `Bearer ${token}`
    };
    const response = await fetch(url, options);
    if (response.status === 401 || response.status === 403) {
        logout();
        throw new Error("Session expired. Please login again.");
    }
    return response;
}

// ─── Wallet ──────────────────────────────────────────────────────────────────

async function checkBalance() {
    try {
        const res = await fetchWithAuth(`${API_BASE}/wallet/balance`);
        const balance = await res.json();
        document.getElementById("balance").innerText = parseFloat(balance).toFixed(2);
    } catch (err) {
        console.error("Balance fetch error:", err.message);
    }
}

async function addMoney() {
    const amount = document.getElementById("addAmount").value;
    if (!amount || parseFloat(amount) <= 0) {
        showMsg("add-msg", "Please enter a valid amount.");
        return;
    }
    try {
        const res = await fetchWithAuth(`${API_BASE}/wallet/add?amount=${amount}`, { method: "POST" });
        const text = await res.text();
        showMsg("add-msg", text);
        checkBalance();
        document.getElementById("addAmount").value = "";
    } catch (err) {
        showMsg("add-msg", err.message);
    }
}

// ─── Transactions ─────────────────────────────────────────────────────────────

async function transferMoney() {
    const receiver = document.getElementById("receiverEmail").value.trim();
    const amount   = document.getElementById("transferAmount").value;

    if (!receiver || !amount || parseFloat(amount) <= 0) {
        showMsg("transfer-msg", "Please fill in all fields with valid values.");
        return;
    }

    try {
        const res = await fetchWithAuth(
            `${API_BASE}/transaction/transfer?receiverEmail=${encodeURIComponent(receiver)}&amount=${amount}`,
            { method: "POST" }
        );
        const text = await res.text();
        showMsg("transfer-msg", text);
        checkBalance();
        document.getElementById("receiverEmail").value = "";
        document.getElementById("transferAmount").value = "";
    } catch (err) {
        showMsg("transfer-msg", err.message);
    }
}

async function loadHistory() {
    try {
        const res  = await fetchWithAuth(`${API_BASE}/transaction/history`);
        const data = await res.json();
        const list = document.getElementById("history-list");
        list.innerHTML = "";

        if (!data || data.length === 0) {
            list.innerHTML = "<li style='border-left-color:#aaa'>No transactions yet.</li>";
            return;
        }

        // Sort newest first
        data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

        data.forEach(tx => {
            // FIX: Properly access nested email — was broken as a markdown link in original
            const counterparty = tx.receiver ? tx.receiver.email : "Self";
            const date         = new Date(tx.timestamp).toLocaleString();
            const li           = document.createElement("li");
            li.textContent     = `${tx.type} | ₹${tx.amount.toFixed(2)} | To/From: ${counterparty} | ${date}`;
            list.appendChild(li);
        });
    } catch (err) {
        console.error("History error:", err.message);
    }
}

// ─── AI ───────────────────────────────────────────────────────────────────────

async function askAi() {
    const prompt = document.getElementById("aiPrompt").value.trim();
    if (!prompt) {
        showMsg("ai-response", "Please enter a question.");
        return;
    }

    showMsg("ai-response", "🤔 Thinking...");

    try {
        const res = await fetchWithAuth(`${API_BASE}/ai/advice?prompt=${encodeURIComponent(prompt)}`);
        const text = await res.text();
        showMsg("ai-response", text);
    } catch (err) {
        showMsg("ai-response", "AI is currently unavailable.");
    }
}

// ─── Utility ──────────────────────────────────────────────────────────────────

function showMsg(elementId, message) {
    const el = document.getElementById(elementId);
    if (el) el.innerText = message;
}