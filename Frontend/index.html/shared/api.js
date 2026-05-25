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

async function handleResponse(response, method, endpoint) {
    if (response.status === 401) {
        sessionStorage.clear();
        window.location.href = "login.html";
        throw new Error("Sessão expirada ou inválida. A redirecionar para o login...");
    }

    if (response.status === 403) {
        alert("Acesso Negado: O seu perfil de acesso não tem permissão para realizar esta operação.");
        throw new Error("Acesso negado (403 Forbidden)");
    }

    if (!response.ok) {
        throw new Error(`Erro ${method} ${endpoint}`);
    }

    return await response.json();
}

async function apiGet(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "GET",
        headers: getHeaders()
    });
    return await handleResponse(response, "GET", endpoint);
}

async function apiPost(endpoint, body) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "POST",
        headers: getHeaders(),
        body: body ? JSON.stringify(body) : undefined
    });
    return await handleResponse(response, "POST", endpoint);
}

async function apiPut(endpoint, body) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "PUT",
        headers: getHeaders(),
        body: body ? JSON.stringify(body) : undefined
    });
    return await handleResponse(response, "PUT", endpoint);
}

async function apiDelete(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "DELETE",
        headers: getHeaders()
    });
    return await handleResponse(response, "DELETE", endpoint);
}

// Global logout handler to intercept #btn-logout click across all pages
document.addEventListener("DOMContentLoaded", () => {
    const logoutBtn = document.getElementById("btn-logout");
    if (logoutBtn) {
        // Clone the button to remove page-specific listeners, ensuring our unified, authenticated logout is called
        const newLogoutBtn = logoutBtn.cloneNode(true);
        logoutBtn.parentNode.replaceChild(newLogoutBtn, logoutBtn);
        newLogoutBtn.addEventListener("click", async () => {
            const token = sessionStorage.getItem("user_token");
            if (token) {
                try {
                    await fetch(`${API_BASE_URL}/api/auth/logout`, {
                        method: "POST",
                        headers: {
                            "Authorization": "Bearer " + token,
                            "Content-Type": "application/json"
                        },
                        keepalive: true
                    });
                } catch (e) {
                    console.error("Erro ao comunicar logout ao backend:", e);
                }
            }
            sessionStorage.clear();
            window.location.href = "login.html";
        });
    }
});