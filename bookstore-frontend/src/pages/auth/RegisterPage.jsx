import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerUser } from '../../api/authApi';
import {
  validateName,
  validateEmail,
  validatePassword
} from '../../utils/validators';
import FormError from '../../components/common/FormError';
import toast from 'react-hot-toast';
import { FiBook, FiEye, FiEyeOff } from 'react-icons/fi';
import './Auth.css';

export default function RegisterPage() {
  const [form, setForm] = useState({
    name: '', email: '', password: '', confirmPassword: ''
  });
  const [errors,  setErrors]  = useState({});
  const [loading, setLoading] = useState(false);
  const [showPwd, setShowPwd] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: null }));
  };

  const validate = () => {
    const newErrors = {};
    const nameErr  = validateName(form.name);
    const emailErr = validateEmail(form.email);
    const passErr  = validatePassword(form.password);

    if (nameErr)  newErrors.name  = nameErr;
    if (emailErr) newErrors.email = emailErr;
    if (passErr)  newErrors.password = passErr;

    if (!form.confirmPassword)
      newErrors.confirmPassword = 'Please confirm your password';
    else if (form.password !== form.confirmPassword)
      newErrors.confirmPassword = 'Passwords do not match';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      await registerUser({
        name:     form.name,
        email:    form.email,
        password: form.password
      });
      toast.success('Account created! Please login.');
      navigate('/login');
    } catch (err) {
      const msg = err.response?.data?.error || 'Registration failed';
      toast.error(msg);
      setErrors({ general: msg });
    } finally {
      setLoading(false);
    }
  };

  // Password strength indicator
  const getStrength = (pwd) => {
    if (!pwd) return { label: '', color: '' };
    if (pwd.length < 6)  return { label: 'Weak',   color: '#ef4444' };
    if (pwd.length < 10) return { label: 'Medium',  color: '#f59e0b' };
    return                      { label: 'Strong',  color: '#22c55e' };
  };
  const strength = getStrength(form.password);

  return (
    <div className="auth-page">
      <div className="auth-card card">
        <div className="auth-header">
          <FiBook className="auth-icon" />
          <h2>Create Account</h2>
          <p>Join BookStore today</p>
        </div>

        {errors.general && (
          <div className="auth-error-banner">⚠ {errors.general}</div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label>Full Name</label>
            <input
              name="name" value={form.name}
              onChange={handleChange}
              placeholder="Your full name"
              className={errors.name ? 'input-error' : ''}
            />
            <FormError message={errors.name} />
          </div>

          <div className="form-group">
            <label>Email</label>
            <input
              type="email" name="email"
              value={form.email} onChange={handleChange}
              placeholder="you@example.com"
              className={errors.email ? 'input-error' : ''}
            />
            <FormError message={errors.email} />
          </div>

          <div className="form-group">
            <label>Password</label>
            <div style={{ position: 'relative' }}>
              <input
                type={showPwd ? 'text' : 'password'}
                name="password"
                value={form.password} onChange={handleChange}
                placeholder="Min 6 characters"
                style={{ paddingRight: 40 }}
                className={errors.password ? 'input-error' : ''}
              />
              <button type="button"
                onClick={() => setShowPwd(!showPwd)}
                style={{
                  position: 'absolute', right: 12,
                  top: '50%', transform: 'translateY(-50%)',
                  background: 'none', border: 'none',
                  cursor: 'pointer', color: 'var(--muted)'
                }}>
                {showPwd ? <FiEyeOff /> : <FiEye />}
              </button>
            </div>
            {form.password && (
              <div style={{ marginTop: 4, display: 'flex', alignItems: 'center', gap: 6 }}>
                <div style={{
                  height: 4, flex: 1, borderRadius: 2,
                  background: '#e5e7eb', overflow: 'hidden'
                }}>
                  <div style={{
                    height: '100%', borderRadius: 2,
                    background: strength.color,
                    width: strength.label === 'Weak' ? '33%'
                         : strength.label === 'Medium' ? '66%' : '100%',
                    transition: 'width 0.3s'
                  }} />
                </div>
                <span style={{ fontSize: 11, color: strength.color }}>
                  {strength.label}
                </span>
              </div>
            )}
            <FormError message={errors.password} />
          </div>

          <div className="form-group">
            <label>Confirm Password</label>
            <input
              type="password" name="confirmPassword"
              value={form.confirmPassword} onChange={handleChange}
              placeholder="Re-enter your password"
              className={errors.confirmPassword ? 'input-error' : ''}
            />
            <FormError message={errors.confirmPassword} />
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-full"
            disabled={loading}>
            {loading ? 'Creating Account...' : 'Register'}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}