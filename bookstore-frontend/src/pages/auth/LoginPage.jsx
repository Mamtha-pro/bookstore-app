import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { loginUser } from '../../api/authApi';
import { setCredentials } from '../../features/authSlice';
import toast from 'react-hot-toast';
import { FiBook } from 'react-icons/fi';
import './Auth.css';

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const dispatch  = useDispatch();
  const navigate  = useNavigate();

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await loginUser(form);
      dispatch(setCredentials({
        token: res.data.token,
        role:  res.data.role,
        email: form.email,
        name:  form.email.split('@')[0],
      }));
      toast.success('Welcome back!');
      navigate(res.data.role === 'ADMIN' ? '/admin' : '/books');
    } catch (err) {
      toast.error(err.response?.data?.error || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card card">
        <div className="auth-header">
          <FiBook className="auth-icon" />
          <h2>Welcome Back</h2>
          <p>Sign in to your BookStore account</p>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input
              type="email" name="email"
              value={form.email} onChange={handleChange}
              placeholder="you@example.com" required
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password" name="password"
              value={form.password} onChange={handleChange}
              placeholder="••••••••" required
            />
          </div>
          <button className="btn btn-primary btn-full" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <p className="auth-footer">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}