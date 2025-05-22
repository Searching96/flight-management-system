import React, { useEffect, useState } from "react";
import { useForm, SubmitHandler, Controller } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { listAirports } from "../services/AirportService";
import { addFlight, getFlight, updateFlight } from "../services/FlightService";
import { getParameter } from "../services/ParameterService";
import { AirportDto } from "../models/Airport";
import { FlightDto } from "../models/Flight";
import { ParameterDto } from "../models/Parameter";
import { Form, Button, Container, Card, Col, Spinner } from "react-bootstrap";
import { Typeahead } from 'react-bootstrap-typeahead';
import "react-bootstrap-typeahead/css/Typeahead.css";
import "react-bootstrap-typeahead/css/Typeahead.bs5.css";

type FlightFormInputs = Omit<FlightDto, "id">;

const FlightForm: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [airports, setAirports] = useState<AirportDto[]>([]);
    const [parameter, setParameter] = useState<ParameterDto>([]);
    const [loading, setLoading] = useState<boolean>(true);

    const {
        register,
        handleSubmit,
        reset,
        control,
        getValues,
        formState: { errors, isSubmitting },
    } = useForm<FlightFormInputs>({
        defaultValues: {
            departureAirportId: 0,
            arrivalAirportId: 0,
            flightDate: "",
            flightTime: "",
            duration: 0,
        },
    });

    useEffect(() => {
        let isMounted = true;
        Promise.all([listAirports(), getParameter(), id ? getFlight(Number(id)) : Promise.resolve(null)])
            .then(([airportsData, parameterData, flightData]) => {
                if (!isMounted) return;
                setAirports(airportsData);
                setParameter(parameterData);
                if (flightData) {
                    const { id: _, ...flightFormData } = flightData;
                    reset(flightFormData);
                }
                setLoading(false);
            }).catch(err => {
                // Optionally handle error
                setLoading(false);
            });
        return () => { isMounted = false };
    }, [id, reset]);

    const onSubmit: SubmitHandler<FlightFormInputs> = async (data) => {
        if (id) {
            await updateFlight(Number(id), data);
        } else {
            await addFlight(data);
        }
        navigate("/flights");
    };

    // Inline helper for Airport Typeahead fields
    const renderAirportTypeahead = (
        fieldName: keyof FlightFormInputs,
        label: string,
        error: any
    ) => (
        <Form.Group className="mb-3" controlId={fieldName}>
            <Form.Label>{label}</Form.Label>
            <Controller
                control={control}
                name={fieldName as any}
                rules={{
                    required: `${label} is required`,
                    validate: value => {
                        if (value === 0) return `Select an airport`;
                        const otherField =
                            fieldName === "departureAirportId"
                                ? "arrivalAirportId"
                                : "departureAirportId";
                        const otherValue = getValues(otherField);
                        if (value === otherValue && value !== 0) {
                            return "Arrival and departure airports must be different";
                        }
                        return true;
                    }
                }}
                render={({ field }) => (
                    <Typeahead
                        id={String(fieldName)}
                        options={airports}
                        labelKey={option => `${option.id} - ${option.name}`}
                        filterBy={(option, props) => {
                            const text = props.text.toLowerCase();
                            return (
                                option.name.toLowerCase().includes(text) ||
                                option.id.toString().includes(text)
                            );
                        }}
                        placeholder={`Select ${label}`}
                        selected={airports.filter(a => a.id === field.value)}
                        onChange={selected => {
                            const sel = selected[0] as AirportDto | undefined;
                            field.onChange(sel ? sel.id : 0);
                        }}
                        isInvalid={!!error}
                        clearButton
                        disabled={isSubmitting}
                    />
                )}
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
        <Card className="mx-auto mt-4" style={{ width: '36rem' }}>
            <Card.Header className="mb-3">{id ? "Edit Flight" : "Add Flight"}</Card.Header>
            <Card.Body>
                <Form onSubmit={handleSubmit(onSubmit)}>
                    {renderAirportTypeahead("departureAirportId", "Departure Airport", errors.departureAirportId)}
                    {renderAirportTypeahead("arrivalAirportId", "Arrival Airport", errors.arrivalAirportId)}
                    <Form.Group className="mb-3" controlId="flightDate">
                        <Form.Label>Flight Date</Form.Label>
                        <Form.Control
                            type="date"
                            {...register("flightDate", {
                                required: "Flight date is required",
                                validate: (value) => {
                                    if (!value || value.trim() === "") return "Flight date is required";
                                    // Compare only dates, not times
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
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.flightDate?.message}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="flightTime">
                        <Form.Label>Flight Time</Form.Label>
                        <Form.Control
                            type="time"
                            {...register("flightTime", {
                                required: "Flight time is required",
                                validate: (value) => {
                                    const date = getValues("flightDate");
                                    if (!date || date.trim() === "") return "Select a flight date first";
                                    if (!value || value.trim() === "") return "Flight time is required";

                                    // Combine flightDate and flightTime for full validation
                                    const flightDateTime = new Date(`${date}T${value}`);
                                    if (isNaN(flightDateTime.getTime())) return "Invalid date/time combination";
                                    if (flightDateTime <= new Date()) {
                                        return "Flight schedule must be later than the current time";
                                    }
                                    return true;
                                }
                            })}
                            isInvalid={!!errors.flightTime}
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.flightTime?.message}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="duration">
                        <Form.Label>Duration (minutes)</Form.Label>
                        <Form.Control
                            type="number"
                            min={1}
                            {...register("duration", {
                                required: "Duration is required",
                                valueAsNumber: true,
                                min: { value: parameter.minFlightDuration, message: `Duration must be at least ${parameter.minFlightDuration} minutes` },
                            })}
                            isInvalid={!!errors.duration}
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.duration?.message}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Col className="d-flex justify-content-between">
                        <Button variant="success" type="submit" disabled={isSubmitting}>
                            {id ? "Update" : "Add"}
                        </Button>
                        <Button variant="secondary" type="button" disabled={isSubmitting} onClick={() => navigate('/flights')}>
                            Cancel
                        </Button>
                    </Col>
                </Form>
            </Card.Body>
        </Card>
    );
};

export default FlightForm;