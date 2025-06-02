import { apiClient } from './api';
import { Ticket, TicketRequest, BookingRequest } from '../models';

export class TicketService {
  private readonly baseUrl = '/tickets';

  async getAllTickets(): Promise<Ticket[]> {
    return apiClient.get(this.baseUrl);
  }

  async getTicketById(id: number): Promise<Ticket> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getTicketsByCustomerId(customerId: number): Promise<Ticket[]> {
    return apiClient.get(`${this.baseUrl}/customer/${customerId}`);
  }

  async getTicketsByFlightId(flightId: number): Promise<Ticket[]> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}`);
  }
  async bookTickets(booking: BookingRequest): Promise<Ticket[]> {
    // Transform passenger data format to match backend expectations
    const transformedBooking = {
      ...booking,
      passengers: booking.passengers.map(p => ({
        passengerName: `${p.firstName} ${p.lastName}`.trim(), // Combine names
        email: p.email,
        citizenId: p.citizenId,
        phoneNumber: p.phoneNumber,
      }))
    };

    return apiClient.post('/tickets/book', transformedBooking);
  }

  async createTicket(ticketData: TicketRequest): Promise<Ticket> {
    return apiClient.post(this.baseUrl, ticketData);
  }

  async updateTicket(id: number, ticketData: Partial<Ticket>): Promise<Ticket> {
    return apiClient.put(`${this.baseUrl}/${id}`, ticketData);
  }
  async cancelTicket(ticketId: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/${ticketId}/cancel`);
  }

  async deleteTicket(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async checkInTicket(ticketId: number): Promise<Ticket> {
    return apiClient.patch(`${this.baseUrl}/${ticketId}/check-in`);
  }

  async payTicket(ticketId: number): Promise<Ticket> {
    return apiClient.put(`${this.baseUrl}/${ticketId}/pay`);
  }

  async searchTickets(query: string): Promise<Ticket[]> {
    return apiClient.get(`${this.baseUrl}/search`, { params: { q: query } });
  }
}

export const ticketService = new TicketService();
