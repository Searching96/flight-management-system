import React, { useState } from 'react';
import FlightManagement from './FlightManagement';
import AirportManagement from './AirportManagement';
import ParameterSettings from './ParameterSettings';
import './AdminPanel.css';

type AdminTab = 'flights' | 'airports' | 'parameters' | 'overview';

const AdminPanel: React.FC = () => {
  const [activeTab, setActiveTab] = useState<AdminTab>('overview');

  const renderTabContent = () => {
    switch (activeTab) {
      case 'flights':
        return <FlightManagement />;
      case 'airports':
        return <AirportManagement />;
      case 'parameters':
        return <ParameterSettings />;
      case 'overview':
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

      <div className="admin-navigation">
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
          className={activeTab === 'parameters' ? 'active' : ''}
          onClick={() => setActiveTab('parameters')}
        >
          ⚙️ Settings
        </button>
      </div>

      <div className="admin-content">
        {renderTabContent()}
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
