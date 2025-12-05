import { apiClient } from "./api";
import { Ticket, TicketRequest, BookingRequest } from "../models/Ticket";
import type { ApiResponse } from "../models/ApiResponse";

class TicketService {
  private readonly baseURL = "/tickets";

  async getAllTickets(): Promise<Ticket[]> {
    const response = await apiClient.get<ApiResponse<Ticket[]>>(this.baseURL);
    return response.data;
  }

  async getTicketById(ticketId: number): Promise<Ticket> {
    const response = await apiClient.get<ApiResponse<Ticket>>(`${this.baseURL}/${ticketId}`);
    return response.data;
  }

  async getTicketsByFlightId(flightId: number): Promise<Ticket[]> {
    const response = await apiClient.get<ApiResponse<Ticket[]>>(`${this.baseURL}/flight/${flightId}`);
    return response.data;
  }

  async bookTickets(booking: BookingRequest): Promise<Ticket[]> {
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
    const response = await apiClient.post<ApiResponse<Ticket[]>>("/tickets/book", transformedBooking);
    return response.data;
  }

  async createTicket(ticketData: TicketRequest): Promise<Ticket> {
    const response = await apiClient.post<ApiResponse<Ticket>>(this.baseURL, ticketData);
    return response.data;
  }

  async updateTicket(
    ticketId: number,
    ticketData: Partial<TicketRequest>
  ): Promise<Ticket> {
    const response = await apiClient.put<ApiResponse<Ticket>>(`${this.baseURL}/${ticketId}`, ticketData);
    return response.data;
  }

  async deleteTicket(ticketId: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`${this.baseURL}/${ticketId}`);
  }

  async getTicketsByCustomerId(
    customerId: number
  ): Promise<Ticket[]> {
    const response = await apiClient.get<ApiResponse<Ticket[]>>(`${this.baseURL}/customer/${customerId}`);
    return response.data;
  }

  async searchTickets(query: string): Promise<Ticket[]> {
    const response = await apiClient.get<ApiResponse<Ticket[]>>(`${this.baseURL}/search`, { params: { q: query } });
    return response.data;
  }

  async countAllTickets(): Promise<number> {
    const response = await apiClient.get<ApiResponse<number>>(`${this.baseURL}/count`);
    return response.data;
  }

  async generateConfirmationCode(): Promise<string> {
    const response = await apiClient.get<ApiResponse<string>>(`${this.baseURL}/confirmation-code`);
    return response.data;
  }

  async getTicketsOnConfirmationCode(
    code: string
  ): Promise<Ticket[]> {
    const response = await apiClient.get<ApiResponse<Ticket[]>>(`${this.baseURL}/booking-lookup/${code}`);
    return response.data;
  }

  async payTicket(ticketId: number): Promise<Ticket> {
    const response = await apiClient.post<ApiResponse<Ticket>>(`${this.baseURL}/pay/${ticketId}`);
    return response.data;
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
