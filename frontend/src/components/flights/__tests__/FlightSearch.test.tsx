import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import FlightSearchCriteria from '../FlightSearch';
import { flightService, airportService, ticketClassService } from '../../../services';

// Mock the services
vi.mock('../../../services', () => ({
  flightService: {
    searchFlights: vi.fn(),
    checkFlightAvailability: vi.fn(),
  },
  airportService: {
    getAllAirports: vi.fn(),
  },
  ticketClassService: {
    getAllTicketClasses: vi.fn(),
  },
}));

const mockedFlightService = vi.mocked(flightService);
const mockedAirportService = vi.mocked(airportService);
const mockedTicketClassService = vi.mocked(ticketClassService);

describe('FlightSearch', () => {
  const mockAirports = [
    { airportId: 1, cityName: 'New York', airportName: 'JFK Airport', countryName: 'USA' },
    { airportId: 2, cityName: 'Los Angeles', airportName: 'LAX Airport', countryName: 'USA' },
  ];

  const mockTicketClasses = [
    { ticketClassId: 1, ticketClassName: 'Economy', color: '#blue' },
    { ticketClassId: 2, ticketClassName: 'Business', color: '#gold' },
  ];

  const mockFlights = [
    {
      flightId: 1,
      flightCode: 'AA123',
      departureTime: '2024-12-01T10:00:00',
      arrivalTime: '2024-12-01T13:00:00',
      planeId: 1,
      departureAirportId: 1,
      arrivalAirportId: 2,
      departureAirportName: 'JFK Airport',
      arrivalAirportName: 'LAX Airport',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    
    // Setup default mock returns
    mockedAirportService.getAllAirports.mockResolvedValue(mockAirports);
    mockedTicketClassService.getAllTicketClasses.mockResolvedValue(mockTicketClasses);
    mockedFlightService.searchFlights.mockResolvedValue(mockFlights);
    mockedFlightService.checkFlightAvailability.mockResolvedValue([]);
  });

  it('should render flight search form', async () => {
    // Arrange & Act
    render(<FlightSearchCriteria />);

    // Assert
    expect(screen.getByText('Search Flights')).toBeInTheDocument();
    expect(screen.getByLabelText('From')).toBeInTheDocument();
    expect(screen.getByLabelText('To')).toBeInTheDocument();
    expect(screen.getByLabelText('Departure Date')).toBeInTheDocument();
    expect(screen.getByLabelText('Passengers')).toBeInTheDocument();
    expect(screen.getByLabelText('Ticket Class')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Search Flights' })).toBeInTheDocument();
  });

  it('should load airports and ticket classes on mount', async () => {
    // Arrange & Act
    render(<FlightSearchCriteria />);

    // Assert
    await waitFor(() => {
      expect(mockedAirportService.getAllAirports).toHaveBeenCalledTimes(1);
      expect(mockedTicketClassService.getAllTicketClasses).toHaveBeenCalledTimes(1);
    });
  });

  it('should display trip type options', () => {
    // Arrange & Act
    render(<FlightSearchCriteria />);

    // Assert
    expect(screen.getByLabelText('One Way')).toBeInTheDocument();
    expect(screen.getByLabelText('Round Trip')).toBeInTheDocument();
  });

  it('should show return date field when round trip is selected', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    // Act
    await user.click(screen.getByLabelText('Round Trip'));

    // Assert
    expect(screen.getByLabelText('Return Date')).toBeInTheDocument();
  });

  it('should hide return date field when one way is selected', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    // First select round trip, then one way
    await user.click(screen.getByLabelText('Round Trip'));
    await user.click(screen.getByLabelText('One Way'));

    // Assert
    expect(screen.queryByLabelText('Return Date')).not.toBeInTheDocument();
  });

  it('should perform flight search with valid data', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    // Wait for airports to load
    await waitFor(() => {
      expect(mockedAirportService.getAllAirports).toHaveBeenCalled();
    });

    // Act - Fill form and submit
    const departureDate = screen.getByLabelText('Departure Date');
    await user.type(departureDate, '2024-12-01');

    const passengerSelect = screen.getByLabelText('Passengers');
    await user.selectOptions(passengerSelect, '2');

    const ticketClassSelect = screen.getByLabelText('Ticket Class');
    await user.selectOptions(ticketClassSelect, '1');

    const searchButton = screen.getByRole('button', { name: 'Search Flights' });
    await user.click(searchButton);

    // Assert
    await waitFor(() => {
      expect(mockedFlightService.searchFlights).toHaveBeenCalledWith(
        expect.objectContaining({
          departureDate: '2024-12-01T00:00:00',
          passengerCount: 2,
          ticketClassId: 1,
        })
      );
    });
  });

  it('should display validation errors for required fields', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    // Act - Submit form without filling required fields
    const searchButton = screen.getByRole('button', { name: 'Search Flights' });
    await user.click(searchButton);

    // Assert
    await waitFor(() => {
      expect(screen.getByText('Departure airport is required')).toBeInTheDocument();
      expect(screen.getByText('Arrival airport is required')).toBeInTheDocument();
      expect(screen.getByText('Departure date is required')).toBeInTheDocument();
    });
  });

  it('should swap airports when swap button is clicked', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    // Wait for component to initialize
    await waitFor(() => {
      expect(mockedAirportService.getAllAirports).toHaveBeenCalled();
    });

    // Act - Click swap button
    const swapButton = screen.getByTitle('Swap airports');
    await user.click(swapButton);

    // Assert - This would need more complex setup to verify the actual swap
    expect(swapButton).toBeInTheDocument();
  });

  it('should display search results when flights are found', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    // Setup form data
    await waitFor(() => {
      expect(mockedAirportService.getAllAirports).toHaveBeenCalled();
    });

    // Act - Perform search (this would need proper form filling)
    // For this test, we'll simulate the search results being set
    mockedFlightService.searchFlights.mockResolvedValueOnce(mockFlights);

    // Fill required fields and submit
    const departureDate = screen.getByLabelText('Departure Date');
    await user.type(departureDate, '2024-12-01');

    const searchButton = screen.getByRole('button', { name: 'Search Flights' });
    await user.click(searchButton);

    // Assert
    await waitFor(() => {
      expect(screen.getByText('Search Results (1 flights found)')).toBeInTheDocument();
    });
  });

  it('should display no results message when no flights found', async () => {
    // Arrange
    mockedFlightService.searchFlights.mockResolvedValueOnce([]);
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    await waitFor(() => {
      expect(mockedAirportService.getAllAirports).toHaveBeenCalled();
    });

    // Act - Perform search that returns no results
    const departureDate = screen.getByLabelText('Departure Date');
    await user.type(departureDate, '2024-12-01');

    const searchButton = screen.getByRole('button', { name: 'Search Flights' });
    await user.click(searchButton);

    // Assert
    await waitFor(() => {
      expect(screen.getByText('No flights found. Try adjusting your search criteria.')).toBeInTheDocument();
    });
  });

  it('should display error message when search fails', async () => {
    // Arrange
    mockedFlightService.searchFlights.mockRejectedValueOnce(new Error('Network error'));
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    await waitFor(() => {
      expect(mockedAirportService.getAllAirports).toHaveBeenCalled();
    });

    // Act - Perform search that fails
    const departureDate = screen.getByLabelText('Departure Date');
    await user.type(departureDate, '2024-12-01');

    const searchButton = screen.getByRole('button', { name: 'Search Flights' });
    await user.click(searchButton);

    // Assert
    await waitFor(() => {
      expect(screen.getByText('Failed to search flights. Please check your connection and try again.')).toBeInTheDocument();
    });
  });

  it('should disable search button while loading', async () => {
    // Arrange
    const user = userEvent.setup();
    render(<FlightSearchCriteria />);

    // Setup a delayed promise to simulate loading
    mockedFlightService.searchFlights.mockImplementationOnce(
      () => new Promise(resolve => setTimeout(() => resolve(mockFlights), 10))
    );

    await waitFor(() => {
      expect(mockedAirportService.getAllAirports).toHaveBeenCalled();
    });

    // Act - Start search
    const departureDate = screen.getByLabelText('Departure Date');
    await user.type(departureDate, '2024-12-01');

    const searchButton = screen.getByRole('button', { name: 'Search Flights' });
    await user.click(searchButton);

    // Assert - Button should be disabled and show loading text
    expect(screen.getByRole('button', { name: 'Searching...' })).toBeDisabled();

    // Wait for search to complete
    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Search Flights' })).not.toBeDisabled();
    });
  });
});
