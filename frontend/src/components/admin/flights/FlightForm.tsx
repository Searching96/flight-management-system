import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { Row, Col, Form, Button, Alert } from "react-bootstrap";
import {
  Flight,
  FlightRequest,
  Airport,
  Plane,
  Parameter,
} from "../../../models";
import TypeAhead from "../../common/TypeAhead";
import { FlightDetailWithIndex } from "../../../hooks/useFlightDetails";
import FlightDetailsTable from "./FlightDetailsTable";

interface FlightFormProps {
  editingFlight: Flight | null;
  airports: Airport[];
  planes: Plane[];
  flightDetails: FlightDetailWithIndex[];
  formErrors: { [key: string]: string };
  detailErrors: { [key: string]: string };
  setFormErrors: React.Dispatch<
    React.SetStateAction<{ [key: string]: string }>
  >;
  setDetailErrors: React.Dispatch<
    React.SetStateAction<{ [key: string]: string }>
  >;
  onSubmit: (data: FlightRequest) => void;
  onCancel: () => void;
  onFlightDetailChange: (index: number, field: string, value: any) => void;
  onAddFlightDetail: () => void;
  onRemoveFlightDetail: (index: number) => void;
  parameters: Parameter | null;
}

const FlightForm: React.FC<FlightFormProps> = ({
  editingFlight,
  airports,
  planes,
  flightDetails,
  formErrors,
  detailErrors,
  setFormErrors,
  setDetailErrors,
  onSubmit,
  onCancel,
  onFlightDetailChange,
  onAddFlightDetail,
  onRemoveFlightDetail,
  parameters,
}) => {
  const [selectedDepartureAirport, setSelectedDepartureAirport] = useState<
    number | ""
  >(editingFlight?.departureAirportId || "");
  const [selectedArrivalAirport, setSelectedArrivalAirport] = useState<
    number | ""
  >(editingFlight?.arrivalAirportId || "");
  const [selectedPlane, setSelectedPlane] = useState<number | "">(
    editingFlight?.planeId || ""
  );

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<FlightRequest>({
    mode: "onChange",
  });

  const watchDepartureTime = watch("departureTime");
  const watchArrivalTime = watch("arrivalTime");
  const watchDepartureAirport = watch("departureAirportId");
  const watchArrivalAirport = watch("arrivalAirportId");

  // Reset form when editing flight changes
  useEffect(() => {
    if (editingFlight) {
      reset({
        flightCode: editingFlight.flightCode,
        departureTime: editingFlight.departureTime.slice(0, 16),
        arrivalTime: editingFlight.arrivalTime.slice(0, 16),
        planeId: editingFlight.planeId,
        departureAirportId: editingFlight.departureAirportId,
        arrivalAirportId: editingFlight.arrivalAirportId,
      });
      setSelectedDepartureAirport(editingFlight.departureAirportId);
      setSelectedArrivalAirport(editingFlight.arrivalAirportId);
      setSelectedPlane(editingFlight.planeId);
    } else {
      reset();
      setSelectedDepartureAirport("");
      setSelectedArrivalAirport("");
      setSelectedPlane("");
    }
  }, [editingFlight, reset]);

  const airportOptions = airports.map((airport) => ({
    value: airport.airportId!,
    label: `${airport.airportName} - ${airport.cityName}`,
    city: airport.cityName,
    name: airport.airportName,
  }));

  const planeOptions = planes.map((plane) => ({
    value: plane.planeId!,
    label: `${plane.planeCode} - ${plane.planeType}`,
    code: plane.planeCode,
    type: plane.planeType,
  }));

  // Validation functions
  const validateFlightCode = (code: string): boolean => {
    const flightCodeRegex = /^[A-Z]{2}-\d{3,4}$/;
    return flightCodeRegex.test(code);
  };

  const validateDuplicateAirports = React.useCallback((): string | null => {
    const allAirports: number[] = [];

    if (selectedDepartureAirport && selectedDepartureAirport !== 0) {
      allAirports.push(Number(selectedDepartureAirport));
    }
    if (selectedArrivalAirport && selectedArrivalAirport !== 0) {
      allAirports.push(Number(selectedArrivalAirport));
    }

    flightDetails.forEach((detail) => {
      if (detail.mediumAirportId && detail.mediumAirportId !== 0) {
        allAirports.push(detail.mediumAirportId);
      }
    });

    const uniqueAirports = new Set(allAirports);
    return uniqueAirports.size !== allAirports.length
      ? "Các sân bay phải đôi một khác nhau."
      : null;
  }, [selectedDepartureAirport, selectedArrivalAirport, flightDetails]);

  const validateFlightDetails = React.useCallback((): {
    [key: string]: string;
  } => {
    const errors: { [key: string]: string } = {};

    if (flightDetails.length === 0) return errors;

    // Check for incomplete details
    const detailsWithAirport = flightDetails.filter(
      (detail) => detail.mediumAirportId && detail.mediumAirportId !== 0
    );

    const hasEmptyTimes = detailsWithAirport.some(
      (detail) => !detail.arrivalTime || detail.arrivalTime === ""
    );

    if (hasEmptyTimes) {
      errors.flightDetails =
        "Vui lòng nhập thời gian đến cho tất cả các điểm dừng đã chọn sân bay.";
      return errors;
    }

    // Check layover duration constraints
    const minLayover = parameters?.minLayoverDuration || 20;
    const maxLayover = parameters?.maxLayoverDuration || 180;
    const maxStops = parameters?.maxMediumAirport || 5;

    const invalidLayovers = detailsWithAirport.filter(
      (detail) =>
        detail.layoverDuration < minLayover ||
        detail.layoverDuration > maxLayover
    );

    if (invalidLayovers.length > 0) {
      errors.layoverDuration = `Thời gian dừng phải trong khoảng từ ${minLayover} đến ${maxLayover} phút.`;
    }

    if (flightDetails.length > maxStops) {
      errors.maxStops = `Số lượng điểm dừng tối đa là ${maxStops}.`;
    }

    // Check time sequence
    if (
      watchDepartureTime &&
      watchArrivalTime &&
      detailsWithAirport.length > 0
    ) {
      const mainDeparture = new Date(watchDepartureTime);
      const mainArrival = new Date(watchArrivalTime);

      const sortedDetails = [...detailsWithAirport]
        .filter((detail) => detail.arrivalTime)
        .sort(
          (a, b) =>
            new Date(a.arrivalTime).getTime() -
            new Date(b.arrivalTime).getTime()
        );

      for (let i = 0; i < sortedDetails.length; i++) {
        const detail = sortedDetails[i];
        const stopArrival = new Date(detail.arrivalTime);
        const stopDeparture = new Date(
          stopArrival.getTime() + (detail.layoverDuration || 0) * 60000
        );

        // Check if within flight window
        if (stopArrival <= mainDeparture || stopDeparture >= mainArrival) {
          errors.stopoverTimes =
            "Thời gian các điểm dừng phải nằm trong khoảng thời gian bay của chuyến bay.";
          break;
        }

        // Check sequence with previous stop
        if (i > 0) {
          const prevDetail = sortedDetails[i - 1];
          const prevDeparture = new Date(
            new Date(prevDetail.arrivalTime).getTime() +
              (prevDetail.layoverDuration || 0) * 60000
          );

          if (stopArrival <= prevDeparture) {
            errors.stopoverTimes =
              "Thời gian đến điểm dừng phải sau thời gian rời khỏi điểm dừng trước đó.";
            break;
          }
        }
      }
    }

    return errors;
  }, [flightDetails, parameters, watchDepartureTime, watchArrivalTime]);

  // Combined validation effect
  useEffect(() => {
    const newFormErrors: { [key: string]: string } = {};
    const newDetailErrors: { [key: string]: string } = {};

    // Validate duplicate airports
    const duplicateError = validateDuplicateAirports();
    if (duplicateError) {
      newFormErrors.airportDuplicates = duplicateError;
    }

    // Validate flight details
    const detailValidationErrors = validateFlightDetails();
    Object.assign(newDetailErrors, detailValidationErrors);

    // Update errors
    setFormErrors(newFormErrors);
    setDetailErrors(newDetailErrors);
  }, [
    selectedDepartureAirport,
    selectedArrivalAirport,
    flightDetails,
    watchDepartureTime,
    watchArrivalTime,
    parameters,
    validateDuplicateAirports,
    validateFlightDetails,
    setFormErrors,
    setDetailErrors,
  ]);

  const submitWithValidation = (data: FlightRequest) => {
    // Check for any existing errors
    if (
      Object.keys(formErrors).length > 0 ||
      Object.keys(detailErrors).length > 0
    ) {
      return;
    }

    // Final validation for flight duration
    const durationMinutes =
      (new Date(data.arrivalTime).getTime() -
        new Date(data.departureTime).getTime()) /
      (1000 * 60);
    const minDuration = parameters?.minFlightDuration || 30;

    // [UNCOMMENT FOR DEPARTURE - ARRIVAL TIME CHECK]
    // if (durationMinutes < minDuration) {
    //   setFormErrors((prev) => ({
    //     ...prev,
    //     flightDuration: `Thời gian bay tối thiểu là ${minDuration} phút theo quy định.`,
    //   }));
    //   return;
    // }

    onSubmit(data);
  };

  return (
    <Form onSubmit={handleSubmit(submitWithValidation)}>
      {/* Display main form errors */}
      {Object.keys(formErrors).length > 0 && (
        <Alert variant="danger" className="mb-3">
          <i className="bi bi-exclamation-triangle-fill me-2"></i>
          <div>
            {Object.entries(formErrors).map(([key, error], index) => (
              <div key={key}>
                {error}
                {index < Object.entries(formErrors).length - 1 && (
                  <hr className="my-2" />
                )}
              </div>
            ))}
          </div>
        </Alert>
      )}

      {/* Basic Flight Information */}
      <Row className="mb-3">
        <Col>
          <Form.Group>
            <Form.Label>Mã chuyến bay</Form.Label>
            <Form.Control
              type="text"
              {...register("flightCode", {
                required: "Mã chuyến bay là bắt buộc",
                validate: (value) =>
                  validateFlightCode(value) ||
                  "Mã chuyến bay không hợp lệ. Định dạng phải là 2 chữ cái + 3-4 số (VD: VN-123, QH-1234)",
              })}
              isInvalid={!!errors.flightCode}
              placeholder="vd: VN-123"
            />
            <Form.Control.Feedback type="invalid">
              {errors.flightCode?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>

        <Col>
          <Form.Group>
            <Form.Label>Sân bay đi</Form.Label>
            <TypeAhead
              options={airportOptions}
              value={selectedDepartureAirport}
              allowClear={true}
              onChange={(option) => {
                const airportId = (option?.value as number) || "";
                setSelectedDepartureAirport(airportId);
                setValue("departureAirportId", Number(airportId), {
                  shouldValidate: true,
                });
              }}
              placeholder="Tìm sân bay đi..."
              error={!!errors.departureAirportId}
            />
            <input
              type="hidden"
              {...register("departureAirportId", {
                required: "Sân bay đi là bắt buộc",
                valueAsNumber: true,
                validate: (value) => {
                  if (value === Number(watchArrivalAirport)) {
                    return "Sân bay đi và sân bay đến không thể là cùng một sân bay.";
                  }
                  return true;
                },
              })}
            />
            {errors.departureAirportId && (
              <div className="text-danger small mt-1">
                {errors.departureAirportId.message}
              </div>
            )}
          </Form.Group>
        </Col>

        <Col>
          <Form.Group>
            <Form.Label>Sân bay đến</Form.Label>
            <TypeAhead
              options={airportOptions}
              value={selectedArrivalAirport}
              allowClear={true}
              onChange={(option) => {
                const airportId = (option?.value as number) || "";
                setSelectedArrivalAirport(airportId);
                setValue("arrivalAirportId", Number(airportId), {
                  shouldValidate: true,
                });
              }}
              placeholder="Tìm sân bay đến..."
              error={!!errors.arrivalAirportId}
            />
            <input
              type="hidden"
              {...register("arrivalAirportId", {
                required: "Sân bay đến là bắt buộc",
                valueAsNumber: true,
                validate: (value) => {
                  if (value === Number(watchDepartureAirport)) {
                    return "Sân bay đi và sân bay đến không thể là cùng một sân bay.";
                  }
                  return true;
                },
              })}
            />
            {errors.arrivalAirportId && (
              <div className="text-danger small mt-1">
                {errors.arrivalAirportId.message}
              </div>
            )}
          </Form.Group>
        </Col>
      </Row>

      {/* Times and Aircraft */}
      <Row className="mb-3">
        <Col>
          <Form.Group>
            <Form.Label>Thời gian khởi hành</Form.Label>
            <Form.Control
              type="datetime-local"
              {...register("departureTime", {
                required: "Thời gian khởi hành là bắt buộc",
                validate: {
                  futureDate: (value) => {
                    const now = new Date();
                    now.setMinutes(now.getMinutes() + 15);
                    return (
                      new Date(value) >= now ||
                      "Thời gian khởi hành không thể trong quá khứ."
                    );
                  },
                  // [UNCOMMENT FOR DEPARTURE - ARRIVAL TIME CHECK]
                  // beforeArrival: (value) => {
                  //   if (!watchArrivalTime) return true;
                  //   return (
                  //     new Date(value) < new Date(watchArrivalTime) ||
                  //     "Thời gian đến phải sau thời gian khởi hành."
                  //   );
                  // },
                },
              })}
              isInvalid={!!errors.departureTime}
            />
            <Form.Control.Feedback type="invalid">
              {errors.departureTime?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>

        <Col>
          <Form.Group>
            <Form.Label>Thời gian đến</Form.Label>
            <Form.Control
              type="datetime-local"
              {...register("arrivalTime", {
                required: "Thời gian đến là bắt buộc",
                validate: {
                  // [UNCOMMENT FOR DEPARTURE - ARRIVAL TIME CHECK]
                  // afterDeparture: (value) => {
                  //   if (!watchDepartureTime) return true;
                  //   return (
                  //     new Date(value) > new Date(watchDepartureTime) ||
                  //     "Thời gian đến phải sau thời gian khởi hành."
                  //   );
                  // },
                  // [UNCOMMENT FOR DEPARTURE - ARRIVAL TIME CHECK]
                  // minFlightDuration: (value) => {
                  //   if (!watchDepartureTime) return true;
                  //   const durationMinutes =
                  //     (new Date(value).getTime() -
                  //       new Date(watchDepartureTime).getTime()) /
                  //     (1000 * 60);
                  //   const minDuration = parameters?.minFlightDuration || 30;
                  //   return (
                  //     durationMinutes >= minDuration ||
                  //     `Thời gian bay tối thiểu là ${minDuration} phút theo quy định.`
                  //   );
                  // },
                },
              })}
              isInvalid={!!errors.arrivalTime}
            />
            <Form.Control.Feedback type="invalid">
              {errors.arrivalTime?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
      </Row>

      <Row className="mb-3">
        <Col>
          <Form.Group>
            <Form.Label>Máy bay</Form.Label>
            <TypeAhead
              options={planeOptions}
              value={selectedPlane}
              allowClear={true}
              onChange={(option) => {
                const planeId = (option?.value as number) || "";
                setSelectedPlane(planeId);
                setValue("planeId", Number(planeId), {
                  shouldValidate: true,
                });
              }}
              placeholder="Tìm máy bay..."
              error={!!errors.planeId}
            />
            <input
              type="hidden"
              {...register("planeId", {
                required: "Bắt buộc chọn máy bay",
                valueAsNumber: true,
              })}
            />
            {errors.planeId && (
              <div className="text-danger small mt-1">
                {errors.planeId.message}
              </div>
            )}
          </Form.Group>
        </Col>
      </Row>

      {/* Flight Details Table */}
      <Row className="mb-3 mx-auto" style={{ overflow: "visible" }}>
        {Object.keys(detailErrors).length > 0 && (
          <Alert variant="danger" className="mb-3">
            <i className="bi bi-exclamation-triangle-fill me-2"></i>
            <div>
              {Object.entries(detailErrors).map(([key, error], index) => (
                <div key={key}>
                  {error}
                  {index < Object.entries(detailErrors).length - 1 && (
                    <hr className="my-2" />
                  )}
                </div>
              ))}
            </div>
          </Alert>
        )}

        <FlightDetailsTable
          flightDetails={flightDetails}
          airportOptions={airportOptions}
          selectedDepartureAirport={selectedDepartureAirport}
          selectedArrivalAirport={selectedArrivalAirport}
          onAddRow={onAddFlightDetail}
          onRemoveRow={onRemoveFlightDetail}
          onDetailChange={onFlightDetailChange}
          parameters={parameters}
        />
      </Row>

      {/* Form Actions */}
      <Row className="mb-3">
        <Col>
          <Button variant="secondary" onClick={onCancel} className="w-100">
            Hủy
          </Button>
        </Col>
        <Col>
          <Button variant="primary" type="submit" className="w-100">
            {editingFlight ? "Cập nhật chuyến bay" : "Thêm chuyến bay"}
          </Button>
        </Col>
      </Row>
    </Form>
  );
};

export default FlightForm;
