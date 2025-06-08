import { useState, useCallback } from 'react';
import { Flight, FlightRequest } from '../models';
import { flightService } from '../services';

export function useFlights() {
  const [flights, setFlights] = useState<Flight[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const loadFlights = useCallback(async () => {
    try {
      setLoading(true);
      const data = await flightService.getAllFlights();
      setFlights(data);
      setError('');
    } catch (err: any) {
      setError('Failed to load flights: ' + (err.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  }, []);
  
  const createFlight = useCallback(async (data: FlightRequest): Promise<Flight | null> => {
    try {
      const newFlight = await flightService.createFlight(data);
      await loadFlights(); // Refresh the list
      return newFlight;
    } catch (err: any) {
      setError('Failed to create flight: ' + (err.message || 'Unknown error'));
      return null;
    }
  }, [loadFlights]);
  
  const updateFlight = useCallback(async (flightId: number, data: FlightRequest): Promise<boolean> => {
    try {
      await flightService.updateFlight(flightId, data);
      await loadFlights(); // Refresh the list
      return true;
    } catch (err: any) {
      setError('Failed to update flight: ' + (err.message || 'Unknown error'));
      return false;
    }
  }, [loadFlights]);
  
  const deleteFlight = useCallback(async (flightId: number): Promise<boolean> => {
    try {
      await flightService.deleteFlight(flightId);
      await loadFlights(); // Refresh the list
      return true;
    } catch (err: any) {
      setError('Failed to delete flight: ' + (err.message || 'Unknown error'));
      return false;
    }
  }, [loadFlights]);
  
  return {
    flights,
    loading,
    error,
    loadFlights,
    createFlight,
    updateFlight,
    deleteFlight,
    clearError: () => setError('')
  };
}
