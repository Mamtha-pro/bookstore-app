import api from './axios';

export const getMyProfile   = () => api.get('/api/users/me');
export const updateProfile  = (data) => api.put('/api/users/me', data);
export const changePassword = (data) => api.put('/api/users/me/password', data);

// Admin
export const getAllUsers  = () => api.get('/api/admin/users');
export const getUserById  = (id) => api.get(`/api/admin/users/${id}`);
export const deleteUser   = (id) => api.delete(`/api/admin/users/${id}`);
export const getDashboard = () => api.get('/api/admin/dashboard');