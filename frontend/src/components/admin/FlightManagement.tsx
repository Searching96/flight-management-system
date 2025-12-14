import React, { useState, useEffect, useMemo } from "react";
import { useForm } from "react-hook-form";
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
  InputGroup,
} from "react-bootstrap";
import {
  airportService,
  planeService,
  ticketClassService,
  flightTicketClassService,
  parameterService,
} from "../../services";
import {
  Flight,
  Airport,
  Plane,
  TicketClass,
  FlightRequest,
  FlightTicketClass,
  FlightTicketClassRequest,
  UpdateFlightTicketClassRequest,
  Parameter,
} from "../../models";

// Internal draft type so we can queue create/update/delete before saving
type FlightTicketClassDraft = FlightTicketClass & {
  tempId?: number;
  isNew?: boolean;
  isDeleted?: boolean;
};
import { usePermissions } from "../../hooks/useAuth";
import { useFlights } from "../../hooks/useFlights";
import { useFlightDetails } from "../../hooks/useFlightDetails";
import FlightForm from "./flights/FlightForm";
import TypeAhead from "../common/TypeAhead";
import FlightTable from "./flights/FlightTable";

const FlightManagement: React.FC<{
  showAddModal?: boolean;
  onCloseAddModal?: () => void;
  readOnly?: boolean;
}> = ({ showAddModal = false, onCloseAddModal, readOnly = false }) => {
  const permissions = usePermissions();

  const {
    flights,
    loading,
    error: flightsError,
    loadFlights,
    createFlight,
    updateFlight,
    deleteFlight,
  } = useFlights();

  const {
    flightDetails,
    error: detailsError,
    loadFlightDetails,
    saveFlightDetails,
    addFlightDetail,
    removeFlightDetail,
    updateFlightDetail,
    clearDetails,
  } = useFlightDetails();

  const { reset } = useForm<FlightRequest>();

  // State for resources and UI
  const [airports, setAirports] = useState<Airport[]>([]);
  const [planes, setPlanes] = useState<Plane[]>([]);
  const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
  const [formErrors, setFormErrors] = useState<{ [key: string]: string }>({});
  const [detailErrors, setDetailErrors] = useState<{ [key: string]: string }>(
    {}
  );
  const [showForm, setShowForm] = useState(false);
  const [editingFlight, setEditingFlight] = useState<Flight | null>(null);
  const [error, setError] = useState("");

  // State for ticket class management
  const [selectedFlightForClasses, setSelectedFlightForClasses] =
    useState<Flight | null>(null);
  const [showTicketClassModal, setShowTicketClassModal] = useState(false);
  const [flightTicketClasses, setFlightTicketClasses] = useState<
    FlightTicketClassDraft[]
  >([]);
  const [originalTicketClasses, setOriginalTicketClasses] = useState<
    FlightTicketClassDraft[]
  >([]);
  const [modifiedTicketClasses, setModifiedTicketClasses] = useState<
    Map<number, Partial<FlightTicketClassDraft>>
  >(new Map());
  const [nextTempId, setNextTempId] = useState(-1);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingAssociation, setEditingAssociation] =
    useState<FlightTicketClassDraft | null>(null);
  const [ticketClassValidationError, setTicketClassValidationError] =
    useState<string>("");
  const [cardValidationErrors, setCardValidationErrors] = useState<
    Map<number, string>
  >(new Map());

  // System parameters
  const [parameters, setParameters] = useState<Parameter | null>(null);

  const [showDeleteFlightModal, setShowDeleteFlightModal] = useState(false);
  const [flightToDelete, setFlightToDelete] = useState<Flight | null>(null);
  const [showDeleteAssociationModal, setShowDeleteAssociationModal] =
    useState(false);
  const [associationToDelete, setAssociationToDelete] = useState<{
    flightId: number;
    ticketClassId: number;
    className: string;
  } | null>(null);
  const [deletingFlight, setDeletingFlight] = useState(false);
  const [deletingAssociation, setDeletingAssociation] = useState(false);

  // Initial data loading
  useEffect(() => {
    loadData();
  }, []);

  // Combine hook errors
  useEffect(() => {
    setError(flightsError || detailsError || "");
  }, [flightsError, detailsError]);

  // Handle external modal trigger
  useEffect(() => {
    if (showAddModal && !readOnly) {
      setShowForm(true);
    }
  }, [showAddModal, readOnly]);

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

  const loadData = async () => {
    try {
      const [airportData, planeData, ticketClassData, parameterData] =
        await Promise.all([
          airportService.getAllAirports(),
          planeService.getAllPlanes(),
          ticketClassService.getAllTicketClasses(),
          parameterService.getAllParameters(),
        ]);

      setAirports(airportData.data);
      setPlanes(planeData.data);
      setTicketClasses(ticketClassData.data);
      setParameters(parameterData); // Assuming first item contains all parameters

      await loadFlights();
    } catch (error) {
      console.log("Error loading data:", error);
      setError("Failed to load data");
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
      let errorMsg = err.message || "Không thể lưu chuyến bay";

      if (
        err.status === 400 &&
        err.message?.includes(
          "Departure and arrival airports cannot be the same"
        )
      ) {
        errorMsg = "Sân bay đi và sân bay đến không thể là cùng một sân bay.";
        setFormErrors((prev) => ({ ...prev, airports: errorMsg }));
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

  const handleDeleteClick = (flight: Flight) => {
    setFlightToDelete(flight);
    setShowDeleteFlightModal(true);
  };

  const handleConfirmDeleteFlight = async () => {
    if (!flightToDelete) return;

    try {
      setDeletingFlight(true);
      await deleteFlight(flightToDelete.flightId!);
      setShowDeleteFlightModal(false);
      setFlightToDelete(null);
    } catch (err: any) {
      setError(err.message || "Không thể xóa chuyến bay");
    } finally {
      setDeletingFlight(false);
    }
  };

  const handleDelete = async (flightId: number) => {
    // Find the flight to delete for modal display
    const flight = flights.find((f) => f.flightId === flightId);
    if (flight) {
      handleDeleteClick(flight);
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingFlight(null);
    clearDetails();
    setFormErrors({});
    setDetailErrors({});
    reset();
    setError("");

    if (onCloseAddModal) {
      onCloseAddModal();
    }
  };

  const handleAddFlightDetail = () => {
    addFlightDetail(editingFlight?.flightId || 0);

    if (error.includes("Lỗi: Sân bay")) {
      setError("");
    }
    setDetailErrors((prev) => {
      const { flightDetails, ...rest } = prev;
      return rest;
    });
  };

  // TICKET CLASS MANAGEMENT FUNCTIONS
  const loadFlightTicketClasses = async (flightId: number) => {
    try {
      const data =
        await flightTicketClassService.getFlightTicketClassesByFlightId(
          flightId
        );
      setOriginalTicketClasses(data);
      setFlightTicketClasses(data);
      setModifiedTicketClasses(new Map());
      setCardValidationErrors(new Map());
    } catch (error) {
      setError("Failed to load flight ticket classes");
      console.log("Error loading flight ticket classes:", error);
    }
  };

  const handleManageTicketClasses = async (flight: Flight) => {
    try {
      setSelectedFlightForClasses(flight);
      await loadFlightTicketClasses(flight.flightId!);
      setShowTicketClassModal(true);
      setTicketClassValidationError("");
    } catch (error) {
      setError("Failed to load ticket class data");
      console.log("Error loading ticket class data:", error);
    }
  };

  const handleTicketClassUpdate = (
    ticketClassId: number,
    data: Partial<FlightTicketClassDraft>
  ) => {
    setFlightTicketClasses((prev) =>
      prev.map((ftc) =>
        ftc.ticketClassId === ticketClassId
          ? { ...ftc, ...data, isDeleted: false }
          : ftc
      )
    );

    const originalAssociation = originalTicketClasses.find(
      (ftc) => ftc.ticketClassId === ticketClassId
    );

    // New rows do not need diff-tracking against originals
    if (!originalAssociation) {
      setModifiedTicketClasses((prev) => {
        const newMap = new Map(prev);
        newMap.set(ticketClassId, data);
        return newMap;
      });
      setEditingAssociation(null);
      return;
    }

    const changedFields: Partial<FlightTicketClassDraft> = {};

    if (
      data.ticketQuantity !== undefined &&
      data.ticketQuantity !== originalAssociation.ticketQuantity
    ) {
      changedFields.ticketQuantity = data.ticketQuantity;
    }

    if (
      data.specifiedFare !== undefined &&
      data.specifiedFare !== originalAssociation.specifiedFare
    ) {
      changedFields.specifiedFare = data.specifiedFare;
    }

    if (
      data.remainingTicketQuantity !== undefined &&
      data.remainingTicketQuantity !==
        originalAssociation.remainingTicketQuantity
    ) {
      changedFields.remainingTicketQuantity = data.remainingTicketQuantity;
    }

    setModifiedTicketClasses((prev) => {
      const newMap = new Map(prev);
      if (Object.keys(changedFields).length === 0) {
        newMap.delete(ticketClassId);
      } else {
        newMap.set(ticketClassId, changedFields);
      }
      return newMap;
    });

    setEditingAssociation(null);
  };

  const handleCardValidationError = (
    ticketClassId: number,
    error: string | null
  ) => {
    setCardValidationErrors((prev) => {
      const newMap = new Map(prev);
      if (error) {
        newMap.set(ticketClassId, error);
      } else {
        newMap.delete(ticketClassId);
      }
      return newMap;
    });
  };

  const handleUndoTicketClassChanges = (ticketClassId: number) => {
    setFlightTicketClasses((prev) => {
      const draft = prev.find((ftc) => ftc.ticketClassId === ticketClassId);
      if (!draft) return prev;

      // Remove newly added rows entirely
      if (draft.isNew) {
        return prev.filter((ftc) => ftc.ticketClassId !== ticketClassId);
      }

      const original = originalTicketClasses.find(
        (ftc) => ftc.ticketClassId === ticketClassId
      );
      if (!original) return prev;

      return prev.map((ftc) =>
        ftc.ticketClassId === ticketClassId ? { ...original } : ftc
      );
    });

    setModifiedTicketClasses((prev) => {
      const newMap = new Map(prev);
      newMap.delete(ticketClassId);
      return newMap;
    });

    setCardValidationErrors((prev) => {
      const newMap = new Map(prev);
      newMap.delete(ticketClassId);
      return newMap;
    });
  };

  const handleSaveAllTicketClasses = async () => {
    if (!selectedFlightForClasses) return;

    try {
      const plane = planes.find(
        (p) => p.planeId === selectedFlightForClasses.planeId
      );
      if (!plane) {
        setTicketClassValidationError("Could not find the plane information");
        return;
      }

      const activeClasses = flightTicketClasses.filter((ftc) => !ftc.isDeleted);
      const totalAssignedSeats = activeClasses.reduce(
        (total, ftc) => total + (ftc.ticketQuantity || 0),
        0
      );

      if (totalAssignedSeats > plane.seatQuantity) {
        setTicketClassValidationError(
          `Tổng số ghế của các hạng vé (${totalAssignedSeats}) không thể vượt quá sức chứa của máy bay (${plane.seatQuantity}).`
        );
        return;
      }

      if (cardValidationErrors.size > 0) {
        setTicketClassValidationError(
          "Vui lòng sửa lỗi trên các thẻ hạng vé trước khi lưu."
        );
        return;
      }

      const originalMap = new Map(
        originalTicketClasses.map((ftc) => [ftc.ticketClassId, ftc])
      );

      const creates = flightTicketClasses.filter(
        (ftc) => ftc.isNew && !ftc.isDeleted
      );
      const deletes = flightTicketClasses.filter(
        (ftc) => ftc.isDeleted && !ftc.isNew
      );
      const updates = flightTicketClasses.filter((ftc) => {
        if (ftc.isNew || ftc.isDeleted) return false;
        const original = originalMap.get(ftc.ticketClassId);
        if (!original) return false;
        return (
          ftc.ticketQuantity !== original.ticketQuantity ||
          ftc.specifiedFare !== original.specifiedFare ||
          ftc.remainingTicketQuantity !== original.remainingTicketQuantity
        );
      });

      const requests: Promise<unknown>[] = [];

      creates.forEach((ftc) => {
        const request: FlightTicketClassRequest = {
          flightId: selectedFlightForClasses.flightId!,
          ticketClassId: ftc.ticketClassId,
          ticketQuantity: ftc.ticketQuantity,
          remainingTicketQuantity: ftc.remainingTicketQuantity,
          specifiedFare: ftc.specifiedFare,
        };
        requests.push(flightTicketClassService.createFlightTicketClass(request));
      });

      updates.forEach((ftc) => {
        const updateRequest: UpdateFlightTicketClassRequest = {
          ticketQuantity: ftc.ticketQuantity,
          specifiedFare: ftc.specifiedFare,
          remainingTicketQuantity: ftc.remainingTicketQuantity,
        };
        requests.push(
          flightTicketClassService.updateFlightTicketClass(
            selectedFlightForClasses.flightId!,
            ftc.ticketClassId,
            updateRequest
          )
        );
      });

      deletes.forEach((ftc) => {
        requests.push(
          flightTicketClassService.deleteFlightTicketClass(
            ftc.flightId,
            ftc.ticketClassId
          )
        );
      });

      await Promise.all(requests);

      await loadFlightTicketClasses(selectedFlightForClasses.flightId!);
      setModifiedTicketClasses(new Map());
      setTicketClassValidationError("");
      setError("");
    } catch (err: any) {
      setError("Failed to save ticket class changes: " + err.message);
    }
  };

  const handleCreateAssociation = (data: {
    ticketClassId: number;
    ticketQuantity: number;
    specifiedFare: number;
  }) => {
    if (!selectedFlightForClasses) return;

    const newDraft: FlightTicketClassDraft = {
      flightId: selectedFlightForClasses.flightId!,
      ticketClassId: data.ticketClassId,
      ticketQuantity: data.ticketQuantity,
      remainingTicketQuantity: data.ticketQuantity,
      specifiedFare: data.specifiedFare,
      isNew: true,
      tempId: nextTempId,
    };

    setNextTempId((prev) => prev - 1);
    setFlightTicketClasses((prev) => [...prev, newDraft]);
    setModifiedTicketClasses((prev) => {
      const newMap = new Map(prev);
      newMap.set(newDraft.ticketClassId, {
        ticketQuantity: data.ticketQuantity,
        remainingTicketQuantity: data.ticketQuantity,
        specifiedFare: data.specifiedFare,
      });
      return newMap;
    });
    setShowCreateForm(false);
    setError("");
  };

  const handleDeleteAssociationClick = (
    flightId: number,
    ticketClassId: number
  ) => {
    const className = getTicketClassName(ticketClassId);
    setAssociationToDelete({ flightId, ticketClassId, className });
    setShowDeleteAssociationModal(true);
  };

  const handleConfirmDeleteAssociation = () => {
    if (!associationToDelete) return;

    setDeletingAssociation(true);

    setFlightTicketClasses((prev) => {
      const draft = prev.find(
        (ftc) => ftc.ticketClassId === associationToDelete.ticketClassId
      );
      if (!draft) return prev;

      // Remove unsaved new draft entirely
      if (draft.isNew) {
        return prev.filter(
          (ftc) => ftc.ticketClassId !== associationToDelete.ticketClassId
        );
      }

      return prev.map((ftc) =>
        ftc.ticketClassId === associationToDelete.ticketClassId
          ? { ...ftc, isDeleted: true }
          : ftc
      );
    });

    setModifiedTicketClasses((prev) => {
      const newMap = new Map(prev);
      newMap.set(associationToDelete.ticketClassId, {
        ticketQuantity: 0,
      });
      return newMap;
    });

    setCardValidationErrors((prev) => {
      const newMap = new Map(prev);
      newMap.delete(associationToDelete.ticketClassId);
      return newMap;
    });

    setShowDeleteAssociationModal(false);
    setAssociationToDelete(null);
    setDeletingAssociation(false);
  };

  const getTicketClassName = (ticketClassId: number) => {
    return (
      ticketClasses.find((tc) => tc.ticketClassId === ticketClassId)
        ?.ticketClassName || "Unknown"
    );
  };

  const getTicketClassColor = (ticketClassId: number) => {
    return (
      ticketClasses.find((tc) => tc.ticketClassId === ticketClassId)?.color ||
      "#ccc"
    );
  };

  const handleCancelTicketClasses = () => {
    setShowTicketClassModal(false);
    setSelectedFlightForClasses(null);
    setFlightTicketClasses(originalTicketClasses);
    setShowCreateForm(false);
    setEditingAssociation(null);
    setCardValidationErrors(new Map());
    setModifiedTicketClasses(new Map());
    setError("");
  };

  const handleFlightDetailChange = (
    index: number,
    field: string,
    value: any
  ) => {
    updateFlightDetail(index, field, value);
  };

  const availableTicketClasses = selectedFlightForClasses
    ? ticketClasses.filter(
        (tc) =>
          !flightTicketClasses.some(
            (ftc) => !ftc.isDeleted && ftc.ticketClassId === tc.ticketClassId
          )
      )
    : [];

  const activeTicketClasses = flightTicketClasses.filter((ftc) => !ftc.isDeleted);
  const totalAssignedSeats = activeTicketClasses.reduce(
    (total, ftc) => total + (ftc.ticketQuantity || 0),
    0
  );
  const planeCapacity = selectedFlightForClasses
    ? planes.find((p) => p.planeId === selectedFlightForClasses.planeId)
        ?.seatQuantity
    : undefined;
  const isPlaneFull =
    planeCapacity !== undefined && totalAssignedSeats >= planeCapacity;
  const hasPendingChanges =
    modifiedTicketClasses.size > 0 ||
    flightTicketClasses.some((ftc) => ftc.isNew || ftc.isDeleted);

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
      {/* Read-only mode alert */}
      {readOnly && (
        <Row className="mb-4">
          <Col>
            <Alert variant="info" className="text-center">
              <Alert.Heading>Chế độ chỉ xem</Alert.Heading>
              <p className="mb-0">
                Bạn đang xem danh sách chuyến bay. Không thể chỉnh sửa trong chế
                độ này.
              </p>
            </Alert>
          </Col>
        </Row>
      )}

      {/* Header */}
      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Header className="d-flex justify-content-between align-items-center">
              <Card.Title className="mb-0">
                ✈️ {readOnly ? "Tra cứu chuyến bay" : "Quản lý chuyến bay"}
              </Card.Title>
              {!readOnly && (
                <Button variant="primary" onClick={() => setShowForm(true)}>
                  Thêm chuyến bay mới
                </Button>
              )}
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
      <Modal show={showForm && !readOnly} onHide={handleCancel} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingFlight ? "Sửa chuyến bay" : "Thêm chuyến bay mới"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {error && error.includes("sân bay") && (
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
            {readOnly ? "Thông tin hạng vé" : "Hạng vé cho"} chuyến bay{" "}
            {selectedFlightForClasses?.flightCode}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {/* Read-only mode alert in modal */}
          {readOnly && (
            <Alert variant="info" className="mb-3">
              <i className="bi bi-info-circle me-2"></i>
              Chế độ chỉ xem - không thể thay đổi thông tin hạng vé
            </Alert>
          )}

          {/* Flight Route Info */}
          <Row className="align-items-center mb-3">
            <Col>
              <h5 className="mb-0">
                Tuyến bay: {selectedFlightForClasses?.departureCityName} →{" "}
                {selectedFlightForClasses?.arrivalCityName}
              </h5>
            </Col>
            <Col md="auto">
              {!readOnly && availableTicketClasses.length > 0 && (
                <Button
                  variant="primary"
                  disabled={!selectedFlightForClasses || isPlaneFull}
                  onClick={() => setShowCreateForm(true)}
                >
                  <i className="bi bi-plus-circle me-2"></i>
                  {isPlaneFull ? "Máy bay đã đầy" : "Thêm hạng vé"}
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
            />
          )}

          {/* Validation Errors */}
          {!readOnly && ticketClassValidationError && (
            <Alert variant="danger" className="mb-3">
              <i className="bi bi-exclamation-triangle-fill me-2"></i>
              {ticketClassValidationError}
            </Alert>
          )}

          {/* Pending Changes Alert */}
          {!readOnly && hasPendingChanges && (
            <Alert variant="warning" className="mb-3">
              <i className="bi bi-info-circle-fill me-2"></i>
              Bạn có thay đổi chưa lưu. Nhấn "Lưu thay đổi" để áp dụng.
            </Alert>
          )}

          {/* Create Association Form */}
          {!readOnly && showCreateForm && (
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
            {flightTicketClasses
              .filter((ftc) => !ftc.isDeleted)
              .map((association) => (
              <Col
                md={6}
                key={`${association.flightId}-${association.ticketClassId}`}
              >
                <TicketClassCard
                  association={association}
                  className={getTicketClassName(association.ticketClassId!)}
                  classColor={getTicketClassColor(association.ticketClassId!)}
                  isEditing={
                    !readOnly &&
                    editingAssociation?.ticketClassId ===
                      association.ticketClassId
                  }
                  onEdit={() => !readOnly && setEditingAssociation(association)}
                  onSave={(data) =>
                    handleTicketClassUpdate(association.ticketClassId!, data)
                  }
                  onCancel={() => setEditingAssociation(null)}
                  onDelete={() =>
                    !readOnly &&
                    handleDeleteAssociationClick(
                      association.flightId!,
                      association.ticketClassId!
                    )
                  }
                  onUndo={() =>
                    handleUndoTicketClassChanges(association.ticketClassId!)
                  }
                  isModified={
                    association.isNew ||
                    modifiedTicketClasses.has(association.ticketClassId!)
                  }
                  modifiedTicketClasses={modifiedTicketClasses}
                  selectedFlightForClasses={selectedFlightForClasses}
                  planes={planes}
                  flightTicketClasses={flightTicketClasses}
                  onValidationChange={(error) =>
                    handleCardValidationError(association.ticketClassId!, error)
                  }
                  readOnly={readOnly}
                />
              </Col>
            ))}
          </Row>

          {/* No Ticket Classes Message */}
          {activeTicketClasses.length === 0 && (
            <Alert variant="info" className="text-center">
              <Alert.Heading>Chưa có hạng vé nào</Alert.Heading>
              <p className="mb-0">
                {readOnly
                  ? "Chuyến bay này chưa có hạng vé nào được cấu hình."
                  : "Thêm hạng vé để cho phép đặt chỗ cho chuyến bay này."}
              </p>
            </Alert>
          )}
        </Modal.Body>
        <Modal.Footer className="d-flex justify-content-between">
          <Button variant="secondary" onClick={handleCancelTicketClasses}>
            Đóng
          </Button>
          {!readOnly && (
            <Button
              variant="success"
              onClick={handleSaveAllTicketClasses}
              disabled={!hasPendingChanges || cardValidationErrors.size > 0}
            >
              <i className="bi bi-save me-2"></i>
              Lưu thay đổi
            </Button>
          )}
        </Modal.Footer>
      </Modal>

      {/* Flights table - REPLACED WITH NEW COMPONENT */}
      <Row>
        <Col>
          <FlightTable
            flights={flights}
            onEdit={handleEdit}
            onDelete={handleDelete}
            onManageTicketClasses={handleManageTicketClasses}
            readOnly={readOnly}
          />
        </Col>
      </Row>

      {/* Flight Delete Confirmation Modal */}
      {!readOnly && (
        <Modal
          show={showDeleteFlightModal}
          onHide={() => !deletingFlight && setShowDeleteFlightModal(false)}
          centered
        >
          <Modal.Header closeButton className="bg-danger text-white">
            <Modal.Title>
              <i className="bi bi-exclamation-triangle me-2"></i>
              Xác nhận xóa chuyến bay
            </Modal.Title>
          </Modal.Header>
          <Modal.Body className="p-4">
            <div className="text-center mb-3">
              <i
                className="bi bi-airplane text-danger"
                style={{ fontSize: "3rem" }}
              ></i>
            </div>
            <h5 className="text-center mb-3">
              Bạn có chắc chắn muốn xóa chuyến bay này không?
            </h5>
            {flightToDelete && (
              <div className="p-3 bg-light rounded mb-3">
                <div className="text-center">
                  <strong>{flightToDelete.flightCode}</strong>
                  <br />
                  <span className="text-muted">
                    {flightToDelete.departureCityName} →{" "}
                    {flightToDelete.arrivalCityName}
                  </span>
                  <br />
                  <small className="text-muted">
                    {new Date(flightToDelete.departureTime).toLocaleString(
                      "vi-VN"
                    )}
                  </small>
                </div>
              </div>
            )}
            <p className="text-center text-muted mb-0">
              Hành động này không thể hoàn tác. Chuyến bay và tất cả dữ liệu
              liên quan (vé đã đặt, thông tin hành khách) sẽ bị xóa vĩnh viễn.
            </p>
          </Modal.Body>
          <Modal.Footer>
            <Button
              variant="secondary"
              onClick={() => setShowDeleteFlightModal(false)}
              disabled={deletingFlight}
            >
              Hủy
            </Button>
            <Button
              variant="danger"
              onClick={handleConfirmDeleteFlight}
              disabled={deletingFlight}
            >
              {deletingFlight ? (
                <>
                  <Spinner animation="border" size="sm" className="me-2" />
                  Đang xóa...
                </>
              ) : (
                <>
                  <i className="bi bi-trash me-2"></i>
                  Có, xóa chuyến bay
                </>
              )}
            </Button>
          </Modal.Footer>
        </Modal>
      )}

      {/* Association Delete Confirmation Modal */}
      {!readOnly && (
        <Modal
          show={showDeleteAssociationModal}
          onHide={() =>
            !deletingAssociation && setShowDeleteAssociationModal(false)
          }
          centered
        >
          <Modal.Header closeButton className="bg-danger text-white">
            <Modal.Title>
              <i className="bi bi-exclamation-triangle me-2"></i>
              Xác nhận xóa hạng vé
            </Modal.Title>
          </Modal.Header>
          <Modal.Body className="p-4">
            <div className="text-center mb-3">
              <i
                className="bi bi-ticket text-danger"
                style={{ fontSize: "3rem" }}
              ></i>
            </div>
            <h5 className="text-center mb-3">
              Bạn có chắc chắn muốn xóa hạng vé này không?
            </h5>
            {associationToDelete && (
              <div className="p-3 bg-light rounded mb-3">
                <div className="text-center">
                  <strong>{associationToDelete.className}</strong>
                  <br />
                  <span className="text-muted">
                    cho chuyến bay {selectedFlightForClasses?.flightCode}
                  </span>
                </div>
              </div>
            )}
            <p className="text-center text-muted mb-0">
              Hành động này không thể hoàn tác. Hạng vé sẽ bị xóa khỏi chuyến
              bay này và tất cả vé đã đặt cho hạng này sẽ bị ảnh hưởng.
            </p>
          </Modal.Body>
          <Modal.Footer>
            <Button
              variant="secondary"
              onClick={() => setShowDeleteAssociationModal(false)}
              disabled={deletingAssociation}
            >
              Hủy
            </Button>
            <Button
              variant="danger"
              onClick={handleConfirmDeleteAssociation}
              disabled={deletingAssociation}
            >
              {deletingAssociation ? (
                <>
                  <Spinner animation="border" size="sm" className="me-2" />
                  Đang xóa...
                </>
              ) : (
                <>
                  <i className="bi bi-trash me-2"></i>
                  Có, xóa hạng vé
                </>
              )}
            </Button>
          </Modal.Footer>
        </Modal>
      )}
    </Container>
  );
};

// Plane Information Card Component
interface PlaneInfoCardProps {
  selectedFlight: Flight;
  planes: Plane[];
  flightTicketClasses: FlightTicketClassDraft[];
}

const PlaneInfoCard: React.FC<PlaneInfoCardProps> = ({
  selectedFlight,
  planes,
  flightTicketClasses,
}) => {
  const planeInfo = useMemo(() => {
    const plane = planes.find((p) => p.planeId === selectedFlight.planeId);
    if (!plane) return null;

    // Calculate assigned seats including pending modifications
    const totalAssignedSeats = flightTicketClasses
      .filter((ftc) => !ftc.isDeleted)
      .reduce((total, ftc) => total + (ftc.ticketQuantity || 0), 0);

    const availableSeats = plane.seatQuantity - totalAssignedSeats;
    const utilizationRate = (
      (totalAssignedSeats / plane.seatQuantity) *
      100
    ).toFixed(1);

    return {
      planeCode: plane.planeCode,
      planeType: plane.planeType,
      totalSeats: plane.seatQuantity,
      assignedSeats: totalAssignedSeats,
      availableSeats: availableSeats,
      utilizationRate: utilizationRate,
    };
  }, [selectedFlight, planes, flightTicketClasses]);

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
          <div className="progress" style={{ height: "8px" }}>
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
  onSubmit: (data: {
    ticketClassId: number;
    ticketQuantity: number;
    specifiedFare: number;
  }) => void;
  onCancel: () => void;
  selectedFlightForClasses: Flight | null;
  planes: Plane[];
  flightTicketClasses: FlightTicketClassDraft[];
}

const CreateAssociationFormWithTypeAhead: React.FC<
  CreateAssociationFormProps
> = ({
  availableClasses,
  onSubmit,
  onCancel,
  selectedFlightForClasses,
  planes,
  flightTicketClasses,
}) => {
  const [selectedTicketClass, setSelectedTicketClass] = useState<number | "">(
    ""
  );
  const [formData, setFormData] = useState({
    ticketQuantity: "",
    specifiedFare: "",
  });
  const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});

  const ticketClassOptions = availableClasses.map((tc) => ({
    value: tc.ticketClassId!,
    label: tc.ticketClassName,
    color: tc.color,
  }));

  // Calculate available seats for validation
  const availableSeats = useMemo(() => {
    if (!selectedFlightForClasses || planes.length === 0) return 0;

    const plane = planes.find(
      (p) => p.planeId === selectedFlightForClasses.planeId
    );
    if (!plane) return 0;

    const totalAssignedSeats = flightTicketClasses
      .filter((ftc) => !ftc.isDeleted)
      .reduce((total, ftc) => total + (ftc.ticketQuantity || 0), 0);

    return plane.seatQuantity - totalAssignedSeats;
  }, [selectedFlightForClasses, planes, flightTicketClasses]);

  // Live validation for ticket quantity
  const validateTicketQuantity = (value: string) => {
    const quantity = Number(value);

    if (!value) {
      return "Số lượng ghế là bắt buộc";
    }

    if (quantity <= 0) {
      return "Số lượng ghế phải lớn hơn 0";
    }

    if (quantity > availableSeats) {
      return `Số lượng ghế không thể vượt quá ${availableSeats} ghế còn lại của máy bay`;
    }

    return "";
  };

  // Live validation for fare
  const validateFare = (value: string) => {
    const fare = Number(value);

    if (!value) {
      return "Giá vé là bắt buộc";
    }

    if (fare <= 0) {
      return "Giá vé phải lớn hơn 0";
    }

    return "";
  };

  // Live validation for ticket class
  const validateTicketClass = (value: number | "") => {
    if (!value) {
      return "Hạng vé là bắt buộc";
    }
    return "";
  };

  // Handle field changes with live validation
  const handleTicketQuantityChange = (value: string) => {
    setFormData((prev) => ({ ...prev, ticketQuantity: value }));
    const error = validateTicketQuantity(value);
    setFieldErrors((prev) => ({ ...prev, ticketQuantity: error }));
  };

  const handleFareChange = (value: string) => {
    setFormData((prev) => ({ ...prev, specifiedFare: value }));
    const error = validateFare(value);
    setFieldErrors((prev) => ({ ...prev, specifiedFare: error }));
  };

  const handleTicketClassChange = (option: any) => {
    const value = (option?.value as number) || "";
    setSelectedTicketClass(value);
    const error = validateTicketClass(value);
    setFieldErrors((prev) => ({ ...prev, ticketClass: error }));
  };

  // Check if form is valid
  const isFormValid = useMemo(() => {
    const hasAllFields =
      selectedTicketClass && formData.ticketQuantity && formData.specifiedFare;
    const hasNoErrors = !Object.values(fieldErrors).some((error) => error);
    return hasAllFields && hasNoErrors;
  }, [selectedTicketClass, formData, fieldErrors]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Final validation check
    const ticketClassError = validateTicketClass(selectedTicketClass);
    const quantityError = validateTicketQuantity(formData.ticketQuantity);
    const fareError = validateFare(formData.specifiedFare);

    setFieldErrors({
      ticketClass: ticketClassError,
      ticketQuantity: quantityError,
      specifiedFare: fareError,
    });

    if (ticketClassError || quantityError || fareError) {
      return;
    }

    onSubmit({
      ticketClassId: Number(selectedTicketClass),
      ticketQuantity: Number(formData.ticketQuantity),
      specifiedFare: Number(formData.specifiedFare),
    });

    // Reset form
    setSelectedTicketClass("");
    setFormData({ ticketQuantity: "", specifiedFare: "" });
    setFieldErrors({});
  };

  return (
    <Card className="mb-4">
      <Card.Header>
        <Card.Title as="h4" className="mb-0">
          Thêm hạng vé
        </Card.Title>
      </Card.Header>
      <Card.Body>
        <Form onSubmit={handleSubmit}>
          <Row className="g-3">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Hạng vé</Form.Label>
                <TypeAhead
                  options={ticketClassOptions}
                  value={selectedTicketClass}
                  onChange={handleTicketClassChange}
                  placeholder="Tìm hạng vé..."
                />
                {fieldErrors.ticketClass && (
                  <div className="text-danger small mt-1">
                    {fieldErrors.ticketClass}
                  </div>
                )}
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
                  onChange={(e) => handleTicketQuantityChange(e.target.value)}
                  placeholder="vd: 100"
                  disabled={availableSeats === 0}
                  isInvalid={!!fieldErrors.ticketQuantity}
                />
                <Form.Control.Feedback type="invalid">
                  {fieldErrors.ticketQuantity}
                </Form.Control.Feedback>
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Giá vé</Form.Label>
                <InputGroup>
                  <Form.Control
                    type="number"
                    min="1"
                    value={formData.specifiedFare}
                    onChange={(e) => handleFareChange(e.target.value)}
                    disabled={availableSeats === 0}
                    isInvalid={!!fieldErrors.specifiedFare}
                  />
                  <InputGroup.Text>VND</InputGroup.Text>
                </InputGroup>
                {fieldErrors.specifiedFare && (
                  <div className="text-danger small mt-1">
                    {fieldErrors.specifiedFare}
                  </div>
                )}
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
              disabled={!isFormValid || availableSeats === 0}
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
  association: FlightTicketClassDraft;
  className: string;
  classColor: string;
  isEditing: boolean;
  onEdit: () => void;
  onSave: (data: Partial<FlightTicketClassDraft>) => void;
  onCancel: () => void;
  onDelete: () => void;
  onUndo: () => void;
  isModified?: boolean;
  modifiedTicketClasses: Map<number, Partial<FlightTicketClassDraft>>;
  selectedFlightForClasses: Flight | null;
  planes: Plane[];
  flightTicketClasses: FlightTicketClassDraft[];
  onValidationChange: (error: string | null) => void;
  readOnly?: boolean;
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
  flightTicketClasses,
  onValidationChange,
  readOnly = false,
}) => {
  const [editData, setEditData] = useState({
    ticketQuantity: association.ticketQuantity || 0,
    specifiedFare: association.specifiedFare || 0,
    remainingTicketQuantity: association.remainingTicketQuantity || 0,
  });
  const [validationError, setValidationError] = useState<string | null>(null);

  // Sync editData with association prop changes (e.g., after undo)
  useEffect(() => {
    setEditData({
      ticketQuantity: association.ticketQuantity || 0,
      specifiedFare: association.specifiedFare || 0,
      remainingTicketQuantity: association.remainingTicketQuantity || 0,
    });
  }, [association.ticketQuantity, association.specifiedFare, association.remainingTicketQuantity]);

  const soldSeats =
    (association.ticketQuantity || 0) -
    (association.remainingTicketQuantity || 0);
  const occupancyRate = association.ticketQuantity
    ? ((soldSeats / association.ticketQuantity) * 100).toFixed(1)
    : "0";

  const minTotalQuantity = soldSeats;

  // Calculate plane capacity info
  const getPlaneCapacityInfo = () => {
    if (!selectedFlightForClasses || planes.length === 0) return null;

    const plane = planes.find(
      (p) => p.planeId === selectedFlightForClasses.planeId
    );
    if (!plane) return null;

    const otherClassesTotalSeats = flightTicketClasses
      .filter(
        (ftc) =>
          !ftc.isDeleted && ftc.ticketClassId !== association.ticketClassId
      )
      .reduce((total, ftc) => total + (ftc.ticketQuantity || 0), 0);

    const currentClassSeats = isEditing
      ? editData.ticketQuantity
      : association.ticketQuantity || 0;
    const totalAssignedSeats = otherClassesTotalSeats + currentClassSeats;
    const availableSeats = plane.seatQuantity - otherClassesTotalSeats;

    return {
      totalSeats: plane.seatQuantity,
      assignedSeats: totalAssignedSeats,
      availableSeats: availableSeats,
    };
  };

  const planeInfo = getPlaneCapacityInfo();

  useEffect(() => {
    validateForm();
  }, [editData]);

  const validateForm = () => {
    setValidationError(null);

    const soldSeats =
      (association.ticketQuantity || 0) -
      (association.remainingTicketQuantity || 0);
    const minTotalQuantity = soldSeats;

    if (editData.ticketQuantity < minTotalQuantity) {
      const error = `Tổng số ghế không thể ít hơn ${minTotalQuantity} (${soldSeats} vé đã bán không thể thay đổi)`;
      setValidationError(error);
      onValidationChange(error);
      return;
    }

    const newRemainingQuantity = editData.ticketQuantity - soldSeats;

    if (newRemainingQuantity < 0) {
      const error = `Số ghế còn lại không thể âm. Tối thiểu cần ${soldSeats} ghế cho các vé đã bán.`;
      setValidationError(error);
      onValidationChange(error);
      return;
    }

    if (planeInfo && editData.ticketQuantity > planeInfo.availableSeats) {
      const error = `Tổng số ghế không thể vượt quá ${planeInfo.availableSeats} (máy bay ${planeInfo.totalSeats} ghế, còn trống ${planeInfo.availableSeats} ghế).`;
      setValidationError(error);
      onValidationChange(error);
      return;
    }

    onValidationChange(null);

    const calculatedRemainingQuantity = editData.ticketQuantity - soldSeats;
    if (editData.remainingTicketQuantity !== calculatedRemainingQuantity) {
      setEditData((prev) => ({
        ...prev,
        remainingTicketQuantity: calculatedRemainingQuantity,
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
    ...(isModified ? { boxShadow: "0 0 0 2px rgba(255, 193, 7, 0.5)" } : {}),
  };

  return (
    <Card className="h-100" style={cardStyle}>
      <Card.Header className="d-flex justify-content-between align-items-center">
        <div className="d-flex align-items-center">
          <Card.Title as="h5" className="mb-0" style={{ color: classColor }}>
            {className}
          </Card.Title>
          {isModified && !readOnly && (
            <Badge bg={association.isNew ? "success" : "warning"} className="ms-2">
              {association.isNew ? "Mới" : "Đã thay đổi"}
            </Badge>
          )}
        </div>
        {!readOnly && (
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
                  <Button
                    size="sm"
                    variant="warning"
                    onClick={onUndo}
                    title="Hoàn tác thay đổi"
                  >
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
        )}
      </Card.Header>

      <Card.Body>
        {!isEditing ? (
          // View mode (both readOnly and non-editing states)
          <>
            <Row className="g-2 mb-3">
              <Col xs={6}>
                <div className="d-flex justify-content-between">
                  <span className="text-muted">Tổng số ghế:</span>
                  <span>
                    <strong>{association.ticketQuantity}</strong>
                    {isModified &&
                      modifiedTicketClasses.get(association.ticketClassId!)
                        ?.ticketQuantity !== undefined && (
                        <Badge bg="warning" pill className="ms-2">
                          →
                          {
                            modifiedTicketClasses.get(
                              association.ticketClassId!
                            )?.ticketQuantity
                          }
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
                    {isModified &&
                      modifiedTicketClasses.get(association.ticketClassId!)
                        ?.remainingTicketQuantity !== undefined && (
                        <Badge bg="warning" pill className="ms-2">
                          →
                          {
                            modifiedTicketClasses.get(
                              association.ticketClassId!
                            )?.remainingTicketQuantity
                          }
                        </Badge>
                      )}
                  </span>
                </div>
              </Col>
              <Col xs={6}>
                <div className="d-flex justify-content-between">
                  <span className="text-muted">Giá vé:</span>
                  <span>
                    <strong>
                      {association.specifiedFare?.toLocaleString()} VND
                    </strong>
                    {isModified &&
                      modifiedTicketClasses.get(association.ticketClassId!)
                        ?.specifiedFare !== undefined && (
                        <Badge bg="warning" pill className="ms-2">
                          →
                          {modifiedTicketClasses
                            .get(association.ticketClassId!)
                            ?.specifiedFare?.toLocaleString()}{" "}
                          VND
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
                        <strong className="text-primary ms-1">
                          {planeInfo.availableSeats}
                        </strong>{" "}
                        còn trống
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
                <div className="progress" style={{ height: "8px" }}>
                  <div
                    className="progress-bar"
                    role="progressbar"
                    style={{
                      width: `${occupancyRate}%`,
                      backgroundColor: classColor,
                    }}
                    aria-valuenow={parseFloat(occupancyRate)}
                    aria-valuemin={0}
                    aria-valuemax={100}
                  />
                </div>
              </Col>
            </Row>
          </>
        ) : (
          // Editing form
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
                  onChange={(e) =>
                    setEditData((prev) => ({
                      ...prev,
                      ticketQuantity: parseInt(e.target.value) || 0,
                    }))
                  }
                  min={minTotalQuantity}
                  max={planeInfo ? planeInfo.totalSeats : undefined}
                  isInvalid={!!validationError}
                  disabled={!isEditing}
                />
                <Form.Control.Feedback type="invalid">
                  {validationError}
                </Form.Control.Feedback>
                <div className="text-muted small">
                  Tối thiểu: {minTotalQuantity} ({soldSeats} đã bán)
                  {planeInfo && (
                    <> | Tối đa: {planeInfo.totalSeats} (sức chứa máy bay)</>
                  )}
                </div>
              </Form.Group>
            </Col>
            <Col sm={6}>
              <Form.Group>
                <Form.Label>Giá vé</Form.Label>
                <InputGroup>
                  <Form.Control
                    type="number"
                    className="pe-1"
                    value={editData.specifiedFare}
                    onChange={(e) =>
                      setEditData((prev) => ({
                        ...prev,
                        specifiedFare: parseInt(e.target.value) || 0,
                      }))
                    }
                    min="0"
                    disabled={!isEditing}
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
                  Tự động tính: Tổng ghế ({editData.ticketQuantity}) - Đã bán (
                  {soldSeats}) = {editData.ticketQuantity - soldSeats}
                </div>
              </Form.Group>
            </Col>
          </Row>
        )}
      </Card.Body>

      {!readOnly && isModified && !isEditing && (
        <Card.Footer className="p-0 border-top">
          <Row>
            <Col xs={12} className="text-center py-2">
              <small className="text-muted">
                <i className="bi bi-info-circle me-1"></i>
                Thay đổi chưa được lưu. Nhấn{" "}
                <i className="bi bi-arrow-counterclockwise"></i> để hoàn tác.
              </small>
            </Col>
          </Row>
        </Card.Footer>
      )}
    </Card>
  );
};

export default FlightManagement;
