import { apiClient } from './api';
import { TicketClass, TicketClassRequest } from '../models';

export class TicketClassService {
  private readonly baseUrl = '/ticket-classes';

  // Get all ticket classes
  async getAllTicketClasses(): Promise<TicketClass[]> {
    return apiClient.get(this.baseUrl);
  }

  // Get ticket class by ID
  async getTicketClassById(id: number): Promise<TicketClass> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  // Create new ticket class
  async createTicketClass(ticketClassData: TicketClassRequest): Promise<TicketClass> {
    return apiClient.post(this.baseUrl, ticketClassData);
  }

  // Update ticket class
  async updateTicketClass(id: number, ticketClassData: Partial<TicketClass>): Promise<TicketClass> {
    return apiClient.put(`${this.baseUrl}/${id}`, ticketClassData);
  }

  // Delete ticket class
  async deleteTicketClass(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  // Search ticket classes
  async searchTicketClasses(query: string): Promise<TicketClass[]> {
    return apiClient.get(`${this.baseUrl}/search`, { params: { q: query } });
  }
}

export const ticketClassService = new TicketClassService();