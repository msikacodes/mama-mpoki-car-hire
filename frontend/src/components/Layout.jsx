import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, Car, Users, UserCheck, Contact, BookOpen,
  Route, Bus, FileText, LogOut, Menu, X, ChevronRight,
  ClipboardList, Fuel, BarChart3, Settings, Bell
} from 'lucide-react';

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => { logout(); navigate('/login'); };
  const closeSidebar = () => setSidebarOpen(false);

  const NavLinkItem = ({ to, icon: Icon, children }) => (
    <NavLink to={to} end className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`} onClick={closeSidebar}>
      <Icon size={18} /> {children}
    </NavLink>
  );

  return (
    <div className="app-layout">
      {/* Mobile overlay */}
      <div className={`sidebar-overlay ${sidebarOpen ? 'open' : ''}`} onClick={closeSidebar} />

      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-header">
          <div className="sidebar-brand">
            <Car size={20} />
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
          <button onClick={handleLogout}>
            <LogOut size={14} /> Sign Out
          </button>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div className="topbar-left">
            <button className="menu-btn mobile-menu-btn" onClick={() => setSidebarOpen(true)}>
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
