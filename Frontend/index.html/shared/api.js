const API_BASE_URL = window.TUB_API_BASE_URL || "https://localhost:8443";

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

async function apiGet(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "GET",
        headers: getHeaders()
    });

    if (!response.ok) {
        throw new Error(`Erro GET ${endpoint}`);
    }

    return await response.json();
}

async function apiPost(endpoint, body) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "POST",
        headers: getHeaders(),
        body: body ? JSON.stringify(body) : undefined
    });

    if (!response.ok) {
        throw new Error(`Erro POST ${endpoint}`);
    }

    return await response.json();
}

async function apiPut(endpoint, body) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "PUT",
        headers: getHeaders(),
        body: body ? JSON.stringify(body) : undefined
    });

    if (!response.ok) {
        throw new Error(`Erro PUT ${endpoint}`);
    }

    return await response.json();
}

async function apiDelete(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: "DELETE",
        headers: getHeaders()
    });

    if (!response.ok) {
        throw new Error(`Erro DELETE ${endpoint}`);
    }

    return await response.json();
}
