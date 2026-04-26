import { Link } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { addToCart } from '../../api/cartApi';
import { getCart } from '../../api/cartApi';
import { setCart } from '../../features/cartSlice';
import { addToWishlist } from '../../api/wishlistApi';
import toast from 'react-hot-toast';
import { FiShoppingCart, FiHeart } from 'react-icons/fi';
import './BookCard.css';

export default function BookCard({ book }) {
  const dispatch = useDispatch();
  const { isAuthenticated } = useSelector((s) => s.auth);

  const handleAddToCart = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) { toast.error('Please login first'); return; }
    try {
      await addToCart({ bookId: book.id, quantity: 1 });
      const res = await getCart();
      dispatch(setCart(res.data));
      toast.success('Added to cart!');
    } catch {
      toast.error('Failed to add to cart');
    }
  };

  const handleWishlist = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) { toast.error('Please login first'); return; }
    try {
      await addToWishlist(book.id);
      toast.success('Added to wishlist!');
    } catch {
      toast.error('Already in wishlist or error');
    }
  };

  return (
    <Link to={`/books/${book.id}`} className="book-card">
      <div className="book-img">
        {book.imageUrl
          ? <img src={book.imageUrl} alt={book.title} />
          : <div className="book-placeholder">📚</div>
        }
        <div className="book-actions">
          <button className="action-btn" onClick={handleAddToCart} title="Add to Cart">
            <FiShoppingCart />
          </button>
          <button className="action-btn" onClick={handleWishlist} title="Wishlist">
            <FiHeart />
          </button>
        </div>
      </div>
      <div className="book-info">
        <p className="book-category">{book.category || 'General'}</p>
        <h3 className="book-title">{book.title}</h3>
        <p className="book-author">by {book.author}</p>
        <div className="book-footer">
          <span className="book-price">₹{book.price}</span>
          <span className={`badge ${book.stock > 0 ? 'badge-success' : 'badge-danger'}`}>
            {book.stock > 0 ? 'In Stock' : 'Out of Stock'}
          </span>
        </div>
      </div>
    </Link>
  );
}