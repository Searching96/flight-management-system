export interface FlightDto {
  id: number;
  departureAirportId: number;
  arrivalAirportId: number;
  flightDate: string; // "YYYY-MM-DD"
  flightTime: string; // "HH:mm:ss"
  duration: number;
}