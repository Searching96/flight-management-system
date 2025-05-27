import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { flightService, airportService, planeService, ticketClassService, flightTicketClassService } from '../../services';
import { Flight, Airport, Plane, TicketClass, FlightTicketClass } from '../../models';
import './FlightManagement.css';
import TypeAhead from '../common/TypeAhead';
import { usePermissions } from '../../hooks/useAuth';

interface FlightFormData {
    flightCode: string;
    departureTime: string;
    arrivalTime: string;
    planeId: number;
    departureAirportId: number;
    arrivalAirportId: number;
}

interface TicketClassAssignment {
    ticketClassId: number;
    ticketQuantity: number;
    specifiedFare: number;
}

const FlightManagement: React.FC = () => {
    const { canViewAdmin } = usePermissions();
    if (!canViewAdmin) {
        return (
            <div className="unauthorized">
                <h2>Access Denied</h2>
                <p>You do not have permission to access flight management.</p>
            </div>
        );
    }

    const [flights, setFlights] = useState<Flight[]>([]);
    const [airports, setAirports] = useState<Airport[]>([]);
    const [planes, setPlanes] = useState<Plane[]>([]);
    const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [editingFlight, setEditingFlight] = useState<Flight | null>(null);
    const [showTicketClassModal, setShowTicketClassModal] = useState(false);
    const [selectedFlightForClasses, setSelectedFlightForClasses] = useState<Flight | null>(null);
    const [flightTicketClasses, setFlightTicketClasses] = useState<FlightTicketClass[]>([]);
    const [ticketClassAssignments, setTicketClassAssignments] = useState<TicketClassAssignment[]>([]);
    const [selectedDepartureAirport, setSelectedDepartureAirport] = useState<number | ''>('');
    const [selectedArrivalAirport, setSelectedArrivalAirport] = useState<number | ''>('');
    const [selectedPlane, setSelectedPlane] = useState<number | ''>('');

    const {
        register,
        handleSubmit,
        reset,
        setValue,
        formState: { errors }
    } = useForm<FlightFormData>();

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            setLoading(true);
            const [flightData, airportData, planeData, ticketClassData] = await Promise.all([
                flightService.getAllFlights(),
                airportService.getAllAirports(),
                planeService.getAllPlanes(),
                ticketClassService.getAllTicketClasses()
            ]);

            setFlights(flightData);
            setAirports(airportData);
            setPlanes(planeData);
            setTicketClasses(ticketClassData);
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

    // Transform airports for TypeAhead
    const airportOptions = airports.map(airport => ({
        value: airport.airportId!,
        label: `${airport.cityName} - ${airport.airportName}`,
        city: airport.cityName,
        name: airport.airportName
    }));

    // Transform planes for TypeAhead
    const planeOptions = planes.map(plane => ({
        value: plane.planeId!,
        label: `${plane.planeCode} - ${plane.planeType}`,
        code: plane.planeCode,
        type: plane.planeType
    }));

    const handleEdit = (flight: Flight) => {
        setEditingFlight(flight);
        setSelectedDepartureAirport(flight.departureAirportId);
        setSelectedArrivalAirport(flight.arrivalAirportId);
        setSelectedPlane(flight.planeId);
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
        setSelectedDepartureAirport('');
        setSelectedArrivalAirport('');
        setSelectedPlane('');
        reset();
        setError('');
    };

    const handleManageTicketClasses = async (flight: Flight) => {
        try {
            setSelectedFlightForClasses(flight);
            const flightClasses = await flightTicketClassService.getFlightTicketClassesByFlightId(flight.flightId!);
            setFlightTicketClasses(flightClasses);
            
            // Initialize assignments for existing classes
            const assignments = ticketClasses.map(tc => {
                const existing = flightClasses.find(ftc => ftc.ticketClassId === tc.ticketClassId);
                return {
                    ticketClassId: tc.ticketClassId!,
                    ticketQuantity: existing?.ticketQuantity || 0,
                    specifiedFare: existing?.specifiedFare ? Number(existing.specifiedFare) : 0
                };
            });
            
            setTicketClassAssignments(assignments);
            setShowTicketClassModal(true);
        } catch (err: any) {
            setError('Failed to load ticket class data');
        }
    };

    const handleTicketClassChange = (classId: number, field: 'ticketQuantity' | 'specifiedFare', value: number) => {
        setTicketClassAssignments(prev => 
            prev.map(assignment => 
                assignment.ticketClassId === classId 
                    ? { ...assignment, [field]: value }
                    : assignment
            )
        );
    };

    const handleSaveTicketClasses = async () => {
        if (!selectedFlightForClasses) return;

        try {
            // Filter out assignments with zero quantity
            const validAssignments = ticketClassAssignments.filter(
                assignment => assignment.ticketQuantity > 0 && assignment.specifiedFare > 0
            );

            // Use the flightService to replace all ticket class assignments
            await flightService.assignTicketClassesToFlight(
                selectedFlightForClasses.flightId!,
                validAssignments
            );

            setShowTicketClassModal(false);
            setSelectedFlightForClasses(null);
            setError('');
        } catch (err: any) {
            setError('Failed to save ticket class assignments');
        }
    };

    const handleCancelTicketClasses = () => {
        setShowTicketClassModal(false);
        setSelectedFlightForClasses(null);
        setTicketClassAssignments([]);
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
                                    <TypeAhead
                                        options={airportOptions}
                                        value={selectedDepartureAirport}
                                        onChange={(option) => {
                                            const airportId = option?.value as number || '';
                                            setSelectedDepartureAirport(airportId);
                                            setValue('departureAirportId', Number(airportId));
                                        }}
                                        placeholder="Search departure airport..."
                                        error={!!errors.departureAirportId}
                                    />
                                    <input
                                        type="hidden"
                                        {...register('departureAirportId', {
                                            required: 'Departure airport is required',
                                            valueAsNumber: true
                                        })}
                                    />
                                    {errors.departureAirportId && (
                                        <span className="field-error">{errors.departureAirportId.message}</span>
                                    )}
                                </div>

                                <div className="form-group">
                                    <label>Arrival Airport</label>
                                    <TypeAhead
                                        options={airportOptions}
                                        value={selectedArrivalAirport}
                                        onChange={(option) => {
                                            const airportId = option?.value as number || '';
                                            setSelectedArrivalAirport(airportId);
                                            setValue('arrivalAirportId', Number(airportId));
                                        }}
                                        placeholder="Search arrival airport..."
                                        error={!!errors.arrivalAirportId}
                                    />
                                    <input
                                        type="hidden"
                                        {...register('arrivalAirportId', {
                                            required: 'Arrival airport is required',
                                            valueAsNumber: true
                                        })}
                                    />
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
                                    <TypeAhead
                                        options={planeOptions}
                                        value={selectedPlane}
                                        onChange={(option) => {
                                            const planeId = option?.value as number || '';
                                            setSelectedPlane(planeId);
                                            setValue('planeId', Number(planeId));
                                        }}
                                        placeholder="Search aircraft..."
                                        error={!!errors.planeId}
                                    />
                                    <input
                                        type="hidden"
                                        {...register('planeId', {
                                            required: 'Aircraft is required',
                                            valueAsNumber: true
                                        })}
                                    />
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

            {showTicketClassModal && selectedFlightForClasses && (
                <div className="form-modal">
                    <div className="form-container ticket-class-modal">
                        <h3>Manage Ticket Classes for Flight {selectedFlightForClasses.flightCode}</h3>
                        
                        <div className="ticket-class-assignments">
                            {ticketClasses.map(ticketClass => {
                                const assignment = ticketClassAssignments.find(
                                    a => a.ticketClassId === ticketClass.ticketClassId
                                );
                                
                                return (
                                    <div key={ticketClass.ticketClassId} className="ticket-class-row">
                                        <div className="class-info">
                                            <span 
                                                className="class-badge" 
                                                style={{ backgroundColor: (ticketClass as any).color || '#ccc' }}
                                            >
                                                {ticketClass.ticketClassName}
                                            </span>
                                        </div>
                                        
                                        <div className="class-inputs">
                                            <div className="input-group">
                                                <label>Seats</label>
                                                <input
                                                    type="number"
                                                    min="0"
                                                    value={assignment?.ticketQuantity || 0}
                                                    onChange={(e) => handleTicketClassChange(
                                                        ticketClass.ticketClassId!,
                                                        'ticketQuantity',
                                                        parseInt(e.target.value) || 0
                                                    )}
                                                    placeholder="Number of seats"
                                                />
                                            </div>
                                            
                                            <div className="input-group">
                                                <label>Price (VND)</label>
                                                <input
                                                    type="number"
                                                    min="0"
                                                    step="1000"
                                                    value={assignment?.specifiedFare || 0}
                                                    onChange={(e) => handleTicketClassChange(
                                                        ticketClass.ticketClassId!,
                                                        'specifiedFare',
                                                        parseFloat(e.target.value) || 0
                                                    )}
                                                    placeholder="Price per ticket"
                                                />
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        <div className="form-actions">
                            <button 
                                type="button" 
                                className="btn btn-secondary" 
                                onClick={handleCancelTicketClasses}
                            >
                                Cancel
                            </button>
                            <button 
                                type="button" 
                                className="btn btn-primary" 
                                onClick={handleSaveTicketClasses}
                            >
                                Save Ticket Classes
                            </button>
                        </div>
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
                                <td>{flight.flightCode}</td>
                                <td>{flight.departureCityName} → {flight.arrivalCityName}</td>
                                <td>{new Date(flight.departureTime).toLocaleString()}</td>
                                <td>{new Date(flight.arrivalTime).toLocaleString()}</td>
                                <td>{flight.planeCode}</td>
                                <td>
                                    <button 
                                        className="btn btn-sm btn-secondary"
                                        onClick={() => handleEdit(flight)}
                                    >
                                        Edit
                                    </button>
                                    <button 
                                        className="btn btn-sm btn-primary"
                                        onClick={() => handleManageTicketClasses(flight)}
                                    >
                                        Manage Classes
                                    </button>
                                    <button 
                                        className="btn btn-sm btn-danger"
                                        onClick={() => handleDelete(flight.flightId!)}
                                    >
                                        Delete
                                    </button>
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

