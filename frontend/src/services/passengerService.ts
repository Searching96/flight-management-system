import { apiClient } from './api';
import { Passenger } from '../models';

export class PassengerService {
  private readonly baseUrl = '/passengers';

  async getAllPassengers(): Promise<Passenger[]> {
    return apiClient.get(this.baseUrl);
  }

  async getPassengerById(id: number): Promise<Passenger> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getPassengerByCitizenId(citizenId: string): Promise<Passenger> {
    return apiClient.get(`${this.baseUrl}/citizen-id/${citizenId}`);
  }

  async getPassengersByEmail(email: string): Promise<Passenger[]> {
    return apiClient.get(`${this.baseUrl}/email/${email}`);
  }

  async searchPassengersByName(name: string): Promise<Passenger[]> {
    return apiClient.get(`${this.baseUrl}/search/${name}`);
  }

  async createPassenger(passenger: Omit<Passenger, 'passengerId'>): Promise<Passenger> {
    return apiClient.post(this.baseUrl, passenger);
  }

  async updatePassenger(id: number, passenger: Partial<Passenger>): Promise<Passenger> {
    return apiClient.put(`${this.baseUrl}/${id}`, passenger);
  }

  async deletePassenger(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }
}

export const passengerService = new PassengerService();
