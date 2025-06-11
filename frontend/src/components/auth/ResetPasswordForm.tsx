import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { authService } from '../../services/authService';

interface ResetPasswordFormData {
    password: string;
    confirmPassword: string;
}

const ResetPasswordForm: React.FC = () => {
    const { token } = useParams<{ token: string }>();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [tokenExpired, setTokenExpired] = useState(false);
    const [validatingToken, setValidatingToken] = useState(true);

    const {
        register,
        handleSubmit,
        formState: { errors },
        watch
    } = useForm<ResetPasswordFormData>();

    const password = watch('password');

    useEffect(() => {
        const validateToken = async () => {
            if (!token) {
                setTokenExpired(true);
                setError('Token đặt lại không hợp lệ. Vui lòng yêu cầu đặt lại mật khẩu mới.');
                setValidatingToken(false);
                return;
            }

            try {
                setValidatingToken(true);
                
                if (!await authService.validateResetToken(token)) {
                    setTokenExpired(true);
                    setError('Token đặt lại không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu đặt lại mật khẩu mới.');
                }
            } catch (error) {
                console.error('Token validation error:', error);
                setTokenExpired(true);
                setError('Không thể xác thực token đặt lại. Vui lòng yêu cầu đặt lại mật khẩu mới.');
            } finally {
                setValidatingToken(false);
            }
        };

        validateToken();
    }, [token]);

    const onSubmit = async (data: ResetPasswordFormData) => {
        if (!token) {
            setError('Token đặt lại không hợp lệ. Vui lòng yêu cầu liên kết đặt lại mới.');
            return;
        }

        try {
            setLoading(true);
            setError('');
            await authService.resetPassword(token, data.password);
            navigate('/login', { state: { resetSuccess: true } });
        } catch (err: any) {
            const errorMessage = err.message || 'Không thể đặt lại mật khẩu. Liên kết có thể đã hết hạn.';
            setError(errorMessage);

            // Check if error indicates token expiration or invalidity
            if (errorMessage.includes('expired') || errorMessage.includes('Invalid token') || errorMessage.includes('invalid')) {
                setTokenExpired(true);
            }
        } finally {
            setLoading(false);
        }
    };

    if (!token) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={6} lg={5}>
                        <Card className="shadow">
                            <Card.Body className="p-4 text-center">
                                <Alert variant="danger">
                                    Liên kết đặt lại không hợp lệ. Vui lòng yêu cầu đặt lại mật khẩu mới.
                                </Alert>
                                <Link to="/forget-password" className="btn btn-primary mt-3">
                                    Yêu cầu đặt lại mật khẩu
                                </Link>
                                <div className="text-center mt-4">
                                    <p className="text-muted">
                                        Nhớ mật khẩu của bạn? <Link to="/login" className="text-decoration-none">Đăng nhập tại đây</Link>
                                    </p>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        );
    }

    // Show loading while validating token
    if (validatingToken) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={6} lg={5}>
                        <Card className="shadow">
                            <Card.Body className="p-4 text-center">
                                <Spinner animation="border" role="status">
                                    <span className="visually-hidden">Đang xác thực liên kết đặt lại...</span>
                                </Spinner>
                                <p className="mt-3 text-muted">Đang xác thực liên kết đặt lại...</p>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        );
    }

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col md={6} lg={5}>
                    <Card className="shadow">
                        <Card.Body className="p-4">
                            {!tokenExpired && (
                                <div className="text-center mb-4">
                                    <h2 className="h4 mb-2">Đặt lại mật khẩu</h2>
                                    <p className="text-muted">Nhập mật khẩu mới của bạn bên dưới</p>
                                </div>
                            )}

                            {error && (
                                <Alert variant="danger">
                                    {error}
                                </Alert>
                            )}

                            {!tokenExpired && (
                                <Form onSubmit={handleSubmit(onSubmit)}>
                                    <Form.Group className="mb-3">
                                        <Form.Label htmlFor="password">Mật khẩu mới</Form.Label>
                                        <Form.Control
                                            id="password"
                                            type="password"
                                            {...register('password', {
                                                required: 'Mật khẩu là bắt buộc',
                                                minLength: { value: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự' },
                                                pattern: {
                                                    value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                                                    message: 'Mật khẩu phải chứa ít nhất một chữ hoa, một chữ thường và một số'
                                                }
                                            })}
                                            isInvalid={!!errors.password}
                                            placeholder="Nhập mật khẩu mới"
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.password?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>

                                    <Form.Group className="mb-4">
                                        <Form.Label htmlFor="confirmPassword">Xác nhận mật khẩu mới</Form.Label>
                                        <Form.Control
                                            id="confirmPassword"
                                            type="password"
                                            {...register('confirmPassword', {
                                                required: 'Vui lòng xác nhận mật khẩu',
                                                validate: value => value === password || 'Mật khẩu không khớp'
                                            })}
                                            isInvalid={!!errors.confirmPassword}
                                            placeholder="Xác nhận mật khẩu mới"
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.confirmPassword?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>

                                    <Button
                                        type="submit"
                                        variant="primary"
                                        className="w-100"
                                        disabled={loading}
                                    >
                                        {loading && <Spinner as="span" animation="border" size="sm" className="me-2" />}
                                        {loading ? 'Đang đặt lại mật khẩu...' : 'Đặt lại mật khẩu'}
                                    </Button>
                                </Form>
                            )}

                            {tokenExpired && (
                                <div className="mt-4 d-flex justify-content-center">
                                    <Link to="/forget-password" className="btn btn-outline-primary btn-sm">
                                        Yêu cầu liên kết đặt lại mới
                                    </Link>
                                </div>
                            )}

                            <div className="text-center mt-4">
                                <p className="text-muted">
                                    Nhớ mật khẩu của bạn? <Link to="/login" className="text-decoration-none">Đăng nhập tại đây</Link>
                                </p>
                                {!tokenExpired && (
                                    <p className="text-muted">
                                        Cần liên kết đặt lại mới? <Link to="/forget-password" className="text-decoration-none">Yêu cầu tại đây</Link>
                                    </p>
                                )}
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default ResetPasswordForm;
