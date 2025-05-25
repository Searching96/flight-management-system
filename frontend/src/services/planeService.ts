import { apiClient } from './api';
import { Plane } from '../models';

export class PlaneService {
  private readonly baseUrl = '/planes';

  async getAllPlanes(): Promise<Plane[]> {
    return apiClient.get(this.baseUrl);
  }

  async getPlaneById(id: number): Promise<Plane> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getPlaneByCode(code: string): Promise<Plane> {
    return apiClient.get(`${this.baseUrl}/code/${code}`);
  }

  async getPlanesByType(type: string): Promise<Plane[]> {
    return apiClient.get(`${this.baseUrl}/type/${type}`);
  }

  async createPlane(plane: Omit<Plane, 'planeId'>): Promise<Plane> {
    return apiClient.post(this.baseUrl, plane);
  }

  async updatePlane(id: number, plane: Partial<Plane>): Promise<Plane> {
    return apiClient.put(`${this.baseUrl}/${id}`, plane);
  }

  async deletePlane(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }
}

export const planeService = new PlaneService();
