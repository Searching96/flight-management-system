import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Row, Col, Card, Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';
import { debugService } from '../../services/debugService';
import { Account } from '../../models/Account';

const DebugLogin: React.FC = () => {
  const { accountName } = useParams<{ accountName: string }>();
  const { user, logout } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [loginData, setLoginData] = useState<Account | null>(null);

  useEffect(() => {
    if (accountName) {
      performDebugLogin();
    } else {
      setError('Không có tên tài khoản được cung cấp');
      setLoading(false);
    }
  }, [accountName]);

  const performDebugLogin = async () => {
    console.log('=== DebugLogin Component START ===');
    console.log('Account name from URL:', accountName);

    try {
      setLoading(true);
      setError('');

      // Logout current user if any
      if (user) {
        console.log('Logging out current user:', user.accountName);
        logout();
      }

      // Perform debug login
      console.log('Attempting debug login for:', accountName);
      const response = await debugService.loginByName(accountName!);

      // Map response to Account interface
      const accountData: Account = {
        accountId: response.userDetails.id,
        accountName: response.userDetails.accountName,
        email: response.userDetails.email,
        accountType: response.userDetails.accountTypeName === 'Customer' ? 1 : 2, // Assuming 1 for Customer, 2 for Employee
      };

      setLoginData(accountData);

      console.log('Debug login successful, redirecting...');
      console.log('Account type:', response.userDetails.accountTypeName);

      // Redirect based on account type
      setTimeout(() => {
        if (response.userDetails.accountTypeName === "Customer") {
          // Customer - redirect to dashboard
          console.log('Redirecting customer to dashboard');
          window.location.href = '/dashboard';
        } else if (response.userDetails.role === "EMPLOYEE_ADMINISTRATOR") {
          // Employee - redirect to admin panel
          console.log('Redirecting employee to admin panel');
          window.location.href = '/admin';
        } else {
          // Unknown account type - redirect to home
          console.log('Unknown account type, redirecting to home');
          window.location.href = '/';
        }
      }, 2000); // 2 second delay to show success message

    } catch (err: any) {
      console.error('Debug login failed:', err);
      setError(err.message || 'Đăng nhập debug thất bại');
    } finally {
      setLoading(false);
      console.log('=== DebugLogin Component END ===');
    }
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={6}>
            <Card className="text-center">
              <Card.Body className="py-5">
                <Spinner animation="border" variant="primary" className="mb-3" />
                <h5>Đang thực hiện đăng nhập debug...</h5>
                <p className="text-muted">
                  Đang cố gắng đăng nhập với tài khoản: <strong>{accountName}</strong>
                </p>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={6}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>Đăng nhập debug thất bại</Alert.Heading>
              <p>Tên tài khoản: <strong>{accountName}</strong></p>
              <p>{error}</p>
              <hr />
              <p className="mb-0">
                Đảm bảo tài khoản với tên "{accountName}" tồn tại trong cơ sở dữ liệu.
              </p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col md={6}>
          <Alert variant="success" className="text-center">
            <Alert.Heading>
              <i className="bi bi-check-circle me-2"></i>
              Đăng nhập debug thành công!
            </Alert.Heading>
            {loginData && (
              <>
                <p><strong>Tài khoản:</strong> {loginData.accountName}</p>
                <p><strong>Email:</strong> {loginData.email}</p>
                <p><strong>Loại:</strong> {loginData.accountType === 1 ? 'Khách hàng' : 'Nhân viên'}</p>
                <hr />
                <p className="mb-0">
                  Đang chuyển hướng đến {loginData.accountType === 1 ? 'Bảng điều khiển' : 'Trang quản trị'}...
                </p>
              </>
            )}
          </Alert>
        </Col>
      </Row>
    </Container>
  );
};

export default DebugLogin;
