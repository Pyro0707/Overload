// ── Navigation Component ─────────────────────────────────────
// Injects the responsive sidebar on desktop & top bar on mobile
// across all authenticated pages.

document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn()) return;

    const currentPage = location.pathname.split('/').pop() || 'dashboard.html';
    const themeIcon = document.documentElement.getAttribute('data-theme') === 'dark'
        ? 'light_mode' : 'dark_mode';

    const navContainer = document.createElement('div');
    navContainer.className = 'nav-wrapper';
    navContainer.innerHTML = `
        <!-- TopNavBar (Mobile Only) -->
        <header class="md:hidden flex justify-between items-center h-20 px-5 bg-[#f9f9f9]/90 backdrop-blur-md sticky top-0 z-50 border-b border-[#eeeeee]">
            <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-full bg-black flex items-center justify-center text-white">
                    <span class="material-symbols-outlined text-[18px]" style="font-variation-settings: 'FILL' 1;">fitness_center</span>
                </div>
                <span class="font-['Plus_Jakarta_Sans'] text-[22px] font-bold tracking-tight text-black">Overload</span>
            </div>
            <div class="flex items-center gap-3">
                <a href="/workout.html" class="bg-black text-white px-3 py-1.5 rounded-full text-xs font-semibold flex items-center gap-1 shadow-sm active:scale-[0.97] transition-transform duration-160">
                    <span class="material-symbols-outlined text-[14px]">bolt</span> Workout
                </a>
                <button onclick="toggleTheme()" class="p-2 text-[#5d5f5d] hover:text-black active:scale-[0.94] transition-colors" title="Toggle dark mode" aria-label="Toggle dark mode">
                    <span class="material-symbols-outlined theme-toggle-icon">${themeIcon}</span>
                </button>
                <button onclick="logout()" class="p-2 text-[#5d5f5d] hover:text-black active:scale-[0.94] transition-colors" title="Logout">
                    <span class="material-symbols-outlined">logout</span>
                </button>
            </div>
        </header>

        <!-- SideNavBar (Desktop) -->
        <nav class="hidden md:flex h-screen w-64 fixed left-0 top-0 bg-[#f9f9f9] flex-col gap-4 p-10 border-r border-[#eeeeee] z-40">
            <!-- Brand Logo -->
            <div class="mb-8 flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-black flex items-center justify-center text-white">
                    <span class="material-symbols-outlined text-[20px]" style="font-variation-settings: 'FILL' 1;">fitness_center</span>
                </div>
                <div>
                    <h1 class="font-['Plus_Jakarta_Sans'] text-[20px] font-extrabold text-black leading-tight tracking-tight">Overload</h1>
                    <p class="font-['Hanken_Grotesk'] text-[12px] font-medium text-[#5d5f5d]">Premium Performance</p>
                </div>
            </div>

            <!-- Navigation Links -->
            <ul class="flex flex-col gap-2 flex-grow">
                <li>
                    <a href="/dashboard.html" class="flex items-center gap-4 px-4 py-3 rounded-full transition-colors duration-160 font-['Hanken_Grotesk'] text-[14px] font-semibold active:scale-[0.98] ${currentPage === 'dashboard.html' ? 'bg-black text-white shadow-sm' : 'text-[#5d5f5d] hover:text-black hover:bg-[#eeeeee]'}">
                        <span class="material-symbols-outlined ${currentPage === 'dashboard.html' ? '' : 'text-[#5d5f5d]'}">dashboard</span>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li>
                    <a href="/routines.html" class="flex items-center gap-4 px-4 py-3 rounded-full transition-colors duration-160 font-['Hanken_Grotesk'] text-[14px] font-semibold active:scale-[0.98] ${currentPage === 'routines.html' ? 'bg-black text-white shadow-sm' : 'text-[#5d5f5d] hover:text-black hover:bg-[#eeeeee]'}">
                        <span class="material-symbols-outlined ${currentPage === 'routines.html' ? '' : 'text-[#5d5f5d]'}">fitness_center</span>
                        <span>Routines</span>
                    </a>
                </li>
                <li>
                    <a href="/workout.html" class="flex items-center gap-4 px-4 py-3 rounded-full transition-colors duration-160 font-['Hanken_Grotesk'] text-[14px] font-semibold active:scale-[0.98] ${currentPage === 'workout.html' ? 'bg-black text-white shadow-sm' : 'text-[#5d5f5d] hover:text-black hover:bg-[#eeeeee]'}">
                        <span class="material-symbols-outlined ${currentPage === 'workout.html' ? '' : 'text-[#5d5f5d]'}" style="font-variation-settings: 'FILL' 1;">edit_note</span>
                        <span>Log Workout</span>
                    </a>
                </li>
                <li>
                    <a href="/history.html" class="flex items-center gap-4 px-4 py-3 rounded-full transition-colors duration-160 font-['Hanken_Grotesk'] text-[14px] font-semibold active:scale-[0.98] ${currentPage === 'history.html' ? 'bg-black text-white shadow-sm' : 'text-[#5d5f5d] hover:text-black hover:bg-[#eeeeee]'}">
                        <span class="material-symbols-outlined ${currentPage === 'history.html' ? '' : 'text-[#5d5f5d]'}">history</span>
                        <span>History</span>
                    </a>
                </li>
                <li>
                    <a href="/progress.html" class="flex items-center gap-4 px-4 py-3 rounded-full transition-colors duration-160 font-['Hanken_Grotesk'] text-[14px] font-semibold active:scale-[0.98] ${currentPage === 'progress.html' ? 'bg-black text-white shadow-sm' : 'text-[#5d5f5d] hover:text-black hover:bg-[#eeeeee]'}">
                        <span class="material-symbols-outlined ${currentPage === 'progress.html' ? '' : 'text-[#5d5f5d]'}">leaderboard</span>
                        <span>Progress</span>
                    </a>
                </li>
            </ul>

            <!-- Bottom User & Start Workout CTA -->
            <div class="mt-auto flex flex-col gap-3 pt-4 border-t border-[#eeeeee]">
                <div class="flex items-center justify-between px-2 text-[#5d5f5d]">
                    <div class="flex items-center gap-2 overflow-hidden">
                        <span class="material-symbols-outlined text-[22px]">account_circle</span>
                        <span class="font-['Hanken_Grotesk'] text-[14px] font-medium truncate text-black">${getUsername()}</span>
                    </div>
                    <div class="flex items-center gap-1">
                        <button onclick="toggleTheme()" class="text-xs text-[#848484] hover:text-black font-semibold transition-colors flex items-center gap-1 active:scale-[0.94]" title="Toggle dark mode" aria-label="Toggle dark mode">
                            <span class="material-symbols-outlined text-[16px] theme-toggle-icon">${themeIcon}</span>
                        </button>
                        <button onclick="logout()" class="text-xs text-[#848484] hover:text-[#dc2626] font-semibold transition-colors flex items-center gap-1 active:scale-[0.94]" title="Logout">
                            <span class="material-symbols-outlined text-[16px]">logout</span>
                        </button>
                    </div>
                </div>
                <a href="/workout.html" class="w-full bg-black text-white rounded-full py-3.5 font-['Hanken_Grotesk'] text-[14px] font-semibold flex items-center justify-center gap-2 hover:opacity-90 active:scale-[0.97] transition-transform duration-160 shadow-sm">
                    <span class="material-symbols-outlined text-[18px]">play_arrow</span>
                    <span>Start Workout</span>
                </a>
            </div>
        </nav>
    `;

    document.body.prepend(navContainer);
});
