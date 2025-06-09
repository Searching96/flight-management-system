import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner, ButtonGroup } from 'react-bootstrap';
import { flightService, airportService, ticketClassService } from '../../services';
import { Flight, Airport, TicketClass, FlightSearchCriteria } from '../../models';
import TypeAhead from '../common/TypeAhead';
import FlightCard from '../flights/FlightCard';

const FlightSearch: React.FC = () => {
  const navigate = useNavigate();
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
  } = useForm<FlightSearchCriteria>({
    defaultValues: {
      passengers: 1
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
  const onSubmit = async (data: FlightSearchCriteria) => {
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
        passengers: data.passengers,
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
      passengerCount: watch('passengers'),
      ticketClassId: ticketClassId
    };

    // Store in sessionStorage for booking form to access
    sessionStorage.setItem('flightSearchContext', JSON.stringify(searchContext));

    // Store booking data in sessionStorage instead of URL parameters
    sessionStorage.setItem('bookingData', JSON.stringify({
      flightId,
      passengers: watch('passengers'),
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
                {/* Trip Type Selection */}
                <Row className="mb-4">
                  <Col>
                    <Form.Label className="fw-bold">Trip Type</Form.Label>
                    <ButtonGroup className="d-block">
                      <Button
                        variant={!isRoundTrip ? "primary" : "outline-primary"}
                        onClick={() => setIsRoundTrip(false)}
                        className="me-2"
                      >
                        <i className="bi bi-arrow-right me-1"></i>
                        One Way
                      </Button>
                      <Button
                        variant={isRoundTrip ? "primary" : "outline-primary"}
                        onClick={() => setIsRoundTrip(true)}
                      >
                        <i className="bi bi-arrow-left-right me-1"></i>
                        Round Trip
                      </Button>
                    </ButtonGroup>
                  </Col>
                </Row>

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
                  <Col md={isRoundTrip ? 6 : 12}>
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

                  {isRoundTrip && (
                    <Col md={6}>
                      <Form.Group>
                        <Form.Label htmlFor="returnDate" className="fw-bold">
                          <i className="bi bi-calendar-check me-1"></i>
                          Return Date
                        </Form.Label>
                        <Form.Control
                          id="returnDate"
                          type="date"
                          min={watch('departureDate') || new Date().toISOString().split('T')[0]}
                          {...register('returnDate', { required: isRoundTrip ? 'Return date is required' : false })}
                          isInvalid={!!errors.returnDate}
                        />
                        <Form.Control.Feedback type="invalid">
                          {errors.returnDate?.message}
                        </Form.Control.Feedback>
                      </Form.Group>
                    </Col>
                  )}
                </Row>

                {/* Passengers and Class Selection */}
                <Row className="mb-4">
                  <Col md={6}>
                    <Form.Group>
                      <Form.Label htmlFor="passengerCount" className="fw-bold">
                        <i className="bi bi-people me-1"></i>
                        Passengers
                      </Form.Label>
                      <Form.Select
                        id="passengerCount"
                        {...register('passengers', { required: 'Passenger count is required', valueAsNumber: true })}
                        isInvalid={!!errors.passengers}
                      >
                        {[...Array(9)].map((_, i) => (
                          <option key={i + 1} value={i + 1}>
                            {i + 1} {i === 0 ? 'Passenger' : 'Passengers'}
                          </option>
                        ))}
                      </Form.Select>
                      <Form.Control.Feedback type="invalid">
                        {errors.passengers?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  <Col md={6}>
                    <Form.Group>
                      <Form.Label htmlFor="ticketClassId" className="fw-bold">
                        <i className="bi bi-star me-1"></i>
                        Ticket Class
                      </Form.Label>
                      <Form.Select
                        id="ticketClassId"
                        value={selectedTicketClass}
                        onChange={e => setSelectedTicketClass(e.target.value === 'all' ? 'all' : Number(e.target.value))}
                      >
                        <option value="all">All Classes</option>
                        {ticketClassOptions.map(tc => (
                          <option key={tc.value} value={tc.value}>{tc.label}</option>
                        ))}
                      </Form.Select>
                      <Form.Control
                        type="hidden"
                        {...register('ticketClassId')}
                        value={selectedTicketClass === 'all' ? 0 : selectedTicketClass}
                      />
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
                      passengerCount: watch('passengers'),
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
