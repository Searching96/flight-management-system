import { Flight } from './Flight';
import { TicketClass } from './TicketClass';
import { Customer } from './Customer';

export interface Ticket {
  ticketId?: number;
  customerId: number;
  flightId: number;
  ticketClassId: number;
  bookingDate: string;
  ticketPrice: number;
  status: TicketStatus;

  // Related entities (populated)
  customer?: Customer;
  flight?: Flight;
  ticketClass?: TicketClass;

  // Metadata
  createdAt?: string;
  updatedAt?: string;
}

export enum TicketStatus {
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  PENDING = 'PENDING',
  CHECKED_IN = 'CHECKED_IN',
  NO_SHOW = 'NO_SHOW'
}

export interface TicketRequest {
  customerId: number;
  flightId: number;
  ticketClassId: number;
  ticketPrice: number;
  status?: TicketStatus;
}

export interface TicketFilters {
  status?: TicketStatus;
  flightId?: number;
  customerId?: number;
  ticketClassId?: number;
  dateRange?: {
    from?: string;
    to?: string;
  };
}
