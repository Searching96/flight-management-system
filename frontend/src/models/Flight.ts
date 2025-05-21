export interface FlightDto {
  id: number;
  airline: string;
  departureAirportId: number;
  arrivalAirportId: number;
  flightDate: string; // "YYYY-MM-DD"
  flightTime: string; // "HH:mm:ss"
  flightNumber: string;
  duration: number;
}