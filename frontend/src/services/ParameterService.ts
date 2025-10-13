import { apiClient } from "./api";
import { Parameter, ParameterUpdateRequest } from "../models";
import type { ApiResponse } from "../models/ApiResponse";

export class ParameterService {
  private readonly baseUrl = "/parameters";

  async getAllParameters(): Promise<ApiResponse<Parameter>> {
    return apiClient.get(this.baseUrl);
  }

  async updateParameters(
    parameterData: ParameterUpdateRequest
  ): Promise<ApiResponse<Parameter>> {
    return apiClient.put(this.baseUrl, parameterData);
  }

  async updateMaxMediumAirports(
    value: number
  ): Promise<ApiResponse<Parameter>> {
    return apiClient.put(`${this.baseUrl}/max-medium-airports/${value}`);
  }

  async updateMinFlightDuration(
    value: number
  ): Promise<ApiResponse<Parameter>> {
    return apiClient.put(`${this.baseUrl}/min-flight-duration/${value}`);
  }

  async updateMaxLayoverDuration(
    value: number
  ): Promise<ApiResponse<Parameter>> {
    return apiClient.put(`${this.baseUrl}/max-layover-duration/${value}`);
  }

  async updateMinLayoverDuration(
    value: number
  ): Promise<ApiResponse<Parameter>> {
    return apiClient.put(`${this.baseUrl}/min-layover-duration/${value}`);
  }

  async updateMinBookingAdvance(
    value: number
  ): Promise<ApiResponse<Parameter>> {
    return apiClient.put(`${this.baseUrl}/min-booking-advance/${value}`);
  }

  async updateMaxBookingHold(value: number): Promise<ApiResponse<Parameter>> {
    return apiClient.put(`${this.baseUrl}/max-booking-hold/${value}`);
  }

  async initializeDefaultParameters(): Promise<ApiResponse<Parameter>> {
    return apiClient.post(`${this.baseUrl}/initialize`);
  }
}

export const parameterService = new ParameterService();
