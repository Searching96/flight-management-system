import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { authService } from '../../services/authService';

interface ForgetPasswordFormData {
    email: string;
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
            await authService.forgetPassword(data.email);
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
                                <h2 className="h4 mb-2">Forgot Password</h2>
                                <p className="text-muted">Enter your email to receive a password reset link</p>
                            </div>

                            {success ? (
                                <Alert variant="success">
                                    <Alert.Heading>Email Sent!</Alert.Heading>
                                    <p>
                                        Password reset email sent successfully. Please check your inbox and click the link to reset your password.
                                    </p>
                                    <hr />
                                    <div className="d-flex justify-content-end">
                                        <Link to="/login" className="btn btn-outline-success">
                                            Back to Login
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
                                                required: 'Email is required',
                                                pattern: { value: /^\S+@\S+\.\S+$/, message: 'Please enter a valid email address' }
                                            })}
                                            isInvalid={!!errors.email}
                                            placeholder="Enter your email"
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.email?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>

                                    <Button
                                        type="submit"
                                        variant="primary"
                                        className="w-100"
                                        disabled={loading}
                                    >
                                        {loading && <Spinner as="span" animation="border" size="sm" className="me-2" />}
                                        {loading ? 'Sending...' : 'Send Reset Link'}
                                    </Button>
                                </Form>
                            )}

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
};

export default ForgetPasswordForm;
