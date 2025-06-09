import { apiClient } from './api';
import { Ticket } from '../models';

export interface BookingConfirmation {
  confirmationCode: string;
  bookingDate: string;
  tickets: Ticket[];
  passengers: string[];
  totalAmount: number;
  flightInfo: {
    flightCode: string;
    departureTime: string;
    arrivalTime: string;
    departureCity: string;
    arrivalCity: string;
  };
}

export interface BookingLookupRequest {
  confirmationCode: string;
  email?: string;
  citizenId?: string;
}

export class BookingConfirmationService {
  private readonly baseUrl = '/bookings';

  /**
   * Generate a unique booking confirmation code
   * Format: FMS-YYYYMMDD-XXXX (e.g., FMS-20250527-A1B2)
   */
  generateConfirmationCode(): string {
    const today = new Date();
    const dateStr = today.getFullYear().toString() + 
                   (today.getMonth() + 1).toString().padStart(2, '0') + 
                   today.getDate().toString().padStart(2, '0');
    
    // Generate random 4-character suffix
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let suffix = '';
    for (let i = 0; i < 4; i++) {
      suffix += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    
    return `FMS-${dateStr}-${suffix}`;
  }

  /**
   * Store booking confirmation (local storage for guest bookings)
   */
  storeGuestBookingConfirmation(confirmation: BookingConfirmation): void {
    try {
      const existingBookings = this.getStoredGuestBookings();
      existingBookings.push(confirmation);
      
      // Keep only last 10 bookings to avoid storage overflow
      if (existingBookings.length > 10) {
        existingBookings.splice(0, existingBookings.length - 10);
      }
      
      localStorage.setItem('guestBookings', JSON.stringify(existingBookings));
    } catch (error) {
      console.warn('Failed to store guest booking confirmation:', error);
    }
  }

  /**
   * Retrieve stored guest bookings from local storage
   */
  getStoredGuestBookings(): BookingConfirmation[] {
    try {
      const stored = localStorage.getItem('guestBookings');
      return stored ? JSON.parse(stored) : [];
    } catch (error) {
      console.warn('Failed to retrieve guest bookings:', error);
      return [];
    }
  }

  /**
   * Find guest booking by confirmation code
   */
  findGuestBooking(confirmationCode: string): BookingConfirmation | null {
    const bookings = this.getStoredGuestBookings();
    return bookings.find(booking => booking.confirmationCode === confirmationCode) || null;
  }

  /**
   * Look up booking by confirmation code and optional email/citizen ID
   * This would typically call a backend endpoint, but for now uses local storage
   */
  // async lookupBooking(request: BookingLookupRequest): Promise<BookingConfirmation | null> {
  //   // For guest bookings, check local storage first
  //   const localBooking = this.findGuestBooking(request.confirmationCode);
  //   if (localBooking) {
  //     // Verify email or citizen ID if provided
  //     if (request.email) {
  //       const hasMatchingEmail = localBooking.passengerEmails.some(
  //         email => email.toLowerCase() === request.email!.toLowerCase()
  //       );
  //       if (!hasMatchingEmail) {
  //         return null; // Email doesn't match
  //       }
  //     }
  //     return localBooking;
  //   }

  //   // TODO: Add backend API call for registered user bookings
  //   try {
  //     return await apiClient.get(`${this.baseUrl}/lookup/${request.confirmationCode}`, {
  //       params: {
  //         email: request.email,
  //         citizenId: request.citizenId
  //       }
  //     });
  //   } catch (error: any) {
  //     if (error.response?.status === 404) {
  //       return null; // Booking not found
  //     }
  //     throw error;
  //   }
  // }

  /**
   * Cancel booking by confirmation code
   */
  async cancelBooking(confirmationCode: string, reason?: string): Promise<void> {
    // For guest bookings, remove from local storage
    const bookings = this.getStoredGuestBookings();
    const updatedBookings = bookings.filter(booking => booking.confirmationCode !== confirmationCode);
    
    if (updatedBookings.length < bookings.length) {
      localStorage.setItem('guestBookings', JSON.stringify(updatedBookings));
    }

    // TODO: Add backend API call for actual cancellation
    try {
      await apiClient.post(`${this.baseUrl}/cancel/${confirmationCode}`, { reason });
    } catch (error) {
      console.warn('Failed to cancel booking on server:', error);
      // For guest bookings, local removal is sufficient for demo
    }
  }

  /**
   * Create booking confirmation from ticket booking response
   */
  createConfirmation(
    confirmationCode: string,
    tickets: Ticket[], 
    passengers: any[], 
    flightInfo: any
  ): BookingConfirmation {
    const totalAmount = tickets.reduce((sum, ticket) => sum + (ticket.fare || 0), 0);
    
    return {
      confirmationCode,
      bookingDate: new Date().toISOString(),
      tickets,
      passengers,
      totalAmount,
      flightInfo: {
        flightCode: flightInfo.flightCode || '',
        departureTime: flightInfo.departureTime || '',
        arrivalTime: flightInfo.arrivalTime || '',
        departureCity: flightInfo.departureCityName || '',
        arrivalCity: flightInfo.arrivalCityName || ''
      }
    };
  }

  /**
   * Request notification permission for booking confirmations
   */
  async requestNotificationPermission(): Promise<void> {
    if ('Notification' in window && Notification.permission === 'default') {
      await Notification.requestPermission();
    }
  }
}

export const bookingConfirmationService = new BookingConfirmationService();
