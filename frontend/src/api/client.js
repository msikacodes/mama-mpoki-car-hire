import axios from 'axios';

const API_BASE = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080') + '/api/v1';

const client = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

client.interceptors.response.use(
  (res) => res.data,
  (err) => {
    if (err.response?.status === 401 || err.response?.status === 403) {
      if (window.location.pathname !== '/login') {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
      }
    }
    return Promise.reject(err.response?.data || err);
  }
);

// Auth
export const login = (data) => client.post('/auth/login', data);
export const refreshToken = (data) => client.post('/auth/refresh', data);
export const changePassword = (data) => client.post('/auth/change-password', data);
export const getMe = () => client.get('/auth/me');

// Vehicles
export const getVehicles = (params) => client.get('/vehicles', { params });
export const getVehicle = (id) => client.get(`/vehicles/${id}`);
export const createVehicle = (data) => client.post('/vehicles', data);
export const updateVehicle = (id, data) => client.put(`/vehicles/${id}`, data);
export const deleteVehicle = (id) => client.delete(`/vehicles/${id}`);
export const updateVehicleStatus = (id, status) => client.put(`/vehicles/${id}/status`, null, { params: { status } });
export const getFleetSummary = () => client.get('/vehicles/fleet-summary');

// Drivers
export const getDrivers = (params) => client.get('/drivers', { params });
export const createDriver = (data) => client.post('/drivers', data);
export const updateDriver = (id, data) => client.put(`/drivers/${id}`, data);
export const deleteDriver = (id) => client.delete(`/drivers/${id}`);

// Conductors
export const getConductors = (params) => client.get('/conductors', { params });
export const createConductor = (data) => client.post('/conductors', data);
export const updateConductor = (id, data) => client.put(`/conductors/${id}`, data);
export const deleteConductor = (id) => client.delete(`/conductors/${id}`);

// Customers
export const getCustomers = (params) => client.get('/customers', { params });
export const searchCustomers = (query) => client.get('/customers/search', { params: { query } });
export const createCustomer = (data) => client.post('/customers', data);
export const updateCustomer = (id, data) => client.put(`/customers/${id}`, data);
export const deleteCustomer = (id) => client.delete(`/customers/${id}`);

// Special Hire
export const getBookings = (params) => client.get('/special-hire/bookings', { params });
export const getBooking = (id) => client.get(`/special-hire/bookings/${id}`);
export const createBooking = (data) => client.post('/special-hire/bookings', data);
export const updateBookingStatus = (id, status) => client.put(`/special-hire/bookings/${id}/status`, null, { params: { status } });
export const getBookingFinancials = (id) => client.get(`/special-hire/bookings/${id}/financials`);
export const getPayments = (bookingId) => client.get(`/special-hire/bookings/${bookingId}/payments`);
export const addPayment = (bookingId, data) => client.post(`/special-hire/bookings/${bookingId}/payments`, data);
export const getTrips = (params) => client.get('/special-hire/trips', { params });
export const createTrip = (data) => client.post('/special-hire/trips', data);
export const completeTrip = (id, data) => client.put(`/special-hire/trips/${id}/complete`, data);
export const getTripExpenses = (tripId) => client.get(`/special-hire/trips/${tripId}/expenses`);
export const addTripExpense = (tripId, data) => client.post(`/special-hire/trips/${tripId}/expenses`, data);

// Daladala
export const getRoutes = (params) => client.get('/daladala/routes', { params });
export const createRoute = (data) => client.post('/daladala/routes', data);
export const updateRoute = (id, data) => client.put(`/daladala/routes/${id}`, data);
export const deleteRoute = (id) => client.delete(`/daladala/routes/${id}`);
export const getOperations = (params) => client.get('/daladala/operations', { params });
export const getOperation = (id) => client.get(`/daladala/operations/${id}`);
export const createOperation = (data) => client.post('/daladala/operations', data);
export const completeOperation = (id, params) => client.put(`/daladala/operations/${id}/complete`, null, { params });
export const getRevenues = (opId) => client.get(`/daladala/operations/${opId}/revenues`);
export const addRevenue = (opId, data) => client.post(`/daladala/operations/${opId}/revenues`, data);
export const getExpenses = (opId) => client.get(`/daladala/operations/${opId}/expenses`);
export const addExpense = (opId, data) => client.post(`/daladala/operations/${opId}/expenses`, data);

// Private Cars
export const getPrivateCars = () => client.get('/private-cars');
export const getPrivateCar = (id) => client.get(`/private-cars/${id}`);
export const createPrivateCar = (data) => client.post('/private-cars', data);
export const updatePrivateCar = (id, data) => client.put(`/private-cars/${id}`, data);
export const deletePrivateCar = (id) => client.delete(`/private-cars/${id}`);
export const getFuelRecords = (id) => client.get(`/private-cars/${id}/fuel`);
export const addFuelRecord = (id, data) => client.post(`/private-cars/${id}/fuel`, data);
export const getMaintenanceRecords = (id) => client.get(`/private-cars/${id}/maintenance`);
export const addMaintenanceRecord = (id, data) => client.post(`/private-cars/${id}/maintenance`, data);
export const getExpiringDocs = (days) => client.get('/private-cars/expiring-docs', { params: { days } });

// Dashboard
export const getDashboard = () => client.get('/dashboard/summary');

// Reports
export const getSpecialHireReport = (from, to) => client.get('/reports/special-hire', { params: { from, to } });
export const getDaladalaReport = (from, to) => client.get('/reports/daladala', { params: { from, to } });
export const getExpenseReport = (from, to) => client.get('/reports/expenses', { params: { from, to } });
export const getMonthlySummary = (year, month) => client.get('/reports/monthly-summary', { params: { year, month } });
export const getVehicleProfitability = () => client.get('/reports/vehicle-profitability');

// Report Downloads
const downloadFile = (url, filename) => {
  const token = localStorage.getItem('accessToken');
  return fetch(`${API_BASE}${url}`, {
    headers: { Authorization: `Bearer ${token}` }
  }).then(r => r.blob()).then(blob => {
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = filename;
    a.click();
  });
};
export const downloadReportPdf = (type, params) => downloadFile(`/reports/${type}/pdf?${new URLSearchParams(params)}`, `${type}-report.pdf`);
export const downloadReportExcel = (type, params) => downloadFile(`/reports/${type}/excel?${new URLSearchParams(params)}`, `${type}-report.xlsx`);

export default client;
