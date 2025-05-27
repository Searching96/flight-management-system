export interface FlightDetail {
  flightId: number;
  mediumAirportId: number;
  mediumAirportName?: string;
  mediumCityName?: string;
  arrivalTime: string;
  layoverDuration: number; // in minutes
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
