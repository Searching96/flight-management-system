import { apiClient } from "./api";
import { API_URL } from "./config";
import {
  FlightTicketClass,
  FlightTicketClassRequest,
  UpdateFlightTicketClassRequest,
} from "../models";
import type { ApiResponse } from "../models/ApiResponse";

class FlightTicketClassService {
  private readonly baseUrl = API_URL.FLIGHT_TICKET_CLASS;

  async getFlightTicketClassesByFlightId(
    flightId: number
  ): Promise<FlightTicketClass[]> {
    const response = await apiClient.get<ApiResponse<FlightTicketClass[]>>(`${this.baseUrl}/flight/${flightId}`);
    return response.data;
  }

  async getFlightTicketClassById(
    flightId: number,
    ticketClassId: number
  ): Promise<FlightTicketClass> {
    const response = await apiClient.get<ApiResponse<FlightTicketClass>>(`${this.baseUrl}/${flightId}/${ticketClassId}`);
    return response.data;
  }

  async createFlightTicketClass(
    request: FlightTicketClassRequest
  ): Promise<FlightTicketClass> {
    const response = await apiClient.post<ApiResponse<FlightTicketClass>>(this.baseUrl, request);
    return response.data;
  }

  async checkFlightAvailability(
    flightId: number
  ): Promise<FlightTicketClass[]> {
    const response = await apiClient.get<ApiResponse<FlightTicketClass[]>>(`${this.baseUrl}/flight/${flightId}`);
    return response.data;
  }

  async updateFlightTicketClass(
    flightId: number,
    ticketClassId: number,
    request: UpdateFlightTicketClassRequest
  ): Promise<FlightTicketClass> {
    const response = await apiClient.put<ApiResponse<FlightTicketClass>>(
      `${this.baseUrl}/${flightId}/${ticketClassId}`,
      request
    );
    return response.data;
  }

  async deleteFlightTicketClass(
    flightId: number,
    ticketClassId: number
  ): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`${this.baseUrl}/${flightId}/${ticketClassId}`);
  }

  async getOccupiedSeats(
    flightId: number,
    ticketClassId: number
  ): Promise<number> {
    const response = await apiClient.get<ApiResponse<number>>(
      `/flight-ticket-classes/occupied-seats/${flightId}/${ticketClassId}`
    );
    return response.data;
  }

  async updateRemainingTickets(
    flightId: number,
    ticketClassId: number,
    quantity: number
  ): Promise<void> {
    await apiClient.put<ApiResponse<void>>(
      `/flight-ticket-classes/${flightId}/${ticketClassId}/update-remaining?quantity=${quantity}`
    );
  }
}

export const flightTicketClassService = new FlightTicketClassService();
export default flightTicketClassService;
