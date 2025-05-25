export interface Parameter {
  parameterId?: number;
  maxMediumAirport: number;
  minFlightDuration: number;
  maxLayoverDuration: number;
  minLayoverDuration: number;
  minBookingInAdvanceDuration: number;
  maxBookingHoldDuration: number;
  deleted_at?: Date | null;
}

export interface ParameterUpdate {
  parameterName: string;
  value: number;
  description?: string;
}