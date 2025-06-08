export interface Ticket {
  ticketId?: number;
  flightId?: number;
  ticketClassId?: number;
  bookCustomerId?: number | null; // Optional - only for frequent flyers program
  passengerId?: number;
  seatNumber?: string;
  ticketStatus?: number; // 1: paid, 2: unpaid, 3: canceled
  paymentTime?: string;
  fare?: number;
}

export interface TicketRequest {
  flightId: number;
  ticketClassId: number;
  bookCustomerId: number | null;
  passengerId: number;
  seatNumber: string;
  fare: number;
  paymentTime?: string; // Optional - only for paid tickets
}

export interface BookingRequest {
  flightId: number;
  customerId: number; // Required - only for frequent flyers program
  ticketClassId: number;
  passengers: {
    firstName: string;
    lastName: string;
    email: string;
    citizenId: string;
    phoneNumber: string;
  }[];
  totalFare?: number;
  seatNumbers: string[];
}
