export interface FlightTicketClass {
  flightId: number;
  ticketClassId: number;
  ticketQuantity: number;
  remainingTicketQuantity: number;
  specifiedFare: number;
  
  // Extended properties from joins
  ticketClassName?: string;
  color?: string;
  flightCode?: string;
  isAvailable?: boolean;
  
  // Navigation properties
  flight?: any;
  ticketClass?: any;
}

export interface FlightTicketClassId {
  flightId: number;
  ticketClassId: number;
}

export interface CreateFlightTicketClassRequest {
  flightId: number;
  ticketClassId: number;
  ticketQuantity: number;
  remainingTicketQuantity: number;
  specifiedFare: number;
}

export interface UpdateFlightTicketClassRequest {
  ticketQuantity?: number;
  remainingTicketQuantity?: number;
  specifiedFare?: number;
}

// Interface for bulk assignment operations
export interface FlightTicketClassAssignment {
  ticketClassId: number;
  ticketQuantity: number;
  remainingTicketQuantity?: number;
  specifiedFare: number;
}
