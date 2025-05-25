import { apiClient } from './api';
import { Flight, FlightSearch, FlightDetail } from '../models';

export class FlightService {
  private readonly baseUrl = '/flights';
  private readonly flightDetailsUrl = '/flight-details';
  private readonly flightTicketClassUrl = '/flight-ticket-classes';

  // Core Flight operations
  async getAllFlights(): Promise<Flight[]> {
    return apiClient.get(this.baseUrl);
  }

  async getFlightById(id: number): Promise<Flight> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getFlightByCode(code: string): Promise<Flight> {
    return apiClient.get(`${this.baseUrl}/code/${code}`);
  }

  async createFlight(flight: Omit<Flight, 'flightId'>): Promise<Flight> {
    return apiClient.post(this.baseUrl, flight);
  }

  async updateFlight(id: number, flight: Partial<Flight>): Promise<Flight> {
    return apiClient.put(`${this.baseUrl}/${id}`, flight);
  }

  async deleteFlight(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  // Flight search operations
  async searchFlights(searchCriteria: FlightSearch): Promise<Flight[]> {
    return apiClient.post(`${this.baseUrl}/search`, searchCriteria);
  }

  async getFlightsByRoute(
    departureAirportId: number,
    arrivalAirportId: number,
    departureDate: string
  ): Promise<Flight[]> {
    return apiClient.get(`${this.baseUrl}/route`, {
      departureAirportId,
      arrivalAirportId,
      departureDate,
    });
  }

  async getFlightsByDateRange(startDate: string, endDate: string): Promise<Flight[]> {
    return apiClient.get(`${this.baseUrl}/date-range`, { startDate, endDate });
  }

  async searchFlightsByDate(departureDate: string): Promise<Flight[]> {
    try {
      const response = await apiClient.get<Flight[]>('/flights/search/date', {
        departureDate: departureDate
      });
      return response;
    } catch (error) {
      console.error('Error searching flights by date:', error);
      throw error;
    }
  }

  // Flight Details operations
  async getFlightDetailsByFlightId(flightId: number): Promise<FlightDetail[]> {
    return apiClient.get(`${this.flightDetailsUrl}/flight/${flightId}`);
  }

  async addFlightDetail(flightDetail: FlightDetail): Promise<FlightDetail> {
    return apiClient.post(this.flightDetailsUrl, flightDetail);
  }

  async updateFlightDetail(
    flightId: number,
    mediumAirportId: number,
    detail: Partial<FlightDetail>
  ): Promise<FlightDetail> {
    return apiClient.put(`${this.flightDetailsUrl}/${flightId}/${mediumAirportId}`, detail);
  }

  async deleteFlightDetail(flightId: number, mediumAirportId: number): Promise<void> {
    return apiClient.delete(`${this.flightDetailsUrl}/${flightId}/${mediumAirportId}`);
  }

  // Flight Ticket Class operations
  async getFlightTicketClassesByFlightId(flightId: number): Promise<any[]> {
    return apiClient.get(`${this.flightTicketClassUrl}/flight/${flightId}`);
  }

  async addFlightTicketClass(data: {
    flightId: number;
    ticketClassId: number;
    specifiedFare: number;
    ticketQuantity: number;
  }): Promise<void> {
    return apiClient.post(this.flightTicketClassUrl, data);
  }

  async updateFlightTicketClass(
    flightId: number,
    ticketClassId: number,
    data: {
      specifiedFare: number;
      ticketQuantity: number;
    }
  ): Promise<void> {
    return apiClient.put(`${this.flightTicketClassUrl}/${flightId}/${ticketClassId}`, data);
  }

  async deleteFlightTicketClass(flightId: number, ticketClassId: number): Promise<void> {
    return apiClient.delete(`${this.flightTicketClassUrl}/${flightId}/${ticketClassId}`);
  }

  // Additional operations
  async checkFlightAvailability(
    flightId: number,
    ticketClassId: number,
    requestedSeats: number
  ): Promise<{ available: boolean; remainingSeats: number; ticketClassName: string }> {
    return apiClient.get(`${this.baseUrl}/${flightId}/availability/${ticketClassId}`, {
      seats: requestedSeats
    });
  }

  async getFlightSchedule(
    startDate: string,
    endDate: string,
    airportId?: number
  ): Promise<Flight[]> {
    const params: any = { startDate, endDate };
    if (airportId) params.airportId = airportId;
    return apiClient.get(`${this.baseUrl}/schedule`, params);
  }
}

export const flightService = new FlightService();

export const searchFlightsByDate = async (departureDate: string): Promise<Flight[]> => {
  try {
    const response = await apiClient.get<Flight[]>('/flights/search/date', {
      departureDate: departureDate
    });
    return response;
  } catch (error) {
    console.error('Error searching flights by date:', error);
    throw error;
  }
};