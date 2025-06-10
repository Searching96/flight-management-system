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
                setError('Invalid reset token. Please request a new password reset.');
                setValidatingToken(false);
                return;
            }

            try {
                setValidatingToken(true);
                
                if (!await authService.validateResetToken(token)) {
                    setTokenExpired(true);
                    setError('Invalid or expired reset token. Please request a new password reset.');
                }
            } catch (error) {
                console.error('Token validation error:', error);
                setTokenExpired(true);
                setError('Unable to validate reset token. Please request a new password reset.');
            } finally {
                setValidatingToken(false);
            }
        };

        validateToken();
    }, [token]);

    const onSubmit = async (data: ResetPasswordFormData) => {
        if (!token) {
            setError('Invalid reset token. Please request a new reset link.');
            return;
        }

        try {
            setLoading(true);
            setError('');
            await authService.resetPassword(token, data.password);
            navigate('/login', { state: { resetSuccess: true } });
        } catch (err: any) {
            const errorMessage = err.message || 'Failed to reset password. The link may have expired.';
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
                                    Invalid reset link. Please request a new password reset.
                                </Alert>
                                <Link to="/forget-password" className="btn btn-primary mt-3">
                                    Request Password Reset
                                </Link>
                                <div className="text-center mt-4">
                                    <p className="text-muted">
                                        Remember your password? <Link to="/login" className="text-decoration-none">Sign in here</Link>
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
                                    <span className="visually-hidden">Validating reset link...</span>
                                </Spinner>
                                <p className="mt-3 text-muted">Validating reset link...</p>
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
                                    <h2 className="h4 mb-2">Reset Password</h2>
                                    <p className="text-muted">Enter your new password below</p>
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
                                        <Form.Label htmlFor="password">New Password</Form.Label>
                                        <Form.Control
                                            id="password"
                                            type="password"
                                            {...register('password', {
                                                required: 'Password is required',
                                                minLength: { value: 6, message: 'Password must be at least 6 characters' },
                                                pattern: {
                                                    value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                                                    message: 'Password must contain at least one uppercase letter, one lowercase letter, and one number'
                                                }
                                            })}
                                            isInvalid={!!errors.password}
                                            placeholder="Enter new password"
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.password?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>

                                    <Form.Group className="mb-4">
                                        <Form.Label htmlFor="confirmPassword">Confirm New Password</Form.Label>
                                        <Form.Control
                                            id="confirmPassword"
                                            type="password"
                                            {...register('confirmPassword', {
                                                required: 'Please confirm your password',
                                                validate: value => value === password || 'Passwords do not match'
                                            })}
                                            isInvalid={!!errors.confirmPassword}
                                            placeholder="Confirm new password"
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
                                        {loading ? 'Resetting Password...' : 'Reset Password'}
                                    </Button>
                                </Form>
                            )}

                            {tokenExpired && (
                                <div className="mt-4 d-flex justify-content-center">
                                    <Link to="/forget-password" className="btn btn-outline-primary btn-sm">
                                        Request New Reset Link
                                    </Link>
                                </div>
                            )}

                            <div className="text-center mt-4">
                                <p className="text-muted">
                                    Remember your password? <Link to="/login" className="text-decoration-none">Sign in here</Link>
                                </p>
                                {!tokenExpired && (
                                    <p className="text-muted">
                                        Need a new reset link? <Link to="/forget-password" className="text-decoration-none">Request here</Link>
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
