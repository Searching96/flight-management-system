import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Badge, ListGroup, Table } from 'react-bootstrap';
import { Flight, TicketClass } from '../../models';
import { flightDetailService } from '../../services/flightDetailService';

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
  const [flightDetails, setFlightDetails] = useState<any[]>([]);

  useEffect(() => {
    const fetchFlightDetail = async () => {
      if (flight.flightId) {
        try {
          const flightDetail = await flightDetailService.getFlightDetailsById(flight.flightId);
          console.log('Flight Detail:', flightDetail);
          setFlightDetails(flightDetail.data || []);
        } catch (error) {
          console.error('Error fetching flight detail:', error);
          setFlightDetails([]);
        }
      }
    };

    fetchFlightDetail();
  }, [flight.flightId]);

  const formatTime = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleTimeString('vi-VN', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  };

  const formatDate = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleDateString('vi-VN', {
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
              {formatDate(flight.departureTime) == formatDate(flight.arrivalTime) ?
              formatDate(flight.departureTime) :
              `${formatDate(flight.departureTime)} - ${formatDate(flight.arrivalTime)}`}
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
              <small className="text-muted d-block mt-2">
                {flightDetails.length > 0 ? "Có điểm dừng" : "Bay thẳng"}
              </small>
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
                    {searchContext.passengerCount} hành khách
                  </span>
                )}
              </div>
            </Col>
          </Row>
        )}

        {/* Flight Details Table */}
        {flightDetails.length > 0 && (
          <Row className="mb-3">
            <Col>
              <h6 className="fw-bold mb-2">
                <i className="bi bi-route me-1"></i>
                Chi tiết chuyến bay:
              </h6>
              <Table striped bordered hover size="sm" className="mb-0">
                <thead className="table-secondary">
                  <tr>
                    <th className="text-center py-2 fs-5 fw-bold" style={{ width: '40%' }}>Sân bay trung gian</th>
                    <th className="text-center py-2 fs-5 fw-bold" style={{ width: '30%' }}>Thời gian đến</th>
                    <th className="text-center py-2 fs-5 fw-bold" style={{ width: '30%' }}>Thời gian dừng</th>
                  </tr>
                </thead>
                <tbody className="table-light">
                  {flightDetails.map((detail, index) => (
                    <tr key={index}>
                      <td className="text-center align-middle py-3">
                        <strong>{detail.mediumAirportName || 'N/A'}</strong>
                      </td>
                      <td className="text-center align-middle py-3">
                        {detail.arrivalTime 
                          ? <span className="badge bg-info">{formatTime(detail.arrivalTime)  + ' - ' + formatDate(detail.arrivalTime)}</span>
                          : <span className="text-muted">N/A</span>
                        }
                      </td>
                      <td className="text-center align-middle py-3">
                        {detail.layoverDuration 
                          ? <span className="badge bg-secondary">{detail.layoverDuration} phút</span>
                          : <span className="text-muted">N/A</span>
                        }
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Col>
          </Row>
        )}

        {/* Ticket Class Options */}
        {showAllClasses && availableClasses.length > 0 && (
          <Row className="mb-3">
            <Col>
              <h6 className="fw-bold mb-2">
                <i className="bi bi-star me-1"></i>
                Hạng vé có sẵn:
              </h6>
              <ListGroup variant="flush">
                {availableClasses.map(classInfo => (
                  <ListGroup.Item
                    key={classInfo.ticketClassId}
                    action
                    onClick={() => handleClassSelection(classInfo.ticketClassId!)}
                    className={`d-flex justify-content-between align-items-center border rounded mb-2 ${
                      selectedClassForBooking === classInfo.ticketClassId 
                        ? 'border-primary border-3 bg-light' 
                        : 'border-secondary'
                    }`}
                    style={{
                      backgroundColor: selectedClassForBooking === classInfo.ticketClassId ? '#f8f9fa' : 'white',
                      flexWrap: 'nowrap',
                      gap: '1rem'
                    }}
                  >
                    <div style={{ flex: '1', minWidth: 0 }}>
                      <div
                        className="fw-bold"
                        style={{ color: classInfo.ticketClass?.color || '#333' }}
                      >
                        {classInfo.ticketClass?.ticketClassName}
                      </div>
                      <small className="text-muted">
                        <i className="bi bi-check-circle me-1"></i>
                        {classInfo.remainingTicketQuantity} ghế có sẵn
                      </small>
                    </div>
                    <div className="text-end" style={{ flexShrink: 0, minWidth: 'fit-content' }}>
                      <div className="fw-bold text-primary" style={{ 
                        whiteSpace: 'nowrap',
                        fontSize: 'clamp(1rem, 4vw, 1.25rem)'
                      }}>
                        {classInfo.price.toLocaleString('vi-VN')} VND
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
                Tổng cộng {flight.flightTicketClasses?.reduce(
                  (total, ftc) => total + (ftc.remainingTicketQuantity || 0),
                  0
                )} ghế có sẵn
              </div>
            ) : (
              <div className="text-danger">
                <i className="bi bi-x-circle me-1"></i>
                Không đủ ghế trống
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
                  {lowestPrice.toLocaleString('vi-VN')} VND
                </div>
                {searchContext?.passengerCount && searchContext.passengerCount > 1 && (
                  <small className="text-muted">mỗi người</small>
                )}
              </div>
            )}
            {showAllClasses && (
              <div>
                <div className="fs-5 fw-bold text-primary">
                  Từ {lowestPrice?.toLocaleString('vi-VN')} VND
                </div>
                {searchContext?.passengerCount && searchContext.passengerCount > 1 && (
                  <small className="text-muted">mỗi người</small>
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
                    Chọn hạng vé
                  </>
                ) : (
                  <>
                    <i className="bi bi-ticket me-1"></i>
                    Đặt vé
                  </>
                )}
              </Button>
            ) : (
              <Button variant="secondary" size="lg" disabled className="px-4">
                <i className="bi bi-x-circle me-1"></i>
                Hết chỗ
              </Button>
            )}
          </Col>
        </Row>
      </Card.Footer>
    </Card>
  );
};

export default FlightCard;
