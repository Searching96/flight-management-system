export const DOMAIN_URL_DEFAULT = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const API_URL = {
  // Authentication
  AUTH: '/api/auth',
  LOGIN: '/api/accounts/login',
  REGISTER: '/api/accounts/register',
  LOGOUT: '/api/auth/logout',
  REFRESH_TOKEN: '/api/auth/refresh',

  ACCOUNTS: '/api/accounts',
  FLIGHTS: '/api/flights',
  FLIGHT_DETAILS: '/api/flight-details',
  FLIGHT_TICKET_CLASS: '/api/flight-ticket-classes',
  AIRPORTS: '/api/airports',
  PLANES: '/api/planes',
  TICKETS: '/api/tickets',
  TICKET_CLASSES: '/api/ticket-classes',
  PASSENGERS: '/api/passengers',
  CUSTOMERS: '/api/customers',
  EMPLOYEES: '/api/employees',
  CHATBOXES: '/api/chatboxes',
  MESSAGES: '/api/messages',
  PARAMETERS: '/api/parameters',

  // Additional endpoints for demo
  DEMO: '/api/demo',
  HEALTH: '/api/demo/health',
  INFO: '/api/demo/info',

  // Booking endpoints
  BOOKINGS: '/api/bookings',
  PAYMENTS: '/api/payments'
} as const;
