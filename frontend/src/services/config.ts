export const DOMAIN_URL_DEFAULT = 'http://localhost:8080';
// process.env.REACT_APP_API_URL || ;

export const API_URL = {
  // Authentication
  AUTH: '/auth',
  LOGIN: '/auth',
  REGISTER: '/auth/register',
  LOGOUT: '/auth/logout',
  REFRESH_TOKEN: '/auth/refresh',

  ACCOUNTS: '/accounts',
  FLIGHTS: '/flights',
  FLIGHT_DETAILS: '/flight-details',
  FLIGHT_TICKET_CLASS: '/flight-ticket-classes',
  AIRPORTS: '/airports',
  PLANES: '/planes',
  TICKETS: '/tickets',
  TICKET_CLASSES: '/ticket-classes',
  PASSENGERS: '/passengers',
  CUSTOMERS: '/customers',
  EMPLOYEES: '/employees',
  CHATBOXES: '/chatboxes',
  MESSAGES: '/messages',
  PARAMETERS: '/parameters',

  // Additional endpoints for demo
  DEMO: '/demo',
  HEALTH: '/demo/health',
  INFO: '/demo/info',

  // Booking endpoints
  BOOKINGS: '/bookings',
  PAYMENTS: '/payments'
} as const;
