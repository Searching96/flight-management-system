import { apiClient } from "./api";
import {
  FlightDetail,
  SeatAvailability,
  BookSeatRequest,
  SeatMap,
} from "../models";
import type { ApiResponse } from "../models/ApiResponse";

import { API_URL } from "./config";

export class FlightDetailService {
  private readonly baseUrl = API_URL.FLIGHT_DETAILS;

  async getFlightDetailsById(
    flightId: number
  ): Promise<ApiResponse<FlightDetail[]>> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}`);
  }

  async getFlightDetailById(id: number): Promise<ApiResponse<FlightDetail>> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getSeatAvailability(
    flightId: number
  ): Promise<ApiResponse<SeatAvailability[]>> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}/availability`);
  }

  async getSeatMap(flightId: number): Promise<ApiResponse<SeatMap>> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}/seat-map`);
  }

  async getAvailableSeats(
    flightId: number,
    ticketClassId?: number
  ): Promise<ApiResponse<FlightDetail[]>> {
    const params = ticketClassId ? { ticketClassId } : {};
    return apiClient.get(`${this.baseUrl}/flight/${flightId}/available`, {
      params,
    });
  }

  async getBookedSeats(flightId: number): Promise<ApiResponse<FlightDetail[]>> {
    return apiClient.get(`${this.baseUrl}/flight/${flightId}/booked`);
  }

  async bookSeat(
    bookingData: BookSeatRequest
  ): Promise<ApiResponse<FlightDetail>> {
    return apiClient.post(`${this.baseUrl}/book`, bookingData);
  }

  async cancelSeatBooking(flightDetailId: number): Promise<ApiResponse<void>> {
    return apiClient.post(`${this.baseUrl}/${flightDetailId}/cancel`);
  }

  async changeSeat(
    currentFlightDetailId: number,
    newSeatNumber: string
  ): Promise<ApiResponse<FlightDetail>> {
    return apiClient.post(
      `${this.baseUrl}/${currentFlightDetailId}/change-seat`,
      {
        newSeatNumber,
      }
    );
  }

  async getPassengerSeats(
    passengerId: number
  ): Promise<ApiResponse<FlightDetail[]>> {
    return apiClient.get(`${this.baseUrl}/passenger/${passengerId}`);
  }

  async createFlightDetails(
    flightDetail: FlightDetail
  ): Promise<ApiResponse<FlightDetail[]>> {
    return apiClient.post(`${this.baseUrl}/`, flightDetail);
  }

  async updateFlightDetail(
    flightId: number,
    currentMediumAirportId: number,
    updateData: Partial<FlightDetail>
  ): Promise<ApiResponse<FlightDetail>> {
    return apiClient.put(
      `${this.baseUrl}/${flightId}/${currentMediumAirportId}`,
      updateData
    );
  }

  async deleteFlightDetail(id: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async checkSeatAvailability(
    flightId: number,
    seatNumber: string
  ): Promise<ApiResponse<boolean>> {
    const response = await apiClient.get(
      `${this.baseUrl}/flight/${flightId}/seat/${seatNumber}/available`
    );
    return response.available;
  }
}

export const flightDetailService = new FlightDetailService();
