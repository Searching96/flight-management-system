import React, { useState } from 'react';
import FlightManagement from './FlightManagement';
import AirportManagement from './AirportManagement';
import ParameterSettings from './ParameterSettings';
import PlaneManagement from './PlaneManagement';
import TicketClassManagement from './TicketClassManagement';
import FlightTicketClassManagement from './FlightTicketClassManagement';
import './AdminPanel.css';
import { usePermissions } from '../../hooks/useAuth';

type AdminTab = 'overview' | 'flights' | 'airports' | 'planes' | 'ticket-classes' | 'flight-ticket-classes' | 'parameters';

export const AdminPanel: React.FC = () => {
  const { canViewAdmin, canManageFlights } = usePermissions();
  const [activeTab, setActiveTab] = useState<AdminTab>('overview');

  // Redirect if user doesn't have admin permissions (accountType should be 2 for employees)
  if (!canViewAdmin) {
    return (
      <div className="unauthorized">
        <h2>Access Denied</h2>
        <p>You do not have permission to access the admin panel.</p>
      </div>
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
    <div className="admin-panel">
      <div className="admin-header">
        <h1>Admin Panel</h1>
        <p>Manage flights, airports, and system settings</p>
      </div>

      <nav className="admin-nav">
        <button
          className={activeTab === 'overview' ? 'active' : ''}
          onClick={() => setActiveTab('overview')}
        >
          📊 Overview
        </button>
        <button
          className={activeTab === 'flights' ? 'active' : ''}
          onClick={() => setActiveTab('flights')}
        >
          ✈️ Flights
        </button>
        <button
          className={activeTab === 'airports' ? 'active' : ''}
          onClick={() => setActiveTab('airports')}
        >
          🏢 Airports
        </button>
        <button
          className={activeTab === 'planes' ? 'active' : ''}
          onClick={() => setActiveTab('planes')}
        >
          🛩️ Aircraft Fleet
        </button>
        <button
          className={activeTab === 'ticket-classes' ? 'active' : ''}
          onClick={() => setActiveTab('ticket-classes')}
        >
          🎟️ Ticket Classes
        </button>
        <button
          className={activeTab === 'flight-ticket-classes' ? 'active' : ''}
          onClick={() => setActiveTab('flight-ticket-classes')}
        >
          ✈️🎟️ Flight Class Assignment
        </button>
        <button
          className={activeTab === 'parameters' ? 'active' : ''}
          onClick={() => setActiveTab('parameters')}
        >
          ⚙️ Settings
        </button>
      </nav>

      <div className="admin-content">
        {renderContent()}
      </div>
    </div>
  );
};

// Admin Overview Component
const AdminOverview: React.FC = () => {
  return (
    <div className="admin-overview">
      <div className="overview-stats">
        <div className="stat-card">
          <div className="stat-icon">✈️</div>
          <div className="stat-info">
            <h3>Total Flights</h3>
            <p className="stat-number">156</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">🏢</div>
          <div className="stat-info">
            <h3>Active Airports</h3>
            <p className="stat-number">23</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">👥</div>
          <div className="stat-info">
            <h3>Total Passengers</h3>
            <p className="stat-number">8,432</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">💰</div>
          <div className="stat-info">
            <h3>Revenue</h3>
            <p className="stat-number">$2.1M</p>
          </div>
        </div>
      </div>

      <div className="overview-actions">
        <div className="action-section">
          <h3>Quick Actions</h3>
          <div className="action-buttons">
            <button className="action-btn">
              <span>➕</span>
              Add New Flight
            </button>
            <button className="action-btn">
              <span>🏢</span>
              Add New Airport
            </button>
            <button className="action-btn">
              <span>📊</span>
              View Reports
            </button>
            <button className="action-btn">
              <span>⚙️</span>
              System Settings
            </button>
          </div>
        </div>

        <div className="recent-activity">
          <h3>Recent Activity</h3>
          <div className="activity-list">
            <div className="activity-item">
              <span className="activity-icon">✈️</span>
              <div className="activity-info">
                <p>New flight FL001 added</p>
                <span className="activity-time">2 hours ago</span>
              </div>
            </div>
            <div className="activity-item">
              <span className="activity-icon">🏢</span>
              <div className="activity-info">
                <p>Airport LAX updated</p>
                <span className="activity-time">4 hours ago</span>
              </div>
            </div>
            <div className="activity-item">
              <span className="activity-icon">⚙️</span>
              <div className="activity-info">
                <p>System parameters updated</p>
                <span className="activity-time">1 day ago</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminPanel;
