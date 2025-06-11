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
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [airportToDelete, setAirportToDelete] = useState<Airport | null>(null);
  const [deleting, setDeleting] = useState(false);

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

  const handleDeleteClick = (airport: Airport) => {
    setAirportToDelete(airport);
    setShowDeleteModal(true);
  };

  const handleConfirmDelete = async () => {
    if (!airportToDelete) return;
    
    try {
      setDeleting(true);
      await airportService.deleteAirport(airportToDelete.airportId!);
      await loadAirports();
      setShowDeleteModal(false);
      setAirportToDelete(null);
    } catch (err: any) {
      setError(err.message || 'Failed to delete airport');
    } finally {
      setDeleting(false);
    }
  };

  const handleCancelDelete = () => {
    setShowDeleteModal(false);
    setAirportToDelete(null);
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

            {/* Add/Edit Airport Modal */}
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

            {/* Delete Confirmation Modal */}
            <Modal show={showDeleteModal} onHide={handleCancelDelete} centered>
                <Modal.Header closeButton className="bg-danger text-white">
                    <Modal.Title>
                        <i className="bi bi-exclamation-triangle me-2"></i>
                        Xác nhận xóa sân bay
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body className="p-4">
                    <div className="text-center mb-3">
                        <i className="bi bi-exclamation-circle text-danger" style={{ fontSize: '3rem' }}></i>
                    </div>
                    <h5 className="text-center mb-3">Bạn có chắc chắn muốn xóa sân bay này không?</h5>
                    {airportToDelete && (
                        <div className="p-3 bg-light rounded mb-3">
                            <div className="text-center">
                                <strong>{airportToDelete.airportName}</strong><br />
                                <span className="text-muted">
                                    {airportToDelete.cityName}, {airportToDelete.countryName}
                                </span>
                            </div>
                        </div>
                    )}
                    <p className="text-center text-muted mb-0">
                        Hành động này không thể hoàn tác. Sân bay và tất cả dữ liệu liên quan sẽ bị xóa vĩnh viễn.
                    </p>
                </Modal.Body>
                <Modal.Footer>
                    <Button
                        variant="secondary"
                        onClick={handleCancelDelete}
                        disabled={deleting}
                    >
                        Hủy
                    </Button>
                    <Button
                        variant="danger"
                        onClick={handleConfirmDelete}
                        disabled={deleting}
                    >
                        {deleting ? (
                            <>
                                <Spinner animation="border" size="sm" className="me-2" />
                                Đang xóa...
                            </>
                        ) : (
                            <>
                                <i className="bi bi-trash me-2"></i>
                                Có, xóa sân bay
                            </>
                        )}
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Airport Table */}
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
                                                        disabled={deleting}
                                                    >
                                                        Sửa
                                                    </Button>
                                                    <Button
                                                        size="sm"
                                                        variant="outline-danger"
                                                        onClick={() => handleDeleteClick(airport)}
                                                        disabled={deleting}
                                                    >
                                                        Xóa
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
