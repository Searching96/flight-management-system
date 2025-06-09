import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal, Table } from 'react-bootstrap';
import { airportService } from '../../services';
import { Airport } from '../../models';
import TypeAhead from '../common/TypeAhead';
import { usePermissions } from '../../hooks/useAuth';

interface AirportFormData {
  airportName: string;
  cityName: string;
  countryName: string;
}

const AirportManagement: React.FC<{
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
                            <p>You do not have permission to access airport management.</p>
                        </Alert>
                    </Col>
                </Row>
            </Container>
        );
  }

  const [airports, setAirports] = useState<Airport[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingAirport, setEditingAirport] = useState<Airport | null>(null);
  const [selectedCountry, setSelectedCountry] = useState<string>('');

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm<AirportFormData>();

  // Country options for TypeAhead
  const countryOptions = [
    { value: 'Vietnam', label: 'Vietnam' },
    { value: 'United States', label: 'United States' },
    { value: 'United Kingdom', label: 'United Kingdom' },
    { value: 'China', label: 'China' },
    { value: 'Japan', label: 'Japan' },
    { value: 'South Korea', label: 'South Korea' },
    { value: 'Singapore', label: 'Singapore' },
    { value: 'Thailand', label: 'Thailand' },
    { value: 'Malaysia', label: 'Malaysia' },
    { value: 'Indonesia', label: 'Indonesia' },
    { value: 'Philippines', label: 'Philippines' },
    { value: 'Australia', label: 'Australia' },
    { value: 'France', label: 'France' },
    { value: 'Germany', label: 'Germany' },
    { value: 'Canada', label: 'Canada' }
  ];

  useEffect(() => {
    loadAirports();
  }, []);

  useEffect(() => {
    if (showAddModal) {
        setShowForm(true);
    }
  }, [showAddModal]);

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
    setSelectedCountry(airport.countryName || '');
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
    setSelectedCountry('');
    reset();
    setError('');
    
    // Call the external close handler if provided
    if (onCloseAddModal) {
      onCloseAddModal();
    }
  };

    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
                <p className="mt-3">Loading airport data...</p>
            </Container>
        );
    }

    return (
        <Container fluid className="py-4">
            <Row className="mb-4">
                <Col>
                    <Card>
                        <Card.Header className="d-flex justify-content-between align-items-center">
                            <Card.Title className="mb-0">🏢 Airport Management</Card.Title>
                            <Button
                                variant="primary"
                                onClick={() => setShowForm(true)}
                            >
                                Add New Airport
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

            <Modal show={showForm} onHide={handleCancel} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>{editingAirport ? 'Edit Airport' : 'Add New Airport'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form id="airport-form" onSubmit={handleSubmit(onSubmit)}>
                        <Row className="mb-3">
                            <Col>
                                <Form.Group>
                                    <Form.Label>Airport Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        {...register('airportName', {
                                            required: 'Airport name is required'
                                        })}
                                        isInvalid={!!errors.airportName}
                                        placeholder="e.g., John F. Kennedy International Airport"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.airportName?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row className="mb-3">
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label>City Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        {...register('cityName', {
                                            required: 'City name is required'
                                        })}
                                        isInvalid={!!errors.cityName}
                                        placeholder="e.g., New York"
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.cityName?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label>Country</Form.Label>
                                    <TypeAhead
                                        options={countryOptions}
                                        value={selectedCountry}
                                        onChange={(option) => {
                                            const country = option?.value || '';
                                            setSelectedCountry(String(country));
                                        }}
                                        placeholder="Search country..."
                                        error={!!errors.countryName}
                                    />
                                    <input
                                        type="hidden"
                                        {...register('countryName', {
                                            required: 'Country is required'
                                        })}
                                        value={selectedCountry}
                                    />
                                    {errors.countryName && (
                                        <div className="text-danger small mt-1">{errors.countryName.message}</div>
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
                        {editingAirport ? 'Update Airport' : 'Create Airport'}
                    </Button>
                </Modal.Footer>
            </Modal>

            <Row>
                <Col>
                    <Card>
                        <Card.Header>
                            <Card.Title className="mb-0">All Airports</Card.Title>
                        </Card.Header>
                        <Card.Body className="p-0">
                            {airports.length === 0 ? (
                                <div className="text-center py-5">
                                    <p className="text-muted mb-0">No airports found. Add your first airport to get started.</p>
                                </div>
                            ) : (
                                <Table responsive striped hover>
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
                                                <td>
                                                    <Badge bg="info">{airport.cityName}</Badge>
                                                </td>
                                                <td>{airport.countryName}</td>
                                                <td>
                                                    <Button
                                                        size="sm"
                                                        variant="outline-secondary"
                                                        className="me-2"
                                                        onClick={() => handleEdit(airport)}
                                                    >
                                                        Edit
                                                    </Button>
                                                    <Button
                                                        size="sm"
                                                        variant="outline-danger"
                                                        onClick={() => handleDelete(airport.airportId!)}
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

export default AirportManagement;
