import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';
import { RegisterRequest } from '../../models';

type RegisterRequestWithConfirm = RegisterRequest & { confirmPassword: string };

const RegisterForm: React.FC = () => {
  const { register: registerUser } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors }
  } = useForm<RegisterRequestWithConfirm>();

  const onSubmit = async (data: RegisterRequestWithConfirm) => {
    try {
      setLoading(true);
      setError('');
      // Set account type to customer (type 1) - matches backend schema
      data.accountType = 1;

      const { confirmPassword, ...submitData } = data; // Remove confirmPassword from data

      await registerUser(submitData);

      navigate('/', {
        state: { message: 'Registration successful! Please sign in.' }
      });
    } catch (err: any) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };
  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col xs={12} md={8} lg={6}>
          <Card>
            <Card.Header className="text-center bg-primary text-white">
              <h2 className="mb-1">Create Account</h2>
              <p className="mb-0">Join us to start booking flights</p>
            </Card.Header>
            <Card.Body className="p-4">
              <Form onSubmit={handleSubmit(onSubmit)}>
                {error && (
                  <Alert variant="danger" className="mb-3">
                    {error}
                  </Alert>
                )}

                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label htmlFor="accountName">Họ và tên</Form.Label>
                      <Form.Control
                        id="accountName"
                        type="text"
                        {...register('accountName', {
                          required: 'Họ và tên là bắt buộc',
                          minLength: { value: 3, message: 'Họ và tên phải ít nhất 3 kí tự' },
                          pattern: {
                            value: /^[\p{L}\s'.-]+$/u, message: 'Họ và tên chỉ có thể chứa kí tự'
                          }
                        })}
                        isInvalid={!!errors.accountName}
                        placeholder="Nhập họ và tên"
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.accountName?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  <Col md={6}>
                    <Form.Group className="mb-3">
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
                  </Col>
                </Row>

                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label htmlFor="phoneNumber">Phone Number</Form.Label>
                      <Form.Control
                        id="phoneNumber"
                        type="tel"
                        {...register('phoneNumber', {
                          required: 'Phone number is required',
                          pattern: { value: /^\+?[\d\s-()]+$/, message: 'Please enter a valid phone number' }
                        })}
                        isInvalid={!!errors.phoneNumber}
                        placeholder="Enter your phone number"
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.phoneNumber?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label htmlFor="citizenId">Citizen ID</Form.Label>
                      <Form.Control
                        id="citizenId"
                        type="text"
                        {...register('citizenId', {
                          required: 'Citizen ID is required',
                          minLength: { value: 9, message: 'Citizen ID must be at least 9 characters' }
                        })}
                        isInvalid={!!errors.citizenId}
                        placeholder="Enter your citizen ID"
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.citizenId?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>
                </Row>

                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label htmlFor="password">Password</Form.Label>
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
                        placeholder="Create a password"
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.password?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label htmlFor="confirmPassword">Confirm Password</Form.Label>
                      <Form.Control
                        id="confirmPassword"
                        type="password"
                        {...register('confirmPassword', {
                          required: 'Please confirm your password',
                          validate: value =>
                            value === watch('password') || 'Passwords do not match'
                        })}
                        isInvalid={!!errors.confirmPassword}
                        placeholder="Confirm your password"
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.confirmPassword?.message}
                      </Form.Control.Feedback>

                    </Form.Group>
                  </Col>
                </Row>

                <Button
                  type="submit"
                  variant="primary"
                  size="lg"
                  className="w-100"
                  disabled={loading}
                >
                  {loading ? (
                    <>
                      <Spinner
                        as="span"
                        animation="border"
                        size="sm"
                        role="status"
                        aria-hidden="true"
                        className="me-2"
                      />
                      Creating Account...
                    </>
                  ) : (
                    'Create Account'
                  )}
                </Button>
              </Form>
            </Card.Body>
            <Card.Footer className="text-center">
              <p className="mb-0">
                Already have an account? <Link to="/login" className="text-decoration-none">Sign in here</Link>
              </p>
            </Card.Footer>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default RegisterForm;
