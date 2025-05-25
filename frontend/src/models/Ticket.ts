export interface Ticket {
  ticketId?: number;
  flightId: number;
  flightCode?: string;
  passengerId: number;
  passengerName?: string;
  customerId: number;
  customerName?: string;
  ticketClassId: number;
  ticketClassName?: string;
  seatNumber: string;
  ticketStatus: 'Confirmed' | 'Cancelled' | 'Pending';
  departureTime?: string;
  arrivalTime?: string;
  departureCityName?: string;
  arrivalCityName?: string;
  departureAirportName?: string;
  arrivalAirportName?: string;
}

export interface TicketRequest {
  flightId: number;
  passengerId: number;
  customerId: number;
  ticketClassId: number;
  seatNumber: string;
  ticketStatus: string;
}
