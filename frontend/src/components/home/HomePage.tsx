import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import './HomePage.css';

const HomePage: React.FC = () => {
  const { user } = useAuth();

  const features = [
    {
      icon: '🔍',
      title: 'Smart Search',
      description: 'Find flights with our intelligent search that compares prices and schedules across multiple airlines.'
    },
    {
      icon: '💳',
      title: 'Secure Booking',
      description: 'Book with confidence using our secure payment system and instant confirmation.'
    },
    {
      icon: '📱',
      title: 'Mobile Ready',
      description: 'Access your bookings anywhere with our responsive design that works on all devices.'
    },
    {
      icon: '🎯',
      title: 'Best Prices',
      description: 'We guarantee competitive prices and offer price matching for the best deals.'
    }
  ];

  const popularDestinations = [
    { city: 'New York', country: 'USA', image: '🗽' },
    { city: 'London', country: 'UK', image: '🏰' },
    { city: 'Tokyo', country: 'Japan', image: '🗾' },
    { city: 'Paris', country: 'France', image: '🗼' },
    { city: 'Dubai', country: 'UAE', image: '🏜️' },
    { city: 'Sydney', country: 'Australia', image: '🏖️' }
  ];

  return (
    <div className="homepage">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-content">
          <h1>Find Your Perfect Flight</h1>
          <p>Discover amazing destinations with unbeatable prices and seamless booking experience</p>
          <div className="hero-actions">
            <Link to="/search" className="btn btn-primary btn-large">
              Search Flights
            </Link>
            {!user && (
              <Link to="/register" className="btn btn-secondary btn-large">
                Sign Up Free
              </Link>
            )}
          </div>
        </div>
        <div className="hero-image">
          <div className="airplane-animation">✈️</div>
        </div>
      </section>

      {/* Features Section */}
      <section className="features">
        <div className="container">
          <h2>Why Choose FlightMS?</h2>
          <div className="features-grid">
            {features.map((feature, index) => (
              <div key={index} className="feature-card">
                <div className="feature-icon">{feature.icon}</div>
                <h3>{feature.title}</h3>
                <p>{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Popular Destinations */}
      <section className="destinations">
        <div className="container">
          <h2>Popular Destinations</h2>
          <div className="destinations-grid">
            {popularDestinations.map((destination, index) => (
              <div key={index} className="destination-card">
                <div className="destination-image">{destination.image}</div>
                <div className="destination-info">
                  <h3>{destination.city}</h3>
                  <p>{destination.country}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="cta">
        <div className="container">
          <div className="cta-content">
            <h2>Ready to Start Your Journey?</h2>
            <p>Join thousands of satisfied travelers who trust FlightMS for their travel needs</p>
            <div className="cta-actions">
              <Link to="/search" className="btn btn-primary btn-large">
                Book Your Flight
              </Link>
              {user ? (
                <Link to="/dashboard" className="btn btn-outline">
                  View My Bookings
                </Link>
              ) : (
                <Link to="/register" className="btn btn-outline">
                  Create Account
                </Link>
              )}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;
