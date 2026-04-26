import { NavLink, Outlet } from 'react-router-dom';
import { MdDashboard, MdMenuBook, MdPeople, MdShoppingBag, MdPayment } from 'react-icons/md';
import './AdminLayout.css';

const links = [
  { to: '/admin',          label: 'Dashboard',  icon: <MdDashboard /> },
  { to: '/admin/books',    label: 'Books',       icon: <MdMenuBook /> },
  { to: '/admin/users',    label: 'Users',       icon: <MdPeople /> },
  { to: '/admin/orders',   label: 'Orders',      icon: <MdShoppingBag /> },
  { to: '/admin/payments', label: 'Payments',    icon: <MdPayment /> },
];

export default function AdminLayout() {
  return (
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <div className="admin-brand">⚙️ Admin Panel</div>
        <nav>
          {links.map(l => (
            <NavLink key={l.to} to={l.to} end={l.to === '/admin'}
              className={({ isActive }) => `admin-nav-link ${isActive ? 'active' : ''}`}>
              {l.icon} {l.label}
            </NavLink>
          ))}
        </nav>
      </aside>
      <main className="admin-main"><Outlet /></main>
    </div>
  );
}