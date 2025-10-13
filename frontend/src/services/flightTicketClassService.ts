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
  ): Promise<ApiResponse<FlightTicketClass[]>> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}`);
  }

  async getFlightTicketClassById(
    flightId: number,
    ticketClassId: number
  ): Promise<ApiResponse<FlightTicketClass>> {
    return apiClient.get(`${this.baseUrl}/${flightId}/${ticketClassId}`);
  }

  async createFlightTicketClass(
    request: FlightTicketClassRequest
  ): Promise<ApiResponse<FlightTicketClass>> {
    return apiClient.post(this.baseUrl, request);
  }

  async checkFlightAvailability(
    flightId: number
  ): Promise<ApiResponse<FlightTicketClass[]>> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}`);
  }

  async updateFlightTicketClass(
    flightId: number,
    ticketClassId: number,
    request: UpdateFlightTicketClassRequest
  ): Promise<FlightTicketClass> {
    return apiClient.put(
      `${this.baseUrl}/${flightId}/${ticketClassId}`,
      request
    );
  }

  async deleteFlightTicketClass(
    flightId: number,
    ticketClassId: number
  ): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.baseUrl}/${flightId}/${ticketClassId}`);
  }

  async getOccupiedSeats(
    flightId: number,
    ticketClassId: number
  ): Promise<ApiResponse<number>> {
    return apiClient.get(
      `/flight-ticket-classes/occupied-seats/${flightId}/${ticketClassId}`
    );
  }

  async updateRemainingTickets(
    flightId: number,
    ticketClassId: number,
    quantity: number
  ): Promise<ApiResponse<void>> {
    return apiClient.put(
      `/flight-ticket-classes/${flightId}/${ticketClassId}/update-remaining?quantity=${quantity}`
    );
  }
}

export const flightTicketClassService = new FlightTicketClassService();
export default flightTicketClassService;
