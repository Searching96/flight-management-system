import React, { useState, useEffect } from 'react';
import { flightService, ticketClassService, flightTicketClassService } from '../../services';
import { Flight, TicketClass, FlightTicketClass } from '../../models';
import './FlightTicketClassManagement.css';
import TypeAhead from '../common/TypeAhead';
import { usePermissions } from '../../hooks/useAuth';

const FlightTicketClassManagement: React.FC = () => {
    const { canViewAdmin } = usePermissions();
    if (!canViewAdmin) {
        return (
            <div className="unauthorized">
                <h2>Access Denied</h2>
                <p>You do not have permission to access flight ticket class management.</p>
            </div>
        );
    }

    const [flights, setFlights] = useState<Flight[]>([]);
    const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
    const [flightTicketClasses, setFlightTicketClasses] = useState<FlightTicketClass[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedFlight, setSelectedFlight] = useState<number | ''>('');
    const [showCreateForm, setShowCreateForm] = useState(false);
    const [editingAssociation, setEditingAssociation] = useState<FlightTicketClass | null>(null);
    const [selectedFlightForTypeAhead, setSelectedFlightForTypeAhead] = useState<number | ''>('');

    useEffect(() => {
        loadInitialData();
    }, []);

    useEffect(() => {
        if (selectedFlight) {
            loadFlightTicketClasses();
        } else {
            setFlightTicketClasses([]);
        }
    }, [selectedFlight]);

    const loadInitialData = async () => {
        try {
            setLoading(true);
            const [flightData, ticketClassData] = await Promise.all([
                flightService.getAllFlights(),
                ticketClassService.getAllTicketClasses()
            ]);

            setFlights(flightData);
            setTicketClasses(ticketClassData);
        } catch (err: any) {
            setError('Failed to load data');
        } finally {
            setLoading(false);
        }
    };

    const loadFlightTicketClasses = async () => {
        if (!selectedFlight) return;

        try {
            const data = await flightTicketClassService.getFlightTicketClassesByFlightId(Number(selectedFlight));
            setFlightTicketClasses(data);
        } catch (err: any) {
            setError('Failed to load flight ticket classes');
        }
    };

    const handleCreateAssociation = async (data: {
        ticketClassId: number;
        ticketQuantity: number;
        specifiedFare: number;
    }) => {
        if (!selectedFlight) return;

        try {
            const flightTicketClass = {
                flightId: Number(selectedFlight),
                ticketClassId: data.ticketClassId,
                ticketQuantity: data.ticketQuantity,
                remainingTicketQuantity: data.ticketQuantity,
                specifiedFare: data.specifiedFare
            };

            await flightTicketClassService.createFlightTicketClass(flightTicketClass);
            loadFlightTicketClasses();
            setShowCreateForm(false);
            setError('');
        } catch (err: any) {
            setError('Failed to create association');
        }
    };

    const handleUpdateAssociation = async (
        flightId: number,
        ticketClassId: number,
        data: Partial<FlightTicketClass>
    ) => {
        try {
            await flightTicketClassService.updateFlightTicketClass(flightId, ticketClassId, data);
            loadFlightTicketClasses();
            setEditingAssociation(null);
            setError('');
        } catch (err: any) {
            setError('Failed to update association');
        }
    };

    const handleDeleteAssociation = async (flightId: number, ticketClassId: number) => {
        if (!window.confirm('Are you sure you want to delete this ticket class association?')) return;

        try {
            await flightTicketClassService.deleteFlightTicketClass(flightId, ticketClassId);
            loadFlightTicketClasses();
        } catch (err: any) {
            setError('Failed to delete association');
        }
    };

    const handleUpdateRemainingSeats = async (
        flightId: number,
        ticketClassId: number,
        quantity: number
    ) => {
        try {
            await flightTicketClassService.updateRemainingTickets(flightId, ticketClassId, quantity);
            loadFlightTicketClasses();
        } catch (err: any) {
            setError('Failed to update remaining seats');
        }
    };

    const getTicketClassName = (ticketClassId: number) => {
        return ticketClasses.find(tc => tc.ticketClassId === ticketClassId)?.ticketClassName || 'Unknown';
    };

    const getTicketClassColor = (ticketClassId: number) => {
        return ticketClasses.find(tc => tc.ticketClassId === ticketClassId)?.color || '#ccc';
    };

    const getFlightInfo = (flightId: number) => {
        return flights.find(f => f.flightId === flightId);
    };

    const availableTicketClasses = ticketClasses.filter(
        tc => !flightTicketClasses.some(ftc => ftc.ticketClassId === tc.ticketClassId)
    );

    // Transform flights for TypeAhead
    const flightOptions = flights.map(flight => ({
        value: flight.flightId!,
        label: `${flight.flightCode} - ${flight.departureCityName} → ${flight.arrivalCityName}`,
        code: flight.flightCode,
        route: `${flight.departureCityName} → ${flight.arrivalCityName}`
    }));

    // Transform ticket classes for TypeAhead
    const ticketClassOptions = ticketClasses.map(tc => ({
        value: tc.ticketClassId!,
        label: tc.ticketClassName,
        color: tc.color
    }));

    if (loading) {
        return <div className="loading">Loading flight ticket class data...</div>;
    }

    return (
        <div className="flight-ticket-class-management">
            <div className="management-header">
                <h2>Flight Class Assignment</h2>
                <div className="flight-selector">
                    <label>Select Flight:</label>
                    <TypeAhead
                        options={flightOptions}
                        value={selectedFlight}
                        onChange={(option) => {
                            const flightId = option?.value as number || '';
                            setSelectedFlight(flightId);
                        }}
                        placeholder="Search flights..."
                        className="flight-typeahead"
                    />
                </div>
            </div>

            {error && <div className="error-message">{error}</div>}

            {selectedFlight && (
                <div className="ticket-class-section">
                    <div className="section-header">
                        <h3>Ticket Classes for {getFlightInfo(Number(selectedFlight))?.flightCode}</h3>
                        {availableTicketClasses.length > 0 && (
                            <button
                                className="btn btn-primary"
                                onClick={() => setShowCreateForm(true)}
                            >
                                Add Ticket Class
                            </button>
                        )}
                    </div>

                    {showCreateForm && (
                        <CreateAssociationFormWithTypeAhead
                            availableClasses={availableTicketClasses}
                            onSubmit={handleCreateAssociation}
                            onCancel={() => setShowCreateForm(false)}
                        />
                    )}

                    <div className="associations-grid">
                        {flightTicketClasses.map(association => (
                            <TicketClassCard
                                key={`${association.flightId}-${association.ticketClassId}`}
                                association={association}
                                className={getTicketClassName(association.ticketClassId!)}
                                classColor={getTicketClassColor(association.ticketClassId!)}
                                isEditing={editingAssociation?.ticketClassId === association.ticketClassId}
                                onEdit={() => setEditingAssociation(association)}
                                onSave={(data) => handleUpdateAssociation(
                                    association.flightId!,
                                    association.ticketClassId!,
                                    data
                                )}
                                onCancel={() => setEditingAssociation(null)}
                                onDelete={() => handleDeleteAssociation(
                                    association.flightId!,
                                    association.ticketClassId!
                                )}
                                onUpdateRemaining={(quantity) => handleUpdateRemainingSeats(
                                    association.flightId!,
                                    association.ticketClassId!,
                                    quantity
                                )}
                            />
                        ))}
                    </div>

                    {flightTicketClasses.length === 0 && (
                        <div className="no-data">
                            <p>No ticket classes assigned to this flight.</p>
                            <p>Add ticket classes to enable booking for this flight.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

// Updated Create Association Form Component with TypeAhead
interface CreateAssociationFormProps {
    availableClasses: TicketClass[];
    onSubmit: (data: { ticketClassId: number; ticketQuantity: number; specifiedFare: number }) => void;
    onCancel: () => void;
}

const CreateAssociationFormWithTypeAhead: React.FC<CreateAssociationFormProps> = ({
    availableClasses,
    onSubmit,
    onCancel
}) => {
    const [selectedTicketClass, setSelectedTicketClass] = useState<number | ''>('');
    const [formData, setFormData] = useState({
        ticketQuantity: '',
        specifiedFare: ''
    });
    const [formErrors, setFormErrors] = useState<string>('');

    const ticketClassOptions = availableClasses.map(tc => ({
        value: tc.ticketClassId!,
        label: tc.ticketClassName,
        color: tc.color
    }));

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!selectedTicketClass || !formData.ticketQuantity || !formData.specifiedFare) {
            setFormErrors('Please fill in all fields');
            return;
        }

        if (Number(formData.ticketQuantity) <= 0) {
            setFormErrors('Number of seats must be greater than 0');
            return;
        }

        if (Number(formData.specifiedFare) <= 0) {
            setFormErrors('Price must be greater than 0');
            return;
        }

        setFormErrors('');
        onSubmit({
            ticketClassId: Number(selectedTicketClass),
            ticketQuantity: Number(formData.ticketQuantity),
            specifiedFare: Number(formData.specifiedFare)
        });

        setSelectedTicketClass('');
        setFormData({ ticketQuantity: '', specifiedFare: '' });
    };

    return (
        <div className="form-section">
            <h4>Add Ticket Class</h4>
            {formErrors && <div className="error-message">{formErrors}</div>}
            <form onSubmit={handleSubmit}>
                <div className="form-row">
                    <div className="form-group">
                        <label>Ticket Class</label>
                        <TypeAhead
                            options={ticketClassOptions}
                            value={selectedTicketClass}
                            onChange={(option) => {
                                setSelectedTicketClass(option?.value as number || '');
                            }}
                            placeholder="Search ticket class..."
                        />
                    </div>

                    <div className="form-group">
                        <label>Number of Seats</label>
                        <input
                            type="number"
                            min="1"
                            value={formData.ticketQuantity}
                            onChange={(e) => setFormData(prev => ({ ...prev, ticketQuantity: e.target.value }))
                            }
                            required
                            placeholder="e.g., 100"
                        />
                    </div>

                    <div className="form-group">
                        <label>Price per Ticket (VND)</label>
                        <input
                            type="number"
                            min="1"
                            step="1000"
                            value={formData.specifiedFare}
                            onChange={(e) => setFormData(prev => ({ ...prev, specifiedFare: e.target.value }))
                            }
                            required
                            placeholder="e.g., 1500000"
                        />
                    </div>
                </div>

                <div className="form-actions">
                    <button type="button" className="btn btn-secondary" onClick={onCancel}>
                        Cancel
                    </button>
                    <button type="submit" className="btn btn-primary">
                        Add Class
                    </button>
                </div>
            </form>
        </div>

    );
};

// Ticket Class Card Component
interface TicketClassCardProps {
    association: FlightTicketClass;
    className: string;
    classColor: string;
    isEditing: boolean;
    onEdit: () => void;
    onSave: (data: Partial<FlightTicketClass>) => void;
    onCancel: () => void;
    onDelete: () => void;
    onUpdateRemaining: (quantity: number) => void;
}

const TicketClassCard: React.FC<TicketClassCardProps> = ({
    association,
    className,
    classColor,
    isEditing,
    onEdit,
    onSave,
    onCancel,
    onDelete,
    onUpdateRemaining
}) => {
    const [editData, setEditData] = useState({
        ticketQuantity: association.ticketQuantity || 0,
        specifiedFare: association.specifiedFare || 0,
        remainingTicketQuantity: association.remainingTicketQuantity || 0
    });

    const handleSave = () => {
        onSave(editData);
    };

    const handleUpdateRemaining = () => {
        onUpdateRemaining(editData.remainingTicketQuantity);
    };

    const soldSeats = (association.ticketQuantity || 0) - (association.remainingTicketQuantity || 0);
    const occupancyRate = association.ticketQuantity ?
        ((soldSeats / association.ticketQuantity) * 100).toFixed(1) : '0';

    return (
        <div className="ticket-class-card" style={{ borderLeft: `4px solid ${classColor}` }}>
            <div className="card-header">
                <h4 style={{ color: classColor }}>{className}</h4>
                <div className="card-actions">
                    {isEditing ? (
                        <>
                            <button className="btn btn-sm btn-success" onClick={handleSave}>Save</button>
                            <button className="btn btn-sm btn-secondary" onClick={onCancel}>Cancel</button>
                        </>
                    ) : (
                        <>
                            <button className="btn btn-sm btn-primary" onClick={onEdit}>Edit</button>
                            <button className="btn btn-sm btn-danger" onClick={onDelete}>Delete</button>
                        </>
                    )}
                </div>
            </div>

            <div className="card-content">
                {isEditing ? (
                    <div className="edit-form">
                        <div className="form-group">
                            <label>Total Seats</label>
                            <input
                                type="number"
                                value={editData.ticketQuantity}
                                onChange={(e) => setEditData(prev => ({ ...prev, ticketQuantity: parseInt(e.target.value) || 0 }))
                                }
                                min="0"
                            />
                        </div>
                        <div className="form-group">
                            <label>Price per Ticket (VND)</label>
                            <input
                                type="number"
                                value={editData.specifiedFare}
                                onChange={(e) => setEditData(prev => ({ ...prev, specifiedFare: parseInt(e.target.value) || 0 }))
                                }
                                min="0"
                            />
                        </div>
                        <div className="remaining-seats-control">
                            <input
                                type="number"
                                value={editData.remainingTicketQuantity}
                                onChange={(e) => setEditData(prev => ({ ...prev, remainingTicketQuantity: parseInt(e.target.value) || 0 }))
                                }
                                min="0"
                                max={editData.ticketQuantity}
                            />
                            <button 
                                className="btn btn-sm btn-warning" 
                                onClick={handleUpdateRemaining}
                                title="Update remaining seats only"
                            >
                                Update Remaining
                            </button>
                        </div>
                    </div>
                ) : (
                    <div className="card-details">
                        <div className="detail-row">
                            <span className="label">Total Seats:</span>
                            <span className="value">{association.ticketQuantity}</span>
                        </div>
                        <div className="detail-row">
                            <span className="label">Remaining:</span>
                            <span className="value">{association.remainingTicketQuantity}</span>
                        </div>
                        <div className="detail-row">
                            <span className="label">Price:</span>
                            <span className="value">{association.specifiedFare?.toLocaleString()} VND</span>
                        </div>
                        <div className="detail-row">
                            <span className="label">Sold:</span>
                            <span className="value">{soldSeats}</span>
                        </div>

                        <div className="occupancy-section">
                            <div className="occupancy-label">Occupancy: {occupancyRate}%</div>
                            <div className="progress-bar">
                                <div 
                                    className="progress-fill" 
                                    style={{ width: `${occupancyRate}%` }}
                                ></div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default FlightTicketClassManagement;
