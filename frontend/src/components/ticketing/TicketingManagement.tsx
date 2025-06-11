import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Badge, Button, Form, InputGroup, Spinner, Alert, Modal } from 'react-bootstrap';
import { ticketService, flightService, passengerService, flightTicketClassService } from '../../services';

interface TicketInfo {
   ticketId: number;
   flightCode: string;
   departureTime: string;
   arrivalTime: string;
   departureAirport: string;
   arrivalAirport: string;
   passengerCitizenId: string;
   passengerName: string;
   phoneNumber: string;
   paymentTime: string | null;
   ticketStatus: 'PAID' | 'UNPAID';
   fare: number;
   seatNumber: string;
   confirmationCode: string;
   ticketClassName: string;
}

const TicketingManagement: React.FC = () => {
   const [tickets, setTickets] = useState<TicketInfo[]>([]);
   const [filteredTickets, setFilteredTickets] = useState<TicketInfo[]>([]);
   const [loading, setLoading] = useState(true);
   const [error, setError] = useState<string | null>(null);
   const [searchTerm, setSearchTerm] = useState('');
   const [statusFilter, setStatusFilter] = useState<'ALL' | 'PAID' | 'UNPAID'>('ALL');
   const [selectedTicket, setSelectedTicket] = useState<TicketInfo | null>(null);
   const [showDetailsModal, setShowDetailsModal] = useState(false);
   const [showCancelModal, setShowCancelModal] = useState(false);
   const [cancelLoading, setCancelLoading] = useState(false);

   useEffect(() => {
      fetchAllTickets();
   }, []);

   useEffect(() => {
      filterTickets();
   }, [tickets, searchTerm, statusFilter]);

   const fetchAllTickets = async () => {
      try {
         setLoading(true);
         setError(null);

         // Get all tickets
         const allTickets = await ticketService.getAllTickets();

         console.log('Fetched tickets:', allTickets);

         // Process each ticket to get complete information
         const ticketInfoPromises = allTickets.map(async (ticket: any) => {
            try {
               // Validate ticket object
               if (!ticket || !ticket.ticketId || !ticket.flightId || !ticket.passengerId) {
                  console.warn('Invalid ticket object:', ticket);
                  return null;
               }

               // Get flight information
               const flight = await flightService.getFlightById(ticket.flightId);

               // Get passenger information
               const passenger = await passengerService.getPassengerById(ticket.passengerId);

               // Validate flight and passenger data
               if (!flight || !passenger) {
                  console.warn('Missing flight or passenger data for ticket:', ticket.ticketId);
                  return null;
               }

               // Determine payment status based on paymentTime or other criteria
               const ticketStatus: 'PAID' | 'UNPAID' = ticket.paymentTime ? 'PAID' : 'UNPAID';

               // Determine ticket class based on seat number prefix
               const getTicketClassFromSeat = (seatNumber: string) => {
                  if (!seatNumber) return 'Economy';
                  const firstChar = seatNumber.charAt(0).toUpperCase();
                  switch (firstChar) {
                     case 'A':
                        return 'Economy';
                     case 'B':
                        return 'Business';
                     case 'C':
                        return 'First Class';
                     default:
                        return 'Economy';
                  }
               };

               const seatNumber = ticket.seatNumber || 'A1';
               const ticketClassName = getTicketClassFromSeat(seatNumber);

               return {
                  ticketId: ticket.ticketId,
                  flightCode: flight.flightCode || 'N/A',
                  departureTime: flight.departureTime,
                  arrivalTime: flight.arrivalTime,
                  departureAirport: flight.departureAirportName || 'N/A',
                  arrivalAirport: flight.arrivalAirportName || 'N/A',
                  passengerCitizenId: passenger.citizenId || 'N/A',
                  passengerName: passenger.passengerName || 'N/A',
                  phoneNumber: passenger.phoneNumber,
                  paymentTime: ticket.paymentTime,
                  ticketStatus,
                  fare: ticket.fare || 0,
                  seatNumber: seatNumber,
                  confirmationCode: ticket.confirmationCode || 'N/A',
                  ticketClassName: ticketClassName
               } as TicketInfo;
            } catch (err) {
               console.error(`Error processing ticket ${ticket?.ticketId}:`, err);
               return null;
            }
         });

         const processedTickets = await Promise.all(ticketInfoPromises);
         const validTickets = processedTickets.filter((ticket): ticket is TicketInfo => ticket !== null);

         console.log('Processed tickets:', validTickets);
         setTickets(validTickets);
      } catch (err: any) {
         console.error('Error fetching tickets:', err);
         setError(`Failed to load tickets: ${err.message || 'Unknown error'}`);
         setTickets([]); // Set empty array on error
      } finally {
         setLoading(false);
      }
   };

   const filterTickets = () => {
      let filtered = tickets;

      // Filter by search term
      if (searchTerm) {
         filtered = filtered.filter(ticket =>
            ticket.flightCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
            ticket.passengerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
            ticket.passengerCitizenId.toLowerCase().includes(searchTerm.toLowerCase()) ||
            ticket.confirmationCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
            ticket.phoneNumber.toLowerCase().includes(searchTerm.toLowerCase())
         );
      }

      // Filter by status
      if (statusFilter !== 'ALL') {
         filtered = filtered.filter(ticket => ticket.ticketStatus === statusFilter);
      }

      setFilteredTickets(filtered);
   };

   const formatDateTime = (dateString: string) => {
      return new Date(dateString).toLocaleString('en-US', {
         year: 'numeric',
         month: 'short',
         day: 'numeric',
         hour: '2-digit',
         minute: '2-digit'
      });
   };

   const formatCurrency = (amount: number) => {
      return new Intl.NumberFormat('en-US', {
         style: 'currency',
         currency: 'USD'
      }).format(amount);
   };

   const handleShowDetails = (ticket: TicketInfo) => {
      setSelectedTicket(ticket);
      setShowDetailsModal(true);
   };

   const getStatusBadge = (status: 'PAID' | 'UNPAID') => {
      return (
         <Badge bg={status === 'PAID' ? 'success' : 'warning'}>
            {status}
         </Badge>
      );
   };

   const getSeatBadgeColor = (seatNumber: string) => {
      if (!seatNumber) return '#6c757d'; // default gray
      const firstChar = seatNumber.charAt(0).toUpperCase();
      switch (firstChar) {
         case 'A':
            return '#3498db'; // blue
         case 'B':
            return '#f39c12'; // orange
         case 'C':
            return '#e74c3c'; // red
         default:
            return '#6c757d'; // default gray
      }
   };

   const handleCancelTicket = () => {
      setShowCancelModal(true);
   };

   const confirmCancelTicket = async () => {
      if (!selectedTicket) return;

      setCancelLoading(true);
      try {
         // Delete the ticket
         const ticketData = await ticketService.getTicketById(selectedTicket.ticketId);
         if (ticketData && ticketData.ticketClassId && ticketData.flightId) {
            await flightTicketClassService.updateRemainingTickets(
               ticketData.flightId,
               ticketData.ticketClassId,
               -1
            );
         }
         await ticketService.deleteTicket(selectedTicket.ticketId);
         await fetchAllTickets();

         // Close modals
         setShowCancelModal(false);
         setShowDetailsModal(false);
         setSelectedTicket(null);

         // Show success message (you could use a toast notification here)
         //alert('Ticket cancelled successfully');
      } catch (err: any) {
         console.error('Error canceling ticket:', err);
         alert('Failed to cancel ticket: ' + (err.message || 'Unknown error'));
      } finally {
         setCancelLoading(false);
      }
   };

   if (loading) {
      return (
         <Container className="py-5">
            <Row className="justify-content-center">
               <Col md={8}>
                  <Card>
                     <Card.Body className="text-center py-5">
                        <Spinner animation="border" variant="primary" className="mb-3" />
                        <p className="mb-0">Loading tickets...</p>
                     </Card.Body>
                  </Card>
               </Col>
            </Row>
         </Container>
      );
   }

   return (
      <Container fluid className="py-4">
         <Row>
            <Col>
               <Card className="shadow">
                  <Card.Header className="bg-primary text-white">
                     <div className="d-flex justify-content-between align-items-center">
                        <h4 className="mb-0">
                           <i className="bi bi-ticket-perforated me-2"></i>
                           Ticketing Management
                        </h4>
                        <Badge bg="light" text="dark" className="fs-6">
                           {filteredTickets.length} tickets
                        </Badge>
                     </div>
                  </Card.Header>

                  <Card.Body className="p-0">
                     {error && (
                        <Alert variant="danger" className="m-3 mb-0">
                           {error}
                        </Alert>
                     )}

                     {/* Filters */}
                     <div className="p-3 border-bottom bg-light">
                        <Row className="g-3">
                           <Col md={6}>
                              <InputGroup>
                                 <InputGroup.Text>
                                    <i className="bi bi-search"></i>
                                 </InputGroup.Text>
                                 <Form.Control
                                    type="text"
                                    placeholder="Search by flight code, passenger name, citizen ID, or confirmation code..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                 />
                              </InputGroup>
                           </Col>
                           <Col md={3}>
                              <Form.Select
                                 value={statusFilter}
                                 onChange={(e) => setStatusFilter(e.target.value as 'ALL' | 'PAID' | 'UNPAID')}
                              >
                                 <option value="ALL">All Status</option>
                                 <option value="PAID">Paid</option>
                                 <option value="UNPAID">Unpaid</option>
                              </Form.Select>
                           </Col>
                           <Col md={3}>
                              <Button variant="outline-primary" onClick={fetchAllTickets} className="w-100">
                                 <i className="bi bi-arrow-clockwise me-1"></i>
                                 Refresh
                              </Button>
                           </Col>
                        </Row>
                     </div>

                     {/* Tickets Table */}
                     <div className="table-responsive">
                        <Table hover className="mb-0">
                           <thead className="table-dark">
                              <tr>
                                 <th>Flight Code</th>
                                 <th>Route</th>
                                 <th>Departure</th>
                                 <th>Passenger</th>
                                 <th>Contact</th>
                                 <th>Status</th>
                                 <th>Fare</th>
                                 <th>Seat</th>
                                 <th>Actions</th>
                              </tr>
                           </thead>
                           <tbody>
                              {filteredTickets.length === 0 ? (
                                 <tr>
                                    <td colSpan={9} className="text-center py-4 text-muted">
                                       <i className="bi bi-inbox display-6 d-block mb-2"></i>
                                       No tickets found
                                    </td>
                                 </tr>
                              ) : (
                                 filteredTickets.map((ticket) => (
                                    <tr key={ticket.ticketId}>
                                       <td>
                                          <strong className="text-primary">{ticket.flightCode}</strong>
                                          <br />
                                          <small className="text-muted">{ticket.ticketClassName}</small>
                                       </td>
                                       <td>
                                          <div className="small">
                                             <strong>{ticket.departureAirport}</strong>
                                             <br />
                                             <i className="bi bi-arrow-down"></i>
                                             <br />
                                             <strong>{ticket.arrivalAirport}</strong>
                                          </div>
                                       </td>
                                       <td>
                                          <div className="small">
                                             <strong>Dep:</strong> {formatDateTime(ticket.departureTime)}
                                             <br />
                                             <strong>Arr:</strong> {formatDateTime(ticket.arrivalTime)}
                                          </div>
                                       </td>
                                       <td>
                                          <div>
                                             <strong>{ticket.passengerName}</strong>
                                             <br />
                                             <small className="text-muted">ID: {ticket.passengerCitizenId}</small>
                                          </div>
                                       </td>
                                       <td>
                                          <small>{ticket.phoneNumber || 'N/A'}</small>
                                       </td>
                                       <td>
                                          {getStatusBadge(ticket.ticketStatus)}
                                          {ticket.paymentTime && (
                                             <div className="small text-muted mt-1">
                                                Paid: {formatDateTime(ticket.paymentTime)}
                                             </div>
                                          )}
                                       </td>
                                       <td>
                                          <strong>{formatCurrency(ticket.fare)}</strong>
                                       </td>
                                       <td>
                                          <Badge
                                             style={{ backgroundColor: getSeatBadgeColor(ticket.seatNumber), border: 'none' }}
                                          >
                                             {ticket.seatNumber}
                                          </Badge>
                                       </td>
                                       <td>
                                          <Button
                                             variant="outline-primary"
                                             size="sm"
                                             onClick={() => handleShowDetails(ticket)}
                                          >
                                             <i className="bi bi-eye"></i>
                                          </Button>
                                       </td>
                                    </tr>
                                 ))
                              )}
                           </tbody>
                        </Table>
                     </div>
                  </Card.Body>
               </Card>
            </Col>
         </Row>

         {/* Ticket Details Modal */}
         <Modal show={showDetailsModal} onHide={() => setShowDetailsModal(false)} size="lg">
            <Modal.Header closeButton>
               <Modal.Title>
                  <i className="bi bi-ticket-detailed me-2"></i>
                  Ticket Details
               </Modal.Title>
            </Modal.Header>
            <Modal.Body>
               {selectedTicket && (
                  <Row>
                     <Col md={6}>
                        <Card className="h-100">
                           <Card.Header>
                              <h6 className="mb-0">Flight Information</h6>
                           </Card.Header>
                           <Card.Body>
                              <dl className="row mb-0">
                                 <dt className="col-sm-4">Flight Code:</dt>
                                 <dd className="col-sm-8">{selectedTicket.flightCode}</dd>

                                 <dt className="col-sm-4">Class:</dt>
                                 <dd className="col-sm-8">{selectedTicket.ticketClassName}</dd>

                                 <dt className="col-sm-4">Route:</dt>
                                 <dd className="col-sm-8">{selectedTicket.departureAirport} → {selectedTicket.arrivalAirport}</dd>

                                 <dt className="col-sm-4">Departure:</dt>
                                 <dd className="col-sm-8">{formatDateTime(selectedTicket.departureTime)}</dd>

                                 <dt className="col-sm-4">Arrival:</dt>
                                 <dd className="col-sm-8">{formatDateTime(selectedTicket.arrivalTime)}</dd>

                                 <dt className="col-sm-4">Seat:</dt>
                                 <dd className="col-sm-8">
                                    <Badge
                                       style={{ backgroundColor: getSeatBadgeColor(selectedTicket.seatNumber), border: 'none' }}
                                    >
                                       {selectedTicket.seatNumber}
                                    </Badge>
                                 </dd>
                              </dl>
                           </Card.Body>
                        </Card>
                     </Col>
                     <Col md={6}>
                        <Card className="h-100">
                           <Card.Header>
                              <h6 className="mb-0">Passenger & Payment</h6>
                           </Card.Header>
                           <Card.Body>
                              <dl className="row mb-0">
                                 <dt className="col-sm-5">Name:</dt>
                                 <dd className="col-sm-7">{selectedTicket.passengerName}</dd>

                                 <dt className="col-sm-5">Citizen ID:</dt>
                                 <dd className="col-sm-7">{selectedTicket.passengerCitizenId}</dd>

                                 <dt className="col-sm-5">Phone:</dt>
                                 <dd className="col-sm-7">{selectedTicket.phoneNumber || 'N/A'}</dd>

                                 <dt className="col-sm-5">Confirmation:</dt>
                                 <dd className="col-sm-7"><code>{selectedTicket.confirmationCode}</code></dd>

                                 <dt className="col-sm-5">Status:</dt>
                                 <dd className="col-sm-7">{getStatusBadge(selectedTicket.ticketStatus)}</dd>

                                 <dt className="col-sm-5">Fare:</dt>
                                 <dd className="col-sm-7"><strong>{formatCurrency(selectedTicket.fare)}</strong></dd>

                                 <dt className="col-sm-5">Payment Time:</dt>
                                 <dd className="col-sm-7">
                                    {selectedTicket.paymentTime ? formatDateTime(selectedTicket.paymentTime) : 'Not paid'}
                                 </dd>
                              </dl>
                           </Card.Body>
                        </Card>
                     </Col>
                  </Row>
               )}
            </Modal.Body>
            <Modal.Footer>
               <Button variant="secondary" onClick={() => setShowDetailsModal(false)}>
                  Close
               </Button>
               {selectedTicket && (
                  <Button variant="danger" onClick={handleCancelTicket}>
                     <i className="bi bi-x-circle me-2"></i>
                     Cancel Ticket
                  </Button>
               )}
            </Modal.Footer>
         </Modal>

         {/* Cancel Ticket Confirmation Modal */}
         <Modal
            show={showCancelModal}
            onHide={() => setShowCancelModal(false)}
            centered
            size="sm"
         >
            <Modal.Header closeButton className="bg-danger text-white">
               <Modal.Title>
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  Cancel Ticket
               </Modal.Title>
            </Modal.Header>
            <Modal.Body className="p-4">
               <div className="text-center mb-3">
                  <i className="bi bi-exclamation-circle text-danger" style={{ fontSize: '3rem' }}></i>
               </div>
               <h5 className="text-center mb-3">Are you sure you want to cancel this ticket?</h5>
               <p className="text-center text-muted mb-0">
                  This action cannot be undone. The ticket will be permanently cancelled.
               </p>
               {selectedTicket && (
                  <div className="mt-3 p-3 bg-light rounded">
                     <div className="text-center">
                        <strong>Ticket ID: {selectedTicket.ticketId}</strong><br />
                        <span className="text-muted">{selectedTicket.flightCode} - {selectedTicket.passengerName}</span><br />
                        <span className="text-muted">Seat: {selectedTicket.seatNumber}</span><br />
                        <span className="text-primary fw-bold">{formatCurrency(selectedTicket.fare)}</span>
                     </div>
                  </div>
               )}
            </Modal.Body>
            <Modal.Footer>
               <Button
                  variant="secondary"
                  onClick={() => setShowCancelModal(false)}
                  disabled={cancelLoading}
               >
                  Keep Ticket
               </Button>
               <Button
                  variant="danger"
                  onClick={confirmCancelTicket}
                  disabled={cancelLoading}
               >
                  {cancelLoading ? (
                     <>
                        <Spinner animation="border" size="sm" className="me-2" />
                        Cancelling...
                     </>
                  ) : (
                     <>
                        <i className="bi bi-trash me-2"></i>
                        Yes, Cancel Ticket
                     </>
                  )}
               </Button>
            </Modal.Footer>
         </Modal>
      </Container>
   );
};

export default TicketingManagement;
