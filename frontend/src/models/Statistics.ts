export interface YearlyStatistics {
  year: number;
  totalPassengers: number;
  totalFlights: number;
  totalRevenue: number;
}

export interface MonthlyStatistics {
  month: number;
  year: number;
  totalFlights: number;
  totalRevenue: number;
  totalPassengers: number;
}
