import { apiClient } from "./api";
import type { MonthlyStatistics, YearlyStatistics } from "../models/Statistics";
import type { ApiResponse } from "../models/ApiResponse";

export class StatisticsService {
  private readonly baseUrl = "/statistics";
  async getYearlyStatistics(): Promise<ApiResponse<YearlyStatistics[]>> {
    return apiClient.get(`${this.baseUrl}/yearly`);
  }

  async getMonthlyStatistics(
    year: number
  ): Promise<ApiResponse<MonthlyStatistics[]>> {
    return apiClient.get(`${this.baseUrl}/monthly/${year}`);
  }
}

export const statisticsService = new StatisticsService();
