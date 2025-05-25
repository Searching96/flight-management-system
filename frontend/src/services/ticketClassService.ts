import { apiClient } from './api';
import { TicketClass } from '../models';

export class TicketClassService {
  private readonly baseUrl = '/ticket-classes';

  async getAllTicketClasses(): Promise<TicketClass[]> {
    return apiClient.get(this.baseUrl);
  }

  async getTicketClassById(id: number): Promise<TicketClass> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async createTicketClass(ticketClass: Omit<TicketClass, 'ticketClassId'>): Promise<TicketClass> {
    return apiClient.post(this.baseUrl, ticketClass);
  }

  async updateTicketClass(id: number, ticketClass: Partial<TicketClass>): Promise<TicketClass> {
    return apiClient.put(`${this.baseUrl}/${id}`, ticketClass);
  }

  async deleteTicketClass(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }
}

export const ticketClassService = new TicketClassService();

// Legacy exports for backward compatibility
export const addTicketClass = (data: Omit<TicketClass, 'ticketClassId'>) => 
  ticketClassService.createTicketClass(data);
export const getTicketClass = (id: number) => 
  ticketClassService.getTicketClassById(id);
export const updateTicketClass = (id: number, data: Partial<TicketClass>) => 
  ticketClassService.updateTicketClass(id, data);
export const listTicketClasses = () => 
  ticketClassService.getAllTicketClasses();
export const deleteTicketClass = (id: number) => 
  ticketClassService.deleteTicketClass(id);
