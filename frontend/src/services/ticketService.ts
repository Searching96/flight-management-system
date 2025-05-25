import { apiClient } from './api';
import { Ticket, TicketRequest, FlightTicketClass } from '../models';

export class TicketService {
  private readonly baseUrl = '/tickets';
  private readonly flightTicketClassUrl = '/flight-ticket-classes';

  // Ticket CRUD operations
  async getAllTickets(): Promise<Ticket[]> {
    return apiClient.get(this.baseUrl);
  }

  async getTicketById(id: number): Promise<Ticket> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async createTicket(ticketRequest: TicketRequest): Promise<Ticket> {
    return apiClient.post(this.baseUrl, ticketRequest);
  }

  async updateTicket(id: number, ticketRequest: Partial<TicketRequest>): Promise<Ticket> {
    return apiClient.put(`${this.baseUrl}/${id}`, ticketRequest);
  }

  async deleteTicket(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  // Query methods
  async getTicketsByFlightId(flightId: number): Promise<Ticket[]> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}`);
  }

  async getTicketsByCustomer(customerId: number): Promise<Ticket[]> {
    return apiClient.get(`${this.baseUrl}/customer/${customerId}`);
  }

  async getTicketsByPassenger(passengerId: number): Promise<Ticket[]> {
    return apiClient.get(`${this.baseUrl}/passenger/${passengerId}`);
  }

  // Ticket operations
  async cancelTicket(ticketId: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/${ticketId}/cancel`);
  }

  async confirmTicket(ticketId: number): Promise<Ticket> {
    return apiClient.put(`${this.baseUrl}/${ticketId}/confirm`);
  }

  // Flight Ticket Class operations
  async getFlightTicketClassesByFlightId(flightId: number): Promise<FlightTicketClass[]> {
    return apiClient.get(`${this.flightTicketClassUrl}/flight/${flightId}`);
  }

  async getAvailableSeats(flightId: number, ticketClassId: number): Promise<string[]> {
    return apiClient.get(`${this.baseUrl}/available-seats/${flightId}/${ticketClassId}`);
  }

  // Simplified booking for demo
  async bookTickets(booking: {
    flightId: number;
    passengers: Array<{
      firstName: string;
      lastName: string;
      dateOfBirth: string;
      gender: string;
      citizenId: string;
      phoneNumber: string;
      email: string;
    }>;
    customerId: number;
    ticketClassId: number;
  }): Promise<Ticket[]> {
    return apiClient.post(`${this.baseUrl}/book`, booking);
  }
}

export const ticketService = new TicketService();
