export interface Airport {
  airportId?: number;
  airportName: string;
  cityName: string;
  countryName: string;
}

export interface AirportSearchResult extends Airport {
  flightCount?: number;
  isPopular?: boolean;
}