import { apiClient } from './api';
import {Passenger, CreatePassengerRequest, UpdatePassengerRequest} from '../models/index';
import { get } from 'lodash';

class PassengerService {
  async getAllPassengers(): Promise<Passenger[]> {
    return apiClient.get('/passengers');
  }

  async getPassengerById(id: number): Promise<Passenger> {
    return apiClient.get(`/passengers/${id}`);
  }

  async getPassengerByCitizenId(citizenId: string): Promise<Passenger> {
    return apiClient.get(`/passengers/citizen-id/${citizenId}`);
  }

  async createPassenger(data: CreatePassengerRequest): Promise<Passenger> {
    return apiClient.post('/passengers', data);
  }

  async updatePassenger(id: number, data: UpdatePassengerRequest): Promise<Passenger> {
    return apiClient.put(`/passengers/${id}`, data);
  }

  async deletePassenger(id: number): Promise<void> {
    return apiClient.delete(`/passengers/${id}`);
  }
  async searchPassengersByName(name: string): Promise<Passenger[]> {
    return apiClient.get(`/passengers/search/${encodeURIComponent(name)}`);
  }
  async getPassengersByEmail(email: string): Promise<Passenger[]> {
    return apiClient.get(`/passengers/email/${encodeURIComponent(email)}`);
  }

  /**
   * Search for existing passenger by citizen ID for guest bookings
   * Returns passenger if found, null if not found (for creating new passenger)
   */
  async findExistingPassenger(citizenId: string): Promise<Passenger | null> {
    return await this.getPassengerByCitizenId(citizenId)
      .then(response => response)
      .catch(error => {
        if (error?.response?.status === 404) {
          return null; // Passenger not found
        }
        throw error; // Rethrow other errors
      });
  }

  /**
   * Create or update passenger for guest booking
   * Checks if passenger exists by citizen ID first
   */
  async createOrUpdatePassenger(passengerData: CreatePassengerRequest): Promise<Passenger> {
    const existingPassenger = await this.findExistingPassenger(passengerData.citizenId);
    
    if (existingPassenger) {
      // Update existing passenger with new information
      const updateData: UpdatePassengerRequest = {
        passengerName: passengerData.passengerName,
        email: passengerData.email,
        phoneNumber: passengerData.phoneNumber
      };
      return this.updatePassenger(existingPassenger.passengerId!, updateData);
    } else {
      // Create new passenger
      return this.createPassenger(passengerData);
    }
  }

  /**
   * Validate passenger data for guest bookings
   */
  validatePassengerData(passengerData: CreatePassengerRequest): string[] {
    const errors: string[] = [];

    if (!passengerData.passengerName?.trim()) {
      errors.push('Passenger name is required');
    }

    if (!passengerData.email?.trim()) {
      errors.push('Email is required');
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(passengerData.email)) {
      errors.push('Invalid email format');
    }

    if (!passengerData.citizenId?.trim()) {
      errors.push('Citizen ID is required');
    }

    if (!passengerData.phoneNumber?.trim()) {
      errors.push('Phone number is required');
    }

    return errors;
  }

  /**
   * Transform frontend passenger data to backend format
   */
  transformPassengerData(frontendData: {
    firstName: string;
    lastName: string;
    email: string;
    citizenId: string;
    phoneNumber: string;
  }): CreatePassengerRequest {
    return {
      passengerName: `${frontendData.firstName.trim()} ${frontendData.lastName.trim()}`,
      email: frontendData.email.trim(),
      citizenId: frontendData.citizenId.trim(),
      phoneNumber: frontendData.phoneNumber.trim()
    };
  }
}

export const passengerService = new PassengerService();
