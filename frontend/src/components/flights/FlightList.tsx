import React, { useState } from "react";
import { Container, Row, Col, Card, Form, Badge } from "react-bootstrap";
import { Flight } from "../../models";
import FlightCard from "./FlightCard";

interface FlightListProps {
  flights: Flight[];
  passengerCount: number;
  onBookFlight: (flightId: number, ticketClassId: number) => void;
}

const FlightList: React.FC<FlightListProps> = ({
  flights,
  passengerCount,
  onBookFlight,
}) => {
  const [sortBy, setSortBy] = useState<"price" | "departure" | "duration">(
    "departure"
  );
  const [filterBy, setFilterBy] = useState<
    "all" | "morning" | "afternoon" | "evening"
  >("all");

  const filterFlights = (flights: Flight[]) => {
    if (filterBy === "all") return flights;

    return flights.filter((flight) => {
      const hour = new Date(flight.departureTime).getHours();
      switch (filterBy) {
        case "morning":
          return hour >= 6 && hour < 12;
        case "afternoon":
          return hour >= 12 && hour < 18;
        case "evening":
          return hour >= 18 || hour < 6;
        default:
          return true;
      }
    });
  };

  const sortFlights = (flights: Flight[]) => {
    return [...flights].sort((a, b) => {
      switch (sortBy) {
        case "departure":
          return (
            new Date(a.departureTime).getTime() -
            new Date(b.departureTime).getTime()
          );
        case "duration": {
          const durationA =
            new Date(a.arrivalTime).getTime() -
            new Date(a.departureTime).getTime();
          const durationB =
            new Date(b.arrivalTime).getTime() -
            new Date(b.departureTime).getTime();
          return durationA - durationB;
        }
        case "price":
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
                    Tìm thấy {flights.length} chuyến bay
                  </h2>
                  <p className="mb-0">
                    <i className="bi bi-people me-1"></i>
                    Cho {passengerCount}{" "}
                    {passengerCount === 1 ? "hành khách" : "hành khách"}
                  </p>
                </Col>
                <Col xs="auto">
                  <Badge bg="light" text="dark" className="fs-6">
                    {processedFlights.length} hiển thị
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
                      Lọc theo thời gian:
                    </Form.Label>
                    <Form.Select
                      value={filterBy}
                      onChange={(e) => setFilterBy(e.target.value as any)}
                    >
                      <option value="all">Tất cả thời gian</option>
                      <option value="morning">Sáng (6h - 12h)</option>
                      <option value="afternoon">Chiều (12h - 18h)</option>
                      <option value="evening">Tối (18h - 6h)</option>
                    </Form.Select>
                  </Form.Group>
                </Col>

                <Col md={6}>
                  <Form.Group>
                    <Form.Label className="fw-bold">
                      <i className="bi bi-sort-down me-1"></i>
                      Sắp xếp theo:
                    </Form.Label>
                    <Form.Select
                      value={sortBy}
                      onChange={(e) => setSortBy(e.target.value as any)}
                    >
                      <option value="departure">Thời gian khởi hành</option>
                      <option value="duration">Thời gian bay</option>
                      <option value="price">Giá</option>
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
                <i
                  className="bi bi-airplane text-muted mb-3"
                  style={{ fontSize: "3rem" }}
                ></i>
                <h5>Không có chuyến bay nào phù hợp với bộ lọc</h5>
                <p className="text-muted">
                  Hãy thử điều chỉnh tiêu chí tìm kiếm hoặc bộ lọc.
                </p>
              </Card.Body>
            </Card>
          ) : (
            processedFlights.map((flight) => (
              <FlightCard
                key={flight.flightId}
                flight={flight}
                onBookFlight={onBookFlight}
                searchContext={{
                  passengerCount: passengerCount,
                  allTicketClasses: [],
                  searchedForAllClasses: true,
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
