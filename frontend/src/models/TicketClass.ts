export interface TicketClass {
  ticketClassId?: number;
  ticketClassName: string;
  classFareRate: number;
}

export interface FlightTicketClass {
  flightId: number;
  ticketClassId: number;
  ticketClassName?: string;
  specifiedFare: number;
  ticketQuantity: number;
  remainingTickets?: number;
}
