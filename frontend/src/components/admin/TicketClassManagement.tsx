import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert, Spinner, Badge, Modal } from 'react-bootstrap';
import { ticketClassService } from '../../services';
import { TicketClass } from '../../models';
import { usePermissions } from '../../hooks/useAuth';

interface TicketClassFormData {
  ticketClassName: string;
  color: string;
}

const TicketClassManagement: React.FC = () => {
  const { canViewAdmin } = usePermissions();
  if (!canViewAdmin) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>Access Denied</Alert.Heading>
              <p>You do not have permission to access ticket class management.</p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingClass, setEditingClass] = useState<TicketClass | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm<TicketClassFormData>();

  useEffect(() => {
    loadTicketClasses();
  }, []);

  const loadTicketClasses = async () => {
    try {
      setLoading(true);
      const data = await ticketClassService.getAllTicketClasses();
      setTicketClasses(data);
    } catch (err: any) {
      setError('Failed to load ticket classes');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: TicketClassFormData) => {
    try {
      if (editingClass) {
        await ticketClassService.updateTicketClass(editingClass.ticketClassId!, data);
      } else {
        await ticketClassService.createTicketClass(data);
      }

      loadTicketClasses();
      handleCancel();
    } catch (err: any) {
      setError(err.message || 'Failed to save ticket class');
    }
  };

  const handleEdit = (ticketClass: TicketClass) => {
    setEditingClass(ticketClass);
    reset({
      ticketClassName: ticketClass.ticketClassName,
      color: ticketClass.color
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this ticket class?')) return;

    try {
      await ticketClassService.deleteTicketClass(id);
      loadTicketClasses();
    } catch (err: any) {
      setError(err.message || 'Failed to delete ticket class');
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingClass(null);
    reset();
    setError('');
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-3">Loading ticket classes...</p>
      </Container>
    );
  }

  return (
    <Container fluid className="py-4">
      {/* Header */}
      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Body>
              <Row className="align-items-center">
                <Col>
                  <h2 className="mb-0">Ticket Class Management</h2>
                </Col>
                <Col xs="auto">
                  <Button
                    variant="primary"
                    onClick={() => setShowForm(true)}
                  >
                    Add New Ticket Class
                  </Button>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Error Alert */}
      {error && (
        <Row className="mb-4">
          <Col>
            <Alert variant="danger" onClose={() => setError('')} dismissible>
              {error}
            </Alert>
          </Col>
        </Row>
      )}

      {/* Form Modal */}
      <Modal show={showForm} onHide={handleCancel} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingClass ? 'Edit Ticket Class' : 'Add New Ticket Class'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit(onSubmit)}>
            <Row>
              <Col md={8}>
                <Form.Group className="mb-3">
                  <Form.Label>Class Name</Form.Label>
                  <Form.Control
                    type="text"
                    {...register('ticketClassName', {
                      required: 'Class name is required'
                    })}
                    isInvalid={!!errors.ticketClassName}
                    placeholder="e.g., Economy, Business, First Class"
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.ticketClassName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Color</Form.Label>
                  <Form.Control
                    type="color"
                    {...register('color', {
                      required: 'Color is required'
                    })}
                    isInvalid={!!errors.color}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.color?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </Col>
            </Row>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCancel}>
            Cancel
          </Button>
          <Button variant="primary" onClick={handleSubmit(onSubmit)}>
            {editingClass ? 'Update Class' : 'Create Class'}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Ticket Classes Grid */}
      {ticketClasses.length > 0 ? (
        <Row>
          {ticketClasses.map(ticketClass => (
            <Col lg={4} md={6} key={ticketClass.ticketClassId} className="mb-4">
              <Card className="h-100">
                <Card.Header className="d-flex justify-content-between align-items-center">
                  <Badge 
                    style={{ backgroundColor: ticketClass.color, color: '#fff' }}
                    className="px-3 py-2 fs-6"
                  >
                    {ticketClass.ticketClassName}
                  </Badge>
                  <div>
                    <Button
                      variant="outline-secondary"
                      size="sm"
                      className="me-2"
                      onClick={() => handleEdit(ticketClass)}
                    >
                      Edit
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() => handleDelete(ticketClass.ticketClassId!)}
                    >
                      Delete
                    </Button>
                  </div>
                </Card.Header>
                <Card.Body>
                  <Row className="mb-2">
                    <Col xs={4} className="text-muted">ID:</Col>
                    <Col xs={8}>{ticketClass.ticketClassId}</Col>
                  </Row>
                  <Row className="mb-2">
                    <Col xs={4} className="text-muted">Name:</Col>
                    <Col xs={8}>{ticketClass.ticketClassName}</Col>
                  </Row>
                  <Row className="mb-2">
                    <Col xs={4} className="text-muted">Color:</Col>
                    <Col xs={8} className="d-flex align-items-center">
                      <div
                        className="me-2 rounded"
                        style={{
                          backgroundColor: ticketClass.color,
                          width: '20px',
                          height: '20px',
                          border: '1px solid #dee2e6'
                        }}
                      ></div>
                      <span>{ticketClass.color}</span>
                    </Col>
                  </Row>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      ) : (
        <Row>
          <Col>
            <Card>
              <Card.Body className="text-center py-5">
                <div className="text-muted">
                  <h4>No ticket classes found</h4>
                  <p>Add your first ticket class to get started.</p>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default TicketClassManagement;
