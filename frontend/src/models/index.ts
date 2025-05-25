import { Ticket } from './Ticket';

// Account related
export type { Account, LoginRequest, RegisterRequest, AuthResponse } from './Account';

// Flight related
export type { Flight, FlightSearch } from './Flight';
export type { FlightDetail } from './FlightDetail';

// Airport related
export type { Airport, AirportSearchResult } from './Airport';

// Ticket related
export type { TicketClass, FlightTicketClass } from './TicketClass';
export type { Ticket, TicketRequest } from './Ticket';

// User related
export type { Customer, Employee } from './Customer';

// Chat related
export type { Chatbox, Message, SendMessageRequest } from './Chat';
export { MessageType } from './Chat';

// Plane related
export type { Plane } from './Plane';

// System related
export type { Parameter, ParameterUpdate } from './Parameter';

// Common types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface ApiError {
  error: string;
  message: string;
  timestamp: string;
  status: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface SearchFilters {
  keyword?: string;
  dateFrom?: string;
  dateTo?: string;
  status?: string;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

// Passenger related
export type { Passenger, PassengerRequest } from './Passenger';

// Flight Detail related  
// Demo and utility types
export interface DemoInfo {
  application: string;
  description: string;
  version: string;
  features: string[];
  demo_accounts: Record<string, string>;
}

export interface HealthStatus {
  status: string;
  timestamp: string;
  message: string;
  version: string;
}

// Booking workflow types
export interface BookingDetails {
  bookingReference: string;
  totalAmount: number;
  paymentDeadline: string;
  tickets: Ticket[];
}

export interface PaymentRequest {
  paymentMethod: string;
  cardNumber?: string;
  expiryDate?: string;
  cvv?: string;
  amount: number;
}

export interface PaymentResponse {
  success: boolean;
  transactionId: string;
  message?: string;
}

export * from './LoginResponse';
