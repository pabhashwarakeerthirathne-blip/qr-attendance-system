const CONFIG = {
  API_BASE_URL: 'http://localhost:8080/api',
  SUPABASE_URL: 'https://snptltcrdlfzkfxvqgyu.supabase.co',
  SUPABASE_PUBLISHABLE_KEY: 'sb_publishable_62qM3TllSCCh3LPTbZ3aiQ_nH_PyvUF'
};

// Safely initialize once globally on the window object to avoid redeclaration errors
if (!window.supabaseInstance) {
    window.supabaseInstance = window.supabase.createClient(CONFIG.SUPABASE_URL, CONFIG.SUPABASE_PUBLISHABLE_KEY);
}

// Expose both naming conventions globally so all pages work seamlessly
window.supabaseClient = window.supabaseInstance;
window.supabase = window.supabaseInstance;