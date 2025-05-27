export interface Parameter {
  id?: number;
  maxMediumAirport: number;
  minFlightDuration: number;
  minLayoverDuration: number;
  maxLayoverDuration: number;
  minBookingInAdvanceDuration: number;
  maxBookingHoldDuration: number;
}

export interface ParameterUpdateRequest {
  maxMediumAirport?: number;
  minFlightDuration?: number;
  minLayoverDuration?: number;
  maxLayoverDuration?: number;
  minBookingInAdvanceDuration?: number;
  maxBookingHoldDuration?: number;
}