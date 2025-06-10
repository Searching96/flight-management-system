import React, { useState } from 'react';
import { Card, Row, Col, Button, Badge, ListGroup } from 'react-bootstrap';
import { Flight, TicketClass } from '../../models';

interface FlightCardProps {
  flight: Flight;
  onBookFlight: (flightId: number, ticketClassId: number) => void;
  searchContext?: {
    passengerCount: number;
    allTicketClasses: TicketClass[];
    selectedTicketClass?: number | null;
    searchedForAllClasses?: boolean;
  };
}

const FlightCard: React.FC<FlightCardProps> = ({ 
  flight, 
  onBookFlight, 
  searchContext 
}) => {
  const [selectedClassForBooking, setSelectedClassForBooking] = useState<number | null>(
    searchContext?.selectedTicketClass || null
  );

  const formatTime = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  };

  const formatDate = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    });
  };

  const calculateDuration = () => {
    const departure = new Date(flight.departureTime);
    const arrival = new Date(flight.arrivalTime);
    const durationMs = arrival.getTime() - departure.getTime();
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    return `${hours}h ${minutes}m`;
  };

  const getAvailableTicketClasses = () => {
    if (!flight.flightTicketClasses || !searchContext?.allTicketClasses) {
      return [];
    }

    const requestedSeats = searchContext.passengerCount || 1;

    return flight.flightTicketClasses
      .filter(ftc => ftc.remainingTicketQuantity && ftc.remainingTicketQuantity >= requestedSeats)
      .map(ftc => {
        const ticketClass = searchContext.allTicketClasses.find(
          tc => tc.ticketClassId === ftc.ticketClassId
        );
        return {
          ...ftc,
          ticketClass,
          price: Number(ftc.specifiedFare)
        };
      })
      .sort((a, b) => a.price - b.price); // Sort by price
  };

  const getLowestPrice = () => {
    const availableClasses = getAvailableTicketClasses();
    return availableClasses.length > 0 ? availableClasses[0].price : null;
  };

  const isFlightAvailable = () => {
    return getAvailableTicketClasses().length > 0;
  };

  const handleClassSelection = (ticketClassId: number) => {
    setSelectedClassForBooking(ticketClassId);
  };

  const handleBooking = () => {
    if (selectedClassForBooking) {
      onBookFlight(flight.flightId!, selectedClassForBooking);
    }
  };

  const availableClasses = getAvailableTicketClasses();
  const isAvailable = isFlightAvailable();
  const lowestPrice = getLowestPrice();
  const showAllClasses = searchContext?.searchedForAllClasses || availableClasses.length > 1;
  return (
    <Card className="mb-3 shadow-sm border-0">
      <Card.Header className="bg-light border-0">
        <Row className="align-items-center">
          <Col>
            <div className="d-flex align-items-center">
              <Badge bg="primary" className="me-2 fs-6">
                {flight.flightCode}
              </Badge>
              <span className="text-muted small">{flight.planeCode}</span>
            </div>
          </Col>
          <Col xs="auto">
            <Badge bg="secondary" className="fs-6">
              {formatDate(flight.departureTime)}
            </Badge>
          </Col>
        </Row>
      </Card.Header>

      <Card.Body className="p-4">
        {/* Flight Route */}
        <Row className="align-items-center mb-4">
          {/* Departure */}
          <Col xs={4}>
            <div className="text-center">
              <h3 className="mb-1 fw-bold text-primary">
                {formatTime(flight.departureTime)}
              </h3>
              <div className="fw-bold">{flight.departureCityName}</div>
              <div className="text-muted small">{flight.departureAirportName}</div>
            </div>
          </Col>

          {/* Duration */}
          <Col xs={4}>
            <div className="text-center">
              <div className="position-relative">
                <hr className="border-2 border-primary" />
                <Badge 
                  bg="primary" 
                  className="position-absolute top-50 start-50 translate-middle px-2"
                >
                  <i className="bi bi-airplane me-1"></i>
                  {calculateDuration()}
                </Badge>
              </div>
              <small className="text-muted d-block mt-2">Bay thẳng</small>
            </div>
          </Col>

          {/* Arrival */}
          <Col xs={4}>
            <div className="text-center">
              <h3 className="mb-1 fw-bold text-success">
                {formatTime(flight.arrivalTime)}
              </h3>
              <div className="fw-bold">{flight.arrivalCityName}</div>
              <div className="text-muted small">{flight.arrivalAirportName}</div>
            </div>
          </Col>
        </Row>

        {/* Selected Class Info */}
        {searchContext?.selectedTicketClass && !searchContext?.searchedForAllClasses && (
          <Row className="mb-3">
            <Col>
              <div className="d-flex align-items-center">
                <Badge bg="info" className="me-2">
                  {searchContext.allTicketClasses?.find(tc => tc.ticketClassId === searchContext.selectedTicketClass)?.ticketClassName}
                </Badge>
                {searchContext.passengerCount > 1 && (
                  <span className="text-muted">
                    <i className="bi bi-people me-1"></i>
                    {searchContext.passengerCount} passengers
                  </span>
                )}
              </div>
            </Col>
          </Row>
        )}

        {/* Ticket Class Options */}
        {showAllClasses && availableClasses.length > 0 && (
          <Row className="mb-3">
            <Col>
              <h6 className="fw-bold mb-2">
                <i className="bi bi-star me-1"></i>
                Available Classes:
              </h6>
              <ListGroup variant="flush">
                {availableClasses.map(classInfo => (
                  <ListGroup.Item 
                    key={classInfo.ticketClassId}
                    action
                    active={selectedClassForBooking === classInfo.ticketClassId}
                    onClick={() => handleClassSelection(classInfo.ticketClassId!)}
                    className="d-flex justify-content-between align-items-center border rounded mb-2"
                  >
                    <div>
                      <div 
                        className="fw-bold"
                        style={{ color: classInfo.ticketClass?.color || '#333' }}
                      >
                        {classInfo.ticketClass?.ticketClassName}
                      </div>
                      <small className="text-muted">
                        <i className="bi bi-check-circle me-1"></i>
                        {classInfo.remainingTicketQuantity} seats available
                      </small>
                    </div>
                    <div className="text-end">
                      <div className="fw-bold fs-5 text-primary">
                        {classInfo.price.toLocaleString()} VND
                      </div>
                    </div>
                  </ListGroup.Item>
                ))}
              </ListGroup>
            </Col>
          </Row>
        )}

        {/* Available Seats Info */}
        <Row className="mb-3">
          <Col>
            {isAvailable ? (
              <div className="text-success">
                <i className="bi bi-check-circle me-1"></i>
                {flight.flightTicketClasses?.reduce(
                  (total, ftc) => total + (ftc.remainingTicketQuantity || 0), 
                  0
                )} total seats available
              </div>
            ) : (
              <div className="text-danger">
                <i className="bi bi-x-circle me-1"></i>
                Not enough seats available
              </div>
            )}
          </Col>
        </Row>
      </Card.Body>

      <Card.Footer className="bg-white border-0">
        <Row className="align-items-center">
          <Col>
            {/* Price Section */}
            {!showAllClasses && lowestPrice && (
              <div>
                <div className="fs-4 fw-bold text-primary">
                  {lowestPrice.toLocaleString()} VND
                </div>
                {searchContext?.passengerCount && searchContext.passengerCount > 1 && (
                  <small className="text-muted">per person</small>
                )}
              </div>
            )}
            {showAllClasses && (
              <div>
                <div className="fs-5 fw-bold text-primary">
                  From {lowestPrice?.toLocaleString()} VND
                </div>
                {searchContext?.passengerCount && searchContext.passengerCount > 1 && (
                  <small className="text-muted">per person</small>
                )}
              </div>
            )}
          </Col>

          <Col xs="auto">
            {/* Book Button */}
            {isAvailable ? (
              <Button 
                variant="primary"
                size="lg"
                onClick={handleBooking}
                disabled={showAllClasses && !selectedClassForBooking}
                className="px-4"
              >
                {showAllClasses && !selectedClassForBooking ? (
                  <>
                    <i className="bi bi-arrow-up me-1"></i>
                    Select Class
                  </>
                ) : (
                  <>
                    <i className="bi bi-ticket me-1"></i>
                    Book Flight
                  </>
                )}
              </Button>
            ) : (
              <Button variant="secondary" size="lg" disabled className="px-4">
                <i className="bi bi-x-circle me-1"></i>
                Unavailable
              </Button>
            )}
          </Col>
        </Row>
      </Card.Footer>
    </Card>
  );
};

export default FlightCard;
