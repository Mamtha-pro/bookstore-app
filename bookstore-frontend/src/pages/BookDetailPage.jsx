import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getBookById } from '../api/bookApi';
import { addToCart, getCart } from '../api/cartApi';
import { addToWishlist } from '../api/wishlistApi';
import { getBookReviews, addReview } from '../api/reviewApi';
import { setCart } from '../features/cartSlice';
import toast from 'react-hot-toast';
import { FiShoppingCart, FiHeart } from 'react-icons/fi';

export default function BookDetailPage() {
  const { id }       = useParams();
  const navigate     = useNavigate();
  const dispatch     = useDispatch();
  const { isAuthenticated } = useSelector(s => s.auth);

  const [book,    setBook]    = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [review,  setReview]  = useState({ rating: 5, comment: '' });
  const [addingCart,     setAddingCart]     = useState(false);
  const [addingWishlist, setAddingWishlist] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [bookRes, reviewRes] = await Promise.all([
          getBookById(id),
          getBookReviews(id)
        ]);
        setBook(bookRes.data);
        setReviews(reviewRes.data || []);
      } catch (err) {
        toast.error('Failed to load book');
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [id]);

  // ── Add to Cart ──────────────────────────────────────────────────
  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      toast.error('Please login first');
      navigate('/login');
      return;
    }

    setAddingCart(true);
    try {
      await addToCart({ bookId: book.id, quantity: 1 });

      // ✅ CHANGED: res.data.data because backend now wraps in ApiResponse
      const cartRes = await getCart();
      dispatch(setCart(cartRes.data.data));

      toast.success('Added to cart!');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to add to cart';
      toast.error(msg);
    } finally {
      setAddingCart(false);
    }
  };

  // ── Add to Wishlist ──────────────────────────────────────────────
  const handleAddToWishlist = async () => {
    if (!isAuthenticated) {
      toast.error('Please login first');
      navigate('/login');
      return;
    }

    setAddingWishlist(true);
    try {
      await addToWishlist(book.id);
      toast.success('Added to wishlist!');
    } catch (err) {
      const msg = err.response?.data?.message || 'Already in wishlist';
      toast.error(msg);
    } finally {
      setAddingWishlist(false);
    }
  };

  // ── Submit Review ─────────────────────────────────────────────────
  const handleReview = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      toast.error('Please login to write a review');
      return;
    }
    try {
      const res = await addReview({
        bookId:  parseInt(id),
        rating:  review.rating,
        comment: review.comment
      });
      setReviews(prev => [res.data, ...prev]);
      setReview({ rating: 5, comment: '' });
      toast.success('Review added!');
    } catch (err) {
      toast.error('Failed to add review');
    }
  };

  if (loading) return <div className="spinner" />;

  if (!book) return (
    <div className="empty-state">
      <h3>Book not found</h3>
    </div>
  );

  return (
    <div className="page">

      {/* ── Book Detail ───────────────────────────────────────── */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: '280px 1fr',
        gap: 32,
        marginBottom: 40
      }}>
        {/* Book Image */}
        <div style={{
          background: '#f3f4f6',
          borderRadius: 12,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: 360,
          fontSize: 80
        }}>
          {book.imageUrl
            ? <img
                src={book.imageUrl}
                alt={book.title}
                style={{ width: '100%', borderRadius: 12, objectFit: 'cover' }}
              />
            : '📚'
          }
        </div>

        {/* Book Info */}
        <div>
          <span style={{
            color: 'var(--primary)',
            fontWeight: 700,
            fontSize: 13,
            textTransform: 'uppercase',
            letterSpacing: '0.5px'
          }}>
            {book.category}
          </span>

          <h1 style={{
            fontSize: 30,
            fontWeight: 800,
            margin: '8px 0 4px',
            lineHeight: 1.3
          }}>
            {book.title}
          </h1>

          <p style={{ color: 'var(--muted)', marginBottom: 8 }}>
            by {book.author}
          </p>

          {book.isbn && (
            <p style={{ color: 'var(--muted)', fontSize: 13, marginBottom: 8 }}>
              ISBN: {book.isbn}
            </p>
          )}

          <div style={{
            fontSize: 36,
            fontWeight: 900,
            color: 'var(--primary)',
            margin: '16px 0'
          }}>
            ₹{book.price}
          </div>

          <span className={`badge ${book.stock > 0
            ? 'badge-success' : 'badge-danger'}`}
            style={{ marginBottom: 16, display: 'inline-block' }}>
            {book.stock > 0 ? `In Stock (${book.stock})` : 'Out of Stock'}
          </span>

          <p style={{
            lineHeight: 1.8,
            color: '#374151',
            marginBottom: 24,
            fontSize: 15
          }}>
            {book.description}
          </p>

          <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
            <button
              className="btn btn-primary"
              onClick={handleAddToCart}
              disabled={book.stock === 0 || addingCart}
            >
              <FiShoppingCart />
              {addingCart ? 'Adding...'
                : book.stock > 0 ? 'Add to Cart' : 'Out of Stock'}
            </button>

            <button
              className="btn btn-outline"
              onClick={handleAddToWishlist}
              disabled={addingWishlist}
            >
              <FiHeart />
              {addingWishlist ? 'Adding...' : 'Add to Wishlist'}
            </button>
          </div>
        </div>
      </div>

      {/* ── Reviews ───────────────────────────────────────────── */}
      <div className="card">
        <h2 style={{ marginBottom: 20 }}>
          ⭐ Reviews ({reviews.length})
        </h2>

        {/* Write Review Form */}
        {isAuthenticated && (
          <form
            onSubmit={handleReview}
            style={{
              marginBottom: 24,
              paddingBottom: 24,
              borderBottom: '1px solid var(--border)'
            }}
          >
            <h3 style={{ marginBottom: 12, fontSize: 16 }}>
              Write a Review
            </h3>
            <div className="form-group">
              <label>Rating (1–5)</label>
              <select
                value={review.rating}
                onChange={e =>
                  setReview(prev => ({
                    ...prev, rating: parseInt(e.target.value)
                  }))
                }
              >
                {[5, 4, 3, 2, 1].map(n => (
                  <option key={n} value={n}>
                    {'⭐'.repeat(n)} — {n} star{n > 1 ? 's' : ''}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Comment</label>
              <textarea
                rows={3}
                value={review.comment}
                onChange={e =>
                  setReview(prev => ({ ...prev, comment: e.target.value }))
                }
                placeholder="Share your thoughts about this book..."
              />
            </div>
            <button type="submit" className="btn btn-primary btn-sm">
              Submit Review
            </button>
          </form>
        )}

        {/* Review List */}
        {reviews.length === 0 ? (
          <p style={{ color: 'var(--muted)' }}>
            No reviews yet. Be the first to review!
          </p>
        ) : (
          reviews.map(r => (
            <div
              key={r.id}
              style={{
                padding: '14px 0',
                borderBottom: '1px solid var(--border)'
              }}
            >
              <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                marginBottom: 6
              }}>
                <strong style={{ fontSize: 14 }}>{r.userName}</strong>
                <span style={{ color: '#f59e0b', fontSize: 14 }}>
                  {'⭐'.repeat(r.rating)}
                </span>
              </div>
              <p style={{ color: '#374151', fontSize: 14 }}>
                {r.comment}
              </p>
              <p style={{ color: 'var(--muted)', fontSize: 12, marginTop: 4 }}>
                {r.createdAt
                  ? new Date(r.createdAt).toLocaleDateString()
                  : ''}
              </p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}