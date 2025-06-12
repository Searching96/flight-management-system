import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { LoginRequest } from '../../models';


const LoginForm: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<LoginRequest>();

  useEffect(() => {
    // Check if coming from password reset success
    const state = location.state as { resetSuccess?: boolean } | null;
    if (state?.resetSuccess) {
      setSuccess('Mật khẩu của bạn đã được đặt lại thành công. Bây giờ bạn có thể đăng nhập bằng mật khẩu mới.');
    }
  }, [location]);

  const onSubmit = async (data: LoginRequest) => {
    try {
      setLoading(true);
      setError('');
      await login(data);
      navigate('/');
    } catch (err: any) {
      setError(err.message || 'Đăng nhập thất bại. Vui lòng kiểm tra thông tin đăng nhập.');
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
                <h2 className="h4 mb-2">Chào mừng trở lại</h2>
                <p className="text-muted">Đăng nhập vào tài khoản của bạn để tiếp tục</p>
              </div>

              <Form onSubmit={handleSubmit(onSubmit)}>
                {error && <Alert variant="danger">{error}</Alert>}
                {success && <Alert variant="success">{success}</Alert>}

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

                <Form.Group className="mb-4">
                  <Form.Label htmlFor="password">Mật khẩu</Form.Label>
                  <Form.Control
                    id="password"
                    type="password"
                    {...register('password', {
                      required: 'Mật khẩu là bắt buộc',
                      minLength: { value: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự' }
                    })}
                    isInvalid={!!errors.password}
                    placeholder="Nhập mật khẩu của bạn"
                    autoComplete="off"
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.password?.message}
                  </Form.Control.Feedback>
                  <div className="text-end mt-1">
                    <Link to="/forget-password" className="text-decoration-none small">Quên mật khẩu?</Link>
                  </div>
                </Form.Group>

                <Button 
                  type="submit" 
                  variant="primary"
                  className="w-100"
                  disabled={loading}
                >
                  {loading && <Spinner as="span" animation="border" size="sm" className="me-2" />}
                  {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
                </Button>
              </Form>

              <div className="text-center mt-4">
                <p className="text-muted">
                  Chưa có tài khoản? <Link to="/register" className="text-decoration-none">Tạo tài khoản tại đây</Link>
                </p>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default LoginForm;
