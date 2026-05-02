import api from './axios';

export const getCart = () =>
  api.get('/api/cart');

export const addToCart = (data) =>
  api.post('/api/cart/add', data);

export const updateCartItem = (id, qty) =>
  api.put('/api/cart/items/' + id + '?quantity=' + qty);

export const removeCartItem = (id) =>
  api.delete('/api/cart/items/' + id);

export const clearCart = () =>
  api.delete('/api/cart/clear');