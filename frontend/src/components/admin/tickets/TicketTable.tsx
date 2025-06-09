import React, { useState, useMemo } from 'react';
import { Table, Badge, Button, Form, Row, Col, InputGroup, Card } from 'react-bootstrap';
import { Ticket, TicketStatus } from '../../../models';

interface TicketTableProps {
  tickets: Ticket[];
  loading?: boolean;
  onEdit: (ticket: Ticket) => void;
  onDelete: (ticketId: number) => void;
  onViewDetails?: (ticket: Ticket) => void;
}

const TicketTable: React.FC<TicketTableProps> = ({
  tickets,
  loading = false,
  onEdit,
  onDelete,
  onViewDetails
}) => {
  // Search and filter states
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<TicketStatus | ''>('');
  const [sortConfig, setSortConfig] = useState<{
    key: keyof Ticket;
    direction: 'asc' | 'desc';
  } | null>(null);

  // Filter and search tickets
  const filteredTickets = useMemo(() => {
    return tickets.filter(ticket => {
      // Search filter
      const searchLower = searchTerm.toLowerCase();
      const matchesSearch = !searchTerm || (
        ticket.ticketId?.toString().includes(searchLower) ||
        ticket.customer?.accountName?.toLowerCase().includes(searchLower) ||
        ticket.customer?.email?.toLowerCase().includes(searchLower) ||
        ticket.flight?.flightCode?.toLowerCase().includes(searchLower) ||
        ticket.ticketClass?.ticketClassName?.toLowerCase().includes(searchLower)
      );

      // Status filter
      const matchesStatus = !statusFilter || ticket.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [tickets, searchTerm, statusFilter]);

  // Sort tickets
  const sortedTickets = useMemo(() => {
    if (!sortConfig) return filteredTickets;

    return [...filteredTickets].sort((a, b) => {
      const aValue = a[sortConfig.key];
      const bValue = b[sortConfig.key];

      if (aValue === null || aValue === undefined) return 1;
      if (bValue === null || bValue === undefined) return -1;

      if (aValue < bValue) {
        return sortConfig.direction === 'asc' ? -1 : 1;
      }
      if (aValue > bValue) {
        return sortConfig.direction === 'asc' ? 1 : -1;
      }
      return 0;
    });
  }, [filteredTickets, sortConfig]);

  // Handle sorting
  const handleSort = (key: keyof Ticket) => {
    setSortConfig(current => {
      if (!current || current.key !== key) {
        return { key, direction: 'asc' };
      }
      if (current.direction === 'asc') {
        return { key, direction: 'desc' };
      }
      return null; // Remove sorting
    });
  };

  // Get sort icon
  const getSortIcon = (key: keyof Ticket) => {
    if (!sortConfig || sortConfig.key !== key) {
      return <span className="text-muted">↕️</span>;
    }
    return sortConfig.direction === 'asc' ? '↑' : '↓';
  };

  // Get status badge variant
  const getStatusVariant = (status: TicketStatus) => {
    switch (status) {
      case TicketStatus.CONFIRMED:
        return 'success';
      case TicketStatus.CANCELLED:
        return 'danger';
      case TicketStatus.PENDING:
        return 'warning';
      case TicketStatus.CHECKED_IN:
        return 'info';
      case TicketStatus.NO_SHOW:
        return 'secondary';
      default:
        return 'secondary';
    }
  };

  // Statistics
  const stats = useMemo(() => {
    const total = tickets.length;
    const confirmed = tickets.filter(t => t.status === TicketStatus.CONFIRMED).length;
    const pending = tickets.filter(t => t.status === TicketStatus.PENDING).length;
    const cancelled = tickets.filter(t => t.status === TicketStatus.CANCELLED).length;
    const totalRevenue = tickets
      .filter(t => t.status === TicketStatus.CONFIRMED)
      .reduce((sum, t) => sum + t.ticketPrice, 0);

    return { total, confirmed, pending, cancelled, totalRevenue };
  }, [tickets]);

  return (
    <div>
      {/* Search and Filter Controls */}
      <Card className="mb-4">
        <Card.Header>
          <Card.Title className="mb-0">Search & Filter</Card.Title>
        </Card.Header>
        <Card.Body>
          <Row className="align-items-end">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Search Tickets</Form.Label>
                <InputGroup>
                  <InputGroup.Text>
                    <i className="bi bi-search"></i>
                  </InputGroup.Text>
                  <Form.Control
                    type="text"
                    placeholder="Search by ID, customer, flight, or class..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                  {searchTerm && (
                    <Button
                      variant="outline-secondary"
                      onClick={() => setSearchTerm('')}
                    >
                      <i className="bi bi-x"></i>
                    </Button>
                  )}
                </InputGroup>
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group>
                <Form.Label>Filter by Status</Form.Label>
                <Form.Select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value as TicketStatus || '')}
                >
                  <option value="">All Statuses</option>
                  {Object.values(TicketStatus).map(status => (
                    <option key={status} value={status}>
                      {status.replace('_', ' ')}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={5}>
              <Row className="text-center">
                <Col>
                  <Badge bg="primary" className="p-2">
                    Total: <strong>{stats.total}</strong>
                  </Badge>
                </Col>
                <Col>
                  <Badge bg="success" className="p-2">
                    Confirmed: <strong>{stats.confirmed}</strong>
                  </Badge>
                </Col>
                <Col>
                  <Badge bg="warning" className="p-2">
                    Pending: <strong>{stats.pending}</strong>
                  </Badge>
                </Col>
                <Col>
                  <Badge bg="info" className="p-2">
                    Revenue: <strong>{stats.totalRevenue.toLocaleString()} VND</strong>
                  </Badge>
                </Col>
              </Row>
            </Col>
          </Row>
          
          {/* Results summary */}
          <Row className="mt-2">
            <Col>
              <small className="text-muted">
                Showing {sortedTickets.length} of {tickets.length} tickets
                {searchTerm && ` matching "${searchTerm}"`}
                {statusFilter && ` with status "${statusFilter}"`}
              </small>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Tickets Table */}
      <Card>
        <Card.Header>
          <Card.Title className="mb-0">Tickets ({sortedTickets.length})</Card.Title>
        </Card.Header>
        <Card.Body className="p-0">
          {sortedTickets.length === 0 ? (
            <div className="text-center py-5">
              <div className="mb-3">
                <i className="bi bi-ticket-perforated" style={{ fontSize: '3rem', color: '#dee2e6' }}></i>
              </div>
              <h5 className="text-muted">No tickets found</h5>
              <p className="text-muted mb-0">
                {searchTerm || statusFilter ?
                  'Try adjusting your search criteria.' :
                  'No tickets have been created yet.'
                }
              </p>
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead className="table-light">
                  <tr>
                    <th 
                      style={{ cursor: 'pointer' }}
                      onClick={() => handleSort('ticketId')}
                    >
                      Ticket ID {getSortIcon('ticketId')}
                    </th>
                    <th 
                      style={{ cursor: 'pointer' }}
                      onClick={() => handleSort('customerId')}
                    >
                      Customer {getSortIcon('customerId')}
                    </th>
                    <th 
                      style={{ cursor: 'pointer' }}
                      onClick={() => handleSort('flightId')}
                    >
                      Flight {getSortIcon('flightId')}
                    </th>
                    <th>Class</th>
                    <th 
                      style={{ cursor: 'pointer' }}
                      onClick={() => handleSort('ticketPrice')}
                    >
                      Price {getSortIcon('ticketPrice')}
                    </th>
                    <th 
                      style={{ cursor: 'pointer' }}
                      onClick={() => handleSort('bookingDate')}
                    >
                      Booking Date {getSortIcon('bookingDate')}
                    </th>
                    <th 
                      style={{ cursor: 'pointer' }}
                      onClick={() => handleSort('status')}
                    >
                      Status {getSortIcon('status')}
                    </th>
                    <th style={{ width: '120px' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {sortedTickets.map(ticket => (
                    <tr key={ticket.ticketId}>
                      <td>
                        <Badge bg="secondary" className="font-monospace">
                          #{ticket.ticketId}
                        </Badge>
                      </td>
                      <td>
                        <div>
                          <div className="fw-medium">
                            {ticket.customer?.accountName || 'Unknown Customer'}
                          </div>
                          <small className="text-muted">
                            {ticket.customer?.email}
                          </small>
                        </div>
                      </td>
                      <td>
                        <div>
                          <Badge bg="primary" className="mb-1">
                            {ticket.flight?.flightCode}
                          </Badge>
                          <br />
                          <small className="text-muted">
                            {ticket.flight?.departureCityName} → {ticket.flight?.arrivalCityName}
                          </small>
                        </div>
                      </td>
                      <td>
                        <Badge 
                          style={{ 
                            backgroundColor: ticket.ticketClass?.color || '#6c757d',
                            color: '#fff'
                          }}
                        >
                          {ticket.ticketClass?.ticketClassName || 'Unknown'}
                        </Badge>
                      </td>
                      <td>
                        <strong className="text-success">
                          {ticket.ticketPrice.toLocaleString()} VND
                        </strong>
                      </td>
                      <td>
                        <div>
                          {new Date(ticket.bookingDate).toLocaleDateString()}
                        </div>
                        <small className="text-muted">
                          {new Date(ticket.bookingDate).toLocaleTimeString()}
                        </small>
                      </td>
                      <td>
                        <Badge bg={getStatusVariant(ticket.status)}>
                          {ticket.status.replace('_', ' ')}
                        </Badge>
                      </td>
                      <td>
                        <div className="d-flex gap-1">
                          {onViewDetails && (
                            <Button
                              size="sm"
                              variant="outline-info"
                              onClick={() => onViewDetails(ticket)}
                              title="View Details"
                            >
                              <i className="bi bi-eye"></i>
                            </Button>
                          )}
                          <Button
                            size="sm"
                            variant="outline-primary"
                            onClick={() => onEdit(ticket)}
                            title="Edit Ticket"
                          >
                            <i className="bi bi-pencil"></i>
                          </Button>
                          <Button
                            size="sm"
                            variant="outline-danger"
                            onClick={() => onDelete(ticket.ticketId!)}
                            title="Delete Ticket"
                          >
                            <i className="bi bi-trash"></i>
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>
    </div>
  );
};

export default TicketTable;
