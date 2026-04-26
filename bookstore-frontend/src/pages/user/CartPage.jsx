import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { getCart, updateCartItem, removeCartItem, clearCart } from '../../api/cartApi';
import { setCart, clearCartState } from '../../features/cartSlice';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { FiTrash2, FiMinus, FiPlus } from 'react-icons/fi';

export default function CartPage() {
  const dispatch  = useDispatch();
  const navigate  = useNavigate();
  const { items, totalAmount } = useSelector((s) => s.cart);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getCart().then(r => dispatch(setCart(r.data))).finally(() => setLoading(false));
  }, []);

  const handleQty = async (itemId, qty) => {
    if (qty < 1) return;
    await updateCartItem(itemId, qty);
    const r = await getCart();
    dispatch(setCart(r.data));
  };

  const handleRemove = async (itemId) => {
    await removeCartItem(itemId);
    const r = await getCart();
    dispatch(setCart(r.data));
    toast.success('Item removed');
  };

  const handleClear = async () => {
    await clearCart();
    dispatch(clearCartState());
    toast.success('Cart cleared');
  };

  if (loading) return <div className="spinner" />;

  return (
    <div className="page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1 style={{ fontSize: 28, fontWeight: 800 }}>🛒 My Cart</h1>
        {items.length > 0 && (
          <button className="btn btn-danger btn-sm" onClick={handleClear}>
            Clear Cart
          </button>
        )}
      </div>

      {items.length === 0 ? (
        <div className="empty-state">
          <h3>Your cart is empty</h3>
          <p>Browse books and add something!</p>
          <button className="btn btn-primary" style={{ marginTop: 16 }}
            onClick={() => navigate('/books')}>Browse Books</button>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 320px', gap: 24 }}>
          <div>
            {items.map(item => (
              <div key={item.itemId} className="card" style={{ marginBottom: 12, display: 'flex', gap: 16, padding: 16 }}>
                <div style={{ fontSize: 40 }}>📚</div>
                <div style={{ flex: 1 }}>
                  <h3 style={{ marginBottom: 4 }}>{item.bookTitle}</h3>
                  <p style={{ color: 'var(--muted)', fontSize: 13 }}>₹{item.unitPrice} each</p>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <button className="action-btn" onClick={() => handleQty(item.itemId, item.quantity - 1)}>
                    <FiMinus />
                  </button>
                  <span style={{ fontWeight: 700, minWidth: 24, textAlign: 'center' }}>
                    {item.quantity}
                  </span>
                  <button className="action-btn" onClick={() => handleQty(item.itemId, item.quantity + 1)}>
                    <FiPlus />
                  </button>
                  <button className="action-btn" onClick={() => handleRemove(item.itemId)}
                    style={{ color: 'var(--danger)' }}>
                    <FiTrash2 />
                  </button>
                </div>
                <div style={{ fontWeight: 800, fontSize: 18, color: 'var(--primary)', alignSelf: 'center' }}>
                  ₹{item.subtotal}
                </div>
              </div>
            ))}
          </div>

          <div className="card" style={{ height: 'fit-content' }}>
            <h2 style={{ marginBottom: 20 }}>Order Summary</h2>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
              <span>Items ({items.length})</span>
              <span>₹{totalAmount}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 20, fontWeight: 800, fontSize: 18 }}>
              <span>Total</span>
              <span style={{ color: 'var(--primary)' }}>₹{totalAmount}</span>
            </div>
            <button className="btn btn-primary btn-full" onClick={() => navigate('/checkout')}>
              Proceed to Checkout
            </button>
          </div>
        </div>
      )}
    </div>
  );
}