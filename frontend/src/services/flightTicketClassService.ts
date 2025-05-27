import { apiClient } from './api';
import { FlightTicketClass } from '../models/index';
export interface CreateFlightTicketClassRequest {
  flightId: number;
  ticketClassId: number;
  ticketQuantity: number;
  remainingTicketQuantity: number;
  specifiedFare: number;
}

export interface UpdateFlightTicketClassRequest {
  ticketQuantity?: number;
  remainingTicketQuantity?: number;
  specifiedFare?: number;
}

class FlightTicketClassService {
  async getAllFlightTicketClasses(): Promise<FlightTicketClass[]> {
    return apiClient.get('/flight-ticket-classes');
  }

  async getFlightTicketClassById(flightId: number, ticketClassId: number): Promise<FlightTicketClass> {
    return apiClient.get(`/flight-ticket-classes/${flightId}/${ticketClassId}`);
  }

  async getFlightTicketClassesByFlightId(flightId: number): Promise<FlightTicketClass[]> {
    return apiClient.get(`/flight-ticket-classes/flight/${flightId}`);
  }

  async createFlightTicketClass(data: CreateFlightTicketClassRequest): Promise<FlightTicketClass> {
    return apiClient.post('/flight-ticket-classes', data);
  }

  async updateFlightTicketClass(flightId: number, ticketClassId: number, data: UpdateFlightTicketClassRequest): Promise<FlightTicketClass> {
    return apiClient.put(`/flight-ticket-classes/${flightId}/${ticketClassId}`, data);
  }

  async deleteFlightTicketClass(flightId: number, ticketClassId: number): Promise<void> {
    return apiClient.delete(`/flight-ticket-classes/${flightId}/${ticketClassId}`);
  }

  async updateRemainingTickets(flightId: number, ticketClassId: number, quantity: number): Promise<void> {
    return apiClient.put(`/flight-ticket-classes/${flightId}/${ticketClassId}/update-remaining?quantity=${quantity}`);
  }

  async getAvailableFlightTicketClasses(): Promise<FlightTicketClass[]> {
    return apiClient.get('/flight-ticket-classes/available');
  }
}

export const flightTicketClassService = new FlightTicketClassService();
