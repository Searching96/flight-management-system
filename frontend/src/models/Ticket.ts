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

// In your models or utils file
export enum TicketStatus {
  PAID = 1,
  UNPAID = 2,
  CANCELLED = 3,
}

export const TicketStatusText: Record<TicketStatus, string> = {
  [TicketStatus.PAID]: 'Paid',
  [TicketStatus.UNPAID]: 'Unpaid',
  [TicketStatus.CANCELLED]: 'Cancelled',
};

