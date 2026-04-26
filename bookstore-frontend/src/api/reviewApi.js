import api from './axios';

export const addReview       = (data) => api.post('/api/reviews', data);
export const getBookReviews  = (bookId) => api.get(`/api/reviews/book/${bookId}`);
export const updateReview    = (id, data) => api.put(`/api/reviews/${id}`, data);
export const deleteReview    = (id) => api.delete(`/api/reviews/${id}`);
export const adminDeleteReview = (id) => api.delete(`/api/admin/reviews/${id}`);