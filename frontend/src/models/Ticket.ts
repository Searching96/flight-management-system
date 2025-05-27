export interface Ticket {
  ticketId?: number;
  flightId?: number;
  flightCode?: string;
  ticketClassId?: number;
  ticketClassName?: string;
  bookCustomerId?: number | null; // Optional - only for frequent flyers program
  passengerId?: number;
  passengerName?: string;
  seatNumber: string;
  ticketStatus?: number; // 1: paid, 2: unpaid, 3: canceled
  paymentTime?: string;
  fare?: number;
}

export interface TicketRequest {
  flightId: number;
  ticketClassId: number;
  passengerId: number;
  seatNumber: string;
  bookCustomerId?: number | null; // Optional - only for frequent flyers program
}

export interface BookingRequest {
  flightId: number;
  customerId?: number | null; // Optional - only for frequent flyers program
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
