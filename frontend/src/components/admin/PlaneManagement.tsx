import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal } from 'react-bootstrap';
import { planeService } from '../../services';
import { Plane } from '../../models';
import { usePermissions } from '../../hooks/useAuth';

interface PlaneFormData {
    planeCode: string;
    planeType: string;
    seatQuantity: number;
}

const PlaneManagement: React.FC<{
    showAddModal?: boolean;
    onCloseAddModal?: () => void;
}> = ({ showAddModal = false, onCloseAddModal }) => {
    const { canViewAdmin } = usePermissions();
    if (!canViewAdmin) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8}>
                        <Alert variant="danger" className="text-center">
                            <Alert.Heading>Access Denied</Alert.Heading>
                            <p>You do not have permission to access aircraft management.</p>
                        </Alert>
                    </Col>
                </Row>
            </Container>
        );
    }

    const [planes, setPlanes] = useState<Plane[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [editingPlane, setEditingPlane] = useState<Plane | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterType, setFilterType] = useState('');

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors }
    } = useForm<PlaneFormData>();

    // Aircraft type options for TypeAhead
    const aircraftTypeOptions = [
        { value: "Boeing 737-800", label: "Boeing 737-800" },
        { value: "Boeing 737-900", label: "Boeing 737-900" },
        { value: "Boeing 787-8", label: "Boeing 787-8" },
        { value: "Boeing 787-9", label: "Boeing 787-9" },
        { value: "Boeing 777-200", label: "Boeing 777-200" },
        { value: "Boeing 777-300ER", label: "Boeing 777-300ER" },
        { value: "Airbus A320", label: "Airbus A320" },
        { value: "Airbus A321", label: "Airbus A321" },
        { value: "Airbus A330", label: "Airbus A330" },
        { value: "Airbus A350", label: "Airbus A350" },
        { value: "Airbus A380", label: "Airbus A380" },
        { value: "ATR 72", label: "ATR 72" },
        { value: "Embraer E170", label: "Embraer E170" },
        { value: "Embraer E190", label: "Embraer E190" },
        { value: "Bombardier Q400", label: "Bombardier Q400" }
    ];

    useEffect(() => {
        loadPlanes();
    }, []);

    // Effect to handle external modal trigger
    useEffect(() => {
        if (showAddModal) {
            setShowForm(true);
        }
    }, [showAddModal]);

    const loadPlanes = async () => {
        try {
            setLoading(true);
            const data = await planeService.getAllPlanes();
            setPlanes(data);
        } catch (err: any) {
            setError('Failed to load planes');
        } finally {
            setLoading(false);
        }
    };

    const onSubmit = async (data: PlaneFormData) => {
        try {
            if (editingPlane) {
                await planeService.updatePlane(editingPlane.planeId!, data);
            } else {
                await planeService.createPlane(data);
            }

            loadPlanes();
            handleCancel();
        } catch (err: any) {
            setError(err.message || 'Failed to save plane');
        }
    };

    const handleEdit = (plane: Plane) => {
        setEditingPlane(plane);
        reset({
            planeCode: plane.planeCode,
            planeType: plane.planeType,
            seatQuantity: plane.seatQuantity
        });
        setShowForm(true);
    };

    const handleDelete = async (planeId: number) => {
        if (!window.confirm('Are you sure you want to delete this plane? This will affect all associated flights.')) return;

        try {
            await planeService.deletePlane(planeId);
            loadPlanes();
        } catch (err: any) {
            setError(err.message || 'Failed to delete plane');
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingPlane(null);
        reset();
        setError('');
        
        // Call the external close handler if provided
        if (onCloseAddModal) {
            onCloseAddModal();
        }
    };

    // Filter planes based on search term and type
    const filteredPlanes = planes.filter(plane => {
        const matchesSearch = plane.planeCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
            plane.planeType.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesType = !filterType || plane.planeType.toLowerCase().includes(filterType.toLowerCase());
        return matchesSearch && matchesType;
    });

    // Get unique plane types for filter - combine existing fleet types with predefined options
    const existingPlaneTypes = [...new Set(planes.map(plane => plane.planeType))];
    const predefinedTypes = aircraftTypeOptions.map(option => option.value);
    const allPlaneTypes = [...new Set([...existingPlaneTypes, ...predefinedTypes])].sort();

    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
                <p className="mt-3">Loading aircraft data...</p>
            </Container>
        );
    }

    return (
        <Container fluid className="py-4">
            <Row className="mb-4">
                <Col>
                    <Card>
                        <Card.Header className="d-flex justify-content-between align-items-center">
                            <Card.Title className="mb-0">✈️ Aircraft Fleet Management</Card.Title>
                            <Button
                                variant="primary"
                                onClick={() => setShowForm(true)}
                            >
                                Add New Aircraft
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

            {/* Search and Filter Controls */}
            <Row className="mb-4">
                <Col>
                    <Card>
                        <Card.Header>
                            <Card.Title className="mb-0">Search & Filter</Card.Title>
                        </Card.Header>
                        <Card.Body>
                            <Row className="align-items-end">
                                <Col md={4}>
                                    <Form.Group>
                                        <Form.Label>Search Aircraft</Form.Label>
                                        <Form.Control
                                            type="text"
                                            placeholder="Search by code or type..."
                                            value={searchTerm}
                                            onChange={(e) => setSearchTerm(e.target.value)}
                                        />
                                    </Form.Group>
                                </Col>
                                <Col md={3}>
                                    <Form.Group>
                                        <Form.Label>Filter by Type</Form.Label>
                                        <Form.Select
                                            value={filterType}
                                            onChange={(e) => setFilterType(e.target.value)}
                                        >
                                            <option value="">All Types</option>
                                            {allPlaneTypes.map(type => (
                                                <option key={type} value={type}>{type}</option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                </Col>
                                <Col md={5}>
                                    <Row className="text-center">
                                        <Col>
                                            <Badge bg="primary" className="p-2">
                                                Total Aircraft: <strong>{planes.length}</strong>
                                            </Badge>
                                        </Col>
                                        <Col>
                                            <Badge bg="info" className="p-2">
                                                Total Seats: <strong>{planes.reduce((sum, plane) => sum + plane.seatQuantity, 0)}</strong>
                                            </Badge>
                                        </Col>
                                    </Row>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            <Modal show={showForm} onHide={handleCancel} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>{editingPlane ? 'Edit Aircraft' : 'Add New Aircraft'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form id="plane-form" onSubmit={handleSubmit(onSubmit)}>
                        <Row className="mb-3">
                            <Col>
                                <Form.Group>
                                    <Form.Label>Aircraft Code</Form.Label>
                                    <Form.Control
                                        type="text"
                                        {...register('planeCode', {
                                            required: 'Aircraft code is required',
                                            pattern: {
                                                value: /^[A-Z0-9-]+$/,
                                                message: 'Use only uppercase letters, numbers, and hyphens'
                                            }
                                        })}
                                        isInvalid={!!errors.planeCode}
                                        placeholder="e.g., VN-A001, B737-001"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.planeCode?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row className="mb-3">
                            <Col md={8}>
                                <Form.Group>
                                    <Form.Label>Aircraft Type</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="Enter aircraft type..."
                                        isInvalid={!!errors.planeType}
                                        {...register('planeType', {
                                            required: 'Aircraft type is required'
                                        })}
                                    />
                                    <Form.Text className="text-muted">
                                        Common types: {aircraftTypeOptions.slice(0, 3).map(type => type.value).join(', ')}, etc.
                                    </Form.Text>
                                    <Form.Control.Feedback type="invalid">
                                        {errors.planeType?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                            <Col md={4}>
                                <Form.Group>
                                    <Form.Label>Seat Capacity</Form.Label>
                                    <Form.Control
                                        type="number"
                                        min="1"
                                        max="850"
                                        {...register('seatQuantity', {
                                            required: 'Seat quantity is required',
                                            min: {
                                                value: 1,
                                                message: 'Must have at least 1 seat'
                                            },
                                            max: {
                                                value: 850,
                                                message: 'Maximum 850 seats allowed'
                                            },
                                            valueAsNumber: true
                                        })}
                                        isInvalid={!!errors.seatQuantity}
                                        placeholder="e.g., 180"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.seatQuantity?.message}
                                    </Form.Control.Feedback>
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
                        {editingPlane ? 'Update Aircraft' : 'Add Aircraft'}
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Aircraft Grid */}
            <Row>
                {filteredPlanes.length === 0 ? (
                    <Col>
                        <Card>
                            <Card.Body className="text-center py-5">
                                <p className="text-muted mb-0">
                                    {searchTerm || filterType ?
                                        'No aircraft found matching your search criteria.' :
                                        'No aircraft in the fleet. Add your first aircraft to get started.'
                                    }
                                </p>
                            </Card.Body>
                        </Card>
                    </Col>
                ) : (
                    filteredPlanes.map(plane => (
                        <Col key={plane.planeId} md={6} lg={4} className="mb-4">
                            <Card className="h-100">
                                <Card.Header className="d-flex justify-content-between align-items-center">
                                    <Badge bg="primary" className="fs-6">{plane.planeCode}</Badge>
                                    <div>
                                        <Button
                                            size="sm"
                                            variant="outline-secondary"
                                            className="me-2"
                                            onClick={() => handleEdit(plane)}
                                        >
                                            Edit
                                        </Button>
                                        {/* <Button
                                            size="sm"
                                            variant="outline-danger"
                                            onClick={() => handleDelete(plane.planeId!)}
                                        >
                                            Delete
                                        </Button> */}
                                    </div>
                                </Card.Header>
                                <Card.Body>
                                    <Card.Title className="text-center mb-3">{plane.planeType}</Card.Title>

                                    <Row className="mb-3">
                                        <Col xs={6}>
                                            <strong>Total Seats:</strong>
                                        </Col>
                                        <Col xs={6} className="text-end">
                                            <Badge bg="info">{plane.seatQuantity}</Badge>
                                        </Col>
                                    </Row>

                                    <Row className="mb-3">
                                        <Col xs={6}>
                                            <strong>Category:</strong>
                                        </Col>
                                        <Col xs={6} className="text-end">
                                            <Badge bg="secondary">
                                                {plane.seatQuantity < 100 ? 'Regional' :
                                                    plane.seatQuantity < 200 ? 'Narrow-body' :
                                                        plane.seatQuantity < 350 ? 'Wide-body' : 'Large Wide-body'}
                                            </Badge>
                                        </Col>
                                    </Row>

                                    <Card className="mt-3">
                                        <Card.Header as="h6" className="bg-light">
                                            Estimated Layout
                                        </Card.Header>
                                        <Card.Body className="py-2">
                                            <Row className="small">
                                                <Col xs={6}>
                                                    <strong>Economy:</strong>
                                                </Col>
                                                <Col xs={6} className="text-end">
                                                    ~{Math.floor(plane.seatQuantity * 0.8)}
                                                </Col>
                                            </Row>
                                            <Row className="small">
                                                <Col xs={6}>
                                                    <strong>Business:</strong>
                                                </Col>
                                                <Col xs={6} className="text-end">
                                                    ~{Math.floor(plane.seatQuantity * 0.15)}
                                                </Col>
                                            </Row>
                                            <Row className="small">
                                                <Col xs={6}>
                                                    <strong>First:</strong>
                                                </Col>
                                                <Col xs={6} className="text-end">
                                                    ~{Math.floor(plane.seatQuantity * 0.05)}
                                                </Col>
                                            </Row>
                                        </Card.Body>
                                    </Card>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))
                )}
            </Row>
        </Container>
    );
};

export default PlaneManagement;
