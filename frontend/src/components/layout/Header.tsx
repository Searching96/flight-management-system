import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import './Header.css';

const Header: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          <Link to="/" className="logo">
            <span className="logo-icon">✈️</span>
            <span>FlightMS</span>
          </Link>

          <nav className={`nav ${mobileMenuOpen ? 'nav-open' : ''}`}>
            <ul className="nav-list">
              <li><Link to="/" className="nav-link">Home</Link></li>
              <li><Link to="/search" className="nav-link">Search Flights</Link></li>
              
              {user ? (
                <>
                  <li><Link to="/dashboard" className="nav-link">Dashboard</Link></li>
                  {user.accountType === 1 && (
                    <li><Link to="/admin" className="nav-link">Admin</Link></li>
                  )}
                </>
              ) : (
                <>
                  <li><Link to="/login" className="nav-link">Login</Link></li>
                  <li><Link to="/register" className="nav-link">Register</Link></li>
                </>
              )}
            </ul>
          </nav>

          <div className="user-menu">
            {user ? (
              <>
                <span className="user-name">Welcome, {user.accountName}</span>
                <button onClick={handleLogout} className="logout-btn">
                  Logout
                </button>
              </>
            ) : (
              <Link to="/login" className="btn btn-primary">
                Sign In
              </Link>
            )}
          </div>

          <button 
            className="mobile-menu-btn"
            onClick={toggleMobileMenu}
          >
            ☰
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;
