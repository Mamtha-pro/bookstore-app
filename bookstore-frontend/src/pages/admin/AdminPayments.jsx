import { useEffect, useState } from 'react';
import { getAllPayments } from '../../api/paymentApi';

const statusColor = { PENDING:'badge-warning',SUCCESS:'badge-success',FAILED:'badge-danger',REFUNDED:'badge-info' };

export default function AdminPayments() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading]   = useState(true);

  useEffect(() => {
    getAllPayments().then(r => setPayments(r.data)).finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="spinner" />;

  return (
    <div>
      <h1 style={{ fontSize:24, fontWeight:800, marginBottom:20 }}>💳 All Payments</h1>
      <div className="card table-wrap">
        <table>
          <thead>
            <tr><th>ID</th><th>Order ID</th><th>Amount</th><th>Method</th><th>Status</th><th>Transaction ID</th></tr>
          </thead>
          <tbody>
            {payments.map(p => (
              <tr key={p.id}>
                <td>#{p.id}</td>
                <td>#{p.orderId}</td>
                <td style={{ fontWeight:700 }}>₹{p.amount}</td>
                <td>{p.paymentMethod}</td>
                <td><span className={`badge ${statusColor[p.status]}`}>{p.status}</span></td>
                <td style={{ fontSize:12, color:'var(--muted)' }}>{p.transactionId || '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}