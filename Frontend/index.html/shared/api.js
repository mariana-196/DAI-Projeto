const API_BASE_URL = "http://localhost:8081";

function getHeaders() {
    const headers = {
        "Content-Type": "application/json"
    };
    const token = sessionStorage.getItem("user_token");
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }
    return headers;
}


async function handleResponse(response, endpoint) {
    if (response.status === 401) {
        sessionStorage.clear();
        window.location.href = "login.html";
        throw new Error("Não autorizado (401). Redirecionando...");
    }
    if (response.status === 403) {
        alert("Acesso Negado: Não tem permissão para realizar esta operação.");
        throw new Error("Acesso negado (403).");
    }
    if (!response.ok) {
        throw new Error(`Erro no pedido para ${endpoint}: ${response.statusText}`);
    }
    const text = await response.text();
    if (!text) {
        return {};
    }

    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return JSON.parse(text);
    }

    return text;
}

async function apiGet(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "GET",
        headers: getHeaders()
    });
    return handleResponse(response, endpoint);
}

async function apiPost(endpoint, body) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "POST",
        headers: getHeaders(),
        body: body ? JSON.stringify(body) : undefined
    });
    return handleResponse(response, endpoint);
}

async function apiPut(endpoint, body) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "PUT",
        headers: getHeaders(),
        body: body ? JSON.stringify(body) : undefined
    });
    return handleResponse(response, endpoint);
}

async function apiDelete(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "DELETE",
        headers: getHeaders()
    });
    return handleResponse(response, endpoint);
}

function logout() {
    const token = sessionStorage.getItem("user_token");
    if (token) {
        fetch(`${API_BASE_URL}/api/auth/logout`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            }
        }).finally(() => {
            sessionStorage.clear();
            window.location.href = "login.html";
        });
    } else {
        sessionStorage.clear();
        window.location.href = "login.html";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // 1. Centralize logout
    const logoutBtn = document.getElementById("btn-logout");
    if (logoutBtn) {
        const newLogoutBtn = logoutBtn.cloneNode(true);
        logoutBtn.parentNode.replaceChild(newLogoutBtn, logoutBtn);
        newLogoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }

    // 2. Navigation security & hiding of elements
    const pathName = window.location.pathname.toLowerCase();
    const isPublicPage = pathName.endsWith("login.html") || pathName.endsWith("dms-screen.html");

    if (!isPublicPage) {
        const token = sessionStorage.getItem("user_token");
        if (!token) {
            window.location.href = "login.html";
            return;
        }

        const userRole = sessionStorage.getItem("user_role");
        if (userRole !== "ADMINISTRADOR") {
            // Hide admin navigation links
            const navAuditoria = document.getElementById("nav-auditoria");
            if (navAuditoria) {
                navAuditoria.style.display = "none";
            }
            // If operator tries to directly access auditoria page, redirect
            if (pathName.endsWith("auditoria.html")) {
                window.location.href = "dashboard.html";
            }
        }
    }
});
