import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: false,
});

// ── Add JWT to every request ──────────────────────────────────────
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');

    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }

    // Debug — remove after fixing
    console.log('API Request:', config.method?.toUpperCase(),
      config.url, '| Token:', token ? 'EXISTS' : 'MISSING');

    return config;
  },
  (error) => Promise.reject(error)
);

// ── Handle responses ──────────────────────────────────────────────
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.status,
      error.config?.url);

    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;