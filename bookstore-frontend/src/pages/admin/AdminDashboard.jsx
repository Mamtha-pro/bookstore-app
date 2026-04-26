import { useEffect, useState } from 'react';
import { getDashboard } from '../../api/userApi';

export default function AdminDashboard() {
  const [data, setData] = useState(null);

  useEffect(() => {
    getDashboard().then(r => setData(r.data)).catch(() => {});
  }, []);

  const stats = [
    { label: 'Total Users',   value: data?.totalUsers   ?? '—', emoji: '👥' },
    { label: 'Total Books',   value: data?.totalBooks   ?? '—', emoji: '📚' },
    { label: 'Total Orders',  value: data?.totalOrders  ?? '—', emoji: '📦' },
    { label: 'Total Revenue', value: data?.totalRevenue ? `₹${data.totalRevenue}` : '—', emoji: '💰' },
  ];

  return (
    <div>
      <h1 style={{ fontSize: 26, fontWeight: 800, marginBottom: 24 }}>Dashboard</h1>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: 16 }}>
        {stats.map(s => (
          <div key={s.label} className="card" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 36, marginBottom: 8 }}>{s.emoji}</div>
            <div style={{ fontSize: 28, fontWeight: 900, color: 'var(--primary)' }}>{s.value}</div>
            <div style={{ color: 'var(--muted)', fontSize: 14 }}>{s.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}