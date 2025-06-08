export interface FlightDetail {
  flightId: number;
  mediumAirportId: number;
  mediumAirportName?: string;
  mediumCityName?: string;
  arrivalTime: string;
  layoverDuration: number; // in minutes
}
