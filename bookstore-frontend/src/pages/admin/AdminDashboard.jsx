import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';

export default function AdminDashboard() {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/api/admin/dashboard')
      .then(r => setData(r.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const stats = [
    {
      label: 'Total Users',
      value: data?.totalUsers    ?? '—',
      emoji: '👥',
      color: '#3b82f6',
      bg:    '#eff6ff',
      link:  '/admin/users'
    },
    {
      label: 'Total Books',
      value: data?.totalBooks    ?? '—',
      emoji: '📚',
      color: '#8b5cf6',
      bg:    '#f5f3ff',
      link:  '/admin/books'
    },
    {
      label: 'Total Orders',
      value: data?.totalOrders   ?? '—',
      emoji: '📦',
      color: '#f59e0b',
      bg:    '#fffbeb',
      link:  '/admin/orders'
    },
    {
      label: 'Total Revenue',
      value: data?.totalRevenue
               ? '₹' + data.totalRevenue.toFixed(2)
               : '—',
      emoji: '💰',
      color: '#10b981',
      bg:    '#ecfdf5',
      link:  '/admin/payments'
    },
    {
      label: 'Pending Orders',
      value: data?.pendingOrders   ?? '—',
      emoji: '⏳',
      color: '#ef4444',
      bg:    '#fef2f2',
      link:  '/admin/orders'
    },
    {
      label: 'Delivered Orders',
      value: data?.deliveredOrders ?? '—',
      emoji: '✅',
      color: '#22c55e',
      bg:    '#f0fdf4',
      link:  '/admin/orders'
    },
  ];

  if (loading) return <div className="spinner" />;

  return (
    <div>
      {/* ── Header ──────────────────────────────────────────── */}
      <div style={{
        display: 'flex', justifyContent: 'space-between',
        alignItems: 'center', marginBottom: 28
      }}>
        <div>
          <h1 style={{ fontSize: 26, fontWeight: 800 }}>
            Dashboard
          </h1>
          <p style={{ color: 'var(--muted)', fontSize: 14, marginTop: 4 }}>
            Welcome back, Admin! Here's what's happening.
          </p>
        </div>
        <div style={{
          background: '#eff6ff', padding: '8px 16px',
          borderRadius: 8, fontSize: 13, color: '#2563eb', fontWeight: 600
        }}>
          📅 {new Date().toLocaleDateString('en-IN', {
            weekday: 'long', year: 'numeric',
            month: 'long', day: 'numeric'
          })}
        </div>
      </div>

      {/* ── Stat Cards ───────────────────────────────────────── */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(3, 1fr)',
        gap: 16,
        marginBottom: 32
      }}>
        {stats.map(s => (
          <div
            key={s.label}
            onClick={() => navigate(s.link)}
            style={{
              background: 'white',
              borderRadius: 12,
              padding: '20px 24px',
              boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
              cursor: 'pointer',
              border: '2px solid transparent',
              transition: 'all 0.2s',
              display: 'flex',
              alignItems: 'center',
              gap: 16,
            }}
            onMouseEnter={e => {
              e.currentTarget.style.borderColor = s.color;
              e.currentTarget.style.transform = 'translateY(-2px)';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.borderColor = 'transparent';
              e.currentTarget.style.transform = 'translateY(0)';
            }}
          >
            <div style={{
              width: 56, height: 56,
              background: s.bg,
              borderRadius: 12,
              display: 'flex', alignItems: 'center',
              justifyContent: 'center',
              fontSize: 28, flexShrink: 0
            }}>
              {s.emoji}
            </div>
            <div>
              <div style={{
                fontSize: 28, fontWeight: 900, color: s.color
              }}>
                {s.value}
              </div>
              <div style={{
                color: 'var(--muted)', fontSize: 13,
                fontWeight: 600, marginTop: 2
              }}>
                {s.label}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* ── Quick Actions ─────────────────────────────────────── */}
      <div className="card" style={{ marginBottom: 24 }}>
        <h2 style={{ fontSize: 18, fontWeight: 700, marginBottom: 16 }}>
          ⚡ Quick Actions
        </h2>
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(4, 1fr)',
          gap: 12
        }}>
          {[
            { label: 'Add New Book',    emoji: '📚', path: '/admin/books'    },
            { label: 'View All Orders', emoji: '📦', path: '/admin/orders'   },
            { label: 'Manage Users',    emoji: '👥', path: '/admin/users'    },
            { label: 'View Payments',   emoji: '💳', path: '/admin/payments' },
          ].map(action => (
            <button
              key={action.label}
              onClick={() => navigate(action.path)}
              style={{
                background: '#f8fafc',
                border: '1.5px solid var(--border)',
                borderRadius: 10,
                padding: '14px',
                cursor: 'pointer',
                textAlign: 'center',
                transition: 'all 0.2s',
                fontFamily: 'inherit',
              }}
              onMouseEnter={e => {
                e.currentTarget.style.background = '#eff6ff';
                e.currentTarget.style.borderColor = '#2563eb';
              }}
              onMouseLeave={e => {
                e.currentTarget.style.background = '#f8fafc';
                e.currentTarget.style.borderColor = 'var(--border)';
              }}
            >
              <div style={{ fontSize: 28, marginBottom: 6 }}>
                {action.emoji}
              </div>
              <div style={{
                fontSize: 13, fontWeight: 600,
                color: 'var(--text)'
              }}>
                {action.label}
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* ── Summary Table ─────────────────────────────────────── */}
      <div className="card">
        <h2 style={{ fontSize: 18, fontWeight: 700, marginBottom: 16 }}>
          📊 Store Summary
        </h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Metric</th>
                <th>Value</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Total Registered Users</td>
                <td><strong>{data?.totalUsers ?? 0}</strong></td>
                <td>
                  <span className="badge badge-info">Active</span>
                </td>
              </tr>
              <tr>
                <td>Books in Catalogue</td>
                <td><strong>{data?.totalBooks ?? 0}</strong></td>
                <td>
                  <span className="badge badge-success">In Stock</span>
                </td>
              </tr>
              <tr>
                <td>Total Orders Placed</td>
                <td><strong>{data?.totalOrders ?? 0}</strong></td>
                <td>
                  <span className="badge badge-info">All Time</span>
                </td>
              </tr>
              <tr>
                <td>Pending Orders</td>
                <td><strong>{data?.pendingOrders ?? 0}</strong></td>
                <td>
                  <span className="badge badge-warning">Needs Action</span>
                </td>
              </tr>
              <tr>
                <td>Delivered Orders</td>
                <td><strong>{data?.deliveredOrders ?? 0}</strong></td>
                <td>
                  <span className="badge badge-success">Completed</span>
                </td>
              </tr>
              <tr>
                <td>Total Revenue</td>
                <td>
                  <strong style={{ color: '#10b981', fontSize: 16 }}>
                    ₹{data?.totalRevenue?.toFixed(2) ?? '0.00'}
                  </strong>
                </td>
                <td>
                  <span className="badge badge-success">Collected</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}