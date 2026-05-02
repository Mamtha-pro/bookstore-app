import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { loginUser } from '../../api/authApi';
import { setCredentials } from '../../features/authSlice';
import toast from 'react-hot-toast';
import { FiBook, FiEye, FiEyeOff } from 'react-icons/fi';
import './Auth.css';

export default function LoginPage() {
  const [form,    setForm]    = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [showPwd, setShowPwd] = useState(false);
  const [error,   setError]   = useState('');

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.email || !form.password) {
      setError('Email and password are required');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const res = await loginUser(form);

      // ✅ CHANGED: res.data.data because backend now wraps in ApiResponse
      const { token, role, email, name } = res.data.data;

      dispatch(setCredentials({
        token: token,
        role:  role,
        email: email,       // ✅ real email from backend
        name:  name,        // ✅ real name from backend
      }));

      toast.success('Welcome back!');

      if (role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/books');
      }

    } catch (err) {
      const msg = err.response?.data?.message
              || 'Login failed. Please try again.';
      setError(msg);
      toast.error(msg);
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

        {error && (
          <div className="alert alert-error" style={{ marginBottom: 16 }}>
            ⚠ {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="admin@bookstore.com"
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <div style={{ position: 'relative' }}>
              <input
                type={showPwd ? 'text' : 'password'}
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="••••••••"
                style={{ paddingRight: 42 }}
                required
              />
              <button
                type="button"
                onClick={() => setShowPwd(!showPwd)}
                style={{
                  position: 'absolute', right: 12,
                  top: '50%', transform: 'translateY(-50%)',
                  background: 'none', border: 'none',
                  cursor: 'pointer', color: 'var(--muted)',
                  fontSize: 16
                }}>
                {showPwd ? <FiEyeOff /> : <FiEye />}
              </button>
            </div>
          </div>

          {/* Test credentials hint */}
          <div style={{
            background: '#f0fdf4', border: '1px solid #86efac',
            borderRadius: 8, padding: '8px 12px',
            fontSize: 12, color: '#166534', marginBottom: 14
          }}>
            <strong>Admin:</strong> admin@bookstore.com / admin123<br />
            <strong>User:</strong> user@bookstore.com / user123
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-full"
            disabled={loading}>
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