import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner } from 'react-bootstrap';
import { parameterService } from '../../services';
import { usePermissions } from '../../hooks/useAuth';

interface ParameterFormData {
    maxMediumAirport: number;
    minFlightDuration: number;
    maxLayoverDuration: number;
    minLayoverDuration: number;
    minBookingInAdvanceDuration: number;
    maxBookingHoldDuration: number;
}

export const ParameterSettings: React.FC = () => {
    const { canViewAdmin } = usePermissions();
    if (!canViewAdmin) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8}>
                        <Alert variant="danger" className="text-center">
                            <Alert.Heading>Access Denied</Alert.Heading>
                            <p>You do not have permission to access system parameter settings.</p>
                        </Alert>
                    </Col>
                </Row>
            </Container>
        );
    }

    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState('');

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors }
    } = useForm<ParameterFormData>();

    // Load parameters on component mount
    useEffect(() => {
        loadParameters();
    }, []);

    const loadParameters = async () => {
        setLoading(true);
        setError(null);
        try {
            const dataArr = await parameterService.getAllParameters();
            const data = Array.isArray(dataArr) ? dataArr[0] : dataArr;
            reset(data);
        } catch (err: any) {
            setError('Failed to load parameters');
        } finally {
            setLoading(false);
        }
    };

    const onSubmit = async (data: ParameterFormData) => {
        try {
            setSaving(true);
            setError('');
            setSuccess('');
            
            // Update each parameter individually
            await Promise.all([
                parameterService.updateMaxMediumAirports(data.maxMediumAirport),
                parameterService.updateMinFlightDuration(data.minFlightDuration),
                parameterService.updateMaxLayoverDuration(data.maxLayoverDuration),
                parameterService.updateMinLayoverDuration(data.minLayoverDuration),
                parameterService.updateMinBookingAdvance(data.minBookingInAdvanceDuration),
                parameterService.updateMaxBookingHold(data.maxBookingHoldDuration)
            ]);
            
            setSuccess('Parameters updated successfully');
            loadParameters();
        } catch (err: any) {
            setError(err.message || 'Failed to update parameters');
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8} className="text-center">
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                        <p className="mt-3">Loading system parameters...</p>
                    </Col>
                </Row>
            </Container>
        );
    }

    return (
        <Container className="py-4" style={{ maxWidth: '800px' }}>
            <Card className="mb-4">
                <Card.Header className="text-center">
                    <Card.Title as="h2" className="mb-2">System Parameters</Card.Title>
                    <p className="text-muted mb-0">Configure flight management system constraints and rules</p>
                </Card.Header>
            </Card>

            {error && (
                <Alert variant="danger" className="mb-4">
                    {error}
                </Alert>
            )}
            
            {success && (
                <Alert variant="success" className="mb-4">
                    {success}
                </Alert>
            )}

            <Card>
                <Card.Body className="p-4">
                    <Form onSubmit={handleSubmit(onSubmit)}>
                        {/* Flight Constraints Section */}
                        <div className="mb-5 pb-4 border-bottom">
                            <h5 className="text-primary mb-4">
                                <span className="me-2">⚙️</span>
                                Flight Constraints
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Maximum Medium Airports</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="0"
                                            max="10"
                                            {...register('maxMediumAirport', {
                                                required: 'Maximum medium airports is required',
                                                min: { value: 0, message: 'Value must be at least 0' },
                                                max: { value: 10, message: 'Value must be at most 10' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.maxMediumAirport}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.maxMediumAirport?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Maximum number of intermediate stops allowed per flight
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Minimum Flight Duration (minutes)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="30"
                                            max="1440"
                                            {...register('minFlightDuration', {
                                                required: 'Minimum flight duration is required',
                                                min: { value: 30, message: 'Minimum duration is 30 minutes' },
                                                max: { value: 1440, message: 'Maximum duration is 24 hours' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minFlightDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minFlightDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Minimum allowed flight duration
                                        </Form.Text>
                                    </Form.Group>
                                </Col>
                            </Row>
                        </div>

                        {/* Layover Settings Section */}
                        <div className="mb-5 pb-4 border-bottom">
                            <h5 className="text-primary mb-4">
                                <span className="me-2">🔄</span>
                                Layover Settings
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Minimum Layover Duration (minutes)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="30"
                                            max="720"
                                            {...register('minLayoverDuration', {
                                                required: 'Minimum layover duration is required',
                                                min: { value: 30, message: 'Minimum layover is 30 minutes' },
                                                max: { value: 720, message: 'Maximum layover is 12 hours' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minLayoverDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minLayoverDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Minimum time required between connecting flights
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Maximum Layover Duration (minutes)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="60"
                                            max="1440"
                                            {...register('maxLayoverDuration', {
                                                required: 'Maximum layover duration is required',
                                                min: { value: 60, message: 'Minimum duration is 60 minutes' },
                                                max: { value: 1440, message: 'Maximum duration is 24 hours' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.maxLayoverDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.maxLayoverDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Maximum allowed layover time
                                        </Form.Text>
                                    </Form.Group>
                                </Col>
                            </Row>
                        </div>

                        {/* Booking Rules Section */}
                        <div className="mb-4">
                            <h5 className="text-primary mb-4">
                                <span className="me-2">📅</span>
                                Booking Rules
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Minimum Booking Advance (minutes)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="60"
                                            max="10080"
                                            {...register('minBookingInAdvanceDuration', {
                                                required: 'Minimum booking advance is required',
                                                min: { value: 60, message: 'Minimum advance is 1 hour' },
                                                max: { value: 10080, message: 'Maximum advance is 1 week' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minBookingInAdvanceDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minBookingInAdvanceDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Minimum time before departure to allow booking
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Maximum Booking Hold (minutes)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="15"
                                            max="1440"
                                            {...register('maxBookingHoldDuration', {
                                                required: 'Maximum booking hold is required',
                                                min: { value: 15, message: 'Minimum hold is 15 minutes' },
                                                max: { value: 1440, message: 'Maximum hold is 24 hours' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.maxBookingHoldDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.maxBookingHoldDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Maximum time to hold a booking before payment
                                        </Form.Text>
                                    </Form.Group>
                                </Col>
                            </Row>
                        </div>

                        <div className="d-flex justify-content-center mt-4">
                            <Button type="submit" variant="primary" disabled={saving} size="lg">
                                {saving ? (
                                    <>
                                        <Spinner
                                            as="span"
                                            animation="border"
                                            size="sm"
                                            role="status"
                                            aria-hidden="true"
                                            className="me-2"
                                        />
                                        Saving...
                                    </>
                                ) : (
                                    'Save Parameters'
                                )}
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default ParameterSettings;
