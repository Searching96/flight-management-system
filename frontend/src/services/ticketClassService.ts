import { apiClient } from "./api";
import { TicketClass, TicketClassRequest } from "../models";
import type { ApiResponse } from "../models/ApiResponse";

export class TicketClassService {
  private readonly baseUrl = "/ticket-classes";

  // Get all ticket classes
  async getAllTicketClasses(): Promise<ApiResponse<TicketClass[]>> {
    return apiClient.get(this.baseUrl);
  }

  // Get ticket class by ID
  async getTicketClassById(id: number): Promise<ApiResponse<TicketClass>> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  // Create new ticket class
  async createTicketClass(
    ticketClassData: TicketClassRequest
  ): Promise<ApiResponse<TicketClass>> {
    return apiClient.post(this.baseUrl, ticketClassData);
  }

  // Update ticket class
  async updateTicketClass(
    id: number,
    ticketClassData: Partial<TicketClass>
  ): Promise<ApiResponse<TicketClass>> {
    return apiClient.put(`${this.baseUrl}/${id}`, ticketClassData);
  }

  // Delete ticket class
  async deleteTicketClass(id: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  // Search ticket classes
  async searchTicketClasses(
    query: string
  ): Promise<ApiResponse<TicketClass[]>> {
    return apiClient.get(`${this.baseUrl}/search`, { params: { q: query } });
  }
}

export const ticketClassService = new TicketClassService();
