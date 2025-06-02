import React, { useState } from 'react';
import { Container, Row, Col, Card, Form, Badge } from 'react-bootstrap';
import { Flight } from '../../models';
import FlightCard from './FlightCard';

interface FlightListProps {
  flights: Flight[];
  passengerCount: number;
  onBookFlight: (flightId: number, ticketClassId: number) => void;
}

const FlightList: React.FC<FlightListProps> = ({ flights, passengerCount, onBookFlight }) => {
  const [sortBy, setSortBy] = useState<'price' | 'departure' | 'duration'>('departure');
  const [filterBy, setFilterBy] = useState<'all' | 'morning' | 'afternoon' | 'evening'>('all');

  const filterFlights = (flights: Flight[]) => {
    if (filterBy === 'all') return flights;
    
    return flights.filter(flight => {
      const hour = new Date(flight.departureTime).getHours();
      switch (filterBy) {
        case 'morning': return hour >= 6 && hour < 12;
        case 'afternoon': return hour >= 12 && hour < 18;
        case 'evening': return hour >= 18 || hour < 6;
        default: return true;
      }
    });
  };

  const sortFlights = (flights: Flight[]) => {
    return [...flights].sort((a, b) => {
      switch (sortBy) {
        case 'departure':
          return new Date(a.departureTime).getTime() - new Date(b.departureTime).getTime();
        case 'duration':
          const durationA = new Date(a.arrivalTime).getTime() - new Date(a.departureTime).getTime();
          const durationB = new Date(b.arrivalTime).getTime() - new Date(b.departureTime).getTime();
          return durationA - durationB;
        case 'price':
          // For now, sort by flight code as price is not available
          return a.flightCode.localeCompare(b.flightCode);
        default:
          return 0;
      }
    });
  };

  const processedFlights = sortFlights(filterFlights(flights));
  return (
    <Container>
      <Row>
        <Col>
          <Card className="shadow-sm mb-4">
            <Card.Header className="bg-info text-white">
              <Row className="align-items-center">
                <Col>
                  <h2 className="mb-0">
                    <i className="bi bi-airplane me-2"></i>
                    {flights.length} flights found
                  </h2>
                  <p className="mb-0">
                    <i className="bi bi-people me-1"></i>
                    For {passengerCount} {passengerCount === 1 ? 'passenger' : 'passengers'}
                  </p>
                </Col>
                <Col xs="auto">
                  <Badge bg="light" text="dark" className="fs-6">
                    {processedFlights.length} displayed
                  </Badge>
                </Col>
              </Row>
            </Card.Header>
            
            <Card.Body>
              <Row className="g-3">
                <Col md={6}>
                  <Form.Group>
                    <Form.Label className="fw-bold">
                      <i className="bi bi-funnel me-1"></i>
                      Filter by time:
                    </Form.Label>
                    <Form.Select 
                      value={filterBy} 
                      onChange={(e) => setFilterBy(e.target.value as any)}
                    >
                      <option value="all">All times</option>
                      <option value="morning">Morning (6AM - 12PM)</option>
                      <option value="afternoon">Afternoon (12PM - 6PM)</option>
                      <option value="evening">Evening (6PM - 6AM)</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
                
                <Col md={6}>
                  <Form.Group>
                    <Form.Label className="fw-bold">
                      <i className="bi bi-sort-down me-1"></i>
                      Sort by:
                    </Form.Label>
                    <Form.Select 
                      value={sortBy} 
                      onChange={(e) => setSortBy(e.target.value as any)}
                    >
                      <option value="departure">Departure time</option>
                      <option value="duration">Flight duration</option>
                      <option value="price">Price</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col>
          {processedFlights.length === 0 ? (
            <Card className="text-center">
              <Card.Body className="py-5">
                <i className="bi bi-airplane text-muted mb-3" style={{ fontSize: '3rem' }}></i>
                <h5>No flights match your filters</h5>
                <p className="text-muted">Try adjusting your search criteria or filters.</p>
              </Card.Body>
            </Card>
          ) : (
            processedFlights.map(flight => (
              <FlightCard
                key={flight.flightId}
                flight={flight}
                onBookFlight={onBookFlight}
                searchContext={{
                  passengerCount: passengerCount,
                  allTicketClasses: [],
                  searchedForAllClasses: true
                }}
              />
            ))
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default FlightList;
