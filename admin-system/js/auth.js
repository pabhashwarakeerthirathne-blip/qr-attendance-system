async function login(email, password) {
  const { data, error } = await supabaseClient.auth.signInWithPassword({ email, password });
  if (error) throw error;
  return data;
}

async function getProfile() {
  const { data: { user }, error } = await supabaseClient.auth.getUser();
  if (error || !user) throw new Error('Not signed in.');
  const { data: profile, error: pError } = await supabaseClient
    .from('profiles').select('*').eq('id', user.id).single();
  if (pError) throw pError;
  return profile;
}

async function requireRole(roles) {
  const profile = await getProfile();
  if (!roles.includes(profile.role)) {
    alert('You are not authorized to access this system.');
    location.href = 'index.html';
    throw new Error('Unauthorized');
  }
  return profile;
}

async function logout() {
  await supabaseClient.auth.signOut();
  location.href = 'index.html';
}

async function login(email,password){const {data,error}=await supabaseClient.auth.signInWithPassword({email,password});if(error)throw error;return data}
async function getProfile(){const {data:{user},error}=await supabaseClient.auth.getUser();if(error||!user)throw new Error('Not signed in.');const {data,error:pError}=await supabaseClient.from('profiles').select('*').eq('id',user.id).single();if(pError)throw pError;return data}
async function requireRole(roles){const p=await getProfile();if(!roles.includes(p.role)){alert('Unauthorized');location.href='index.html';throw new Error('Unauthorized')}return p}
async function logout(){await supabaseClient.auth.signOut();location.href='index.html'}

async function markAttendance(studentNumber, sessionId) {
    const p = await getProfile();
    
    const { data: sessionData } = await supabaseClient.auth.getSession();
    const token = sessionData.session?.access_token;

    const r = await fetch(`${CONFIG.API_BASE_URL}/attendance/scan`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ studentNumber, sessionId, scannedBy: p.id })
    });
    
    const d = await r.json();
    if (!r.ok) throw new Error(d.message || 'Attendance failed');
    return d;
}
