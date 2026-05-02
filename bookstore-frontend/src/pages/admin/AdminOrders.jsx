import { useEffect, useState } from 'react';
import { getAllOrders, updateOrderStatus } from '../../api/orderApi';
import toast from 'react-hot-toast';

const statuses = ['PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED'];
const statusColor = {
  PENDING:   'badge-warning',
  CONFIRMED: 'badge-info',
  SHIPPED:   'badge-info',
  DELIVERED: 'badge-success',
  CANCELLED: 'badge-danger'
};

export default function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // ✅ CHANGED: r.data.data
    getAllOrders().then(r => setOrders(r.data.data)).finally(() => setLoading(false));
  }, []);

  const handleStatus = async (id, status) => {
    await updateOrderStatus(id, status);
    setOrders(orders.map(o => o.id === id ? { ...o, status } : o));
    toast.success('Status updated');
  };

  if (loading) return <div className="spinner" />;

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 800, marginBottom: 20 }}>📦 All Orders</h1>
      <div className="card table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Date</th>
              <th>Update Status</th>
            </tr>
          </thead>
          <tbody>
            {orders.map(o => (
              <tr key={o.id}>
                <td>#{o.id}</td>
                <td>{o.address}</td>
                <td style={{ fontWeight: 700 }}>₹{o.totalAmount}</td>
                <td>
                  <span className={`badge ${statusColor[o.status]}`}>
                    {o.status}
                  </span>
                </td>
                <td>{new Date(o.orderedAt).toLocaleDateString()}</td>
                <td>
                  <select
                    style={{ padding: '4px 8px', borderRadius: 6, border: '1px solid var(--border)' }}
                    value={o.status}
                    onChange={e => handleStatus(o.id, e.target.value)}>
                    {statuses.map(s => <option key={s}>{s}</option>)}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}