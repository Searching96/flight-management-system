import { apiClient } from './api';
import { API_URL } from './config';
import {
  FlightTicketClass,
  FlightTicketClassRequest,
  UpdateFlightTicketClassRequest,
} from '../models';

class FlightTicketClassService {
  private readonly baseUrl = API_URL.FLIGHT_TICKET_CLASS;

  async getFlightTicketClassesByFlightId(flightId: number): Promise<FlightTicketClass[]> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}`);
  }

  async getFlightTicketClassById(flightId: number, ticketClassId: number): Promise<FlightTicketClass> {
    return apiClient.get(`${this.baseUrl}/${flightId}/${ticketClassId}`);
  }

  async createFlightTicketClass(request: FlightTicketClassRequest): Promise<FlightTicketClass> {
    return apiClient.post(this.baseUrl, request);
  }

  async updateFlightTicketClass(
    flightId: number,
    ticketClassId: number,
    request: UpdateFlightTicketClassRequest
  ): Promise<FlightTicketClass> {
    return apiClient.put(`${this.baseUrl}/${flightId}/${ticketClassId}`, request);
  }

  async deleteFlightTicketClass(flightId: number, ticketClassId: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${flightId}/${ticketClassId}`);
  }

  async getOccupiedSeats(flightId: number, ticketClassId: number): Promise<number> {
    return apiClient.get(`/flight-ticket-classes/occupied-seats/${flightId}/${ticketClassId}`);
  }
}

export const flightTicketClassService = new FlightTicketClassService();
export default flightTicketClassService;
