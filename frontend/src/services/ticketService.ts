import { apiClient } from "./api";
import { Ticket, TicketRequest, BookingRequest } from "../models/Ticket";
import type { ApiResponse } from "../models/ApiResponse";

class TicketService {
  private readonly baseURL = "/tickets";

  async getAllTickets(): Promise<ApiResponse<Ticket[]>> {
    return apiClient.get(this.baseURL);
  }

  async getTicketById(ticketId: number): Promise<ApiResponse<Ticket>> {
    return apiClient.get(`${this.baseURL}/${ticketId}`);
  }

  async getTicketsByFlightId(flightId: number): Promise<ApiResponse<Ticket[]>> {
    return apiClient.get(`${this.baseURL}/flight/${flightId}`);
  }

  async bookTickets(booking: BookingRequest): Promise<ApiResponse<Ticket[]>> {
    // Transform passenger data format to match backend expectations
    const transformedBooking = {
      ...booking,
      passengers: booking.passengers.map((p) => ({
        passengerName: `${p.firstName} ${p.lastName}`.trim(), // Combine names
        email: p.email,
        citizenId: p.citizenId,
        phoneNumber: p.phoneNumber,
      })),
    };

    console.log(transformedBooking);
    return apiClient.post("/tickets/book", transformedBooking);
  }

  async createTicket(ticketData: TicketRequest): Promise<ApiResponse<Ticket>> {
    return apiClient.post(this.baseURL, ticketData);
  }

  async updateTicket(
    ticketId: number,
    ticketData: Partial<TicketRequest>
  ): Promise<ApiResponse<Ticket>> {
    return apiClient.put(`${this.baseURL}/${ticketId}`, ticketData);
  }

  async deleteTicket(ticketId: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.baseURL}/${ticketId}`);
  }

  async getTicketsByCustomerId(
    customerId: number
  ): Promise<ApiResponse<Ticket[]>> {
    return apiClient.get(`${this.baseURL}/customer/${customerId}`);
  }

  async searchTickets(query: string): Promise<ApiResponse<Ticket[]>> {
    return apiClient.get(`${this.baseURL}/search`, { params: { q: query } });
  }

  async countAllTickets(): Promise<ApiResponse<number>> {
    return apiClient.get(`${this.baseURL}/count`);
  }

  async generateConfirmationCode(): Promise<ApiResponse<string>> {
    return apiClient.get(`${this.baseURL}/confirmation-code`);
  }

  async getTicketsOnConfirmationCode(
    code: string
  ): Promise<ApiResponse<Ticket[]>> {
    return apiClient.get(`${this.baseURL}/booking-lookup/${code}`);
  }

  async payTicket(ticketId: number): Promise<ApiResponse<Ticket>> {
    return apiClient.post(`${this.baseURL}/pay/${ticketId}`);
  }

  transformTicketData(ticket: {
    flightId: number;
    ticketClassId: number;
    bookCustomerId: number | null; // Optional for frequent flyers
    passengerId: number;
    seatNumber: string;
    fare: number;
    paymentTime?: string; // Optional for paid tickets
    confirmationCode: string;
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
      confirmationCode: ticket.confirmationCode,
    };
  }
}

export const ticketService = new TicketService();
