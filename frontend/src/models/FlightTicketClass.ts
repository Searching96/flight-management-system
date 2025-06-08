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

export interface FlightTicketClassRequest {
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


export interface SeatAvailability {
  ticketClassId: number;
  ticketClassName: string;
  totalSeats: number;
  availableSeats: number;
  bookedSeats: number;
  fare: number;
}

export interface BookSeatRequest {
  flightId: number;
  seatNumber: string;
  passengerId: number;
}

export interface SeatMap {
  [ticketClass: string]: {
    seats: {
      seatNumber: string;
      isBooked: boolean;
      passengerId?: number;
    }[];
  };
}