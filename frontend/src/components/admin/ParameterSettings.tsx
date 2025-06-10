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
                            <Alert.Heading>Từ chối truy cập</Alert.Heading>
                            <p>Bạn không có quyền truy cập cài đặt tham số hệ thống.</p>
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
                        <p className="mt-3">Đang tải tham số hệ thống...</p>
                    </Col>
                </Row>
            </Container>
        );
    }

    return (
        <Container className="py-4" style={{ maxWidth: '800px' }}>
            <Card className="mb-4">
                <Card.Header className="text-center">
                    <Card.Title as="h2" className="mb-2">Tham số hệ thống</Card.Title>
                    <p className="text-muted mb-0">Cấu hình ràng buộc và quy tắc của hệ thống quản lý chuyến bay</p>
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
                                Ràng buộc chuyến bay
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Số sân bay trung gian tối đa</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="0"
                                            max="10"
                                            {...register('maxMediumAirport', {
                                                required: 'Số sân bay trung gian tối đa là bắt buộc',
                                                min: { value: 0, message: 'Giá trị phải ít nhất là 0' },
                                                max: { value: 10, message: 'Giá trị phải nhiều nhất là 10' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.maxMediumAirport}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.maxMediumAirport?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Số điểm dừng trung gian tối đa được phép mỗi chuyến bay
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Thời gian bay tối thiểu (phút)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="30"
                                            max="1440"
                                            {...register('minFlightDuration', {
                                                required: 'Thời gian bay tối thiểu là bắt buộc',
                                                min: { value: 30, message: 'Thời gian tối thiểu là 30 phút' },
                                                max: { value: 1440, message: 'Thời gian tối đa là 24 giờ' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minFlightDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minFlightDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Thời gian bay tối thiểu được phép
                                        </Form.Text>
                                    </Form.Group>
                                </Col>
                            </Row>
                        </div>

                        {/* Layover Settings Section */}
                        <div className="mb-5 pb-4 border-bottom">
                            <h5 className="text-primary mb-4">
                                <span className="me-2">🔄</span>
                                Cài đặt thời gian dừng
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Thời gian dừng tối thiểu (phút)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="30"
                                            max="720"
                                            {...register('minLayoverDuration', {
                                                required: 'Thời gian dừng tối thiểu là bắt buộc',
                                                min: { value: 30, message: 'Thời gian dừng tối thiểu là 30 phút' },
                                                max: { value: 720, message: 'Thời gian dừng tối đa là 12 giờ' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minLayoverDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minLayoverDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Thời gian tối thiểu cần thiết giữa các chuyến bay kết nối
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Thời gian dừng tối đa (phút)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="60"
                                            max="1440"
                                            {...register('maxLayoverDuration', {
                                                required: 'Thời gian dừng tối đa là bắt buộc',
                                                min: { value: 60, message: 'Thời gian tối thiểu là 60 phút' },
                                                max: { value: 1440, message: 'Thời gian tối đa là 24 giờ' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.maxLayoverDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.maxLayoverDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Thời gian dừng tối đa được phép
                                        </Form.Text>
                                    </Form.Group>
                                </Col>
                            </Row>
                        </div>

                        {/* Booking Rules Section */}
                        <div className="mb-4">
                            <h5 className="text-primary mb-4">
                                <span className="me-2">📅</span>
                                Quy tắc đặt vé
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Thời gian đặt vé tối thiểu (phút)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="60"
                                            max="10080"
                                            {...register('minBookingInAdvanceDuration', {
                                                required: 'Thời gian đặt vé tối thiểu là bắt buộc',
                                                min: { value: 60, message: 'Thời gian tối thiểu là 1 giờ' },
                                                max: { value: 10080, message: 'Thời gian tối đa là 1 tuần' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minBookingInAdvanceDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minBookingInAdvanceDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Thời gian tối thiểu trước khi khởi hành để cho phép đặt vé
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Thời gian giữ vé tối đa (phút)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="15"
                                            max="1440"
                                            {...register('maxBookingHoldDuration', {
                                                required: 'Thời gian giữ vé tối đa là bắt buộc',
                                                min: { value: 15, message: 'Thời gian giữ tối thiểu là 15 phút' },
                                                max: { value: 1440, message: 'Thời gian giữ tối đa là 24 giờ' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.maxBookingHoldDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.maxBookingHoldDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Thời gian tối đa để giữ đặt chỗ trước khi thanh toán
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
                                        Đang lưu...
                                    </>
                                ) : (
                                    'Lưu tham số'
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
