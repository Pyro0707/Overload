// ── Theme (light/dark) ──────────────────────────────────────
// Loaded blocking in <head> so data-theme is set before first paint (no flash).
// Falls back to the OS preference until the user picks explicitly.
(function () {
    const saved = localStorage.getItem('theme');
    const dark = saved
        ? saved === 'dark'
        : window.matchMedia('(prefers-color-scheme: dark)').matches;
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light');
})();

function toggleTheme() {
    const dark = document.documentElement.getAttribute('data-theme') !== 'dark';
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light');
    localStorage.setItem('theme', dark ? 'dark' : 'light');
    document.querySelectorAll('.theme-toggle-icon').forEach(el => {
        el.textContent = dark ? 'light_mode' : 'dark_mode';
    });
}
