import React, { useState, useEffect } from 'react';
import {
  Users,
  Search,
  MessageSquare,
  Award,
  Calendar,
  Zap,
  Activity,
  UserCircle,
  ShieldCheck,
  Star
} from 'lucide-react';
import axios from 'axios';
import Login from './components/Login';
import Matches from './components/Matches';
import Profile from './components/Profile';
import Chat from './components/Chat';
import Bookings from './components/Bookings';
import Badges from './components/Badges';
import './App.css';

const API_BASE = '';

function App() {
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState('dashboard');
  const [services, setServices] = useState([
    { id: 'auth', name: 'Auth Service', status: 'checking', icon: ShieldCheck, path: '/api/auth/health' },
    { id: 'profile', name: 'Profile Service', status: 'checking', icon: UserCircle, path: '/api/profiles/health' },
    { id: 'matching', name: 'Matching Service', status: 'checking', icon: Zap, path: '/api/matching/health' },
    { id: 'chat', name: 'Chat Service', status: 'checking', icon: MessageSquare, path: '/chat/health' },
    { id: 'reputation', name: 'Reputation Service', status: 'checking', icon: Award, path: '/api/reputation/health' },
    { id: 'booking', name: 'Booking Service', status: 'checking', icon: Calendar, path: '/api/bookings/health' },
  ]);

  const [initialChatRecipient, setInitialChatRecipient] = useState(null);

  const startChat = (recipient) => {
    setActiveTab('messages');
    setInitialChatRecipient(recipient);
  };

  useEffect(() => {
    const checkStatus = async () => {
      const updatedServices = await Promise.all(services.map(async (s) => {
        try {
          await axios.get(`${API_BASE}${s.path}`, { timeout: 3000 });
          return { ...s, status: 'online' };
        } catch (err) {
          console.warn(`Service ${s.id} check failed:`, err);
          // Ignore 404 for health checks if service is actually running, but here we expect 200
          // Some checks might return 404 if health endpoint is missing, but service is up.
          // For now, let's trust 200 OK.
          if (err.response && err.response.status === 200) return { ...s, status: 'online' };
          return { ...s, status: 'offline' };
        }
      }));
      setServices(updatedServices);
    };

    checkStatus();

    // Check for existing session
    const savedUser = localStorage.getItem('user');
    if (savedUser) setUser(JSON.parse(savedUser));
  }, []);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  if (!user) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  const stats = [
    { label: 'Active Users', value: '1,280', color: 'var(--primary)' },
    { label: 'Skill Matches', value: '452', color: 'var(--success)' },
    { label: 'Avg Reputation', value: '4.8', color: 'var(--warning)' },
  ];





  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return (
          <>
            <section className="stats-grid">
              {stats.map((stat, i) => (
                <div key={i} className="card stat-card">
                  <p className="text-muted">{stat.label}</p>
                  <h2 style={{ color: stat.color }}>{stat.value}</h2>
                </div>
              ))}
            </section>

          </>
        );
      case 'matches':
        return <Matches onChatStart={startChat} />;
      case 'messages':
        return <Chat user={user} initialRecipient={initialChatRecipient} />;
      case 'profile':
        return <Profile user={user} />;
      case 'bookings':
        return <Bookings user={user} />;

      default:
        return <div>Selected feature is under development.</div>;
    }
  };

  return (
    <div className="app-container">
      {/* Sidebar */}
      <nav className="sidebar">
        <div className="logo">
          <Activity size={32} color="var(--primary)" />
          <span className="gradient-text">SkillVerse</span>
        </div>

        <ul className="nav-links">
          <li className={activeTab === 'dashboard' ? 'active' : ''} onClick={() => setActiveTab('dashboard')}>
            <Users size={20} /> Dashboard
          </li>
          <li className={activeTab === 'matches' ? 'active' : ''} onClick={() => setActiveTab('matches')}>
            <Search size={20} /> Find Matches
          </li>
          <li className={activeTab === 'messages' ? 'active' : ''} onClick={() => setActiveTab('messages')}>
            <MessageSquare size={20} /> Messages
          </li>
          <li className={activeTab === 'profile' ? 'active' : ''} onClick={() => setActiveTab('profile')}>
            <UserCircle size={20} /> My Profile
          </li>
          <li className={activeTab === 'bookings' ? 'active' : ''} onClick={() => setActiveTab('bookings')}>
            <Calendar size={20} /> Bookings
          </li>

        </ul>

        <div className="user-profile" onClick={handleLogout} style={{ cursor: 'pointer' }} title="Click to logout">
          <div className="avatar">{user.username?.substring(0, 2).toUpperCase() || 'U'}</div>
          <div className="user-info">
            <p className="user-name">{user.username || 'User'}</p>
            <p className="user-role">{user.email || 'Member'}</p>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="main-content">
        <header className="top-header">
          <div>
            <h1>{activeTab === 'dashboard' ? 'Welcome back,' : ''} <span className="gradient-text">{activeTab === 'dashboard' ? user.username : activeTab.charAt(0).toUpperCase() + activeTab.slice(1)}</span>{activeTab === 'dashboard' ? '!' : ''}</h1>
            <p className="text-muted">
              {activeTab === 'dashboard'
                ? "Here's what's happening on SkillVerse today."
                : "Manage your activity and connections."}
            </p>
          </div>
          <div className="header-actions">
            <button className="btn btn-primary" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </header>

        {renderContent()}
      </main>
    </div>
  );
}

export default App;
