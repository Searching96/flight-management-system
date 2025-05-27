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

  async getParameterById(id: number): Promise<Parameter> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getParameterByName(name: string): Promise<Parameter> {
    return apiClient.get(`${this.baseUrl}/name/${name}`);
  }

  async getParametersByCategory(category: string): Promise<Parameter[]> {
    return apiClient.get(`${this.baseUrl}/category/${category}`);
  }

  async createParameter(parameterData: ParameterRequest): Promise<Parameter> {
    return apiClient.post(this.baseUrl, parameterData);
  }

  async updateParameter(id: number, parameterData: Partial<ParameterRequest>): Promise<Parameter> {
    return apiClient.put(`${this.baseUrl}/${id}`, parameterData);
  }

  async updateParameterValue(id: number, value: string): Promise<Parameter> {
    return apiClient.patch(`${this.baseUrl}/${id}/value`, { parameterValue: value });
  }

  async deleteParameter(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async searchParameters(query: string): Promise<Parameter[]> {
    return apiClient.get(`${this.baseUrl}/search`, { params: { q: query } });
  }

  async getSystemSettings(): Promise<Record<string, string>> {
    return apiClient.get(`${this.baseUrl}/system-settings`);
  }

  async updateSystemSettings(settings: Record<string, string>): Promise<void> {
    return apiClient.patch(`${this.baseUrl}/system-settings`, settings);
  }
}

export const parameterService = new ParameterService();