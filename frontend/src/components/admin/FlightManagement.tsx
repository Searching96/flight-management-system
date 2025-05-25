import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { flightService, airportService, planeService } from '../../services';
import { Flight, Airport, Plane } from '../../models';
import './FlightManagement.css';

interface FlightFormData {
    flightCode: string;
    departureTime: string;
    arrivalTime: string;
    planeId: number;
    departureAirportId: number;
    arrivalAirportId: number;
}

const FlightManagement: React.FC = () => {
    const [flights, setFlights] = useState<Flight[]>([]);
    const [airports, setAirports] = useState<Airport[]>([]);
    const [planes, setPlanes] = useState<Plane[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [editingFlight, setEditingFlight] = useState<Flight | null>(null);

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors }
    } = useForm<FlightFormData>();

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            setLoading(true);
            const [flightData, airportData, planeData] = await Promise.all([
                flightService.getAllFlights(),
                airportService.getAllAirports(),
                planeService.getAllPlanes()
            ]);

            setFlights(flightData);
            setAirports(airportData);
            setPlanes(planeData);
        } catch (err: any) {
            setError('Failed to load data');
        } finally {
            setLoading(false);
        }
    };

    const onSubmit = async (data: FlightFormData) => {
        try {
            if (editingFlight) {
                await flightService.updateFlight(editingFlight.flightId!, data);
            } else {
                await flightService.createFlight(data);
            }

            loadData();
            handleCancel();
        } catch (err: any) {
            setError(err.message || 'Failed to save flight');
        }
    };

    const handleEdit = (flight: Flight) => {
        setEditingFlight(flight);
        reset({
            flightCode: flight.flightCode,
            departureTime: flight.departureTime.slice(0, 16),
            arrivalTime: flight.arrivalTime.slice(0, 16),
            planeId: flight.planeId,
            departureAirportId: flight.departureAirportId,
            arrivalAirportId: flight.arrivalAirportId
        });
        setShowForm(true);
    };

    const handleDelete = async (flightId: number) => {
        if (!window.confirm('Are you sure you want to delete this flight?')) return;

        try {
            await flightService.deleteFlight(flightId);
            loadData();
        } catch (err: any) {
            setError(err.message || 'Failed to delete flight');
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingFlight(null);
        reset();
        setError('');
    };

    if (loading) {
        return <div className="loading">Loading flight data...</div>;
    }

    return (
        <div className="flight-management">
            <div className="management-header">
                <h2>Flight Management</h2>
                <button
                    className="btn btn-primary"
                    onClick={() => setShowForm(true)}
                >
                    Add New Flight
                </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            {showForm && (
                <div className="form-modal">
                    <div className="form-container">
                        <h3>{editingFlight ? 'Edit Flight' : 'Add New Flight'}</h3>

                        <form onSubmit={handleSubmit(onSubmit)}>
                            <div className="form-row">
                                <div className="form-group">
                                    <label>Flight Code</label>
                                    <input
                                        type="text"
                                        {...register('flightCode', {
                                            required: 'Flight code is required'
                                        })}
                                        className={errors.flightCode ? 'error' : ''}
                                        placeholder="e.g., FL001"
                                    />
                                    {errors.flightCode && (
                                        <span className="field-error">{errors.flightCode.message}</span>
                                    )}
                                </div>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>Departure Airport</label>
                                    <select
                                        {...register('departureAirportId', {
                                            required: 'Departure airport is required',
                                            valueAsNumber: true
                                        })}
                                        className={errors.departureAirportId ? 'error' : ''}
                                    >
                                        <option value="">Select departure airport</option>
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

                                <div className="form-group">
                                    <label>Arrival Airport</label>
                                    <select
                                        {...register('arrivalAirportId', {
                                            required: 'Arrival airport is required',
                                            valueAsNumber: true
                                        })}
                                        className={errors.arrivalAirportId ? 'error' : ''}
                                    >
                                        <option value="">Select arrival airport</option>
                                        {airports.map(airport => (
                                            <option key={airport.airportId} value={airport.airportId}>
                                                {airport.cityName} - {airport.airportName}
                                            </option>
                                        ))}
                                    </select>
                                    {errors.arrivalAirportId && (
                                        <span className="field-error">{errors.arrivalAirportId.message}</span>
                                    )}
                                </div>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>Departure Time</label>
                                    <input
                                        type="datetime-local"
                                        {...register('departureTime', {
                                            required: 'Departure time is required'
                                        })}
                                        className={errors.departureTime ? 'error' : ''}
                                    />
                                    {errors.departureTime && (
                                        <span className="field-error">{errors.departureTime.message}</span>
                                    )}
                                </div>

                                <div className="form-group">
                                    <label>Arrival Time</label>
                                    <input
                                        type="datetime-local"
                                        {...register('arrivalTime', {
                                            required: 'Arrival time is required'
                                        })}
                                        className={errors.arrivalTime ? 'error' : ''}
                                    />
                                    {errors.arrivalTime && (
                                        <span className="field-error">{errors.arrivalTime.message}</span>
                                    )}
                                </div>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label>Aircraft</label>
                                    <select
                                        {...register('planeId', {
                                            required: 'Aircraft is required',
                                            valueAsNumber: true
                                        })}
                                        className={errors.planeId ? 'error' : ''}
                                    >
                                        <option value="">Select aircraft</option>
                                        {planes.map(plane => (
                                            <option key={plane.planeId} value={plane.planeId}>
                                                {plane.planeCode} - {plane.planeType}
                                            </option>
                                        ))}
                                    </select>
                                    {errors.planeId && (
                                        <span className="field-error">{errors.planeId.message}</span>
                                    )}
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="button" className="btn btn-secondary" onClick={handleCancel}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingFlight ? 'Update Flight' : 'Create Flight'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            <div className="flights-table">
                <table>
                    <thead>
                        <tr>
                            <th>Flight Code</th>
                            <th>Route</th>
                            <th>Departure</th>
                            <th>Arrival</th>
                            <th>Aircraft</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {flights.map(flight => (
                            <tr key={flight.flightId}>
                                <td className="flight-code">{flight.flightCode}</td>
                                <td>
                                    <div className="route">
                                        <span>{flight.departureCityName}</span>
                                        <span className="arrow">→</span>
                                        <span>{flight.arrivalCityName}</span>
                                    </div>
                                </td>
                                <td>
                                    <div className="datetime">
                                        <div>{new Date(flight.departureTime).toLocaleDateString()}</div>
                                        <div>{new Date(flight.departureTime).toLocaleTimeString()}</div>
                                    </div>
                                </td>
                                <td>
                                    <div className="datetime">
                                        <div>{new Date(flight.arrivalTime).toLocaleDateString()}</div>
                                        <div>{new Date(flight.arrivalTime).toLocaleTimeString()}</div>
                                    </div>
                                </td>
                                <td>{flight.planeCode}</td>
                                <td>
                                    <div className="actions">
                                        <button 
                                            className="btn btn-sm btn-secondary"
                                            onClick={() => handleEdit(flight)}
                                        >
                                            Edit
                                        </button>
                                        <button 
                                            className="btn btn-sm btn-danger"
                                            onClick={() => handleDelete(flight.flightId!)}
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {flights.length === 0 && (
                    <div className="no-data">
                        <p>No flights found. Add your first flight to get started.</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default FlightManagement;
