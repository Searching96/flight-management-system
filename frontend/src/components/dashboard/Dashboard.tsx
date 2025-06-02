import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Nav, Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';
import { ticketService } from '../../services';
import { Ticket } from '../../models';
import TicketCard from '../tickets/TicketCard';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  // Only allow customers (accountType === 1) to access dashboard
  if (!user || user.accountType !== 1) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={6}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>
                <i className="bi bi-shield-x me-2"></i>
                Access Denied
              </Alert.Heading>
              <p className="mb-0">You do not have permission to access the customer dashboard.</p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<'upcoming' | 'past' | 'all'>('upcoming');

  useEffect(() => {
    if (user) {
      loadUserTickets();
    }
  }, [user]);

  const loadUserTickets = async () => {
    try {
      setLoading(true);
      const userTickets = await ticketService.getTicketsByCustomerId(user!.accountId!);
      setTickets(userTickets);
    } catch (err: any) {
      setError('Failed to load your bookings');
    } finally {
      setLoading(false);
    }
  };

  // Filtering by upcoming/past requires flight departureTime, which is not present in Ticket model.
  // For demonstration, show all tickets regardless of tab.
  const filterTickets = () => tickets;

  // Stats for demonstration: just show total tickets
  const getStats = () => ({
    total: tickets.length,
    upcoming: 0, // Not available
    past: 0 // Not available
  });

  const stats = getStats();
  const filteredTickets = filterTickets();

  if (loading) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={6}>
            <Card className="text-center">
              <Card.Body className="py-5">
                <Spinner animation="border" variant="primary" className="mb-3" />
                <h5>Loading your dashboard...</h5>
                <p className="text-muted">Please wait while we fetch your bookings.</p>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container className="py-4">
      {/* Dashboard Header */}
      <Row className="mb-4">
        <Col>
          <div className="text-center text-md-start">
            <h1 className="display-5 fw-bold text-primary">
              <i className="bi bi-speedometer2 me-2"></i>
              Welcome back, {user?.accountName}!
            </h1>
            <p className="lead text-muted">Manage your flights and bookings</p>
          </div>
        </Col>
      </Row>

      {/* Dashboard Stats */}
      <Row className="mb-4 g-3">
        <Col md={4}>
          <Card className="text-center border-0 shadow-sm bg-primary text-white">
            <Card.Body>
              <div className="display-4 fw-bold">{stats.total}</div>
              <div className="fs-6">Total Bookings</div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center border-0 shadow-sm bg-success text-white">
            <Card.Body>
              <div className="display-4 fw-bold">{stats.upcoming}</div>
              <div className="fs-6">Upcoming Flights</div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center border-0 shadow-sm bg-secondary text-white">
            <Card.Body>
              <div className="display-4 fw-bold">{stats.past}</div>
              <div className="fs-6">Past Flights</div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Dashboard Content */}
      <Row>
        <Col>
          <Card className="shadow-sm">
            <Card.Header className="bg-light">
              <Row className="align-items-center">
                <Col>
                  <h2 className="mb-0">
                    <i className="bi bi-ticket me-2"></i>
                    Your Bookings
                  </h2>
                </Col>
                <Col xs="auto">
                  <Nav variant="pills" activeKey={activeTab}>
                    <Nav.Item>
                      <Nav.Link 
                        eventKey="upcoming"
                        onClick={() => setActiveTab('upcoming')}
                      >
                        <i className="bi bi-calendar-plus me-1"></i>
                        Upcoming
                      </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                      <Nav.Link 
                        eventKey="past"
                        onClick={() => setActiveTab('past')}
                      >
                        <i className="bi bi-calendar-check me-1"></i>
                        Past
                      </Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                      <Nav.Link 
                        eventKey="all"
                        onClick={() => setActiveTab('all')}
                      >
                        <i className="bi bi-list me-1"></i>
                        All
                      </Nav.Link>
                    </Nav.Item>
                  </Nav>
                </Col>
              </Row>
            </Card.Header>

            <Card.Body className="p-0">
              {error && (
                <Alert variant="danger" className="m-3">
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  {error}
                </Alert>
              )}

              {filteredTickets.length === 0 ? (
                <div className="text-center py-5">
                  <i className="bi bi-ticket text-muted mb-3" style={{ fontSize: '4rem' }}></i>
                  <h3>No bookings found</h3>
                  <p className="text-muted mb-4">
                    {activeTab === 'upcoming' 
                      ? "You don't have any upcoming flights. Start planning your next trip!"
                      : activeTab === 'past'
                      ? "No past flights to display."
                      : "You haven't made any bookings yet. Search for flights to get started!"
                    }
                  </p>
                  <Button 
                    variant="primary" 
                    size="lg"
                    href="/search"
                    className="text-decoration-none"
                  >
                    <i className="bi bi-search me-2"></i>
                    Search Flights
                  </Button>
                </div>
              ) : (
                <div className="p-3">
                  {filteredTickets.map(ticket => (
                    <TicketCard 
                      key={ticket.ticketId} 
                      ticket={ticket}
                      onCancel={loadUserTickets}
                    />
                  ))}
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Dashboard;
