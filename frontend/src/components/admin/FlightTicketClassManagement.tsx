import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge } from 'react-bootstrap';
import { flightService, ticketClassService, flightTicketClassService } from '../../services';
import { Flight, TicketClass, FlightTicketClass } from '../../models';
import TypeAhead from '../common/TypeAhead';
import { usePermissions } from '../../hooks/useAuth';

const FlightTicketClassManagement: React.FC = () => {
    const { canViewAdmin } = usePermissions();
    if (!canViewAdmin) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8}>
                        <Alert variant="danger" className="text-center">
                            <Alert.Heading>Access Denied</Alert.Heading>
                            <p>You do not have permission to access flight ticket class management.</p>
                        </Alert>
                    </Col>
                </Row>
            </Container>
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

    if (loading) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8} className="text-center">
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                        <p className="mt-3">Loading flight ticket class data...</p>
                    </Col>
                </Row>
            </Container>
        );
    }

    return (
        <Container fluid className="py-4">
            <Card className="mb-4">
                <Card.Header>
                    <Row className="align-items-center">
                        <Col>
                            <Card.Title as="h2" className="mb-0">Flight Class Assignment</Card.Title>
                        </Col>
                        <Col md="auto">
                            <Form.Group className="d-flex align-items-center gap-3 mb-0">
                                <Form.Label className="mb-0 fw-medium">Select Flight:</Form.Label>
                                <div style={{ minWidth: '300px' }}>
                                    <TypeAhead
                                        options={flightOptions}
                                        value={selectedFlight}
                                        onChange={(option) => {
                                            const flightId = option?.value as number || '';
                                            setSelectedFlight(flightId);
                                        }}
                                        placeholder="Search flights..."
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                </Card.Header>
            </Card>

            {error && (
                <Alert variant="danger" className="mb-4">
                    {error}
                </Alert>
            )}

            {selectedFlight && (
                <Card>
                    <Card.Header>
                        <Row className="align-items-center">
                            <Col>
                                <Card.Title as="h3" className="mb-0">
                                    Ticket Classes for {getFlightInfo(Number(selectedFlight))?.flightCode}
                                </Card.Title>
                            </Col>
                            <Col md="auto">
                                {availableTicketClasses.length > 0 && (
                                    <Button
                                        variant="primary"
                                        onClick={() => setShowCreateForm(true)}
                                    >
                                        Add Ticket Class
                                    </Button>
                                )}
                            </Col>
                        </Row>
                    </Card.Header>

                    <Card.Body>
                        {showCreateForm && (
                            <CreateAssociationFormWithTypeAhead
                                availableClasses={availableTicketClasses}
                                onSubmit={handleCreateAssociation}
                                onCancel={() => setShowCreateForm(false)}
                            />
                        )}

                        <Row className="g-3">
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
                                        onUpdateRemaining={(quantity) => handleUpdateRemainingSeats(
                                            association.flightId!,
                                            association.ticketClassId!,
                                            quantity
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
                    </Card.Body>
                </Card>
            )}
        </Container>
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
                                    onChange={(e) => setFormData(prev => ({ ...prev, ticketQuantity: e.target.value }))}
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
                                    onChange={(e) => setFormData(prev => ({ ...prev, specifiedFare: e.target.value }))}
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
                                    onChange={(e) => setEditData(prev => ({ ...prev, ticketQuantity: parseInt(e.target.value) || 0 }))}
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
                                    onChange={(e) => setEditData(prev => ({ ...prev, specifiedFare: parseInt(e.target.value) || 0 }))}
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
                                        onChange={(e) => setEditData(prev => ({ ...prev, remainingTicketQuantity: parseInt(e.target.value) || 0 }))}
                                        min="0"
                                        max={editData.ticketQuantity}
                                    />
                                    <Button 
                                        size="sm"
                                        variant="warning" 
                                        onClick={handleUpdateRemaining}
                                        title="Update remaining seats only"
                                    >
                                        Update Remaining
                                    </Button>
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

export default FlightTicketClassManagement;
