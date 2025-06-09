import React from 'react';
import { Navbar, Nav, Container, Dropdown, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const Header: React.FC = () => {
  const { user, logout } = useAuth();
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
            <Nav.Link as={Link} to="/test" className="text-decoration-none">Test</Nav.Link>
            {!user && (
              <>
                <Nav.Link as={Link} to="/search" className="text-decoration-none">Search Flights</Nav.Link>
                <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">Manage Booking</Nav.Link>
              </>
            )}
            {user && (
              <>
                {user.accountTypeName === "Customer" && (
                  <>
                    <Nav.Link as={Link} to="/search" className="text-decoration-none">Search Flights</Nav.Link>
                    <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">Manage Booking</Nav.Link>
                  </>
                )}
                <Nav.Link as={Link} to="/dashboard" className="text-decoration-none">Dashboard</Nav.Link>
                {user.accountTypeName === 'Employee' && (
                  <>
                  <Nav.Link as={Link} to="/admin/customer-support" className="text-decoration-none">Customer Support</Nav.Link>
                  <Nav.Link as={Link} to="/admin" className="text-decoration-none">Admin</Nav.Link>
                  </>
                )}
              </>
            )}
          </Nav>
          <Nav className="ms-auto">
            {user ? (
              <Dropdown align="end">
                <Dropdown.Toggle variant="outline-primary" id="dropdown-basic">
                  <i className="bi bi-person-circle me-1"></i>
                  {user.accountName}
                </Dropdown.Toggle>
                <Dropdown.Menu>
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
