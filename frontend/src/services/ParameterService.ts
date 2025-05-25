import { apiClient } from './api';
import { Parameter } from '../models';

export class ParameterService {
  private readonly baseUrl = '/parameters';

  async getParameters(): Promise<Parameter> {
    return apiClient.get(this.baseUrl);
  }

  async updateParameters(parameters: Partial<Parameter>): Promise<Parameter> {
    return apiClient.put(this.baseUrl, parameters);
  }

  async updateMaxMediumAirports(value: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/max-medium-airports/${value}`);
  }

  async updateMinFlightDuration(value: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/min-flight-duration/${value}`);
  }

  async updateMaxLayoverDuration(value: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/max-layover-duration/${value}`);
  }

  async updateMinLayoverDuration(value: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/min-layover-duration/${value}`);
  }

  async updateMinBookingAdvance(value: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/min-booking-advance/${value}`);
  }

  async updateMaxBookingHold(value: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/max-booking-hold/${value}`);
  }

  async initializeDefaultParameters(): Promise<void> {
    return apiClient.post(`${this.baseUrl}/initialize`);
  }

  // Legacy methods for backward compatibility
  async getFlightConstraints() {
    const params = await this.getParameters();
    return {
      minDuration: params.minFlightDuration,
      maxDuration: params.maxLayoverDuration,
      maxLayovers: params.maxMediumAirport,
      maxStopDuration: params.maxLayoverDuration
    };
  }

  async getBookingConstraints() {
    const params = await this.getParameters();
    return {
      minAdvanceBooking: params.minBookingInAdvanceDuration || 60,
      maxHoldDuration: params.maxBookingHoldDuration || 900,
      cancellationDeadline: 24
    };
  }
}

export const parameterService = new ParameterService();