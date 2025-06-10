import React from 'react';
import { Container, Row, Col, Nav } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Footer: React.FC = () => {
  return (
    <footer className="bg-dark text-light py-5 mt-auto">
      <Container>
        <Row>
          <Col lg={4} md={6} className="mb-4">
            <h5 className="mb-3">Flight Management System</h5>
            <p className="text-muted mb-3">Your trusted partner for flight booking and management.</p>
            <div className="d-flex gap-3">
              <a href="#" className="text-light fs-4" aria-label="Facebook">
                <i className="bi bi-facebook"></i>
              </a>
              <a href="#" className="text-light fs-4" aria-label="Twitter">
                <i className="bi bi-twitter"></i>
              </a>
              <a href="#" className="text-light fs-4" aria-label="Instagram">
                <i className="bi bi-instagram"></i>
              </a>
              <a href="#" className="text-light fs-4" aria-label="LinkedIn">
                <i className="bi bi-linkedin"></i>
              </a>
            </div>
          </Col>          <Col lg={2} md={6} className="mb-4">
            <h6 className="mb-3">Quick Links</h6>
            <Nav className="flex-column">
              <Nav.Link as={Link} to="/search" className="text-muted text-decoration-none p-1">
                Search Flights
              </Nav.Link>
              <Nav.Link as={Link} to="/dashboard" className="text-muted text-decoration-none p-1">
                My Bookings
              </Nav.Link>
              <Nav.Link as={Link} to="/support" className="text-muted text-decoration-none p-1">
                Customer Support
              </Nav.Link>
              <Nav.Link as={Link} to="/about" className="text-muted text-decoration-none p-1">
                About Us
              </Nav.Link>
            </Nav>
          </Col>          <Col lg={3} md={6} className="mb-4">
            <h6 className="mb-3">Support</h6>
            <Nav className="flex-column">
              <Nav.Link href="/help" className="text-muted text-decoration-none p-1">
                Help Center
              </Nav.Link>
              <Nav.Link href="/contact" className="text-muted text-decoration-none p-1">
                Contact Us
              </Nav.Link>
              <Nav.Link href="/faq" className="text-muted text-decoration-none p-1">
                FAQ
              </Nav.Link>
              <Nav.Link href="/terms" className="text-muted text-decoration-none p-1">
                Terms of Service
              </Nav.Link>
            </Nav>
          </Col>          <Col lg={3} md={6} className="mb-4">
            <h6 className="mb-3">Contact Info</h6>
            <ul className="list-unstyled">
              <li className="mb-2 text-muted">
                <i className="bi bi-telephone me-2"></i>+84 (028) 123-4567
              </li>
              <li className="mb-2 text-muted">
                <i className="bi bi-envelope me-2"></i>hotro@flightms.com
              </li>
              <li className="mb-2 text-muted">
                <i className="bi bi-geo-alt me-2"></i>123 Đường Hàng Không, TP.HCM
              </li>
              <li className="mb-2 text-muted">
                <i className="bi bi-clock me-2"></i>Hỗ trợ khách hàng 24/7
              </li>
            </ul>
          </Col>
        </Row>

        <hr className="my-4" />
          <Row>
          <Col className="text-center">
            <p className="text-muted mb-0">&copy; {new Date().getFullYear()} Hệ thống quản lý chuyến bay. Bảo lưu mọi quyền.</p>
          </Col>
        </Row>
      </Container>
    </footer>
  );
};

export default Footer;
