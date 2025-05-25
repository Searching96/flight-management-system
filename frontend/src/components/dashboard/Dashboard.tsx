import React, { useState, useEffect } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { ticketService } from '../../services';
import { Ticket } from '../../models';
import TicketCard from '../tickets/TicketCard';
import './Dashboard.css';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<'upcoming' | 'past' | 'all'>('upcoming');

  useEffect(() => {
    if (user) {
      loadUserTickets();
    }
  }, [user]);

  const loadUserTickets = async () => {
    try {
      setLoading(true);
      const userTickets = await ticketService.getTicketsByCustomer(user!.accountId!);
      setTickets(userTickets);
    } catch (err: any) {
      setError('Failed to load your bookings');
    } finally {
      setLoading(false);
    }
  };

  const filterTickets = () => {
    const now = new Date();
    switch (activeTab) {
      case 'upcoming':
        return tickets.filter(ticket => ticket.departureTime && new Date(ticket.departureTime) > now);
      case 'past':
        return tickets.filter(ticket => ticket.departureTime && new Date(ticket.departureTime) <= now);
      case 'all':
      default:
        return tickets;
    }
  };

  const getStats = () => {
    const now = new Date();
    const upcoming = tickets.filter(ticket => ticket.departureTime && new Date(ticket.departureTime) > now);
    const past = tickets.filter(ticket => ticket.departureTime && new Date(ticket.departureTime) <= now);
    
    return {
      total: tickets.length,
      upcoming: upcoming.length,
      past: past.length
    };
  };

  const stats = getStats();
  const filteredTickets = filterTickets();

  if (loading) {
    return (
      <div className="dashboard">
        <div className="loading">Loading your dashboard...</div>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Welcome back, {user?.accountName}!</h1>
        <p>Manage your flights and bookings</p>
      </div>

      <div className="dashboard-stats">
        <div className="stat-card">
          <div className="stat-number">{stats.total}</div>
          <div className="stat-label">Total Bookings</div>
        </div>
        <div className="stat-card">
          <div className="stat-number">{stats.upcoming}</div>
          <div className="stat-label">Upcoming Flights</div>
        </div>
        <div className="stat-card">
          <div className="stat-number">{stats.past}</div>
          <div className="stat-label">Past Flights</div>
        </div>
      </div>

      <div className="dashboard-content">
        <div className="section-header">
          <h2>Your Bookings</h2>
          <div className="tab-buttons">
            <button 
              className={activeTab === 'upcoming' ? 'active' : ''}
              onClick={() => setActiveTab('upcoming')}
            >
              Upcoming
            </button>
            <button 
              className={activeTab === 'past' ? 'active' : ''}
              onClick={() => setActiveTab('past')}
            >
              Past
            </button>
            <button 
              className={activeTab === 'all' ? 'active' : ''}
              onClick={() => setActiveTab('all')}
            >
              All
            </button>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="tickets-container">
          {filteredTickets.length === 0 ? (
            <div className="no-tickets">
              <h3>No bookings found</h3>
              <p>
                {activeTab === 'upcoming' 
                  ? "You don't have any upcoming flights. Start planning your next trip!"
                  : activeTab === 'past'
                  ? "No past flights to display."
                  : "You haven't made any bookings yet. Search for flights to get started!"
                }
              </p>
              <a href="/search" className="btn btn-primary">Search Flights</a>
            </div>
          ) : (
            filteredTickets.map(ticket => (
              <TicketCard 
                key={ticket.ticketId} 
                ticket={ticket}
                onCancel={loadUserTickets}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
