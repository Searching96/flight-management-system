import { describe, it, expect, beforeEach, vi } from 'vitest';
import { bookingConfirmationService, BookingConfirmation, BookingLookupRequest } from '../bookingConfirmationService';
import { apiClient } from '../api';
import { Ticket } from '../../models';

// Mock the API client
vi.mock('../api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

const mockApiClient = vi.mocked(apiClient);

// Mock localStorage
const mockLocalStorage = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
};

Object.defineProperty(window, 'localStorage', {
  value: mockLocalStorage,
});

// Mock Notification API
Object.defineProperty(window, 'Notification', {
  value: {
    permission: 'granted',
    requestPermission: vi.fn().mockResolvedValue('granted'),
  },
  configurable: true,
});

const mockNotificationConstructor = vi.fn();
(window as any).Notification = mockNotificationConstructor;

describe('BookingConfirmationService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockLocalStorage.getItem.mockReturnValue(null);
  });

  const mockTicket: Ticket = {
    ticketId: 1,
    flightId: 100,
    ticketClassId: 1,
    seatNumber: 'A1',
    fare: 299.99,
  };

  const mockFlightInfo = {
    flightCode: 'FMS001',
    departureTime: '2025-06-01T10:00:00Z',
    arrivalTime: '2025-06-01T14:00:00Z',
    departureCityName: 'New York',
    arrivalCityName: 'Los Angeles',
  };

  const mockConfirmation: BookingConfirmation = {
    confirmationCode: 'FMS-20250527-A1B2',
    bookingDate: '2025-05-27T10:00:00Z',
    tickets: [mockTicket],
    passengerEmails: ['test@example.com'],
    totalAmount: 299.99,
    flightInfo: {
      flightCode: 'FMS001',
      departureTime: '2025-06-01T10:00:00Z',
      arrivalTime: '2025-06-01T14:00:00Z',
      departureCity: 'New York',
      arrivalCity: 'Los Angeles',
    },
  };

  describe('generateConfirmationCode', () => {
    it('should generate code with correct format', () => {
      // Mock Date to return a specific date
      const mockDate = new Date('2025-05-27T10:00:00Z');
      vi.spyOn(globalThis, 'Date').mockImplementation(() => mockDate);

      const code = bookingConfirmationService.generateConfirmationCode();

      expect(code).toMatch(/^FMS-20250527-[A-Z0-9]{4}$/);
      
      vi.restoreAllMocks();
    });

    it('should generate unique codes on multiple calls', () => {
      const code1 = bookingConfirmationService.generateConfirmationCode();
      const code2 = bookingConfirmationService.generateConfirmationCode();

      expect(code1).not.toBe(code2);
      expect(code1).toMatch(/^FMS-\d{8}-[A-Z0-9]{4}$/);
      expect(code2).toMatch(/^FMS-\d{8}-[A-Z0-9]{4}$/);
    });
  });

  describe('storeGuestBookingConfirmation', () => {
    it('should store booking in localStorage', () => {
      mockLocalStorage.getItem.mockReturnValue('[]');

      bookingConfirmationService.storeGuestBookingConfirmation(mockConfirmation);

      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        'guestBookings',
        JSON.stringify([mockConfirmation])
      );
    });

    it('should append to existing bookings', () => {
      const existingBooking = { ...mockConfirmation, confirmationCode: 'FMS-20250526-X1Y2' };
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify([existingBooking]));

      bookingConfirmationService.storeGuestBookingConfirmation(mockConfirmation);

      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        'guestBookings',
        JSON.stringify([existingBooking, mockConfirmation])
      );
    });

    it('should limit stored bookings to 10', () => {
      const existingBookings = Array.from({ length: 10 }, (_, i) => ({
        ...mockConfirmation,
        confirmationCode: `FMS-20250526-${i}ABC`,
      }));
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify(existingBookings));

      bookingConfirmationService.storeGuestBookingConfirmation(mockConfirmation);

      const expectedBookings = [...existingBookings.slice(1), mockConfirmation];
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        'guestBookings',
        JSON.stringify(expectedBookings)
      );
    });

    it('should handle localStorage errors gracefully', () => {
      mockLocalStorage.getItem.mockImplementation(() => {
        throw new Error('localStorage error');
      });

      // Should not throw
      expect(() => {
        bookingConfirmationService.storeGuestBookingConfirmation(mockConfirmation);
      }).not.toThrow();
    });
  });

  describe('getStoredGuestBookings', () => {
    it('should return parsed bookings from localStorage', () => {
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify([mockConfirmation]));

      const result = bookingConfirmationService.getStoredGuestBookings();

      expect(result).toEqual([mockConfirmation]);
      expect(mockLocalStorage.getItem).toHaveBeenCalledWith('guestBookings');
    });

    it('should return empty array when no bookings stored', () => {
      mockLocalStorage.getItem.mockReturnValue(null);

      const result = bookingConfirmationService.getStoredGuestBookings();

      expect(result).toEqual([]);
    });

    it('should handle JSON parse errors gracefully', () => {
      mockLocalStorage.getItem.mockReturnValue('invalid json');

      const result = bookingConfirmationService.getStoredGuestBookings();

      expect(result).toEqual([]);
    });
  });

  describe('findGuestBooking', () => {
    it('should find booking by confirmation code', () => {
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify([mockConfirmation]));

      const result = bookingConfirmationService.findGuestBooking('FMS-20250527-A1B2');

      expect(result).toEqual(mockConfirmation);
    });

    it('should return null when booking not found', () => {
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify([mockConfirmation]));

      const result = bookingConfirmationService.findGuestBooking('FMS-20250527-XXXX');

      expect(result).toBeNull();
    });

    it('should return null when no bookings stored', () => {
      mockLocalStorage.getItem.mockReturnValue('[]');

      const result = bookingConfirmationService.findGuestBooking('FMS-20250527-A1B2');

      expect(result).toBeNull();
    });
  });

  describe('lookupBooking', () => {
    it('should return local booking when found', async () => {
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify([mockConfirmation]));

      const request: BookingLookupRequest = {
        confirmationCode: 'FMS-20250527-A1B2',
      };

      const result = await bookingConfirmationService.lookupBooking(request);

      expect(result).toEqual(mockConfirmation);
    });

    it('should verify email when provided', async () => {
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify([mockConfirmation]));

      const request: BookingLookupRequest = {
        confirmationCode: 'FMS-20250527-A1B2',
        email: 'test@example.com',
      };

      const result = await bookingConfirmationService.lookupBooking(request);

      expect(result).toEqual(mockConfirmation);
    });

    it('should return null when email does not match', async () => {
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify([mockConfirmation]));

      const request: BookingLookupRequest = {
        confirmationCode: 'FMS-20250527-A1B2',
        email: 'wrong@example.com',
      };

      const result = await bookingConfirmationService.lookupBooking(request);

      expect(result).toBeNull();
    });

    it('should fall back to API when not found locally', async () => {
      mockLocalStorage.getItem.mockReturnValue('[]');
      mockApiClient.get.mockResolvedValue(mockConfirmation);

      const request: BookingLookupRequest = {
        confirmationCode: 'FMS-20250527-A1B2',
        email: 'test@example.com',
      };

      const result = await bookingConfirmationService.lookupBooking(request);

      expect(result).toEqual(mockConfirmation);
      expect(mockApiClient.get).toHaveBeenCalledWith(
        '/bookings/lookup/FMS-20250527-A1B2',
        {
          params: {
            email: 'test@example.com',
            citizenId: undefined,
          },
        }
      );
    });

    it('should return null when API returns 404', async () => {
      mockLocalStorage.getItem.mockReturnValue('[]');
      const error = new Error('Not found');
      (error as any).response = { status: 404 };
      mockApiClient.get.mockRejectedValue(error);

      const request: BookingLookupRequest = {
        confirmationCode: 'FMS-20250527-A1B2',
      };

      const result = await bookingConfirmationService.lookupBooking(request);

      expect(result).toBeNull();
    });
  });

  describe('cancelBooking', () => {
    it('should remove booking from localStorage', async () => {
      const bookings = [mockConfirmation, { ...mockConfirmation, confirmationCode: 'FMS-20250527-X1Y2' }];
      mockLocalStorage.getItem.mockReturnValue(JSON.stringify(bookings));

      await bookingConfirmationService.cancelBooking('FMS-20250527-A1B2');

      const expectedBookings = [{ ...mockConfirmation, confirmationCode: 'FMS-20250527-X1Y2' }];
      expect(mockLocalStorage.setItem).toHaveBeenCalledWith(
        'guestBookings',
        JSON.stringify(expectedBookings)
      );
    });

    it('should call API for cancellation', async () => {
      mockLocalStorage.getItem.mockReturnValue('[]');
      mockApiClient.post.mockResolvedValue(undefined);

      await bookingConfirmationService.cancelBooking('FMS-20250527-A1B2', 'Test reason');

      expect(mockApiClient.post).toHaveBeenCalledWith(
        '/bookings/cancel/FMS-20250527-A1B2',
        { reason: 'Test reason' }
      );
    });

    it('should handle API errors gracefully', async () => {
      mockLocalStorage.getItem.mockReturnValue('[]');
      mockApiClient.post.mockRejectedValue(new Error('Server error'));

      // Should not throw
      await expect(
        bookingConfirmationService.cancelBooking('FMS-20250527-A1B2')
      ).resolves.toBeUndefined();
    });
  });

  describe('createConfirmation', () => {
    it('should create confirmation with correct structure', () => {
      const tickets = [mockTicket];
      const emails = ['test@example.com'];

      // Mock generateConfirmationCode
      vi.spyOn(bookingConfirmationService, 'generateConfirmationCode')
        .mockReturnValue('FMS-20250527-A1B2');

      const result = bookingConfirmationService.createConfirmation(tickets, emails, mockFlightInfo);

      expect(result).toMatchObject({
        confirmationCode: 'FMS-20250527-A1B2',
        tickets,
        passengerEmails: emails,
        totalAmount: 299.99,
        flightInfo: {
          flightCode: 'FMS001',
          departureTime: '2025-06-01T10:00:00Z',
          arrivalTime: '2025-06-01T14:00:00Z',
          departureCity: 'New York',
          arrivalCity: 'Los Angeles',
        },
      });
      expect(result.bookingDate).toBeDefined();
    });

    it('should calculate total amount correctly', () => {
      const tickets = [
        { ...mockTicket, fare: 100 },
        { ...mockTicket, fare: 200 },
        { ...mockTicket, fare: undefined }, // Should default to 0
      ];

      const result = bookingConfirmationService.createConfirmation(
        tickets,
        ['test@example.com'],
        mockFlightInfo
      );

      expect(result.totalAmount).toBe(300);
    });
  });

  describe('sendConfirmationEmail', () => {
    it('should show notification when permission granted', async () => {
      (window as any).Notification.permission = 'granted';

      await bookingConfirmationService.sendConfirmationEmail(mockConfirmation);

      expect(mockNotificationConstructor).toHaveBeenCalledWith(
        'Booking Confirmed!',
        {
          body: 'Your booking confirmation code is: FMS-20250527-A1B2',
          icon: '/vite.svg',
        }
      );
    });

    it('should not show notification when permission not granted', async () => {
      (window as any).Notification.permission = 'denied';

      await bookingConfirmationService.sendConfirmationEmail(mockConfirmation);

      expect(mockNotificationConstructor).not.toHaveBeenCalled();
    });
  });

  describe('requestNotificationPermission', () => {
    it('should request permission when default', async () => {
      (window as any).Notification.permission = 'default';
      const requestPermission = vi.fn().mockResolvedValue('granted');
      (window as any).Notification.requestPermission = requestPermission;

      await bookingConfirmationService.requestNotificationPermission();

      expect(requestPermission).toHaveBeenCalled();
    });

    it('should not request permission when already granted', async () => {
      (window as any).Notification.permission = 'granted';
      const requestPermission = vi.fn();
      (window as any).Notification.requestPermission = requestPermission;

      await bookingConfirmationService.requestNotificationPermission();

      expect(requestPermission).not.toHaveBeenCalled();
    });
  });
});
