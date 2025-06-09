import { apiClient } from './api';
import { Ticket, TicketRequest, BookingRequest } from '../models/Ticket';

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


  async getTicketsByFlightId(flightId: number): Promise<Ticket[]> {
    try {
      const response = await apiClient.get(`${this.baseURL}/flight/${flightId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching tickets by flight:', error);
      throw error;
    }
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

    console.log(transformedBooking);
    return apiClient.post('/tickets/book', transformedBooking);
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
    return apiClient.get(`${this.baseURL}/search`, { params: { q: query } });
  }

  async countAllTickets(): Promise<number> {
    const tickets = await this.getAllTickets();
    return tickets.length;
  }

  async generateConfirmationCode(): Promise<string> {
    return apiClient.get(`${this.baseURL}/confirmation-code`);
  }

  async getTicketsOnConfirmationCode(code: string): Promise<Ticket[]> {
    return apiClient.get(`${this.baseURL}/booking-lookup/${code}`);
  }

  transformTicketData(ticket: {
    flightId: number;
    ticketClassId: number;
    bookCustomerId: number | null; // Optional for frequent flyers
    passengerId: number;
    seatNumber: string;
    fare: number;
    paymentTime?: string; // Optional for paid tickets
    confirmationCode: string
  }): TicketRequest {
    return {
      customerId: ticket.bookCustomerId || 0, // Default to 0 if not provided
      flightId: ticket.flightId,
      ticketClassId: ticket.ticketClassId,
      bookCustomerId: ticket.bookCustomerId || null,
      passengerId: ticket.passengerId,
      seatNumber: ticket.seatNumber,
      fare: ticket.fare,
      paymentTime: ticket.paymentTime,
      confirmationCode: ticket.confirmationCode
    };
  }
}

export const ticketService = new TicketService();
