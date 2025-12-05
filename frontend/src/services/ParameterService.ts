import { apiClient } from "./api";
import { Parameter, ParameterUpdateRequest, ApiResponse } from "../models";

export class ParameterService {
  private readonly baseUrl = "/parameters";

  async getLatestParameter(): Promise<Parameter> {
    const result = await apiClient.get<ApiResponse<Parameter>>(this.baseUrl);
    console.log("Latest parameters fetched:", result);
    return result.data;
  }

  async getAllParameters(): Promise<Parameter> {
    // Alias for getLatestParameter since the backend returns a single parameter object
    return this.getLatestParameter();
  }

  async updateParameters(
    parameterData: ParameterUpdateRequest
  ): Promise<Parameter> {
    const result = await apiClient.put<ApiResponse<Parameter>>(this.baseUrl, parameterData);
    return result.data;
  }

  async updateMaxMediumAirports(
    value: number
  ): Promise<Parameter> {
    const result = await apiClient.put<ApiResponse<Parameter>>(`${this.baseUrl}/max-medium-airports/${value}`);
    return result.data;
  }

  async updateMinFlightDuration(
    value: number
  ): Promise<Parameter> {
    const result = await apiClient.put<ApiResponse<Parameter>>(`${this.baseUrl}/min-flight-duration/${value}`);
    return result.data;
  }

  async updateMaxLayoverDuration(
    value: number
  ): Promise<Parameter> {
    const result = await apiClient.put<ApiResponse<Parameter>>(`${this.baseUrl}/max-layover-duration/${value}`);
    return result.data;
  }

  async updateMinLayoverDuration(
    value: number
  ): Promise<Parameter> {
    const result = await apiClient.put<ApiResponse<Parameter>>(`${this.baseUrl}/min-layover-duration/${value}`);
    return result.data;
  }

  async updateMinBookingAdvance(
    value: number
  ): Promise<Parameter> {
    const result = await apiClient.put<ApiResponse<Parameter>>(`${this.baseUrl}/min-booking-advance/${value}`);
    return result.data;
  }

  async updateMaxBookingHold(value: number): Promise<Parameter> {
    const result = await apiClient.put<ApiResponse<Parameter>>(`${this.baseUrl}/max-booking-hold/${value}`);
    return result.data;
  }

  async initializeDefaultParameters(): Promise<Parameter> {
    const result = await apiClient.post<ApiResponse<Parameter>>(`${this.baseUrl}/initialize`);
    return result.data;
  }
}

export const parameterService = new ParameterService();
