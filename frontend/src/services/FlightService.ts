import { apiClient } from './api';
import { API_URL } from './config';
import { Flight, FlightSearch, FlightRequest } from '../models';

export class FlightService {
  private readonly baseUrl = API_URL.FLIGHTS;

  async getAllFlights(): Promise<Flight[]> {
    return apiClient.get(this.baseUrl);
  }

  async getFlightById(id: number): Promise<Flight> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getFlightByCode(flightCode: string): Promise<Flight> {
    return apiClient.get(`${this.baseUrl}/code/${flightCode}`);
  }
  async searchFlights(criteria: FlightSearch): Promise<Flight[]> {
    const params: any = {
      departureAirportId: criteria.departureAirportId,
      arrivalAirportId: criteria.arrivalAirportId,
      departureDate: criteria.departureDate,
      passengerCount: criteria.passengers,
      isRoundTrip: criteria.isRoundTrip || false,
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
  ): Promise<Flight[]> {
    return apiClient.get(`${this.baseUrl}/route`, {
      params: { departureAirportId, arrivalAirportId }
    });
  }

  async createFlight(flightData: FlightRequest): Promise<Flight> {
    return apiClient.post(this.baseUrl, flightData);
  }

  async updateFlight(id: number, flightData: Partial<FlightRequest>): Promise<Flight> {
    return apiClient.put(`${this.baseUrl}/${id}`, flightData);
  }

  async deleteFlight(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async getFlightsByAirport(airportId: number): Promise<Flight[]> {
    return apiClient.get(`${this.baseUrl}/airport/${airportId}`);
  }

  async getFlightsByPlane(planeId: number): Promise<Flight[]> {
    return apiClient.get(`${this.baseUrl}/plane/${planeId}`);
  }

  async getFlightsByDateRange(startDate: string, endDate: string): Promise<Flight[]> {
    return apiClient.get(`${this.baseUrl}/date-range`, {
      params: { startDate, endDate }
    });
  }
  async getFlightTicketClassesByFlightId(flightId: number): Promise<any[]> {
    return apiClient.get(API_URL.FLIGHT_TICKET_CLASS + `/flight/${flightId}`);
  }

  async checkFlightAvailability(flightId: number): Promise<any> {
    return apiClient.get(API_URL.FLIGHT_TICKET_CLASS + `/flight/${flightId}`);
  }

  async cancelFlight(id: number): Promise<Flight> {
    return apiClient.patch(`${this.baseUrl}/${id}/cancel`);
  }

  async delayFlight(id: number, newDepartureTime: string, newArrivalTime: string): Promise<Flight> {
    return apiClient.patch(`${this.baseUrl}/${id}/delay`, {
      newDepartureTime,
      newArrivalTime,
    });
  }
}

export const flightService = new FlightService();