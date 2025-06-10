import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner, Badge, ListGroup, Modal } from 'react-bootstrap';
import { BookingConfirmation } from '../../services/bookingConfirmationService';
import { ticketService, flightService, passengerService, flightTicketClassService } from '../../services';

const BookingLookup: React.FC = () => {
  const navigate = useNavigate();
  const [searchData, setSearchData] = useState({
    confirmationCode: '',
  });
  const [booking, setBooking] = useState<BookingConfirmation | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showCancelModal, setShowCancelModal] = useState(false);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!searchData.confirmationCode.trim()) {
      setError('Please enter a confirmation code');
      return;
    }

    setLoading(true);
    setError('');
    setBooking(null);

    try {
      // Get tickets by confirmation code from backend
      const tickets = await ticketService.getTicketsOnConfirmationCode(searchData.confirmationCode);

      if (!tickets || tickets.length === 0) {
        setError('No booking found with this confirmation code');
        return;
      }

      // Get flight information for the first ticket (all tickets should have same flight)
      const firstTicket = tickets[0];
      const flight = await flightService.getFlightById(firstTicket.flightId!);

      // Get passenger names for each ticket
      const passengerNames = await Promise.all(
        tickets.map(async (ticket) => {
          try {
            const passenger = await passengerService.getPassengerById(ticket.passengerId!);
            return passenger.passengerName;
          } catch (error) {
            console.error(`Error getting passenger ${ticket.passengerId}:`, error);
            return `Passenger ${ticket.passengerId}`;
          }
        })
      );

      // Create booking confirmation object similar to BookingConfirmation
      const bookingData: BookingConfirmation = {
        confirmationCode: searchData.confirmationCode,
        bookingDate: new Date().toISOString(), // You might want to get actual booking date from ticket
        tickets: tickets,
        passengers: passengerNames, // Now contains actual passenger names
        totalAmount: tickets.reduce((sum, ticket) => sum + (ticket.fare || 0), 0),
        flightInfo: {
          flightCode: flight.flightCode || '',
          departureTime: flight.departureTime || '',
          arrivalTime: flight.arrivalTime || '',
          departureCity: flight.departureCityName || '',
          arrivalCity: flight.arrivalCityName || ''
        }
      };

      setBooking(bookingData);
    } catch (err: any) {
      console.error('Error looking up booking:', err);
      setError('Failed to find booking. Please check your confirmation code and try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async () => {
    setShowCancelModal(true);
  };

  const confirmCancelBooking = async () => {
    if (!booking) return;

    setShowCancelModal(false);
    
    try {
      // Cancel all tickets in the booking
      for (const ticket of booking.tickets) {
        if (ticket.ticketId) {
          await ticketService.deleteTicket(ticket.ticketId);
          await flightTicketClassService.updateRemainingTickets(
            ticket.flightId!, 
            ticket.ticketClassId!, 
            -1
          ); 
        }
      }

      setBooking(null);
      setSearchData({ confirmationCode: '' });
    } catch (err: any) {
      console.error('Error canceling booking:', err);
      alert('Failed to cancel booking: ' + (err.message || 'Unknown error'));
    }
  };

  const handlePrintBooking = () => {
    window.print();
  };

  const handleCloseCancelModal = () => {
    setShowCancelModal(false);
  };

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col lg={8}>
          {/* Header */}
          <div className="text-center mb-5">
            <h1 className="mb-3">Manage Your Booking</h1>
            <p className="text-muted">Enter your booking confirmation code to view and manage your reservation</p>
          </div>

          {/* Search Form */}
          <Card className="mb-4">
            <Card.Header>
              <h4 className="mb-0">Find Your Booking</h4>
            </Card.Header>
            <Card.Body>
              <Form onSubmit={handleSearch}>
                <Row>
                  <Col md={12}>
                    <Form.Group className="mb-3">
                      <Form.Label className="w-100 text-center fw-bold fs-4">Confirmation Code *</Form.Label>
                      <Form.Control
                        type="text"
                        value={searchData.confirmationCode}
                        onChange={(e) => setSearchData(prev => ({
                          ...prev,
                          confirmationCode: e.target.value.toUpperCase()
                        }))}
                        placeholder="FMS-YYYYMMDD-XXXX"
                        required
                      />
                      <Form.Text className="text-muted">
                        Format: FMS-YYYYMMDD-XXXX (e.g., FMS-20240527-A1B2)
                      </Form.Text>
                    </Form.Group>
                  </Col>
                </Row>

                {error && (
                  <Alert variant="danger" className="mb-3">
                    {error}
                  </Alert>
                )}

                <div className="d-flex gap-3">
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <Spinner animation="border" size="sm" className="me-2" />
                        Searching...
                      </>
                    ) : (
                      'Find Booking'
                    )}
                  </Button>
                  <Button
                    variant="outline-secondary"
                    onClick={() => navigate('/')}
                  >
                    Back to Home
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>

          {/* Booking Details */}
          {booking && (
            <Card className="mb-4">
              <Card.Header className="bg-success text-white">
                <div className="d-flex justify-content-between align-items-center">
                  <h4 className="mb-0">Booking Found</h4>
                  <Badge bg="light" text="dark" className="fs-6">
                    Code: {booking.confirmationCode}
                  </Badge>
                </div>
              </Card.Header>
              <Card.Body>
                {/* Flight Information */}
                <div className="mb-4">
                  <h5 className="text-primary mb-3">Flight Information</h5>
                  <Row className="g-3">
                    <Col sm={6}>
                      <strong>Flight:</strong>
                      <div>{booking.flightInfo.flightCode}</div>
                    </Col>
                    <Col sm={6}>
                      <strong>Route:</strong>
                      <div>{booking.flightInfo.departureCity} → {booking.flightInfo.arrivalCity}</div>
                    </Col>
                    <Col sm={6}>
                      <strong>Departure:</strong>
                      <div>{new Date(booking.flightInfo.departureTime).toLocaleString()}</div>
                    </Col>
                    {booking.flightInfo.arrivalTime && (
                      <Col sm={6}>
                        <strong>Arrival:</strong>
                        <div>{new Date(booking.flightInfo.arrivalTime).toLocaleString()}</div>
                      </Col>
                    )}
                  </Row>
                </div>

                {/* Passenger Information */}
                <div className="mb-4">
                  <h5 className="text-primary mb-3">Passenger Information</h5>
                  <ListGroup>
                    {booking.tickets.map((ticket, index) => (
                      <ListGroup.Item key={index} className="d-flex justify-content-between align-items-center">
                        <div>
                          <strong>Passenger {index + 1} : {booking.passengers[index]}</strong>
                          <div className="text-muted">Seat: {ticket.seatNumber}</div>
                        </div>
                        <Badge bg="primary" className="fs-6">${ticket.fare}</Badge>
                      </ListGroup.Item>
                    ))}
                  </ListGroup>
                </div>

                {/* Booking Summary */}
                <div className="mb-0">
                  <h5 className="text-primary mb-3">Booking Summary</h5>
                  <Row className="g-3">
                    <Col sm={6}>
                      <strong>Booking Date:</strong>
                      <div>{new Date(booking.bookingDate).toLocaleDateString()}</div>
                    </Col>
                    <Col sm={6}>
                      <strong>Total Passengers:</strong>
                      <div>{booking.tickets.length}</div>
                    </Col>
                    <Col xs={12}>
                      <div className="border-top pt-3">
                        <Row>
                          <Col>
                            <strong className="fs-5">Total Amount:</strong>
                          </Col>
                          <Col className="text-end">
                            <strong className="fs-4 text-primary">${booking.totalAmount}</strong>
                          </Col>
                        </Row>
                      </div>
                    </Col>
                  </Row>
                </div>
              </Card.Body>

              {/* Booking Actions */}
              <Card.Footer className="bg-light">
                <Row className="g-2">
                  <Col md={6}>
                    <Button
                      onClick={handlePrintBooking}
                      variant="outline-secondary"
                      className="w-100"
                    >
                      Print Booking
                    </Button>
                  </Col>
                  <Col md={6}>
                    <Button
                      onClick={handleCancelBooking}
                      variant="danger"
                      className="w-100"
                    >
                      Cancel Booking
                    </Button>
                  </Col>
                </Row>
              </Card.Footer>
            </Card>
          )}

          {/* Help Section */}
          <Card className="bg-light">
            <Card.Header>
              <h5 className="mb-0">Need Help?</h5>
            </Card.Header>
            <Card.Body>
              <ul className="mb-0">
                <li>Make sure you enter the confirmation code exactly as shown</li>
                <li>The confirmation code format is: FMS-YYYYMMDD-XXXX</li>
                <li>If you can't find your booking, contact customer service</li>
                <li>Guest bookings are stored locally for up to 10 recent bookings</li>
              </ul>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Cancel Confirmation Modal */}
      <Modal 
        show={showCancelModal} 
        onHide={handleCloseCancelModal}
        centered
      >
        <Modal.Header closeButton className="bg-danger text-white">
          <Modal.Title>
            <i className="bi bi-exclamation-triangle me-2"></i>
            Cancel Booking
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          <div className="text-center mb-3">
            <i className="bi bi-exclamation-circle text-danger" style={{ fontSize: '3rem' }}></i>
          </div>
          <h5 className="text-center mb-3">Are you sure you want to cancel this booking?</h5>
          <p className="text-center text-muted mb-0">
            This action cannot be undone. All tickets for this booking will be permanently cancelled.
          </p>
          {booking && (
            <div className="mt-3 p-3 bg-light rounded">
              <div className="text-center">
                <strong>Booking: {booking.confirmationCode}</strong><br/>
                <span className="text-muted">{booking.flightInfo.flightCode} - {booking.tickets.length} passenger(s)</span><br/>
                <span className="text-primary fw-bold">${booking.totalAmount}</span>
              </div>
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button 
            variant="secondary" 
            onClick={handleCloseCancelModal}
          >
            Keep Booking
          </Button>
          <Button 
            variant="danger" 
            onClick={confirmCancelBooking}
          >
            <i className="bi bi-trash me-2"></i>
            Yes, Cancel Booking
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default BookingLookup;
