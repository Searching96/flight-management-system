import { FlightTicketClass } from './FlightTicketClass';

export interface Flight {
  flightId: number;
  flightCode: string;
  departureTime: string; // ISO string format
  arrivalTime: string; // ISO string format
  planeId: number;
  planeCode?: string;
  departureAirportId: number;
  departureAirportName?: string;
  departureCityName?: string;
  arrivalAirportId: number;
  arrivalAirportName?: string;
  arrivalCityName?: string;
  flightTicketClasses?: FlightTicketClass[];
}

export interface FlightRequest {
  flightCode: string;
  departureTime: string;
  arrivalTime: string;
  planeId: number;
  departureAirportId: number;
  arrivalAirportId: number;
}

export interface FlightSearch {
  departureAirportId: number;
  arrivalAirportId: number;
  departureDate: string;
  returnDate?: string;
  passengers: number;
  ticketClassId: number;
  isRoundTrip?: boolean;
}