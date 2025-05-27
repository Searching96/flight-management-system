import { apiClient } from './api';
import { 
  FlightDetail, 
  SeatAvailability, 
  BookSeatRequest, 
  SeatMap 
} from '../models';

import { API_URL } from './config';

export class FlightDetailService {
  private readonly baseUrl = API_URL.FLIGHT_DETAILS;

  async getFlightDetails(flightId: number): Promise<FlightDetail[]> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}`);
  }

  async getFlightDetailById(id: number): Promise<FlightDetail> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getSeatAvailability(flightId: number): Promise<SeatAvailability[]> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}/availability`);
  }

  async getSeatMap(flightId: number): Promise<SeatMap> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}/seat-map`);
  }

  async getAvailableSeats(flightId: number, ticketClassId?: number): Promise<FlightDetail[]> {
    const params = ticketClassId ? { ticketClassId } : {};
    return apiClient.get(`${this.baseUrl}/flight/${flightId}/available`, { params });
  }

  async getBookedSeats(flightId: number): Promise<FlightDetail[]> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}/booked`);
  }

  async bookSeat(bookingData: BookSeatRequest): Promise<FlightDetail> {
    return apiClient.post(`${this.baseUrl}/book`, bookingData);
  }

  async cancelSeatBooking(flightDetailId: number): Promise<void> {
    return apiClient.post(`${this.baseUrl}/${flightDetailId}/cancel`);
  }

  async changeSeat(currentFlightDetailId: number, newSeatNumber: string): Promise<FlightDetail> {
    return apiClient.post(`${this.baseUrl}/${currentFlightDetailId}/change-seat`, {
      newSeatNumber
    });
  }

  async getPassengerSeats(passengerId: number): Promise<FlightDetail[]> {
    return apiClient.get(`${this.baseUrl}/passenger/${passengerId}`);
  }

  async createFlightDetails(flightId: number): Promise<FlightDetail[]> {
    return apiClient.post(`${this.baseUrl}/generate`, { flightId });
  }

  async updateFlightDetail(id: number, updateData: Partial<FlightDetail>): Promise<FlightDetail> {
    return apiClient.put(`${this.baseUrl}/${id}`, updateData);
  }

  async deleteFlightDetail(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async checkSeatAvailability(flightId: number, seatNumber: string): Promise<boolean> {
    const response = await apiClient.get(`${this.baseUrl}/flight/${flightId}/seat/${seatNumber}/available`);
    return response.available;
  }
}

export const flightDetailService = new FlightDetailService();
