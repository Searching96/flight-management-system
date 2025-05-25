export interface Airport {
  airportId?: number;
  airportName: string;
  cityName: string;
  countryName: string;
  deleted_at?: Date | null;
}

export interface AirportSearchResult extends Airport {
  flightCount?: number;
  isPopular?: boolean;
}