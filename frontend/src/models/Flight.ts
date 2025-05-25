export interface Flight {
  flightId?: number;
  flightCode: string;
  departureTime: string; // ISO string format
  arrivalTime: string; // ISO string format
  planeId: number;
  planeCode?: string;
  planeModel?: string;
  departureAirportId: number;
  departureAirportName?: string;
  departureCityName?: string;
  departureCountryName?: string;
  arrivalAirportId: number;
  arrivalAirportName?: string;
  arrivalCityName?: string;
  arrivalCountryName?: string;
}

export interface FlightSearch {
  departureAirportId: number;
  arrivalAirportId: number;
  departureDate: string; // YYYY-MM-DD format
  passengerCount?: number;
  ticketClassId?: number;
}