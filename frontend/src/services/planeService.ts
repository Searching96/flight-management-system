import { apiClient } from "./api";
import { Plane } from "../models";
import type { ApiResponse } from "../models/ApiResponse";

export class PlaneService {
  private readonly baseUrl = "/planes";

  async getAllPlanes(): Promise<ApiResponse<Plane[]>> {
    return apiClient.get(this.baseUrl);
  }

  async getPlaneById(id: number): Promise<ApiResponse<Plane>> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getPlaneByCode(code: string): Promise<ApiResponse<Plane>> {
    return apiClient.get(`${this.baseUrl}/code/${code}`);
  }

  async getPlanesByType(type: string): Promise<ApiResponse<Plane[]>> {
    return apiClient.get(`${this.baseUrl}/type/${type}`);
  }

  async createPlane(
    plane: Omit<Plane, "planeId">
  ): Promise<ApiResponse<Plane>> {
    return apiClient.post(this.baseUrl, plane);
  }

  async updatePlane(
    id: number,
    plane: Partial<Plane>
  ): Promise<ApiResponse<Plane>> {
    return apiClient.put(`${this.baseUrl}/${id}`, plane);
  }

  async deletePlane(id: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }
}

export const planeService = new PlaneService();
