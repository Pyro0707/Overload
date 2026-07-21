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

// ── Exercise Visual Vector Avatars ────────────────────────────
function getExerciseIconSvg(name = '', muscleGroup = '') {
    const n = (name + ' ' + muscleGroup).toLowerCase();

    if (n.includes('bench') || n.includes('chest') || n.includes('pectoral') || n.includes('fly') || n.includes('dip')) {
        // Chest / Bench Press Icon
        return `<svg class="w-6 h-6 text-[#0d9488] dark:text-[#14b8a6]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="15" width="20" height="3" rx="1.5" fill="currentColor" fill-opacity="0.15"/>
            <path d="M4 18v3M20 18v3M12 15V8"/>
            <path d="M6 8h12M4 6h16" stroke-width="2.2"/>
            <circle cx="12" cy="4.5" r="2" fill="currentColor"/>
        </svg>`;
    } else if (n.includes('squat') || n.includes('leg') || n.includes('quad') || n.includes('hamstring') || n.includes('calf') || n.includes('lunge')) {
        // Legs / Squat Icon
        return `<svg class="w-6 h-6 text-[#16a34a] dark:text-[#22c55e]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="4" r="2" fill="currentColor"/>
            <path d="M6 7h12" stroke-width="2.2"/>
            <path d="M12 6v6l-3 4-1 5M12 12l3 4 1 5"/>
        </svg>`;
    } else if (n.includes('pull') || n.includes('row') || n.includes('lat') || n.includes('back') || n.includes('deadlift')) {
        // Back / Rows / Pull-ups Icon
        return `<svg class="w-6 h-6 text-[#2563eb] dark:text-[#3b82f6]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="4" r="2" fill="currentColor"/>
            <path d="M5 6h14" stroke-width="2.2"/>
            <path d="M7 6v6l5 3 5-3V6"/>
            <path d="M10 20l2-5 2 5"/>
        </svg>`;
    } else if (n.includes('shoulder') || n.includes('overhead') || n.includes('press') || n.includes('deltoid') || n.includes('raise')) {
        // Shoulders / Overhead Press Icon
        return `<svg class="w-6 h-6 text-[#d97706] dark:text-[#f59e0b]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 5h18" stroke-width="2.2"/>
            <circle cx="12" cy="8" r="2" fill="currentColor"/>
            <path d="M7 5v5l5 2 5-2V5"/>
            <path d="M9 16v5M15 16v5"/>
        </svg>`;
    } else if (n.includes('arm') || n.includes('bicep') || n.includes('tricep') || n.includes('curl') || n.includes('extension')) {
        // Arms / Bicep / Tricep Icon
        return `<svg class="w-6 h-6 text-[#9333ea] dark:text-[#a855f7]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 5h12M6 19h12" stroke-width="2"/>
            <path d="M9 5v14M15 5v14"/>
            <circle cx="12" cy="12" r="3" fill="currentColor" fill-opacity="0.2"/>
        </svg>`;
    } else {
        // Core / General Fitness Icon
        return `<svg class="w-6 h-6 text-[#475569] dark:text-[#94a3b8]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2l3 6 7 1-5 5 1 7-6-3-6 3 1-7-5-5 7-1z" fill="currentColor" fill-opacity="0.15"/>
        </svg>`;
    }
}

