import React, { useEffect, useState } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { listAirports } from "../services/AirportService";
import {
    addFlight,
    getFlight,
    updateFlight,
    addFlightDetails,
    getFlightDetailsByFlightId,
    updateFlightDetail,
    deleteFlightDetail,
    addFlightSeatClass,
    getFlightSeatClassesByFlightId,
    updateFlightSeatClass,
    deleteFlightSeatClass
} from "../services/FlightService";
import { listSeatClasses } from "../services/SeatClassService";
import { getParameter } from "../services/ParameterService";
import { AirportDto } from "../models/Airport";
import { FlightDto } from "../models/Flight";
import { ParameterDto } from "../models/Parameter";
import { FlightDetailDto } from "../models/FlightDetail";
import { SeatClassDto } from "../models/SeatClass";
import { FlightSeatClassDto } from "../models/FlightSeatClass";
import {
    Form,
    Button,
    Container,
    Card,
    Spinner,
    Row,
    Col,
    InputGroup,
} from "react-bootstrap";
import { Typeahead } from "react-bootstrap-typeahead";
import "react-bootstrap-typeahead/css/Typeahead.css";
import "react-bootstrap-typeahead/css/Typeahead.bs5.css";

type FlightFormInputs = Omit<FlightDto, "id">;
type FlightDetailInput = Omit<FlightDetailDto, "flightId"> & { flightId?: number };
type FlightSeatClassInput = Omit<FlightSeatClassDto, "flightId" | "remainingTickets" | "deletedAt" | "id"> & { flightId?: number };

const FlightForm: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [airports, setAirports] = useState<AirportDto[]>([]);
    const [parameter, setParameter] = useState<ParameterDto | undefined>(undefined);
    const [flightDetails, setFlightDetails] = useState<FlightDetailInput[]>([]);
    const [originalFlightDetails, setOriginalFlightDetails] = useState<FlightDetailInput[]>([]);
    const [seatClasses, setSeatClasses] = useState<SeatClassDto[]>([]);
    const [flightSeatClasses, setFlightSeatClasses] = useState<FlightSeatClassInput[]>([]);
    const [originalFlightSeatClasses, setOriginalFlightSeatClasses] = useState<FlightSeatClassInput[]>([]);
    const [seatClassErrors, setSeatClassErrors] = useState<Record<string, string>>({});
    const [loading, setLoading] = useState<boolean>(true);
    const [detailErrors, setDetailErrors] = useState<Record<string, string>>({});

    const {
        register,
        handleSubmit,
        reset,
        getValues,
        setValue,
        formState: { errors, isSubmitting },
        watch,
        setError,
    } = useForm<FlightFormInputs>({
        defaultValues: {
            departureAirportId: 0,
            arrivalAirportId: 0,
            flightDate: "",
            flightTime: "",
            duration: 0,
        },
    });

    // Watch for cross validation in details
    const departureAirportId = watch("departureAirportId");
    const arrivalAirportId = watch("arrivalAirportId");

    // Centralized validation function for flight details
    const validateFlightDetails = (
        flightDetails: FlightDetailInput[],
        departureAirportId: number,
        arrivalAirportId: number,
        parameter?: ParameterDto
    ): Record<string, string> => {
        const errors: Record<string, string> = {};
        flightDetails.forEach((detail, idx) => {
            if (detail.mediumAirportId === 0) {
                errors[`mediumAirportId-${idx}`] = "Medium airport is required";
            } else if (
                detail.mediumAirportId === departureAirportId ||
                detail.mediumAirportId === arrivalAirportId
            ) {
                errors[`mediumAirportId-${idx}`] =
                    "Medium airport cannot be the same as departure or arrival airport";
            } else if (
                flightDetails.filter(
                    (d, i) => d.mediumAirportId === detail.mediumAirportId && i !== idx
                ).length > 0
            ) {
                errors[`mediumAirportId-${idx}`] =
                    "Medium airport must be unique in flight details";
            }
            if (detail.stopTime < 1) {
                errors[`stopTime-${idx}`] = "Stop time must be at least 1 minute";
            } else if (
                parameter?.maxStopDuration &&
                detail.stopTime > parameter.maxStopDuration
            ) {
                errors[`stopTime-${idx}`] = `Stop time cannot exceed ${parameter.maxStopDuration} minutes`;
            }
        });
        return errors;
    };

    // Centralized validation for flight seat classes
    const validateFlightSeatClasses = (
        flightSeatClasses: FlightSeatClassInput[]
    ): Record<string, string> => {
        const errors: Record<string, string> = {};
        flightSeatClasses.forEach((item, idx) => {
            if (!item.seatClassId || item.seatClassId === 0) {
                errors[`seatClassId-${idx}`] = "Seat class is required";
            } else if (
                flightSeatClasses.filter(
                    (c, i) => c.seatClassId === item.seatClassId && i !== idx
                ).length > 0
            ) {
                errors[`seatClassId-${idx}`] = "Seat class must be unique";
            }
            if (item.currentPrice == null || item.currentPrice < 0)
                errors[`currentPrice-${idx}`] = "Price must be >= 0";
            if (item.totalTickets == null || item.totalTickets < 1)
                errors[`totalTickets-${idx}`] = "Quantity must be > 0";
        });
        return errors;
    };

    useEffect(() => {
        setDetailErrors(
            validateFlightDetails(flightDetails, departureAirportId, arrivalAirportId, parameter)
        );
    }, [flightDetails, departureAirportId, arrivalAirportId, parameter]);

    useEffect(() => {
        setSeatClassErrors(validateFlightSeatClasses(flightSeatClasses));
    }, [flightSeatClasses]);

    // ADD medium airport row
    const handleAddFlightDetail = () => {
        if (flightDetails.length >= (parameter?.maxMediumAirport || 0)) return;
        const defaultStopDuration = parameter?.maxStopDuration || 30;
        setFlightDetails((prev) => [
            ...prev,
            { mediumAirportId: 0, stopTime: defaultStopDuration, note: "" },
        ]);
    };

    // DELETE medium airport row
    const handleDeleteFlightDetail = (idx: number) => {
        setFlightDetails((prev) => prev.filter((_, i) => i !== idx));
        // Remove errors for this row
        const newDetailErrors = { ...detailErrors };
        delete newDetailErrors[`mediumAirportId-${idx}`];
        delete newDetailErrors[`stopTime-${idx}`];
        setDetailErrors(newDetailErrors);
    };

    // ADD seat class row
    const handleAddSeatClass = () => {
        setFlightSeatClasses((prev) => [
            ...prev,
            { seatClassId: 0, currentPrice: 0, totalTickets: 1 }
        ]);
    };

    // DELETE seat class row
    const handleDeleteSeatClass = (idx: number) => {
        setFlightSeatClasses((prev) => prev.filter((_, i) => i !== idx));
        const newErrors = { ...seatClassErrors };
        delete newErrors[`seatClassId-${idx}`];
        delete newErrors[`currentPrice-${idx}`];
        delete newErrors[`totalTickets-${idx}`];
        setSeatClassErrors(newErrors);
    };

    useEffect(() => {
        let isMounted = true;

        Promise.all([
            listAirports(),
            listSeatClasses(),
            getParameter(),
            id ? getFlight(Number(id)) : Promise.resolve(null),
        ])
            .then(([airportsData, seatClassesData, parameterData, flightData]) => {
                if (!isMounted) return;

                setAirports(airportsData);
                setSeatClasses(seatClassesData);
                setParameter(parameterData);

                if (flightData) {
                    const { id: _, ...flightFormData } = flightData;
                    reset(flightFormData);

                    if (id) {
                        // Get flight details
                        getFlightDetailsByFlightId(Number(id))
                            .then((detailsData) => {
                                if (!isMounted) return;
                                if (detailsData && Array.isArray(detailsData)) {
                                    const mapped = detailsData.map((detail) => ({
                                        flightId: detail.flightId,
                                        mediumAirportId: detail.mediumAirportId,
                                        stopTime: detail.stopTime,
                                        note: detail.note || "",
                                    }));
                                    setFlightDetails(mapped);
                                    setOriginalFlightDetails(mapped);
                                }
                            })
                            .catch((err) => {
                                console.error("Error loading flight details:", err);
                            });

                        // Get flight seat classes
                        getFlightSeatClassesByFlightId(Number(id))
                            .then((seatClassRows) => {
                                if (!isMounted) return;
                                setFlightSeatClasses(seatClassRows);
                                setOriginalFlightSeatClasses(seatClassRows);
                            })
                            .catch((err) => {
                                console.error("Error loading flight seat classes:", err);
                            });
                    }
                }
                setLoading(false);
            })
            .catch((err) => {
                console.error("Error loading form data:", err);
                setLoading(false);
            });

        return () => {
            isMounted = false;
        };
    }, [id, reset]);

    const validateForm = () => {
        let valid = true;
        if (departureAirportId === 0) {
            setError("departureAirportId", {
                type: "manual",
                message: "Departure airport is required",
            });
            valid = false;
        }
        if (arrivalAirportId === 0) {
            setError("arrivalAirportId", {
                type: "manual",
                message: "Arrival airport is required",
            });
            valid = false;
        }
        if (departureAirportId === arrivalAirportId) {
            setError("arrivalAirportId", {
                type: "manual",
                message: "Departure and arrival airports cannot be the same",
            });
            valid = false;
        }
        const errors = validateFlightDetails(
            flightDetails,
            departureAirportId,
            arrivalAirportId,
            parameter
        );
        setDetailErrors(errors);
        if (Object.keys(errors).length > 0) valid = false;

        const seatErrors = validateFlightSeatClasses(flightSeatClasses);
        setSeatClassErrors(seatErrors);
        if (Object.keys(seatErrors).length > 0) valid = false;

        return valid;
    };

    const onSubmit: SubmitHandler<FlightFormInputs> = async (data) => {
        if (!validateForm()) return;
        try {
            let flightId: number;
            if (id) {
                const updated = await updateFlight(Number(id), data);
                flightId = updated.id;
            } else {
                const created = await addFlight(data);
                flightId = created.id;
            }

            // --- Flight Details ---
            const detailsWithFlightId = flightDetails.map((d) => ({
                ...d,
                flightId: flightId,
            }));
            const originalsWithFlightId = originalFlightDetails.map((d) => ({
                ...d,
                flightId: flightId,
            }));

            const deletedDetails = originalsWithFlightId.filter(
                (od) =>
                    !detailsWithFlightId.some(
                        (fd) =>
                            fd.flightId === od.flightId && fd.mediumAirportId === od.mediumAirportId
                    )
            );
            const addedDetails = detailsWithFlightId.filter(
                (fd) =>
                    !originalsWithFlightId.some(
                        (od) =>
                            fd.flightId === od.flightId && fd.mediumAirportId === od.mediumAirportId
                    )
            );
            const updatedDetails = detailsWithFlightId.filter((fd) =>
                originalsWithFlightId.some(
                    (od) =>
                        fd.flightId === od.flightId &&
                        fd.mediumAirportId === od.mediumAirportId &&
                        (fd.stopTime !== od.stopTime || fd.note !== od.note)
                )
            );

            // --- Flight Seat Classes ---
            const seatWithFlightId = flightSeatClasses.map(s => ({ ...s, flightId }));
            const originalWithFlightId = originalFlightSeatClasses.map(s => ({ ...s, flightId }));

            const deletedSeatClasses = originalWithFlightId.filter(
                (o) => !seatWithFlightId.some(s => s.seatClassId === o.seatClassId)
            );
            const addedSeatClasses = seatWithFlightId.filter(
                (s) => !originalWithFlightId.some(o => o.seatClassId === s.seatClassId)
            );
            const updatedSeatClasses = seatWithFlightId.filter(s =>
                originalWithFlightId.some(o =>
                    o.seatClassId === s.seatClassId &&
                    (o.currentPrice !== s.currentPrice || o.totalTickets !== s.totalTickets)
                )
            );

            await Promise.all([
                // Flight Details
                ...deletedDetails.map((detail) =>
                    deleteFlightDetail(detail.flightId!, detail.mediumAirportId)
                ),
                ...addedDetails.map((detail) =>
                    addFlightDetails({
                        flightId: detail.flightId!,
                        mediumAirportId: detail.mediumAirportId,
                        stopTime: detail.stopTime,
                        note: detail.note,
                    })
                ),
                ...updatedDetails.map((detail) =>
                    updateFlightDetail(detail.flightId!, detail.mediumAirportId, {
                        stopTime: detail.stopTime,
                        note: detail.note,
                    })
                ),
                // Seat Classes
                ...deletedSeatClasses.map(s => deleteFlightSeatClass(flightId, s.seatClassId!)),
                ...addedSeatClasses.map(s =>
                    addFlightSeatClass({
                        flightId,
                        seatClassId: s.seatClassId!,
                        currentPrice: s.currentPrice!,
                        totalTickets: s.totalTickets!
                    })
                ),
                ...updatedSeatClasses.map(s =>
                    updateFlightSeatClass(
                        flightId,
                        s.seatClassId!,
                        {
                            currentPrice: s.currentPrice!,
                            totalTickets: s.totalTickets!
                        }
                    )
                ),
            ]);

            navigate("/flights");
        } catch (error) {
            console.error("Error submitting form:", error);
        }
    };

    // Helper for Airport Typeahead fields
    const renderAirportTypeahead = ({
        value,
        onChange,
        label,
        error,
        options = airports,
        disabled = false,
        placeholder,
        id,
        isInvalid = false,
        clearButton = true,
        customFilterBy,
    }: {
        value: number;
        onChange: (airportId: number) => void;
        label: string;
        error?: any;
        options?: AirportDto[];
        disabled?: boolean;
        placeholder?: string;
        id?: string;
        isInvalid?: boolean;
        clearButton?: boolean;
        customFilterBy?: (option: AirportDto, props: any) => boolean;
    }) => (
        <Form.Group className="mb-3" controlId={id || label}>
            <Form.Label>{label}</Form.Label>
            <Typeahead
                id={id || label}
                options={options}
                labelKey={(option: AirportDto) => `${option.id} - ${option.name}`}
                filterBy={
                    customFilterBy
                        ? customFilterBy
                        : (option: AirportDto, props: any) => {
                            const text = props.text.toLowerCase();
                            return (
                                option.name.toLowerCase().includes(text) ||
                                option.id.toString().includes(text)
                            );
                        }
                }
                placeholder={placeholder || `Select ${label}`}
                selected={options.filter((a) => a.id === value)}
                onChange={(selected) => {
                    const sel = selected[0] as AirportDto | undefined;
                    onChange(sel ? sel.id : 0);
                }}
                isInvalid={isInvalid || !!error}
                clearButton={clearButton}
                disabled={disabled}
            />
            <Form.Control.Feedback type="invalid" className={error ? "d-block" : ""}>
                {error?.message}
            </Form.Control.Feedback>
        </Form.Group>
    );

    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" />
            </Container>
        );
    }

    return (
        <Card className="mx-auto mt-4" style={{ maxWidth: "60rem", width: "100%" }}>
            <Card.Header>{id ? "Edit Flight" : "Add Flight"}</Card.Header>
            <Card.Body>
                <Form onSubmit={handleSubmit(onSubmit)}>
                    {/* First row: 2 typeahead fields */}
                    <Row className="mb-3">
                        <Col md={6}>
                            {renderAirportTypeahead({
                                value: watch("departureAirportId"),
                                onChange: (val) => setValue("departureAirportId", val),
                                label: "Departure Airport",
                                error: errors.departureAirportId,
                                disabled: isSubmitting,
                                id: "departureAirportId",
                            })}
                        </Col>
                        <Col md={6}>
                            {renderAirportTypeahead({
                                value: watch("arrivalAirportId"),
                                onChange: (val) => setValue("arrivalAirportId", val),
                                label: "Arrival Airport",
                                error: errors.arrivalAirportId,
                                disabled: isSubmitting,
                                id: "arrivalAirportId",
                            })}
                        </Col>
                    </Row>
                    {/* Second row: 3 controls */}
                    <Row className="mb-3">
                        <Col md={4}>
                            <Form.Group controlId="flightDate">
                                <Form.Label>Flight Date</Form.Label>
                                <Form.Control
                                    type="date"
                                    {...register("flightDate", {
                                        required: "Flight date is required",
                                        validate: (value) => {
                                            if (!value || value.trim() === "")
                                                return "Flight date is required";
                                            const today = new Date();
                                            today.setHours(0, 0, 0, 0);
                                            const selected = new Date(value);
                                            if (selected < today) {
                                                return "Flight date must be today or later";
                                            }
                                            return true;
                                        },
                                    })}
                                    isInvalid={!!errors.flightDate}
                                    disabled={isSubmitting}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.flightDate?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group controlId="flightTime">
                                <Form.Label>Flight Time</Form.Label>
                                <Form.Control
                                    type="time"
                                    {...register("flightTime", {
                                        required: "Flight time is required",
                                        validate: (value) => {
                                            const date = getValues("flightDate");
                                            if (!date || date.trim() === "")
                                                return "Select a flight date first";
                                            if (!value || value.trim() === "")
                                                return "Flight time is required";
                                            const flightDateTime = new Date(`${date}T${value}`);
                                            if (isNaN(flightDateTime.getTime()))
                                                return "Invalid date/time combination";
                                            if (flightDateTime <= new Date()) {
                                                return "Flight schedule must be later than the current time";
                                            }
                                            return true;
                                        },
                                    })}
                                    isInvalid={!!errors.flightTime}
                                    disabled={isSubmitting}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.flightTime?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group controlId="duration">
                                <Form.Label>Duration (minutes)</Form.Label>
                                <Form.Control
                                    type="number"
                                    min={parameter?.minFlightDuration || 1}
                                    {...register("duration", {
                                        required: "Duration is required",
                                        valueAsNumber: true,
                                        min: {
                                            value: parameter?.minFlightDuration || 1,
                                            message: `Duration must be at least ${parameter?.minFlightDuration || 1} minutes`,
                                        },
                                    })}
                                    isInvalid={!!errors.duration}
                                    disabled={isSubmitting}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.duration?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                    </Row>
                    {/* Flight Details Section */}
                    <div className="mb-4">
                        <h5>Flight Details (Medium Airports)</h5>
                        {flightDetails.map((detail, idx) => {
                            const mediumAirportError = detailErrors[`mediumAirportId-${idx}`];
                            const stopTimeError = detailErrors[`stopTime-${idx}`];
                            return (
                                <Row
                                    key={idx}
                                    className="p-3 border rounded align-items-start mb-3"
                                >
                                    {/* Medium Airport Typeahead */}
                                    <Col xs="auto">
                                        <Form.Label>Medium Airport {idx + 1}</Form.Label>
                                        <Typeahead
                                            id={`mediumAirportId-typeahead-${idx}`}
                                            options={airports.filter(
                                                (a) =>
                                                    a.id !== departureAirportId &&
                                                    a.id !== arrivalAirportId &&
                                                    !flightDetails.some(
                                                        (d, i) => i !== idx && d.mediumAirportId === a.id
                                                    )
                                            )}
                                            labelKey={(option: AirportDto) =>
                                                `${option.id} - ${option.name}`
                                            }
                                            placeholder="Select Medium Airport"
                                            selected={airports.filter(
                                                (a) => a.id === detail.mediumAirportId
                                            )}
                                            onChange={(selected) => {
                                                const sel = selected[0] as AirportDto | undefined;
                                                setFlightDetails((fd) =>
                                                    fd.map((d, i) =>
                                                        i === idx
                                                            ? { ...d, mediumAirportId: sel ? sel.id : 0 }
                                                            : d
                                                    )
                                                );
                                            }}
                                            isInvalid={!!mediumAirportError}
                                            disabled={isSubmitting}
                                        />
                                        <Form.Control.Feedback
                                            type="invalid"
                                            className={mediumAirportError ? "d-block" : ""}
                                        >
                                            {mediumAirportError}
                                        </Form.Control.Feedback>
                                    </Col>
                                    {/* Stop Time */}
                                    <Col xs="auto">
                                        <Form.Label>Stop Time (minutes)</Form.Label>
                                        <InputGroup className="mb-2">
                                            <Form.Control
                                                type="number"
                                                min={1}
                                                value={detail.stopTime}
                                                onChange={(e) => {
                                                    const val = Number(e.target.value);
                                                    setFlightDetails((fd) =>
                                                        fd.map((d, i) =>
                                                            i === idx ? { ...d, stopTime: val } : d
                                                        )
                                                    );
                                                }}
                                                isInvalid={!!stopTimeError}
                                                disabled={isSubmitting}
                                                placeholder="Stop Time"
                                            />
                                            <InputGroup.Text>min</InputGroup.Text>
                                        </InputGroup>
                                        <Form.Control.Feedback
                                            type="invalid"
                                            className={stopTimeError ? "d-block" : ""}
                                        >
                                            {stopTimeError}
                                        </Form.Control.Feedback>
                                    </Col>
                                    {/* Note */}
                                    <Col xs="auto">
                                        <Form.Label>Note</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={detail.note}
                                            onChange={(e) => {
                                                const val = e.target.value;
                                                setFlightDetails((fd) =>
                                                    fd.map((d, i) =>
                                                        i === idx ? { ...d, note: val } : d
                                                    )
                                                );
                                            }}
                                            disabled={isSubmitting}
                                            placeholder="Note"
                                        />
                                    </Col>
                                    {/* Delete Button */}
                                    <Col xs="auto">
                                        <Form.Group className="h-100">
                                            <Row>
                                                <Form.Label>Action</Form.Label>
                                                <Button
                                                    variant="danger"
                                                    type="button"
                                                    onClick={() => handleDeleteFlightDetail(idx)}
                                                    disabled={isSubmitting}
                                                >
                                                    Delete
                                                </Button>
                                            </Row>
                                        </Form.Group>
                                    </Col>
                                </Row>
                            );
                        })}
                        {/* Add button for flight details */}
                        <div className="mt-2">
                            <Button
                                variant="outline-primary"
                                type="button"
                                disabled={
                                    flightDetails.length >= (parameter?.maxMediumAirport || 0) ||
                                    isSubmitting
                                }
                                onClick={handleAddFlightDetail}
                                className="d-flex align-items-center"
                            >
                                <i className="bi bi-plus-circle me-2"></i> Add Medium Airport
                            </Button>
                            {parameter?.maxMediumAirport &&
                                flightDetails.length >= parameter.maxMediumAirport && (
                                    <small className="text-muted mt-1">
                                        Maximum medium airports ({parameter.maxMediumAirport}) reached
                                    </small>
                                )}
                        </div>
                    </div>
                    {/* Seat Classes Section */}
                    <div className="mb-4">
                        <h5>Seat Classes</h5>
                        {flightSeatClasses.map((item, idx) => {
                            const seatClassError = seatClassErrors[`seatClassId-${idx}`];
                            const currentPriceError = seatClassErrors[`currentPrice-${idx}`];
                            const totalTicketsError = seatClassErrors[`totalTickets-${idx}`];
                            return (
                                <Row
                                    key={idx}
                                    className="p-3 border rounded align-items-start mb-3"
                                >
                                    {/* Seat Class Dropdown */}
                                    <Col>
                                        <Form.Label>Seat Class {idx + 1}</Form.Label>
                                        <Form.Select
                                            value={item.seatClassId}
                                            onChange={e => {
                                                const val = Number(e.target.value);
                                                setFlightSeatClasses(list =>
                                                    list.map((c, i) =>
                                                        i === idx ? { ...c, seatClassId: val } : c
                                                    )
                                                );
                                            }}
                                            isInvalid={!!seatClassError}
                                            disabled={isSubmitting}
                                        >
                                            <option value={0}>Select Seat Class</option>
                                            {seatClasses.map(sc => (
                                                <option key={sc.id} value={sc.id}>{sc.seatName}</option>
                                            ))}
                                        </Form.Select>
                                        <Form.Control.Feedback
                                            type="invalid"
                                            className={seatClassError ? "d-block" : ""}
                                        >
                                            {seatClassError}
                                        </Form.Control.Feedback>
                                    </Col>
                                    {/* Price */}
                                    <Col xs="auto">
                                        <Form.Label>Price</Form.Label>
                                        <InputGroup className="mb-2">
                                            <Form.Control
                                                type="number"
                                                min={0}
                                                value={item.currentPrice}
                                                onChange={e => {
                                                    const val = Number(e.target.value);
                                                    setFlightSeatClasses(list =>
                                                        list.map((c, i) =>
                                                            i === idx ? { ...c, currentPrice: val } : c
                                                        )
                                                    );
                                                }}
                                                isInvalid={!!currentPriceError}
                                                disabled={isSubmitting}
                                                placeholder="Price"
                                            />
                                            <InputGroup.Text>$</InputGroup.Text>
                                        </InputGroup>
                                        <Form.Control.Feedback
                                            type="invalid"
                                            className={currentPriceError ? "d-block" : ""}
                                        >
                                            {currentPriceError}
                                        </Form.Control.Feedback>
                                    </Col>
                                    {/* Quantity */}
                                    <Col xs="auto">
                                        <Form.Label>Quantity</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min={1}
                                            value={item.totalTickets}
                                            onChange={e => {
                                                const val = Number(e.target.value);
                                                setFlightSeatClasses(list =>
                                                    list.map((c, i) =>
                                                        i === idx ? { ...c, totalTickets: val } : c
                                                    )
                                                );
                                            }}
                                            isInvalid={!!totalTicketsError}
                                            disabled={isSubmitting}
                                            placeholder="Quantity"
                                        />
                                        <Form.Control.Feedback
                                            type="invalid"
                                            className={totalTicketsError ? "d-block" : ""}
                                        >
                                            {totalTicketsError}
                                        </Form.Control.Feedback>
                                    </Col>
                                    {/* Delete Button */}
                                    <Col xs="auto">
                                        <Form.Group className="h-100">
                                            <Row>
                                                <Form.Label>Action</Form.Label>
                                                <Button
                                                    variant="danger"
                                                    type="button"
                                                    onClick={() => handleDeleteSeatClass(idx)}
                                                    disabled={isSubmitting}
                                                >
                                                    Delete
                                                </Button>
                                            </Row>
                                        </Form.Group>
                                    </Col>
                                </Row>
                            );
                        })}
                        {/* Add button for seat classes */}
                        <div className="mt-2">
                            <Button
                                variant="outline-primary"
                                type="button"
                                disabled={isSubmitting}
                                onClick={handleAddSeatClass}
                                className="d-flex align-items-center"
                            >
                                <i className="bi bi-plus-circle me-2"></i> Add Seat Class
                            </Button>
                        </div>
                    </div>
                    {/* Status message if needed */}
                    {(Object.keys(detailErrors).length > 0 || Object.keys(seatClassErrors).length > 0) && (
                        <div className="alert alert-danger">
                            Please fix the errors in flight details and seat classes before submitting
                        </div>
                    )}
                    {/* Action buttons */}
                    <div className="d-flex justify-content-between mt-4">
                        <Button variant="success" type="submit" disabled={isSubmitting}>
                            {isSubmitting ? (
                                <>
                                    <Spinner
                                        as="span"
                                        animation="border"
                                        size="sm"
                                        className="me-2"
                                    />
                                    {id ? "Updating..." : "Saving..."}
                                </>
                            ) : id ? (
                                "Update Flight"
                            ) : (
                                "Add Flight"
                            )}
                        </Button>
                        <Button
                            variant="secondary"
                            type="button"
                            disabled={isSubmitting}
                            onClick={() => navigate("/flights")}
                        >
                            Cancel
                        </Button>
                    </div>
                </Form>
            </Card.Body>
            <Card.Footer className="text-muted">
                <small>
                    Last updated: {new Date().toISOString().split("T")[0]} by thinh0704hcm
                </small>
            </Card.Footer>
        </Card>
    );
};

export default FlightForm;