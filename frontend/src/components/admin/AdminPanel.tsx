import React, { useState } from 'react';
import { Container, Row, Col, Card, Nav, Alert, Button } from 'react-bootstrap';
import FlightManagement from './FlightManagement';
import AirportManagement from './AirportManagement';
import ParameterSettings from './ParameterSettings';
import PlaneManagement from './PlaneManagement';
import TicketClassManagement from './TicketClassManagement';
import FlightTicketClassManagement from './FlightTicketClassManagement';
import { usePermissions } from '../../hooks/useAuth';

type AdminTab = 'overview' | 'flights' | 'airports' | 'planes' | 'ticket-classes' | 'flight-ticket-classes' | 'parameters';

export const AdminPanel: React.FC = () => {
  const { canViewAdmin } = usePermissions();
  const [activeTab, setActiveTab] = useState<AdminTab>('overview');
  // Redirect if user doesn't have admin permissions (accountType should be 2 for employees)
  if (!canViewAdmin) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>Access Denied</Alert.Heading>
              <p>You do not have permission to access the admin panel.</p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  const renderContent = () => {
    switch (activeTab) {
      case 'overview':
        return <AdminOverview />;
      case 'flights':
        return <FlightManagement />;
      case 'airports':
        return <AirportManagement />;
      case 'planes':
        return <PlaneManagement />;
      case 'ticket-classes':
        return <TicketClassManagement />;
      case 'flight-ticket-classes':
        return <FlightTicketClassManagement />;
      case 'parameters':
        return <ParameterSettings />;
      default:
        return <AdminOverview />;
    }
  };
  return (
    <Container fluid className="py-4">
      <Row>
        <Col>
          <div className="text-center mb-4">
            <h1 className="mb-2">Admin Panel</h1>
            <p className="text-muted">Manage flights, airports, and system settings</p>
          </div>

          <Nav variant="pills" className="justify-content-center mb-4 flex-wrap">
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'overview'}
                onClick={() => setActiveTab('overview')}
              >
                📊 Overview
              </Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'flights'}
                onClick={() => setActiveTab('flights')}
              >
                ✈️ Flights
              </Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'airports'}
                onClick={() => setActiveTab('airports')}
              >
                🏢 Airports
              </Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'planes'}
                onClick={() => setActiveTab('planes')}
              >
                🛩️ Aircraft Fleet
              </Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'ticket-classes'}
                onClick={() => setActiveTab('ticket-classes')}
              >
                🎟️ Ticket Classes
              </Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'flight-ticket-classes'}
                onClick={() => setActiveTab('flight-ticket-classes')}
              >
                ✈️🎟️ Flight Class Assignment
              </Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'parameters'}
                onClick={() => setActiveTab('parameters')}
              >
                ⚙️ Settings
              </Nav.Link>
            </Nav.Item>
          </Nav>

          <div>
            {renderContent()}
          </div>
        </Col>
      </Row>
    </Container>
  );
};

// Admin Overview Component
const AdminOverview: React.FC = () => {
  return (
    <Container fluid>
      {/* Statistics Cards */}
      <Row className="mb-4">
        <Col lg={3} md={6} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <div className="mb-2" style={{ fontSize: '2rem' }}>✈️</div>
              <Card.Title className="h5 text-muted">Total Flights</Card.Title>
              <Card.Text className="h3 mb-0 text-primary">156</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={3} md={6} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <div className="mb-2" style={{ fontSize: '2rem' }}>🏢</div>
              <Card.Title className="h5 text-muted">Active Airports</Card.Title>
              <Card.Text className="h3 mb-0 text-info">23</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={3} md={6} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <div className="mb-2" style={{ fontSize: '2rem' }}>👥</div>
              <Card.Title className="h5 text-muted">Total Passengers</Card.Title>
              <Card.Text className="h3 mb-0 text-success">8,432</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={3} md={6} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <div className="mb-2" style={{ fontSize: '2rem' }}>💰</div>
              <Card.Title className="h5 text-muted">Revenue</Card.Title>
              <Card.Text className="h3 mb-0 text-warning">$2.1M</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        {/* Quick Actions */}
        <Col lg={6} className="mb-4">
          <Card className="h-100">
            <Card.Header>
              <Card.Title className="h4 mb-0">Quick Actions</Card.Title>
            </Card.Header>
            <Card.Body>
              <Row>
                <Col md={6} className="mb-3">
                  <Button variant="outline-primary" className="w-100 text-start" size="lg">
                    <span className="me-2">➕</span>
                    Add New Flight
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button variant="outline-info" className="w-100 text-start" size="lg">
                    <span className="me-2">🏢</span>
                    Add New Airport
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button variant="outline-success" className="w-100 text-start" size="lg">
                    <span className="me-2">📊</span>
                    View Reports
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button variant="outline-secondary" className="w-100 text-start" size="lg">
                    <span className="me-2">⚙️</span>
                    System Settings
                  </Button>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>

        {/* Recent Activity */}
        <Col lg={6} className="mb-4">
          <Card className="h-100">
            <Card.Header>
              <Card.Title className="h4 mb-0">Recent Activity</Card.Title>
            </Card.Header>
            <Card.Body>
              <div className="d-flex align-items-center mb-3 pb-3 border-bottom">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>✈️</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">New flight FL001 added</div>
                  <small className="text-muted">2 hours ago</small>
                </div>
              </div>
              <div className="d-flex align-items-center mb-3 pb-3 border-bottom">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>🏢</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">Airport LAX updated</div>
                  <small className="text-muted">4 hours ago</small>
                </div>
              </div>
              <div className="d-flex align-items-center">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>⚙️</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">System parameters updated</div>
                  <small className="text-muted">1 day ago</small>
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default AdminPanel;
