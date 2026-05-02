import { useEffect, useState } from 'react';
import { getAllUsers, deleteUser } from '../../api/userApi';
import toast from 'react-hot-toast';
import { FiTrash2 } from 'react-icons/fi';

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // ✅ CHANGED: r.data.data
    getAllUsers().then(r => setUsers(r.data.data)).finally(() => setLoading(false));
  }, []);

  const handleDelete = async (id) => {
    if (!confirm('Delete this user?')) return;
    await deleteUser(id);
    setUsers(users.filter(u => u.id !== id));
    toast.success('User deleted');
  };

  if (loading) return <div className="spinner" />;

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 800, marginBottom: 20 }}>👥 All Users</h1>
      <div className="card table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Joined</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id}>
                <td>#{u.id}</td>
                <td style={{ fontWeight: 600 }}>{u.name}</td>
                <td>{u.email}</td>
                <td>
                  <span className={`badge ${u.role === 'ADMIN' ? 'badge-danger' : 'badge-info'}`}>
                    {u.role}
                  </span>
                </td>
                <td>{u.createdAt ? new Date(u.createdAt).toLocaleDateString() : '—'}</td>
                <td>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(u.id)}>
                    <FiTrash2 />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}