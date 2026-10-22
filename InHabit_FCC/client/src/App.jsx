import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Activity, AlertTriangle, ArrowRight, ArrowUp, ArrowUpRight,
  Building2, CalendarDays, Check, CheckCircle2, ChevronDown,
  ClipboardList, Clock3, Droplets, ExternalLink, FileImage, Filter, HeartHandshake,
  Home, Leaf, Lightbulb, LockKeyhole, LogOut, Mail, MapPin, Menu,
  MessageSquare, Moon, PackageSearch, Phone, Plus, RefreshCw, Search, ShieldCheck,
  Sparkles, Sun, TrendingUp, UploadCloud, UserRound, Users, Wifi, Wrench, X
} from 'lucide-react';
import { API_BASE, api, assetUrl } from './api.js';

const STATUSES = ['Open', 'Under Review', 'In Progress', 'Resolved', 'Closed'];
const CATEGORIES = ['Internet', 'Plumbing', 'Electrical', 'Cleanliness', 'Mess', 'Security', 'Water', 'Other'];

// Official Evaluation Accounts matching InHabit_Approved (InHabitDBNew.sql)
const DEMO_ACCOUNTS = [
  { label: 'Raghav', role: 'Student', prn: '257122235', email: 'raghav.sarda.btech25@college.edu.in', password: 'student123', detail: 'Alpha-101 (CSE-C)' },
  { label: 'Aniruddha', role: 'Student', prn: '257122247', email: 'aniruddha.singh.btech25@college.edu.in', password: 'student123', detail: 'Alpha-102 (CSE-C)' },
  { label: 'Chief Warden', role: 'Chief Warden', email: 'admin@college.edu.in', password: 'admin123', detail: 'Arvind Kulkarni · STF101' }
];

const categoryIcon = (category) => {
  if (category === 'Internet') return Wifi;
  if (category === 'Water' || category === 'Plumbing') return Droplets;
  if (category === 'Electricity' || category === 'Electrical') return Lightbulb;
  if (category === 'Security') return ShieldCheck;
  return MessageSquare;
};

function dateLabel(value) {
  if (!value) return 'Just now';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return 'Just now';
  return new Intl.DateTimeFormat('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }).format(date);
}

function relativeLabel(value) {
  if (!value) return 'Just now';
  const date = new Date(value);
  const diff = Math.max(0, Date.now() - date.getTime());
  const minutes = Math.floor(diff / 60000);
  if (minutes < 1) return 'Just now';
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
}

function initials(name = 'InHabit') {
  return name.split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]).join('').toUpperCase();
}

function StatusPill({ status }) {
  const className = String(status || 'Open').toLowerCase().replace(/\s+/g, '-');
  return <span className={`status-pill status-${className}`}><span className="status-dot" />{status || 'Open'}</span>;
}

function Toast({ toast, onClose }) {
  useEffect(() => {
    if (!toast) return undefined;
    const timer = window.setTimeout(onClose, 3800);
    return () => window.clearTimeout(timer);
  }, [toast, onClose]);
  if (!toast) return null;
  return <div className={`toast toast-${toast.type || 'success'}`} role="status">
    {toast.type === 'error' ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
    <span>{toast.message}</span>
    <button className="icon-button toast-close" onClick={onClose} aria-label="Dismiss notification"><X size={16} /></button>
  </div>;
}

function AuthScreen({ onLogin, onRegister, loading, error, theme, onToggleTheme }) {
  const [isRegister, setIsRegister] = useState(false);
  const [values, setValues] = useState({ name: '', email: '', password: '', prn: '', block: 'Alpha', room: '', wing: 'A' });
  const [showPassword, setShowPassword] = useState(false);
  const [selectedDemo, setSelectedDemo] = useState('Raghav');
  const set = (key, value) => setValues((old) => ({ ...old, [key]: value }));

  const handleSubmit = (event) => {
    event.preventDefault();
    if (isRegister) onRegister(values);
    else onLogin({ email: values.email, password: values.password });
  };

  const fillDemo = (demo) => {
    setSelectedDemo(demo.label);
    setValues((old) => ({ ...old, email: demo.prn || demo.email, password: demo.password }));
  };

  return <main className="auth-page">
    <section className="auth-visual">
      <div className="auth-visual-grain" />
      <div className="auth-brand">
        <span className="brand-mark"><Leaf size={24} /></span>
        <span>InHabit<span className="brand-dot">.</span></span>
        <span className="brand-mini">SYMBIOSIS INSTITUTE OF TECHNOLOGY · PUNE</span>
      </div>
      <div className="auth-story">
        <div className="eyebrow eyebrow-light"><span className="eyebrow-dot" /> SIT HOSTEL GRIEVANCE REDRESSAL SYSTEM</div>
        <h1>Hostel Grievance &<br />Maintenance <em>Portal</em></h1>
        <p>A unified collegiate platform for tracking hostel complaints, community peer support, room occupancy records, and campus lost & found recovery.</p>
        <div className="auth-feature-list">
          <div><span className="feature-icon"><ClipboardList size={20} /></span><span><strong>Role-Segregated Student & Admin Portals</strong><small>Students manage room grievances; wardens oversee facility operations.</small></span></div>
          <div><span className="feature-icon"><HeartHandshake size={20} /></span><span><strong>Democratic Community Prioritization</strong><small>Campus issues backed by 30+ students are auto-flagged as urgent concerns.</small></span></div>
          <div><span className="feature-icon"><ShieldCheck size={20} /></span><span><strong>Verified Resident Allocation</strong><small>Direct parity with InHabit Java/DBMS approved database schema.</small></span></div>
        </div>
      </div>
      <div className="auth-visual-footer">
        <span>Raghav Mahesh Sarda (257122235) & Aniruddha Pratap Singh (257122247)</span>
        <span>COURSE F0002 · FULL STACK DEVELOPMENT</span>
      </div>
    </section>

    <section className="auth-panel">
      <div className="auth-panel-top">
        <span className="mobile-brand"><Leaf size={22} /> InHabit.</span>
        <div className="top-right-tools">
          <button className="icon-button theme-toggle-btn" type="button" onClick={onToggleTheme} title={theme === 'dark' ? 'Switch to Light Mode' : 'Switch to Dark Mode'}>
            {theme === 'dark' ? <Sun size={19} /> : <Moon size={19} />}
          </button>
          <span className="secure-label"><LockKeyhole size={14} /> SECURE CAMPUS ACCESS</span>
        </div>
      </div>

      <div className="auth-form-wrap">
        <div className="auth-welcome">
          <span className="eyebrow">AUTHENTICATION DESK</span>
          <h2>{isRegister ? 'Resident Registration' : 'Sign In to InHabit'}<span>.</span></h2>
          <p>{isRegister ? 'Create your hostel resident profile to start lodging grievances.' : 'Sign in using your College Email or PRN.'}</p>
        </div>

        {error && <div className="inline-error"><AlertTriangle size={18} />{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          {isRegister && (
            <label className="field"><span>Full Name</span><div className="field-input"><UserRound size={18} /><input value={values.name} onChange={(e) => set('name', e.target.value)} placeholder="Full Student Name" required minLength={2} autoComplete="name" /></div></label>
          )}
          <label className="field"><span>College Email or PRN</span><div className="field-input"><Mail size={18} /><input value={values.email} onChange={(e) => set('email', e.target.value)} placeholder="PRN (e.g. 257122235) or college email" required autoComplete="username" /></div></label>
          <label className="field"><span>Password</span><div className="field-input"><LockKeyhole size={18} /><input type={showPassword ? 'text' : 'password'} value={values.password} onChange={(e) => set('password', e.target.value)} placeholder="Enter account password" required minLength={isRegister ? 8 : 1} autoComplete={isRegister ? 'new-password' : 'current-password'} /><button className="reveal-password" type="button" onClick={() => setShowPassword((old) => !old)}>{showPassword ? 'Hide' : 'Show'}</button></div></label>

          {isRegister && (
            <div className="register-extra">
              <label className="field"><span>PRN</span><input className="plain-input" value={values.prn} onChange={(e) => set('prn', e.target.value)} placeholder="Student PRN" required /></label>
              <label className="field"><span>Block</span><select className="plain-input" value={values.block} onChange={(e) => set('block', e.target.value)}><option>Alpha</option><option>Beta</option><option>Charlie</option><option>Delta</option></select></label>
              <label className="field"><span>Room</span><input className="plain-input" value={values.room} onChange={(e) => set('room', e.target.value)} placeholder="e.g. 101" required /></label>
              <label className="field"><span>Wing</span><input className="plain-input" value={values.wing} onChange={(e) => set('wing', e.target.value)} placeholder="e.g. A" required /></label>
            </div>
          )}

          <button className="primary-button auth-submit" type="submit" disabled={loading}>
            {loading ? <span className="spinner" /> : <>{isRegister ? 'Complete Registration' : 'Sign in to Portal'} <span className="btn-icon-pill"><ArrowRight size={16} /></span></>}
          </button>
        </form>

        <div className="auth-switch">
          {isRegister ? 'Already registered?' : 'New resident on campus?'} <button onClick={() => setIsRegister((old) => !old)}>{isRegister ? 'Sign in' : 'Create resident account'}</button>
        </div>

        {!isRegister && (
          <div className="demo-access">
            <div className="demo-heading"><span>1-CLICK EVALUATION LOGINS</span><span className="demo-line" /></div>
            <div className="demo-accounts-grid">
              {DEMO_ACCOUNTS.map((demo) => (
                <button key={demo.label} className={`demo-account ${selectedDemo === demo.label ? 'demo-active' : ''}`} type="button" onClick={() => fillDemo(demo)}>
                  <span className={`demo-avatar demo-${demo.label.toLowerCase().replace(/\s+/g, '-')}`}>
                    {demo.role === 'Student' ? <UserRound size={18} /> : demo.role === 'Chief Warden' ? <ShieldCheck size={18} /> : <Wrench size={18} />}
                  </span>
                  <span className="demo-text">
                    <strong>{demo.label}</strong>
                    <small>{demo.detail}</small>
                  </span>
                  <ArrowUpRight size={16} className="demo-arrow" />
                </button>
              ))}
            </div>
          </div>
        )}
      </div>

      <div className="auth-panel-footer">
        <span>Symbiosis Institute of Technology (SIT), Pune</span>
        <span><ShieldCheck size={14} /> InHabit Campus Hostel Management</span>
      </div>
    </section>
  </main>;
}

function Sidebar({ user, view, setView, onLogout, mobileOpen, setMobileOpen, theme, onToggleTheme }) {
  // Strict Role Segregation matching Java Mini Project Dashboards:
  // - Students see ONLY student tabs: My Profile, Campus Feed, File Grievance, My Grievances, Campus Notices & Lost-Found
  // - Admin sees ONLY admin tabs: Analytics & Insights, All Grievances, Resident Directory, Staff Directory, Campus Notices & Lost-Found
  // - Technician sees ONLY technician tabs: Assigned Work Orders, Campus Notices & Lost-Found
  const links = useMemo(() => {
    if (user.role === 'student') {
      return [
        { id: 'profile', label: 'My Profile', icon: UserRound },
        { id: 'grievances', label: 'Campus Feed', icon: ClipboardList, badge: 'Live' },
        { id: 'file-grievance', label: 'File Grievance', icon: Plus },
        { id: 'my-grievances', label: 'My Grievances', icon: FileImage },
        { id: 'lostfound', label: 'Campus Notices & Lost-Found', icon: PackageSearch }
      ];
    }
    if (user.role === 'admin') {
      return [
        { id: 'overview', label: 'Analytics & Insights', icon: Activity },
        { id: 'grievances', label: 'All Grievances', icon: ClipboardList, badge: 'Desk' },
        { id: 'directory', label: 'Resident Directory', icon: Users },
        { id: 'staff', label: 'Staff & Technicians', icon: ShieldCheck },
        { id: 'lostfound', label: 'Campus Notices & Lost-Found', icon: PackageSearch }
      ];
    }
    // Technician
    return [
      { id: 'operations', label: 'Assigned Work Orders', icon: Wrench, badge: 'Active' },
      { id: 'lostfound', label: 'Campus Notices & Lost-Found', icon: PackageSearch }
    ];
  }, [user.role]);

  return <>
    {mobileOpen && <button className="sidebar-scrim" onClick={() => setMobileOpen(false)} aria-label="Close navigation" />}
    <aside className={`sidebar ${mobileOpen ? 'sidebar-open' : ''}`}>
      <div className="sidebar-brand">
        <span className="brand-mark"><Leaf size={22} /></span>
        <span>InHabit<span className="brand-dot">.</span></span>
        <button className="icon-button sidebar-close" onClick={() => setMobileOpen(false)} aria-label="Close navigation"><X size={20} /></button>
      </div>

      <div className="workspace-switch">
        <span className="workspace-symbol"><Building2 size={19} /></span>
        <span>
          <strong>{user.role === 'admin' ? 'Chief Warden Desk' : user.role === 'technician' ? 'Maintenance Desk' : 'Student Resident'}</strong>
          <small>{user.role === 'student' ? (user.profile?.room ? `Room ${user.profile.room} (${user.profile.block || 'Alpha'})` : 'Student Resident') : (user.trade ? `${user.trade} Dept` : 'Administration')}</small>
        </span>
      </div>

      <div className="nav-label">PORTAL NAVIGATION</div>
      <nav className="main-nav">
        {links.map((link) => {
          const Icon = link.icon;
          return <button className={`nav-link ${view === link.id ? 'nav-active' : ''}`} key={link.id} onClick={() => { setView(link.id); setMobileOpen(false); }}>
            <Icon size={19} strokeWidth={1.8} />
            <span>{link.label}</span>
            {link.badge && <span className="nav-live">{link.badge}</span>}
            {view === link.id && <span className="nav-indicator" />}
          </button>;
        })}
      </nav>

      <div className="sidebar-bottom">
        <div className="theme-toggle-card">
          <div className="theme-toggle-info">
            <span>{theme === 'dark' ? <Moon size={16} /> : <Sun size={16} />}</span>
            <strong>{theme === 'dark' ? 'Dark Theme' : 'Light Theme'}</strong>
          </div>
          <button className="theme-toggle-switch" type="button" onClick={onToggleTheme} aria-label="Toggle theme">
            <span className={`switch-knob ${theme === 'dark' ? 'switch-dark' : ''}`} />
          </button>
        </div>

        <div className="sidebar-profile">
          <span className="user-avatar">{initials(user.name)}</span>
          <span className="sidebar-profile-copy">
            <strong>{user.name}</strong>
            <small>{user.role === 'student' ? (user.prn ? `PRN: ${user.prn}` : 'Student') : user.role === 'admin' ? 'Chief Warden' : (user.trade ? `${user.trade} Technician` : 'Maintenance Staff')}</small>
          </span>
          <button className="icon-button logout-icon" onClick={onLogout} title="Sign out" aria-label="Sign out"><LogOut size={18} /></button>
        </div>
      </div>
    </aside>
  </>;
}

function Header({ user, view, onNewComplaint, onNewLostFound, onMenu, refreshing, onRefresh, theme, onToggleTheme }) {
  const titles = {
    profile: ['My Profile', 'Your registered student allocation, division, and verified room co-occupants.'],
    grievances: [
      user.role === 'admin' ? 'All Grievances Operations Desk' : 'Campus Grievance Feed',
      user.role === 'admin' ? 'Review all hostel complaints, assign facility technicians, and record redressal.' : 'Browse active campus issues, filter by category, and support community tickets.'
    ],
    'file-grievance': ['File Grievance', 'Lodge a maintenance or facility complaint to the hostel warden and technicians.'],
    'my-grievances': ['My Grievances', 'Live tracking of grievances submitted under your student account.'],
    lostfound: ['Campus Notices & Lost-Found', 'Community bulletin for reporting lost items and recovering found belongings.'],
    overview: ['Analytics & Insights', 'Campus infrastructure KPIs, grievance pipeline, and category breakdown.'],
    directory: ['Resident Directory', 'Roster of registered student residents, room allocations, and PRN records.'],
    staff: ['Staff & Technicians Directory', 'Roster of hostel facility staff, maintenance technicians, and wardens.'],
    operations: ['Assigned Work Orders', 'Manage maintenance tickets assigned to your technical trade.']
  };
  const [title, subtitle] = titles[view] || titles.grievances;

  return <header className="topbar">
    <div className="topbar-heading">
      <button className="icon-button mobile-menu" onClick={onMenu} aria-label="Open navigation"><Menu size={22} /></button>
      <div>
        <div className="breadcrumb"><span>InHabit</span><span>/</span><strong>{title}</strong></div>
        <h1>{title}</h1>
        <p>{subtitle}</p>
      </div>
    </div>
    <div className="topbar-actions">
      <div className="today-chip"><CalendarDays size={16} /> {new Intl.DateTimeFormat('en-IN', { weekday: 'short', day: '2-digit', month: 'short' }).format(new Date())}</div>
      <button className="icon-button theme-toggle-btn" onClick={onToggleTheme} title={theme === 'dark' ? 'Switch to Light Mode' : 'Switch to Dark Mode'} aria-label="Toggle theme">
        {theme === 'dark' ? <Sun size={19} /> : <Moon size={19} />}
      </button>
      <button className="icon-button refresh-button" onClick={onRefresh} title="Refresh live data" aria-label="Refresh data">
        <RefreshCw size={18} className={refreshing ? 'spin-icon' : ''} />
      </button>
      {user.role === 'student' && view !== 'file-grievance' && (
        <button className="primary-button top-create" onClick={onNewComplaint}><Plus size={18} /> Lodge Grievance</button>
      )}
      {view === 'lostfound' && (
        <button className="primary-button top-create" onClick={onNewLostFound}><Plus size={18} /> Post Notice</button>
      )}
      <span className="top-user-avatar" title={user.name}>{initials(user.name)}</span>
    </div>
  </header>;
}

function StatCard({ icon: Icon, label, value, note, tone = 'green', trend }) {
  return <article className={`stat-card stat-${tone}`}>
    <div className="stat-card-top">
      <span className="stat-icon"><Icon size={22} /></span>
      {trend && <span className="stat-trend"><TrendingUp size={14} /> {trend}</span>}
    </div>
    <div className="stat-value">{value}</div>
    <div className="stat-label">{label}</div>
    <div className="stat-note">{note}</div>
  </article>;
}

function ComplaintCard({ complaint, user, onVote, onStatus, onAssign, technicians, isMineView = false }) {
  const Icon = categoryIcon(complaint.category);
  const own = (
    String(complaint.createdBy?._id || complaint.createdBy?.id || complaint.createdBy) === String(user.id) ||
    (user.prn && String(complaint.createdBy?.prn) === String(user.prn)) ||
    (user.email && String(complaint.createdBy?.email).toLowerCase() === String(user.email).toLowerCase())
  );
  const canVote = user.role === 'student' && !own && !complaint.currentUserVoted && !['Resolved', 'Closed'].includes(complaint.status);
  const [assigning, setAssigning] = useState(false);

  return <article className={`complaint-card ${complaint.concerning ? 'card-concerning' : ''}`}>
    <div className="complaint-topline">
      <span className={`category-icon category-${String(complaint.category || 'other').toLowerCase().replace(/[^a-z]+/g, '-')}`}><Icon size={18} /></span>
      <span className="ticket-number">{complaint.ticketNumber || 'INH-TKT'}</span>
      <span className="ticket-date">{relativeLabel(complaint.createdAt)}</span>
      {complaint.concerning && <span className="priority-label"><AlertTriangle size={14} /> Urgent Concern (30+ Supports)</span>}
    </div>
    <div className="complaint-content">
      <h3>{complaint.title}</h3>
      <p>{complaint.description}</p>
      <div className="complaint-meta">
        <span><MapPin size={15} /> {complaint.location}</span>
        <span><UserRound size={15} /> {own ? 'Submitted by You' : (complaint.isAnonymous ? 'Anonymous Resident' : (complaint.createdBy?.name || 'Resident'))}</span>
        <span><Clock3 size={15} /> {dateLabel(complaint.createdAt)}</span>
      </div>
      {complaint.attachmentUrl && (
        <a className="attachment-link" href={assetUrl(complaint.attachmentUrl)} target="_blank" rel="noreferrer">
          <FileImage size={16} /> View Evidence Attachment <ExternalLink size={14} />
        </a>
      )}
    </div>
    <div className="complaint-footer">
      <div className="complaint-footer-left">
        <StatusPill status={complaint.status} />
        <span className="vote-count"><HeartHandshake size={17} /> {complaint.voteCount || 0} supports</span>
        {complaint.assignedTo?.name && (
          <span className="assigned-chip"><Wrench size={14} /> Assigned: {complaint.assignedTo.name}{complaint.assignedTo.trade ? ` (${complaint.assignedTo.trade})` : ''}</span>
        )}
      </div>
      <div className="complaint-actions">
        {canVote && (
          <button className="support-button" onClick={() => onVote(complaint.id || complaint._id)}>
            <span className="btn-icon-pill support-icon-pill"><ArrowUp size={15} /></span>
            <span>Support (+1)</span>
          </button>
        )}
        {own && <span className="own-report-note"><CheckCircle2 size={14} /> Your Submission</span>}
        {(user.role === 'admin' || (user.role === 'technician' && (String(complaint.assignedTo?._id || complaint.assignedTo?.id) === String(user.id)))) && (
          <select className="status-select" aria-label={`Update status for ${complaint.title}`} value={complaint.status} onChange={(e) => onStatus(complaint.id || complaint._id, e.target.value)}>
            {STATUSES.map((status) => <option key={status}>{status}</option>)}
          </select>
        )}
        {user.role === 'admin' && technicians?.length > 0 && (
          <select className={`status-select assign-select ${assigning ? 'assigning' : ''}`} aria-label={`Assign ${complaint.title}`} value={complaint.assignedTo?._id || complaint.assignedTo?.id || ''} onFocus={() => setAssigning(true)} onBlur={() => setAssigning(false)} onChange={(e) => { if (e.target.value) onAssign(complaint.id || complaint._id, e.target.value); }}>
            <option value="">Assign Technician</option>
            {technicians.map((tech) => <option key={tech.id || tech._id} value={tech.id || tech._id}>{tech.name}{tech.trade ? ` · ${tech.trade}` : ''}</option>)}
          </select>
        )}
      </div>
    </div>
  </article>;
}

// Student In-Page File Grievance Form (Parity with StudentDashboard.java Tab 3)
function FileGrievanceView({ user, onSubmit, submitting, error, onNavigate }) {
  const [form, setForm] = useState({
    title: '',
    category: 'Internet',
    location: user.profile?.room ? `${user.profile.block || 'Alpha'} · Room ${user.profile.room}` : '',
    description: '',
    isAnonymous: false
  });
  const [file, setFile] = useState(null);
  const [localError, setLocalError] = useState('');

  const submit = (e) => {
    e.preventDefault();
    if (form.title.trim().length < 4 || form.description.trim().length < 10 || form.location.trim().length < 2) {
      return setLocalError('Please complete all required fields. Description must be at least 10 characters.');
    }
    setLocalError('');
    onSubmit(form, file);
  };

  return <div className="page-body">
    <div className="board-intro">
      <div>
        <span className="eyebrow">STUDENT MAINTENANCE DESK</span>
        <h2>Lodge a Hostel Grievance</h2>
        <p>File an official complaint for infrastructure, electrical, plumbing, internet, or food hygiene issues.</p>
      </div>
    </div>

    <div className="file-grievance-grid">
      <div className="panel file-grievance-panel">
        <div className="panel-heading">
          <div>
            <span className="eyebrow">NEW COMPLAINT FORM</span>
            <h3>Grievance Details</h3>
          </div>
          <span className="live-badge"><span /> Tab 3: File Grievance</span>
        </div>

        {(error || localError) && <div className="inline-error"><AlertTriangle size={18} />{localError || error}</div>}

        <form className="modal-form inpage-form" onSubmit={submit}>
          <label className="field">
            <span>Grievance Title <b>*</b></span>
            <input className="plain-input" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} maxLength={120} placeholder="e.g. Fortinet captive portal timeout on 3rd floor Wi-Fi" required />
          </label>

          <div className="form-row">
            <label className="field">
              <span>Category <b>*</b></span>
              <select className="plain-input" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
                {CATEGORIES.map((cat) => <option key={cat}>{cat}</option>)}
              </select>
            </label>
            <label className="field">
              <span>Location / Affected Room <b>*</b></span>
              <input className="plain-input" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} placeholder="e.g. Alpha · Room 101" required />
            </label>
          </div>

          <label className="field">
            <span>Detailed Description <b>*</b></span>
            <textarea className="plain-input textarea-input" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} minLength={10} maxLength={2000} rows={5} placeholder="Describe symptoms, affected equipment, and duration of the problem…" required />
            <small className="field-footnote">{form.description.length}/2000 characters (minimum 10 characters required)</small>
          </label>

          <label className="upload-field">
            <input type="file" accept="image/png,image/jpeg,image/webp" onChange={(e) => setFile(e.target.files?.[0] || null)} />
            <span className="upload-icon"><UploadCloud size={22} /></span>
            <span>
              <strong>{file ? file.name : 'Attach Evidence Photo (Optional)'}</strong>
              <small>{file ? `${(file.size / (1024 * 1024)).toFixed(2)} MB · click to change` : 'JPG, PNG or WEBP · up to 5 MB'}</small>
            </span>
            <FileImage size={20} className="upload-trailing" />
          </label>

          <label className="checkbox-line">
            <input type="checkbox" checked={form.isAnonymous} onChange={(e) => setForm({ ...form, isAnonymous: e.target.checked })} />
            <span>Submit anonymously (hides your name on the public Campus Feed while maintaining warden audit logs)</span>
          </label>

          <div className="form-actions-bar">
            <button className="primary-button inpage-submit" type="submit" disabled={submitting}>
              {submitting ? <span className="spinner" /> : <>Submit Grievance to Warden Desk <span className="btn-icon-pill"><ArrowRight size={16} /></span></>}
            </button>
            <button className="secondary-button" type="button" onClick={() => onNavigate('my-grievances')}>
              View My Grievances
            </button>
          </div>
        </form>
      </div>

      <div className="panel grievance-guidance-card">
        <div className="panel-heading">
          <div><span className="eyebrow">REDRESSAL POLICY</span><h3>Resolution Guidelines</h3></div>
        </div>
        <div className="guidance-list">
          <div className="guidance-step">
            <span className="step-num">01</span>
            <div><strong>Instant Ticket Generation</strong><p>Upon submission, a unique ticket ID (e.g. INH-TKT-1001) is issued and tracked under My Grievances.</p></div>
          </div>
          <div className="guidance-step">
            <span className="step-num">02</span>
            <div><strong>Community Peer Support</strong><p>Other residents in your hostel can upvote genuine community issues on the Campus Feed.</p></div>
          </div>
          <div className="guidance-step">
            <span className="step-num">03</span>
            <div><strong>Auto-Escalation Threshold</strong><p>Issues accumulating 30 or more community supports are auto-escalated to Urgent Concern status.</p></div>
          </div>
          <div className="guidance-step">
            <span className="step-num">04</span>
            <div><strong>Technician Dispatch</strong><p>Chief Warden assigns specialized staff (Electrical, Plumbing, Network, Mess) to resolve the ticket.</p></div>
          </div>
        </div>
      </div>
    </div>
  </div>;
}

// Admin-Only Analytics & Insights (Parity with AdminDashboard.java Tab 4)
function AdminOverview({ stats, complaints, onNavigate }) {
  const totals = stats?.totals || {};
  const categories = stats?.categoryBreakdown || [];
  const maxCategory = Math.max(1, ...categories.map((row) => row.count));

  return <div className="page-body dashboard-page">
    <div className="board-intro">
      <div>
        <span className="eyebrow">CHIEF WARDEN PORTAL</span>
        <h2>Hostel Infrastructure & Grievance Analytics</h2>
        <p>Live aggregate metrics, ticket escalation pipeline, and maintenance resolution metrics.</p>
      </div>
    </div>

    <section className="stats-grid">
      <StatCard icon={ClipboardList} label="Total Logged Grievances" value={totals.total ?? '—'} note="Across all hostel blocks" tone="green" />
      <StatCard icon={Activity} label="Active In Pipeline" value={totals.active ?? '—'} note="Open and In Progress" tone="blue" trend="Work Active" />
      <StatCard icon={AlertTriangle} label="Urgent Escalated" value={totals.concerning ?? '—'} note="30+ community supports" tone="orange" />
      <StatCard icon={CheckCircle2} label="Resolution Rate" value={totals.total == null ? '—' : `${totals.resolutionRate ?? 0}%`} note={`${totals.resolved ?? 0} tickets resolved`} tone="purple" />
    </section>

    <section className="overview-grid">
      <div className="panel category-panel">
        <div className="panel-heading">
          <div><span className="eyebrow">FACILITY WORKLOAD</span><h2>Complaints by Category</h2></div>
          <button className="text-button" onClick={() => onNavigate('grievances')}>View Full Queue <ArrowRight size={15} /></button>
        </div>
        {categories.length ? (
          <div className="category-bars">
            {categories.map((row) => (
              <div className="category-bar-row" key={row.category}>
                <div className="category-bar-meta"><span>{row.category}</span><strong>{row.count}</strong></div>
                <div className="bar-track"><span style={{ width: `${Math.max(6, (row.count / maxCategory) * 100)}%` }} /></div>
              </div>
            ))}
          </div>
        ) : (
          <div className="empty-inline"><ClipboardList size={24} /><span>No category records found.</span></div>
        )}
      </div>

      <div className="panel recent-panel">
        <div className="panel-heading">
          <div><span className="eyebrow">RECENT LODGED TICKETS</span><h2>Latest Submissions</h2></div>
          <button className="icon-button soft-icon" onClick={() => onNavigate('grievances')} aria-label="Open grievance queue"><ArrowUpRight size={18} /></button>
        </div>
        {complaints.slice(0, 5).length ? (
          <div className="recent-list">
            {complaints.slice(0, 5).map((item) => {
              const Icon = categoryIcon(item.category);
              return <button className="recent-item" key={item.id || item._id} onClick={() => onNavigate('grievances')}>
                <span className="recent-category"><Icon size={18} /></span>
                <span className="recent-item-copy"><strong>{item.title}</strong><small>{item.ticketNumber} · {relativeLabel(item.createdAt)}</small></span>
                <StatusPill status={item.status} />
                <ArrowRight size={15} className="recent-arrow" />
              </button>;
            })}
          </div>
        ) : (
          <div className="empty-inline"><MessageSquare size={24} /><span>No complaints filed yet.</span></div>
        )}
      </div>
    </section>
  </div>;
}

// Student & Admin Complaint Board (Feed, My Grievances, Technician Work Orders)
function ComplaintBoard({ complaints, user, onVote, onStatus, onAssign, technicians, onNewComplaint, view, search, setSearch, statusFilter, setStatusFilter, categoryFilter, setCategoryFilter, loading, onNavigate }) {
  const isMine = view === 'my-grievances';
  const isOperations = view === 'operations';

  // Strict client-side filter guarantee: "My Grievances" ONLY displays tickets filed by this logged-in student
  const visible = useMemo(() => complaints.filter((item) => {
    if (isMine) {
      const creatorId = item.createdBy?._id || item.createdBy?.id || item.createdBy;
      return (
        String(creatorId) === String(user.id) ||
        (user.prn && String(item.createdBy?.prn) === String(user.prn)) ||
        (user.email && String(item.createdBy?.email).toLowerCase() === String(user.email).toLowerCase())
      );
    }
    if (isOperations && user.role === 'technician') {
      const assigneeId = item.assignedTo?._id || item.assignedTo?.id || item.assignedTo;
      return String(assigneeId) === String(user.id);
    }
    return true;
  }), [complaints, isMine, isOperations, user]);

  return <div className="page-body">
    <div className="board-intro">
      <div>
        <span className="eyebrow">{isMine ? 'MY LODGED GRIEVANCES' : isOperations ? 'TECHNICIAN WORK DESK' : 'CAMPUS GRIEVANCES'}</span>
        <h2>{isMine ? 'My Grievances' : isOperations ? 'Assigned Maintenance Work Orders' : 'Campus Grievance Redressal Feed'}</h2>
        <p>{isMine ? 'Official grievances lodged by your account. Review live inspection and redressal status.' : isOperations ? 'Update tickets assigned to your technical trade and record resolution notes.' : 'Browse active community grievances, filter by category, and support campus issues.'}</p>
      </div>
      {user.role === 'student' && <button className="primary-button" onClick={() => onNavigate('file-grievance')}><Plus size={18} /> Lodge Grievance</button>}
    </div>

    {isMine && (
      <div className="my-submissions-summary-bar">
        <div className="summary-bar-text">
          <h3>Grievances Filed Under Your Student Account ({visible.length})</h3>
          <p>Showing tickets registered for <strong>{user.name}</strong> (PRN: <strong>{user.prn || '257122235'}</strong> · Room: {user.profile?.room ? `Alpha-${user.profile.room}` : 'Hostel Resident'}).</p>
        </div>
        <span className="summary-bar-pill"><ShieldCheck size={16} /> Verified Student Filter Active</span>
      </div>
    )}

    <div className="filter-toolbar">
      <label className="search-field">
        <Search size={18} />
        <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search title, ticket number, room..." />
      </label>
      <label className="filter-select">
        <Filter size={16} />
        <select value={categoryFilter} onChange={(e) => setCategoryFilter(e.target.value)}>
          <option value="">All Categories</option>
          {CATEGORIES.map((cat) => <option key={cat}>{cat}</option>)}
        </select>
        <ChevronDown size={15} />
      </label>
      <label className="filter-select">
        <Activity size={16} />
        <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="">All Statuses</option>
          {STATUSES.map((status) => <option key={status}>{status}</option>)}
        </select>
        <ChevronDown size={15} />
      </label>
      <div className="result-count"><strong>{visible.length}</strong> {visible.length === 1 ? 'ticket' : 'tickets'}</div>
    </div>

    {loading ? (
      <div className="loading-state"><span className="spinner spinner-dark" /> Loading grievances…</div>
    ) : visible.length ? (
      <div className="complaint-list">
        {visible.map((item) => (
          <ComplaintCard key={item.id || item._id} complaint={item} user={user} onVote={onVote} onStatus={onStatus} onAssign={onAssign} technicians={technicians} isMineView={isMine} />
        ))}
      </div>
    ) : (
      <div className="empty-state">
        <div className="empty-illustration"><ClipboardList size={36} /></div>
        <h3>{isMine ? 'You have not submitted any grievances yet' : 'No matching grievances found'}</h3>
        <p>{isMine ? 'If you have an issue with electricity, plumbing, Wi-Fi, or hostel facilities, click below to lodge a grievance.' : 'Try adjusting your search query or category filters.'}</p>
        {user.role === 'student' && <button className="primary-button" onClick={() => onNavigate('file-grievance')}><Plus size={17} /> Lodge Grievance</button>}
      </div>
    )}
  </div>;
}

// Student Profile & Roommates Panel (Parity with StudentDashboard.java Tab 1)
function ProfileView({ user, roommates = [], onNavigate }) {
  return <div className="page-body">
    <div className="board-intro">
      <div>
        <span className="eyebrow">STUDENT RESIDENT PROFILE</span>
        <h2>Hostel Room & Roommate Information</h2>
        <p>Official student registration and room allocation details for the academic term.</p>
      </div>
      <button className="primary-button" onClick={() => onNavigate('file-grievance')}><Plus size={18} /> Lodge Grievance</button>
    </div>

    <div className="profile-layout-grid">
      <div className="panel profile-card">
        <div className="profile-header">
          <span className="profile-large-avatar">{initials(user.name)}</span>
          <div>
            <h3>{user.name}</h3>
            <span className="profile-prn-pill">PRN: {user.prn || '257122235'}</span>
          </div>
        </div>
        <div className="profile-details-list">
          <div className="profile-detail-row"><span>Hostel Block</span><strong>{user.profile?.block || 'Alpha'} (Boys Hostel)</strong></div>
          <div className="profile-detail-row"><span>Room Allocation</span><strong>Room {user.profile?.room || '101'}</strong></div>
          <div className="profile-detail-row"><span>Wing</span><strong>Wing {user.profile?.wing || 'A'}</strong></div>
          <div className="profile-detail-row"><span>Branch & Division</span><strong>B.Tech Computer Science & Engineering (CSE - C)</strong></div>
          <div className="profile-detail-row"><span>Institutional Email</span><strong>{user.email}</strong></div>
          <div className="profile-detail-row"><span>Contact Phone</span><strong>{user.phone || '+91 98230 25001'}</strong></div>
          <div className="profile-detail-row"><span>Institution</span><strong>Symbiosis Institute of Technology (SIT), Pune</strong></div>
        </div>
      </div>

      <div className="panel roommates-card">
        <div className="panel-heading">
          <div>
            <span className="eyebrow">ROOM OCCUPANTS</span>
            <h3>Roommates ({user.profile?.block || 'Alpha'}-{user.profile?.room || '101'})</h3>
          </div>
          <span className="live-badge"><span /> Verified Occupancy</span>
        </div>
        {roommates.length ? (
          <div className="roommates-list">
            {roommates.map((rm) => (
              <div key={rm.prn} className="roommate-item">
                <span className="roommate-avatar">{initials(rm.name)}</span>
                <div className="roommate-info">
                  <strong>{rm.name}</strong>
                  <small>PRN: {rm.prn} · {rm.branch}</small>
                </div>
                <span className="roommate-email">{rm.email}</span>
              </div>
            ))}
          </div>
        ) : (
          <div className="empty-inline">
            <span>No roommate records found for this room.</span>
          </div>
        )}
      </div>
    </div>
  </div>;
}

// Chief Warden Resident Directory (Parity with AdminDashboard.java Tab 2)
function ResidentDirectory({ residents = [] }) {
  const [search, setSearch] = useState('');
  const filtered = residents.filter(r =>
    `${r.name} ${r.prn} ${r.email} ${r.profile?.room} ${r.profile?.block}`.toLowerCase().includes(search.toLowerCase())
  );

  return <div className="page-body">
    <div className="board-intro">
      <div>
        <span className="eyebrow">ADMINISTRATION DIRECTORY</span>
        <h2>Hostel Resident Directory</h2>
        <p>Roster of registered student residents, room allocations, and PRN records from InHabitDBNew.</p>
      </div>
    </div>

    <div className="filter-toolbar">
      <label className="search-field">
        <Search size={18} />
        <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search resident by name, PRN, room..." />
      </label>
      <div className="result-count"><strong>{filtered.length}</strong> residents</div>
    </div>

    <div className="directory-table-wrap">
      <table className="directory-table">
        <thead>
          <tr>
            <th>Resident Name</th>
            <th>PRN</th>
            <th>Hostel Block</th>
            <th>Room & Wing</th>
            <th>Official Email</th>
            <th>Contact Phone</th>
          </tr>
        </thead>
        <tbody>
          {filtered.map(r => (
            <tr key={r.id || r._id}>
              <td><strong>{r.name}</strong></td>
              <td><span className="code-badge">{r.prn || '—'}</span></td>
              <td>{r.profile?.block || 'Alpha'}</td>
              <td>Room {r.profile?.room || '—'} ({r.profile?.wing || 'A'})</td>
              <td>{r.email}</td>
              <td>{r.phone || '+91 98230 25000'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  </div>;
}

// Chief Warden Staff & Technicians Directory (Parity with AdminDashboard.java Tab 3)
function StaffDirectory({ staff = [] }) {
  const [search, setSearch] = useState('');
  const filtered = staff.filter(s =>
    `${s.name} ${s.staffId} ${s.department} ${s.trade} ${s.email}`.toLowerCase().includes(search.toLowerCase())
  );

  return <div className="page-body">
    <div className="board-intro">
      <div>
        <span className="eyebrow">FACILITY MAINTENANCE DESK</span>
        <h2>Staff & Technicians Directory</h2>
        <p>Roster of maintenance technicians, trade specializations, and hostel wardens from InHabitDBNew.</p>
      </div>
    </div>

    <div className="filter-toolbar">
      <label className="search-field">
        <Search size={18} />
        <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search staff by name, trade, department, staff ID..." />
      </label>
      <div className="result-count"><strong>{filtered.length}</strong> staff members</div>
    </div>

    <div className="directory-table-wrap">
      <table className="directory-table staff-table">
        <thead>
          <tr>
            <th>Staff ID</th>
            <th>Name</th>
            <th>Role</th>
            <th>Department</th>
            <th>Trade / Specialization</th>
            <th>Official Email</th>
            <th>Contact Phone</th>
          </tr>
        </thead>
        <tbody>
          {filtered.map(s => (
            <tr key={s.id || s._id}>
              <td><span className="staff-id-pill">{s.staffId || 'STF100'}</span></td>
              <td><strong>{s.name}</strong></td>
              <td><span className={`role-badge role-${s.role}`}>{s.role === 'admin' ? 'Chief Warden' : 'Technician'}</span></td>
              <td>{s.department || 'Maintenance / Facilities'}</td>
              <td><span className="trade-tag">{s.trade || 'Administration'}</span></td>
              <td>{s.email}</td>
              <td>{s.phone || '+91 98230 11000'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  </div>;
}

// Lost & Found Board (Parity with StudentDashboard.java Tab 5)
function LostFoundBoard({ items, user, onCreate, onClaim, onReturned, onDelete, loading, search, setSearch, typeFilter, setTypeFilter }) {
  const openItems = items.filter((item) => ['Open', 'Found'].includes(item.status)).length;
  return <div className="page-body">
    <div className="board-intro">
      <div>
        <span className="eyebrow">CAMPUS NOTICEBOARD</span>
        <h2>Lost & Found Items</h2>
        <p>Report lost belongings or post found items to reconnect them with their rightful owner across campus.</p>
      </div>
      <button className="primary-button" onClick={onCreate}><Plus size={18} /> Post Notice</button>
    </div>

    <div className="lostfound-banner">
      <span className="lostfound-banner-icon"><PackageSearch size={26} /></span>
      <div>
        <strong>{openItems} Active Notices on Campus</strong>
        <p>Check the bulletin board before replacing valuable misplaced items.</p>
      </div>
    </div>

    <div className="filter-toolbar">
      <label className="search-field">
        <Search size={18} />
        <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search item name, location, description..." />
      </label>
      <label className="filter-select">
        <PackageSearch size={16} />
        <select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
          <option value="">All Notices (Lost & Found)</option>
          <option value="Lost">Lost Items</option>
          <option value="Found">Found Items</option>
        </select>
        <ChevronDown size={15} />
      </label>
      <div className="result-count"><strong>{items.length}</strong> notices</div>
    </div>

    {loading ? (
      <div className="loading-state"><span className="spinner spinner-dark" /> Loading notices…</div>
    ) : items.length ? (
      <div className="lostfound-grid">
        {items.map((item) => {
          const own = (item.reportedBy?._id || item.reportedBy?.id) === user.id;
          const claimLabel = item.itemType === 'Lost' ? 'I found this item' : 'Claim this item';
          const completed = item.status === 'Returned' || item.status === 'Claimed';
          return <article className="lostfound-card" key={item.id || item._id}>
            <div className={`item-type-banner item-${item.itemType.toLowerCase()}`}>
              <span>{item.itemType === 'Lost' ? <Search size={18} /> : <CheckCircle2 size={18} />}{item.itemType} Notice</span>
              <span className={`item-state state-${item.status.toLowerCase()}`}>{item.status}</span>
            </div>
            {item.imageUrl && <img className="lostfound-image" src={assetUrl(item.imageUrl)} alt={item.itemName} />}
            <div className="lostfound-card-body">
              <h3>{item.itemName}</h3>
              <p>{item.description}</p>
              <div className="item-location"><MapPin size={15} /> {item.location}</div>
              <div className="lostfound-card-meta">
                <span>Posted by <strong>{item.reportedBy?.name || 'Campus Resident'}</strong></span>
                <span>{dateLabel(item.createdAt)}</span>
              </div>
              {item.contactNote && <p className="contact-note">{item.contactNote}</p>}
              <div className="lostfound-actions">
                {!own && item.status === 'Open' && (
                  <button className="secondary-button" onClick={() => onClaim(item.id || item._id)}>{claimLabel} <ArrowRight size={15} /></button>
                )}
                {own && ['Open', 'Found'].includes(item.status) && (
                  <button className="secondary-button" onClick={() => onReturned(item.id || item._id)}><Check size={15} /> Mark Returned</button>
                )}
                {(own || user.role === 'admin') && (
                  <button className="icon-button delete-button" title="Delete notice" onClick={() => onDelete(item.id || item._id)}><X size={17} /></button>
                )}
                {completed && (
                  <span className="completed-note"><CheckCircle2 size={15} /> {item.status === 'Claimed' ? 'Claim Initiated' : 'Resolved'}</span>
                )}
              </div>
            </div>
          </article>;
        })}
      </div>
    ) : (
      <div className="empty-state">
        <div className="empty-illustration"><PackageSearch size={36} /></div>
        <h3>No notices found</h3>
        <p>Post a notice if you have lost or found something around the hostel.</p>
        <button className="primary-button" onClick={onCreate}><Plus size={17} /> Post Notice</button>
      </div>
    )}
  </div>;
}

// Modal Dialog for Quick Action
function Modal({ modal, onClose, onSubmit, submitting, error }) {
  const [form, setForm] = useState({ title: '', description: '', category: 'Internet', location: '', isAnonymous: false, itemName: '', itemType: 'Lost', contactNote: '' });
  const [file, setFile] = useState(null);
  const [localError, setLocalError] = useState('');
  useEffect(() => { setForm({ title: '', description: '', category: 'Internet', location: '', isAnonymous: false, itemName: '', itemType: 'Lost', contactNote: '' }); setFile(null); setLocalError(''); }, [modal]);
  if (!modal) return null;
  const isComplaint = modal === 'complaint';
  const submit = (event) => {
    event.preventDefault();
    if (isComplaint && (form.title.trim().length < 4 || form.description.trim().length < 10 || form.location.trim().length < 2)) {
      return setLocalError('Please fill each required field. Description must be at least 10 characters.');
    }
    if (!isComplaint && (form.itemName.trim().length < 2 || !form.description.trim() || form.location.trim().length < 2)) {
      return setLocalError('Please enter the item name, description, and location.');
    }
    setLocalError('');
    onSubmit({ ...form }, file);
  };

  return <div className="modal-overlay" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose(); }}>
    <section className="modal-card" role="dialog" aria-modal="true" aria-labelledby="modal-title">
      <div className="modal-header">
        <div>
          <span className="eyebrow">{isComplaint ? 'GRIEVANCE REDRESSAL' : 'CAMPUS BULLETIN'}</span>
          <h2 id="modal-title">{isComplaint ? 'Lodge a Campus Grievance' : 'Post Lost / Found Notice'}</h2>
          <p>{isComplaint ? 'Provide details so hostel wardens and technicians can quickly resolve the issue.' : 'Provide details and location to help return misplaced items.'}</p>
        </div>
        <button className="icon-button modal-close" onClick={onClose} aria-label="Close dialog"><X size={20} /></button>
      </div>
      {(error || localError) && <div className="inline-error modal-error"><AlertTriangle size={18} />{localError || error}</div>}
      <form className="modal-form" onSubmit={submit}>
        {isComplaint ? <>
          <label className="field"><span>Grievance Title <b>*</b></span><input className="plain-input" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} maxLength={120} placeholder="e.g. Wi-Fi keeps disconnecting on 3rd floor" required /></label>
          <label className="field"><span>Detailed Description <b>*</b></span><textarea className="plain-input textarea-input" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} minLength={10} maxLength={2000} rows={4} placeholder="Describe the problem, room/area affected, and symptoms…" required /><small className="field-footnote">{form.description.length}/2000 characters</small></label>
          <div className="form-row">
            <label className="field"><span>Category <b>*</b></span><select className="plain-input" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>{CATEGORIES.map((cat) => <option key={cat}>{cat}</option>)}</select></label>
            <label className="field"><span>Location / Room <b>*</b></span><input className="plain-input" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} placeholder="e.g. Alpha · Room 304" required /></label>
          </div>
          <label className="upload-field"><input type="file" accept="image/png,image/jpeg,image/webp" onChange={(e) => setFile(e.target.files?.[0] || null)} /><span className="upload-icon"><UploadCloud size={20} /></span><span><strong>{file ? file.name : 'Attach Photo Evidence (Optional)'}</strong><small>{file ? `${(file.size / (1024 * 1024)).toFixed(2)} MB · click to change` : 'JPG, PNG or WEBP · up to 5 MB'}</small></span><FileImage size={19} className="upload-trailing" /></label>
          <label className="checkbox-line"><input type="checkbox" checked={form.isAnonymous} onChange={(e) => setForm({ ...form, isAnonymous: e.target.checked })} /><span>Submit anonymously (hides your name on the public board)</span></label>
        </> : <>
          <label className="field"><span>Item Name <b>*</b></span><input className="plain-input" value={form.itemName} onChange={(e) => setForm({ ...form, itemName: e.target.value })} placeholder="e.g. Casio fx-991CW Calculator" required maxLength={100} /></label>
          <div className="form-row">
            <label className="field"><span>Notice Type <b>*</b></span><select className="plain-input" value={form.itemType} onChange={(e) => setForm({ ...form, itemType: e.target.value })}><option>Lost</option><option>Found</option></select></label>
            <label className="field"><span>Location <b>*</b></span><input className="plain-input" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} placeholder="Where was it lost/found?" required /></label>
          </div>
          <label className="field"><span>Description <b>*</b></span><textarea className="plain-input textarea-input" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} rows={3} maxLength={1000} placeholder="Color, brand, identifying marks, serial..." required /></label>
          <label className="field"><span>Contact / Handover Instructions <small>optional</small></span><input className="plain-input" value={form.contactNote} onChange={(e) => setForm({ ...form, contactNote: e.target.value })} placeholder="e.g. Handed over to Warden Office / call room 101" maxLength={160} /></label>
          <label className="upload-field"><input type="file" accept="image/png,image/jpeg,image/webp" onChange={(e) => setFile(e.target.files?.[0] || null)} /><span className="upload-icon"><UploadCloud size={20} /></span><span><strong>{file ? file.name : 'Attach Item Photo (Optional)'}</strong><small>JPG, PNG or WEBP · up to 5 MB</small></span><FileImage size={19} className="upload-trailing" /></label>
        </>}
        <div className="modal-footer">
          <span><ShieldCheck size={16} /> Campus Data Integrity & Privacy Protected</span>
          <div>
            <button className="secondary-button" type="button" onClick={onClose}>Cancel</button>
            <button className="primary-button" type="submit" disabled={submitting}>{submitting ? <span className="spinner" /> : <>{isComplaint ? 'Submit Grievance' : 'Publish Notice'} <span className="btn-icon-pill"><ArrowRight size={16} /></span></>}</button>
          </div>
        </div>
      </form>
    </section>
  </div>;
}

export default function App() {
  const [theme, setTheme] = useState(() => localStorage.getItem('inhabit-theme') || 'dark');
  const [user, setUser] = useState(null);
  const [authChecking, setAuthChecking] = useState(true);
  const [authLoading, setAuthLoading] = useState(false);
  const [authError, setAuthError] = useState('');
  const [toast, setToast] = useState(null);
  const [view, setView] = useState('grievances');
  const [mobileOpen, setMobileOpen] = useState(false);
  const [complaints, setComplaints] = useState([]);
  const [lostItems, setLostItems] = useState([]);
  const [stats, setStats] = useState(null);
  const [technicians, setTechnicians] = useState([]);
  const [roommates, setRoommates] = useState([]);
  const [residents, setResidents] = useState([]);
  const [staff, setStaff] = useState([]);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [lostSearch, setLostSearch] = useState('');
  const [lostType, setLostType] = useState('');
  const [dataLoading, setDataLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [modal, setModal] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [modalError, setModalError] = useState('');

  // Handle theme persistence
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('inhabit-theme', theme);
  }, [theme]);

  const toggleTheme = useCallback(() => {
    setTheme((prev) => (prev === 'dark' ? 'light' : 'dark'));
  }, []);

  const notify = useCallback((message, type = 'success') => setToast({ message, type, id: Date.now() }), []);
  const closeToast = useCallback(() => setToast(null), []);

  // Strict role view authorization guard:
  // Prevents students from ever accessing admin or technician views, and vice versa.
  useEffect(() => {
    if (!user) return;
    const allowedMap = {
      student: ['profile', 'grievances', 'file-grievance', 'my-grievances', 'lostfound'],
      admin: ['overview', 'grievances', 'directory', 'staff', 'lostfound'],
      technician: ['operations', 'lostfound']
    };
    const allowed = allowedMap[user.role] || ['grievances'];
    if (!allowed.includes(view)) {
      const defaultForRole = user.role === 'admin' ? 'overview' : user.role === 'technician' ? 'operations' : 'grievances';
      setView(defaultForRole);
    }
  }, [user, view]);

  useEffect(() => {
    const token = localStorage.getItem('inhabit_token');
    if (!token) { setAuthChecking(false); return; }
    api('/auth/me').then((result) => {
      setUser(result.user);
      const defaultView = result.user.role === 'admin' ? 'overview' : result.user.role === 'technician' ? 'operations' : 'grievances';
      setView(defaultView);
    }).catch(() => {
      localStorage.removeItem('inhabit_token');
    }).finally(() => setAuthChecking(false));
  }, []);

  const loadWorkspace = useCallback(async (quiet = false) => {
    if (!user) return;
    if (quiet) setRefreshing(true); else setDataLoading(true);
    try {
      const params = new URLSearchParams();
      if (search.trim()) params.set('q', search.trim());
      if (statusFilter) params.set('status', statusFilter);
      if (categoryFilter) params.set('category', categoryFilter);
      if (view === 'my-grievances') params.set('mine', 'true');
      if (user.role === 'technician' && view === 'operations') params.set('mine', 'false');

      const lfParams = new URLSearchParams();
      if (lostSearch.trim()) lfParams.set('q', lostSearch.trim());
      if (lostType) lfParams.set('type', lostType);

      const promises = [
        api(`/complaints${params.toString() ? `?${params}` : ''}`),
        api(`/lost-found${lfParams.toString() ? `?${lfParams}` : ''}`)
      ];

      if (user.role === 'admin') {
        promises.push(api('/dashboard/stats'));
        promises.push(api('/dashboard/technicians'));
        promises.push(api('/dashboard/residents'));
        promises.push(api('/dashboard/staff'));
      } else if (user.role === 'student') {
        promises.push(api('/dashboard/roommates'));
      }

      const results = await Promise.all(promises);
      setComplaints(results[0]?.complaints || []);
      setLostItems(results[1]?.items || []);

      if (user.role === 'admin') {
        setStats(results[2] || null);
        setTechnicians(results[3]?.technicians || []);
        setResidents(results[4]?.residents || []);
        setStaff(results[5]?.staff || []);
      } else if (user.role === 'student') {
        setRoommates(results[2]?.roommates || []);
      }
    } catch (error) {
      notify(error.message || 'Unable to load workspace data.', 'error');
    } finally {
      setDataLoading(false); setRefreshing(false);
    }
  }, [user, search, statusFilter, categoryFilter, view, lostSearch, lostType, notify]);

  useEffect(() => {
    if (!user) return undefined;
    const timer = window.setTimeout(() => loadWorkspace(false), (search || lostSearch) ? 250 : 0);
    return () => window.clearTimeout(timer);
  }, [user, search, statusFilter, categoryFilter, view, lostSearch, lostType, loadWorkspace]);

  const handleLogin = async (credentials) => {
    setAuthLoading(true); setAuthError('');
    try {
      const result = await api('/auth/login', { method: 'POST', body: credentials });
      localStorage.setItem('inhabit_token', result.token);
      setUser(result.user);
      const defaultView = result.user.role === 'admin' ? 'overview' : result.user.role === 'technician' ? 'operations' : 'grievances';
      setView(defaultView);
      setSearch(''); setStatusFilter(''); setCategoryFilter('');
      notify(`Welcome back, ${result.user.name.split(' ')[0]}.`);
    } catch (error) { setAuthError(error.message); }
    finally { setAuthLoading(false); }
  };

  const handleRegister = async (data) => {
    setAuthLoading(true); setAuthError('');
    try {
      const result = await api('/auth/register', { method: 'POST', body: data });
      localStorage.setItem('inhabit_token', result.token);
      setUser(result.user);
      setView('grievances');
      notify('Your resident account is ready. Welcome to InHabit!');
    } catch (error) { setAuthError(error.message); }
    finally { setAuthLoading(false); }
  };

  const logout = () => {
    localStorage.removeItem('inhabit_token');
    setUser(null);
    setComplaints([]);
    setLostItems([]);
    setView('grievances');
    setAuthError('');
    notify('You have signed out.');
  };

  const refreshNow = () => loadWorkspace(true);
  const openModal = (kind) => { setModal(kind); setModalError(''); };

  const uploadOptionalFile = async (file) => {
    if (!file) return '';
    if (file.size > 5 * 1024 * 1024) throw new Error('Image must be 5 MB or smaller.');
    const form = new FormData(); form.append('image', file);
    const result = await api('/complaints/upload', { method: 'POST', body: form });
    return result.url;
  };

  const createRecord = async (data, file) => {
    setSubmitting(true); setModalError('');
    try {
      const uploadedUrl = await uploadOptionalFile(file);
      if (modal === 'complaint' || view === 'file-grievance') {
        const res = await api('/complaints', { method: 'POST', body: { title: data.title, description: data.description, category: data.category, location: data.location, isAnonymous: data.isAnonymous, attachmentUrl: uploadedUrl } });
        notify(`Grievance submitted (${res.complaint?.ticketNumber || 'INH-TKT'}). Tracking under My Grievances.`);
        setView('my-grievances');
      } else {
        await api('/lost-found', { method: 'POST', body: { itemName: data.itemName, itemType: data.itemType, description: data.description, location: data.location, contactNote: data.contactNote, imageUrl: uploadedUrl } });
        notify('Your lost-and-found notice is live.');
        setView('lostfound');
      }
      setModal(null); await loadWorkspace(true);
    } catch (error) { setModalError(error.message); }
    finally { setSubmitting(false); }
  };

  const castVote = async (id) => {
    try { const result = await api(`/complaints/${id}/vote`, { method: 'POST' }); notify(result.message); await loadWorkspace(true); }
    catch (error) { notify(error.message, 'error'); }
  };
  const changeStatus = async (id, status) => {
    try { await api(`/complaints/${id}/status`, { method: 'PATCH', body: { status } }); notify(`Grievance status updated to ${status}.`); await loadWorkspace(true); }
    catch (error) { notify(error.message, 'error'); await loadWorkspace(true); }
  };
  const assignTechnician = async (id, technicianId) => {
    try { const result = await api(`/complaints/${id}/assign`, { method: 'PATCH', body: { technicianId } }); notify(result.message); await loadWorkspace(true); }
    catch (error) { notify(error.message, 'error'); }
  };
  const claimItem = async (id) => {
    try { const result = await api(`/lost-found/${id}/claim`, { method: 'PATCH' }); notify(result.message); await loadWorkspace(true); }
    catch (error) { notify(error.message, 'error'); }
  };
  const markReturned = async (id) => {
    try { const result = await api(`/lost-found/${id}/returned`, { method: 'PATCH' }); notify(result.message); await loadWorkspace(true); }
    catch (error) { notify(error.message, 'error'); }
  };
  const deleteItem = async (id) => {
    if (!window.confirm('Delete this notice?')) return;
    try { await api(`/lost-found/${id}`, { method: 'DELETE' }); notify('Notice deleted.'); await loadWorkspace(true); }
    catch (error) { notify(error.message, 'error'); }
  };
  const navigate = (newView) => { setView(newView); setMobileOpen(false); };

  if (authChecking) return <div className="screen-loading"><span className="spinner spinner-dark" /><span>Opening InHabit Portal…</span></div>;
  if (!user) return <><AuthScreen onLogin={handleLogin} onRegister={handleRegister} loading={authLoading} error={authError} theme={theme} onToggleTheme={toggleTheme} /><Toast toast={toast} onClose={closeToast} /></>;

  return <div className="app-shell">
    <a href="#main-content" className="skip-link">Skip to main content</a>
    <Sidebar user={user} view={view} setView={navigate} onLogout={logout} mobileOpen={mobileOpen} setMobileOpen={setMobileOpen} theme={theme} onToggleTheme={toggleTheme} />
    <main id="main-content" className="main-shell">
      <Header user={user} view={view} onNewComplaint={() => navigate('file-grievance')} onNewLostFound={() => openModal('lostfound')} onMenu={() => setMobileOpen(true)} refreshing={refreshing} onRefresh={refreshNow} theme={theme} onToggleTheme={toggleTheme} />

      {/* Role-Segregated Views */}
      {view === 'overview' && user.role === 'admin' && (
        <AdminOverview stats={stats} complaints={complaints} onNavigate={navigate} />
      )}
      {(view === 'grievances' || view === 'my-grievances' || view === 'operations') && (
        <ComplaintBoard complaints={complaints} user={user} onVote={castVote} onStatus={changeStatus} onAssign={assignTechnician} technicians={technicians} onNewComplaint={() => navigate('file-grievance')} view={view} search={search} setSearch={setSearch} statusFilter={statusFilter} setStatusFilter={setStatusFilter} categoryFilter={categoryFilter} setCategoryFilter={setCategoryFilter} loading={dataLoading} onNavigate={navigate} />
      )}
      {view === 'file-grievance' && user.role === 'student' && (
        <FileGrievanceView user={user} onSubmit={createRecord} submitting={submitting} error={modalError} onNavigate={navigate} />
      )}
      {view === 'profile' && user.role === 'student' && (
        <ProfileView user={user} roommates={roommates} onNavigate={navigate} />
      )}
      {view === 'directory' && user.role === 'admin' && (
        <ResidentDirectory residents={residents} />
      )}
      {view === 'staff' && user.role === 'admin' && (
        <StaffDirectory staff={staff} />
      )}
      {view === 'lostfound' && (
        <LostFoundBoard items={lostItems} user={user} onCreate={() => openModal('lostfound')} onClaim={claimItem} onReturned={markReturned} onDelete={deleteItem} loading={dataLoading} search={lostSearch} setSearch={setLostSearch} typeFilter={lostType} setTypeFilter={setLostType} />
      )}

      <footer className="main-footer">
        <span><span className="footer-dot" /> InHabit — Campus Hostel Grievance Management System · Symbiosis Institute of Technology</span>
        <span>F0002 FULL STACK DEVELOPMENT <span className="footer-divider">/</span> Raghav Sarda (257122235) & Aniruddha Singh (257122247)</span>
      </footer>
    </main>
    <Toast toast={toast} onClose={closeToast} />
    <Modal modal={modal} onClose={() => { setModal(null); setModalError(''); }} onSubmit={createRecord} submitting={submitting} error={modalError} />
  </div>;
}
