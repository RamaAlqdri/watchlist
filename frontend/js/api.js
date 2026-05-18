const API_BASE_URL = 'http://localhost:8080/api';
const TOKEN_KEY = 'movie_watchlist_token';
const USER_KEY = 'movie_watchlist_user';

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function getCurrentUser() {
    const rawUser = localStorage.getItem(USER_KEY);
    return rawUser ? JSON.parse(rawUser) : null;
}

function saveAuth(authResponse) {
    localStorage.setItem(TOKEN_KEY, authResponse.token);
    localStorage.setItem(USER_KEY, JSON.stringify({
        userId: authResponse.userId,
        name: authResponse.name,
        username: authResponse.username
    }));
}

function clearAuth() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
}

function requireAuth() {
    if (!getToken()) {
        window.location.href = 'login.html';
    }
}

async function apiFetch(path, options = {}) {
    const token = getToken();
    const headers = {
        ...(options.headers || {})
    };

    if (!(options.body instanceof FormData)) {
        headers['Content-Type'] = headers['Content-Type'] || 'application/json';
    }

    if (token && !options.skipAuth) {
        headers.Authorization = `Bearer ${token}`;
    }

    const fetchOptions = {
        ...options,
        headers
    };
    delete fetchOptions.skipAuth;

    const response = await fetch(`${API_BASE_URL}${path}`, fetchOptions);
    const contentType = response.headers.get('content-type') || '';
    const hasJson = contentType.includes('application/json');
    const data = response.status === 204 ? null : hasJson ? await response.json() : await response.text();

    if (!response.ok) {
        if (response.status === 401 && !options.skipAuth) {
            clearAuth();
            window.location.href = 'login.html';
            return null;
        }

        const message = data && data.message ? data.message : 'Request gagal diproses';
        throw new Error(message);
    }

    return data;
}

function setMessage(element, message, type = 'error') {
    if (!element) {
        return;
    }

    element.textContent = message || '';
    element.className = message ? `message ${type}` : 'message';
}
