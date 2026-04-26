import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Provider } from 'react-redux';
import { Toaster } from 'react-hot-toast';
import { store } from './app/store';

import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';
import ProtectedRoute from './routes/ProtectedRoute';
import AdminRoute from './routes/AdminRoute';

import LoginPage    from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import BooksPage    from './pages/BooksPage';
import BookDetailPage from './pages/BookDetailPage';
import CartPage     from './pages/user/CartPage';
import CheckoutPage from './pages/user/CheckoutPage';
import OrdersPage   from './pages/user/OrdersPage';
import WishlistPage from './pages/user/WishlistPage';
import ProfilePage  from './pages/user/ProfilePage';

import AdminLayout    from './pages/admin/AdminLayout';
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminBooks     from './pages/admin/AdminBooks';
import AdminOrders    from './pages/admin/AdminOrders';
import AdminUsers     from './pages/admin/AdminUsers';
import AdminPayments  from './pages/admin/AdminPayments';

export default function App() {
  return (
    <Provider store={store}>
      <BrowserRouter>
        <Toaster position="top-right" />
        <Navbar />

        <Routes>
          {/* Public */}
          <Route path="/"        element={<BooksPage />} />
          <Route path="/books"   element={<BooksPage />} />
          <Route path="/books/:id" element={<BookDetailPage />} />
          <Route path="/login"    element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* User protected */}
          <Route path="/cart"     element={<ProtectedRoute><CartPage /></ProtectedRoute>} />
          <Route path="/checkout" element={<ProtectedRoute><CheckoutPage /></ProtectedRoute>} />
          <Route path="/orders"   element={<ProtectedRoute><OrdersPage /></ProtectedRoute>} />
          <Route path="/wishlist" element={<ProtectedRoute><WishlistPage /></ProtectedRoute>} />
          <Route path="/profile"  element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />

          {/* Admin protected */}
          <Route path="/admin" element={<AdminRoute><AdminLayout /></AdminRoute>}>
            <Route index          element={<AdminDashboard />} />
            <Route path="books"   element={<AdminBooks />} />
            <Route path="orders"  element={<AdminOrders />} />
            <Route path="users"   element={<AdminUsers />} />
            <Route path="payments" element={<AdminPayments />} />
          </Route>
        </Routes>

        <Footer />
      </BrowserRouter>
    </Provider>
  );
}