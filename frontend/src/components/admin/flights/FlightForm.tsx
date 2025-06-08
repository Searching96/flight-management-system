import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Row, Col, Form, Button, Alert} from 'react-bootstrap';
import { Flight, FlightRequest, Airport, Plane, Parameter } from '../../../models';
import TypeAhead from '../../common/TypeAhead';
import { FlightDetailWithIndex } from '../../../hooks/useFlightDetails';
import FlightDetailsTable from './FlightDetailsTable';

interface FlightFormProps {
    editingFlight: Flight | null;
    airports: Airport[];
    planes: Plane[];
    flightDetails: FlightDetailWithIndex[];
    formErrors: { [key: string]: string }; // Main form errors
    detailErrors: { [key: string]: string }; // Flight details specific errors
    setFormErrors: React.Dispatch<React.SetStateAction<{ [key: string]: string }>>;
    setDetailErrors: React.Dispatch<React.SetStateAction<{ [key: string]: string }>>;
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
    parameters
}) => {
    const [selectedDepartureAirport, setSelectedDepartureAirport] = useState<number | ''>(
        editingFlight?.departureAirportId || ''
    );
    const [selectedArrivalAirport, setSelectedArrivalAirport] = useState<number | ''>(
        editingFlight?.arrivalAirportId || ''
    );
    const [selectedPlane, setSelectedPlane] = useState<number | ''>(
        editingFlight?.planeId || ''
    );

    const {
        register,
        handleSubmit,
        reset,
        setValue,
        getValues,
        watch,
        formState: { errors }
    } = useForm<FlightRequest>({
        mode: 'onChange' // Enable validation on change
    });

    // Watch values for cross-field validation
    const watchDepartureTime = watch('departureTime');
    const watchArrivalTime = watch('arrivalTime');
    const watchDepartureAirport = watch('departureAirportId');
    const watchArrivalAirport = watch('arrivalAirportId');

    // Reset form when editing flight changes
    useEffect(() => {
        if (editingFlight) {
            reset({
                flightCode: editingFlight.flightCode,
                departureTime: editingFlight.departureTime.slice(0, 16),
                arrivalTime: editingFlight.arrivalTime.slice(0, 16),
                planeId: editingFlight.planeId,
                departureAirportId: editingFlight.departureAirportId,
                arrivalAirportId: editingFlight.arrivalAirportId
            });
            setSelectedDepartureAirport(editingFlight.departureAirportId);
            setSelectedArrivalAirport(editingFlight.arrivalAirportId);
            setSelectedPlane(editingFlight.planeId);
        } else {
            reset();
            setSelectedDepartureAirport('');
            setSelectedArrivalAirport('');
            setSelectedPlane('');
        }
    }, [editingFlight, reset]);

    const airportOptions = airports.map(airport => ({
        value: airport.airportId!,
        label: `${airport.cityName} - ${airport.airportName}`,
        city: airport.cityName,
        name: airport.airportName
    }));

    const planeOptions = planes.map(plane => ({
        value: plane.planeId!,
        label: `${plane.planeCode} - ${plane.planeType}`,
        code: plane.planeCode,
        type: plane.planeType
    }));

    // Helper function to validate flight code format
    const validateFlightCode = (code: string): boolean => {
        const flightCodeRegex = /^[A-Z]{2}\d{3,4}$/;
        return flightCodeRegex.test(code);
    };

    // Enhanced validation for stopovers
    useEffect(() => {
        if (flightDetails.length < 2) return;

        const departureTime = getValues('departureTime');
        const arrivalTime = getValues('arrivalTime');

        if (!departureTime || !arrivalTime) return;

        const mainDepartureTime = new Date(departureTime);
        const mainArrivalTime = new Date(arrivalTime);

        // Sort details by arrival time for checking sequence
        const sortedDetails = [...flightDetails]
            .filter(detail => detail.arrivalTime)
            .sort((a, b) => new Date(a.arrivalTime).getTime() - new Date(b.arrivalTime).getTime());

        // Check if all details are between main departure and arrival times
        const allTimesInSequence = sortedDetails.every(detail => {
            const stopTime = new Date(detail.arrivalTime);
            return stopTime > mainDepartureTime && stopTime < mainArrivalTime;
        });

        // Check if stopover times are in sequence
        const stopTimesInSequence = sortedDetails.every((detail, index, array) => {
            if (index === 0) return true;
            const prevStop = new Date(array[index - 1].arrivalTime);
            const currentStop = new Date(detail.arrivalTime);
            return currentStop > prevStop;
        });

        if (!allTimesInSequence || !stopTimesInSequence) {
            setFormErrors(prev => ({
                ...prev,
                stopoverTimes: 'Thời gian các điểm dừng phải nằm giữa thời gian khởi hành và đến, và phải theo đúng trình tự.'
            }));
        } else {
            setFormErrors(prev => {
                const { stopoverTimes, ...rest } = prev;
                return rest;
            });
        }
    }, [flightDetails, getValues, setFormErrors]);

    // Validate flight details
    const validateFlightDetails = () => {
        // Reset detail errors first
        setDetailErrors(prev => ({ ...prev, flightDetails: '' }));

        // Check for duplicate airports - this error affects main form
        if (checkDuplicateAirports()) {
            setFormErrors(prev => ({
                ...prev,
                airportDuplicates: 'Các sân bay phải đôi một khác nhau.'
            }));
            return false;
        } else {
            setFormErrors(prev => {
                const { airportDuplicates, ...rest } = prev;
                return rest;
            });
        }

        // Check for empty airport selections
        const hasEmptyAirports = flightDetails.some(detail =>
            !detail.mediumAirportId || detail.mediumAirportId === 0
        );

        if (hasEmptyAirports && flightDetails.length > 0) {
            setDetailErrors(prev => ({
                ...prev,
                flightDetails: 'Vui lòng chọn sân bay cho tất cả các điểm dừng.'
            }));
            return false;
        }

        // Check for empty arrival times
        const hasEmptyTimes = flightDetails.some(detail =>
            !detail.arrivalTime || detail.arrivalTime === ''
        );

        if (hasEmptyTimes && flightDetails.length > 0) {
            setDetailErrors(prev => ({
                ...prev,
                flightDetails: 'Vui lòng nhập thời gian đến cho tất cả các điểm dừng.'
            }));
            return false;
        }

        return true;
    };

    // Check for duplicate airports
    const checkDuplicateAirports = () => {
        // Collect all selected airports (departure, arrival, and stopovers)
        const allAirports: number[] = [];

        if (selectedDepartureAirport && selectedDepartureAirport !== 0) {
            allAirports.push(Number(selectedDepartureAirport));
        }

        if (selectedArrivalAirport && selectedArrivalAirport !== 0) {
            allAirports.push(Number(selectedArrivalAirport));
        }

        // Add stopover airports
        flightDetails.forEach(detail => {
            if (detail.mediumAirportId && detail.mediumAirportId !== 0) {
                allAirports.push(detail.mediumAirportId);
            }
        });

        // Check for duplicates
        const uniqueAirports = new Set(allAirports);
        return uniqueAirports.size !== allAirports.length;
    };

    // Add validation for flight code format
    useEffect(() => {
        const flightCode = getValues('flightCode');
        if (flightCode && !validateFlightCode(flightCode)) {
            setFormErrors(prev => ({
                ...prev,
                flightCode: 'Mã chuyến bay không hợp lệ. Định dạng phải là 2 chữ cái + 3-4 số (VD: VN123, QH1234)'
            }));
        } else {
            setFormErrors(prev => {
                const { flightCode, ...rest } = prev;
                return rest;
            });
        }
    }, [getValues, setFormErrors]);

    // Add validation for time sequence of stopovers - moved to detail errors
    useEffect(() => {
        if (flightDetails.length < 2) {
            // Clear errors if no details
            setDetailErrors(prev => {
                const { stopoverTimes, ...rest } = prev;
                return rest;
            });
            return;
        }

        const departureTime = getValues('departureTime');
        const arrivalTime = getValues('arrivalTime');

        if (!departureTime || !arrivalTime) return;

        const mainDepartureTime = new Date(departureTime);
        const mainArrivalTime = new Date(arrivalTime);

        // Sort details by arrival time for checking sequence
        const sortedDetails = [...flightDetails]
            .filter(detail => detail.arrivalTime)
            .sort((a, b) => new Date(a.arrivalTime).getTime() - new Date(b.arrivalTime).getTime());

        // Check if all details are between main departure and arrival times
        const allTimesInSequence = sortedDetails.every(detail => {
            const stopTime = new Date(detail.arrivalTime);
            return stopTime > mainDepartureTime && stopTime < mainArrivalTime;
        });

        // Check if stopover times are in sequence
        const stopTimesInSequence = sortedDetails.every((detail, index, array) => {
            if (index === 0) return true;
            const prevStop = new Date(array[index - 1].arrivalTime);
            const currentStop = new Date(detail.arrivalTime);
            return currentStop > prevStop;
        });

        if (!allTimesInSequence || !stopTimesInSequence) {
            setDetailErrors(prev => ({
                ...prev,
                stopoverTimes: 'Thời gian các điểm dừng phải nằm giữa thời gian khởi hành và đến, và phải theo đúng trình tự.'
            }));
        } else {
            setDetailErrors(prev => {
                const { stopoverTimes, ...rest } = prev;
                return rest;
            });
        }
    }, [flightDetails, getValues, setDetailErrors]);

    // Add validation for future flights (can't create flights in the past)
    useEffect(() => {
        const departureTime = getValues('departureTime');
        if (departureTime) {
            const departureDate = new Date(departureTime);
            const now = new Date();

            // Allow a small buffer (e.g., 15 minutes) for form submission
            now.setMinutes(now.getMinutes() + 15);

            if (departureDate < now) {
                setFormErrors(prev => ({
                    ...prev,
                    pastFlight: 'Thời gian khởi hành không thể trong quá khứ.'
                }));
            } else {
                setFormErrors(prev => {
                    const { pastFlight, ...rest } = prev;
                    return rest;
                });
            }
        }
    }, [getValues, setFormErrors]);

    // Add validation for minimum layover duration at each stopover - moved to detail errors
    useEffect(() => {
        if (flightDetails.length === 0) {
            setDetailErrors(prev => {
                const { layoverDuration, ...rest } = prev;
                return rest;
            });
            return;
        }

        const invalidLayovers = flightDetails.filter(detail => {
            return detail.layoverDuration !== undefined && 
                   detail.layoverDuration < (parameters?.minLayoverDuration || 20);
        });

        if (invalidLayovers.length > 0) {
            setDetailErrors(prev => ({
                ...prev,
                layoverDuration: `Thời gian dừng tối thiểu là ${parameters?.minLayoverDuration || 20} phút cho mỗi sân bay trung gian.`
            }));
        } else {
            setDetailErrors(prev => {
                const { layoverDuration, ...rest } = prev;
                return rest;
            });
        }
        
        // Check for exceeding max layovers
        const maxLayers = parameters?.maxMediumAirport || 5;
        if (flightDetails.length > maxLayers) {
            setDetailErrors(prev => ({
                ...prev,
                maxStops: `Số lượng điểm dừng tối đa là ${maxLayers}.`
            }));
        } else {
            setDetailErrors(prev => {
                const { maxStops, ...rest } = prev;
                return rest;
            });
        }
    }, [flightDetails, setDetailErrors, parameters]);

    // Handle form submission with validation
    const submitWithValidation = (data: FlightRequest) => {
        // Validate flight details
        if (!validateFlightDetails()) {
            return;
        }

        // Check all stopovers have proper times and layover durations
        if (flightDetails.length > 0) {

            // Validate stopover sequence
            const sortedDetails = [...flightDetails]
                .filter(detail => detail.arrivalTime)
                .sort((a, b) => new Date(a.arrivalTime).getTime() - new Date(b.arrivalTime).getTime());

            let inSequence = true;
            for (let i = 0; i < sortedDetails.length; i++) {
                const stopTime = new Date(sortedDetails[i].arrivalTime);

                // Check if within flight time window
                if (stopTime.getTime() <= new Date(data.departureTime).getTime() || stopTime.getTime() >= new Date(data.arrivalTime).getTime()) {
                    inSequence = false;
                    break;
                }

                // Check sequential order
                if (i > 0) {
                    const prevStop = new Date(sortedDetails[i - 1].arrivalTime);
                    if (stopTime <= prevStop) {
                        inSequence = false;
                        break;
                    }
                }

                // Validate minimum layover
                if (sortedDetails[i].layoverDuration < 20) {
                    setFormErrors(prev => ({
                        ...prev,
                        layoverDuration: 'Thời gian dừng tối thiểu là 20 phút cho mỗi sân bay trung gian.'
                    }));
                    return;
                }
            }

            if (!inSequence) {
                setFormErrors(prev => ({
                    ...prev,
                    stopoverTimes: 'Thời gian các điểm dừng phải nằm giữa thời gian khởi hành và đến, và phải theo đúng trình tự.'
                }));
                return;
            }

            // Validate layovers using system parameters
            const invalidLayovers = flightDetails.filter(detail => {
                return detail.layoverDuration < (parameters?.minLayoverDuration || 20) ||
                       detail.layoverDuration > (parameters?.maxLayoverDuration || 180);
            });

            if (invalidLayovers.length > 0) {
                setFormErrors(prev => ({
                    ...prev,
                    layoverDuration: `Thời gian dừng phải trong khoảng từ ${parameters?.minLayoverDuration || 20} đến ${parameters?.maxLayoverDuration || 180} phút.`
                }));
                return;
            }
        }

        // Flight time total validation
        const durationMinutes = (new Date(data.arrivalTime).getTime() - new Date(data.departureTime).getTime()) / (1000 * 60);
        
        if (durationMinutes < (parameters?.minFlightDuration || 30)) {
            setFormErrors(prev => ({
                ...prev,
                flightDuration: `Thời gian bay tối thiểu là ${parameters?.minFlightDuration || 30} phút theo quy định.`
            }));
            return;
        } else if (durationMinutes > 24 * 60) { // 24 hours in minutes
            setFormErrors(prev => ({
                ...prev,
                flightDuration: 'Thời gian bay không nên vượt quá 24 giờ.'
            }));
            return;
        }

        // All validation passed, call the onSubmit prop
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
                                {index < Object.entries(formErrors).length - 1 && <hr className="my-2" />}
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
                            {...register('flightCode', {
                                required: 'Mã chuyến bay là bắt buộc',
                                validate: (value) => validateFlightCode(value) || 
                                    'Mã chuyến bay không hợp lệ. Định dạng phải là 2 chữ cái + 3-4 số (VD: VN123, QH1234)'
                            })}
                            isInvalid={!!errors.flightCode}
                            placeholder="vd: VN123"
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
                            onChange={(option) => {
                                const airportId = option?.value as number || '';
                                setSelectedDepartureAirport(airportId);
                                setValue('departureAirportId', Number(airportId), { 
                                    shouldValidate: true 
                                });
                            }}
                            placeholder="Tìm sân bay đi..."
                            error={!!errors.departureAirportId}
                        />
                        <input
                            type="hidden"
                            {...register('departureAirportId', {
                                required: 'Sân bay đi là bắt buộc',
                                valueAsNumber: true,
                                validate: (value) => {
                                    if (value === Number(watchArrivalAirport)) {
                                        return 'Sân bay đi và sân bay đến không thể là cùng một sân bay.';
                                    }
                                    return true;
                                }
                            })}
                        />
                        {errors.departureAirportId && (
                            <div className="text-danger small mt-1">{errors.departureAirportId.message}</div>
                        )}
                    </Form.Group>
                </Col>

                <Col>
                    <Form.Group>
                        <Form.Label>Sân bay đến</Form.Label>
                        <TypeAhead
                            options={airportOptions}
                            value={selectedArrivalAirport}
                            onChange={(option) => {
                                const airportId = option?.value as number || '';
                                setSelectedArrivalAirport(airportId);
                                setValue('arrivalAirportId', Number(airportId), { 
                                    shouldValidate: true 
                                });
                            }}
                            placeholder="Tìm sân bay đến..."
                            error={!!errors.arrivalAirportId}
                        />
                        <input
                            type="hidden"
                            {...register('arrivalAirportId', {
                                required: 'Sân bay đến là bắt buộc',
                                valueAsNumber: true,
                                validate: (value) => {
                                    if (value === Number(watchDepartureAirport)) {
                                        return 'Sân bay đi và sân bay đến không thể là cùng một sân bay.';
                                    }
                                    return true;
                                }
                            })}
                        />
                        {errors.arrivalAirportId && (
                            <div className="text-danger small mt-1">{errors.arrivalAirportId.message}</div>
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
                            {...register('departureTime', {
                                required: 'Thời gian khởi hành là bắt buộc',
                                validate: {
                                    futureDate: (value) => {
                                        const now = new Date();
                                        now.setMinutes(now.getMinutes() + 15); // Add buffer
                                        return new Date(value) >= now || 
                                            'Thời gian khởi hành không thể trong quá khứ.';
                                    },
                                    beforeArrival: (value) => {
                                        if (!watchArrivalTime) return true;
                                        return new Date(value) < new Date(watchArrivalTime) || 
                                            'Thời gian đến phải sau thời gian khởi hành.';
                                    }
                                }
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
                            {...register('arrivalTime', {
                                required: 'Thời gian đến là bắt buộc',
                                validate: {
                                    afterDeparture: (value) => {
                                        if (!watchDepartureTime) return true;
                                        return new Date(value) > new Date(watchDepartureTime) || 
                                            'Thời gian đến phải sau thời gian khởi hành.';
                                    },
                                    flightDuration: (value) => {
                                        if (!watchDepartureTime) return true;
                                        const departureDate = new Date(watchDepartureTime);
                                        const arrivalDate = new Date(value);
                                        const durationMinutes = (arrivalDate.getTime() - departureDate.getTime()) / (1000 * 60);
                                        
                                        if (durationMinutes < (parameters?.minFlightDuration || 30)) {
                                            return `Thời gian bay tối thiểu là ${parameters?.minFlightDuration || 30} phút theo quy định.`;
                                        }
                                        
                                        if (durationMinutes > 24 * 60) {
                                            return 'Thời gian bay không nên vượt quá 24 giờ.';
                                        }
                                        
                                        return true;
                                    }
                                }
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
                            onChange={(option) => {
                                const planeId = option?.value as number || '';
                                setSelectedPlane(planeId);
                                setValue('planeId', Number(planeId), { 
                                    shouldValidate: true 
                                });
                            }}
                            placeholder="Tìm máy bay..."
                            error={!!errors.planeId}
                        />
                        <input
                            type="hidden"
                            {...register('planeId', {
                                required: 'Bắt buộc chọn máy bay',
                                valueAsNumber: true
                            })}
                        />
                        {errors.planeId && (
                            <div className="text-danger small mt-1">{errors.planeId.message}</div>
                        )}
                    </Form.Group>
                </Col>
            </Row>

            {/* Flight Details Table */}
            <Row className="mb-3 mx-auto" style={{ overflow: 'visible' }}>
                {/* Display detail-specific errors */}
                {Object.keys(detailErrors).length > 0 && (
                    <Alert variant="danger" className="mb-3">
                        <i className="bi bi-exclamation-triangle-fill me-2"></i>
                        <div>
                            {Object.entries(detailErrors).map(([key, error], index) => (
                                <div key={key}>
                                    {error}
                                    {index < Object.entries(detailErrors).length - 1 && <hr className="my-2" />}
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
                        {editingFlight ? 'Cập nhật chuyến bay' : 'Thêm chuyến bay'}
                    </Button>
                </Col>
            </Row>
        </Form>
    );
};

export default FlightForm;
