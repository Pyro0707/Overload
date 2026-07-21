// ── Centralized API Fetch Wrapper ────────────────────────────
// Attaches JWT from localStorage to all requests.
// Redirects to login on 401. Throws on errors.

const API = {
    async fetch(path, options = {}) {
        const token = localStorage.getItem('token');
        const headers = { 'Content-Type': 'application/json', ...options.headers };
        if (token) headers['Authorization'] = `Bearer ${token}`;

        const res = await fetch(path, { cache: 'no-store', ...options, headers });

        if (res.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            window.location.href = '/index.html';
            throw new Error('Unauthorized');
        }

        if (!res.ok) {
            const bodyText = await res.text().catch(() => '');
            let body = {};
            try { body = bodyText ? JSON.parse(bodyText) : {}; } catch (e) {}
            throw new Error(body.message || `Request failed (${res.status})`);
        }

        if (res.status === 204) return null;
        const text = await res.text().catch(() => '');
        if (!text || !text.trim()) return null;
        try {
            return JSON.parse(text);
        } catch (e) {
            return text;
        }
    },

    get:    (path)       => API.fetch(path),
    post:   (path, body) => API.fetch(path, { method: 'POST',   body: JSON.stringify(body) }),
    put:    (path, body) => API.fetch(path, { method: 'PUT',    body: JSON.stringify(body) }),
    patch:  (path, body) => API.fetch(path, { method: 'PATCH',  body: JSON.stringify(body) }),
    delete: (path)       => API.fetch(path, { method: 'DELETE' }),
};

// ── Auth helpers ────────────────────────────────────────────
function isLoggedIn() { return !!localStorage.getItem('token'); }
function getUsername() { return localStorage.getItem('username') || ''; }
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = '/index.html';
}
function requireAuth() {
    if (!isLoggedIn()) { window.location.href = '/index.html'; }
}

// ── Toast system ────────────────────────────────────────────
function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const iconMap = {
        info: 'info',
        success: 'check_circle',
        error: 'error',
        pr: 'star'
    };
    const iconColorMap = {
        info: 'text-black',
        success: 'text-[#16a34a]',
        error: 'text-[#dc2626]',
        pr: 'text-[#eab308]'
    };
    const iconName = iconMap[type] || 'info';
    const iconClass = iconColorMap[type] || 'text-black';

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <span class="material-symbols-outlined ${iconClass} text-[20px]" ${type === 'pr' ? 'style="font-variation-settings: \'FILL\' 1;"' : ''}>${iconName}</span>
        <span class="flex-1">${message}</span>
    `;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3500);
}
