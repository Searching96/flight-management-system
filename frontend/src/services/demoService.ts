import { apiClient } from "./api";
import { DemoInfo, HealthStatus } from "../models";

export class DemoService {
  private readonly baseUrl = "/demo";

  async getHealth(): Promise<HealthStatus> {
    return apiClient.get(`${this.baseUrl}/health`);
  }

  async getInfo(): Promise<DemoInfo> {
    return apiClient.get(`${this.baseUrl}/info`);
  }
}

export const demoService = new DemoService();
