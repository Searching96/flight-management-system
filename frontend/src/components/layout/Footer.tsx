import React from 'react';
import './Footer.css';

const Footer: React.FC = () => {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-content">
          <div className="footer-section">
            <h3>Flight Management System</h3>
            <p>Your trusted partner for flight booking and management.</p>
            <div className="social-links">
              <a href="#" aria-label="Facebook">📘</a>
              <a href="#" aria-label="Twitter">🐦</a>
              <a href="#" aria-label="Instagram">📷</a>
              <a href="#" aria-label="LinkedIn">💼</a>
            </div>
          </div>

          <div className="footer-section">
            <h4>Quick Links</h4>
            <ul>
              <li><a href="/search">Search Flights</a></li>
              <li><a href="/dashboard">My Bookings</a></li>
              <li><a href="/support">Customer Support</a></li>
              <li><a href="/about">About Us</a></li>
            </ul>
          </div>

          <div className="footer-section">
            <h4>Support</h4>
            <ul>
              <li><a href="/help">Help Center</a></li>
              <li><a href="/contact">Contact Us</a></li>
              <li><a href="/faq">FAQ</a></li>
              <li><a href="/terms">Terms of Service</a></li>
            </ul>
          </div>

          <div className="footer-section">
            <h4>Contact Info</h4>
            <ul>
              <li>📞 +1 (555) 123-4567</li>
              <li>✉️ support@flightms.com</li>
              <li>📍 123 Aviation Street, Sky City</li>
              <li>🕒 24/7 Customer Support</li>
            </ul>
          </div>
        </div>

        <div className="footer-bottom">
          <p>&copy; 2024 Flight Management System. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
