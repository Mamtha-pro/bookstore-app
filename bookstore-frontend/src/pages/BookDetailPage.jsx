import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getBookById } from '../api/bookApi';
import { addToCart, getCart } from '../api/cartApi';
import { addToWishlist } from '../api/wishlistApi';
import { getBookReviews, addReview } from '../api/reviewApi';
import { setCart } from '../features/cartSlice';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import { FiShoppingCart, FiHeart, FiStar } from 'react-icons/fi';

export default function BookDetailPage() {
  const { id } = useParams();
  const dispatch = useDispatch();
  const { isAuthenticated } = useSelector((s) => s.auth);

  const [book, setBook]       = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [review, setReview]   = useState({ rating: 5, comment: '' });

  useEffect(() => {
    Promise.all([getBookById(id), getBookReviews(id)])
      .then(([b, r]) => { setBook(b.data); setReviews(r.data); })
      .finally(() => setLoading(false));
  }, [id]);

  const handleAddToCart = async () => {
    if (!isAuthenticated) { toast.error('Please login'); return; }
    await addToCart({ bookId: book.id, quantity: 1 });
    const res = await getCart();
    dispatch(setCart(res.data));
    toast.success('Added to cart!');
  };

  const handleWishlist = async () => {
    if (!isAuthenticated) { toast.error('Please login'); return; }
    await addToWishlist(book.id);
    toast.success('Added to wishlist!');
  };

  const handleReview = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) { toast.error('Please login'); return; }
    try {
      const res = await addReview({ bookId: parseInt(id), ...review });
      setReviews([res.data, ...reviews]);
      setReview({ rating: 5, comment: '' });
      toast.success('Review added!');
    } catch { toast.error('Failed to add review'); }
  };

  if (loading) return <div className="spinner" />;
  if (!book) return <div className="empty-state"><h3>Book not found</h3></div>;

  return (
    <div className="page">
      <div style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: 32, marginBottom: 40 }}>
        <div style={{
          background: '#f3f4f6', borderRadius: 12,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          minHeight: 360, fontSize: 80
        }}>
          {book.imageUrl ? <img src={book.imageUrl} style={{ width: '100%', borderRadius: 12 }} /> : '📚'}
        </div>

        <div>
          <span style={{ color: 'var(--primary)', fontWeight: 700, fontSize: 13 }}>
            {book.category}
          </span>
          <h1 style={{ fontSize: 32, fontWeight: 800, margin: '8px 0 4px' }}>{book.title}</h1>
          <p style={{ color: 'var(--muted)', marginBottom: 16 }}>by {book.author}</p>
          {book.isbn && <p style={{ color: 'var(--muted)', fontSize: 13 }}>ISBN: {book.isbn}</p>}
          <div style={{ fontSize: 36, fontWeight: 900, color: 'var(--primary)', margin: '16px 0' }}>
            ₹{book.price}
          </div>
          <p style={{ lineHeight: 1.7, color: '#374151', marginBottom: 24 }}>{book.description}</p>
          <div style={{ display: 'flex', gap: 12 }}>
            <button className="btn btn-primary" onClick={handleAddToCart}
              disabled={book.stock === 0}>
              <FiShoppingCart /> {book.stock > 0 ? 'Add to Cart' : 'Out of Stock'}
            </button>
            <button className="btn btn-outline" onClick={handleWishlist}>
              <FiHeart /> Wishlist
            </button>
          </div>
        </div>
      </div>

      {/* Reviews */}
      <div className="card">
        <h2 style={{ marginBottom: 20 }}>⭐ Reviews ({reviews.length})</h2>

        {isAuthenticated && (
          <form onSubmit={handleReview} style={{ marginBottom: 24, paddingBottom: 24, borderBottom: '1px solid var(--border)' }}>
            <h3 style={{ marginBottom: 12 }}>Write a Review</h3>
            <div className="form-group">
              <label>Rating (1-5)</label>
              <input type="number" min={1} max={5} value={review.rating}
                onChange={(e) => setReview({ ...review, rating: +e.target.value })} />
            </div>
            <div className="form-group">
              <label>Comment</label>
              <textarea rows={3} value={review.comment}
                onChange={(e) => setReview({ ...review, comment: e.target.value })}
                placeholder="Share your thoughts..." />
            </div>
            <button className="btn btn-primary btn-sm">Submit Review</button>
          </form>
        )}

        {reviews.length === 0
          ? <p style={{ color: 'var(--muted)' }}>No reviews yet. Be the first!</p>
          : reviews.map(r => (
            <div key={r.id} style={{ padding: '14px 0', borderBottom: '1px solid var(--border)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                <strong>{r.userName}</strong>
                <span style={{ color: '#f59e0b' }}>{'⭐'.repeat(r.rating)}</span>
              </div>
              <p style={{ color: '#374151' }}>{r.comment}</p>
            </div>
          ))
        }
      </div>
    </div>
  );
}