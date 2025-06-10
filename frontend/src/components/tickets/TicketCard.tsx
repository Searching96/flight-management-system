import React, { useState } from 'react';
import { Card, Row, Col, Badge, Button, Spinner } from 'react-bootstrap';
import { Ticket } from '../../models';
import { ticketService } from '../../services';

interface TicketCardProps {
  ticket: Ticket;
  onCancel?: () => void;
}

const TicketCard: React.FC<TicketCardProps> = ({ ticket, onCancel }) => {
  const [cancelling, setCancelling] = useState(false);

  const getStatusVariant = (status?: number) => {
    switch (status) {
      case 1: return 'success'; // paid
      case 2: return 'warning'; // unpaid  
      case 3: return 'danger';  // cancelled
      default: return 'secondary';
    }
  };

  const getStatusText = (status?: number) => {
    switch (status) {
      case 1: return 'Confirmed';
      case 2: return 'Pending Payment';
      case 3: return 'Cancelled';
      default: return 'Unknown';
    }
  };

  const canCancelTicket = () => {
    // Allow cancellation if status is confirmed (paid)
    return ticket.ticketStatus === 1;
  };

  const handleCancelTicket = async () => {
    if (!window.confirm('Are you sure you want to cancel this ticket? This action cannot be undone.')) {
      return;
    }

    try {
      setCancelling(true);
      await ticketService.deleteTicket(ticket.ticketId!);
      alert('Ticket cancelled successfully');
      if (onCancel) onCancel();
    } catch (error: any) {
      alert(error.message || 'Failed to cancel ticket');
    } finally {
      setCancelling(false);
    }
  };

  return (
    <Card className="mb-3 shadow-sm">
      <Card.Header className="bg-primary text-white">
        <Row className="align-items-center">
          <Col>
            <div className="d-flex align-items-center gap-3">
              <span className="fw-bold">Ticket #{ticket.ticketId}</span>
              {ticket.flightId && (
                <Badge bg="light" text="dark" className="fs-6">
                  {ticket.flightId}
                </Badge>
              )}
            </div>
          </Col>
          <Col xs="auto">
            <Badge bg={getStatusVariant(ticket.ticketStatus)}>
              {getStatusText(ticket.ticketStatus)}
            </Badge>
          </Col>
        </Row>
      </Card.Header>

      <Card.Body>
        <Row className="g-3">
          <Col md={4}>
            <div className="text-center">
              <small className="text-muted d-block">Hành khách</small>
              <strong>{ticket.passengerId || 'Chưa có'}</strong>
            </div>
          </Col>
          
          <Col md={4}>
            <div className="text-center">
              <small className="text-muted d-block">Ghế ngồi</small>
              <strong>{ticket.seatNumber || 'Chưa phân'}</strong>
            </div>
          </Col>
          
          <Col md={4}>
            <div className="text-center">
              <small className="text-muted d-block">Hạng vé</small>
              <strong>{ticket.ticketClassId || 'Chưa có'}</strong>
            </div>
          </Col>

          {ticket.fare && (
            <Col xs={12}>
              <div className="text-center border-top pt-3">
                <small className="text-muted d-block">Fare</small>
                <h5 className="text-primary mb-0">${ticket.fare}</h5>
              </div>
            </Col>
          )}

          {ticket.paymentTime && (
            <Col xs={12}>
              <div className="text-center">
                <small className="text-muted">
                  Payment confirmed on {new Date(ticket.paymentTime).toLocaleDateString()}
                </small>
              </div>
            </Col>
          )}
        </Row>

        {canCancelTicket() && (
          <div className="mt-3 d-flex justify-content-center">
            <Button 
              variant="outline-danger"
              onClick={handleCancelTicket}
              disabled={cancelling}
              size="sm"
            >
              {cancelling ? (
                <>
                  <Spinner animation="border" size="sm" className="me-2" />
                  Cancelling...
                </>
              ) : (
                'Cancel Ticket'
              )}
            </Button>
          </div>
        )}
      </Card.Body>
    </Card>
  );
};

export default TicketCard;
