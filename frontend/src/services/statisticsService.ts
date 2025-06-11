import { apiClient } from './api';

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

export class StatisticsService {
   private readonly baseUrl = '/statistics';
   async getYearlyStatistics(): Promise<YearlyStatistics[]> {
      return apiClient.get(`${this.baseUrl}/yearly`);
   }

   async getMonthlyStatistics(year: number): Promise<MonthlyStatistics[]> {
      return apiClient.get(`${this.baseUrl}/monthly/${year}`);
   }
}

export const statisticsService = new StatisticsService();
