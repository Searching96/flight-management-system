import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Button, Alert, Badge, ListGroup } from 'react-bootstrap';
import { BookingConfirmation as BookingConfirmationType } from '../../services/bookingConfirmationService';

const BookingConfirmation: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  const { confirmationCode, confirmationData, message } = location.state || {};
  if (!confirmationCode || !confirmationData) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Card>
              <Card.Body className="text-center py-5">
                <Alert variant="danger" className="mb-4">
                  No booking confirmation data found. Please check your booking details.
                </Alert>
                <Button onClick={() => navigate('/')} variant="primary">
                  Return to Home
                </Button>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  const booking: BookingConfirmationType = confirmationData;

  const handlePrint = () => {
    window.print();
  };

  const handleEmailCopy = () => {
    const emailText = `
Booking Confirmation: ${booking.confirmationCode}

Flight: ${booking.flightInfo.flightCode}
Route: ${booking.flightInfo.departureCity} → ${booking.flightInfo.arrivalCity}
Departure: ${new Date(booking.flightInfo.departureTime).toLocaleString()}
Passengers: ${booking.passengerEmails.length}
Total Amount: $${booking.totalAmount}

Please save this confirmation code for future reference.
    `;
    
    navigator.clipboard.writeText(emailText).then(() => {
      alert('Booking details copied to clipboard!');
    });
  };

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col lg={8}>
          {/* Success Header */}
          <Card className="mb-4 border-success">
            <Card.Body className="text-center py-4">
              <div className="display-1 text-success mb-3">✅</div>
              <h1 className="text-success mb-3">Booking Confirmed!</h1>
              {message && (
                <Alert variant="success" className="mb-0">
                  {message}
                </Alert>
              )}
            </Card.Body>
          </Card>

          {/* Confirmation Code */}
          <Card className="mb-4 border-primary">
            <Card.Header className="bg-primary text-white">
              <h4 className="mb-0">Your Confirmation Code</h4>
            </Card.Header>
            <Card.Body className="text-center">
              <div className="bg-light p-4 rounded mb-3">
                <h2 className="text-primary fw-bold mb-0 font-monospace">
                  {booking.confirmationCode}
                </h2>
              </div>
              <Alert variant="warning" className="mb-0">
                <strong>⚠️ Important:</strong> Please save this confirmation code. 
                You'll need it to retrieve or manage your booking later.
              </Alert>
            </Card.Body>
          </Card>

          {/* Booking Details */}
          <Card className="mb-4">
            <Card.Header>
              <h4 className="mb-0">Booking Details</h4>
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
                    <div>
                      {new Date(booking.flightInfo.departureTime).toLocaleDateString()} at{' '}
                      {new Date(booking.flightInfo.departureTime).toLocaleTimeString()}
                    </div>
                  </Col>
                  {booking.flightInfo.arrivalTime && (
                    <Col sm={6}>
                      <strong>Arrival:</strong>
                      <div>
                        {new Date(booking.flightInfo.arrivalTime).toLocaleDateString()} at{' '}
                        {new Date(booking.flightInfo.arrivalTime).toLocaleTimeString()}
                      </div>
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
                        <strong>Passenger {index + 1}</strong>
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
          </Card>

          {/* Action Buttons */}
          <Card className="mb-4">
            <Card.Body>
              <Row className="g-3">
                <Col md={6} lg={3}>
                  <Button 
                    onClick={handlePrint} 
                    variant="outline-secondary"
                    className="w-100"
                  >
                    Print Confirmation
                  </Button>
                </Col>
                <Col md={6} lg={3}>
                  <Button 
                    onClick={handleEmailCopy} 
                    variant="outline-secondary"
                    className="w-100"
                  >
                    Copy Details
                  </Button>
                </Col>
                <Col md={6} lg={3}>
                  <Button 
                    onClick={() => navigate('/booking-lookup')} 
                    variant="primary"
                    className="w-100"
                  >
                    Manage Booking
                  </Button>
                </Col>
                <Col md={6} lg={3}>
                  <Button 
                    onClick={() => navigate('/')} 
                    variant="success"
                    className="w-100"
                  >
                    Book Another Flight
                  </Button>
                </Col>
              </Row>
            </Card.Body>
          </Card>

          {/* Next Steps */}
          <Card className="bg-light">
            <Card.Header>
              <h4 className="mb-0">What's Next?</h4>
            </Card.Header>
            <Card.Body>
              <ul className="mb-0">
                <li>Save your confirmation code: <strong className="text-primary">{booking.confirmationCode}</strong></li>
                <li>Arrive at the airport at least 2 hours before departure</li>
                <li>Bring valid ID and your confirmation code</li>
                <li>You can manage your booking using the "Manage Booking" button above</li>
              </ul>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default BookingConfirmation;
