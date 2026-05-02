import { useState } from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import {
  MdDashboard, MdMenuBook, MdPeople,
  MdShoppingBag, MdPayment, MdMenu, MdClose
} from 'react-icons/md';
import './AdminLayout.css';

const links = [
  { to: '/admin',          label: 'Dashboard', icon: <MdDashboard />   },
  { to: '/admin/books',    label: 'Books',     icon: <MdMenuBook />    },
  { to: '/admin/users',    label: 'Users',     icon: <MdPeople />      },
  { to: '/admin/orders',   label: 'Orders',    icon: <MdShoppingBag /> },
  { to: '/admin/payments', label: 'Payments',  icon: <MdPayment />     },
];

export default function AdminLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const closeSidebar = () => setSidebarOpen(false);

  return (
    <div className="admin-layout">

      {/* ── Mobile Top Bar ─────────────────────────────── */}
      <div className="admin-topbar" style={{ display: 'none' }}>
        <button
          className="admin-menu-btn"
          onClick={() => setSidebarOpen(!sidebarOpen)}>
          {sidebarOpen ? <MdClose /> : <MdMenu />}
        </button>
        <span style={{ fontWeight: 800, fontSize: 16 }}>⚙️ Admin Panel</span>
      </div>

      {/* ── Dark overlay ───────────────────────────────── */}
      <div
        className={`admin-overlay ${sidebarOpen ? 'open' : ''}`}
        onClick={closeSidebar}
      />

      {/* ── Sidebar ────────────────────────────────────── */}
      <aside className={`admin-sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="admin-brand">⚙️ Admin Panel</div>

        <a href="/books" style={{
          display: 'flex',
          alignItems: 'center',
          gap: 8,
          padding: '8px 20px 12px',
          color: '#94a3b8',
          fontSize: 13,
          textDecoration: 'none',
          borderBottom: '1px solid rgba(255,255,255,0.1)',
          marginBottom: 8
        }}>
          ← Back to Store
        </a>
        <nav>
          {links.map(l => (
            <NavLink
              key={l.to}
              to={l.to}
              end={l.to === '/admin'}
              className={({ isActive }) =>
                `admin-nav-link ${isActive ? 'active' : ''}`}
              onClick={closeSidebar}>
              {l.icon} {l.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      {/* ── Main content ───────────────────────────────── */}
      <main className="admin-main">
        {/* Mobile hamburger inside main — shows above content */}
        <div style={{
          display: 'none',
          marginBottom: 16
        }} className="mobile-header">
          <button
            className="admin-menu-btn"
            onClick={() => setSidebarOpen(!sidebarOpen)}
            style={{ marginBottom: 0 }}>
            {sidebarOpen ? <MdClose /> : <MdMenu />}
          </button>
        </div>
        <Outlet />
      </main>
    </div>
  );
}