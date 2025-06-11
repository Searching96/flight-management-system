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
        state: { message: 'Đăng ký thành công! Vui lòng đăng nhập.' }
      });
    } catch (err: any) {
      setError(err.message || 'Đăng ký thất bại. Vui lòng thử lại.');
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
              <h2 className="mb-1">Tạo tài khoản</h2>
              <p className="mb-0">Tham gia với chúng tôi để bắt đầu đặt vé máy bay</p>
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
                  </Col>
                </Row>

                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label htmlFor="phoneNumber">Số điện thoại</Form.Label>
                      <Form.Control
                        id="phoneNumber"
                        type="tel"
                        {...register('phoneNumber', {
                          required: 'Số điện thoại là bắt buộc',
                          pattern: { value: /^\+?[\d\s-()]+$/, message: 'Vui lòng nhập số điện thoại hợp lệ' }
                        })}
                        isInvalid={!!errors.phoneNumber}
                        placeholder="Nhập số điện thoại của bạn"
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.phoneNumber?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label htmlFor="citizenId">CCCD</Form.Label>
                      <Form.Control
                        id="citizenId"
                        type="text"
                        {...register('citizenId', {
                          required: 'CCCD là bắt buộc',
                          minLength: { value: 9, message: 'CCCD phải có ít nhất 9 ký tự' }
                        })}
                        isInvalid={!!errors.citizenId}
                        placeholder="Nhập số CCCD của bạn"
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
                      <Form.Label htmlFor="password">Mật khẩu</Form.Label>
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
                        placeholder="Tạo mật khẩu"
                      />
                      <Form.Control.Feedback type="invalid">
                        {errors.password?.message}
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label htmlFor="confirmPassword">Xác nhận mật khẩu</Form.Label>
                      <Form.Control
                        id="confirmPassword"
                        type="password"
                        {...register('confirmPassword', {
                          required: 'Vui lòng xác nhận mật khẩu',
                          validate: value =>
                            value === watch('password') || 'Mật khẩu không khớp'
                        })}
                        isInvalid={!!errors.confirmPassword}
                        placeholder="Xác nhận mật khẩu của bạn"
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
                      Đang tạo tài khoản...
                    </>
                  ) : (
                    'Tạo tài khoản'
                  )}
                </Button>
              </Form>
            </Card.Body>
            <Card.Footer className="text-center">
              <p className="mb-0">
                Đã có tài khoản? <Link to="/login" className="text-decoration-none">Đăng nhập tại đây</Link>
              </p>
            </Card.Footer>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default RegisterForm;
