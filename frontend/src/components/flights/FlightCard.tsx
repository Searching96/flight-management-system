import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Flight, FlightTicketClass } from '../../models';
import { flightService } from '../../services';
import { useAuth } from '../../hooks/useAuth';
import './FlightCard.css';

interface FlightCardProps {
  flight: Flight;
  passengerCount?: number;
  onSelect?: (flight: Flight) => void;
}

const FlightCard: React.FC<FlightCardProps> = ({ 
  flight, 
  passengerCount = 1, 
  onSelect 
}) => {
  const [ticketClasses, setTicketClasses] = useState<FlightTicketClass[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    loadTicketClasses();
  }, [flight.flightId]);

  const loadTicketClasses = async () => {
    try {
      if (flight.flightId) {
        const classes = await flightService.getFlightTicketClassesByFlightId(flight.flightId);
        setTicketClasses(classes);
      }
    } catch (error) {
      console.error('Failed to load ticket classes:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatTime = (dateTime: string) => {
    return new Date(dateTime).toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  const formatDate = (dateTime: string) => {
    return new Date(dateTime).toLocaleDateString();
  };

  const calculateDuration = () => {
    const departure = new Date(flight.departureTime);
    const arrival = new Date(flight.arrivalTime);
    const durationMs = arrival.getTime() - departure.getTime();
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    return `${hours}h ${minutes}m`;
  };

  const handleBookFlight = (ticketClassId?: number) => {
    if (!user) {
      navigate('/login', { 
        state: { 
          message: 'Please sign in to book flights',
          returnTo: `/book/${flight.flightId}` 
        }
      });
      return;
    }

    if (onSelect) {
      onSelect(flight);
    } else {
      navigate(`/book/${flight.flightId}`, {
        state: { flight, ticketClassId, passengerCount }
      });
    }
  };

  return (
    <div className="flight-card">
      <div className="flight-header">
        <div className="flight-code">
          <span className="code">{flight.flightCode}</span>
          <span className="plane">✈️ {flight.planeCode}</span>
        </div>
        <div className="flight-price">
          <span className="from">from</span>
          <span className="amount">$299</span>
          <span className="per">per person</span>
        </div>
      </div>

      <div className="flight-route">
        <div className="departure">
          <div className="time">{formatTime(flight.departureTime)}</div>
          <div className="airport">{flight.departureCityName}</div>
          <div className="airport-name">{flight.departureAirportName}</div>
          <div className="date">{formatDate(flight.departureTime)}</div>
        </div>

        <div className="flight-duration">
          <div className="duration">{calculateDuration()}</div>
          <div className="line">
            <div className="line-path"></div>
            <div className="airplane-icon">✈️</div>
          </div>
          <div className="direct">Direct flight</div>
        </div>

        <div className="arrival">
          <div className="time">{formatTime(flight.arrivalTime)}</div>
          <div className="airport">{flight.arrivalCityName}</div>
          <div className="airport-name">{flight.arrivalAirportName}</div>
          <div className="date">{formatDate(flight.arrivalTime)}</div>
        </div>
      </div>

      <div className="ticket-classes">
        {loading ? (
          <div className="loading">Loading ticket classes...</div>
        ) : ticketClasses.length > 0 ? (
          ticketClasses.map(ticketClass => (
            <div key={ticketClass.ticketClassId} className="ticket-class">
              <div className="class-info">
                <span className="class-name">
                  {ticketClass.ticketClassName}
                </span>
                <span className="remaining-seats">
                  {ticketClass.remainingTickets || 0} seats left
                </span>
              </div>
              <div className="class-price">
                <span className="price">
                  ${ticketClass.specifiedFare?.toLocaleString() || '299'}
                </span>
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => handleBookFlight(ticketClass.ticketClassId)}
                  disabled={(ticketClass.remainingTickets || 0) < passengerCount}
                >
                  Book
                </button>
              </div>
            </div>
          ))
        ) : (
          <div className="ticket-class">
            <div className="class-info">
              <span className="class-name">Economy</span>
              <span className="remaining-seats">Available</span>
            </div>
            <div className="class-price">
              <span className="price">$299</span>
              <button
                className="btn btn-primary btn-sm"
                onClick={() => handleBookFlight()}
              >
                Book
              </button>
            </div>
          </div>
        )}
      </div>

      <div className="flight-footer">
        <div className="flight-details">
          <span className="passengers">
            {passengerCount} {passengerCount === 1 ? 'passenger' : 'passengers'}
          </span>
          <span className="class">Economy Class</span>
        </div>
        
        <button 
          className="book-btn"
          onClick={() => handleBookFlight()}
        >
          {user ? 'Book Flight' : 'Sign in to Book'}
        </button>
      </div>
    </div>
  );
};

export default FlightCard;
