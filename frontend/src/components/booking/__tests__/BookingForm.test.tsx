import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import BookingForm from '../BookingForm';
import { useAuth } from '../../../hooks/useAuth';
import { flightService, ticketService, passengerService, bookingConfirmationService } from '../../../services';

// Mock dependencies
vi.mock('../../../hooks/useAuth');
vi.mock('../../../services');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useParams: () => ({ flightId: '1' }),
    useLocation: () => ({ state: { passengerCount: 2 } }),
    useNavigate: () => vi.fn(),
  };
});

const mockUseAuth = vi.mocked(useAuth);
const mockFlightService = vi.mocked(flightService);
const mockTicketService = vi.mocked(ticketService);
const mockPassengerService = vi.mocked(passengerService);
const mockBookingConfirmationService = vi.mocked(bookingConfirmationService);

// Test wrapper component
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>{children}</BrowserRouter>
);

describe('BookingForm', () => {  const mockFlight = {
    flightId: 1,
    flightCode: 'FMS001',
    departureCityName: 'New York',
    arrivalCityName: 'Los Angeles',
    departureTime: '2025-06-01T10:00:00Z',
    arrivalTime: '2025-06-01T14:00:00Z',
    planeId: 1,
    departureAirportId: 1,
    arrivalAirportId: 2,
  };

  const mockTicketClasses = [
    {
      ticketClassId: 1,
      ticketClassName: 'Economy',
      specifiedFare: 299.99,
    },
    {
      ticketClassId: 2,
      ticketClassName: 'Business',
      specifiedFare: 599.99,
    },
  ];
  const mockUser = {
    accountId: 1,
    accountName: 'testuser',
    email: 'test@example.com',
    accountType: 1,
  };

  beforeEach(() => {
    vi.clearAllMocks();    mockUseAuth.mockReturnValue({
      user: null,
      login: vi.fn(),
      logout: vi.fn(),
      register: vi.fn(),
      loading: false,
    });

    mockFlightService.getFlightById.mockResolvedValue(mockFlight);
    mockFlightService.getFlightTicketClassesByFlightId.mockResolvedValue(mockTicketClasses);
    mockPassengerService.validatePassengerData.mockReturnValue([]);
    mockPassengerService.transformPassengerData.mockImplementation((data) => ({
      passengerName: `${data.firstName} ${data.lastName}`,
      email: data.email,
      citizenId: data.citizenId,
      phoneNumber: data.phoneNumber,
    }));
    mockPassengerService.findExistingPassenger.mockResolvedValue(null);
  });

  describe('Component Rendering', () => {
    it('should render loading state initially', () => {
      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      expect(screen.getByText('Loading booking form...')).toBeInTheDocument();
    });

    it('should render booking form after loading', async () => {
      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
        expect(screen.getByText('FMS001')).toBeInTheDocument();
        expect(screen.getByText('New York → Los Angeles')).toBeInTheDocument();
      });
    });

    it('should render error message when flight not found', async () => {
      mockFlightService.getFlightById.mockRejectedValue(new Error('Flight not found'));

      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Failed to load booking information')).toBeInTheDocument();
      });
    });
  });

  describe('Passenger Form', () => {
    beforeEach(async () => {
      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
      });
    });

    it('should render passenger forms based on passenger count', () => {
      expect(screen.getByText('Passenger 1')).toBeInTheDocument();
      expect(screen.getByText('Passenger 2')).toBeInTheDocument();
    });

    it('should validate required fields', async () => {
      const submitButton = screen.getByRole('button', { name: /book flight/i });
      
      // Select a ticket class first
      const economyRadio = screen.getByDisplayValue('1');
      fireEvent.click(economyRadio);

      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('First name is required')).toBeInTheDocument();
        expect(screen.getByText('Last name is required')).toBeInTheDocument();
        expect(screen.getByText('Email is required')).toBeInTheDocument();
        expect(screen.getByText('Citizen ID is required')).toBeInTheDocument();
      });
    });

    it('should validate email format', async () => {
      const emailInput = screen.getAllByPlaceholderText('Enter email address')[0];
      fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
      fireEvent.blur(emailInput);

      await waitFor(() => {
        expect(screen.getByText('Invalid email address')).toBeInTheDocument();
      });
    });
  });

  describe('Ticket Class Selection', () => {
    beforeEach(async () => {
      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
      });
    });

    it('should render ticket class options', () => {
      expect(screen.getByText('Economy')).toBeInTheDocument();
      expect(screen.getByText('Business')).toBeInTheDocument();
      expect(screen.getByText('$299.99')).toBeInTheDocument();
      expect(screen.getByText('$599.99')).toBeInTheDocument();
    });

    it('should update booking summary when class selected', () => {
      const economyRadio = screen.getByDisplayValue('1');
      fireEvent.click(economyRadio);

      expect(screen.getByText('Economy')).toBeInTheDocument();
      expect(screen.getByText('$599.98')).toBeInTheDocument(); // 299.99 * 2 passengers
    });

    it('should require ticket class selection', async () => {
      const submitButton = screen.getByRole('button', { name: /book flight/i });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Please select a ticket class')).toBeInTheDocument();
      });
    });
  });

  describe('Guest vs Authenticated User Booking', () => {
    it('should not show frequent flyer option for guest users', async () => {
      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
      });

      expect(screen.queryByText('Frequent Flyer Program')).not.toBeInTheDocument();
    });    it('should show frequent flyer option for authenticated users', async () => {
      mockUseAuth.mockReturnValue({
        user: mockUser,
        login: vi.fn(),
        logout: vi.fn(),
        register: vi.fn(),
        loading: false,
      });

      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Frequent Flyer Program')).toBeInTheDocument();
        expect(screen.getByText('Join frequent flyer program and link this booking to your account')).toBeInTheDocument();
      });
    });
  });

  describe('Form Submission', () => {
    const fillPassengerForm = (passengerIndex: number, data: any) => {
      const firstNameInputs = screen.getAllByPlaceholderText('Enter first name');
      const lastNameInputs = screen.getAllByPlaceholderText('Enter last name');
      const emailInputs = screen.getAllByPlaceholderText('Enter email address');
      const citizenIdInputs = screen.getAllByPlaceholderText('Enter citizen ID number');

      fireEvent.change(firstNameInputs[passengerIndex], { target: { value: data.firstName } });
      fireEvent.change(lastNameInputs[passengerIndex], { target: { value: data.lastName } });
      fireEvent.change(emailInputs[passengerIndex], { target: { value: data.email } });
      fireEvent.change(citizenIdInputs[passengerIndex], { target: { value: data.citizenId } });
    };

    it('should submit guest booking successfully', async () => {      mockTicketService.bookTickets.mockResolvedValue([]);
      mockBookingConfirmationService.generateConfirmationCode.mockReturnValue('FMS-20250527-A1B2');
      mockBookingConfirmationService.createConfirmation.mockReturnValue({
        confirmationCode: 'FMS-20250527-A1B2',
        bookingDate: new Date().toISOString(),
        tickets: [],
        passengerEmails: [],
        totalAmount: 599.98,
        flightInfo: {
          flightCode: 'FMS001',
          departureTime: '2025-06-01T10:00:00Z',
          arrivalTime: '2025-06-01T14:00:00Z',
          departureCity: 'New York',
          arrivalCity: 'Los Angeles',
        },
      });

      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
      });

      // Select ticket class
      const economyRadio = screen.getByDisplayValue('1');
      fireEvent.click(economyRadio);

      // Fill passenger forms
      fillPassengerForm(0, {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        citizenId: 'ID123456789',
      });

      fillPassengerForm(1, {
        firstName: 'Jane',
        lastName: 'Doe',
        email: 'jane@example.com',
        citizenId: 'ID987654321',
      });

      // Submit form
      const submitButton = screen.getByRole('button', { name: /book flight/i });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(mockTicketService.bookTickets).toHaveBeenCalledWith({
          flightId: 1,
          customerId: null,
          ticketClassId: 1,
          passengers: expect.arrayContaining([
            expect.objectContaining({
              firstName: 'John',
              lastName: 'Doe',
              email: 'john@example.com',
              citizenId: 'ID123456789',
            }),
          ]),
          seatNumbers: ['A1', 'A2'],
        });
      });
    });

    it('should handle booking validation errors', async () => {
      mockPassengerService.validatePassengerData.mockReturnValue(['Email is required']);

      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
      });

      // Select ticket class
      const economyRadio = screen.getByDisplayValue('1');
      fireEvent.click(economyRadio);

      // Fill incomplete passenger form
      fillPassengerForm(0, {
        firstName: 'John',
        lastName: 'Doe',
        email: '', // Invalid email
        citizenId: 'ID123456789',
      });

      // Submit form
      const submitButton = screen.getByRole('button', { name: /book flight/i });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Please correct the following errors: Email is required')).toBeInTheDocument();
      });
    });

    it('should show warnings for existing passengers', async () => {
      mockPassengerService.findExistingPassenger.mockResolvedValue({
        passengerId: 1,
        passengerName: 'John Doe',
        email: 'john@example.com',
        citizenId: 'ID123456789',
        phoneNumber: '+1 555-0123',
      });

      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
      });

      // Select ticket class
      const economyRadio = screen.getByDisplayValue('1');
      fireEvent.click(economyRadio);

      // Fill passenger form with existing citizen ID
      fillPassengerForm(0, {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        citizenId: 'ID123456789',
      });

      fillPassengerForm(1, {
        firstName: 'Jane',
        lastName: 'Doe',
        email: 'jane@example.com',
        citizenId: 'ID987654321',
      });

      // Submit form
      const submitButton = screen.getByRole('button', { name: /book flight/i });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('⚠️ Warnings:')).toBeInTheDocument();
        expect(screen.getByText('Passenger with Citizen ID ID123456789 already exists in the system.')).toBeInTheDocument();
      });
    });

    it('should handle booking submission errors', async () => {
      mockTicketService.bookTickets.mockRejectedValue(new Error('Booking failed'));

      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
      });

      // Select ticket class
      const economyRadio = screen.getByDisplayValue('1');
      fireEvent.click(economyRadio);

      // Fill passenger forms
      fillPassengerForm(0, {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        citizenId: 'ID123456789',
      });

      fillPassengerForm(1, {
        firstName: 'Jane',
        lastName: 'Doe',
        email: 'jane@example.com',
        citizenId: 'ID987654321',
      });

      // Submit form
      const submitButton = screen.getByRole('button', { name: /book flight/i });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Booking failed')).toBeInTheDocument();
      });
    });
  });

  describe('Booking Summary', () => {
    beforeEach(async () => {
      render(
        <TestWrapper>
          <BookingForm />
        </TestWrapper>
      );

      await waitFor(() => {
        expect(screen.getByText('Complete Your Booking')).toBeInTheDocument();
      });
    });

    it('should display correct booking summary', () => {
      expect(screen.getByText('FMS001')).toBeInTheDocument();
      expect(screen.getByText('2')).toBeInTheDocument(); // Passenger count

      // Select economy class
      const economyRadio = screen.getByDisplayValue('1');
      fireEvent.click(economyRadio);

      expect(screen.getByText('Economy')).toBeInTheDocument();
      expect(screen.getByText('$299.99')).toBeInTheDocument(); // Price per ticket
      expect(screen.getByText('$599.98')).toBeInTheDocument(); // Total price
    });

    it('should update total when different class selected', () => {
      // Select business class
      const businessRadio = screen.getByDisplayValue('2');
      fireEvent.click(businessRadio);

      expect(screen.getByText('Business')).toBeInTheDocument();
      expect(screen.getByText('$599.99')).toBeInTheDocument(); // Price per ticket
      expect(screen.getByText('$1199.98')).toBeInTheDocument(); // Total price
    });
  });
});
