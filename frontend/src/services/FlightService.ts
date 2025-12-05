import { apiClient } from "./api";
import { API_URL } from "./config";
import { Flight, FlightSearchCriteria, FlightRequest } from "../models";
import type { ApiResponse } from "../models/ApiResponse";

export class FlightService {
  private readonly baseUrl = API_URL.FLIGHTS;

  async getAllFlights(): Promise<ApiResponse<Flight[]>> {
    return apiClient.get(this.baseUrl);
  }

  async getFlightById(id: number): Promise<Flight> {
    const response = await apiClient.get<ApiResponse<Flight>>(`${this.baseUrl}/${id}`);
    return response.data;
  }

  async getFlightByCode(flightCode: string): Promise<ApiResponse<Flight>> {
    return apiClient.get(`${this.baseUrl}/code/${flightCode}`);
  }

  async searchFlights(
    criteria: FlightSearchCriteria
  ): Promise<ApiResponse<Flight[]>> {
    const params: any = {
      departureAirportId: criteria.departureAirportId,
      arrivalAirportId: criteria.arrivalAirportId,
      departureDate: criteria.departureDate,
      passengerCount: criteria.passengerCount,
      ticketClassId: criteria.ticketClassId || 0, // Default to 0 if not specified
    };

    // Only include ticketClassId if specified and > 0 to avoid 500 errors
    if (criteria.ticketClassId && criteria.ticketClassId > 0) {
      params.ticketClassId = criteria.ticketClassId;
    }

    // Only include returnDate if provided
    if (criteria.returnDate) {
      params.returnDate = criteria.returnDate;
    }

    return apiClient.get(`${this.baseUrl}/search`, { params });
  }

  async getFlightsByRoute(
    departureAirportId: number,
    arrivalAirportId: number
  ): Promise<ApiResponse<Flight[]>> {
    return apiClient.get(`${this.baseUrl}/route`, {
      params: { departureAirportId, arrivalAirportId },
    });
  }

  async createFlight(flightData: FlightRequest): Promise<ApiResponse<Flight>> {
    return apiClient.post(this.baseUrl, flightData);
  }

  async updateFlight(
    id: number,
    flightData: Partial<FlightRequest>
  ): Promise<ApiResponse<Flight>> {
    return apiClient.put(`${this.baseUrl}/${id}`, flightData);
  }

  async deleteFlight(id: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async getFlightsByAirport(airportId: number): Promise<ApiResponse<Flight[]>> {
    return apiClient.get(`${this.baseUrl}/airport/${airportId}`);
  }

  async getFlightsByPlane(planeId: number): Promise<ApiResponse<Flight[]>> {
    return apiClient.get(`${this.baseUrl}/plane/${planeId}`);
  }

  async getFlightsByDateRange(
    startDate: string,
    endDate: string
  ): Promise<ApiResponse<Flight[]>> {
    return apiClient.get(`${this.baseUrl}/date-range`, {
      params: { startDate, endDate },
    });
  }

  async cancelFlight(id: number): Promise<ApiResponse<Flight>> {
    return apiClient.patch(`${this.baseUrl}/${id}/cancel`);
  }

  async delayFlight(
    id: number,
    newDepartureTime: string,
    newArrivalTime: string
  ): Promise<ApiResponse<Flight>> {
    return apiClient.patch(`${this.baseUrl}/${id}/delay`, {
      newDepartureTime,
      newArrivalTime,
    });
  }
}

export const flightService = new FlightService();
