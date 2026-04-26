import api from './axios';

export const getAllBooks       = (page = 0, size = 12) =>
  api.get(`/api/books?page=${page}&size=${size}`);
export const getBookById      = (id) => api.get(`/api/books/${id}`);
export const searchBooks      = (q, page = 0) =>
  api.get(`/api/books/search?q=${q}&page=${page}`);
export const getBooksByCategory = (name, page = 0) =>
  api.get(`/api/books/category/${name}?page=${page}`);

// Admin
export const createBook  = (data) => api.post('/api/admin/books', data);
export const updateBook  = (id, data) => api.put(`/api/admin/books/${id}`, data);
export const deleteBook  = (id) => api.delete(`/api/admin/books/${id}`);
export const updateStock = (id, stock) =>
  api.patch(`/api/admin/books/${id}/stock?stock=${stock}`);