import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';
import { debugService } from '../../services/debugService';
import { Account } from '../../models/Account';

const DebugLogin: React.FC = () => {
  const { accountName } = useParams<{ accountName: string }>();
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [loginData, setLoginData] = useState<Account | null>(null);

  useEffect(() => {
    if (accountName) {
      performDebugLogin();
    } else {
      setError('No account name provided');
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
        accountId: response.accountId,
        accountName: response.accountName,
        email: response.email,
        accountType: response.accountType
      };
      
      setLoginData(accountData);
      
      // Store auth data
      if (response.token) {
        localStorage.setItem('authToken', response.token);
      }
      localStorage.setItem('userAccount', JSON.stringify(accountData));

      console.log('Debug login successful, redirecting...');
      console.log('Account type:', response.accountType);

      // Redirect based on account type
      setTimeout(() => {
        if (response.accountType === 1) {
          // Customer - redirect to dashboard
          console.log('Redirecting customer to dashboard');
          navigate('/dashboard');
        } else if (response.accountType === 2) {
          // Employee - redirect to admin panel
          console.log('Redirecting employee to admin panel');
          navigate('/admin');
        } else {
          // Unknown account type - redirect to home
          console.log('Unknown account type, redirecting to home');
          navigate('/');
        }
      }, 2000); // 2 second delay to show success message

    } catch (err: any) {
      console.error('Debug login failed:', err);
      setError(err.message || 'Debug login failed');
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
                <h5>Debug Login in Progress...</h5>
                <p className="text-muted">
                  Attempting to login as: <strong>{accountName}</strong>
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
              <Alert.Heading>Debug Login Failed</Alert.Heading>
              <p>Account name: <strong>{accountName}</strong></p>
              <p>{error}</p>
              <hr />
              <p className="mb-0">
                Make sure an account with name "{accountName}" exists in the database.
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
              Debug Login Successful!
            </Alert.Heading>
            {loginData && (
              <>
                <p><strong>Account:</strong> {loginData.accountName}</p>
                <p><strong>Email:</strong> {loginData.email}</p>
                <p><strong>Type:</strong> {loginData.accountType === 1 ? 'Customer' : 'Employee'}</p>
                <hr />
                <p className="mb-0">
                  Redirecting to {loginData.accountType === 1 ? 'Dashboard' : 'Admin Panel'}...
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
