import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Alert, Spinner, Modal } from 'react-bootstrap';
import { Ticket, TicketStatus, TicketFilters, Customer, Flight, TicketClass, TicketRequest } from '../../models';
import { ticketService, customerService, flightService, ticketClassService } from '../../services';
import { usePermissions } from '../../hooks/useAuth';
import { useTickets } from '../../hooks/useTickets';
import TicketForm from './tickets/TicketForm';
import TicketTable from './tickets/TicketTable';

const TicketManagement: React.FC<{
    showAddModal?: boolean;
    onCloseAddModal?: () => void;
}> = ({ showAddModal = false, onCloseAddModal }) => {
    const { canViewAdmin } = usePermissions();
    
    if (!canViewAdmin) {
        return (
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={8}>
                        <Alert variant="danger" className="text-center">
                            <Alert.Heading>Access Denied</Alert.Heading>
                            <p>You do not have permission to access ticket management.</p>
                        </Alert>
                    </Col>
                </Row>
            </Container>
        );
    }

    // State for filters (removed from here since it's now in TicketTable)
    const [filters, setFilters] = useState<TicketFilters>({});

    // Use the custom hook with filters
    const {
        tickets,
        loading,
        error: ticketsError,
        createTicket,
        updateTicket,
        deleteTicket,
        refetch
    } = useTickets(filters);

    // UI state
    const [showForm, setShowForm] = useState(false);
    const [editingTicket, setEditingTicket] = useState<Ticket | null>(null);
    const [error, setError] = useState('');

    // Related data
    const [customers, setCustomers] = useState<Customer[]>([]);
    const [flights, setFlights] = useState<Flight[]>([]);
    const [ticketClasses, setTicketClasses] = useState<TicketClass[]>([]);

    useEffect(() => {
        loadRelatedData();
    }, []);

    // Effect to handle external modal trigger
    useEffect(() => {
        if (showAddModal) {
            setShowForm(true);
        }
    }, [showAddModal]);

    // Combine hook errors
    useEffect(() => {
        setError(ticketsError || '');
    }, [ticketsError]);

    const loadRelatedData = async () => {
        try {
            const [customerData, flightData, ticketClassData] = await Promise.all([
                customerService.getAllCustomers(),
                flightService.getAllFlights(),
                ticketClassService.getAllTicketClasses()
            ]);

            setCustomers(customerData);
            setFlights(flightData);
            setTicketClasses(ticketClassData);
        } catch (err: any) {
            setError('Failed to load related data');
        }
    };

    const handleSubmit = async (data: TicketRequest) => {
        try {
            if (editingTicket) {
                await updateTicket(editingTicket.ticketId!, data);
            } else {
                await createTicket(data);
            }
            handleCancel();
        } catch (err: any) {
            setError(err.message || 'Failed to save ticket');
        }
    };

    const handleEdit = (ticket: Ticket) => {
        setEditingTicket(ticket);
        setShowForm(true);
    };

    const handleDelete = async (ticketId: number) => {
        if (!window.confirm('Are you sure you want to delete this ticket?')) return;
        
        try {
            await deleteTicket(ticketId);
        } catch (err: any) {
            setError(err.message || 'Failed to delete ticket');
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingTicket(null);
        setError('');
        
        if (onCloseAddModal) {
            onCloseAddModal();
        }
    };

    const handleViewDetails = (ticket: Ticket) => {
        // Optional: Implement ticket details view
        console.log('View ticket details:', ticket);
    };

    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
                <p className="mt-3">Loading ticket data...</p>
            </Container>
        );
    }

    return (
        <Container fluid className="py-4">
            {/* Header */}
            <Row className="mb-4">
                <Col>
                    <Card>
                        <Card.Header className="d-flex justify-content-between align-items-center">
                            <div>
                                <Card.Title className="mb-0">🎫 Ticket Management</Card.Title>
                                <small className="text-muted">Manage customer tickets and bookings</small>
                            </div>
                            <Button
                                variant="primary"
                                onClick={() => setShowForm(true)}
                            >
                                <i className="bi bi-plus-circle me-2"></i>
                                Add New Ticket
                            </Button>
                        </Card.Header>
                    </Card>
                </Col>
            </Row>

            {error && (
                <Row className="mb-4">
                    <Col>
                        <Alert variant="danger" onClose={() => setError('')} dismissible>
                            <Alert.Heading>Error</Alert.Heading>
                            {error}
                        </Alert>
                    </Col>
                </Row>
            )}

            {/* Ticket Form Modal */}
            <Modal show={showForm} onHide={handleCancel} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>
                        {editingTicket ? 'Edit Ticket' : 'Add New Ticket'}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <TicketForm
                        ticket={editingTicket || undefined}
                        customers={customers}
                        flights={flights}
                        ticketClasses={ticketClasses}
                        onSubmit={handleSubmit}
                        onCancel={handleCancel}
                    />
                </Modal.Body>
            </Modal>

            {/* Tickets Table */}
            <Row>
                <Col>
                    <TicketTable
                        tickets={tickets}
                        loading={loading}
                        onEdit={handleEdit}
                        onDelete={handleDelete}
                        onViewDetails={handleViewDetails}
                    />
                </Col>
            </Row>
        </Container>
    );
};

export default TicketManagement;
