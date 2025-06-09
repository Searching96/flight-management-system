import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Row, Col, Form, Button, Alert } from 'react-bootstrap';
import { Ticket, TicketRequest, TicketStatus, Customer, Flight, TicketClass } from '../../../models';
import TypeAhead from '../../common/TypeAhead';

interface TicketFormProps {
  ticket?: Ticket;
  customers: Customer[];
  flights: Flight[];
  ticketClasses: TicketClass[];
  onSubmit: (data: TicketRequest) => void;
  onCancel: () => void;
}

const TicketForm: React.FC<TicketFormProps> = ({
  ticket,
  customers,
  flights,
  ticketClasses,
  onSubmit,
  onCancel
}) => {
  const [selectedCustomer, setSelectedCustomer] = useState<number | ''>(
    ticket?.customerId || ''
  );
  const [selectedFlight, setSelectedFlight] = useState<number | ''>(
    ticket?.flightId || ''
  );
  const [selectedTicketClass, setSelectedTicketClass] = useState<number | ''>(
    ticket?.ticketClassId || ''
  );

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors }
  } = useForm<TicketRequest>();

  const watchedFlight = watch('flightId');

  // Reset form when ticket changes
  useEffect(() => {
    if (ticket) {
      reset({
        customerId: ticket.customerId,
        flightId: ticket.flightId,
        ticketClassId: ticket.ticketClassId,
        ticketPrice: ticket.ticketPrice,
        status: ticket.status
      });
      setSelectedCustomer(ticket.customerId);
      setSelectedFlight(ticket.flightId);
      setSelectedTicketClass(ticket.ticketClassId);
    } else {
      reset({
        customerId: 0,
        flightId: 0,
        ticketClassId: 0,
        ticketPrice: 0,
        status: TicketStatus.PENDING
      });
      setSelectedCustomer('');
      setSelectedFlight('');
      setSelectedTicketClass('');
    }
  }, [ticket, reset]);

  // Customer options for TypeAhead
  const customerOptions = customers.map(customer => ({
    value: customer.customerId!,
    label: `${customer.fullName} (${customer.email})`,
    email: customer.email
  }));

  // Flight options for TypeAhead
  const flightOptions = flights.map(flight => ({
    value: flight.flightId!,
    label: `${flight.flightCode} - ${flight.departureCityName} → ${flight.arrivalCityName}`,
    code: flight.flightCode,
    route: `${flight.departureCityName} → ${flight.arrivalCityName}`
  }));

  // Filter ticket classes based on selected flight
  const availableTicketClasses = ticketClasses.filter(tc => {
    if (!selectedFlight) return true;
    // In a real implementation, you'd filter based on flight-ticket-class associations
    return true;
  });

  const ticketClassOptions = availableTicketClasses.map(tc => ({
    value: tc.ticketClassId!,
    label: tc.ticketClassName,
    color: tc.color
  }));

  const statusOptions = Object.values(TicketStatus).map(status => ({
    value: status,
    label: status.replace('_', ' ')
  }));

  const submitWithValidation = (data: TicketRequest) => {
    // Validate required fields
    if (!data.customerId || data.customerId === 0) {
      return;
    }
    if (!data.flightId || data.flightId === 0) {
      return;
    }
    if (!data.ticketClassId || data.ticketClassId === 0) {
      return;
    }
    if (!data.ticketPrice || data.ticketPrice <= 0) {
      return;
    }

    onSubmit(data);
  };

  return (
    <Form onSubmit={handleSubmit(submitWithValidation)}>
      {/* Customer Selection */}
      <Row className="mb-3">
        <Col>
          <Form.Group>
            <Form.Label>Customer</Form.Label>
            <TypeAhead
              options={customerOptions}
              value={selectedCustomer}
              onChange={(option) => {
                const customerId = option?.value as number || '';
                setSelectedCustomer(customerId);
                setValue('customerId', Number(customerId), { 
                  shouldValidate: true 
                });
              }}
              placeholder="Search customer..."
              error={!!errors.customerId}
            />
            <input
              type="hidden"
              {...register('customerId', {
                required: 'Customer is required',
                valueAsNumber: true,
                min: { value: 1, message: 'Please select a customer' }
              })}
            />
            {errors.customerId && (
              <div className="text-danger small mt-1">{errors.customerId.message}</div>
            )}
          </Form.Group>
        </Col>
      </Row>

      {/* Flight and Ticket Class Selection */}
      <Row className="mb-3">
        <Col md={6}>
          <Form.Group>
            <Form.Label>Flight</Form.Label>
            <TypeAhead
              options={flightOptions}
              value={selectedFlight}
              onChange={(option) => {
                const flightId = option?.value as number || '';
                setSelectedFlight(flightId);
                setValue('flightId', Number(flightId), { 
                  shouldValidate: true 
                });
                // Reset ticket class when flight changes
                setSelectedTicketClass('');
                setValue('ticketClassId', 0);
              }}
              placeholder="Search flight..."
              error={!!errors.flightId}
            />
            <input
              type="hidden"
              {...register('flightId', {
                required: 'Flight is required',
                valueAsNumber: true,
                min: { value: 1, message: 'Please select a flight' }
              })}
            />
            {errors.flightId && (
              <div className="text-danger small mt-1">{errors.flightId.message}</div>
            )}
          </Form.Group>
        </Col>

        <Col md={6}>
          <Form.Group>
            <Form.Label>Ticket Class</Form.Label>
            <TypeAhead
              options={ticketClassOptions}
              value={selectedTicketClass}
              onChange={(option) => {
                const ticketClassId = option?.value as number || '';
                setSelectedTicketClass(ticketClassId);
                setValue('ticketClassId', Number(ticketClassId), { 
                  shouldValidate: true 
                });
              }}
              placeholder="Select ticket class..."
              error={!!errors.ticketClassId}
              disabled={!selectedFlight}
            />
            <input
              type="hidden"
              {...register('ticketClassId', {
                required: 'Ticket class is required',
                valueAsNumber: true,
                min: { value: 1, message: 'Please select a ticket class' }
              })}
            />
            {errors.ticketClassId && (
              <div className="text-danger small mt-1">{errors.ticketClassId.message}</div>
            )}
          </Form.Group>
        </Col>
      </Row>

      {/* Price and Status */}
      <Row className="mb-3">
        <Col md={6}>
          <Form.Group>
            <Form.Label>Ticket Price (VND)</Form.Label>
            <Form.Control
              type="number"
              min="0"
              step="1000"
              {...register('ticketPrice', {
                required: 'Ticket price is required',
                valueAsNumber: true,
                min: { value: 1, message: 'Price must be greater than 0' }
              })}
              isInvalid={!!errors.ticketPrice}
              placeholder="e.g., 1500000"
            />
            <Form.Control.Feedback type="invalid">
              {errors.ticketPrice?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>

        <Col md={6}>
          <Form.Group>
            <Form.Label>Status</Form.Label>
            <Form.Select
              {...register('status', {
                required: 'Status is required'
              })}
              isInvalid={!!errors.status}
            >
              <option value="">Select status...</option>
              {statusOptions.map(option => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </Form.Select>
            <Form.Control.Feedback type="invalid">
              {errors.status?.message}
            </Form.Control.Feedback>
          </Form.Group>
        </Col>
      </Row>

      {/* Form Actions */}
      <Row className="mb-3">
        <Col>
          <Button variant="secondary" onClick={onCancel} className="me-2">
            Cancel
          </Button>
          <Button variant="primary" type="submit">
            {ticket ? 'Update Ticket' : 'Create Ticket'}
          </Button>
        </Col>
      </Row>
    </Form>
  );
};

export default TicketForm;
