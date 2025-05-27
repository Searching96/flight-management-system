export interface TicketClass {
  ticketClassId?: number;
  ticketClassName: string;
  color: string;
}

export interface TicketClassRequest {
  ticketClassName: string;
  color: string;
}
