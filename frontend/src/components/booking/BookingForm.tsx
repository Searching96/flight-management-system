import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner, Badge, Table, Modal } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';
import { flightService, ticketService, passengerService, bookingConfirmationService, flightTicketClassService, accountService, customerService } from '../../services';
import { flightDetailService } from '../../services/flightDetailService';
import { Flight } from '../../models';

interface BookingFormData {
  passengers: {
    passengerId: number;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    citizenId: string;
    phoneNumber: string;
    email: string;
  }[];
  ticketClassId: number;
  useFrequentFlyer: boolean;
}

const BookingForm: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  // Get booking data from sessionStorage (preferred) or fallback to query parameters
  const getBookingData = () => {
    const sessionData = sessionStorage.getItem('bookingData');

    if (sessionData) {
      try {
        const parsed = JSON.parse(sessionData);

        // console.log('Parsed booking data from sessionStorage:', parsed);
        return {
          flightId: parsed.flightId?.toString(),
          queryPassengers: parsed.passengers?.toString(),
          queryClass: parsed.class?.toString()
        };
      } catch (error) {
        console.warn('Failed to parse booking data from sessionStorage:', error);
      }
    }

    // Fallback to query parameters for backward compatibility
    const searchParams = new URLSearchParams(location.search);
    return {
      flightId: searchParams.get('flightId'),
      queryPassengers: searchParams.get('passengers'),
      queryClass: searchParams.get('class')
    };
  };

  // write a function to get account from accountId


  const { flightId, queryPassengers, queryClass } = getBookingData();

  const [flight, setFlight] = useState<Flight | null>(null);
  const [ticketClasses, setTicketClasses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [flightDetails, setFlightDetails] = useState<any[]>([]);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [pendingBookingData, setPendingBookingData] = useState<BookingFormData | null>(null);

  // Get passenger count from query param or location state
  const passengerCount = parseInt(queryPassengers || '0') || location.state?.passengerCount || 1;

  // Helper to get user info for default values
  const [accountInfo, setAccountInfo] = useState<any>(null);
  const [customerScore, setCustomerScore] = useState<number>(0);
  
  useEffect(() => {
    const logUserInfo = async () => {
      if (user?.accountTypeName === "Customer" && user?.id) {
        const userInfo = await accountService.getAccountById(user.id);
        console.log('User account info:', userInfo);
        setAccountInfo(userInfo);
        
        // Get customer's current score using customerService
        try {
          const customerInfo = await customerService.getCustomerById(user.id);
          setCustomerScore(customerInfo.score || 0);
        } catch (error) {
          console.error('Error fetching customer score:', error);
          setCustomerScore(0);
        }
      }
    };
    logUserInfo();
    // eslint-disable-next-line
  }, [user]);

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
    control
  } = useForm<BookingFormData>({
    defaultValues: {
      passengers: Array(passengerCount).fill(null).map((_, i) => ({
        passengerId: undefined,
        firstName: user?.accountTypeName === "Customer" && i === 0 ? accountInfo?.accountName || '' : '',
        lastName: user?.accountTypeName === "Customer" && i === 0 ? accountInfo?.accountName || '' : '',
        dateOfBirth: '',
        citizenId: user?.accountTypeName === "Customer" && i === 0 ? accountInfo?.citizenId || '' : '',
        phoneNumber: user?.accountTypeName === "Customer" && i === 0 ? accountInfo?.phoneNumber || '' : '',
        email: user?.accountTypeName === "Customer" && i === 0 ? user.email || '' : ''
      })),
      ticketClassId: parseInt(queryClass || '0') || 0,
      useFrequentFlyer: false
    }
  });

  // Update default values for first passenger when accountInfo is loaded
  useEffect(() => {
    if (user?.accountTypeName === "Customer" && accountInfo?.accountName) {
      console.log('Thiết lập thông tin hành khách mặc định từ tài khoản:', accountInfo);
      const nameParts = accountInfo.accountName.trim().split(' ');
      const firstName = nameParts.slice(0, -1).join(' ') || '';
      const lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : nameParts[0] || '';
      setValue('passengers.0.firstName', firstName);
      setValue('passengers.0.lastName', lastName);
      setValue('passengers.0.citizenId', accountInfo.citizenId || '');
      setValue('passengers.0.phoneNumber', accountInfo.phoneNumber || '');
      setValue('passengers.0.email', accountInfo.email || user.email || '');
    }
    // eslint-disable-next-line
  }, [accountInfo]);

  const { fields } = useFieldArray({
    control,
    name: 'passengers'
  });

  useEffect(() => {
    if (flightId) {
      loadBookingData();
      fetchFlightDetails();
    }
  }, [flightId]);

  // Set the ticket class when query parameter is provided and ticket classes are loaded
  useEffect(() => {
    if (queryClass && ticketClasses.length > 0) {
      const classId = parseInt(queryClass);
      const classExists = ticketClasses.some(tc => tc.ticketClassId === classId);
      if (classExists) {
        setValue('ticketClassId', classId);
      }
    }
  }, [queryClass, ticketClasses, setValue]);

  const loadBookingData = async () => {
    try {
      setLoading(true);
      const [flightData, ticketClassData] = await Promise.all([
        flightService.getFlightById(Number(flightId)),
        flightService.getFlightTicketClassesByFlightId(Number(flightId))
      ]);

      setFlight(flightData);
      setTicketClasses(ticketClassData);
    } catch (err: any) {
      setError('Failed to load booking information');
    } finally {
      setLoading(false);
    }
  };

  const fetchFlightDetails = async () => {
    if (flightId) {
      try {
        const flightDetail = await flightDetailService.getFlightDetailsById(Number(flightId));
        console.log('Flight Detail:', flightDetail);
        setFlightDetails(flightDetail || []);
      } catch (error) {
        console.error('Error fetching flight detail:', error);
        setFlightDetails([]);
      }
    }
  };

  // Calculate score and discount
  const calculateScoreAndDiscount = () => {
    if (!selectedClass) return { score: 0, discount: 0, discountedPrice: 0 };
    
    const standardPrice = selectedClass.specifiedFare;
    const totalStandardPrice = standardPrice * passengerCount;
    const score = Math.floor(totalStandardPrice / 10000);
    
    let discountPercent = 0;
    if (customerScore >= 200000) {
      discountPercent = 8;
    } else if (customerScore >= 50000) {
      discountPercent = 5;
    } else if (customerScore >= 10000) {
      discountPercent = 2;
    }
    
    const discountAmount = (totalStandardPrice * discountPercent) / 100;
    const discountedPrice = totalStandardPrice - discountAmount;
    
    return { score, discount: discountPercent, discountedPrice, discountAmount };
  };

  const onSubmit = async (data: BookingFormData) => {
    try {
      setSubmitting(true);
      setError('');
      
      /*
        VALIDATION LOGIC
      */
      // Check for duplicate citizen IDs
      const citizenIds = data.passengers.map(p => p.citizenId).filter(id => id);
      const duplicateCitizenIds = citizenIds.filter((id, index) => citizenIds.indexOf(id) !== index);
      
      if (duplicateCitizenIds.length > 0) {
        setError('Duplicate citizen IDs found. Each passenger must have a unique citizen ID.');
        return;
      }

      // Validate passenger data using the service
      const validationErrors: string[] = [];
      for (const passenger of data.passengers) {
        const passengerData = passengerService.transformPassengerData(passenger);
        const errors = passengerService.validatePassengerData(passengerData);
        validationErrors.push(...errors);
      }

      if (validationErrors.length > 0) {
        setError('Please correct the following errors: ' + validationErrors.join(', '));
        return;
      }

      // Check if any passenger already exists in DB by citizenId
      for (let i = 0; i < data.passengers.length; i++) {
        const passenger = data.passengers[i];
        if (passenger.citizenId) {
          try {
            const existing = await passengerService.findExistingPassenger(passenger.citizenId);
            if (existing) {
              try {
                const created = await passengerService.transformPassengerData(passenger);
                await passengerService.updatePassenger(existing.passengerId!, created);
              } catch (updateErr: any) {
                setError('Error updating passenger: ' + (updateErr.message || 'Unknown error'));
                return;
              }
              // Set passengerId if found
              data.passengers[i] = {
                ...data.passengers[i],
                ...existing,
                passengerId: existing.passengerId ?? 0
              };
            } else {
              // If not found, create new passenger
              const created = await passengerService.transformPassengerData(passenger);
              const createdPassenger = await passengerService.createPassenger(created);
              data.passengers[i] = {
                ...data.passengers[i],
                ...createdPassenger,
                passengerId: createdPassenger.passengerId ?? 0
              };
            }
          } catch (err: any) {
            // If not found (404), create new passenger
            if (err?.response?.status === 404) {
              try {
                const created = await passengerService.transformPassengerData(passenger);
                const createdPassenger = await passengerService.createPassenger(created);
                data.passengers[i] = {
                  ...data.passengers[i],
                  ...createdPassenger,
                  passengerId: createdPassenger.passengerId ?? 0
                };
              } catch (createErr: any) {
                setError('Error creating passenger: ' + (createErr.message || 'Unknown error'));
                return;
              }
            } else {
              setError('Error checking existing passenger: ' + (err.message || 'Unknown error'));
              return;
            }
          }
        }
      }

      const occupiedSeats = await flightTicketClassService.getOccupiedSeats(Number(flightId), Number(data.ticketClassId));
      const seatNumbers = data.passengers.map((_, index) => {
        const selectedClass = ticketClasses.find(tc => tc.ticketClassId === data.ticketClassId);
        const seatPrefix = selectedClass?.ticketClassName === 'Economy' ? 'A' :
          selectedClass?.ticketClassName === 'Business' ? 'B' : 'C';
        return `${seatPrefix}${occupiedSeats + index + 1}`;
      });

      const booking = {
        customerId: user?.id! ?? null,
        flightId: Number(flightId),
        passengers: data.passengers, // Keep original format for BookingReques
        seatNumbers: seatNumbers,
        ticketClassId: data.ticketClassId
      };

      // Book the tickets
      console.log("Booking data:", booking);

      let confirmationCode = '';
      try {
        confirmationCode = await ticketService.generateConfirmationCode();
      } catch (err: any) {
        console.error('confirmation code: ', err);
        return;
      }

      // Calculate final prices with discount
      const { score, discountedPrice } = calculateScoreAndDiscount();
      const pricePerTicket = discountedPrice / passengerCount;

      const tickets = data.passengers.map((passenger, index) => ({
        flightId: Number(flightId),
        ticketClassId: data.ticketClassId,
        bookCustomerId: user ? user?.accountTypeName === "Customer" && user?.id != null ? user.id : null : null,
        passengerId: passenger.passengerId,
        seatNumber: seatNumbers[index],
        fare: pricePerTicket, // Use discounted price per ticket
        confirmationCode: confirmationCode
      }));


      console.log("Tickets to be confirmed:", tickets);

      for (const ticket of tickets) {
        try {
          const newTicket = ticketService.transformTicketData(ticket);
          await ticketService.createTicket(newTicket);
          await flightTicketClassService.updateRemainingTickets(ticket.flightId, ticket.ticketClassId, 1);
          console.log("Ticket created:", newTicket);
        } catch (err: any) {
          console.error("Error creating ticket:", err);
          return;
        }
      }

      // Remove score update from here - it should happen after payment
      // Score will be updated when payment is successful

      const confirmationData = bookingConfirmationService.createConfirmation(
        confirmationCode,
        tickets,
        data.passengers.map(p => p.firstName + ' ' + p.lastName),
        flight!
      );

      bookingConfirmationService.storeGuestBookingConfirmation(confirmationData);

      // Navigate to confirmation page for guest bookings
      navigate('/booking-confirmation', {
        state: {
          confirmationCode,
          confirmationData,
          message: 'Guest booking successful! Please save your confirmation code for future reference.'
        }
      });

    } catch (err: any) {
      setError(err.message || 'Booking failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleFormSubmit = async (data: BookingFormData) => {
    // Store the form data and show confirmation modal
    setPendingBookingData(data);
    setShowConfirmModal(true);
  };

  const handleConfirmBooking = async () => {
    if (!pendingBookingData) return;

    setShowConfirmModal(false);
    await onSubmit(pendingBookingData);
  };

  const handleCancelBooking = () => {
    setShowConfirmModal(false);
    setPendingBookingData(null);
  };

  const selectedTicketClass = watch('ticketClassId');
  const selectedClass = ticketClasses.find(tc => tc.ticketClassId === selectedTicketClass);
  const calculateTotalPrice = () => {
    const { discountedPrice } = calculateScoreAndDiscount();
    return discountedPrice || 0;
  };

  const formatTime = (dateTimeString: string) => {
    return new Date(dateTimeString).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  };

  // Guard: Check if flightId is provided
  if (!flightId) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Card>
              <Card.Body className="text-center py-5">
                <Alert variant="danger" className="mb-0">
                  <Alert.Heading>Missing Flight Information</Alert.Heading>
                  <p>No flight ID provided. Please select a flight from the search results.</p>
                  <Button variant="primary" onClick={() => navigate('/flights')}>
                    Back to Flight Search
                  </Button>
                </Alert>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  if (loading) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Card>
              <Card.Body className="text-center py-5">
                <Spinner animation="border" variant="primary" className="mb-3" />
                <p className="mb-0">Loading booking form...</p>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  if (!flight) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Card>
              <Card.Body className="text-center py-5">
                <Alert variant="danger" className="mb-0">Flight not found</Alert>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  const renderPassengerForm = (index: number) => {
    // Get current values from form state
    const currentPhone = watch(`passengers.${index}.phoneNumber`) || '';
    const isAccountPassenger = user?.accountTypeName === "Customer" && index === 0;

    return (
      <Card key={index} className="mb-4">
        <Card.Header>
          <h5 className="mb-0">Passenger {index + 1}</h5>
        </Card.Header>
        <Card.Body>
          <Row className="mb-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>First Name *</Form.Label>
                <Form.Control
                  type="text"
                  {...register(`passengers.${index}.firstName`, {
                    required: 'First name is required'
                  })}
                  placeholder="Enter first name"
                  isInvalid={!!errors.passengers?.[index]?.firstName}
                  disabled={isAccountPassenger}
                />
                <Form.Control.Feedback type="invalid">
                  {errors.passengers?.[index]?.firstName?.message}
                </Form.Control.Feedback>
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group>
                <Form.Label>Last Name *</Form.Label>
                <Form.Control
                  type="text"
                  {...register(`passengers.${index}.lastName`, {
                    required: 'Last name is required'
                  })}
                  placeholder="Enter last name"
                  isInvalid={!!errors.passengers?.[index]?.lastName}
                  disabled={isAccountPassenger}
                />
                <Form.Control.Feedback type="invalid">
                  {errors.passengers?.[index]?.lastName?.message}
                </Form.Control.Feedback>
              </Form.Group>
            </Col>
          </Row>

          <Row className="mb-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>Email *</Form.Label>
                <Form.Control
                  type="email"
                  {...register(`passengers.${index}.email`, {
                    required: 'Email is required',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Invalid email address'
                    }
                  })}
                  placeholder="Enter email address"
                  isInvalid={!!errors.passengers?.[index]?.email}
                  disabled={isAccountPassenger}
                />
                <Form.Control.Feedback type="invalid">
                  {errors.passengers?.[index]?.email?.message}
                </Form.Control.Feedback>
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group>
                <Form.Label>Phone Number</Form.Label>
                <div className="d-flex gap-2">
                  <Form.Control
                    type="tel"
                    placeholder="Phone number"
                    value={currentPhone.replace(/^\+\d+\s*/, '')}
                    onChange={(e) => {
                      const phoneNumber = `${e.target.value}`;
                      setValue(`passengers.${index}.phoneNumber`, phoneNumber);
                    }}
                    disabled={isAccountPassenger}
                  />
                  <input
                    type="hidden"
                    {...register(`passengers.${index}.phoneNumber`)}
                  />
                </div>
              </Form.Group>
            </Col>
          </Row>

          <Row className="mb-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>Citizen ID *</Form.Label>
                <Form.Control
                  type="text"
                  {...register(`passengers.${index}.citizenId`, {
                    required: 'Citizen ID is required'
                  })}
                  placeholder="Enter citizen ID number"
                  isInvalid={!!errors.passengers?.[index]?.citizenId}
                  disabled={isAccountPassenger}
                />
                <Form.Control.Feedback type="invalid">
                  {errors.passengers?.[index]?.citizenId?.message}
                </Form.Control.Feedback>
              </Form.Group>
            </Col>
          </Row>
        </Card.Body>
      </Card>
    );
  };

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col md={10} lg={8}>
          {/* Header Section */}
          <div className="text-center mb-4">
            <h1 className="mb-4">Complete Your Booking</h1>
            <Card className="bg-primary text-white">
              <Card.Body>
                <div className="d-flex justify-content-center align-items-center flex-wrap gap-3">
                  <Badge bg="light" text="dark" className="fs-6 px-3 py-2">
                    {flight.flightCode}
                  </Badge>
                  <span className="fs-5">
                    {flight.departureCityName} → {flight.arrivalCityName}
                  </span>
                  <small className="opacity-75">
                    {new Date(flight.departureTime).toLocaleDateString()}
                  </small>
                </div>
              </Card.Body>
            </Card>
          </div>

          <Card className="shadow">
            <Card.Body className="p-4">
              <Form onSubmit={handleSubmit(handleFormSubmit)}>
                {/* Error and Warning Messages */}
                {error && (
                  <Alert variant="danger" className="mb-4">
                    {error}
                  </Alert>
                )}

                {/* Passenger Information */}
                <div className="mb-5 pb-4 border-bottom">
                  <h4 className="mb-3">Passenger Information</h4>
                  {fields.map((_, index) => renderPassengerForm(index))}
                </div>

                {/* Flight Details Table */}
                {flightDetails.length > 0 && (
                  <div className="mb-5 pb-4 border-bottom">
                    <h4 className="mb-3">Flight Details</h4>
                    <Table striped bordered hover size="sm" className="mb-0">
                      <thead className="table-secondary">
                        <tr>
                          <th className="text-center py-2 fs-5 fw-bold" style={{ width: '40%' }}>Medium Airport</th>
                          <th className="text-center py-2 fs-5 fw-bold" style={{ width: '30%' }}>Arrival Time</th>
                          <th className="text-center py-2 fs-5 fw-bold" style={{ width: '30%' }}>Layover Duration</th>
                        </tr>
                      </thead>
                      <tbody className="table-light">
                        {flightDetails.map((detail, index) => (
                          <tr key={index}>
                            <td className="text-center align-middle py-3">
                              <strong>{detail.mediumAirportName || 'N/A'}</strong>
                            </td>
                            <td className="text-center align-middle py-3">
                              {detail.arrivalTime
                                ? <span className="badge bg-info">{formatTime(detail.arrivalTime)}</span>
                                : <span className="text-muted">N/A</span>
                              }
                            </td>
                            <td className="text-center align-middle py-3">
                              {detail.layoverDuration
                                ? <span className="badge bg-secondary">{detail.layoverDuration}</span>
                                : <span className="text-muted">N/A</span>
                              }
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </Table>
                  </div>
                )}

                {/* Booking Summary */}
                <Card className="bg-light mb-4">
                  <Card.Body>
                    <h4 className="mb-3">Booking Summary</h4>
                    <Row className="g-2">
                      <Col xs={6}>
                        <strong>Flight:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        {flight.flightCode}
                      </Col>

                      <Col xs={6}>
                        <strong>Passengers:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        {passengerCount}
                      </Col>

                      {selectedClass && (
                        <>
                          <Col xs={6}>
                            <strong>Class:</strong>
                          </Col>
                          <Col xs={6} className="text-end">
                            {selectedClass.ticketClassName}
                          </Col>

                          <Col xs={6}>
                            <strong>Price per ticket:</strong>
                          </Col>
                          <Col xs={6} className="text-end">
                            ${selectedClass.specifiedFare}
                          </Col>

                          {user?.accountTypeName === "Customer" && (
                            <>
                              <Col xs={6}>
                                <strong>Current Score:</strong>
                              </Col>
                              <Col xs={6} className="text-end">
                                {customerScore.toLocaleString()}
                              </Col>

                              {calculateScoreAndDiscount().discount > 0 && (
                                <>
                                  <Col xs={6}>
                                    <strong className="text-success">Discount ({calculateScoreAndDiscount().discount}%):</strong>
                                  </Col>
                                  <Col xs={6} className="text-end text-success">
                                    -${(calculateScoreAndDiscount().discountAmount ?? 0).toFixed(2)}
                                  </Col>
                                </>
                              )}

                              <Col xs={6}>
                                <strong className="text-info">Score to Earn:</strong>
                              </Col>
                              <Col xs={6} className="text-end text-info">
                                +{calculateScoreAndDiscount().score.toLocaleString()}
                              </Col>
                            </>
                          )}

                          <Col xs={12}>
                            <hr className="my-2" />
                          </Col>

                          <Col xs={6}>
                            <strong className="text-primary fs-5">Total:</strong>
                          </Col>
                          <Col xs={6} className="text-end">
                            <strong className="text-primary fs-5">${calculateTotalPrice().toFixed(2)}</strong>
                          </Col>
                        </>
                      )}
                    </Row>
                  </Card.Body>
                </Card>

                {/* Form Actions */}
                <div className="d-flex justify-content-between gap-3">
                  <Button
                    variant="outline-secondary"
                    onClick={() => navigate(-1)}
                    size="lg"
                  >
                    Back
                  </Button>
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={submitting || !selectedClass}
                    size="lg"
                    className="flex-fill"
                    style={{ maxWidth: '300px' }}
                  >
                    {submitting ? (
                      <>
                        <Spinner animation="border" size="sm" className="me-2" />
                        Processing...
                      </>
                    ) : (
                      `Book Flight - $${calculateTotalPrice()}`
                    )}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>

          {/* Confirmation Modal */}
          <Modal
            show={showConfirmModal}
            onHide={handleCancelBooking}
            centered
            size="lg"
          >
            <Modal.Header closeButton className="bg-primary text-white">
              <Modal.Title>
                <i className="bi bi-exclamation-triangle me-2"></i>
                Confirm Your Booking
              </Modal.Title>
            </Modal.Header>
            <Modal.Body className="p-4">
              <div className="text-center mb-4">
                <h5 className="text-primary">Please review your booking details</h5>
                <p className="text-muted">Once confirmed, this booking cannot be undone</p>
              </div>

              {flight && selectedClass && (
                <Card className="bg-light">
                  <Card.Body>
                    <h6 className="fw-bold mb-3">Booking Summary</h6>
                    <Row className="g-2">
                      <Col xs={6}>
                        <strong>Flight:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        {flight.flightCode}
                      </Col>

                      <Col xs={6}>
                        <strong>Route:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        {flight.departureCityName} → {flight.arrivalCityName}
                      </Col>

                      <Col xs={6}>
                        <strong>Departure:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        {new Date(flight.departureTime).toLocaleString()}
                      </Col>

                      <Col xs={6}>
                        <strong>Passengers:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        {passengerCount}
                      </Col>

                      <Col xs={6}>
                        <strong>Class:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        {selectedClass.ticketClassName}
                      </Col>

                      <Col xs={6}>
                        <strong>Price per ticket:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        ${selectedClass.specifiedFare}
                      </Col>

                      <Col xs={12}>
                        <hr className="my-2" />
                      </Col>

                      <Col xs={6}>
                        <strong className="text-primary fs-5">Total Amount:</strong>
                      </Col>
                      <Col xs={6} className="text-end">
                        <strong className="text-primary fs-4">${calculateTotalPrice().toFixed(2)}</strong>
                      </Col>
                    </Row>
                  </Card.Body>
                </Card>
              )}
            </Modal.Body>
            <Modal.Footer>
              <Button
                variant="secondary"
                onClick={handleCancelBooking}
                disabled={submitting}
              >
                Cancel
              </Button>
              <Button
                variant="success"
                onClick={handleConfirmBooking}
                disabled={submitting}
                size="lg"
              >
                {submitting ? (
                  <>
                    <Spinner animation="border" size="sm" className="me-2" />
                    Processing...
                  </>
                ) : (
                  <>
                    <i className="bi bi-check-circle me-2"></i>
                    Confirm Booking - ${calculateTotalPrice().toFixed(2)}
                  </>
                )}
              </Button>
            </Modal.Footer>
          </Modal>
        </Col>
      </Row>
    </Container>
  );
};

export default BookingForm;