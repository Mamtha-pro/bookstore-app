import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';

export default function AdminRoute({ children }) {
  const { isAuthenticated, user } = useSelector(s => s.auth);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // ✅ Case insensitive check
  if (user?.role?.toUpperCase() !== 'ADMIN') {
    return <Navigate to="/" replace />;
  }

  return children;
}