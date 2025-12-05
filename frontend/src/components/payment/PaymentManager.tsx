import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Row, 
  Col, 
  Card, 
  Button, 
  Alert, 
  Badge, 
  Spinner,
  Modal,
  Form,
  Table
} from 'react-bootstrap';
import { paymentService } from '../../services';

interface PaymentStatus {
  success: boolean;
  confirmationCode: string;
  totalTickets: number;
  paidTickets: number;
  unpaidTickets: number;
  bookingPaid: boolean;
  partiallyPaid: boolean;
  totalAmount: number;
  paidAmount: number;
  unpaidAmount: number;
  paymentRequired: boolean;
  message?: string;
}

interface PaymentManagerProps {
  confirmationCode?: string;
}

const PaymentManager: React.FC<PaymentManagerProps> = ({ confirmationCode: initialCode }) => {
  const [confirmationCode, setConfirmationCode] = useState(initialCode || '');
  const [paymentStatus, setPaymentStatus] = useState<PaymentStatus | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showRefundModal, setShowRefundModal] = useState(false);
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [refundAmount, setRefundAmount] = useState('');
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    if (confirmationCode) {
      handleCheckStatus();
    }
  }, [confirmationCode]);

  const handleCheckStatus = async () => {
    if (!confirmationCode.trim()) {
      setError('Please enter a confirmation code');
      return;
    }

    setLoading(true);
    setError('');
    
    try {
      const status = await paymentService.getPaymentStatus(confirmationCode);
      setPaymentStatus(status);
      
      if (!status.success) {
        setError(status.message || 'Failed to get payment status');
      }
    } catch (err: any) {
      setError('Error checking payment status: ' + (err.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePayment = async () => {
    if (!paymentStatus) return;
    
    setProcessing(true);
    try {
      const result = await paymentService.createPayment(confirmationCode);
      
      if (result.code === '00' && result.data) {
        // Redirect to MoMo payment page
        window.open(result.data, '_self');
      } else {
        setError('Failed to create payment: ' + result.message);
      }
    } catch (err: any) {
      setError('Error creating payment: ' + (err.message || 'Unknown error'));
    } finally {
      setProcessing(false);
    }
  };

  const handleCancelPayment = async () => {
    if (!paymentStatus) return;
    
    setProcessing(true);
    try {
      const result = await paymentService.cancelPayment(confirmationCode);
      
      if (result.success) {
        setShowCancelModal(false);
        await handleCheckStatus(); // Refresh status
        alert(`Payment cancelled successfully. ${result.cancelledTickets} tickets were cancelled.`);
      } else {
        if (result.requiresRefund) {
          setError('Cannot cancel - payment already completed. Use refund instead.');
        } else {
          setError('Failed to cancel payment: ' + result.message);
        }
      }
    } catch (err: any) {
      setError('Error cancelling payment: ' + (err.message || 'Unknown error'));
    } finally {
      setProcessing(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount);
  };

  return (
    <Container className="py-4">
      <Row className="justify-content-center">
        <Col lg={8}>
          <Card>
            <Card.Header>
              <h4 className="mb-0">
                <i className="bi bi-credit-card me-2"></i>
                Payment Management
              </h4>
            </Card.Header>
            <Card.Body>
              {/* Confirmation Code Input */}
              <Form className="mb-4">
                <Row className="g-3">
                  <Col md={8}>
                    <Form.Group>
                      <Form.Label>Confirmation Code</Form.Label>
                      <Form.Control
                        type="text"
                        value={confirmationCode}
                        onChange={(e) => setConfirmationCode(e.target.value)}
                        placeholder="Enter booking confirmation code"
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4} className="d-flex align-items-end">
                    <Button 
                      variant="primary" 
                      onClick={handleCheckStatus}
                      disabled={loading || !confirmationCode.trim()}
                      className="w-100"
                    >
                      {loading ? (
                        <>
                          <Spinner size="sm" className="me-2" />
                          Checking...
                        </>
                      ) : (
                        <>
                          <i className="bi bi-search me-2"></i>
                          Check Status
                        </>
                      )}
                    </Button>
                  </Col>
                </Row>
              </Form>

              {error && (
                <Alert variant="danger" className="mb-4">
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  {error}
                </Alert>
              )}

              {/* Payment Status Display */}
              {paymentStatus && paymentStatus.success && (
                <Card className="mb-4">
                  <Card.Header className="bg-light">
                    <h5 className="mb-0">Payment Status</h5>
                  </Card.Header>
                  <Card.Body>
                    <Row className="g-3">
                      <Col md={6}>
                        <h6>Booking Details</h6>
                        <p className="mb-1">
                          <strong>Confirmation Code:</strong> {paymentStatus.confirmationCode}
                        </p>
                        <p className="mb-1">
                          <strong>Total Tickets:</strong> {paymentStatus.totalTickets}
                        </p>
                        <p className="mb-1">
                          <strong>Payment Status:</strong>{' '}
                          <Badge bg={paymentStatus.bookingPaid ? 'success' : paymentStatus.partiallyPaid ? 'warning' : 'secondary'}>
                            {paymentStatus.bookingPaid ? 'Booking Paid' : paymentStatus.partiallyPaid ? 'Partial Payment' : 'Payment Pending'}
                          </Badge>
                        </p>
                      </Col>
                      <Col md={6}>
                        <h6>Financial Summary</h6>
                        <Table size="sm" className="mb-0">
                          <tbody>
                            <tr>
                              <td>Total Amount:</td>
                              <td className="text-end fw-bold">
                                {formatCurrency(paymentStatus.totalAmount)}
                              </td>
                            </tr>
                            <tr className="text-success">
                              <td>Paid Amount:</td>
                              <td className="text-end">
                                {formatCurrency(paymentStatus.paidAmount)}
                              </td>
                            </tr>
                            <tr className="text-warning">
                              <td>Unpaid Amount:</td>
                              <td className="text-end">
                                {formatCurrency(paymentStatus.unpaidAmount)}
                              </td>
                            </tr>
                          </tbody>
                        </Table>
                      </Col>
                    </Row>

                    <hr />

                    {/* Action Buttons */}
                    <div className="d-flex gap-2 flex-wrap">
                      {paymentStatus.paymentRequired && (
                        <Button
                          variant="success"
                          onClick={handleCreatePayment}
                          disabled={processing}
                        >
                          {processing ? (
                            <>
                              <Spinner size="sm" className="me-2" />
                              Processing...
                            </>
                          ) : (
                            <>
                              <i className="bi bi-credit-card me-2"></i>
                              Pay Booking ({formatCurrency(paymentStatus.totalAmount)})
                            </>
                          )}
                        </Button>
                      )}

                      {!paymentStatus.bookingPaid && (
                        <Button
                          variant="outline-danger"
                          onClick={() => setShowCancelModal(true)}
                          disabled={processing}
                        >
                          <i className="bi bi-x-circle me-2"></i>
                          Cancel Booking
                        </Button>
                      )}

                      {paymentStatus.bookingPaid && (
                        <Button
                          variant="outline-warning"
                          onClick={() => setShowRefundModal(true)}
                          disabled={processing}
                        >
                          <i className="bi bi-arrow-counterclockwise me-2"></i>
                          Request Refund
                        </Button>
                      )}

                      <Button
                        variant="outline-secondary"
                        onClick={handleCheckStatus}
                        disabled={loading}
                      >
                        <i className="bi bi-arrow-clockwise me-2"></i>
                        Refresh Status
                      </Button>
                    </div>
                  </Card.Body>
                </Card>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Cancel Confirmation Modal */}
      <Modal show={showCancelModal} onHide={() => setShowCancelModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Cancel Payment</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Are you sure you want to cancel this booking?</p>
          <p className="text-muted">
            This action will cancel all unpaid tickets for confirmation code: <strong>{confirmationCode}</strong>
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCancelModal(false)}>
            Keep Booking
          </Button>
          <Button variant="danger" onClick={handleCancelPayment} disabled={processing}>
            {processing ? (
              <>
                <Spinner size="sm" className="me-2" />
                Cancelling...
              </>
            ) : (
              'Cancel Booking'
            )}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Refund Modal */}
      <Modal show={showRefundModal} onHide={() => setShowRefundModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Request Refund</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Request a refund for paid tickets.</p>
          <Form>
            <Form.Group>
              <Form.Label>Refund Amount</Form.Label>
              <Form.Control
                type="number"
                value={refundAmount}
                onChange={(e) => setRefundAmount(e.target.value)}
                placeholder={`Max: ${paymentStatus?.totalAmount || 0}`}
                max={paymentStatus?.totalAmount || 0}
              />
              <Form.Text className="text-muted">
                Maximum refund amount: {formatCurrency(paymentStatus?.totalAmount || 0)}
              </Form.Text>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowRefundModal(false)}>
            Cancel
          </Button>
          <Button variant="warning" disabled={!refundAmount || processing}>
            {processing ? (
              <>
                <Spinner size="sm" className="me-2" />
                Processing...
              </>
            ) : (
              'Request Refund'
            )}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default PaymentManager;