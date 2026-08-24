import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Logo from '../assets/Logo';
import {
  LayoutDashboard, Car, Users, UserCheck, Contact, BookOpen,
  Route, Bus, LogOut, Menu,
  ClipboardList, Fuel, BarChart3, Bell
} from 'lucide-react';

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(() => {
    try { return localStorage.getItem('sidebar-collapsed') === 'true'; } catch { return false; }
  });

  const toggleCollapse = () => {
    const next = !collapsed;
    setCollapsed(next);
    try { localStorage.setItem('sidebar-collapsed', next); } catch {}
  };

  const handleMenuClick = () => toggleCollapse();

  const handleLogout = () => { logout(); navigate('/login'); };

  const NavLinkItem = ({ to, icon: Icon, children }) => (
    <NavLink to={to} end className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`} title={collapsed ? children : undefined}>
      <Icon size={18} /> <span className="sidebar-link-text">{children}</span>
    </NavLink>
  );

  return (
    <div className="app-layout">
      <aside className={`sidebar ${collapsed ? 'collapsed' : ''}`}>
        <div className="sidebar-header">
          <div className="sidebar-brand">
            <Logo size={38} />
          </div>
          <div className="sidebar-brand-text">
            <h1>MAMA MPOKI</h1>
            <small>Car Hire System</small>
          </div>
        </div>

        <nav className="sidebar-nav">
          <div className="sidebar-section">Overview</div>
          <NavLinkItem to="/" icon={LayoutDashboard}>Dashboard</NavLinkItem>

          <div className="sidebar-section">Fleet Management</div>
          <NavLinkItem to="/vehicles" icon={Car}>Vehicles</NavLinkItem>
          <NavLinkItem to="/drivers" icon={Users}>Drivers</NavLinkItem>
          <NavLinkItem to="/conductors" icon={UserCheck}>Conductors</NavLinkItem>
          <NavLinkItem to="/customers" icon={Contact}>Customers</NavLinkItem>

          <div className="sidebar-section">Special Hire</div>
          <NavLinkItem to="/bookings" icon={BookOpen}>Bookings</NavLinkItem>
          <NavLinkItem to="/trips" icon={ClipboardList}>Trips</NavLinkItem>

          <div className="sidebar-section">Daladala</div>
          <NavLinkItem to="/routes" icon={Route}>Routes</NavLinkItem>
          <NavLinkItem to="/operations" icon={Bus}>Operations</NavLinkItem>

          <div className="sidebar-section">Private</div>
          <NavLinkItem to="/private-cars" icon={Fuel}>My Cars</NavLinkItem>

          <div className="sidebar-section">Analytics</div>
          <NavLinkItem to="/reports" icon={BarChart3}>Reports</NavLinkItem>
        </nav>

        <div className="sidebar-footer">
          <button onClick={handleLogout} title={collapsed ? 'Sign Out' : undefined}>
            <LogOut size={14} /> <span className="sidebar-link-text">Sign Out</span>
          </button>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div className="topbar-left">
            <button className="menu-btn" onClick={handleMenuClick} title={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}>
              <Menu size={20} />
            </button>
          </div>
          <div className="topbar-right">
            <button className="btn btn-icon btn-ghost" style={{ position: 'relative' }}>
              <Bell size={18} />
            </button>
            <div className="topbar-user">
              <div className="topbar-user-name">
                <span>{user?.fullName || user?.username}</span>
                <small>Owner</small>
              </div>
              <div className="topbar-avatar">{(user?.fullName || 'O')[0].toUpperCase()}</div>
            </div>
          </div>
        </header>
        <div className="page-content">
          {children}
        </div>
      </main>
    </div>
  );
}
