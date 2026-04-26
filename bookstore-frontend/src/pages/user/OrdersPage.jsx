import { useEffect, useState } from 'react';
import { getMyOrders, cancelOrder } from '../../api/orderApi';
import toast from 'react-hot-toast';

const statusColor = {
  PENDING: 'badge-warning', CONFIRMED: 'badge-info',
  SHIPPED: 'badge-info', DELIVERED: 'badge-success', CANCELLED: 'badge-danger'
};

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyOrders().then(r => setOrders(r.data)).finally(() => setLoading(false));
  }, []);

  const handleCancel = async (id) => {
    try {
      await cancelOrder(id);
      setOrders(orders.map(o => o.id === id ? { ...o, status: 'CANCELLED' } : o));
      toast.success('Order cancelled');
    } catch { toast.error('Cannot cancel this order'); }
  };

  if (loading) return <div className="spinner" />;

  return (
    <div className="page">
      <h1 style={{ fontSize: 28, fontWeight: 800, marginBottom: 24 }}>📦 My Orders</h1>
      {orders.length === 0
        ? <div className="empty-state"><h3>No orders yet</h3></div>
        : orders.map(order => (
          <div key={order.id} className="card" style={{ marginBottom: 16 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
              <div>
                <span style={{ fontWeight: 700, fontSize: 16 }}>Order #{order.id}</span>
                <span className={`badge ${statusColor[order.status]}`} style={{ marginLeft: 10 }}>
                  {order.status}
                </span>
              </div>
              <div style={{ fontWeight: 800, fontSize: 18, color: 'var(--primary)' }}>
                ₹{order.totalAmount}
              </div>
            </div>
            <p style={{ color: 'var(--muted)', fontSize: 13, marginBottom: 12 }}>
              📍 {order.address} &nbsp;|&nbsp; 🕐 {new Date(order.orderedAt).toLocaleDateString()}
            </p>
            <div style={{ marginBottom: 12 }}>
              {order.items?.map((item, i) => (
                <span key={i} style={{ fontSize: 13, color: '#374151', marginRight: 16 }}>
                  📚 {item.bookTitle} x{item.quantity}
                </span>
              ))}
            </div>
            {order.status === 'PENDING' && (
              <button className="btn btn-danger btn-sm" onClick={() => handleCancel(order.id)}>
                Cancel Order
              </button>
            )}
          </div>
        ))
      }
    </div>
  );
}