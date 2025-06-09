import React, { useState } from 'react';
import { Container, Row, Col, Card, Nav, Alert, Button } from 'react-bootstrap';
import FlightManagement from './FlightManagement';
import AirportManagement from './AirportManagement';
import ParameterSettings from './ParameterSettings';
import PlaneManagement from './PlaneManagement';
import TicketClassManagement from './TicketClassManagement';
import TicketManagement from './TicketManagement';
import { usePermissions } from '../../hooks/useAuth';
import EmployeeManagement from './EmployeeManagement';

type AdminTab = 'overview' | 'flights' | 'employees' | 'airports' | 'planes' | 'ticket-classes' | 'tickets' | 'parameters';

export const AdminPanel: React.FC = () => {
  const { canViewAdmin } = usePermissions();
  const [activeTab, setActiveTab] = useState<AdminTab>('overview');
  
  // Add state for managing quick action modals
  const [quickActionModals, setQuickActionModals] = useState({
    showFlightModal: false,
    showAirportModal: false,
    showTicketClassModal: false,
    showPlaneModal: false,
    showTicketModal: false
  });

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
      case 'flights':
        return <FlightManagement showAddModal={quickActionModals.showFlightModal} onCloseAddModal={() => setQuickActionModals(prev => ({...prev, showFlightModal: false}))} />;
      case 'airports':
        return <AirportManagement showAddModal={quickActionModals.showAirportModal} onCloseAddModal={() => setQuickActionModals(prev => ({...prev, showAirportModal: false}))} />;
      case 'planes':
        return <PlaneManagement showAddModal={quickActionModals.showPlaneModal} onCloseAddModal={() => setQuickActionModals(prev => ({...prev, showPlaneModal: false}))} />;
      case 'ticket-classes':
        return <TicketClassManagement showAddModal={quickActionModals.showTicketClassModal} onCloseAddModal={() => setQuickActionModals(prev => ({...prev, showTicketClassModal: false}))} />;
      case 'tickets':
        return <TicketManagement showAddModal={quickActionModals.showTicketModal} onCloseAddModal={() => setQuickActionModals(prev => ({...prev, showTicketModal: false}))} />;
      case 'parameters':
        return <ParameterSettings />;
      case 'employees':
        return <EmployeeManagement />;
      default:
      case 'overview':
        return <AdminOverview onNavigate={handleQuickAction} />;
    }
  };

  // Update the navigation handler to support quick actions
  const handleQuickAction = (action: AdminTab | 'add-flight' | 'add-airport' | 'add-plane' | 'add-ticket-class' | 'add-ticket') => {
    switch (action) {
      case 'add-flight':
        setActiveTab('flights');
        setQuickActionModals(prev => ({...prev, showFlightModal: true}));
        break;
      case 'add-airport':
        setActiveTab('airports');
        setQuickActionModals(prev => ({...prev, showAirportModal: true}));
        break;
      case 'add-plane':
        setActiveTab('planes');
        setQuickActionModals(prev => ({...prev, showPlaneModal: true}));
        break;
      case 'add-ticket-class':
        setActiveTab('ticket-classes');
        setQuickActionModals(prev => ({...prev, showTicketClassModal: true}));
        break;
      case 'add-ticket':
        setActiveTab('tickets');
        setQuickActionModals(prev => ({...prev, showTicketModal: true}));
        break;
      default:
        setActiveTab(action);
        break;
    }
  };

  return (
    <Container fluid className="py-4" data-admin-panel ref={(el) => {
      if (el) {
        (el as any).setActiveTab = setActiveTab;
      }
    }}>
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
                active={activeTab === 'tickets'}
                onClick={() => setActiveTab('tickets')}
              >
                🎫 Tickets
              </Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'employees'}
                onClick={() => setActiveTab('employees')}
              >
                👥 Employees
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
const AdminOverview: React.FC<{ onNavigate: (action: AdminTab | 'add-flight' | 'add-airport' | 'add-plane' | 'add-ticket-class' | 'add-ticket') => void }> = ({ onNavigate }) => {
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
                  <Button
                    variant="outline-primary"
                    className="w-100 text-start"
                    size="lg"
                    onClick={() => onNavigate('add-flight')}
                  >
                    <span className="me-2">➕</span>
                    Add New Flight
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button
                    variant="outline-info"
                    className="w-100 text-start"
                    size="lg"
                    onClick={() => onNavigate('add-airport')}
                  >
                    <span className="me-2">🏢</span>
                    Add New Airport
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button
                    variant="outline-warning"
                    className="w-100 text-start"
                    size="lg"
                    onClick={() => onNavigate('add-plane')}
                  >
                    <span className="me-2">🛩️</span>
                    Add New Aircraft
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button
                    variant="outline-dark"
                    className="w-100 text-start"
                    size="lg"
                    onClick={() => onNavigate('add-ticket-class')}
                  >
                    <span className="me-2">🎟️</span>
                    Add Ticket Class
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button
                    variant="outline-purple"
                    className="w-100 text-start"
                    size="lg"
                    onClick={() => onNavigate('add-ticket')}
                  >
                    <span className="me-2">🎫</span>
                    Add New Ticket
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button
                    variant="outline-success"
                    className="w-100 text-start"
                    size="lg"
                    onClick={() => alert('Reports feature coming soon!')}
                  >
                    <span className="me-2">📊</span>
                    View Reports
                  </Button>
                </Col>
                <Col md={6} className="mb-3">
                  <Button
                    variant="outline-secondary"
                    className="w-100 text-start"
                    size="lg"
                    onClick={() => onNavigate('parameters')}
                  >
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
