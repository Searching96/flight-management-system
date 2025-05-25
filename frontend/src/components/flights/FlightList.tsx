import React, { useState } from 'react';
import { Flight } from '../../models';
import FlightCard from './FlightCard';
import './FlightList.css';

interface FlightListProps {
  flights: Flight[];
  passengerCount: number;
}

const FlightList: React.FC<FlightListProps> = ({ flights, passengerCount }) => {
  const [sortBy, setSortBy] = useState<'price' | 'departure' | 'duration'>('departure');
  const [filterBy, setFilterBy] = useState<'all' | 'morning' | 'afternoon' | 'evening'>('all');

  const filterFlights = (flights: Flight[]) => {
    if (filterBy === 'all') return flights;
    
    return flights.filter(flight => {
      const hour = new Date(flight.departureTime).getHours();
      switch (filterBy) {
        case 'morning': return hour >= 6 && hour < 12;
        case 'afternoon': return hour >= 12 && hour < 18;
        case 'evening': return hour >= 18 || hour < 6;
        default: return true;
      }
    });
  };

  const sortFlights = (flights: Flight[]) => {
    return [...flights].sort((a, b) => {
      switch (sortBy) {
        case 'departure':
          return new Date(a.departureTime).getTime() - new Date(b.departureTime).getTime();
        case 'duration':
          const durationA = new Date(a.arrivalTime).getTime() - new Date(a.departureTime).getTime();
          const durationB = new Date(b.arrivalTime).getTime() - new Date(b.departureTime).getTime();
          return durationA - durationB;
        case 'price':
          // For now, sort by flight code as price is not available
          return a.flightCode.localeCompare(b.flightCode);
        default:
          return 0;
      }
    });
  };

  const processedFlights = sortFlights(filterFlights(flights));

  return (
    <div className="flight-list">
      <div className="list-header">
        <div className="results-info">
          <h2>{flights.length} flights found</h2>
          <p>For {passengerCount} {passengerCount === 1 ? 'passenger' : 'passengers'}</p>
        </div>
        
        <div className="list-controls">
          <div className="filter-group">
            <label>Filter by time:</label>
            <select value={filterBy} onChange={(e) => setFilterBy(e.target.value as any)}>
              <option value="all">All times</option>
              <option value="morning">Morning (6AM - 12PM)</option>
              <option value="afternoon">Afternoon (12PM - 6PM)</option>
              <option value="evening">Evening (6PM - 6AM)</option>
            </select>
          </div>
          
          <div className="sort-group">
            <label>Sort by:</label>
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value as any)}>
              <option value="departure">Departure time</option>
              <option value="duration">Flight duration</option>
              <option value="price">Price</option>
            </select>
          </div>
        </div>
      </div>

      <div className="flights-container">
        {processedFlights.length === 0 ? (
          <div className="no-flights">
            <p>No flights match your filters. Try adjusting your search criteria.</p>
          </div>
        ) : (
          processedFlights.map(flight => (
            <FlightCard
              key={flight.flightId}
              flight={flight}
              passengerCount={passengerCount}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default FlightList;
