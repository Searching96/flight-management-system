import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal, Table } from 'react-bootstrap';
import { flightService, airportService, planeService, ticketClassService, flightTicketClassService } from '../../services';
import { 
  Flight, 
  Airport, 
  Plane, 
  TicketClass, 
  FlightRequest, 
  FlightTicketClass,
  FlightTicketClassRequest,
  UpdateFlightTicketClassRequest,
} from '../../models';
import TypeAhead from '../common/TypeAhead';
import { usePermissions } from '../../hooks/useAuth';

const FlightManagement: React.FC = () => {
    const { canViewAdmin } = usePermissions();
    if (!canViewAdmin) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8}>
                        <Alert variant="danger" className="text-center">
                            <Alert.Heading>Access Denied</Alert.Heading>
                            <p>You do not have permission to access flight management.</p>
                        </Alert>
                    </Col>
                </Row>
            </Container>
        );
    }

    // Main state for flight management
    const [flights, setFlights] = useState<Flight[]>([]);
    const [airports, setAirports] = useState<Airport[]>([]);
    const [planes, setPlanes] = useState<Plane[]>([]);
    const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [editingFlight, setEditingFlight] = useState<Flight | null>(null);

    // State for ticket class management
    const [selectedFlightForClasses, setSelectedFlightForClasses] = useState<Flight | null>(null);
    const [showTicketClassModal, setShowTicketClassModal] = useState(false);
    const [flightTicketClasses, setFlightTicketClasses] = useState<FlightTicketClass[]>([]);
    const [showCreateForm, setShowCreateForm] = useState(false);
    const [editingAssociation, setEditingAssociation] = useState<FlightTicketClass | null>(null);

    const [selectedDepartureAirport, setSelectedDepartureAirport] = useState<number | ''>('');
    const [selectedArrivalAirport, setSelectedArrivalAirport] = useState<number | ''>('');
    const [selectedPlane, setSelectedPlane] = useState<number | ''>('');

    const {
        register,
        handleSubmit,
        reset,
        setValue,
        formState: { errors }
    } = useForm<FlightRequest>();

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

    // Flight CRUD operations
    const onSubmit = async (data: FlightRequest) => {
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

    // Transform data for TypeAhead
    const airportOptions = airports.map(airport => ({
        value: airport.airportId!,
        label: `${airport.cityName} - ${airport.airportName}`,
        city: airport.cityName,
        name: airport.airportName
    }));

    const planeOptions = planes.map(plane => ({
        value: plane.planeId!,
        label: `${plane.planeCode} - ${plane.planeType}`,
        code: plane.planeCode,
        type: plane.planeType
    }));

    // TICKET CLASS MANAGEMENT FUNCTIONS (from FlightTicketClassManagement)
    const loadFlightTicketClasses = async (flightId: number) => {
        try {
            const data = await flightTicketClassService.getFlightTicketClassesByFlightId(flightId);
            setFlightTicketClasses(data);
        } catch (err: any) {
            setError('Failed to load flight ticket classes');
        }
    };

    const handleManageTicketClasses = async (flight: Flight) => {
        try {
            setSelectedFlightForClasses(flight);
            await loadFlightTicketClasses(flight.flightId!);
            setShowTicketClassModal(true);
        } catch (err: any) {
            setError('Failed to load ticket class data');
        }
    };

    const handleCreateAssociation = async (data: {
        ticketClassId: number;
        ticketQuantity: number;
        specifiedFare: number;
    }) => {
        if (!selectedFlightForClasses) return;

        try {
            const flightTicketClassRequest: FlightTicketClassRequest = {
                flightId: selectedFlightForClasses.flightId!,
                ticketClassId: data.ticketClassId,
                ticketQuantity: data.ticketQuantity,
                remainingTicketQuantity: data.ticketQuantity,
                specifiedFare: data.specifiedFare
            };

            await flightTicketClassService.createFlightTicketClass(flightTicketClassRequest);
            await loadFlightTicketClasses(selectedFlightForClasses.flightId!);
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
            const updateRequest: UpdateFlightTicketClassRequest = {
                ticketQuantity: data.ticketQuantity!,
                specifiedFare: data.specifiedFare!,
                remainingTicketQuantity: data.remainingTicketQuantity!
            };
            
            await flightTicketClassService.updateFlightTicketClass(flightId, ticketClassId, updateRequest);
            await loadFlightTicketClasses(flightId);
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
            await loadFlightTicketClasses(flightId);
        } catch (err: any) {
            setError('Failed to delete association');
        }
    };

    const getTicketClassName = (ticketClassId: number) => {
        return ticketClasses.find(tc => tc.ticketClassId === ticketClassId)?.ticketClassName || 'Unknown';
    };

    const getTicketClassColor = (ticketClassId: number) => {
        return ticketClasses.find(tc => tc.ticketClassId === ticketClassId)?.color || '#ccc';
    };

    const handleCancelTicketClasses = () => {
        setShowTicketClassModal(false);
        setSelectedFlightForClasses(null);
        setFlightTicketClasses([]);
        setShowCreateForm(false);
        setEditingAssociation(null);
        setError('');
    };

    const availableTicketClasses = selectedFlightForClasses 
        ? ticketClasses.filter(
            tc => !flightTicketClasses.some(ftc => ftc.ticketClassId === tc.ticketClassId)
        )
        : [];

    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
                <p className="mt-3">Loading flight data...</p>
            </Container>
        );
    } 
    
    return (
        <Container fluid className="py-4">
            <Row className="mb-4">
                <Col>
                    <Card>
                        <Card.Header className="d-flex justify-content-between align-items-center">
                            <Card.Title className="mb-0">✈️ Flight Management</Card.Title>
                            <Button
                                variant="primary"
                                onClick={() => setShowForm(true)}
                            >
                                Add New Flight
                            </Button>
                        </Card.Header>
                    </Card>
                </Col>
            </Row>

            {error && (
                <Row className="mb-4">
                    <Col>
                        <Alert variant="danger" className="text-center">
                            {error}
                        </Alert>
                    </Col>
                </Row>
            )}
            
            {/* Flight Creation/Edit Modal */}
            <Modal show={showForm} onHide={handleCancel} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>{editingFlight ? 'Edit Flight' : 'Add New Flight'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={handleSubmit(onSubmit)}>
                        <Row className="mb-3">
                            <Col>
                                <Form.Group>
                                    <Form.Label>Flight Code</Form.Label>
                                    <Form.Control
                                        type="text"
                                        {...register('flightCode', {
                                            required: 'Flight code is required'
                                        })}
                                        isInvalid={!!errors.flightCode}
                                        placeholder="e.g., FL001"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.flightCode?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>

                            <Col>
                                <Form.Group>
                                    <Form.Label>Departure Airport</Form.Label>
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
                                        <div className="text-danger small mt-1">{errors.departureAirportId.message}</div>
                                    )}
                                </Form.Group>
                            </Col>

                            <Col>
                                <Form.Group>
                                    <Form.Label>Arrival Airport</Form.Label>
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
                                        <div className="text-danger small mt-1">{errors.arrivalAirportId.message}</div>
                                    )}
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row className="mb-3">
                            <Col>
                                <Form.Group>
                                    <Form.Label>Departure Time</Form.Label>
                                    <Form.Control
                                        type="datetime-local"
                                        {...register('departureTime', {
                                            required: 'Departure time is required'
                                        })}
                                        isInvalid={!!errors.departureTime}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.departureTime?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>

                            <Col>
                                <Form.Group>
                                    <Form.Label>Arrival Time</Form.Label>
                                    <Form.Control
                                        type="datetime-local"
                                        {...register('arrivalTime', {
                                            required: 'Arrival time is required'
                                        })}
                                        isInvalid={!!errors.arrivalTime}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.arrivalTime?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row className="mb-3">
                            <Col>
                                <Form.Group>
                                    <Form.Label>Aircraft</Form.Label>
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
                                        <div className="text-danger small mt-1">{errors.planeId.message}</div>
                                    )}
                                </Form.Group>
                            </Col>
                        </Row>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCancel}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={handleSubmit(onSubmit)}>
                        {editingFlight ? 'Update Flight' : 'Create Flight'}
                    </Button>
                </Modal.Footer>
            </Modal>
            
            {/* Ticket Class Management Modal - IMPROVED VERSION */}
            <Modal 
                show={showTicketClassModal && !!selectedFlightForClasses} 
                onHide={handleCancelTicketClasses} 
                size="xl"
                fullscreen="lg-down"
            >
                <Modal.Header closeButton>
                    <Modal.Title>
                        Ticket Classes for Flight {selectedFlightForClasses?.flightCode}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Row className="align-items-center mb-3">
                        <Col>
                            <h5 className="mb-0">
                                Route: {selectedFlightForClasses?.departureCityName} → {selectedFlightForClasses?.arrivalCityName}
                            </h5>
                        </Col>
                        <Col md="auto">
                            {availableTicketClasses.length > 0 && (
                                <Button
                                    variant="primary"
                                    onClick={() => setShowCreateForm(true)}
                                >
                                    <i className="bi bi-plus-circle me-2"></i>
                                    Add Ticket Class
                                </Button>
                            )}
                        </Col>
                    </Row>

                    {showCreateForm && (
                        <CreateAssociationFormWithTypeAhead
                            availableClasses={availableTicketClasses}
                            onSubmit={handleCreateAssociation}
                            onCancel={() => setShowCreateForm(false)}
                        />
                    )}

                    <Row className="g-3 mt-2">
                        {flightTicketClasses.map(association => (
                            <Col lg={6} xl={4} key={`${association.flightId}-${association.ticketClassId}`}>
                                <TicketClassCard
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
                                />
                            </Col>
                        ))}
                    </Row>

                    {flightTicketClasses.length === 0 && (
                        <Alert variant="info" className="text-center">
                            <Alert.Heading>No Ticket Classes Assigned</Alert.Heading>
                            <p className="mb-0">Add ticket classes to enable booking for this flight.</p>
                        </Alert>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCancelTicketClasses}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>
            
            {/* Flights table */}
            <Row>
                <Col>
                    <Card>
                        <Card.Header>
                            <Card.Title className="mb-0">All Flights</Card.Title>
                        </Card.Header>
                        <Card.Body className="p-0">
                            {flights.length === 0 ? (
                                <div className="text-center py-5">
                                    <p className="text-muted mb-0">No flights found. Add your first flight to get started.</p>
                                </div>
                            ) : (
                                <Table responsive striped hover>
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
                                                <td>
                                                    <Badge bg="primary">{flight.flightCode}</Badge>
                                                </td>
                                                <td>{flight.departureCityName} → {flight.arrivalCityName}</td>
                                                <td>{new Date(flight.departureTime).toLocaleString()}</td>
                                                <td>{new Date(flight.arrivalTime).toLocaleString()}</td>
                                                <td>{flight.planeCode}</td>
                                                <td>
                                                    <Button
                                                        size="sm"
                                                        variant="outline-secondary"
                                                        className="me-2"
                                                        onClick={() => handleEdit(flight)}
                                                    >
                                                        Edit
                                                    </Button>
                                                    <Button
                                                        size="sm"
                                                        variant="outline-primary"
                                                        className="me-2"
                                                        onClick={() => handleManageTicketClasses(flight)}
                                                    >
                                                        Manage Classes
                                                    </Button>
                                                    <Button
                                                        size="sm"
                                                        variant="outline-danger"
                                                        onClick={() => handleDelete(flight.flightId!)}
                                                    >
                                                        Delete
                                                    </Button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

// Create Association Form Component with TypeAhead (from FlightTicketClassManagement)
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
        <Card className="mb-4">
            <Card.Header>
                <Card.Title as="h4" className="mb-0">Add Ticket Class</Card.Title>
            </Card.Header>
            <Card.Body>
                {formErrors && (
                    <Alert variant="danger" className="mb-3">
                        {formErrors}
                    </Alert>
                )}
                
                <Form onSubmit={handleSubmit}>
                    <Row className="g-3">
                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>Ticket Class</Form.Label>
                                <TypeAhead
                                    options={ticketClassOptions}
                                    value={selectedTicketClass}
                                    onChange={(option) => {
                                        setSelectedTicketClass(option?.value as number || '');
                                    }}
                                    placeholder="Search ticket class..."
                                />
                            </Form.Group>
                        </Col>

                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>Number of Seats</Form.Label>
                                <Form.Control
                                    type="number"
                                    min="1"
                                    value={formData.ticketQuantity}
                                    onChange={(e) => setFormData(prev => ({ ...prev, ticketQuantity: e.target.value }))
                                    }
                                    required
                                    placeholder="e.g., 100"
                                />
                            </Form.Group>
                        </Col>

                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>Price per Ticket (VND)</Form.Label>
                                <Form.Control
                                    type="number"
                                    min="1"
                                    step="1000"
                                    value={formData.specifiedFare}
                                    onChange={(e) => setFormData(prev => ({ ...prev, specifiedFare: e.target.value }))
                                    }
                                    required
                                    placeholder="e.g., 1500000"
                                />
                            </Form.Group>
                        </Col>
                    </Row>

                    <div className="d-flex gap-2 mt-3">
                        <Button type="button" variant="secondary" onClick={onCancel}>
                            Cancel
                        </Button>
                        <Button type="submit" variant="primary">
                            Add Class
                        </Button>
                    </div>
                </Form>
            </Card.Body>
        </Card>
    );
};

// Ticket Class Card Component (from FlightTicketClassManagement)
interface TicketClassCardProps {
    association: FlightTicketClass;
    className: string;
    classColor: string;
    isEditing: boolean;
    onEdit: () => void;
    onSave: (data: Partial<FlightTicketClass>) => void;
    onCancel: () => void;
    onDelete: () => void;
}

const TicketClassCard: React.FC<TicketClassCardProps> = ({
    association,
    className,
    classColor,
    isEditing,
    onEdit,
    onSave,
    onCancel,
    onDelete
}) => {
    const [editData, setEditData] = useState({
        ticketQuantity: association.ticketQuantity || 0,
        specifiedFare: association.specifiedFare || 0,
        remainingTicketQuantity: association.remainingTicketQuantity || 0
    });

    const handleSave = () => {
        onSave(editData);
    };

    const soldSeats = (association.ticketQuantity || 0) - (association.remainingTicketQuantity || 0);
    const occupancyRate = association.ticketQuantity ?
        ((soldSeats / association.ticketQuantity) * 100).toFixed(1) : '0';

    return (
        <Card className="h-100" style={{ borderLeft: `4px solid ${classColor}` }}>
            <Card.Header className="d-flex justify-content-between align-items-center">
                <Card.Title as="h5" className="mb-0" style={{ color: classColor }}>
                    {className}
                </Card.Title>
                <div className="d-flex gap-1">
                    {isEditing ? (
                        <>
                            <Button size="sm" variant="success" onClick={handleSave}>
                                Save
                            </Button>
                            <Button size="sm" variant="secondary" onClick={onCancel}>
                                Cancel
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button size="sm" variant="outline-primary" onClick={onEdit}>
                                Edit
                            </Button>
                            <Button size="sm" variant="outline-danger" onClick={onDelete}>
                                Delete
                            </Button>
                        </>
                    )}
                </div>
            </Card.Header>

            <Card.Body>
                {isEditing ? (
                    <Row className="g-3">
                        <Col sm={6}>
                            <Form.Group>
                                <Form.Label>Total Seats</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={editData.ticketQuantity}
                                    onChange={(e) => setEditData(prev => ({ ...prev, ticketQuantity: parseInt(e.target.value) || 0 }))
                                    }
                                    min="0"
                                />
                            </Form.Group>
                        </Col>
                        <Col sm={6}>
                            <Form.Group>
                                <Form.Label>Price per Ticket (VND)</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={editData.specifiedFare}
                                    onChange={(e) => setEditData(prev => ({ ...prev, specifiedFare: parseInt(e.target.value) || 0 }))
                                    }
                                    min="0"
                                />
                            </Form.Group>
                        </Col>
                        <Col xs={12}>
                            <Form.Group>
                                <Form.Label>Remaining Seats</Form.Label>
                                <div className="d-flex gap-2">
                                    <Form.Control
                                        type="number"
                                        value={editData.remainingTicketQuantity}
                                        onChange={(e) => setEditData(prev => ({ ...prev, remainingTicketQuantity: parseInt(e.target.value) || 0 }))
                                        }
                                        min="0"
                                        max={editData.ticketQuantity}
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                ) : (
                    <>
                        <Row className="g-2 mb-3">
                            <Col xs={6}>
                                <div className="d-flex justify-content-between">
                                    <span className="text-muted">Total Seats:</span>
                                    <strong>{association.ticketQuantity}</strong>
                                </div>
                            </Col>
                            <Col xs={6}>
                                <div className="d-flex justify-content-between">
                                    <span className="text-muted">Remaining:</span>
                                    <strong>{association.remainingTicketQuantity}</strong>
                                </div>
                            </Col>
                            <Col xs={6}>
                                <div className="d-flex justify-content-between">
                                    <span className="text-muted">Price:</span>
                                    <strong>{association.specifiedFare?.toLocaleString()} VND</strong>
                                </div>
                            </Col>
                            <Col xs={6}>
                                <div className="d-flex justify-content-between">
                                    <span className="text-muted">Sold:</span>
                                    <strong>{soldSeats}</strong>
                                </div>
                            </Col>
                        </Row>

                        <div>
                            <div className="d-flex justify-content-between align-items-center mb-2">
                                <span className="text-muted">Occupancy:</span>
                                <Badge bg="secondary">{occupancyRate}%</Badge>
                            </div>
                            <div className="progress" style={{ height: '8px' }}>
                                <div 
                                    className="progress-bar" 
                                    role="progressbar"
                                    style={{ 
                                        width: `${occupancyRate}%`,
                                        backgroundColor: classColor
                                    }}
                                    aria-valuenow={parseFloat(occupancyRate)}
                                    aria-valuemin={0}
                                    aria-valuemax={100}
                                />
                            </div>
                        </div>
                    </>
                )}
            </Card.Body>
        </Card>
    );
};

export default FlightManagement;

