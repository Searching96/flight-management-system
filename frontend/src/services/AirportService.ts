import { apiClient } from "./api";
import { Airport, ApiResponse, PaginatedResponse } from "../models";

export class AirportService {
  private readonly baseUrl = "/airports";

  async getAllAirports(): Promise<ApiResponse<Airport[]>> {
    return apiClient.get(this.baseUrl);
  }

  async getAllAirportsPaged(
    page: number = 0,
    size: number = 10
  ): Promise<ApiResponse<PaginatedResponse<Airport>>> {
    return apiClient.get(`${this.baseUrl}?page=${page}&size=${size}`);
  }

  async getAirportById(id: number): Promise<ApiResponse<Airport>> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getAirportsByCity(cityName: string): Promise<ApiResponse<Airport[]>> {
    return apiClient.get(`${this.baseUrl}/city/${cityName}`);
  }

  async getAirportsByCountry(
    countryName: string
  ): Promise<ApiResponse<Airport[]>> {
    return apiClient.get(`${this.baseUrl}/country/${countryName}`);
  }

  async searchAirportsByName(name: string): Promise<ApiResponse<Airport[]>> {
    return apiClient.get(`${this.baseUrl}/search/${name}`);
  }

  async createAirport(
    airport: Omit<Airport, "airportId">
  ): Promise<ApiResponse<Airport>> {
    return apiClient.post(this.baseUrl, airport);
  }

  async updateAirport(
    id: number,
    airport: Partial<Airport>
  ): Promise<ApiResponse<Airport>> {
    return apiClient.put(`${this.baseUrl}/${id}`, airport);
  }

  async deleteAirport(id: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }
}

export const airportService = new AirportService();
