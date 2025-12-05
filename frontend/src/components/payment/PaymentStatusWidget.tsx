import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Badge, 
  Button, 
  Spinner, 
  Alert, 
  Row, 
  Col,
  ProgressBar 
} from 'react-bootstrap';
import { paymentService } from '../../services';

interface PaymentStatusWidgetProps {
  confirmationCode: string;
  onPaymentRequired?: () => void;
  onPaymentComplete?: () => void;
  compact?: boolean;
}

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

const PaymentStatusWidget: React.FC<PaymentStatusWidgetProps> = ({
  confirmationCode,
  onPaymentRequired,
  onPaymentComplete,
  compact = false
}) => {
  const [status, setStatus] = useState<PaymentStatus | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadPaymentStatus();
    
    // Auto-refresh every 30 seconds
    const interval = setInterval(loadPaymentStatus, 30000);
    return () => clearInterval(interval);
  }, [confirmationCode]);

  const loadPaymentStatus = async () => {
    try {
      const result = await paymentService.getPaymentStatus(confirmationCode);
      
      // Map response to our interface
      const mappedStatus: PaymentStatus = {
        success: result.success,
        confirmationCode: result.confirmationCode,
        totalTickets: result.totalTickets,
        paidTickets: result.paidTickets,
        unpaidTickets: result.unpaidTickets,
        bookingPaid: result.bookingPaid || false,
        partiallyPaid: result.partiallyPaid || false,
        totalAmount: result.totalAmount,
        paidAmount: result.paidAmount,
        unpaidAmount: result.unpaidAmount,
        paymentRequired: result.paymentRequired || false,
        message: result.message
      };
      
      setStatus(mappedStatus);
      
      if (!mappedStatus.success) {
        setError(mappedStatus.message || 'Failed to load payment status');
      } else {
        setError('');
        
        // Trigger callbacks
        if (mappedStatus.bookingPaid && onPaymentComplete) {
          onPaymentComplete();
        } else if (mappedStatus.paymentRequired && onPaymentRequired) {
          onPaymentRequired();
        }
      }
    } catch (err: any) {
      setError('Error loading payment status: ' + (err.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  const handlePayNow = async () => {
    if (!status) return;
    
    try {
      const result = await paymentService.createPayment(confirmationCode);
      
      if (result.code === '00' && result.data) {
        // Redirect to MoMo payment
        window.open(result.data, '_self');
      } else {
        setError('Failed to create payment: ' + result.message);
      }
    } catch (err: any) {
      setError('Error creating payment: ' + (err.message || 'Unknown error'));
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount);
  };

  if (loading) {
    return (
      <Card className={compact ? 'border-0 shadow-none' : ''}>
        <Card.Body className="text-center py-4">
          <Spinner animation="border" size="sm" className="me-2" />
          Loading payment status...
        </Card.Body>
      </Card>
    );
  }

  if (error) {
    return (
      <Alert variant="warning" className="mb-0">
        <i className="bi bi-exclamation-triangle me-2"></i>
        {error}
        <Button 
          variant="link" 
          size="sm" 
          className="ms-2 p-0"
          onClick={loadPaymentStatus}
        >
          Try again
        </Button>
      </Alert>
    );
  }

  if (!status || !status.success) {
    return (
      <Alert variant="info" className="mb-0">
        <i className="bi bi-info-circle me-2"></i>
        No payment information available for this booking.
      </Alert>
    );
  }

  return (
    <Card className={compact ? 'border-0 shadow-none' : ''}>
      {!compact && (
        <Card.Header className="bg-light d-flex justify-content-between align-items-center">
          <h6 className="mb-0">Payment Status</h6>
          <Button 
            variant="link" 
            size="sm"
            onClick={loadPaymentStatus}
            className="p-0"
          >
            <i className="bi bi-arrow-clockwise"></i>
          </Button>
        </Card.Header>
      )}
      
      <Card.Body className={compact ? 'p-3' : ''}>
        {/* Payment Status Badge */}
        <div className="d-flex justify-content-between align-items-center mb-3">
          <div>
            <Badge 
              bg={status.bookingPaid ? 'success' : status.partiallyPaid ? 'warning' : 'secondary'}
              className="me-2"
            >
              {status.bookingPaid ? (
                <>
                  <i className="bi bi-check-circle me-1"></i>
                  Booking Paid
                </>
              ) : status.partiallyPaid ? (
                <>
                  <i className="bi bi-exclamation-triangle me-1"></i>
                  Partial Payment
                </>
              ) : (
                <>
                  <i className="bi bi-clock me-1"></i>
                  Payment Pending
                </>
              )}
            </Badge>
            {!compact && (
              <small className="text-muted">
                {status.bookingPaid ? 'All tickets paid' : `${status.totalTickets} tickets pending payment`}
              </small>
            )}
          </div>
          
          {!compact && (
            <div className="text-end">
              <div className="fw-bold">{formatCurrency(status.paidAmount)}</div>
              <small className="text-muted">
                of {formatCurrency(status.totalAmount)}
              </small>
            </div>
          )}
        </div>

        {/* Progress Bar */}
        {!compact && (
          <ProgressBar
            now={status.bookingPaid ? 100 : status.partiallyPaid ? 50 : 0}
            className="mb-3"
            style={{ height: '8px' }}
            variant={status.bookingPaid ? 'success' : status.partiallyPaid ? 'warning' : 'secondary'}
          />
        )}

        {/* Amount Details */}
        {compact ? (
          <Row className="g-2 small">
            <Col xs={6}>
              <div className="text-muted">Total:</div>
              <div className="fw-bold">{formatCurrency(status.totalAmount)}</div>
            </Col>
            <Col xs={6}>
              <div className="text-muted">Remaining:</div>
              <div className={status.unpaidAmount > 0 ? 'text-warning fw-bold' : 'text-success'}>
                {formatCurrency(status.unpaidAmount)}
              </div>
            </Col>
          </Row>
        ) : (
          <Row className="g-3 text-center">
            <Col md={4}>
              <div className="border rounded p-3">
                <div className="text-muted small">Total Amount</div>
                <div className="h5 mb-0">{formatCurrency(status.totalAmount)}</div>
              </div>
            </Col>
            <Col md={4}>
              <div className="border rounded p-3">
                <div className="text-muted small">Paid Amount</div>
                <div className="h5 mb-0 text-success">{formatCurrency(status.paidAmount)}</div>
              </div>
            </Col>
            <Col md={4}>
              <div className="border rounded p-3">
                <div className="text-muted small">Remaining</div>
                <div className={`h5 mb-0 ${status.unpaidAmount > 0 ? 'text-warning' : 'text-success'}`}>
                  {formatCurrency(status.unpaidAmount)}
                </div>
              </div>
            </Col>
          </Row>
        )}

        {/* Action Button */}
        {status.paymentRequired && (
          <div className={compact ? 'mt-3' : 'mt-4'}>
            <Button
              variant="success"
              onClick={handlePayNow}
              className="w-100"
              size={compact ? 'sm' : undefined}
            >
              <i className="bi bi-credit-card me-2"></i>
              Pay Booking ({formatCurrency(status.totalAmount)})
            </Button>
          </div>
        )}

        {status.bookingPaid && (
          <div className={`${compact ? 'mt-3' : 'mt-4'} text-center`}>
            <div className="text-success">
              <i className="bi bi-check-circle-fill me-2"></i>
              Booking payment completed successfully!
            </div>
          </div>
        )}
      </Card.Body>
    </Card>
  );
};

export default PaymentStatusWidget;