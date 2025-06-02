import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal, Table } from 'react-bootstrap';
import { flightService, airportService, planeService, ticketClassService, flightTicketClassService } from '../../services';
import { Flight, Airport, Plane, TicketClass } from '../../models';
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

const FlightManagement: React.FC = () => {    const { canViewAdmin } = usePermissions();
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
            setSelectedFlightForClasses(flight);            const flightClasses = await flightTicketClassService.getFlightTicketClassesByFlightId(flight.flightId!);
            
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
    };    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
                <p className="mt-3">Loading flight data...</p>
            </Container>
        );
    }    return (
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
            )}            <Modal show={showForm} onHide={handleCancel} size="lg">
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
            </Modal>            <Modal show={showTicketClassModal && !!selectedFlightForClasses} onHide={handleCancelTicketClasses} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>Manage Ticket Classes for Flight {selectedFlightForClasses?.flightCode}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Row className="g-3">
                        {ticketClasses.map(ticketClass => {
                            const assignment = ticketClassAssignments.find(
                                a => a.ticketClassId === ticketClass.ticketClassId
                            );
                            
                            return (
                                <Col md={6} key={ticketClass.ticketClassId}>
                                    <Card className="h-100">
                                        <Card.Header className="py-2">
                                            <Badge 
                                                bg="primary" 
                                                style={{ backgroundColor: (ticketClass as any).color || '#0d6efd' }}
                                            >
                                                {ticketClass.ticketClassName}
                                            </Badge>
                                        </Card.Header>
                                        <Card.Body>
                                            <Row className="g-2">
                                                <Col>
                                                    <Form.Group>
                                                        <Form.Label className="small">Seats</Form.Label>
                                                        <Form.Control
                                                            type="number"
                                                            min="0"
                                                            value={assignment?.ticketQuantity || 0}
                                                            onChange={(e) => handleTicketClassChange(
                                                                ticketClass.ticketClassId!,
                                                                'ticketQuantity',
                                                                parseInt(e.target.value) || 0
                                                            )}
                                                            placeholder="Number of seats"
                                                            size="sm"
                                                        />
                                                    </Form.Group>
                                                </Col>
                                                <Col>
                                                    <Form.Group>
                                                        <Form.Label className="small">Price (VND)</Form.Label>
                                                        <Form.Control
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
                                                            size="sm"
                                                        />
                                                    </Form.Group>
                                                </Col>
                                            </Row>
                                        </Card.Body>
                                    </Card>
                                </Col>
                            );
                        })}
                    </Row>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCancelTicketClasses}>
                        Cancel
                    </Button>
                    <Button variant="primary" onClick={handleSaveTicketClasses}>
                        Save Ticket Classes
                    </Button>
                </Modal.Footer>
            </Modal>            <Row>
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

export default FlightManagement;

