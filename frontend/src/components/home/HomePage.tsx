import React from 'react';
import { Link } from 'react-router-dom';
import { Container, Row, Col, Card, Badge } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';

const HomePage: React.FC = () => {
  const { user } = useAuth();
  const features = [
    {
      icon: 'bi-search',
      title: 'Smart Search',
      description: 'Find flights with our intelligent search that compares prices and schedules across multiple airlines.'
    },
    {
      icon: 'bi-shield-check',
      title: 'Secure Booking',
      description: 'Book with confidence using our secure payment system and instant confirmation.'
    },
    {
      icon: 'bi-phone',
      title: 'Mobile Ready',
      description: 'Access your bookings anywhere with our responsive design that works on all devices.'
    },
    {
      icon: 'bi-currency-dollar',
      title: 'Best Prices',
      description: 'We guarantee competitive prices and offer price matching for the best deals.'
    }
  ];

  const popularDestinations = [
    { city: 'New York', country: 'USA', icon: 'bi-building' },
    { city: 'London', country: 'UK', icon: 'bi-clock-history' },
    { city: 'Tokyo', country: 'Japan', icon: 'bi-geo-alt' },
    { city: 'Paris', country: 'France', icon: 'bi-heart' },
    { city: 'Dubai', country: 'UAE', icon: 'bi-sun' },
    { city: 'Sydney', country: 'Australia', icon: 'bi-water' }
  ];
  return (
    <div>
      {/* Hero Section */}
      <div className="bg-primary text-white py-5">
        <Container>
          <Row className="align-items-center min-vh-50">
            <Col lg={6}>
              <h1 className="display-4 fw-bold mb-4">Find Your Perfect Flight</h1>
              <p className="lead mb-4">
                Discover amazing destinations with unbeatable prices and seamless booking experience
              </p>              <div className="d-flex gap-3 flex-wrap">
                <Link to="/search" className="btn btn-light btn-lg text-decoration-none">
                  <i className="bi bi-search me-2"></i>
                  Search Flights
                </Link>
                {!user && (
                  <Link to="/register" className="btn btn-outline-light btn-lg text-decoration-none">
                    <i className="bi bi-person-plus me-2"></i>
                    Sign Up Free
                  </Link>
                )}
              </div>
            </Col>
            <Col lg={6} className="text-center">
              <div className="position-relative">
                <i 
                  className="bi bi-airplane text-white opacity-75" 
                  style={{ fontSize: '8rem', animation: 'float 3s ease-in-out infinite' }}
                ></i>
              </div>
            </Col>
          </Row>
        </Container>
      </div>

      {/* Features Section */}
      <Container className="py-5">
        <Row>
          <Col>
            <h2 className="text-center mb-5 display-5 fw-bold">Why Choose FlightMS?</h2>
          </Col>
        </Row>
        <Row className="g-4">
          {features.map((feature, index) => (
            <Col key={index} md={6} lg={3}>
              <Card className="h-100 text-center border-0 shadow-sm">
                <Card.Body className="p-4">
                  <div className="mb-3">
                    <i 
                      className={`${feature.icon} text-primary`} 
                      style={{ fontSize: '3rem' }}
                    ></i>
                  </div>
                  <Card.Title className="h4 mb-3">{feature.title}</Card.Title>
                  <Card.Text className="text-muted">
                    {feature.description}
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>

      {/* Popular Destinations */}
      <div className="bg-light py-5">
        <Container>
          <Row>
            <Col>
              <h2 className="text-center mb-5 display-5 fw-bold">Popular Destinations</h2>
            </Col>
          </Row>
          <Row className="g-4">
            {popularDestinations.map((destination, index) => (
              <Col key={index} md={6} lg={4}>
                <Card className="h-100 border-0 shadow-sm overflow-hidden">
                  <Card.Body className="p-4 text-center">
                    <div className="mb-3">
                      <i 
                        className={`${destination.icon} text-primary`} 
                        style={{ fontSize: '3rem' }}
                      ></i>
                    </div>
                    <Card.Title className="h4 mb-2">{destination.city}</Card.Title>
                    <Card.Text className="text-muted">
                      <Badge bg="secondary" className="fs-6">
                        {destination.country}
                      </Badge>
                    </Card.Text>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        </Container>
      </div>

      {/* CTA Section */}
      <div className="bg-dark text-white py-5">
        <Container>
          <Row>
            <Col lg={8} className="mx-auto text-center">
              <h2 className="display-5 fw-bold mb-3">Ready to Start Your Journey?</h2>
              <p className="lead mb-4">
                Join thousands of satisfied travelers who trust FlightMS for their travel needs
              </p>              <div className="d-flex gap-3 justify-content-center flex-wrap">
                <Link to="/search" className="btn btn-primary btn-lg text-decoration-none">
                  <i className="bi bi-ticket me-2"></i>
                  Book Your Flight
                </Link>
                {user ? (
                  <Link to="/dashboard" className="btn btn-outline-light btn-lg text-decoration-none">
                    <i className="bi bi-calendar-check me-2"></i>
                    View My Bookings
                  </Link>
                ) : (
                  <Link to="/register" className="btn btn-outline-light btn-lg text-decoration-none">
                    <i className="bi bi-person-plus me-2"></i>
                    Create Account
                  </Link>
                )}
              </div>
            </Col>
          </Row>
        </Container>
      </div>

      <style>{`
        @keyframes float {
          0%, 100% { transform: translateY(0px); }
          50% { transform: translateY(-20px); }
        }
        .min-vh-50 { min-height: 50vh; }
      `}</style>
    </div>
  );
};

export default HomePage;
