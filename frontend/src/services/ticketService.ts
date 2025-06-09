import { apiClient } from './api';
import { Ticket, TicketRequest, TicketFilters } from '../models/Ticket';

class TicketService {
  private readonly baseURL = '/tickets';

  async getAllTickets(): Promise<Ticket[]> {
    try {
      const response = await apiClient.get(`${this.baseURL}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching tickets:', error);
      throw error;
    }
  }

  async getTicketById(ticketId: number): Promise<Ticket> {
    try {
      const response = await apiClient.get(`${this.baseURL}/${ticketId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching ticket:', error);
      throw error;
    }
  }

  async createTicket(ticketData: TicketRequest): Promise<Ticket> {
    try {
      const response = await apiClient.post(this.baseURL, ticketData);
      return response.data;
    } catch (error) {
      console.error('Error creating ticket:', error);
      throw error;
    }
  }

  async updateTicket(ticketId: number, ticketData: Partial<TicketRequest>): Promise<Ticket> {
    try {
      const response = await apiClient.put(`${this.baseURL}/${ticketId}`, ticketData);
      return response.data;
    } catch (error) {
      console.error('Error updating ticket:', error);
      throw error;
    }
  }

  async deleteTicket(ticketId: number): Promise<void> {
    try {
      await apiClient.delete(`${this.baseURL}/${ticketId}`);
    } catch (error) {
      console.error('Error deleting ticket:', error);
      throw error;
    }
  }

  async getTicketsByFlightId(flightId: number): Promise<Ticket[]> {
    try {
      const response = await apiClient.get(`${this.baseURL}/flight/${flightId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching tickets by flight:', error);
      throw error;
    }
  }

  async getTicketsByCustomerId(customerId: number): Promise<Ticket[]> {
    try {
      const response = await apiClient.get(`${this.baseURL}/customer/${customerId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching tickets by customer:', error);
      throw error;
    }
  }

  async searchTickets(query: string): Promise<Ticket[]> {
    try {
      const response = await apiClient.get(`${this.baseURL}/search`, {
        params: { q: query }
      });
      return response.data;
    } catch (error) {
      console.error('Error searching tickets:', error);
      throw error;
    }
  }
}

export const ticketService = new TicketService();
