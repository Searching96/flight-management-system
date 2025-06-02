import { apiClient } from './api';
import { Parameter } from '../models';

export interface ParameterRequest {
  parameterName: string;
  parameterValue: string;
  description?: string;
  category?: string;
}

export class ParameterService {
  private readonly baseUrl = '/parameters';

  async getAllParameters(): Promise<Parameter[]> {
    return apiClient.get(this.baseUrl);
  }

  async updateParameters(parameterData: ParameterRequest): Promise<Parameter> {
    return apiClient.put(this.baseUrl, parameterData);
  }

  async updateMaxMediumAirports(value: number): Promise<Parameter> {
    return apiClient.put(`${this.baseUrl}/max-medium-airports/${value}`);
  }

  async updateMinFlightDuration(value: number): Promise<Parameter> {
    return apiClient.put(`${this.baseUrl}/min-flight-duration/${value}`);
  }

  async updateMaxLayoverDuration(value: number): Promise<Parameter> {
    return apiClient.put(`${this.baseUrl}/max-layover-duration/${value}`);
  }

  async updateMinLayoverDuration(value: number): Promise<Parameter> {
    return apiClient.put(`${this.baseUrl}/min-layover-duration/${value}`);
  }

  async updateMinBookingAdvance(value: number): Promise<Parameter> {
    return apiClient.put(`${this.baseUrl}/min-booking-advance/${value}`);
  }

  async updateMaxBookingHold(value: number): Promise<Parameter> {
    return apiClient.put(`${this.baseUrl}/max-booking-hold/${value}`);
  }

  async initializeDefaultParameters(): Promise<Parameter> {
    return apiClient.post(`${this.baseUrl}/initialize`);
  }
}

export const parameterService = new ParameterService();