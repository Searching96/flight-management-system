import { apiClient } from "./api";
import { DemoInfo, HealthStatus, ApiResponse } from "../models";

export class DemoService {
  private readonly baseUrl = "/demo";

  async getHealth(): Promise<HealthStatus> {
    const response = await apiClient.get<ApiResponse<HealthStatus>>(`${this.baseUrl}/health`);
    return response.data;
  }

  async getInfo(): Promise<DemoInfo> {
    const response = await apiClient.get<ApiResponse<DemoInfo>>(`${this.baseUrl}/info`);
    return response.data;
  }
}

export const demoService = new DemoService();
