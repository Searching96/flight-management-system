import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal, Table, InputGroup } from 'react-bootstrap';
import {
    airportService,
    planeService,
    ticketClassService,
    flightTicketClassService,
    parameterService
} from '../../services';
import {
    Flight,
    Airport,
    Plane,
    TicketClass,
    FlightRequest,
    FlightTicketClass,
    FlightTicketClassRequest,
    UpdateFlightTicketClassRequest,
    Parameter
} from '../../models';
import { usePermissions } from '../../hooks/useAuth';
import { useFlights } from '../../hooks/useFlights';
import { useFlightDetails } from '../../hooks/useFlightDetails';
import FlightForm from './flights/FlightForm';
import TypeAhead from '../common/TypeAhead';

const FlightManagement: React.FC<{
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
                            <Alert.Heading>Từ chối truy cập</Alert.Heading>
                            <p>Bạn không có quyền truy cập vào quản lý chuyến bay.</p>
                        </Alert>
                    </Col>
                </Row>
            </Container>
        );
    }

    // Use our custom hooks
    const {
        flights,
        loading,
        error: flightsError,
        loadFlights,
        createFlight,
        updateFlight,
        deleteFlight
    } = useFlights();

    const {
        flightDetails,
        error: detailsError,
        loadFlightDetails,
        saveFlightDetails,
        addFlightDetail,
        removeFlightDetail,
        updateFlightDetail,
        clearDetails
    } = useFlightDetails();

    // Add this to access the reset function
    const {
        reset
    } = useForm<FlightRequest>();

    // State for resources and UI
    const [airports, setAirports] = useState<Airport[]>([]);
    const [planes, setPlanes] = useState<Plane[]>([]);
    const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
    const [formErrors, setFormErrors] = useState<{ [key: string]: string }>({});
    const [detailErrors, setDetailErrors] = useState<{ [key: string]: string }>({});
    const [showForm, setShowForm] = useState(false);
    const [editingFlight, setEditingFlight] = useState<Flight | null>(null);
    const [error, setError] = useState('');

    // State for ticket class management
    const [selectedFlightForClasses, setSelectedFlightForClasses] = useState<Flight | null>(null);
    const [showTicketClassModal, setShowTicketClassModal] = useState(false);
    const [flightTicketClasses, setFlightTicketClasses] = useState<FlightTicketClass[]>([]);
    const [modifiedTicketClasses, setModifiedTicketClasses] = useState<Map<number, Partial<FlightTicketClass>>>(new Map());
    const [showCreateForm, setShowCreateForm] = useState(false);
    const [editingAssociation, setEditingAssociation] = useState<FlightTicketClass | null>(null);
    const [ticketClassValidationError, setTicketClassValidationError] = useState<string>('');

    // Add state for system parameters
    const [parameters, setParameters] = useState<Parameter | null>(null);

    // Initial data loading
    useEffect(() => {
        loadData();
    }, []);

    // Combine hook errors into one state
    useEffect(() => {
        setError(flightsError || detailsError || '');
    }, [flightsError, detailsError]);

    // Effect to handle external modal trigger
    useEffect(() => {
        if (showAddModal) {
            setShowForm(true);
        }
    }, [showAddModal]);

    const loadData = async () => {
        try {
            const [airportData, planeData, ticketClassData, parameterData] = await Promise.all([
                airportService.getAllAirports(),
                planeService.getAllPlanes(),
                ticketClassService.getAllTicketClasses(),
                parameterService.getAllParameters()
            ]);

            setAirports(airportData);
            setPlanes(planeData);
            setTicketClasses(ticketClassData);
            setParameters(parameterData); // Assuming first item contains all parameters

            await loadFlights();
        } catch (err: any) {
            setError('Failed to load data');
        }
    };

    // Flight form handlers
    const handleSubmit = async (data: FlightRequest) => {
        try {
            // Save the flight first
            let flightId: number;
            if (editingFlight) {
                await updateFlight(editingFlight.flightId!, data);
                flightId = editingFlight.flightId!;
            } else {
                const newFlight = await createFlight(data);
                if (!newFlight) return;
                flightId = newFlight.flightId!;
            }

            // Then save flight details
            const detailsSaved = await saveFlightDetails(flightId);
            if (!detailsSaved) return;

            // Success - close form and reload data
            handleCancel();
        } catch (err: any) {
            // Enhanced error handling
            let errorMsg = err.message || 'Không thể lưu chuyến bay';
            
            // Check for specific API error about duplicate airports
            if (err.status === 400 && err.message?.includes('Departure and arrival airports cannot be the same')) {
                errorMsg = 'Sân bay đi và sân bay đến không thể là cùng một sân bay.';
                setFormErrors(prev => ({...prev, airports: errorMsg}));
            } else if (err.message) {
                errorMsg = err.message;
            }
            
            setError(errorMsg);
        }
    };

    const handleEdit = async (flight: Flight) => {
        setEditingFlight(flight);
        await loadFlightDetails(flight.flightId!);
        setShowForm(true);
    };

    const handleDelete = async (flightId: number) => {
        if (!window.confirm('Are you sure you want to delete this flight?')) return;
        await deleteFlight(flightId);
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingFlight(null);
        clearDetails();
        setFormErrors({});
        setDetailErrors({});
        reset();
        setError('');
        
        // Call the external close handler if provided
        if (onCloseAddModal) {
            onCloseAddModal();
        }
    };

    // Add handler for FlightForm to add flight details
    const handleAddFlightDetail = () => {
        addFlightDetail(editingFlight?.flightId || 0);

        // Clear any previous errors
        if (error.includes('Lỗi: Sân bay')) {
            setError('');
        }
        // Also clear form errors but only for flightDetails
        setDetailErrors(prev => {
            const { flightDetails, ...rest } = prev;
            return rest;
        });
    };

    // TICKET CLASS MANAGEMENT FUNCTIONS
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
            setModifiedTicketClasses(new Map());
            setTicketClassValidationError('');
        } catch (err: any) {
            setError('Failed to load ticket class data');
        }
    };

    // Handler for local updates to ticket class data (doesn't save to backend)
    const handleTicketClassUpdate = (
        ticketClassId: number,
        data: Partial<FlightTicketClass>
    ) => {
        // Get the original association to compare with
        const originalAssociation = flightTicketClasses.find(ftc => ftc.ticketClassId === ticketClassId);
        if (!originalAssociation) return;

        // Only store fields that have actually changed
        const changedFields: Partial<FlightTicketClass> = {};

        // Compare each field with the original and only store if different
        if (data.ticketQuantity !== undefined && data.ticketQuantity !== originalAssociation.ticketQuantity) {
            changedFields.ticketQuantity = data.ticketQuantity;
        }

        if (data.specifiedFare !== undefined && data.specifiedFare !== originalAssociation.specifiedFare) {
            changedFields.specifiedFare = data.specifiedFare;
        }

        if (data.remainingTicketQuantity !== undefined && data.remainingTicketQuantity !== originalAssociation.remainingTicketQuantity) {
            changedFields.remainingTicketQuantity = data.remainingTicketQuantity;
        }

        // If nothing changed, don't update the map
        if (Object.keys(changedFields).length === 0) {
            // If this was already in the modified map, remove it
            if (modifiedTicketClasses.has(ticketClassId)) {
                setModifiedTicketClasses(prev => {
                    const newMap = new Map(prev);
                    newMap.delete(ticketClassId);
                    return newMap;
                });
            }
            setEditingAssociation(null);
            return;
        }

        // Update the modified map with only the changed fields
        setModifiedTicketClasses(prev => {
            const newMap = new Map(prev);
            newMap.set(ticketClassId, changedFields);
            return newMap;
        });

        setEditingAssociation(null);
    };

    // Handler for undoing local ticket class changes
    const handleUndoTicketClassChanges = (ticketClassId: number) => {
        setModifiedTicketClasses(prev => {
            const newMap = new Map(prev);
            newMap.delete(ticketClassId);
            return newMap;
        });
    };

    // Save all ticket class changes at once
    const handleSaveAllTicketClasses = async () => {
        if (!selectedFlightForClasses) return;

        try {
            // Find the plane to get its capacity
            const plane = planes.find(p => p.planeId === selectedFlightForClasses.planeId);
            if (!plane) {
                setTicketClassValidationError('Could not find the plane information');
                return;
            }

            // Calculate total seats assigned across all ticket classes
            let totalAssignedSeats = 0;

            // Calculate total from current classes, accounting for modifications
            flightTicketClasses.forEach(ftc => {
                const modified = modifiedTicketClasses.get(ftc.ticketClassId!);
                // Use the modified ticket quantity if available, otherwise use original
                const quantity = modified?.ticketQuantity !== undefined
                    ? modified.ticketQuantity
                    : ftc.ticketQuantity!;
                totalAssignedSeats += quantity;
            });

            // Validate total seat count matches plane capacity
            if (plane.seatQuantity !== totalAssignedSeats) {
                setTicketClassValidationError(
                    `Total seat allocation (${totalAssignedSeats}) must match plane capacity (${plane.seatQuantity})`
                );
                return;
            }

            // Process all modifications
            const updatePromises = Array.from(modifiedTicketClasses.entries()).map(([ticketClassId, changedFields]) => {
                // Find the original association to merge with changes
                const original = flightTicketClasses.find(ftc => ftc.ticketClassId === ticketClassId);
                if (!original) return Promise.resolve();

                // Create the update request by merging original values with changed fields
                const updateRequest: UpdateFlightTicketClassRequest = {
                    ticketQuantity: changedFields.ticketQuantity !== undefined
                        ? changedFields.ticketQuantity
                        : original.ticketQuantity!,

                    specifiedFare: changedFields.specifiedFare !== undefined
                        ? changedFields.specifiedFare
                        : original.specifiedFare!,

                    remainingTicketQuantity: changedFields.remainingTicketQuantity !== undefined
                        ? changedFields.remainingTicketQuantity
                        : original.remainingTicketQuantity!
                };

                return flightTicketClassService.updateFlightTicketClass(
                    selectedFlightForClasses!.flightId!,
                    ticketClassId,
                    updateRequest
                );
            });

            await Promise.all(updatePromises);

            // Refresh data
            await loadFlightTicketClasses(selectedFlightForClasses.flightId!);
            setModifiedTicketClasses(new Map());
            setTicketClassValidationError('');
            setError('');
        } catch (err: any) {
            setError('Failed to save ticket class changes: ' + err.message);
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


    // Update the handleFlightDetailChange function to move validation to the form
    const handleFlightDetailChange = (index: number, field: string, value: any) => {
        // When changing airport, check if it's a duplicate is now handled inside FlightForm
        updateFlightDetail(index, field, value);
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
                <p className="mt-3">Đang tải dữ liệu chuyến bay...</p>
            </Container>
        );
    }

    return (
        <Container fluid className="py-4">
            {/* Header */}
            <Row className="mb-4">
                <Col>
                    <Card>
                        <Card.Header className="d-flex justify-content-between align-items-center">
                            <Card.Title className="mb-0">✈️ Quản lý chuyến bay</Card.Title>
                            <Button
                                variant="primary"
                                onClick={() => setShowForm(true)}
                            >
                                Thêm chuyến bay mới
                            </Button>
                        </Card.Header>
                    </Card>
                </Col>
            </Row>

            {/* Error display */}
            {error && (
                <Row className="mb-4">
                    <Col>
                        <Alert variant="danger" className="text-center">
                            {error}
                        </Alert>
                    </Col>
                </Row>
            )}

            {/* Flight Form Modal */}
            <Modal show={showForm} onHide={handleCancel} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>{editingFlight ? 'Sửa chuyến bay' : 'Thêm chuyến bay mới'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {/* Add a specific alert for API errors */}
                    {error && error.includes('sân bay') && (
                        <Alert variant="danger" className="mb-3">
                            <i className="bi bi-exclamation-triangle-fill me-2"></i>
                            {error}
                        </Alert>
                    )}
                    <FlightForm
                        editingFlight={editingFlight}
                        airports={airports}
                        planes={planes}
                        flightDetails={flightDetails}
                        formErrors={formErrors}
                        detailErrors={detailErrors}
                        setFormErrors={setFormErrors}
                        setDetailErrors={setDetailErrors}
                        onSubmit={handleSubmit}
                        onCancel={handleCancel}
                        onFlightDetailChange={handleFlightDetailChange}
                        onAddFlightDetail={handleAddFlightDetail}
                        onRemoveFlightDetail={removeFlightDetail}
                        parameters={parameters}
                    />
                </Modal.Body>
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
                        Hạng vé cho chuyến bay {selectedFlightForClasses?.flightCode}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Row className="align-items-center mb-3">
                        <Col>
                            <h5 className="mb-0">
                                Tuyến bay: {selectedFlightForClasses?.departureCityName} → {selectedFlightForClasses?.arrivalCityName}
                            </h5>
                            <p className="text-muted">
                                Máy bay: {selectedFlightForClasses?.planeCode}
                                {planes.find(p => p.planeId === selectedFlightForClasses?.planeId)?.seatQuantity &&
                                    ` (${planes.find(p => p.planeId === selectedFlightForClasses?.planeId)?.seatQuantity} ghế)`
                                }
                            </p>
                        </Col>
                        <Col md="auto">
                            {availableTicketClasses.length > 0 && (
                                <Button
                                    variant="primary"
                                    onClick={() => setShowCreateForm(true)}
                                >
                                    <i className="bi bi-plus-circle me-2"></i>
                                    Thêm hạng vé
                                </Button>
                            )}
                        </Col>
                    </Row>

                    {ticketClassValidationError && (
                        <Alert variant="danger" className="mb-3">
                            <i className="bi bi-exclamation-triangle-fill me-2"></i>
                            {ticketClassValidationError}
                        </Alert>
                    )}

                    {/* Show indication of pending changes */}
                    {modifiedTicketClasses.size > 0 && (
                        <Alert variant="warning" className="mb-3">
                            <i className="bi bi-info-circle-fill me-2"></i>
                            Bạn có {modifiedTicketClasses.size} thay đổi chưa lưu. Nhấn "Lưu thay đổi" để áp dụng.
                        </Alert>
                    )}

                    {showCreateForm && (
                        <CreateAssociationFormWithTypeAhead
                            availableClasses={availableTicketClasses}
                            onSubmit={handleCreateAssociation}
                            onCancel={() => setShowCreateForm(false)}
                        />
                    )

                        /*  MODIFIED SECTION STARTS HERE  */
                    }

                    <Row className="g-3 mt-2">
                        {flightTicketClasses.map(association => (
                            <Col lg={6} xl={4} key={`${association.flightId}-${association.ticketClassId}`}>
                                <TicketClassCard
                                    association={association}
                                    className={getTicketClassName(association.ticketClassId!)}
                                    classColor={getTicketClassColor(association.ticketClassId!)}
                                    isEditing={editingAssociation?.ticketClassId === association.ticketClassId}
                                    onEdit={() => setEditingAssociation(association)}
                                    onSave={(data) => handleTicketClassUpdate(
                                        association.ticketClassId!,
                                        data
                                    )}
                                    onCancel={() => setEditingAssociation(null)}
                                    onDelete={() => handleDeleteAssociation(
                                        association.flightId!,
                                        association.ticketClassId!
                                    )}
                                    onUndo={() => handleUndoTicketClassChanges(association.ticketClassId!)}
                                    isModified={modifiedTicketClasses.has(association.ticketClassId!)}
                                    modifiedTicketClasses={modifiedTicketClasses}
                                />
                            </Col>
                        ))}
                    </Row>

                    {flightTicketClasses.length === 0 && (
                        <Alert variant="info" className="text-center">
                            <Alert.Heading>Chưa có hạng vé nào</Alert.Heading>
                            <p className="mb-0">Thêm hạng vé để cho phép đặt chỗ cho chuyến bay này.</p>
                        </Alert>
                    )}
                </Modal.Body>
                <Modal.Footer className="d-flex justify-content-between">
                    <Button variant="secondary" onClick={handleCancelTicketClasses}>
                        Đóng
                    </Button>
                    <Button
                        variant="success"
                        onClick={handleSaveAllTicketClasses}
                        disabled={modifiedTicketClasses.size === 0}
                    >
                        <i className="bi bi-save me-2"></i>
                        Lưu thay đổi
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Flights table */}
            <Row>
                <Col>
                    <Card>
                        <Card.Header>
                            <Card.Title className="mb-0">Tất cả chuyến bay</Card.Title>
                        </Card.Header>
                        <Card.Body className="p-0">
                            {flights.length === 0 ? (
                                <div className="text-center py-5">
                                    <p className="text-muted mb-0">Không tìm thấy chuyến bay nào. Thêm chuyến bay đầu tiên để bắt đầu.</p>
                                </div>
                            ) : (
                                <Table responsive striped hover>
                                    <thead>
                                        <tr>
                                            <th>Mã chuyến bay</th>
                                            <th>Tuyến bay</th>
                                            <th>Khởi hành</th>
                                            <th>Đến</th>
                                            <th>Máy bay</th>
                                            <th>Thao tác</th>
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
                                                        Quản lý hạng vé
                                                    </Button>
                                                    <Button
                                                        size="sm"
                                                        variant="outline-danger"
                                                        onClick={() => handleDelete(flight.flightId!)}
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
                <Card.Title as="h4" className="mb-0">Thêm hạng vé</Card.Title>
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
                                <Form.Label>Hạng vé</Form.Label>
                                <TypeAhead
                                    options={ticketClassOptions}
                                    value={selectedTicketClass}
                                    onChange={(option) => {
                                        setSelectedTicketClass(option?.value as number || '');
                                    }}
                                    placeholder="Tìm hạng vé..."
                                />
                            </Form.Group>
                        </Col>

                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>Số lượng ghế</Form.Label>
                                <Form.Control
                                    type="number"
                                    min="1"
                                    value={formData.ticketQuantity}
                                    onChange={(e) => setFormData(prev => ({ ...prev, ticketQuantity: e.target.value }))
                                    }
                                    required
                                    placeholder="vd: 100"
                                />
                            </Form.Group>
                        </Col>

                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>Giá vé</Form.Label>
                                <InputGroup>
                                    <Form.Control
                                        className='me-0'
                                        type="number"
                                        min="1"
                                        step="1000"
                                        value={formData.specifiedFare}
                                        onChange={(e) => setFormData(prev => ({ ...prev, specifiedFare: e.target.value }))
                                        }
                                        required
                                        placeholder="vd: 1500000"
                                    />
                                    <InputGroup.Text>VND</InputGroup.Text>
                                </InputGroup>
                            </Form.Group>
                        </Col>
                    </Row>

                    <div className="d-flex gap-2 mt-3">
                        <Button type="button" variant="secondary" onClick={onCancel}>
                            Hủy
                        </Button>
                        <Button type="submit" variant="primary">
                            Thêm hạng vé
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
    onUndo: () => void; // New handler for undoing changes
    isModified?: boolean;
    modifiedTicketClasses: Map<number, Partial<FlightTicketClass>>;
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
    onUndo,  // New prop
    isModified = false,
    modifiedTicketClasses
}) => {
    const [editData, setEditData] = useState({
        ticketQuantity: association.ticketQuantity || 0,
        specifiedFare: association.specifiedFare || 0,
        remainingTicketQuantity: association.remainingTicketQuantity || 0
    });
    const [validationError, setValidationError] = useState<string | null>(null);

    // Calculate the number of sold seats
    const soldSeats = (association.ticketQuantity || 0) - (association.remainingTicketQuantity || 0);
    const occupancyRate = association.ticketQuantity ?
        ((soldSeats / association.ticketQuantity) * 100).toFixed(1) : '0';

    // Validate the form when any input changes
    useEffect(() => {
        validateForm();
    }, [editData]);

    // Validation logic for remaining ticket quantity
    const validateForm = () => {
        // Reset validation error
        setValidationError(null);

        // Validate remaining tickets - can't be less than sold tickets
        if (editData.remainingTicketQuantity < 0) {
            setValidationError('Remaining seats cannot be negative');
        } else if (editData.remainingTicketQuantity > editData.ticketQuantity) {
            setValidationError('Remaining seats cannot exceed total seats');
        } else if (editData.ticketQuantity - editData.remainingTicketQuantity < soldSeats) {
            setValidationError(`Cannot reduce remaining seats below sold seats (${soldSeats})`);
        }
    };

    const handleSave = () => {
        // Only save if validation passes
        if (!validationError) {
            onSave(editData);
        }
    };

    // Update card style to show modified status
    const cardStyle = {
        borderLeft: `4px solid ${classColor}`,
        ...(isModified ? { boxShadow: '0 0 0 2px rgba(255, 193, 7, 0.5)' } : {})
    };

    return (
        <Card className="h-100" style={cardStyle}>
            <Card.Header className="d-flex justify-content-between align-items-center">
                <div className="d-flex align-items-center">
                    <Card.Title as="h5" className="mb-0" style={{ color: classColor }}>
                        {className}
                    </Card.Title>
                    {isModified && (
                        <Badge bg="warning" className="ms-2">Modified</Badge>
                    )}
                </div>
                <div className="d-flex gap-1">
                    {isEditing ? (
                        <>
                            <Button
                                size="sm"
                                variant="success"
                                onClick={handleSave}
                                disabled={!!validationError}
                            >
                                Apply
                            </Button>
                            <Button size="sm" variant="secondary" onClick={onCancel}>
                                Cancel
                            </Button>
                        </>
                    ) : (
                        <>
                            {isModified && (
                                <Button size="sm" variant="warning" onClick={onUndo} title="Undo changes">
                                    <i className="bi bi-arrow-counterclockwise"></i>
                                </Button>
                            )}
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
                                <Form.Label>Tổng số ghế</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={editData.ticketQuantity}
                                    onChange={(e) => setEditData(prev => ({
                                        ...prev,
                                        ticketQuantity: parseInt(e.target.value) || 0
                                    }))}
                                    min={soldSeats}
                                />
                                {soldSeats > 0 && (
                                    <div className="text-muted small">
                                        Tối thiểu: {soldSeats} (vé đã bán)
                                    </div>
                                )}
                            </Form.Group>
                        </Col>
                        <Col sm={6}>
                            <Form.Group>
                                <Form.Label>Giá vé</Form.Label>
                                <InputGroup>
                                    <Form.Control
                                        type="number"
                                        className='pe-1'
                                        value={editData.specifiedFare}
                                        onChange={(e) => setEditData(prev => ({
                                            ...prev,
                                            specifiedFare: parseInt(e.target.value) || 0
                                        }))}
                                        min="0"
                                    />
                                    <InputGroup.Text>VND</InputGroup.Text>
                                </InputGroup>
                            </Form.Group>
                        </Col>
                        <Col xs={12}>
                            <Form.Group>
                                <Form.Label>Số ghế còn lại</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={editData.remainingTicketQuantity}
                                    onChange={(e) => setEditData(prev => ({
                                        ...prev,
                                        remainingTicketQuantity: parseInt(e.target.value) || 0
                                    }))}
                                    min={editData.ticketQuantity - soldSeats}
                                    max={editData.ticketQuantity}
                                    isInvalid={!!validationError}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {validationError}
                                </Form.Control.Feedback>
                                <div className="text-muted small">
                                    Vé đã bán: {soldSeats}
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                ) : (
                    <>
                        <Row className="g-2 mb-3">
                            <Col xs={6}>
                                <div className="d-flex justify-content-between">
                                    <span className="text-muted">Tổng số ghế:</span>
                                    <span>
                                        <strong>{association.ticketQuantity}</strong>
                                        {isModified && modifiedTicketClasses.get(association.ticketClassId!)?.ticketQuantity !== undefined && (
                                            <Badge bg="warning" pill className="ms-2">
                                                →{modifiedTicketClasses.get(association.ticketClassId!)?.ticketQuantity}
                                            </Badge>
                                        )}
                                    </span>
                                </div>
                            </Col>
                            <Col xs={6}>
                                <div className="d-flex justify-content-between">
                                    <span className="text-muted">Còn lại:</span>
                                    <span>
                                        <strong>{association.remainingTicketQuantity}</strong>
                                        {isModified && modifiedTicketClasses.get(association.ticketClassId!)?.remainingTicketQuantity !== undefined && (
                                            <Badge bg="warning" pill className="ms-2">
                                                →{modifiedTicketClasses.get(association.ticketClassId!)?.remainingTicketQuantity}
                                            </Badge>
                                        )}
                                    </span>
                                </div>
                            </Col>
                            <Col xs={6}>
                                <div className="d-flex justify-content-between">
                                    <span className="text-muted">Giá vé:</span>
                                    <span>
                                        <strong>{association.specifiedFare?.toLocaleString()} VND</strong>
                                        {isModified && modifiedTicketClasses.get(association.ticketClassId!)?.specifiedFare !== undefined && (
                                            <Badge bg="warning" pill className="ms-2">
                                                →{modifiedTicketClasses.get(association.ticketClassId!)?.specifiedFare?.toLocaleString()} VND
                                            </Badge>
                                        )}
                                    </span>
                                </div>
                            </Col>
                            <Col xs={6}>
                                <div className="d-flex justify-content-between">
                                    <span className="text-muted">Đã bán:</span>
                                    <strong>{soldSeats}</strong>
                                </div>
                            </Col>
                        </Row>

                        <Row className="mb-2">
                            <Col xs={12}>
                                <div className="d-flex justify-content-between align-items-center mb-2">
                                    <span className="text-muted">Tỷ lệ lấp đầy:</span>
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
                            </Col>
                        </Row>
                    </>
                )}
            </Card.Body>

            {/* Add undo hint at bottom of card when modified - now in a Row */}
            {isModified && !isEditing && (
                <Card.Footer className="p-0 border-top">
                    <Row>
                        <Col xs={12} className="text-center py-2">
                            <small className="text-muted">
                                <i className="bi bi-info-circle me-1"></i>
                                Thay đổi chưa được lưu. Nhấn <i className="bi bi-arrow-counterclockwise"></i> để hoàn tác.
                            </small>
                        </Col>
                    </Row>
                </Card.Footer>
            )}
        </Card>
    );
};

export default FlightManagement;