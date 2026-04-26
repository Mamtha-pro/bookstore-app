import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../features/authSlice';
import { FiShoppingCart, FiHeart, FiUser, FiLogOut, FiBook } from 'react-icons/fi';
import { MdAdminPanelSettings } from 'react-icons/md';
import './Navbar.css';

export default function Navbar() {
  const { isAuthenticated, user } = useSelector((s) => s.auth);
  const { items } = useSelector((s) => s.cart);
  const dispatch  = useDispatch();
  const navigate  = useNavigate();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="nav-inner">
        <Link to="/" className="nav-logo">
          <FiBook /> BookStore
        </Link>

        <div className="nav-links">
          <Link to="/books">Books</Link>
          {isAuthenticated && <Link to="/wishlist"><FiHeart /> Wishlist</Link>}
          {isAuthenticated && (
            <Link to="/cart" className="cart-link">
              <FiShoppingCart />
              {items.length > 0 && <span className="cart-badge">{items.length}</span>}
              Cart
            </Link>
          )}
          {user?.role === 'ADMIN' && (
            <Link to="/admin" className="admin-link">
              <MdAdminPanelSettings /> Admin
            </Link>
          )}
        </div>

        <div className="nav-actions">
          {isAuthenticated ? (
            <>
              <Link to="/profile" className="nav-user">
                <FiUser /> {user?.name || 'Profile'}
              </Link>
              <button className="btn btn-outline btn-sm" onClick={handleLogout}>
                <FiLogOut /> Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login"  className="btn btn-outline btn-sm">Login</Link>
              <Link to="/register" className="btn btn-primary btn-sm">Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}