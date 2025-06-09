import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { flightService, airportService, ticketClassService } from '../../services';
import { Flight, Airport, TicketClass } from '../../models';
import TypeAhead from '../common/TypeAhead';
import FlightCard from '../flights/FlightCard';

interface SearchFormData {
  departureAirportId: number;
  arrivalAirportId: number;
  departureDate: string;
  returnDate?: string;
  passengerCount: number;
  ticketClassId: number; // Keep this but we'll use it differently
}

const FlightSearch: React.FC = () => {
  const navigate = useNavigate();
  const [flights, setFlights] = useState<Flight[]>([]);
  const [airports, setAirports] = useState<Airport[]>([]);
  const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
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
            const flightTicketClasses = await flightService.checkFlightAvailability(flight.flightId!);
            return { ...flight, flightTicketClasses };
          } catch (err) {
            console.error(`Could not check availability for flight ${flight.flightCode}:`, err);
            // Return flight without availability data rather than failing completely
            return { ...flight, flightTicketClasses: [] };
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

    // Store booking data in sessionStorage instead of URL parameters
    sessionStorage.setItem('bookingData', JSON.stringify({
      flightId,
      passengers: watch('passengerCount'),
      class: ticketClassId
    }));

    // Navigate to booking page without query parameters
    navigate('/booking');
  };

  return (
    <Container className="py-4">
      <Row>
        <Col>
          <Card className="shadow-sm">
            <Card.Header className="bg-primary text-white">
              <h2 className="mb-0">
                <i className="bi bi-search me-2"></i>
                Search Flights
              </h2>
            </Card.Header>
            <Card.Body className="p-4">
              <Form onSubmit={handleSubmit(onSubmit)}>
                {/* Airport Selection */}
                <Row className="mb-4">
                  <Col md={5}>
                    <Form.Group>
                      <Form.Label className="fw-bold">
                        <i className="bi bi-geo-alt me-1"></i>
                        From
                      </Form.Label>
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
                      <Form.Control
                        type="hidden"
                        {...register('departureAirportId', {
                          required: 'Departure airport is required',
                          validate: (value) => value > 0 || 'Please select a departure airport'
                        })}
                        value={selectedDepartureAirport || ''}
                      />
                      {errors.departureAirportId && (
                        <Form.Text className="text-danger">
                          {errors.departureAirportId.message}
                        </Form.Text>
                      )}
                    </Form.Group>
                  </Col>

                  <Col md={2} className="d-flex align-items-end justify-content-center">
                    <Button
                      variant="outline-secondary"
                      onClick={swapAirports}
                      className="mb-3"
                      title="Swap airports"
                    >
                      <i className="bi bi-arrow-left-right"></i>
                    </Button>
                  </Col>

                  <Col md={5}>
                    <Form.Group>
                      <Form.Label className="fw-bold">
                        <i className="bi bi-geo me-1"></i>
                        To
                      </Form.Label>
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
                      <Form.Control
                        type="hidden"
                        {...register('arrivalAirportId', {
                          required: 'Arrival airport is required',
                          validate: (value) => value > 0 || 'Please select an arrival airport'
                        })}
                        value={selectedArrivalAirport || ''}
                      />
                      {errors.arrivalAirportId && (
                        <Form.Text className="text-danger">
                          {errors.arrivalAirportId.message}
                        </Form.Text>
                      )}
                    </Form.Group>
                  </Col>
                </Row>

                {/* Date Selection */}
                <Row className="mb-4">
                  <Col md={6}>
                    <Form.Group>
                      <Form.Label htmlFor="departureDate" className="fw-bold">
                        <i className="bi bi-calendar-event me-1"></i>
                        Departure Date
                      </Form.Label>
                      <Form.Control
                        id="departureDate"
                        type="date"
                        min={new Date().toISOString().split('T')[0]}
                        {...register('departureDate', { required: 'Departure date is required' })}
                        isInvalid={!!errors.departureDate}
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.departureDate?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  <Col md={6}>
                    <Form.Group>
                      <Form.Label htmlFor="passengerCount" className="fw-bold">
                        <i className="bi bi-people me-1"></i>
                        Passengers
                      </Form.Label>
                      <Form.Select
                        id="passengerCount"
                        {...register('passengerCount', { required: 'Passenger count is required', valueAsNumber: true })}
                        isInvalid={!!errors.passengerCount}
                      >
                        {[...Array(9)].map((_, i) => (
                          <option key={i + 1} value={i + 1}>
                            {i + 1} {i === 0 ? 'Passenger' : 'Passengers'}
                          </option>
                        ))}
                      </Form.Select>
                      <Form.Control.Feedback type="invalid">
                        {errors.passengerCount?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>
                </Row>

                {/* Error Message */}
                {error && (
                  <Alert variant="danger" className="mb-3">
                    <i className="bi bi-exclamation-triangle me-2"></i>
                    {error}
                  </Alert>
                )}

                {/* Search Button */}
                <div className="d-grid">
                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <Spinner
                          as="span"
                          animation="border"
                          size="sm"
                          role="status"
                          aria-hidden="true"
                          className="me-2"
                        />
                        Searching...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-search me-2"></i>
                        Search Flights
                      </>
                    )}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Loading State */}
      {loading && (
        <Row className="mt-4">
          <Col>
            <Card className="text-center">
              <Card.Body className="py-5">
                <Spinner animation="border" variant="primary" className="mb-3" />
                <h5>Searching for flights...</h5>
                <p className="text-muted">Please wait while we find the best options for you.</p>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}

      {/* Search Results */}
      {flights.length > 0 && !loading && (
        <Row className="mt-4">
          <Col>
            <Card>
              <Card.Header className="bg-success text-white">
                <h3 className="mb-0">
                  <i className="bi bi-check-circle me-2"></i>
                  Search Results ({flights.length} flights found)
                </h3>
              </Card.Header>
              <Card.Body className="p-0">
                {flights.map(flight => (
                  <FlightCard
                    key={flight.flightId}
                    flight={flight}
                    onBookFlight={handleBookFlight} searchContext={{
                      passengerCount: watch('passengerCount'),
                      allTicketClasses: ticketClasses,
                      selectedTicketClass: selectedTicketClass === 'all' ? null : (selectedTicketClass as number),
                      searchedForAllClasses: selectedTicketClass === 'all'
                    }}
                  />
                ))}
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}

      {/* No Results */}
      {flights.length === 0 && !loading && error === '' && (
        <Row className="mt-4">
          <Col>
            <Card className="text-center">
              <Card.Body className="py-5">
                <i className="bi bi-search text-muted mb-3" style={{ fontSize: '3rem' }}></i>
                <h5>No flights found</h5>
                <p className="text-muted">Try adjusting your search criteria and search again.</p>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default FlightSearch;
