import React from 'react';
import { Navbar, Nav, Container, Dropdown, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth, usePermissions } from '../../hooks/useAuth';

const Header: React.FC = () => {
  const { user, logout } = useAuth();
  const permissions = usePermissions();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <Navbar bg="white" expand="lg" sticky="top" className="shadow-sm border-bottom">
      <Container>
        <Navbar.Brand as={Link} to="/" className="fw-bold text-primary fs-4 text-decoration-none">
          <i className="bi bi-airplane me-2"></i>
          FlightMS
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/" className="text-decoration-none">Home</Nav.Link>
            
            {/* Public links (when not logged in) */}
            {!user && (
              <>
                <Nav.Link as={Link} to="/search" className="text-decoration-none">Search Flights</Nav.Link>
                <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">Manage Booking</Nav.Link>
              </>
            )}
            
            {/* Customer links */}
            {user && permissions.isCustomer() && (
              <>
                <Nav.Link as={Link} to="/search" className="text-decoration-none">Search Flights</Nav.Link>
                <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">Manage Booking</Nav.Link>
                <Nav.Link as={Link} to="/dashboard" className="text-decoration-none">Dashboard</Nav.Link>
              </>
            )}
            
            {/* Employee links */}
            {user && permissions.isEmployee() && (
              <>
                {/* Basic employee functions */}
                {permissions.canSearchFlights() && (
                  <Nav.Link as={Link} to="/search" className="text-decoration-none">Search Flights</Nav.Link>
                )}
                {permissions.canManageBookings() && (
                  <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">Manage Booking</Nav.Link>
                )}
                
                {/* Flight Management - For EMPLOYEE_TICKETING, EMPLOYEE_FLIGHT_OPERATIONS , EMPLOYEE_ADMIN and EMPLOYEE_FLIGHT_SCHEDULING */}
                {permissions.canViewFlightManagement() && (
                  <Nav.Link as={Link} to="/flights" className="text-decoration-none">✈️ Flight Management</Nav.Link>
                )}
                
                {/* Plane Management - For EMPLOYEE_ADMIN, EMPLOYEE_FLIGHT_OPERATIONS  */}
                {permissions.canViewPlaneManagement() && (
                  <Nav.Link as={Link} to="/planes" className="text-decoration-none">🛩️ Aircraft Fleet</Nav.Link>
                )}
                
                {/* Ticket Class Management - For EMPLOYEE_TICKETING, EMPLOYEE_FLIGHT_OPERATIONS  and EMPLOYEE_ADMIN */}
                {permissions.canViewTicketClassManagement() && (
                  <Nav.Link as={Link} to="/ticket-classes" className="text-decoration-none">🎟️ Ticket Classes</Nav.Link>
                )}
                
                {/* Department-specific panels */}
                {/* Admin Panel - Only for EMPLOYEE_ADMIN */}
                {permissions.canViewAdmin() && (
                  <Nav.Link as={Link} to="/admin" className="text-decoration-none">⚙️ Admin Panel</Nav.Link>
                )}
                
                {/* Customer Support - For EMPLOYEE_SUPPORT */}
                {permissions.canViewCustomerSupport() && (
                  <Nav.Link as={Link} to="/customer-support" className="text-decoration-none">🎧 Customer Support</Nav.Link>
                )}
                
                {/* Ticketing - For EMPLOYEE_TICKETING and EMPLOYEE_ADMIN */}
                {permissions.canViewTicketing() && (
                  <Nav.Link as={Link} to="/ticketing" className="text-decoration-none">🎫 Ticketing</Nav.Link>
                )}
                
                {/* Accounting - For EMPLOYEE_ACCOUNTING and EMPLOYEE_ADMIN */}
                {permissions.canViewAccounting() && (
                  <Nav.Link as={Link} to="/accounting" className="text-decoration-none">💰 Accounting</Nav.Link>
                )}
                
                {/* Regulations Management - For EMPLOYEE_FLIGHT_OPERATIONS and EMPLOYEE_ADMIN */}
                {permissions.canViewParameterSettings() && (
                  <Nav.Link as={Link} to="/regulations" className="text-decoration-none">📜 Regulations</Nav.Link>
                )}
                
                {/* Reports - For EMPLOYEE_ADMIN and EMPLOYEE_ACCOUNTING */}
              </>
            )}
          </Nav>
          
          <Nav className="ms-auto">
            {user ? (
              <Dropdown align="end">
                <Dropdown.Toggle variant="outline-primary" id="dropdown-basic">
                  <i className="bi bi-person-circle me-1"></i>
                  {user.accountName}
                  <small className="ms-1 text-muted">({user.role?.replace('EMPLOYEE_', '')})</small>
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  <Dropdown.ItemText>
                    <strong>{user.accountName}</strong><br />
                    <small className="text-muted">{user.role?.replace('EMPLOYEE_', '').replace('_', ' ')}</small>
                  </Dropdown.ItemText>
                  <Dropdown.Divider />
                  <Dropdown.Item onClick={handleLogout}>
                    <i className="bi bi-box-arrow-right me-2"></i>
                    Logout
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            ) : (
              <div className="d-flex gap-2">
                <Button as={Link as any} to="/login" className="me-2" variant='outline-primary'>
                  Login
                </Button>
                <Button as={Link as any} to="/register">
                  Sign Up
                </Button>
              </div>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;