import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config.url.includes('/auth/')) {
      localStorage.removeItem('token');
      localStorage.removeItem('cart');
      window.dispatchEvent(new Event('unauthorized'));
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials) => axiosInstance.post('/auth/login', credentials),
  register: (userData) => axiosInstance.post('/auth/register', userData),
};

export const productAPI = {
  getAll: (brand, size) => {
    const params = {};
    if (brand) params.brand = brand;
    if (size) params.size = size;
    return axiosInstance.get('/products', { params });
  },
  getById: (id) => axiosInstance.get(`/products/${id}`),
  create: (product) => axiosInstance.post('/products', product),
  update: (id, product) => axiosInstance.put(`/products/${id}`, product),
  delete: (id) => axiosInstance.delete(`/products/${id}`),
};

export const orderAPI = {
  getMyOrders: () => axiosInstance.get('/orders/mine'),
  getById: (id) => axiosInstance.get(`/orders/${id}`),
  create: (order) => axiosInstance.post('/orders', order),
};

export default axiosInstance;
