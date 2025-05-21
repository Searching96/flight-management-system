import React, { useEffect, useState } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { addFlight, getFlight, updateFlight } from "../services/FlightService";
import { FlightDto } from "../models/Flight";
import { listAirports } from "../services/AirportService";
import { AirportDto } from "../models/Airport";
import { Form, Button, Container, Row, Col, Spinner } from "react-bootstrap";

type FlightFormInputs = Omit<FlightDto, "id">;

const FlightForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [airports, setAirports] = useState<AirportDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FlightFormInputs>({
    defaultValues: {
      airline: "",
      departureAirportId: 0,
      arrivalAirportId: 0,
      flightDate: "",
      flightTime: "",
      flightNumber: "",
      duration: 0,
    },
  });

  useEffect(() => {
    listAirports().then(setAirports);
    if (id) {
      getFlight(Number(id)).then((data) => {
        // Remove id for editing since we use Omit<FlightDto, "id">
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const { id: _, ...flightData } = data;
        reset(flightData);
        setLoading(false);
      });
    } else {
      setLoading(false);
    }
  }, [id, reset]);

  const onSubmit: SubmitHandler<FlightFormInputs> = async (data) => {
    if (id) {
      await updateFlight(Number(id), data);
    } else {
      await addFlight(data);
    }
    navigate("/flights");
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container>
      <Row className="justify-content-md-center">
        <Col xs={12} md={8} lg={6}>
          <h2 className="mt-4 mb-3">{id ? "Edit Flight" : "Add Flight"}</h2>
          <Form onSubmit={handleSubmit(onSubmit)}>
            <Form.Group className="mb-3" controlId="airline">
              <Form.Label>Airline</Form.Label>
              <Form.Control
                type="text"
                {...register("airline", { required: "Airline is required" })}
                isInvalid={!!errors.airline}
              />
              <Form.Control.Feedback type="invalid">
                {errors.airline?.message}
              </Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3" controlId="flightNumber">
              <Form.Label>Flight Number</Form.Label>
              <Form.Control
                type="text"
                {...register("flightNumber", { required: "Flight Number is required" })}
                isInvalid={!!errors.flightNumber}
              />
              <Form.Control.Feedback type="invalid">
                {errors.flightNumber?.message}
              </Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3" controlId="departureAirportId">
              <Form.Label>Departure Airport</Form.Label>
              <Form.Select
                {...register("departureAirportId", {
                  required: "Departure Airport is required",
                  valueAsNumber: true,
                  validate: (value) => value !== 0 || "Select an airport",
                })}
                isInvalid={!!errors.departureAirportId}
              >
                <option value={0}>Select Departure Airport</option>
                {airports.map((airport) => (
                  <option key={airport.id} value={airport.id}>
                    {airport.name}
                  </option>
                ))}
              </Form.Select>
              <Form.Control.Feedback type="invalid">
                {errors.departureAirportId?.message}
              </Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3" controlId="arrivalAirportId">
              <Form.Label>Arrival Airport</Form.Label>
              <Form.Select
                {...register("arrivalAirportId", {
                  required: "Arrival Airport is required",
                  valueAsNumber: true,
                  validate: (value) => value !== 0 || "Select an airport",
                })}
                isInvalid={!!errors.arrivalAirportId}
              >
                <option value={0}>Select Arrival Airport</option>
                {airports.map((airport) => (
                  <option key={airport.id} value={airport.id}>
                    {airport.name}
                  </option>
                ))}
              </Form.Select>
              <Form.Control.Feedback type="invalid">
                {errors.arrivalAirportId?.message}
              </Form.Control.Feedback>
            </Form.Group>
            <Form.Group className="mb-3" controlId="flightDate">
              <Form.Label>Flight Date</Form.Label>
              <Form.Control
                type="date"
                {...register("flightDate", { required: "Flight date is required" })}
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
                {...register("flightTime", { required: "Flight time is required" })}
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
                  min: { value: 1, message: "Duration must be at least 1 minute" },
                })}
                isInvalid={!!errors.duration}
              />
              <Form.Control.Feedback type="invalid">
                {errors.duration?.message}
              </Form.Control.Feedback>
            </Form.Group>
            <Button variant="success" type="submit" disabled={isSubmitting}>
              {id ? "Update" : "Add"}
            </Button>
          </Form>
        </Col>
      </Row>
    </Container>
  );
};

export default FlightForm;