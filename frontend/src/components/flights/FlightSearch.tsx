import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { flightService, airportService, ticketClassService, parameterService } from '../../services';
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
  const [selectedDepartureAirport, setSelectedDepartureAirport] = useState<number | ''>('');
  const [selectedArrivalAirport, setSelectedArrivalAirport] = useState<number | ''>('');
  const [selectedTicketClass, setSelectedTicketClass] = useState<number | 'all'>('all'); // Changed to support 'all'
  const [minBookingDate, setMinBookingDate] = useState<string>('');

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors }
  } = useForm<FlightSearchCriteria>({
    defaultValues: {
      passengerCount: 1
    }
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      const [airportData, ticketClassData, parameterData] = await Promise.all([
        airportService.getAllAirports(),
        ticketClassService.getAllTicketClasses(),
        parameterService.getAllParameters()
      ]);
      setAirports(airportData);
      setTicketClasses(ticketClassData);
      console.log('Parameters:', parameterData);
      // Set default ticket class to 'all'
      // Calculate minimum booking date
      const minAdvanceDuration = parameterData.minBookingInAdvanceDuration;
      console.log('Minimum booking in advance duration:', minAdvanceDuration);
      const minDate = new Date();
      minDate.setDate(minDate.getDate() + minAdvanceDuration + 1);
      setMinBookingDate(minDate.toISOString().split('T')[0]);
    } catch (err: any) {
      console.error('Error loading airports and ticket classes:', err);
      setError('Failed to load airports and ticket classes');
      // Fallback to today's date if parameter fetch fails
      setMinBookingDate(new Date().toISOString().split('T')[0]);
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

  const onSubmit = async (data: FlightSearchCriteria) => {
    try {
      setLoading(true);
      setError('');

      // Validate airport selection
      if (!selectedDepartureAirport || !selectedArrivalAirport) {
        setError('Vui lòng chọn cả sân bay khởi hành và sân bay đến');
        setLoading(false);
        return;
      }

      if (selectedDepartureAirport === selectedArrivalAirport) {
        setError('Sân bay khởi hành và sân bay đến phải khác nhau');
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
        setError('Tiêu chí tìm kiếm không hợp lệ. Vui lòng kiểm tra thông tin và thử lại.');
      } else if (err.response?.status === 500) {
        setError('Lỗi máy chủ. Vui lòng thử lại sau một lúc.');
      } else if (!navigator.onLine) {
        setError('Không có kết nối internet. Vui lòng kiểm tra kết nối và thử lại.');
      } else {
        setError('Không thể tìm kiếm chuyến bay. Vui lòng kiểm tra kết nối và thử lại.');
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
      passengerCount: watch('passengerCount'),
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
                Tìm kiếm chuyến bay
              </h2>
            </Card.Header>
            <Card.Body className="p-4">
              <Form onSubmit={handleSubmit(onSubmit)}>
                {/* Airport Selection */}
                <Row className="mb-4 align-items-end">
                  <Col md={5}>
                    <Form.Group>
                      <Form.Label className="fw-bold">
                        <i className="bi bi-geo-alt me-1"></i>
                        Từ
                      </Form.Label>
                      <TypeAhead
                        options={airportOptions}
                        value={selectedDepartureAirport}
                        onChange={(option) => {
                          const airportId = option?.value as number || '';
                          setSelectedDepartureAirport(airportId);
                          setValue('departureAirportId', Number(airportId) || 0);
                        }}
                        placeholder="Thành phố hoặc sân bay khởi hành..."
                        error={!!errors.departureAirportId}
                      />
                      <Form.Control
                        type="hidden"
                        {...register('departureAirportId', {
                          required: 'Sân bay khởi hành là bắt buộc',
                          validate: (value) => value > 0 || 'Vui lòng chọn sân bay khởi hành'
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

                  <Col md={2} className="d-flex justify-content-center">
                    <Button
                      variant="outline-secondary"
                      onClick={swapAirports}
                      title="Đổi sân bay"
                      style={{ height: '38px' }}
                    >
                      <i className="bi bi-arrow-left-right"></i>
                    </Button>
                  </Col>

                  <Col md={5}>
                    <Form.Group>
                      <Form.Label className="fw-bold">
                        <i className="bi bi-geo me-1"></i>
                        Đến
                      </Form.Label>
                      <TypeAhead
                        options={airportOptions}
                        value={selectedArrivalAirport}
                        onChange={(option) => {
                          const airportId = option?.value as number || '';
                          setSelectedArrivalAirport(airportId);
                          setValue('arrivalAirportId', Number(airportId) || 0);
                        }}
                        placeholder="Thành phố hoặc sân bay đến..."
                        error={!!errors.arrivalAirportId}
                      />
                      <Form.Control
                        type="hidden"
                        {...register('arrivalAirportId', {
                          required: 'Sân bay đến là bắt buộc',
                          validate: (value) => value > 0 || 'Vui lòng chọn sân bay đến'
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
                        <i className="bi bi-calendar me-1"></i>
                        Ngày khởi hành
                      </Form.Label>
                      <Form.Control
                        id="departureDate"
                        type="date"
                        min={minBookingDate || new Date().toISOString().split('T')[0]}
                        {...register('departureDate', { required: 'Ngày khởi hành là bắt buộc' })}
                        isInvalid={!!errors.departureDate}
                      />
                    </Form.Group>
                  </Col>

                  <Col md={6}>
                    <Form.Group>
                      <Form.Label htmlFor="passengerCount" className="fw-bold">
                        <i className="bi bi-people me-1"></i>
                        Hành khách
                      </Form.Label>
                      <Form.Select
                        id="passengerCount"
                        {...register('passengerCount', { required: 'Số lượng hành khách là bắt buộc', valueAsNumber: true })}
                        isInvalid={!!errors.passengerCount}
                      >
                        {[...Array(9)].map((_, i) => (
                          <option key={i + 1} value={i + 1}>
                            {i + 1} Hành khách
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
                        Đang tìm kiếm...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-search me-2"></i>
                        Tìm chuyến bay
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
                <h5>Đang tìm kiếm chuyến bay...</h5>
                <p className="text-muted">Vui lòng đợi trong khi chúng tôi tìm những lựa chọn tốt nhất cho bạn.</p>
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
                  Kết quả tìm kiếm ({flights.length} chuyến bay được tìm thấy)
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
                <h5>Không tìm thấy chuyến bay</h5>
                <p className="text-muted">Hãy thử điều chỉnh tiêu chí tìm kiếm và tìm kiếm lại.</p>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default FlightSearch;
