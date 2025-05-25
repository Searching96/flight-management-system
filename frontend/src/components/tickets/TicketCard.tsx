import React, { useState } from 'react';
import { Ticket } from '../../models';
import { ticketService } from '../../services';
import './TicketCard.css';

interface TicketCardProps {
  ticket: Ticket;
  onCancel?: () => void;
}

const TicketCard: React.FC<TicketCardProps> = ({ ticket, onCancel }) => {
  const [cancelling, setCancelling] = useState(false);

  const formatTime = (dateTime: string) => {
    return new Date(dateTime).toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  const formatDate = (dateTime: string) => {
    return new Date(dateTime).toLocaleDateString();
  };

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'confirmed': return '#28a745';
      case 'cancelled': return '#dc3545';
      case 'pending': return '#ffc107';
      default: return '#6c757d';
    }
  };

  const canCancelTicket = () => {
    const departureTime = new Date(ticket.departureTime);
    const now = new Date();
    const hoursDifference = (departureTime.getTime() - now.getTime()) / (1000 * 60 * 60);
    
    return ticket.ticketStatus === 'Confirmed' && hoursDifference > 24;
  };

  const handleCancelTicket = async () => {
    if (!window.confirm('Are you sure you want to cancel this ticket? This action cannot be undone.')) {
      return;
    }

    try {
      setCancelling(true);
      await ticketService.cancelTicket(ticket.ticketId!);
      alert('Ticket cancelled successfully');
      if (onCancel) onCancel();
    } catch (error: any) {
      alert(error.message || 'Failed to cancel ticket');
    } finally {
      setCancelling(false);
    }
  };

  return (
    <div className="ticket-card">
      <div className="ticket-header">
        <div className="ticket-info">
          <span className="ticket-id">Ticket #{ticket.ticketId}</span>
          <span className="flight-code">{ticket.flightCode}</span>
        </div>
        <div 
          className="ticket-status"
          style={{ backgroundColor: getStatusColor(ticket.ticketStatus) }}
        >
          {ticket.ticketStatus}
        </div>
      </div>

      <div className="ticket-route">
        <div className="departure">
          <div className="time">{formatTime(ticket.departureTime)}</div>
          <div className="airport">{ticket.departureCityName}</div>
          <div className="date">{formatDate(ticket.departureTime)}</div>
        </div>

        <div className="flight-path">
          <div className="duration">
            {(() => {
              const departure = new Date(ticket.departureTime);
              const arrival = new Date(ticket.arrivalTime);
              const durationMs = arrival.getTime() - departure.getTime();
              const hours = Math.floor(durationMs / (1000 * 60 * 60));
              const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
              return `${hours}h ${minutes}m`;
            })()}
          </div>
          <div className="path-line">
            <div className="line"></div>
            <div className="plane-icon">✈️</div>
          </div>
        </div>

        <div className="arrival">
          <div className="time">{formatTime(ticket.arrivalTime)}</div>
          <div className="airport">{ticket.arrivalCityName}</div>
          <div className="date">{formatDate(ticket.arrivalTime)}</div>
        </div>
      </div>

      <div className="ticket-details">
        <div className="passenger-info">
          <span className="label">Passenger:</span>
          <span className="value">{ticket.passengerName}</span>
        </div>
        <div className="seat-info">
          <span className="label">Seat:</span>
          <span className="value">{ticket.seatNumber || 'TBA'}</span>
        </div>
        <div className="class-info">
          <span className="label">Class:</span>
          <span className="value">{ticket.ticketClassName}</span>
        </div>
      </div>

      {canCancelTicket() && (
        <div className="ticket-actions">
          <button 
            className="cancel-btn"
            onClick={handleCancelTicket}
            disabled={cancelling}
          >
            {cancelling ? 'Cancelling...' : 'Cancel Ticket'}
          </button>
        </div>
      )}
    </div>
  );
};

export default TicketCard;
