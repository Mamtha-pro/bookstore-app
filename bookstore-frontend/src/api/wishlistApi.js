import api from './axios';

export const getWishlist = () =>
  api.get('/api/wishlist');

export const addToWishlist = (bookId) =>
  api.post('/api/wishlist/add', { bookId });

export const removeFromWishlist = (id) =>
  api.delete('/api/wishlist/' + id);

export const moveToCart = (id) =>
  api.post('/api/wishlist/' + id + '/move-to-cart');