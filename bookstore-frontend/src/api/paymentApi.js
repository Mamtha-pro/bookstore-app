import api from './axios';

export const initiatePayment    = (data) => api.post('/api/payments', data);
export const getPaymentByOrder  = (orderId) => api.get(`/api/payments/${orderId}`);
export const getAllPayments      = () => api.get('/api/admin/payments');
export const updatePaymentStatus = (id, status) =>
  api.put(`/api/admin/payments/${id}?status=${status}`);