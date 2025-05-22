import React, { useEffect, useState } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { listAirports } from "../services/AirportService";
import { addFlight, getFlight, updateFlight, addFlightDetails, getFlightDetailsByFlightId } from "../services/FlightService";
import { getParameter } from "../services/ParameterService";
import { AirportDto } from "../models/Airport";
import { FlightDto } from "../models/Flight";
import { ParameterDto } from "../models/Parameter";
import { Form, Button, Container, Card, Col, Spinner } from "react-bootstrap";
import { Typeahead } from 'react-bootstrap-typeahead';
import "react-bootstrap-typeahead/css/Typeahead.css";
import "react-bootstrap-typeahead/css/Typeahead.bs5.css";

type FlightFormInputs = Omit<FlightDto, "id">;
type FlightDetailInput = {
    mediumAirportId: number;
    stopTime: number;
    note: string;
};

const FlightForm: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [airports, setAirports] = useState<AirportDto[]>([]);
    const [parameter, setParameter] = useState<ParameterDto | undefined>(undefined);
    const [flightDetails, setFlightDetails] = useState<FlightDetailInput[]>([]);
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

    // Cross-validate medium airports and stop times
    useEffect(() => {
        const newErrors: Record<string, string> = {};
        flightDetails.forEach((detail, idx) => {
            // Medium airport validation (existing)
            const result = validateMediumAirport(detail.mediumAirportId, idx);
            if (result !== true) {
                newErrors[`mediumAirportId-${idx}`] = result;
            }

            // Stop duration validation
            if (detail.stopTime < 1) {
                newErrors[`stopTime-${idx}`] = "Stop time must be at least 1 minute";
            } else if (parameter?.maxStopDuration && detail.stopTime > parameter.maxStopDuration) {
                newErrors[`stopTime-${idx}`] = `Stop time cannot exceed ${parameter.maxStopDuration} minutes`;
            }
        });
        setDetailErrors(newErrors);
    }, [departureAirportId, arrivalAirportId, flightDetails, parameter]);

    const handleAddFlightDetail = () => {
        if (flightDetails.length >= (parameter?.maxMediumAirport || 0)) return;
        const defaultStopDuration = parameter?.maxStopDuration || 30;
        setFlightDetails(prev => [
            ...prev,
            { mediumAirportId: 0, stopTime: defaultStopDuration, note: "" }  // Default stop time to 30 minutes
        ]);
    };

    const handleDeleteFlightDetail = (idx: number) => {
        setFlightDetails(prev => prev.filter((_, i) => i !== idx));

        // Clear any errors for this detail
        const newDetailErrors = { ...detailErrors };
        delete newDetailErrors[`mediumAirportId-${idx}`];
        delete newDetailErrors[`stopTime-${idx}`];
        setDetailErrors(newDetailErrors);
    };

    // Cross validation for unique and not matching dep/arr
    const validateMediumAirport = (selectedId: number, idx: number) => {
        if (selectedId === 0) return "Medium airport is required";
        if (selectedId === departureAirportId || selectedId === arrivalAirportId) {
            return "Medium airport cannot be the same as departure or arrival airport";
        }
        const duplicates = flightDetails.filter((d, i) => d.mediumAirportId === selectedId && i !== idx);
        if (duplicates.length > 0) {
            return "Medium airport must be unique in flight details";
        }
        return true;
    };

    useEffect(() => {
        let isMounted = true;

        Promise.all([
            listAirports(),
            getParameter(),
            id ? getFlight(Number(id)) : Promise.resolve(null)
        ])
            .then(([airportsData, parameterData, flightData]) => {
                if (!isMounted) return;

                setAirports(airportsData);
                setParameter(parameterData);

                if (flightData) {
                    const { id: _, ...flightFormData } = flightData;
                    reset(flightFormData);

                    // Now get the flight details separately
                    if (id) {
                        getFlightDetailsByFlightId(Number(id))
                            .then(detailsData => {
                                if (!isMounted) return;
                                if (detailsData && Array.isArray(detailsData)) {
                                    setFlightDetails(detailsData.map(detail => ({
                                        mediumAirportId: detail.mediumAirportId,
                                        stopTime: detail.stopTime,
                                        note: detail.note || ""
                                    })));
                                }
                                setLoading(false);
                            })
                            .catch(err => {
                                console.error("Error loading flight details:", err);
                                setLoading(false);
                            });
                    } else {
                        setLoading(false);
                    }
                } else {
                    setLoading(false);
                }
            })
            .catch(err => {
                console.error("Error loading form data:", err);
                setLoading(false);
            });

        return () => { isMounted = false };
    }, [id, reset]);

    // Cross-validate medium airports on dep/arr change
    useEffect(() => {
        const newErrors: Record<string, string> = {};
        flightDetails.forEach((detail, idx) => {
            const result = validateMediumAirport(detail.mediumAirportId, idx);
            if (result !== true) {
                newErrors[`mediumAirportId-${idx}`] = result;
            }

            if (detail.stopTime < 1) {
                newErrors[`stopTime-${idx}`] = "Stop time must be at least 1 minute";
            }
        });
        setDetailErrors(newErrors);
    }, [departureAirportId, arrivalAirportId, flightDetails]);

    const validateForm = () => {
        // Check for departureAirportId and arrivalAirportId
        if (departureAirportId === 0) {
            setError("departureAirportId", {
                type: "manual",
                message: "Departure airport is required"
            });
            return false;
        }

        if (arrivalAirportId === 0) {
            setError("arrivalAirportId", {
                type: "manual",
                message: "Arrival airport is required"
            });
            return false;
        }

        if (departureAirportId === arrivalAirportId) {
            setError("arrivalAirportId", {
                type: "manual",
                message: "Departure and arrival airports cannot be the same"
            });
            return false;
        }

        // Validate flight details
        if (Object.keys(detailErrors).length > 0) {
            return false;
        }

        for (let i = 0; i < flightDetails.length; i++) {
            const detail = flightDetails[i];
            // ...existing mediumAirportId validation...

            if (detail.stopTime < 1) {
                setDetailErrors(prev => ({
                    ...prev,
                    [`stopTime-${i}`]: "Stop time must be at least 1 minute"
                }));
                return false;
            }

            if (parameter?.maxStopDuration && detail.stopTime > parameter.maxStopDuration) {
                setDetailErrors(prev => ({
                    ...prev,
                    [`stopTime-${i}`]: `Stop time cannot exceed ${parameter.maxStopDuration} minutes`
                }));
                return false;
            }
        }

        return true;
    };

    const onSubmit: SubmitHandler<FlightFormInputs> = async (data) => {
        try {
            if (!validateForm()) {
                return;
            }

            // Check if any flight detail has invalid medium airport
            for (let i = 0; i < flightDetails.length; i++) {
                const detail = flightDetails[i];
                if (detail.mediumAirportId === 0) {
                    setDetailErrors(prev => ({
                        ...prev,
                        [`mediumAirportId-${i}`]: "Medium airport is required"
                    }));
                    return;
                }

                if (detail.stopTime < 1) {
                    setDetailErrors(prev => ({
                        ...prev,
                        [`stopTime-${i}`]: "Stop time must be at least 1 minute"
                    }));
                    return;
                }
            }

            let flightId: number;
            if (id) {
                const updated = await updateFlight(Number(id), data);
                flightId = updated.id;
            } else {
                const created = await addFlight(data);
                flightId = created.id;
            }

            // Clear existing flight details if editing

            // Add flight details
            await Promise.all(
                flightDetails.map(detail =>
                    addFlightDetails({
                        flightId,
                        mediumAirportId: detail.mediumAirportId,
                        stopTime: detail.stopTime,
                        note: detail.note,
                    })
                )
            );

            navigate("/flights");
        } catch (error) {
            console.error("Error submitting form:", error);
            // Handle error (could add a toast notification here)
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
        value: number,
        onChange: (airportId: number) => void,
        label: string,
        error?: any,
        options?: AirportDto[],
        disabled?: boolean,
        placeholder?: string,
        id?: string,
        isInvalid?: boolean,
        clearButton?: boolean,
        customFilterBy?: (option: AirportDto, props: any) => boolean,
    }) => (
        <Form.Group className="mb-3" controlId={id || label}>
            <Form.Label>{label}</Form.Label>
            <Typeahead
                id={id || label}
                options={options}
                labelKey={(option: AirportDto) => `${option.id} - ${option.name}`}
                filterBy={customFilterBy ? customFilterBy : (option: AirportDto, props: any) => {
                    const text = props.text.toLowerCase();
                    return (
                        option.name.toLowerCase().includes(text) ||
                        option.id.toString().includes(text)
                    );
                }}
                placeholder={placeholder || `Select ${label}`}
                selected={options.filter(a => a.id === value)}
                onChange={selected => {
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
        <Card className="mx-auto mt-4" style={{ maxWidth: '60rem', width: '100%' }}>
            <Card.Header>{id ? "Edit Flight" : "Add Flight"}</Card.Header>
            <Card.Body>
                <Form onSubmit={handleSubmit(onSubmit)}>
                    {/* First row: 2 typeahead fields */}
                    <div className="row mb-3">
                        <div className="col-md-6">
                            {renderAirportTypeahead({
                                value: watch("departureAirportId"),
                                onChange: val => setValue("departureAirportId", val),
                                label: "Departure Airport",
                                error: errors.departureAirportId,
                                disabled: isSubmitting,
                                id: "departureAirportId"
                            })}
                        </div>
                        <div className="col-md-6">
                            {renderAirportTypeahead({
                                value: watch("arrivalAirportId"),
                                onChange: val => setValue("arrivalAirportId", val),
                                label: "Arrival Airport",
                                error: errors.arrivalAirportId,
                                disabled: isSubmitting,
                                id: "arrivalAirportId"
                            })}
                        </div>
                    </div>

                    {/* Second row: 3 controls */}
                    <div className="row mb-3">
                        <div className="col-md-4">
                            <Form.Group controlId="flightDate">
                                <Form.Label>Flight Date</Form.Label>
                                <Form.Control
                                    type="date"
                                    {...register("flightDate", {
                                        required: "Flight date is required",
                                        validate: (value) => {
                                            if (!value || value.trim() === "") return "Flight date is required";
                                            const today = new Date();
                                            today.setHours(0, 0, 0, 0);
                                            const selected = new Date(value);
                                            if (selected < today) {
                                                return "Flight date must be today or later";
                                            }
                                            return true;
                                        }
                                    })}
                                    isInvalid={!!errors.flightDate}
                                    disabled={isSubmitting}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.flightDate?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                        </div>
                        <div className="col-md-4">
                            <Form.Group controlId="flightTime">
                                <Form.Label>Flight Time</Form.Label>
                                <Form.Control
                                    type="time"
                                    {...register("flightTime", {
                                        required: "Flight time is required",
                                        validate: (value) => {
                                            const date = getValues("flightDate");
                                            if (!date || date.trim() === "") return "Select a flight date first";
                                            if (!value || value.trim() === "") return "Flight time is required";
                                            const flightDateTime = new Date(`${date}T${value}`);
                                            if (isNaN(flightDateTime.getTime())) return "Invalid date/time combination";
                                            if (flightDateTime <= new Date()) {
                                                return "Flight schedule must be later than the current time";
                                            }
                                            return true;
                                        }
                                    })}
                                    isInvalid={!!errors.flightTime}
                                    disabled={isSubmitting}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.flightTime?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                        </div>
                        <div className="col-md-4">
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
                                            message: `Duration must be at least ${parameter?.minFlightDuration || 1} minutes`
                                        },
                                    })}
                                    isInvalid={!!errors.duration}
                                    disabled={isSubmitting}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.duration?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                        </div>
                    </div>

                    {/* Flight Details Section */}
                    <div className="mb-4">
                        <h5>Flight Details (Medium Airports)</h5>
                        <div className="d-flex flex-column gap-3">
                            {flightDetails.map((detail, idx) => {
                                const mediumAirportError = detailErrors[`mediumAirportId-${idx}`];
                                const stopTimeError = detailErrors[`stopTime-${idx}`];

                                return (
                                    <div key={idx} className="p-3 border rounded">
                                        {/* Add this style to ensure all fields align properly */}
                                        <div className="row g-3 align-items-start">
                                            {/* Medium Airport Typeahead */}
                                            <div className="col-md-4">
                                                <Form.Group controlId={`mediumAirportId-${idx}`} className="h-100">
                                                    <Form.Label>Medium Airport {idx + 1}</Form.Label>
                                                    <Typeahead
                                                        id={`mediumAirportId-typeahead-${idx}`}
                                                        options={airports.filter(a =>
                                                            a.id !== departureAirportId &&
                                                            a.id !== arrivalAirportId &&
                                                            (!flightDetails.some((d, i) => i !== idx && d.mediumAirportId === a.id))
                                                        )}
                                                        labelKey={(option: AirportDto) => `${option.id} - ${option.name}`}
                                                        placeholder="Select Medium Airport"
                                                        selected={airports.filter(a => a.id === detail.mediumAirportId)}
                                                        onChange={selected => {
                                                            const sel = selected[0] as AirportDto | undefined;
                                                            setFlightDetails(fd =>
                                                                fd.map((d, i) => i === idx ? { ...d, mediumAirportId: sel ? sel.id : 0 } : d)
                                                            );
                                                        }}
                                                        isInvalid={!!mediumAirportError}
                                                        disabled={isSubmitting}
                                                    />
                                                    <Form.Control.Feedback type="invalid" className={mediumAirportError ? "d-block" : ""}>
                                                        {mediumAirportError}
                                                    </Form.Control.Feedback>
                                                </Form.Group>
                                            </div>
                                            {/* Other fields in the same row */}
                                            <div className="col-md-3">
                                                <Form.Group controlId={`stopTime-${idx}`} className="h-100">
                                                    <Form.Label>Stop Time (minutes)</Form.Label>
                                                    <Form.Control
                                                        type="number"
                                                        min={1}
                                                        value={detail.stopTime}
                                                        onChange={e => {
                                                            const val = Number(e.target.value);
                                                            setFlightDetails(fd =>
                                                                fd.map((d, i) => i === idx ? { ...d, stopTime: val } : d)
                                                            );
                                                        }}
                                                        isInvalid={!!stopTimeError}
                                                        disabled={isSubmitting}
                                                    />
                                                    <Form.Control.Feedback type="invalid" className={stopTimeError ? "d-block" : ""}>
                                                        {stopTimeError}
                                                    </Form.Control.Feedback>
                                                </Form.Group>
                                            </div>
                                            <div className="col-md-4">
                                                <Form.Group controlId={`note-${idx}`} className="h-100">
                                                    <Form.Label>Note</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        value={detail.note}
                                                        onChange={e => {
                                                            const val = e.target.value;
                                                            setFlightDetails(fd =>
                                                                fd.map((d, i) => i === idx ? { ...d, note: val } : d)
                                                            );
                                                        }}
                                                        disabled={isSubmitting}
                                                    />
                                                </Form.Group>
                                            </div>
                                            <div className="col-md-1 d-flex align-items-center h-100 pb-0">
                                                <Button
                                                    variant="danger"
                                                    type="button"
                                                    onClick={() => handleDeleteFlightDetail(idx)}
                                                    disabled={isSubmitting}
                                                    className="mb-0"
                                                >
                                                    Delete
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}

                            {/* Add button for flight details */}
                            <div className="mt-2">
                                <Button
                                    variant="outline-primary"
                                    type="button"
                                    disabled={flightDetails.length >= (parameter?.maxMediumAirport || 0) || isSubmitting}
                                    onClick={handleAddFlightDetail}
                                    className="d-flex align-items-center"
                                >
                                    <i className="bi bi-plus-circle me-2"></i> Add Medium Airport
                                </Button>
                                {parameter?.maxMediumAirport && flightDetails.length >= parameter.maxMediumAirport && (
                                    <small className="text-muted mt-1">
                                        Maximum medium airports ({parameter.maxMediumAirport}) reached
                                    </small>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Status message if needed */}
                    {Object.keys(detailErrors).length > 0 && (
                        <div className="alert alert-danger">
                            Please fix the errors in flight details before submitting
                        </div>
                    )}

                    {/* Action buttons */}
                    <div className="d-flex justify-content-between mt-4">
                        <Button variant="success" type="submit" disabled={isSubmitting}>
                            {isSubmitting ? (
                                <>
                                    <Spinner as="span" animation="border" size="sm" className="me-2" />
                                    {id ? "Updating..." : "Saving..."}
                                </>
                            ) : (
                                id ? "Update Flight" : "Add Flight"
                            )}
                        </Button>
                        <Button
                            variant="secondary"
                            type="button"
                            disabled={isSubmitting}
                            onClick={() => navigate('/flights')}
                        >
                            Cancel
                        </Button>
                    </div>
                </Form>
            </Card.Body>
            <Card.Footer className="text-muted">
                <small>Last updated: {new Date().toISOString().split('T')[0]} by {`thinh0704hcm`}</small>
            </Card.Footer>
        </Card>
    );
};

export default FlightForm;