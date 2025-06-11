import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner } from 'react-bootstrap';
import { parameterService } from '../../services';
import { usePermissions } from '../../hooks/useAuth';
import { ParameterUpdateRequest } from '../../models';

export const ParameterSettings: React.FC = () => {
    const { canViewAdmin } = usePermissions();
    if (!canViewAdmin) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8}>
                        <Alert variant="danger" className="text-center">
                            <Alert.Heading>Từ chối truy cập</Alert.Heading>
                            <p>Bạn không có quyền truy cập quản lý quy định hệ thống.</p>
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
    } = useForm<ParameterUpdateRequest>();

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
            console.error('Error loading parameters at 2025-06-11 09:12:40 UTC by thinh0704hcm:', err);
            setError('Không thể tải quy định hệ thống');
        } finally {
            setLoading(false);
        }
    };

    const onSubmit = async (data: ParameterUpdateRequest) => {
        try {
            setSaving(true);
            setError('');
            setSuccess('');

            console.log('Updating parameters at 2025-06-11 09:12:40 UTC by thinh0704hcm');
            await parameterService.updateParameters(data);

            setSuccess('Cập nhật quy định thành công!');
            loadParameters();
        } catch (err: any) {
            console.error('Error updating parameters at 2025-06-11 09:12:40 UTC by thinh0704hcm:', err);
            setError(err.message || 'Không thể cập nhật quy định');
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
                            <span className="visually-hidden">Đang tải...</span>
                        </Spinner>
                        <p className="mt-3">Đang tải quy định hệ thống...</p>
                    </Col>
                </Row>
            </Container>
        );
    }

    return (
        <Container className="py-4" style={{ maxWidth: '800px' }}>
            <Card className="mb-4">
                <Card.Header className="text-center bg-primary text-white">
                    <Card.Title as="h2" className="mb-2">
                        <i className="bi bi-gear-fill me-2"></i>
                        Thay đổi quy định hệ thống
                    </Card.Title>
                </Card.Header>
            </Card>

            {error && (
                <Alert variant="danger" className="mb-4">
                    <Alert.Heading>Lỗi</Alert.Heading>
                    {error}
                </Alert>
            )}

            {success && (
                <Alert variant="success" className="mb-4">
                    <Alert.Heading>Thành công</Alert.Heading>
                    {success}
                </Alert>
            )}

            <Card>
                <Card.Body className="p-4">
                    <Form onSubmit={handleSubmit(onSubmit)}>
                        {/* Flight Constraints Section */}
                        <div className="mb-5 pb-4 border-bottom">
                            <h5 className="text-primary mb-4">
                                <span className="me-2">✈️</span>
                                Quy định về chuyến bay
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Số sân bay trung gian tối đa</Form.Label>
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
                                            Quy định số điểm dừng trung gian tối đa được phép cho mỗi chuyến bay
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Thời gian bay tối thiểu (phút)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="30"
                                            max="1440"
                                            {...register('minFlightDuration', {
                                                required: 'Thời gian bay tối thiểu là bắt buộc',
                                                min: { value: 30, message: 'Thời gian tối thiểu là 30 phút' },
                                                max: { value: 1440, message: 'Thời gian tối đa là 24 giờ (1440 phút)' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minFlightDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minFlightDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Quy định thời gian bay tối thiểu được phép cho mỗi chuyến
                                        </Form.Text>
                                    </Form.Group>
                                </Col>
                            </Row>
                        </div>

                        {/* Layover Settings Section */}
                        <div className="mb-5 pb-4 border-bottom">
                            <h5 className="text-primary mb-4">
                                <span className="me-2">🔄</span>
                                Quy định về thời gian dừng chân
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Thời gian dừng chân tối thiểu (phút)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="30"
                                            max="720"
                                            {...register('minLayoverDuration', {
                                                required: 'Thời gian dừng chân tối thiểu là bắt buộc',
                                                min: { value: 30, message: 'Thời gian dừng chân tối thiểu là 30 phút' },
                                                max: { value: 720, message: 'Thời gian dừng chân tối đa là 12 giờ (720 phút)' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minLayoverDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minLayoverDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Quy định thời gian dừng chân tối thiểu cần thiết giữa các chuyến bay kết nối
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Thời gian dừng chân tối đa (phút)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="60"
                                            max="1440"
                                            {...register('maxLayoverDuration', {
                                                required: 'Thời gian dừng chân tối đa là bắt buộc',
                                                min: { value: 60, message: 'Thời gian tối thiểu là 60 phút' },
                                                max: { value: 1440, message: 'Thời gian tối đa là 24 giờ (1440 phút)' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.maxLayoverDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.maxLayoverDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Quy định thời gian dừng chân tối đa được phép
                                        </Form.Text>
                                    </Form.Group>
                                </Col>
                            </Row>
                        </div>

                        {/* Booking Rules Section */}
                        <div className="mb-4">
                            <h5 className="text-primary mb-4">
                                <span className="me-2">📅</span>
                                Quy định về đặt vé và thanh toán
                            </h5>

                            <Row className="g-4">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Thời gian đặt vé trước tối thiểu (ngày)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="0"
                                            {...register('minBookingInAdvanceDuration', {
                                                required: 'Thời gian đặt vé trước tối thiểu là bắt buộc',
                                                min: { value: 0, message: 'Thời gian không được âm' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.minBookingInAdvanceDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.minBookingInAdvanceDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Quy định số ngày tối thiểu khách hàng phải đặt vé trước khi khởi hành
                                        </Form.Text>
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label className="fw-bold">Thời gian giữ chỗ tối đa (giờ)</Form.Label>
                                        <Form.Control
                                            type="number"
                                            min="0"
                                            max="720"
                                            {...register('maxBookingHoldDuration', {
                                                required: 'Thời gian giữ chỗ tối đa là bắt buộc',
                                                max: { value: 720, message: 'Thời gian giữ chỗ tối đa là 720 giờ (30 ngày)' },
                                                valueAsNumber: true
                                            })}
                                            isInvalid={!!errors.maxBookingHoldDuration}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.maxBookingHoldDuration?.message}
                                        </Form.Control.Feedback>
                                        <Form.Text className="text-muted">
                                            Quy định thời gian tối đa hệ thống giữ chỗ trước khi yêu cầu thanh toán
                                        </Form.Text>
                                    </Form.Group>
                                </Col>
                            </Row>
                        </div>

                        <div className="d-flex justify-content-center mt-5">
                            <Button type="submit" variant="primary" disabled={saving} size="lg" className="px-5">
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
                                        Đang lưu quy định...
                                    </>
                                ) : (
                                    <>
                                        <i className="bi bi-check-circle me-2"></i>
                                        Lưu quy định hệ thống
                                    </>
                                )}
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>

            {/* Additional Information Card */}
            <Card className="mt-4">
                <Card.Body className="bg-light">
                    <h6 className="text-primary mb-3">
                        <i className="bi bi-info-circle me-2"></i>
                        Thông tin quan trọng về quy định
                    </h6>
                    <Row>
                        <Col md={6}>
                            <ul className="small text-muted mb-0">
                                <li>Tất cả quy định áp dụng ngay sau khi lưu</li>
                                <li>Thay đổi quy định có thể ảnh hưởng đến đặt vé hiện tại</li>
                                <li>Thời gian tính bằng phút (1 giờ = 60 phút)</li>
                            </ul>
                        </Col>
                        <Col md={6}>
                            <ul className="small text-muted mb-0">
                                <li>Chỉ quản trị viên và nhân viên dịch vụ mới có quyền thay đổi</li>
                                <li>Mọi thay đổi được ghi lại trong hệ thống</li>
                                <li>Liên hệ IT nếu gặp sự cố kỹ thuật</li>
                            </ul>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default ParameterSettings;