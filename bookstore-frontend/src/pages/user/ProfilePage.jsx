import { useState, useEffect } from 'react';
import { getMyProfile, updateProfile } from '../../api/userApi';
import toast from 'react-hot-toast';

export default function ProfilePage() {
  const [form, setForm]     = useState({ name: '', email: '' });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyProfile().then(r => setForm({ name: r.data.name, email: r.data.email }))
      .finally(() => setLoading(false));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await updateProfile(form);
      toast.success('Profile updated!');
    } catch { toast.error('Update failed'); }
  };

  if (loading) return <div className="spinner" />;

  return (
    <div className="page" style={{ maxWidth: 500 }}>
      <h1 style={{ fontSize: 28, fontWeight: 800, marginBottom: 24 }}>👤 My Profile</h1>
      <form className="card" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Name</label>
          <input value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Email</label>
          <input type="email" value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })} />
        </div>
        <button className="btn btn-primary">Save Changes</button>
      </form>
    </div>
  );
}