import { useEffect, useState } from 'react';
import { getWishlist, removeFromWishlist, moveToCart } from '../../api/wishlistApi';
import { getCart } from '../../api/cartApi';
import { setCart } from '../../features/cartSlice';
import { useDispatch } from 'react-redux';
import toast from 'react-hot-toast';
import { FiTrash2, FiShoppingCart } from 'react-icons/fi';

export default function WishlistPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const dispatch = useDispatch();

  useEffect(() => {
    getWishlist().then(r => setItems(r.data)).finally(() => setLoading(false));
  }, []);

  const handleRemove = async (id) => {
    await removeFromWishlist(id);
    setItems(items.filter(i => i.id !== id));
    toast.success('Removed from wishlist');
  };

  const handleMoveToCart = async (id) => {
    await moveToCart(id);
    const res = await getCart();
    dispatch(setCart(res.data));
    setItems(items.filter(i => i.id !== id));
    toast.success('Moved to cart!');
  };

  if (loading) return <div className="spinner" />;

  return (
    <div className="page">
      <h1 style={{ fontSize: 28, fontWeight: 800, marginBottom: 24 }}>❤️ My Wishlist</h1>
      {items.length === 0
        ? <div className="empty-state"><h3>Wishlist is empty</h3></div>
        : <div className="grid-4">
          {items.map(item => (
            <div key={item.id} className="card" style={{ padding: 16 }}>
              <div style={{ fontSize: 48, textAlign: 'center', marginBottom: 12 }}>📚</div>
              <h3 style={{ marginBottom: 4, fontSize: 15 }}>{item.book?.title}</h3>
              <p style={{ color: 'var(--muted)', fontSize: 13, marginBottom: 12 }}>
                by {item.book?.author}
              </p>
              <p style={{ fontWeight: 800, color: 'var(--primary)', marginBottom: 14 }}>
                ₹{item.book?.price}
              </p>
              <div style={{ display: 'flex', gap: 8 }}>
                <button className="btn btn-primary btn-sm" style={{ flex: 1 }}
                  onClick={() => handleMoveToCart(item.id)}>
                  <FiShoppingCart /> Cart
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => handleRemove(item.id)}>
                  <FiTrash2 />
                </button>
              </div>
            </div>
          ))}
        </div>
      }
    </div>
  );
}