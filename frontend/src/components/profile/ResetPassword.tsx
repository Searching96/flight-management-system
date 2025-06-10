import React, { useState } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import { accountService } from '../../services';

const ResetPassword: React.FC = () => {
   const { user } = useAuth();
   const navigate = useNavigate();
   const [loading, setLoading] = useState(false);
   const [error, setError] = useState('');
   const [success, setSuccess] = useState('');

   const [formData, setFormData] = useState({
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
   });

   const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      setFormData(prev => ({
         ...prev,
         [name]: value
      }));
   };

   const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      setLoading(true);
      setError('');
      setSuccess('');

      // Validation - compare two new passwords
      if (formData.newPassword !== formData.confirmPassword) {
         setError('New passwords do not match');
         setLoading(false);
         return;
      }

      if (formData.newPassword.length < 6) {
         setError('New password must be at least 6 characters long');
         setLoading(false);
         return;
      }

      try {
         // Check old password first
         try {
            const isCurrentPasswordValid = await accountService.verifyCurrentPassword(user!.id, formData.currentPassword);
            if (!isCurrentPasswordValid) {
               setError('Current password is incorrect');
               setLoading(false);
               return;
            }
         } catch (verifyErr: any) {
            setError('Current password is incorrect');
            setLoading(false);
            return;
         }

         // Reset password
         const resetData = {
            accountId: user!.id,
            currentPassword: formData.currentPassword,
            newPassword: formData.newPassword,
         };

         console.log('Resetting password for user:', user?.id);

         await accountService.resetPassword(resetData);

         setSuccess('Password reset successfully!');
         setFormData({
            currentPassword: '',
            newPassword: '',
            confirmPassword: '',
         });
      } catch (err: any) {
         setError(err.message || 'Failed to reset password');
      } finally {
         setLoading(false);
      }
   };

   const handleCancel = () => {
      navigate(-1);
   };

   return (
      <Container className="mt-4">
         <Row className="justify-content-center">
            <Col md={6} lg={5}>
               <Card className="shadow">
                  <Card.Header className="bg-warning text-dark">
                     <h4 className="mb-0">
                        <i className="bi bi-shield-lock me-2"></i>
                        Reset Password
                     </h4>
                  </Card.Header>
                  <Card.Body className="p-4">
                     {error && (
                        <Alert variant="danger" dismissible onClose={() => setError('')}>
                           {error}
                        </Alert>
                     )}
                     {success && (
                        <Alert variant="success" dismissible onClose={() => setSuccess('')}>
                           {success}
                        </Alert>
                     )}

                     <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                           <Form.Label>Current Password</Form.Label>
                           <Form.Control
                              type="password"
                              name="currentPassword"
                              value={formData.currentPassword}
                              onChange={handleInputChange}
                              required
                              placeholder="Enter your current password"
                              autoComplete="off"
                           />
                        </Form.Group>

                        <Form.Group className="mb-3">
                           <Form.Label>New Password</Form.Label>
                           <Form.Control
                              type="password"
                              name="newPassword"
                              value={formData.newPassword}
                              onChange={handleInputChange}
                              required
                              placeholder="Enter your new password"
                              minLength={6}
                              autoComplete="new-password"
                           />
                           <Form.Text className="text-muted">
                              Password must be at least 6 characters long
                           </Form.Text>
                        </Form.Group>

                        <Form.Group className="mb-4">
                           <Form.Label>Confirm New Password</Form.Label>
                           <Form.Control
                              type="password"
                              name="confirmPassword"
                              value={formData.confirmPassword}
                              onChange={handleInputChange}
                              required
                              placeholder="Confirm your new password"
                              minLength={6}
                              autoComplete="new-password"
                           />
                        </Form.Group>

                        <div className="d-flex gap-2 justify-content-end">
                           <Button
                              variant="secondary"
                              onClick={handleCancel}
                              disabled={loading}
                           >
                              Cancel
                           </Button>
                           <Button
                              type="submit"
                              variant="warning"
                              disabled={loading}
                           >
                              {loading ? (
                                 <>
                                    <span className="spinner-border spinner-border-sm me-2" />
                                    Resetting...
                                 </>
                              ) : (
                                 <>
                                    <i className="bi bi-shield-check me-2"></i>
                                    Reset Password
                                 </>
                              )}
                           </Button>
                        </div>
                     </Form>
                  </Card.Body>
               </Card>
            </Col>
         </Row>
      </Container>
   );
};

export default ResetPassword;
