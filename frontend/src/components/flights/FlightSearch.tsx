import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { flightService, airportService, ticketClassService } from '../../services';
import { Flight, Airport, TicketClass } from '../../models';
import TypeAhead from '../common/TypeAhead';
import FlightCard from '../flights/FlightCard';
import './FlightSearch.css';

interface SearchFormData {
  departureAirportId: number;
  arrivalAirportId: number;
  departureDate: string;
  returnDate?: string;
  passengerCount: number;
  ticketClassId: number; // Keep this but we'll use it differently
}

const FlightSearch: React.FC = () => {
  const [flights, setFlights] = useState<Flight[]>([]);
  const [airports, setAirports] = useState<Airport[]>([]);
  const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [isRoundTrip, setIsRoundTrip] = useState(false);
  const [selectedDepartureAirport, setSelectedDepartureAirport] = useState<number | ''>('');
  const [selectedArrivalAirport, setSelectedArrivalAirport] = useState<number | ''>('');
  const [selectedTicketClass, setSelectedTicketClass] = useState<number | 'all'>('all'); // Changed to support 'all'

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors }
  } = useForm<SearchFormData>({
    defaultValues: {
      passengerCount: 1
    }
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      const [airportData, ticketClassData] = await Promise.all([
        airportService.getAllAirports(),
        ticketClassService.getAllTicketClasses()
      ]);
      setAirports(airportData);
      setTicketClasses(ticketClassData);
    } catch (err: any) {
      setError('Failed to load airports and ticket classes');
    }
  };

  // Transform airports for TypeAhead
  const airportOptions = airports.map(airport => ({
    value: airport.airportId!,
    label: `${airport.cityName} - ${airport.airportName}`,
    city: airport.cityName,
    name: airport.airportName,
    country: airport.countryName
  }));

  // Transform ticket classes for TypeAhead
  const ticketClassOptions = ticketClasses.map(tc => ({
    value: tc.ticketClassId!,
    label: tc.ticketClassName,
    color: tc.color
  }));
  const onSubmit = async (data: SearchFormData) => {
    try {
      setLoading(true);
      setError('');
      
      // Validate airport selection
      if (!selectedDepartureAirport || !selectedArrivalAirport) {
        setError('Please select both departure and arrival airports');
        setLoading(false);
        return;
      }

      if (selectedDepartureAirport === selectedArrivalAirport) {
        setError('Departure and arrival airports must be different');
        setLoading(false);
        return;
      }
      
      const searchCriteria = {
        departureAirportId: selectedDepartureAirport as number,
        arrivalAirportId: selectedArrivalAirport as number,
        departureDate: data.departureDate + 'T00:00:00',
        returnDate: isRoundTrip && data.returnDate ? data.returnDate + 'T00:00:00' : undefined,
        passengerCount: data.passengerCount,
        // Send 0 for "all classes" or the specific class ID
        ticketClassId: selectedTicketClass === 'all' ? 0 : (selectedTicketClass as number)
      };

      console.log('Sending search criteria:', searchCriteria);
      const results = await flightService.searchFlights(searchCriteria);
      
      // Get availability for all ticket classes of each flight
      const flightsWithAvailability = await Promise.all(
        results.map(async (flight) => {
          try {
            const availability = await flightService.checkFlightAvailability(flight.flightId!);
            return { ...flight, availability };
          } catch (err) {
            console.error(`Could not check availability for flight ${flight.flightCode}:`, err);
            // Return flight without availability data rather than failing completely
            return { ...flight, availability: [] };
          }
        })
      );
      
      setFlights(flightsWithAvailability);
    } catch (err: any) {
      console.error('Flight search error:', err);
      // Provide more specific error messages
      if (err.response?.status === 400) {
        setError('Invalid search criteria. Please check your input and try again.');
      } else if (err.response?.status === 500) {
        setError('Server error occurred. Please try again in a moment.');
      } else if (!navigator.onLine) {
        setError('No internet connection. Please check your connection and try again.');
      } else {
        setError('Failed to search flights. Please check your connection and try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const swapAirports = () => {
    const tempDeparture = selectedDepartureAirport;
    setSelectedDepartureAirport(selectedArrivalAirport);
    setSelectedArrivalAirport(tempDeparture);
  };

  const handleBookFlight = (flightId: number, ticketClassId: number) => {
    // Store search context for better UX
    const searchContext = {
      departureAirportId: selectedDepartureAirport,
      arrivalAirportId: selectedArrivalAirport,
      departureDate: watch('departureDate'),
      passengerCount: watch('passengerCount'),
      ticketClassId: ticketClassId
    };
    
    // Store in sessionStorage for booking form to access
    sessionStorage.setItem('flightSearchContext', JSON.stringify(searchContext));
    
    // Navigate to booking page with flight ID
    window.location.href = `/booking?flightId=${flightId}&passengers=${watch('passengerCount')}&class=${ticketClassId}`;
  };

  return (
    <div className="flight-search">
      <div className="search-container">
        <h2>Search Flights</h2>
        
        <form onSubmit={handleSubmit(onSubmit)} className="search-form">
          {/* Trip Type Selection */}
          <div className="trip-type-selector">
            <label>
              <input
                type="radio"
                checked={!isRoundTrip}
                onChange={() => setIsRoundTrip(false)}
              />
              One Way
            </label>
            <label>
              <input
                type="radio"
                checked={isRoundTrip}
                onChange={() => setIsRoundTrip(true)}
              />
              Round Trip
            </label>
          </div>

          {/* Airport Selection */}
          <div className="airport-selection">
            <div className="form-group airport-group">
              <label>From</label>
              <TypeAhead
                options={airportOptions}
                value={selectedDepartureAirport}
                onChange={(option) => {
                  const airportId = option?.value as number || '';
                  setSelectedDepartureAirport(airportId);
                  setValue('departureAirportId', Number(airportId) || 0);
                }}
                placeholder="Departure city or airport..."
                error={!!errors.departureAirportId}
              />
              <input
                type="hidden"
                {...register('departureAirportId', {
                  required: 'Departure airport is required',
                  validate: (value) => value > 0 || 'Please select a departure airport'
                })}
                value={selectedDepartureAirport || ''}
              />
              {errors.departureAirportId && (
                <span className="field-error">{errors.departureAirportId.message}</span>
              )}
            </div>

            <button
              type="button"
              className="swap-button"
              onClick={swapAirports}
              title="Swap airports"
            >
              ⇄
            </button>

            <div className="form-group airport-group">
              <label>To</label>
              <TypeAhead
                options={airportOptions}
                value={selectedArrivalAirport}
                onChange={(option) => {
                  const airportId = option?.value as number || '';
                  setSelectedArrivalAirport(airportId);
                  setValue('arrivalAirportId', Number(airportId) || 0);
                }}
                placeholder="Arrival city or airport..."
                error={!!errors.arrivalAirportId}
              />
              <input
                type="hidden"
                {...register('arrivalAirportId', {
                  required: 'Arrival airport is required',
                  validate: (value) => value > 0 || 'Please select an arrival airport'
                })}
                value={selectedArrivalAirport || ''}
              />
              {errors.arrivalAirportId && (
                <span className="field-error">{errors.arrivalAirportId.message}</span>
              )}
            </div>
          </div>

          {/* Date Selection */}
          <div className="date-selection">
            <div className="form-group">
              <label htmlFor="departureDate">Departure Date</label>
              <input
                id="departureDate"
                className=""
                min={new Date().toISOString().split('T')[0]}
                type="date"
                {...register('departureDate', { required: 'Departure date is required' })}
              />
            </div>

            {isRoundTrip && (
              <div className="form-group">
                <label htmlFor="returnDate">Return Date</label>
                <input
                  id="returnDate"
                  className=""
                  min={watch('departureDate') || new Date().toISOString().split('T')[0]}
                  type="date"
                  {...register('returnDate', { required: 'Return date is required' })}
                />
              </div>
            )}
          </div>

          {/* Passengers and Class Selection */}
          <div className="passenger-class-selection">
            <div className="form-group">
              <label htmlFor="passengerCount">Passengers</label>
              <select
                id="passengerCount"
                className=""
                {...register('passengerCount', { required: 'Passenger count is required', valueAsNumber: true })}
              >
                {[...Array(9)].map((_, i) => (
                  <option key={i + 1} value={i + 1}>
                    {i + 1} {i === 0 ? 'Passenger' : 'Passengers'}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="ticketClassId">Ticket Class</label>
              <select
                id="ticketClassId"
                className="ticket-class-select"
                value={selectedTicketClass}
                onChange={e => setSelectedTicketClass(e.target.value === 'all' ? 'all' : Number(e.target.value))}
              >
                <option value="all">All Classes</option>
                {ticketClassOptions.map(tc => (
                  <option key={tc.value} value={tc.value}>{tc.label}</option>
                ))}
              </select>
            </div>
            <input
              type="hidden"
              {...register('ticketClassId')}
              value={selectedTicketClass === 'all' ? 0 : selectedTicketClass}
            />
          </div>

          {/* Search Button */}
          <button
            type="submit"
            className="search-button"
            disabled={loading}
            aria-busy={loading}
          >
            {loading ? 'Searching...' : 'Search Flights'}
          </button>

          {error && <div className="error-message">{error}</div>}
        </form>
      </div>

      {/* Search Results */}
      {loading && (
        <div className="search-loading">
          <p>Searching for flights...</p>
        </div>
      )}

      {flights.length > 0 && !loading && (
        <div className="search-results">
          <h3>Search Results ({flights.length} flights found)</h3>
          <div className="flights-list">
            {flights.map(flight => (
              <FlightCard
                key={flight.flightId}
                flight={flight}
                onBookFlight={handleBookFlight}
                searchContext={{
                  passengerCount: watch('passengerCount'),
                  allTicketClasses: ticketClasses,
                  availability: (flight as any).availability || [],
                  selectedTicketClass: selectedTicketClass === 'all' ? null : (selectedTicketClass as number),
                  searchedForAllClasses: selectedTicketClass === 'all'
                }}
              />
            ))}
          </div>
        </div>
      )}

      {flights.length === 0 && !loading && error === '' && (
        <div className="no-results">
          <p>No flights found. Try adjusting your search criteria.</p>
        </div>
      )}
    </div>
  );
};

export default FlightSearch;
