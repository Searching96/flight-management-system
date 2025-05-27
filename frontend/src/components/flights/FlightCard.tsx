import React, { useState } from 'react';
import { Flight, TicketClass } from '../../models';
import './FlightCard.css';

interface FlightCardProps {
  flight: Flight;
  onBookFlight: (flightId: number, ticketClassId: number) => void;
  searchContext?: {
    passengerCount: number;
    allTicketClasses: TicketClass[];
    availability: any[];
    selectedTicketClass?: number | null;
    searchedForAllClasses?: boolean;
  };
}

const FlightCard: React.FC<FlightCardProps> = ({ 
  flight, 
  onBookFlight, 
  searchContext 
}) => {
  const [selectedClassForBooking, setSelectedClassForBooking] = useState<number | null>(
    searchContext?.selectedTicketClass || null
  );

  const formatTime = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  };

  const formatDate = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    });
  };

  const calculateDuration = () => {
    const departure = new Date(flight.departureTime);
    const arrival = new Date(flight.arrivalTime);
    const durationMs = arrival.getTime() - departure.getTime();
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    return `${hours}h ${minutes}m`;
  };

  const getAvailableTicketClasses = () => {
    if (!flight.flightTicketClasses || !searchContext?.allTicketClasses) {
      return [];
    }

    const requestedSeats = searchContext.passengerCount || 1;

    return flight.flightTicketClasses
      .filter(ftc => ftc.remainingTicketQuantity && ftc.remainingTicketQuantity >= requestedSeats)
      .map(ftc => {
        const ticketClass = searchContext.allTicketClasses.find(
          tc => tc.ticketClassId === ftc.ticketClassId
        );
        return {
          ...ftc,
          ticketClass,
          price: Number(ftc.specifiedFare)
        };
      })
      .sort((a, b) => a.price - b.price); // Sort by price
  };

  const getLowestPrice = () => {
    const availableClasses = getAvailableTicketClasses();
    return availableClasses.length > 0 ? availableClasses[0].price : null;
  };

  const isFlightAvailable = () => {
    return getAvailableTicketClasses().length > 0;
  };

  const handleClassSelection = (ticketClassId: number) => {
    setSelectedClassForBooking(ticketClassId);
  };

  const handleBooking = () => {
    if (selectedClassForBooking) {
      onBookFlight(flight.flightId!, selectedClassForBooking);
    }
  };

  const availableClasses = getAvailableTicketClasses();
  const isAvailable = isFlightAvailable();
  const lowestPrice = getLowestPrice();
  const showAllClasses = searchContext?.searchedForAllClasses || availableClasses.length > 1;

  return (
    <div className={`flight-card ${!isAvailable ? 'unavailable' : ''}`}>
      <div className="flight-header">
        <div className="flight-number">
          <span className="airline-code">{flight.flightCode}</span>
          <span className="plane-type">{flight.planeCode}</span>
        </div>
        <div className="flight-date">
          {formatDate(flight.departureTime)}
        </div>
      </div>

      <div className="flight-route">
        <div className="departure">
          <div className="time">{formatTime(flight.departureTime)}</div>
          <div className="airport">
            <div className="city">{flight.departureCityName}</div>
            <div className="airport-name">{flight.departureAirportName}</div>
          </div>
        </div>

        <div className="flight-duration">
          <div className="duration-line">
            <span className="duration-text">{calculateDuration()}</span>
          </div>
          <div className="flight-info">Direct</div>
        </div>

        <div className="arrival">
          <div className="time">{formatTime(flight.arrivalTime)}</div>
          <div className="airport">
            <div className="city">{flight.arrivalCityName}</div>
            <div className="airport-name">{flight.arrivalAirportName}</div>
          </div>
        </div>
      </div>

      <div className="flight-details">
        {searchContext?.selectedTicketClass && !searchContext?.searchedForAllClasses && (
          <div className="selected-class">
            <span className="class-name">
              {searchContext.allTicketClasses?.find(tc => tc.ticketClassId === searchContext.selectedTicketClass)?.ticketClassName}
            </span>
            {searchContext.passengerCount > 1 && (
              <span className="passenger-count">{searchContext.passengerCount} passengers</span>
            )}
          </div>
        )}

        {/* Ticket Class Options */}
        {showAllClasses && availableClasses.length > 0 && (
          <div className="ticket-classes">
            <h4>Available Classes:</h4>
            <div className="class-options">
              {availableClasses.map(classInfo => (
                <div 
                  key={classInfo.ticketClassId}
                  className={`class-option ${selectedClassForBooking === classInfo.ticketClassId ? 'selected' : ''}`}
                  onClick={() => handleClassSelection(classInfo.ticketClassId!)}
                >
                  <div className="class-info">
                    <span 
                      className="class-name"
                      style={{ color: classInfo.ticketClass?.color || '#333' }}
                    >
                      {classInfo.ticketClass?.ticketClassName}
                    </span>
                    <span className="available-seats">
                      {classInfo.remainingTicketQuantity} seats
                    </span>
                  </div>
                  <div className="class-price">
                    <span className="amount">{classInfo.price.toLocaleString()}</span>
                    <span className="currency">VND</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="available-seats">
          {isAvailable ? (
            <span className="seats-available">
              {flight.flightTicketClasses?.reduce(
                (total, ftc) => total + (ftc.remainingTicketQuantity || 0), 
                0
              )} total seats available
            </span>
          ) : (
            <span className="seats-unavailable">Not enough seats available</span>
          )}
        </div>
      </div>

      <div className="flight-footer">
        <div className="price-section">
          {!showAllClasses && lowestPrice && (
            <>
              <div className="price">
                <span className="amount">{lowestPrice.toLocaleString()}</span>
                <span className="currency">VND</span>
              </div>
              {searchContext?.passengerCount && searchContext.passengerCount > 1 && (
                <div className="price-per-person">per person</div>
              )}
            </>
          )}
          {showAllClasses && (
            <div className="price-range">
              <span className="from-price">From {lowestPrice?.toLocaleString()} VND</span>
              {searchContext?.passengerCount && searchContext.passengerCount > 1 && (
                <div className="price-per-person">per person</div>
              )}
            </div>
          )}
        </div>

        <div className="book-section">
          {isAvailable ? (
            <button 
              className={`book-button ${showAllClasses && !selectedClassForBooking ? 'disabled' : ''}`}
              onClick={handleBooking}
              disabled={showAllClasses && !selectedClassForBooking}
            >
              {showAllClasses && !selectedClassForBooking ? 'Select Class' : 'Book Flight'}
            </button>
          ) : (
            <button className="book-button disabled" disabled>
              Unavailable
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default FlightCard;
