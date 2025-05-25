import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { airportService, flightService } from '../../services';
import { Airport, Flight, FlightSearch as FlightSearchType } from '../../models';
import FlightList from './FlightList';
import './FlightSearch.css';

interface SearchFormData {
  departureAirportId: number;
  arrivalAirportId: number;
  departureDate: string;
  passengerCount: number;
}

const FlightSearch: React.FC = () => {
  const [airports, setAirports] = useState<Airport[]>([]);
  const [flights, setFlights] = useState<Flight[]>([]);
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);
  const [error, setError] = useState('');

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors }
  } = useForm<SearchFormData>({
    defaultValues: {
      passengerCount: 1,
      departureDate: new Date().toISOString().split('T')[0]
    }
  });

  const departureAirportId = watch('departureAirportId');

  useEffect(() => {
    loadAirports();
  }, []);

  const loadAirports = async () => {
    try {
      setLoading(true);
      const airportData = await airportService.getAllAirports();
      setAirports(airportData);
    } catch (err: any) {
      setError('Failed to load airports');
      console.error('Error loading airports:', err);
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: SearchFormData) => {
    try {
      setSearching(true);
      setError('');
      
      // Simple search by date - you can expand this later
      const searchResults = await flightService.searchFlightsByDate(data.departureDate);
      setFlights(searchResults);
    } catch (err: any) {
      console.error('Search error:', err);
      setError(err.response?.data?.message || err.message || 'Search failed. Please try again.');
      setFlights([]);
    } finally {
      setSearching(false);
    }
  };

  if (loading) {
    return (
      <div className="flight-search">
        <div className="loading">Loading search form...</div>
      </div>
    );
  }

  return (
    <div className="flight-search">
      <div className="search-header">
        <h1>Search Flights</h1>
        <p>Find the perfect flight for your journey</p>
      </div>

      <div className="search-form-container">
        <form onSubmit={handleSubmit(onSubmit)} className="search-form">
          {error && <div className="error-message">{error}</div>}

          <div className="search-fields">
            <div className="field-group">
              <label>From</label>
              <select
                {...register('departureAirportId', {
                  required: 'Please select departure airport',
                  valueAsNumber: true
                })}
                className={errors.departureAirportId ? 'error' : ''}
              >
                <option value="">Select departure city</option>
                {airports.map(airport => (
                  <option key={airport.airportId} value={airport.airportId}>
                    {airport.cityName} - {airport.airportName}
                  </option>
                ))}
              </select>
              {errors.departureAirportId && (
                <span className="field-error">{errors.departureAirportId.message}</span>
              )}
            </div>

            <div className="field-group">
              <label>To</label>
              <select
                {...register('arrivalAirportId', {
                  required: 'Please select arrival airport',
                  valueAsNumber: true
                })}
                className={errors.arrivalAirportId ? 'error' : ''}
              >
                <option value="">Select destination city</option>
                {airports
                  .filter(airport => airport.airportId !== departureAirportId)
                  .map(airport => (
                    <option key={airport.airportId} value={airport.airportId}>
                      {airport.cityName} - {airport.airportName}
                    </option>
                  ))}
              </select>
              {errors.arrivalAirportId && (
                <span className="field-error">{errors.arrivalAirportId.message}</span>
              )}
            </div>

            <div className="field-group">
              <label>Departure Date</label>
              <input
                type="date"
                {...register('departureDate', {
                  required: 'Please select departure date'
                })}
                min={new Date().toISOString().split('T')[0]}
                className={errors.departureDate ? 'error' : ''}
              />
              {errors.departureDate && (
                <span className="field-error">{errors.departureDate.message}</span>
              )}
            </div>

            <div className="field-group">
              <label>Passengers</label>
              <select
                {...register('passengerCount', {
                  required: 'Please select number of passengers',
                  valueAsNumber: true
                })}
                className={errors.passengerCount ? 'error' : ''}
              >
                {[1, 2, 3, 4, 5, 6, 7, 8, 9].map(num => (
                  <option key={num} value={num}>
                    {num} {num === 1 ? 'Passenger' : 'Passengers'}
                  </option>
                ))}
              </select>
              {errors.passengerCount && (
                <span className="field-error">{errors.passengerCount.message}</span>
              )}
            </div>

            <button 
              type="submit" 
              className="search-btn"
              disabled={searching}
            >
              {searching ? 'Searching...' : 'Search Flights'}
            </button>
          </div>
        </form>
      </div>

      {flights.length > 0 && (
        <div className="search-results">
          <FlightList 
            flights={flights} 
            passengerCount={watch('passengerCount')}
          />
        </div>
      )}
    </div>
  );
};

export default FlightSearch;
