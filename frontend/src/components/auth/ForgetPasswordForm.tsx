import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { authService } from '../../services/authService';

interface ForgetPasswordFormData {
    email: string;
    phoneNumber: string;
}

const ForgetPasswordForm: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors }
    } = useForm<ForgetPasswordFormData>();

    const onSubmit = async (data: ForgetPasswordFormData) => {
        try {
            setLoading(true);
            setError('');
            setSuccess(false);
            await authService.forgetPassword(data.email, data.phoneNumber);
            setSuccess(true);
        } catch (err: any) {
            const originalMessage = err.response?.data?.message || err.message || 'An error occurred';
            setError(originalMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col md={6} lg={5}>
                    <Card className="shadow">
                        <Card.Body className="p-4">
                            <div className="text-center mb-4">
                                <h2 className="h4 mb-2">Quên mật khẩu</h2>
                                <p className="text-muted">Nhập email của bạn để nhận liên kết đặt lại mật khẩu</p>
                            </div>

                            {success ? (
                                <Alert variant="success">
                                    <Alert.Heading>Email đã gửi!</Alert.Heading>
                                    <p>
                                        Email đặt lại mật khẩu đã được gửi thành công. Vui lòng kiểm tra hộp thư và nhấp vào liên kết để đặt lại mật khẩu.
                                    </p>
                                    <hr />
                                    <div className="d-flex justify-content-end">
                                        <Link to="/login" className="btn btn-outline-success">
                                            Quay lại đăng nhập
                                        </Link>
                                    </div>
                                </Alert>
                            ) : (
                                <Form onSubmit={handleSubmit(onSubmit)}>
                                    {error && <Alert variant="danger">{error}</Alert>}

                                    <Form.Group className="mb-4">
                                        <Form.Label htmlFor="email">Email</Form.Label>
                                        <Form.Control
                                            id="email"
                                            type="email"
                                            {...register('email', {
                                                required: 'Email là bắt buộc',
                                                pattern: { value: /^\S+@\S+\.\S+$/, message: 'Vui lòng nhập địa chỉ email hợp lệ' }
                                            })}
                                            isInvalid={!!errors.email}
                                            placeholder="Nhập email của bạn"
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.email?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>

                                    <Form.Group className="mb-4">
                                        <Form.Label htmlFor="phoneNumber">Số điện thoại</Form.Label>
                                        <Form.Control
                                            id="phoneNumber"
                                            type="text"
                                            {...register('phoneNumber', {
                                                required: 'Số điện thoại là bắt buộc',
                                                pattern: { value: /^[0-9]{10,15}$/, message: 'Vui lòng nhập số điện thoại hợp lệ' }
                                            })}
                                            isInvalid={!!errors.phoneNumber}
                                            placeholder="Nhập số điện thoại của bạn"
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.phoneNumber?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>

                                    <Button
                                        type="submit"
                                        variant="primary"
                                        className="w-100"
                                        disabled={loading}
                                    >
                                        {loading && <Spinner as="span" animation="border" size="sm" className="me-2" />}
                                        {loading ? 'Đang gửi...' : 'Gửi liên kết đặt lại'}
                                    </Button>
                                </Form>
                            )}

                            <div className="text-center mt-4">
                                <p className="text-muted">
                                    Nhớ mật khẩu? <Link to="/login" className="text-decoration-none">Đăng nhập tại đây</Link>
                                </p>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default ForgetPasswordForm;
