import { describe, it, expect, beforeEach, vi } from 'vitest'
import { flightService, FlightSearchCriteria } from '../flightService'
import { apiClient } from '../api'
import { Flight } from '../../models'

// Mock the api client
vi.mock('../api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

const mockedApiClient = vi.mocked(apiClient)

describe('FlightService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  const mockFlight: Flight = {
    flightId: 1,
    flightCode: 'AA123',
    departureTime: '2024-12-01T10:00:00',
    arrivalTime: '2024-12-01T13:00:00',
    planeId: 1,
    planeCode: 'B737-800',
    departureAirportId: 1,
    departureAirportName: 'JFK Airport',
    departureCityName: 'New York',
    arrivalAirportId: 2,
    arrivalAirportName: 'LAX Airport',
    arrivalCityName: 'Los Angeles',
  }

  describe('getAllFlights', () => {
    it('should fetch all flights successfully', async () => {
      // Arrange
      const mockFlights = [mockFlight]
      mockedApiClient.get.mockResolvedValueOnce(mockFlights)

      // Act
      const result = await flightService.getAllFlights()

      // Assert
      expect(result).toEqual(mockFlights)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/flights')
    })

    it('should handle API errors gracefully', async () => {
      // Arrange
      const errorMessage = 'Network error'
      mockedApiClient.get.mockRejectedValueOnce(new Error(errorMessage))

      // Act & Assert
      await expect(flightService.getAllFlights()).rejects.toThrow(errorMessage)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/flights')
    })
  })

  describe('getFlightById', () => {
    it('should fetch flight by ID successfully', async () => {
      // Arrange
      mockedApiClient.get.mockResolvedValueOnce(mockFlight)

      // Act
      const result = await flightService.getFlightById(1)

      // Assert
      expect(result).toEqual(mockFlight)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/flights/1')
    })
  })

  describe('searchFlights', () => {
    const mockSearchCriteria: FlightSearchCriteria = {
      departureAirportId: 1,
      arrivalAirportId: 2,
      departureDate: '2024-12-01T10:00:00',
      passengerCount: 1,
      ticketClassId: 1,
    }

    it('should search flights with all parameters', async () => {
      // Arrange
      const mockFlights = [mockFlight]
      mockedApiClient.get.mockResolvedValueOnce(mockFlights)

      // Act
      const result = await flightService.searchFlights(mockSearchCriteria)

      // Assert
      expect(result).toEqual(mockFlights)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/flights/search', {
        params: {
          departureAirportId: 1,
          arrivalAirportId: 2,
          departureDate: '2024-12-01T10:00:00',
          passengerCount: 1,
          ticketClassId: 1,
        },
      })
    })

    it('should search flights without ticket class when not specified', async () => {
      // Arrange
      const criteriaWithoutClass = { ...mockSearchCriteria, ticketClassId: 0 }
      const mockFlights = [mockFlight]
      mockedApiClient.get.mockResolvedValueOnce(mockFlights)

      // Act
      const result = await flightService.searchFlights(criteriaWithoutClass)

      // Assert
      expect(result).toEqual(mockFlights)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/flights/search', {
        params: {
          departureAirportId: 1,
          arrivalAirportId: 2,
          departureDate: '2024-12-01T10:00:00',
          passengerCount: 1,
          // ticketClassId should not be included when 0
        },
      })
    })

    it('should include return date when provided', async () => {
      // Arrange
      const criteriaWithReturn = { 
        ...mockSearchCriteria, 
        returnDate: '2024-12-08T15:00:00' 
      }
      const mockFlights = [mockFlight]
      mockedApiClient.get.mockResolvedValueOnce(mockFlights)

      // Act
      const result = await flightService.searchFlights(criteriaWithReturn)

      // Assert
      expect(result).toEqual(mockFlights)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/flights/search', {
        params: {
          departureAirportId: 1,
          arrivalAirportId: 2,
          departureDate: '2024-12-01T10:00:00',
          returnDate: '2024-12-08T15:00:00',
          passengerCount: 1,
          ticketClassId: 1,
        },
      })
    })
  })

  describe('checkFlightAvailability', () => {
    it('should check flight availability successfully', async () => {
      // Arrange
      const mockAvailability = [
        { ticketClassId: 1, ticketClassName: 'Economy', remainingTicketQuantity: 50 },
        { ticketClassId: 2, ticketClassName: 'Business', remainingTicketQuantity: 10 },
      ]
      mockedApiClient.get.mockResolvedValueOnce(mockAvailability)

      // Act
      const result = await flightService.checkFlightAvailability(1)

      // Assert
      expect(result).toEqual(mockAvailability)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/flight-ticket-classes/flight/1')
    })

    it('should handle availability check errors', async () => {
      // Arrange
      const errorMessage = 'Flight not found'
      mockedApiClient.get.mockRejectedValueOnce(new Error(errorMessage))

      // Act & Assert
      await expect(flightService.checkFlightAvailability(999)).rejects.toThrow(errorMessage)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/flight-ticket-classes/flight/999')
    })
  })

  describe('createFlight', () => {
    const mockFlightRequest = {
      flightCode: 'AA123',
      departureTime: '2024-12-01T10:00:00',
      arrivalTime: '2024-12-01T13:00:00',
      planeId: 1,
      departureAirportId: 1,
      arrivalAirportId: 2,
    }

    it('should create flight successfully', async () => {
      // Arrange
      mockedApiClient.post.mockResolvedValueOnce(mockFlight)

      // Act
      const result = await flightService.createFlight(mockFlightRequest)

      // Assert
      expect(result).toEqual(mockFlight)
      expect(mockedApiClient.post).toHaveBeenCalledWith('/flights', mockFlightRequest)
    })
  })

  describe('updateFlight', () => {
    const mockUpdateData = {
      flightCode: 'AA124',
      departureTime: '2024-12-01T11:00:00',
    }

    it('should update flight successfully', async () => {
      // Arrange
      const updatedFlight = { ...mockFlight, ...mockUpdateData }
      mockedApiClient.put.mockResolvedValueOnce(updatedFlight)

      // Act
      const result = await flightService.updateFlight(1, mockUpdateData)

      // Assert
      expect(result).toEqual(updatedFlight)
      expect(mockedApiClient.put).toHaveBeenCalledWith('/flights/1', mockUpdateData)
    })
  })

  describe('deleteFlight', () => {
    it('should delete flight successfully', async () => {
      // Arrange
      mockedApiClient.delete.mockResolvedValueOnce(undefined)

      // Act
      await flightService.deleteFlight(1)

      // Assert
      expect(mockedApiClient.delete).toHaveBeenCalledWith('/flights/1')
    })
  })

  describe('error handling and edge cases', () => {
    it('should handle malformed search criteria', async () => {
      // Arrange
      const invalidCriteria = {
        departureAirportId: -1,
        arrivalAirportId: -1,
        departureDate: 'invalid-date',
        passengerCount: 0,
        ticketClassId: 999,
      } as any

      const apiError = {
        response: {
          status: 400,
          data: { message: 'Invalid search criteria' }
        }
      }
      mockedApiClient.get.mockRejectedValueOnce(apiError)

      // Act & Assert
      await expect(flightService.searchFlights(invalidCriteria)).rejects.toEqual(apiError)
    })
  })
})