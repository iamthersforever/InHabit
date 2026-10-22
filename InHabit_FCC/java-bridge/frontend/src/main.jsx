import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';

const API = 'http://localhost:8090/api';

const demoComplaints = [
  {ticketId:1003,title:'Mess dinner paneer gravy sour and undercooked rotis',description:'Food hygiene concern reported by multiple residents.',category:'Mess',location:'Central Mess',status:'Open',concerning:true,createdByPrn:'257122001',assignedStaffId:'STF113',createdAt:'2026-09-26 21:10:00',voteCount:30},
  {ticketId:1001,title:'Fortinet captive portal timeout on 3rd floor Wi-Fi',description:'Internet access times out during peak hours.',category:'Internet',location:'Alpha-304',status:'In Progress',concerning:false,createdByPrn:'257122235',assignedStaffId:'STF111',createdAt:'2026-09-24 16:40:00',voteCount:2},
  {ticketId:1002,title:'Solar hot water pipeline cold in washroom',description:'Hot water line running cold in washrooms 201–210.',category:'Plumbing',location:'Beta-201',status:'Under Review',concerning:false,createdByPrn:'257122247',assignedStaffId:'STF108',createdAt:'2026-09-25 07:15:00',voteCount:2},
  {ticketId:1004,title:'Balcony sliding window latch broken',description:'Window does not lock securely.',category:'Cleanliness',location:'Delta-108',status:'Open',concerning:false,createdByPrn:'257122006',assignedStaffId:null,createdAt:'2026-09-27 11:30:00',voteCount:0},
  {ticketId:1006,title:'Charlie-Study AC excessive rattling noise',description:'AC emits loud mechanical vibration.',category:'Electrical',location:'Charlie-Study',status:'Resolved',concerning:false,createdByPrn:'257126003',assignedStaffId:'STF107',createdAt:'2026-09-28 14:10:00',voteCount:1}
];

const demoLost = [
  {itemId:'LF103',itemName:'boAt Airdopes 141 ANC Charging Case',description:'Black matte earbud case left on table.',location:'Hostel Mess Ground Floor',itemType:'Found',status:'Open',reportedByPrn:'257122001',reportedAt:'2026-09-25 09:05:00'},
  {itemId:'LF104',itemName:'Set of 3 Godrej Keys on Pulsar Keychain',description:'Bundle of 3 room keys.',location:'Sports Complex / Court',itemType:'Lost',status:'Open',reportedByPrn:'257122005',reportedAt:'2026-09-26 19:40:00'},
  {itemId:'LF105',itemName:'Milton Thermosteel 1000ml Bottle (Grey)',description:'Left on reading desk.',location:'Central Library 2nd Floor',itemType:'Found',status:'Open',reportedByPrn:'257126003',reportedAt:'2026-09-27 16:20:00'}
];

async function request(path, options={}) {
  const res = await fetch(API + path, options);
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Request failed');
  return data;
}
const post = (path, data) => request(path, {method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body:new URLSearchParams(data)});

function App(){
  const [user,setUser] = useState(()=>JSON.parse(localStorage.getItem('inhabitUser')||'null'));
  const [online,setOnline] = useState(false);
  const [tab,setTab] = useState('dashboard');
  const [complaints,setComplaints] = useState(demoComplaints);
  const [lost,setLost] = useState(demoLost);
  const [toast,setToast] = useState('');
  const [search,setSearch] = useState('');

  useEffect(()=>{ request('/health').then(()=>setOnline(true)).catch(()=>setOnline(false)); },[]);
  useEffect(()=>{ if(!user)return; refreshComplaints(); refreshLost(); },[user,online]);
  useEffect(()=>{ if(toast){const t=setTimeout(()=>setToast(''),2600);return()=>clearTimeout(t)} },[toast]);

  async function refreshComplaints(){
    try{
      const scope = user.role?.toLowerCase().includes('technician') ? `scope=staff&staffId=${encodeURIComponent(user.id)}` : user.role==='Student' ? `scope=student&prn=${encodeURIComponent(user.prn)}` : 'scope=all';
      const data=await request('/complaints?'+scope); setComplaints(data); setOnline(true);
    }catch(e){setOnline(false);}
  }
  async function refreshLost(){try{const d=await request('/lostfound');setLost(d);setOnline(true)}catch(e){}}
  function logout(){localStorage.removeItem('inhabitUser');setUser(null)}

  if(!user) return <Login onLogin={u=>{setUser(u);localStorage.setItem('inhabitUser',JSON.stringify(u));}} onToast={setToast}/>;
  const filtered=complaints.filter(c=>`${c.title} ${c.category} ${c.location} ${c.status}`.toLowerCase().includes(search.toLowerCase()));
  const stats={total:complaints.length,open:complaints.filter(c=>c.status==='Open').length,progress:complaints.filter(c=>c.status==='In Progress').length,urgent:complaints.filter(c=>c.concerning).length};

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><div className="brand-mark">IH</div><div><b>InHabit</b><span>Hostel Community</span></div></div>
      <nav>{[['dashboard','Dashboard'],['complaints','Grievances'],['lost','Lost & Found']].map(([id,label])=><button key={id} className={tab===id?'nav active':'nav'} onClick={()=>setTab(id)}>{label}</button>)}</nav>
      <div className="sidebar-bottom"><span className={'dot '+(online?'online':'offline')}></span>{online?'Java + MySQL connected':'Demo / fallback mode'}</div>
    </aside>
    <main className="main">
      <header className="topbar"><div><div className="eyebrow">HOSTEL OPERATIONS</div><h1>{tab==='dashboard'?'Good to see you.':tab==='complaints'?'Community Grievances':'Lost & Found'}</h1></div><div className="profile"><div><b>{user.name}</b><span>{user.role}</span></div><button className="ghost" onClick={logout}>Logout</button></div></header>
      {tab==='dashboard' && <Dashboard stats={stats} complaints={complaints} setTab={setTab}/>} 
      {tab==='complaints' && <Complaints user={user} complaints={filtered} search={search} setSearch={setSearch} refresh={refreshComplaints} toast={setToast}/>} 
      {tab==='lost' && <LostFound user={user} items={lost} refresh={refreshLost} toast={setToast}/>} 
    </main>
    {toast && <div className="toast">{toast}</div>}
  </div>
}

function Login({onLogin,onToast}){
 const [identity,setIdentity]=useState('257122235'); const [password,setPassword]=useState('student123'); const [loading,setLoading]=useState(false); const [demo,setDemo]=useState(false);
 async function submit(e){e.preventDefault();setLoading(true);try{const u=await post('/login',{identity,password});onLogin(u)}catch(err){onToast(err.message);setDemo(true)}finally{setLoading(false)}}
 function demoLogin(role){const u=role==='Student'?{id:'257122235',prn:'257122235',name:'Raghav Mahesh Sarda',email:'raghav.sarda.btech25@college.edu.in',role,location:'Alpha-101'}:{id:'STF113',prn:'STF113',name:'Hostel Administrator',email:'admin@college.edu.in',role:'Admin',location:'Hostel'};onLogin(u)}
 return <div className="login-page"><div className="login-card"><div className="brand large"><div className="brand-mark">IH</div><div><b>InHabit</b><span>Hostel Grievance Management</span></div></div><h2>One place for every hostel issue.</h2><p className="muted">This FCC Jugaad frontend talks to the original Java DAO layer through a tiny HTTP adapter.</p><form onSubmit={submit}><label>Institutional Email / PRN<input value={identity} onChange={e=>setIdentity(e.target.value)} /></label><label>Password<input type="password" value={password} onChange={e=>setPassword(e.target.value)} /></label><button className="primary" disabled={loading}>{loading?'Signing in…':'Sign in'}</button></form><div className="login-divider">or use demo mode</div><div className="demo-row"><button onClick={()=>demoLogin('Student')}>Student demo</button><button onClick={()=>demoLogin('Admin')}>Admin demo</button></div>{demo && <small className="muted">Java bridge unavailable — demo data is being used. Start WebBridge on port 8090 for the real DAO/database path.</small>}</div></div>
}

function Dashboard({stats,complaints,setTab}){return <section className="content"><div className="hero"><div><span className="pill">INHABIT • FCC PORT</span><h2>Make hostel problems visible, actionable, and trackable.</h2><p>Same project logic. New web interface. The original Java DAO and MySQL layer stays underneath.</p></div><button className="primary" onClick={()=>setTab('complaints')}>Open grievances →</button></div><div className="stats">{[['Total tickets',stats.total],['Open',stats.open],['In progress',stats.progress],['Concerning',stats.urgent]].map(([l,v])=><div className="stat" key={l}><span>{l}</span><b>{v}</b></div>)}</div><div className="grid-two"><div className="panel"><div className="panel-head"><h3>Priority queue</h3><span>Votes / urgency</span></div>{complaints.slice(0,5).map(c=><ComplaintRow c={c} key={c.ticketId}/>)}</div><div className="panel dark-panel"><div className="panel-head"><h3>Architecture</h3></div><div className="stack"><div>React UI</div><i>↓</i><div>WebBridge.java</div><i>↓</i><div>Existing DAO classes</div><i>↓</i><div>MySQL / fallback data</div></div></div></div></section>}
function ComplaintRow({c}){return <div className="complaint-row"><div className="priority">{c.concerning?'!':c.voteCount}</div><div className="row-main"><b>#{c.ticketId} · {c.title}</b><span>{c.category} · {c.location}</span></div><span className={'status '+c.status.toLowerCase().replaceAll(' ','-')}>{c.status}</span></div>}

function Complaints({user,complaints,search,setSearch,refresh,toast}){const [form,setForm]=useState({title:'',description:'',category:'Internet',location:''});
 async function vote(c){try{await post('/vote',{ticketId:c.ticketId,prn:user.prn,voteType:'Genuine'});toast('Vote recorded.');refresh()}catch(e){toast(e.message)}}
 async function submit(e){e.preventDefault();try{await post('/complaints',{...form,prn:user.prn,filedBy:'USR_'+user.prn});toast('Grievance filed successfully.');setForm({title:'',description:'',category:'Internet',location:''});refresh()}catch(e){toast(e.message)}}
 async function update(c,status){try{await post('/status',{ticketId:c.ticketId,status});toast('Status updated.');refresh()}catch(e){toast(e.message)}}
 return <section className="content"><div className="toolbar"><input placeholder="Search grievances…" value={search} onChange={e=>setSearch(e.target.value)}/><button className="ghost" onClick={refresh}>Refresh</button></div><div className="grid-two"><div className="panel"><div className="panel-head"><h3>Tickets</h3><span>{complaints.length} shown</span></div>{complaints.map(c=><div className="ticket" key={c.ticketId}><div className="ticket-top"><span className="ticket-id">#{c.ticketId}</span><span className={'status '+c.status.toLowerCase().replaceAll(' ','-')}>{c.status}</span></div><h3>{c.title}</h3><p>{c.description}</p><div className="meta"><span>{c.category}</span><span>{c.location}</span><span>{c.voteCount} votes</span></div><div className="actions"><button onClick={()=>vote(c)}>Vote genuine</button>{['Admin','Warden'].some(r=>user.role?.includes(r))&&<><button onClick={()=>update(c,'In Progress')}>Start</button><button onClick={()=>update(c,'Resolved')}>Resolve</button></>}</div></div>)}</div><div className="panel"><div className="panel-head"><h3>File a grievance</h3><span>Existing Java validation</span></div><form onSubmit={submit} className="stack-form"><label>Title<input required value={form.title} onChange={e=>setForm({...form,title:e.target.value})}/></label><label>Description<textarea required value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></label><label>Category<select value={form.category} onChange={e=>setForm({...form,category:e.target.value})}>{['Internet','Plumbing','Mess','Electrical','Cleanliness','Security','Other'].map(x=><option key={x}>{x}</option>)}</select></label><label>Location<input required value={form.location} onChange={e=>setForm({...form,location:e.target.value})}/></label><button className="primary">Submit grievance</button></form></div></div></section>}

function LostFound({user,items,refresh,toast}){const [form,setForm]=useState({itemName:'',description:'',location:'',itemType:'Found'});async function submit(e){e.preventDefault();try{await post('/lostfound',{...form,prn:user.prn});toast('Notice posted.');setForm({itemName:'',description:'',location:'',itemType:'Found'});refresh()}catch(err){toast(err.message)}}async function claim(id){try{await post('/claim',{itemId:id});toast('Item marked claimed.');refresh()}catch(e){toast(e.message)}}return <section className="content"><div className="grid-two"><div className="panel"><div className="panel-head"><h3>Campus notices</h3><span>{items.length} items</span></div>{items.map(i=><div className="ticket" key={i.itemId}><div className="ticket-top"><span className="ticket-id">{i.itemId}</span><span className={'status '+i.status.toLowerCase()}>{i.status}</span></div><h3>{i.itemName}</h3><p>{i.description}</p><div className="meta"><span>{i.itemType}</span><span>{i.location}</span></div>{i.status==='Open'&&<div className="actions"><button onClick={()=>claim(i.itemId)}>Mark claimed</button></div>}</div>)}</div><div className="panel"><div className="panel-head"><h3>Report lost / found</h3></div><form onSubmit={submit} className="stack-form"><label>Item name<input required value={form.itemName} onChange={e=>setForm({...form,itemName:e.target.value})}/></label><label>Description<textarea required value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></label><label>Location<input required value={form.location} onChange={e=>setForm({...form,location:e.target.value})}/></label><label>Type<select value={form.itemType} onChange={e=>setForm({...form,itemType:e.target.value})}><option>Found</option><option>Lost</option></select></label><button className="primary">Post notice</button></form></div></div></section>}

createRoot(document.getElementById('root')).render(<App/>);
