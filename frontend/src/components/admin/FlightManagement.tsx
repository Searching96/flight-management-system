import React, { useState, useEffect, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import {
    Container,
    Row,
    Col,
    Card,
    Button,
    Form,
    Alert,
    Spinner,
    Badge,
    Modal,
    InputGroup
} from 'react-bootstrap';
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
import FlightTable from './flights/FlightTable';

const FlightManagement: React.FC<{
    showAddModal?: boolean;
    onCloseAddModal?: () => void;
}> = ({ showAddModal = false, onCloseAddModal }) => {
    const permissions = usePermissions();

    // Check permissions first
    if (!permissions.canViewFlightManagement()) {
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

    // Use custom hooks
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

    const { reset } = useForm<FlightRequest>();

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

    // System parameters
    const [parameters, setParameters] = useState<Parameter | null>(null);

    // Initial data loading
    useEffect(() => {
        loadData();
    }, []);

    // Combine hook errors
    useEffect(() => {
        setError(flightsError || detailsError || '');
    }, [flightsError, detailsError]);

    // Handle external modal trigger
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
            setParameters(parameterData[0] || null);

            await loadFlights();
        } catch (err: any) {
            setError('Failed to load data');
        }
    };

    // Flight form handlers
    const handleSubmit = async (data: FlightRequest) => {
        try {
            let flightId: number;
            if (editingFlight) {
                await updateFlight(editingFlight.flightId!, data);
                flightId = editingFlight.flightId!;
            } else {
                const newFlight = await createFlight(data);
                if (!newFlight) return;
                flightId = newFlight.flightId!;
            }

            const detailsSaved = await saveFlightDetails(flightId);
            if (!detailsSaved) return;

            handleCancel();
        } catch (err: any) {
            let errorMsg = err.message || 'Không thể lưu chuyến bay';

            if (err.status === 400 && err.message?.includes('Departure and arrival airports cannot be the same')) {
                errorMsg = 'Sân bay đi và sân bay đến không thể là cùng một sân bay.';
                setFormErrors(prev => ({ ...prev, airports: errorMsg }));
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
        if (!window.confirm('Xác nhận xóa chuyến bay?')) return;
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

        if (onCloseAddModal) {
            onCloseAddModal();
        }
    };

    const handleAddFlightDetail = () => {
        addFlightDetail(editingFlight?.flightId || 0);

        if (error.includes('Lỗi: Sân bay')) {
            setError('');
        }
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

    const handleTicketClassUpdate = async (
        ticketClassId: number,
        data: Partial<FlightTicketClass>
    ) => {
        try {
            const originalAssociation = await flightTicketClassService.getFlightTicketClassById(
                selectedFlightForClasses!.flightId!,
                ticketClassId
            );
            if (!originalAssociation) return;

            const changedFields: Partial<FlightTicketClass> = {};

            if (data.ticketQuantity !== undefined && data.ticketQuantity !== originalAssociation.ticketQuantity) {
                changedFields.ticketQuantity = data.ticketQuantity;
            }

            if (data.specifiedFare !== undefined && data.specifiedFare !== originalAssociation.specifiedFare) {
                changedFields.specifiedFare = data.specifiedFare;
            }

            if (data.remainingTicketQuantity !== undefined && data.remainingTicketQuantity !== originalAssociation.remainingTicketQuantity) {
                changedFields.remainingTicketQuantity = data.remainingTicketQuantity;
            }

            if (Object.keys(changedFields).length === 0) {
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

            setModifiedTicketClasses(prev => {
                const newMap = new Map(prev);
                newMap.set(ticketClassId, changedFields);
                return newMap;
            });

            setEditingAssociation(null);
        } catch (err: any) {
            setError('Failed to load original ticket class data');
        }
    };

    const handleUndoTicketClassChanges = (ticketClassId: number) => {
        setModifiedTicketClasses(prev => {
            const newMap = new Map(prev);
            newMap.delete(ticketClassId);
            return newMap;
        });
    };

    const handleSaveAllTicketClasses = async () => {
        if (!selectedFlightForClasses) return;

        try {
            const plane = planes.find(p => p.planeId === selectedFlightForClasses.planeId);
            if (!plane) {
                setTicketClassValidationError('Could not find the plane information');
                return;
            }

            let totalAssignedSeats = 0;

            flightTicketClasses.forEach(ftc => {
                const modified = modifiedTicketClasses.get(ftc.ticketClassId!);
                const quantity = modified?.ticketQuantity !== undefined
                    ? modified.ticketQuantity
                    : ftc.ticketQuantity!;
                totalAssignedSeats += quantity;
            });

            if (plane.seatQuantity !== totalAssignedSeats) {
                setTicketClassValidationError(
                    `Total seat allocation (${totalAssignedSeats}) must match plane capacity (${plane.seatQuantity})`
                );
                return;
            }

            const updatePromises = Array.from(modifiedTicketClasses.entries()).map(([ticketClassId, changedFields]) => {
                const original = flightTicketClasses.find(ftc => ftc.ticketClassId === ticketClassId);
                if (!original) return Promise.resolve();

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

    const handleFlightDetailChange = (index: number, field: string, value: any) => {
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

            {/* Ticket Class Management Modal */}
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
                    {/* Flight Route Info */}
                    <Row className="align-items-center mb-3">
                        <Col>
                            <h5 className="mb-0">
                                Tuyến bay: {selectedFlightForClasses?.departureCityName} → {selectedFlightForClasses?.arrivalCityName}
                            </h5>
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

                    {/* Plane Information Card */}
                    {selectedFlightForClasses && (
                        <PlaneInfoCard
                            selectedFlight={selectedFlightForClasses}
                            planes={planes}
                            flightTicketClasses={flightTicketClasses}
                            modifiedTicketClasses={modifiedTicketClasses}
                        />
                    )}

                    {/* Validation Errors */}
                    {ticketClassValidationError && (
                        <Alert variant="danger" className="mb-3">
                            <i className="bi bi-exclamation-triangle-fill me-2"></i>
                            {ticketClassValidationError}
                        </Alert>
                    )}

                    {/* Pending Changes Alert */}
                    {modifiedTicketClasses.size > 0 && (
                        <Alert variant="warning" className="mb-3">
                            <i className="bi bi-info-circle-fill me-2"></i>
                            Bạn có {modifiedTicketClasses.size} thay đổi chưa lưu. Nhấn "Lưu thay đổi" để áp dụng.
                        </Alert>
                    )}

                    {/* Create Association Form */}
                    {showCreateForm && (
                        <CreateAssociationFormWithTypeAhead
                            availableClasses={availableTicketClasses}
                            onSubmit={handleCreateAssociation}
                            onCancel={() => setShowCreateForm(false)}
                            selectedFlightForClasses={selectedFlightForClasses}
                            planes={planes}
                            flightTicketClasses={flightTicketClasses}
                        />
                    )}

                    {/* Ticket Class Cards */}
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
                                    selectedFlightForClasses={selectedFlightForClasses}
                                    planes={planes}
                                    flightTicketClasses={flightTicketClasses}
                                />
                            </Col>
                        ))}
                    </Row>

                    {/* No Ticket Classes Message */}
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
        </Container>
    );
}

// Plane Information Card Component
interface PlaneInfoCardProps {
    selectedFlight: Flight;
    planes: Plane[];
    flightTicketClasses: FlightTicketClass[];
    modifiedTicketClasses: Map<number, Partial<FlightTicketClass>>;
}

const PlaneInfoCard: React.FC<PlaneInfoCardProps> = ({
    selectedFlight,
    planes,
    flightTicketClasses,
    modifiedTicketClasses
}) => {
    const planeInfo = useMemo(() => {
        const plane = planes.find(p => p.planeId === selectedFlight.planeId);
        if (!plane) return null;

        // Calculate assigned seats including pending modifications
        const totalAssignedSeats = flightTicketClasses.reduce((total, ftc) => {
            const modified = modifiedTicketClasses.get(ftc.ticketClassId!);
            const quantity = modified?.ticketQuantity !== undefined
                ? modified.ticketQuantity
                : ftc.ticketQuantity!;
            return total + quantity;
        }, 0);

        const availableSeats = plane.seatQuantity - totalAssignedSeats;
        const utilizationRate = ((totalAssignedSeats / plane.seatQuantity) * 100).toFixed(1);

        return {
            planeCode: plane.planeCode,
            planeType: plane.planeType,
            totalSeats: plane.seatQuantity,
            assignedSeats: totalAssignedSeats,
            availableSeats: availableSeats,
            utilizationRate: utilizationRate
        };
    }, [selectedFlight, planes, flightTicketClasses, modifiedTicketClasses]);

    if (!planeInfo) return null;

    return (
        <Card className="mb-4 border-info">
            <Card.Header className="bg-info text-white">
                <Row className="align-items-center">
                    <Col>
                        <h6 className="mb-0">
                            <i className="bi bi-airplane me-2"></i>
                            Máy bay: {planeInfo.planeCode} ({planeInfo.planeType})
                        </h6>
                    </Col>
                    <Col xs="auto">
                        <Badge bg="light" text="dark">
                            Sử dụng: {planeInfo.utilizationRate}%
                        </Badge>
                    </Col>
                </Row>
            </Card.Header>
            <Card.Body className="p-3">
                <Row className="text-center g-3">
                    <Col xs={4}>
                        <div className="border rounded p-2 h-100">
                            <div className="h4 mb-1 text-info">{planeInfo.totalSeats}</div>
                            <small className="text-muted">Tổng ghế</small>
                        </div>
                    </Col>
                    <Col xs={4}>
                        <div className="border rounded p-2 h-100 bg-success text-white">
                            <div className="h4 mb-1">{planeInfo.assignedSeats}</div>
                            <small>Đã phân bổ</small>
                        </div>
                    </Col>
                    <Col xs={4}>
                        <div className="border rounded p-2 h-100 bg-primary text-white">
                            <div className="h4 mb-1">{planeInfo.availableSeats}</div>
                            <small>Còn trống</small>
                        </div>
                    </Col>
                </Row>

                {/* Progress bar */}
                <div className="mt-3">
                    <div className="d-flex justify-content-between mb-1">
                        <small className="text-muted">Sức chứa đã sử dụng</small>
                        <small className="text-muted">{planeInfo.utilizationRate}%</small>
                    </div>
                    <div className="progress" style={{ height: '8px' }}>
                        <div
                            className="progress-bar bg-success"
                            style={{ width: `${planeInfo.utilizationRate}%` }}
                        />
                    </div>
                    <div className="d-flex justify-content-between mt-1">
                        <small className="text-muted">0</small>
                        <small className="text-muted">{planeInfo.totalSeats}</small>
                    </div>
                </div>

                {/* Status alerts */}
                {planeInfo.availableSeats === 0 && (
                    <Alert variant="danger" className="mt-3 mb-0 small">
                        <i className="bi bi-exclamation-triangle me-1"></i>
                        Máy bay đã đầy! Không thể thêm hạng vé mới.
                    </Alert>
                )}

                {planeInfo.availableSeats > 0 && planeInfo.availableSeats <= 10 && (
                    <Alert variant="warning" className="mt-3 mb-0 small">
                        <i className="bi bi-info-circle me-1"></i>
                        Chỉ còn {planeInfo.availableSeats} ghế trống.
                    </Alert>
                )}
            </Card.Body>
        </Card>
    );
};

// Create Association Form Component with TypeAhead and Plane Info
interface CreateAssociationFormProps {
    availableClasses: TicketClass[];
    onSubmit: (data: { ticketClassId: number; ticketQuantity: number; specifiedFare: number }) => void;
    onCancel: () => void;
    selectedFlightForClasses: Flight | null;
    planes: Plane[];
    flightTicketClasses: FlightTicketClass[];
}

const CreateAssociationFormWithTypeAhead: React.FC<CreateAssociationFormProps> = ({
    availableClasses,
    onSubmit,
    onCancel,
    selectedFlightForClasses,
    planes,
    flightTicketClasses
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

    // Calculate available seats for validation
    const availableSeats = useMemo(() => {
        if (!selectedFlightForClasses || planes.length === 0) return 0;

        const plane = planes.find(p => p.planeId === selectedFlightForClasses.planeId);
        if (!plane) return 0;

        const totalAssignedSeats = flightTicketClasses.reduce(
            (total, ftc) => total + (ftc.ticketQuantity || 0),
            0
        );

        return plane.seatQuantity - totalAssignedSeats;
    }, [selectedFlightForClasses, planes, flightTicketClasses]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!selectedTicketClass || !formData.ticketQuantity || !formData.specifiedFare) {
            setFormErrors('Vui lòng điền đầy đủ tất cả các trường');
            return;
        }

        if (Number(formData.ticketQuantity) <= 0) {
            setFormErrors('Số lượng ghế phải lớn hơn 0');
            return;
        }

        if (Number(formData.specifiedFare) <= 0) {
            setFormErrors('Giá vé phải lớn hơn 0');
            return;
        }

        if (Number(formData.ticketQuantity) > availableSeats) {
            setFormErrors(`Số lượng ghế không thể vượt quá ${availableSeats} ghế còn lại của máy bay.`);
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
                                <Form.Label>
                                    Số lượng ghế
                                    {availableSeats > 0 && (
                                        <Badge bg="primary" className="ms-2">
                                            tối đa: {availableSeats}
                                        </Badge>
                                    )}
                                </Form.Label>
                                <Form.Control
                                    type="number"
                                    min="1"
                                    max={availableSeats || undefined}
                                    value={formData.ticketQuantity}
                                    onChange={(e) => setFormData(prev => ({ ...prev, ticketQuantity: e.target.value }))}
                                    required
                                    placeholder="vd: 100"
                                    disabled={availableSeats === 0}
                                />
                                {availableSeats > 0 && (
                                    <Form.Text className="text-muted">
                                        Còn {availableSeats} ghế trống
                                    </Form.Text>
                                )}
                            </Form.Group>
                        </Col>

                        <Col md={4}>
                            <Form.Group>
                                <Form.Label>Giá vé</Form.Label>
                                <InputGroup>
                                    <Form.Control
                                        type="number"
                                        min="1"
                                        step="1000"
                                        value={formData.specifiedFare}
                                        onChange={(e) => setFormData(prev => ({ ...prev, specifiedFare: e.target.value }))}
                                        required
                                        placeholder="vd: 1500000"
                                        disabled={availableSeats === 0}
                                    />
                                    <InputGroup.Text>VND</InputGroup.Text>
                                </InputGroup>
                            </Form.Group>
                        </Col>
                    </Row>

                    <div className="d-flex justify-content-end gap-2 mt-4">
                        <Button type="button" variant="secondary" onClick={onCancel}>
                            <i className="bi bi-x-circle me-1"></i>
                            Hủy
                        </Button>
                        <Button
                            type="submit"
                            variant="primary"
                            disabled={availableSeats === 0}
                        >
                            <i className="bi bi-plus-circle me-1"></i>
                            Thêm hạng vé
                        </Button>
                    </div>
                </Form>
            </Card.Body>
        </Card>
    );
};

// Ticket Class Card Component - Updated to show plane capacity info
interface TicketClassCardProps {
    association: FlightTicketClass;
    className: string;
    classColor: string;
    isEditing: boolean;
    onEdit: () => void;
    onSave: (data: Partial<FlightTicketClass>) => void;
    onCancel: () => void;
    onDelete: () => void;
    onUndo: () => void;
    isModified?: boolean;
    modifiedTicketClasses: Map<number, Partial<FlightTicketClass>>;
    selectedFlightForClasses: Flight | null;
    planes: Plane[];
    flightTicketClasses: FlightTicketClass[];
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
    onUndo,
    isModified = false,
    modifiedTicketClasses,
    selectedFlightForClasses,
    planes,
    flightTicketClasses
}) => {
    const [editData, setEditData] = useState({
        ticketQuantity: association.ticketQuantity || 0,
        specifiedFare: association.specifiedFare || 0,
        remainingTicketQuantity: association.remainingTicketQuantity || 0
    });
    const [validationError, setValidationError] = useState<string | null>(null);

    const soldSeats = (association.ticketQuantity || 0) - (association.remainingTicketQuantity || 0);
    const occupancyRate = association.ticketQuantity ?
        ((soldSeats / association.ticketQuantity) * 100).toFixed(1) : '0';

    const minTotalQuantity = soldSeats;

    // Calculate plane capacity info
    const getPlaneCapacityInfo = () => {
        if (!selectedFlightForClasses || planes.length === 0) return null;

        const plane = planes.find(p => p.planeId === selectedFlightForClasses.planeId);
        if (!plane) return null;

        const otherClassesTotalSeats = flightTicketClasses
            .filter(ftc => ftc.ticketClassId !== association.ticketClassId)
            .reduce((total, ftc) => {
                const modified = modifiedTicketClasses.get(ftc.ticketClassId!);
                const quantity = modified?.ticketQuantity !== undefined
                    ? modified.ticketQuantity
                    : ftc.ticketQuantity!;
                return total + quantity;
            }, 0);

        const currentClassSeats = isEditing ? editData.ticketQuantity : (association.ticketQuantity || 0);
        const totalAssignedSeats = otherClassesTotalSeats + currentClassSeats;
        const availableSeats = plane.seatQuantity - otherClassesTotalSeats;

        return {
            totalSeats: plane.seatQuantity,
            assignedSeats: totalAssignedSeats,
            availableSeats: availableSeats
        };
    };

    const planeInfo = getPlaneCapacityInfo();

    useEffect(() => {
        validateForm();
    }, [editData]);

    const validateForm = () => {
        setValidationError(null);

        const soldSeats = (association.ticketQuantity || 0) - (association.remainingTicketQuantity || 0);
        const minTotalQuantity = soldSeats;

        if (editData.ticketQuantity < minTotalQuantity) {
            setValidationError(`Tổng số ghế không thể ít hơn ${minTotalQuantity} (${soldSeats} vé đã bán không thể thay đổi)`);
            return;
        }

        const newRemainingQuantity = editData.ticketQuantity - soldSeats;

        if (newRemainingQuantity < 0) {
            setValidationError(`Số ghế còn lại không thể âm. Tối thiểu cần ${soldSeats} ghế cho các vé đã bán.`);
            return;
        }

        if (planeInfo && editData.ticketQuantity > planeInfo.availableSeats) {
            setValidationError(`Tổng số ghế không thể vượt quá ${planeInfo.availableSeats} (máy bay ${planeInfo.totalSeats} ghế, còn trống ${planeInfo.availableSeats} ghế).`);
            return;
        }

        const calculatedRemainingQuantity = editData.ticketQuantity - soldSeats;
        if (editData.remainingTicketQuantity !== calculatedRemainingQuantity) {
            setEditData(prev => ({
                ...prev,
                remainingTicketQuantity: calculatedRemainingQuantity
            }));
        }
    };

    const handleSave = () => {
        if (!validationError) {
            onSave(editData);
        }
    };

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
                        <Badge bg="warning" className="ms-2">Đã thay đổi</Badge>
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
                                Áp dụng
                            </Button>
                            <Button size="sm" variant="secondary" onClick={onCancel}>
                                Hủy
                            </Button>
                        </>
                    ) : (
                        <>
                            {isModified && (
                                <Button size="sm" variant="warning" onClick={onUndo} title="Hoàn tác thay đổi">
                                    <i className="bi bi-arrow-counterclockwise"></i>
                                </Button>
                            )}
                            <Button size="sm" variant="secondary" onClick={onEdit}>
                                Sửa
                            </Button>
                            <Button size="sm" variant="outline-danger" onClick={onDelete}>
                                Xóa
                            </Button>
                        </>
                    )}
                </div>
            </Card.Header>

            <Card.Body>
                {/* Show plane capacity info when editing */}
                {isEditing && planeInfo && (
                    <Alert variant="info" className="mb-3 small">
                        <div className="d-flex justify-content-between">
                            <span>
                                <i className="bi bi-airplane me-1"></i>
                                Máy bay: {planeInfo.totalSeats} ghế
                            </span>
                            <span>
                                <i className="bi bi-circle text-primary me-1"></i>
                                Còn trống: <strong>{planeInfo.availableSeats}</strong> ghế
                            </span>
                        </div>
                    </Alert>
                )}

                {isEditing ? (
                    <Row className="g-3">
                        <Col sm={6}>
                            <Form.Group>
                                <Form.Label>
                                    Tổng số ghế
                                    {planeInfo && (
                                        <small className="text-muted ms-2">
                                            (tối đa: {planeInfo.availableSeats})
                                        </small>
                                    )}
                                </Form.Label>
                                <Form.Control
                                    type="number"
                                    value={editData.ticketQuantity}
                                    onChange={(e) => setEditData(prev => ({
                                        ...prev,
                                        ticketQuantity: parseInt(e.target.value) || 0
                                    }))}
                                    min={minTotalQuantity}
                                    max={planeInfo?.availableSeats || undefined}
                                    isInvalid={!!validationError}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {validationError}
                                </Form.Control.Feedback>
                                <div className="text-muted small">
                                    Tối thiểu: {minTotalQuantity} ({soldSeats} đã bán)
                                </div>
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
                                    value={editData.ticketQuantity - soldSeats}
                                    disabled
                                    className="bg-light"
                                />
                                <div className="text-muted small">
                                    <i className="bi bi-lock me-1"></i>
                                    Tự động tính: Tổng ghế ({editData.ticketQuantity}) - Đã bán ({soldSeats}) = {editData.ticketQuantity - soldSeats}
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

                        {/* Show plane capacity info in view mode */}
                        {planeInfo && (
                            <Row className="g-2 mb-3">
                                <Col xs={12}>
                                    <div className="p-2 bg-light rounded small">
                                        <div className="d-flex justify-content-between">
                                            <span className="text-muted">
                                                <i className="bi bi-airplane me-1"></i>
                                                Sức chứa máy bay:
                                            </span>
                                            <span>
                                                <strong>{planeInfo.totalSeats}</strong> ghế tổng |
                                                <strong className="text-primary ms-1">{planeInfo.availableSeats}</strong> còn trống
                                            </span>
                                        </div>
                                    </div>
                                </Col>
                            </Row>
                        )}

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