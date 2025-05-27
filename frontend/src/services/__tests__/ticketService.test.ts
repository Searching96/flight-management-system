import { describe, it, expect, beforeEach, vi } from 'vitest'
import { ticketService, BookingRequest } from '../ticketService'
import { apiClient } from '../api'
import { Ticket } from '../../models'

// Mock the api client
vi.mock('../api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
  },
}))

const mockedApiClient = vi.mocked(apiClient)

describe('TicketService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  const mockTicket: Ticket = {
    ticketId: 1,
    flightId: 1,
    bookCustomerId: 1,
    passengerId: 1,
    ticketClassId: 1,
    seatNumber: 'A1',
    ticketStatus: 2, // unpaid
    fare: 299.99,
    paymentTime: undefined, // Changed from null to undefined
  }

  const mockBookingRequest: BookingRequest = {
    flightId: 1,
    customerId: 1,
    ticketClassId: 1,
    passengers: [
      {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@email.com',
        citizenId: '123456789',
        phoneNumber: '+1234567890',
      },
    ],
    seatNumbers: ['A1'],
  }

  describe('bookTickets', () => {
    it('should book tickets successfully with passenger data transformation', async () => {
      // Arrange
      const mockBookedTickets = [mockTicket]
      mockedApiClient.post.mockResolvedValueOnce(mockBookedTickets)

      // Act
      const result = await ticketService.bookTickets(mockBookingRequest)

      // Assert
      expect(result).toEqual(mockBookedTickets)
      expect(mockedApiClient.post).toHaveBeenCalledWith('/tickets/book', {
        ...mockBookingRequest,
        passengers: [
          {
            passengerName: 'John Doe', // Combined from firstName + lastName
            email: 'john.doe@email.com',
            citizenId: '123456789',
            phoneNumber: '+1234567890',
          },
        ],
      })
    })

    it('should handle booking errors', async () => {
      // Arrange
      const errorMessage = 'Insufficient seats available'
      mockedApiClient.post.mockRejectedValueOnce(new Error(errorMessage))

      // Act & Assert
      await expect(ticketService.bookTickets(mockBookingRequest)).rejects.toThrow(errorMessage)
      expect(mockedApiClient.post).toHaveBeenCalledWith('/tickets/book', expect.any(Object))
    })

    it('should transform multiple passengers correctly', async () => {
      // Arrange
      const multiPassengerRequest = {
        ...mockBookingRequest,
        passengers: [
          {
            firstName: 'John',
            lastName: 'Doe',
            email: 'john.doe@email.com',
            citizenId: '123456789',
            phoneNumber: '+1234567890',
          },
          {
            firstName: 'Jane',
            lastName: 'Smith',
            email: 'jane.smith@email.com',
            citizenId: '987654321',
            phoneNumber: '+0987654321',
          },
        ],
        seatNumbers: ['A1', 'A2'],
      }

      const mockBookedTickets = [mockTicket, { ...mockTicket, ticketId: 2, seatNumber: 'A2' }]
      mockedApiClient.post.mockResolvedValueOnce(mockBookedTickets)

      // Act
      const result = await ticketService.bookTickets(multiPassengerRequest)

      // Assert
      expect(result).toEqual(mockBookedTickets)
      expect(mockedApiClient.post).toHaveBeenCalledWith('/tickets/book', {
        ...multiPassengerRequest,
        passengers: [
          {
            passengerName: 'John Doe',
            email: 'john.doe@email.com',
            citizenId: '123456789',
            phoneNumber: '+1234567890',
          },
          {
            passengerName: 'Jane Smith',
            email: 'jane.smith@email.com',
            citizenId: '987654321',
            phoneNumber: '+0987654321',
          },
        ],
      })
    })
  })

  describe('getAllTickets', () => {
    it('should fetch all tickets successfully', async () => {
      // Arrange
      const mockTickets = [mockTicket]
      mockedApiClient.get.mockResolvedValueOnce(mockTickets)

      // Act
      const result = await ticketService.getAllTickets()

      // Assert
      expect(result).toEqual(mockTickets)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/tickets')
    })
  })

  describe('getTicketById', () => {
    it('should fetch ticket by ID successfully', async () => {
      // Arrange
      mockedApiClient.get.mockResolvedValueOnce(mockTicket)

      // Act
      const result = await ticketService.getTicketById(1)

      // Assert
      expect(result).toEqual(mockTicket)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/tickets/1')
    })
  })

  describe('getTicketsByCustomerId', () => {
    it('should fetch tickets by customer ID successfully', async () => {
      // Arrange
      const mockTickets = [mockTicket]
      mockedApiClient.get.mockResolvedValueOnce(mockTickets)

      // Act
      const result = await ticketService.getTicketsByCustomerId(1)

      // Assert
      expect(result).toEqual(mockTickets)
      expect(mockedApiClient.get).toHaveBeenCalledWith('/tickets/customer/1')
    })
  })

  describe('cancelTicket', () => {
    it('should cancel ticket successfully', async () => {
      // Arrange
      mockedApiClient.delete.mockResolvedValueOnce(undefined)

      // Act
      await ticketService.cancelTicket(1)

      // Assert
      expect(mockedApiClient.delete).toHaveBeenCalledWith('/tickets/1')
    })
  })

  describe('payTicket', () => {
    it('should pay for ticket successfully', async () => {
      // Arrange
      const paidTicket = { ...mockTicket, ticketStatus: 1, paymentTime: '2024-12-01T10:00:00' }
      mockedApiClient.put.mockResolvedValueOnce(paidTicket)

      // Act
      const result = await ticketService.payTicket(1)

      // Assert
      expect(result).toEqual(paidTicket)
      expect(mockedApiClient.put).toHaveBeenCalledWith('/tickets/1/pay')
    })
  })
})
