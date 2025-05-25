import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { airportService } from '../../services';
import { Airport } from '../../models';
import './AirportManagement.css';

interface AirportFormData {
  airportName: string;
  cityName: string;
  countryName: string;
}

const AirportManagement: React.FC = () => {
  const [airports, setAirports] = useState<Airport[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingAirport, setEditingAirport] = useState<Airport | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm<AirportFormData>();

  useEffect(() => {
    loadAirports();
  }, []);

  const loadAirports = async () => {
    try {
      setLoading(true);
      const data = await airportService.getAllAirports();
      setAirports(data);
    } catch (err: any) {
      setError('Failed to load airports');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: AirportFormData) => {
    try {
      if (editingAirport) {
        await airportService.updateAirport(editingAirport.airportId!, data);
      } else {
        await airportService.createAirport(data);
      }
      
      loadAirports();
      handleCancel();
    } catch (err: any) {
      setError(err.message || 'Failed to save airport');
    }
  };

  const handleEdit = (airport: Airport) => {
    setEditingAirport(airport);
    reset({
      airportName: airport.airportName,
      cityName: airport.cityName,
      countryName: airport.countryName
    });
    setShowForm(true);
  };

  const handleDelete = async (airportId: number) => {
    if (!window.confirm('Are you sure you want to delete this airport?')) return;
    
    try {
      await airportService.deleteAirport(airportId);
      loadAirports();
    } catch (err: any) {
      setError(err.message || 'Failed to delete airport');
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingAirport(null);
    reset();
    setError('');
  };

  if (loading) {
    return <div className="loading">Loading airports...</div>;
  }

  return (
    <div className="airport-management">
      <div className="management-header">
        <h2>Airport Management</h2>
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
        >
          Add New Airport
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {showForm && (
        <div className="form-modal">
          <div className="form-container">
            <h3>{editingAirport ? 'Edit Airport' : 'Add New Airport'}</h3>
            
            <form onSubmit={handleSubmit(onSubmit)}>
              <div className="form-group">
                <label>Airport Name</label>
                <input
                  type="text"
                  {...register('airportName', {
                    required: 'Airport name is required'
                  })}
                  className={errors.airportName ? 'error' : ''}
                  placeholder="e.g., John F. Kennedy International Airport"
                />
                {errors.airportName && (
                  <span className="field-error">{errors.airportName.message}</span>
                )}
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>City</label>
                  <input
                    type="text"
                    {...register('cityName', {
                      required: 'City name is required'
                    })}
                    className={errors.cityName ? 'error' : ''}
                    placeholder="e.g., New York"
                  />
                  {errors.cityName && (
                    <span className="field-error">{errors.cityName.message}</span>
                  )}
                </div>

                <div className="form-group">
                  <label>Country</label>
                  <input
                    type="text"
                    {...register('countryName', {
                      required: 'Country name is required'
                    })}
                    className={errors.countryName ? 'error' : ''}
                    placeholder="e.g., United States"
                  />
                  {errors.countryName && (
                    <span className="field-error">{errors.countryName.message}</span>
                  )}
                </div>
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={handleCancel}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingAirport ? 'Update Airport' : 'Create Airport'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="airports-table">
        <table>
          <thead>
            <tr>
              <th>Airport Name</th>
              <th>City</th>
              <th>Country</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {airports.map(airport => (
              <tr key={airport.airportId}>
                <td>{airport.airportName}</td>
                <td>{airport.cityName}</td>
                <td>{airport.countryName}</td>
                <td>
                  <div className="actions">
                    <button 
                      className="btn btn-sm btn-secondary"
                      onClick={() => handleEdit(airport)}
                    >
                      Edit
                    </button>
                    <button 
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(airport.airportId!)}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {airports.length === 0 && (
          <div className="no-data">
            <p>No airports found. Add your first airport to get started.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default AirportManagement;
