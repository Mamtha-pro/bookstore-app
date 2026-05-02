import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MdMenu, MdClose } from 'react-icons/md';
import api from '../../api/axios';

export default function AdminDashboard() {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/api/admin/dashboard')
      .then(r => setData(r.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const stats = [
    { label: 'Total Users',      value: data?.totalUsers    ?? '—', emoji: '👥', color: '#3b82f6', bg: '#eff6ff', link: '/admin/users'    },
    { label: 'Total Books',      value: data?.totalBooks    ?? '—', emoji: '📚', color: '#8b5cf6', bg: '#f5f3ff', link: '/admin/books'    },
    { label: 'Total Orders',     value: data?.totalOrders   ?? '—', emoji: '📦', color: '#f59e0b', bg: '#fffbeb', link: '/admin/orders'   },
    { label: 'Revenue',          value: data?.totalRevenue  ? '₹' + Number(data.totalRevenue).toLocaleString('en-IN') : '—', emoji: '💰', color: '#10b981', bg: '#ecfdf5', link: '/admin/payments' },
    { label: 'Pending',          value: data?.pendingOrders   ?? '—', emoji: '⏳', color: '#ef4444', bg: '#fef2f2', link: '/admin/orders'   },
    { label: 'Delivered',        value: data?.deliveredOrders ?? '—', emoji: '✅', color: '#22c55e', bg: '#f0fdf4', link: '/admin/orders'   },
  ];

  if (loading) return (
    <div style={{ display: 'flex', justifyContent: 'center', padding: 60 }}>
      <div className="spinner" />
    </div>
  );

  return (
    <div>

      {/* ── Header ─────────────────────────────────────── */}
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 20,
        flexWrap: 'wrap',
        gap: 8
      }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 800, margin: 0 }}>
            📊 Dashboard
          </h1>
          <p style={{ color: '#64748b', fontSize: 13, margin: '4px 0 0' }}>
            Welcome back, Admin!
          </p>
        </div>
        <div style={{
          background: '#eff6ff',
          padding: '6px 14px',
          borderRadius: 20,
          fontSize: 12,
          color: '#2563eb',
          fontWeight: 600,
          whiteSpace: 'nowrap'
        }}>
          📅 {new Date().toLocaleDateString('en-IN', {
            day: 'numeric', month: 'short', year: 'numeric'
          })}
        </div>
      </div>

      {/* ── Stat Cards ─────────────────────────────────── */}
      <div className="dashboard-stats">
        {stats.map(s => (
          <div
            key={s.label}
            onClick={() => navigate(s.link)}
            style={{
              background: 'white',
              borderRadius: 12,
              padding: '16px 14px',
              boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
              cursor: 'pointer',
              border: '2px solid transparent',
              transition: 'all 0.2s',
              display: 'flex',
              alignItems: 'center',
              gap: 12,
            }}
            onMouseEnter={e => {
              e.currentTarget.style.borderColor = s.color;
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.12)';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.borderColor = 'transparent';
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.boxShadow = '0 1px 4px rgba(0,0,0,0.08)';
            }}
          >
            <div style={{
              width: 42, height: 42, minWidth: 42,
              background: s.bg,
              borderRadius: 10,
              display: 'flex', alignItems: 'center',
              justifyContent: 'center',
              fontSize: 20
            }}>
              {s.emoji}
            </div>
            <div style={{ minWidth: 0 }}>
              <div style={{
                fontSize: 20, fontWeight: 900,
                color: s.color, lineHeight: 1.2
              }}>
                {s.value}
              </div>
              <div style={{
                color: '#64748b', fontSize: 11,
                fontWeight: 600, marginTop: 2,
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap'
              }}>
                {s.label}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* ── Quick Actions ───────────────────────────────── */}
      <div style={{
        background: 'white',
        borderRadius: 12,
        padding: '16px',
        marginBottom: 16,
        boxShadow: '0 1px 4px rgba(0,0,0,0.08)'
      }}>
        <h2 style={{ fontSize: 15, fontWeight: 700, marginBottom: 12, margin: '0 0 12px' }}>
          ⚡ Quick Actions
        </h2>
        <div className="dashboard-actions">
          {[
            { label: 'Add Book',    emoji: '📚', path: '/admin/books',    bg: '#eff6ff', color: '#2563eb' },
            { label: 'Orders',     emoji: '📦', path: '/admin/orders',   bg: '#fffbeb', color: '#d97706' },
            { label: 'Users',      emoji: '👥', path: '/admin/users',    bg: '#f0fdf4', color: '#16a34a' },
            { label: 'Payments',   emoji: '💳', path: '/admin/payments', bg: '#fdf4ff', color: '#9333ea' },
          ].map(action => (
            <button
              key={action.label}
              onClick={() => navigate(action.path)}
              style={{
                background: action.bg,
                border: 'none',
                borderRadius: 10,
                padding: '14px 8px',
                cursor: 'pointer',
                textAlign: 'center',
                transition: 'all 0.2s',
                fontFamily: 'inherit',
              }}
              onMouseEnter={e => {
                e.currentTarget.style.transform = 'translateY(-2px)';
                e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.12)';
              }}
              onMouseLeave={e => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.boxShadow = 'none';
              }}
            >
              <div style={{ fontSize: 26, marginBottom: 6 }}>{action.emoji}</div>
              <div style={{ fontSize: 12, fontWeight: 700, color: action.color }}>
                {action.label}
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* ── Store Summary ───────────────────────────────── */}
      <div style={{
        background: 'white',
        borderRadius: 12,
        padding: '16px',
        boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
        marginBottom: 24
      }}>
        <h2 style={{ fontSize: 15, fontWeight: 700, margin: '0 0 12px' }}>
          📊 Store Summary
        </h2>
        <div className="table-wrap">
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
            <thead>
              <tr style={{ background: '#f8fafc' }}>
                <th style={{ textAlign: 'left', padding: '10px 12px', fontWeight: 700, color: '#475569', fontSize: 12 }}>Metric</th>
                <th style={{ textAlign: 'left', padding: '10px 12px', fontWeight: 700, color: '#475569', fontSize: 12 }}>Value</th>
                <th style={{ textAlign: 'left', padding: '10px 12px', fontWeight: 700, color: '#475569', fontSize: 12 }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {[
                { label: 'Total Users',     value: data?.totalUsers     ?? 0, badge: 'badge-info',    status: 'Active'       },
                { label: 'Books',           value: data?.totalBooks     ?? 0, badge: 'badge-success', status: 'In Stock'     },
                { label: 'Total Orders',    value: data?.totalOrders    ?? 0, badge: 'badge-info',    status: 'All Time'     },
                { label: 'Pending Orders',  value: data?.pendingOrders  ?? 0, badge: 'badge-warning', status: 'Needs Action' },
                { label: 'Delivered',       value: data?.deliveredOrders ?? 0, badge: 'badge-success', status: 'Completed'   },
                { label: 'Revenue',         value: '₹' + (data?.totalRevenue?.toLocaleString('en-IN') ?? '0'), badge: 'badge-success', status: 'Collected', highlight: true },
              ].map((row, i) => (
                <tr key={i} style={{ borderTop: '1px solid #f1f5f9' }}>
                  <td style={{ padding: '10px 12px', color: '#374151' }}>{row.label}</td>
                  <td style={{ padding: '10px 12px', fontWeight: 700, color: row.highlight ? '#10b981' : '#1e293b' }}>
                    {row.value}
                  </td>
                  <td style={{ padding: '10px 12px' }}>
                    <span className={`badge ${row.badge}`}>{row.status}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

    </div>
  );
}