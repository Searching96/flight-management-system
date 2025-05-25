import { apiClient } from './api';
import { Airport } from '../models';

export class AirportService {
  private readonly baseUrl = '/airports';

  async getAllAirports(): Promise<Airport[]> {
    return apiClient.get(this.baseUrl);
  }

  async getAirportById(id: number): Promise<Airport> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getAirportsByCity(cityName: string): Promise<Airport[]> {
    return apiClient.get(`${this.baseUrl}/city/${cityName}`);
  }

  async getAirportsByCountry(countryName: string): Promise<Airport[]> {
    return apiClient.get(`${this.baseUrl}/country/${countryName}`);
  }

  async searchAirportsByName(name: string): Promise<Airport[]> {
    return apiClient.get(`${this.baseUrl}/search/${name}`);
  }

  async createAirport(airport: Omit<Airport, 'airportId'>): Promise<Airport> {
    return apiClient.post(this.baseUrl, airport);
  }

  async updateAirport(id: number, airport: Partial<Airport>): Promise<Airport> {
    return apiClient.put(`${this.baseUrl}/${id}`, airport);
  }

  async deleteAirport(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }
}

export const airportService = new AirportService();