import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Button, Alert, Spinner, Badge, ListGroup, Modal } from 'react-bootstrap';
import { BookingConfirmation } from '../../services/bookingConfirmationService';
import { ticketService, flightService, passengerService, paymentService } from '../../services';

const PaymentHandler: React.FC = () => {
  const { confirmationCode } = useParams<{ confirmationCode: string }>();
  const navigate = useNavigate();
  
  const [booking, setBooking] = useState<BookingConfirmation | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [processingPayment, setProcessingPayment] = useState(false);
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [modalTitle, setModalTitle] = useState('');

  useEffect(() => {
    if (confirmationCode) {
      loadBookingDetails();
    } else {
      setError('Mã xác nhận không hợp lệ');
      setLoading(false);
    }
  }, [confirmationCode]);

  const loadBookingDetails = async () => {
    if (!confirmationCode) return;

    try {
      setLoading(true);
      setError('');

      const tickets = await ticketService.getTicketsOnConfirmationCode(confirmationCode);

      if (!tickets || tickets.length === 0) {
        setError('Không tìm thấy đặt chỗ với mã xác nhận này');
        return;
      }

      // Check if booking is already paid
      const isPaid = tickets.every(ticket => ticket.ticketStatus === 1);
      if (isPaid) {
        setError('Đặt chỗ này đã được thanh toán');
        return;
      }

      // Check if there are any unpaid tickets
      const hasUnpaidTickets = tickets.some(ticket => ticket.ticketStatus === 0);
      if (!hasUnpaidTickets) {
        setError('Không có vé nào cần thanh toán');
        return;
      }

      const firstTicket = tickets[0];
      const flight = await flightService.getFlightById(firstTicket.flightId!);

      const passengerNames = await Promise.all(
        tickets.map(async (ticket) => {
          try {
            const passenger = await passengerService.getPassengerById(ticket.passengerId!);
            return passenger.passengerName;
          } catch (error) {
            console.error(`Lỗi khi lấy thông tin hành khách ${ticket.passengerId}:`, error);
            return `Hành khách ${ticket.passengerId}`;
          }
        })
      );

      // Calculate total for unpaid tickets only
      const totalAmount = tickets
        .filter(ticket => ticket.ticketStatus === 0)
        .reduce((sum, ticket) => sum + (ticket.fare || 0), 0);

      const bookingData: BookingConfirmation = {
        confirmationCode: confirmationCode,
        bookingDate: new Date().toISOString(),
        tickets: tickets,
        passengers: passengerNames,
        totalAmount: totalAmount,
        flightInfo: {
          flightCode: flight.flightCode || '',
          departureTime: flight.departureTime || '',
          arrivalTime: flight.arrivalTime || '',
          departureCity: flight.departureCityName || '',
          arrivalCity: flight.arrivalCityName || ''
        }
      };

      setBooking(bookingData);
      console.log(`Booking loaded at 2025-06-11 05:14:08 UTC by user for confirmation: ${confirmationCode}`);
    } catch (err: any) {
      console.error('Lỗi khi tải thông tin đặt chỗ:', err);
      setError('Không thể tải thông tin đặt chỗ. Vui lòng thử lại sau.');
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async () => {
    if (!booking) return;

    try {
      setProcessingPayment(true);
      console.log(`Payment initiated at 2025-06-11 05:14:08 UTC by user for ${booking.confirmationCode}`);
      
      const response = await paymentService.createPayment(booking.confirmationCode);
      
      if (response && response.data) {
        console.log('Chuyển hướng đến URL thanh toán:', response.data);
        window.location.href = response.data;
      } else {
        setModalTitle('Lỗi thanh toán');
        setModalMessage('URL thanh toán không hợp lệ. Vui lòng thử lại.');
        setShowErrorModal(true);
      }
    } catch (error) {
      console.error('Tạo thanh toán thất bại:', error);
      setModalTitle('Lỗi thanh toán');
      setModalMessage('Không thể tạo thanh toán. Vui lòng thử lại sau.');
      setShowErrorModal(true);
    } finally {
      setProcessingPayment(false);
    }
  };

  const formatDateTime = (dateString: string) => {
    try {
      return new Date(dateString).toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        timeZone: 'Asia/Ho_Chi_Minh'
      });
    } catch (error) {
      console.error('Lỗi định dạng thời gian:', error);
      return dateString;
    }
  };

  const formatCurrency = (amount: number) => {
    return amount.toLocaleString('vi-VN', {
      style: 'currency',
      currency: 'VND'
    });
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" role="status" variant="primary">
          <span className="visually-hidden">Đang tải...</span>
        </Spinner>
        <p className="mt-3">Đang tải thông tin đặt chỗ...</p>
        <p className="text-muted small">Xử lý tại: 2025-06-11 05:14:08 UTC bởi user</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Card className="border-danger">
              <Card.Header className="bg-danger text-white">
                <h4 className="mb-0">
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  Lỗi
                </h4>
              </Card.Header>
              <Card.Body className="text-center p-4">
                <Alert variant="danger" className="mb-4">
                  {error}
                </Alert>
                <div className="d-flex gap-3 justify-content-center">
                  <Button variant="primary" onClick={() => navigate('/booking-lookup')}>
                    <i className="bi bi-search me-2"></i>
                    Tìm kiếm đặt chỗ
                  </Button>
                  <Button variant="outline-secondary" onClick={() => navigate('/')}>
                    <i className="bi bi-house me-2"></i>
                    Về trang chủ
                  </Button>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  if (!booking) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Alert variant="warning" className="text-center">
              <i className="bi bi-exclamation-triangle me-2"></i>
              Không có thông tin đặt chỗ để hiển thị.
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  // Calculate unpaid tickets
  const unpaidTickets = booking.tickets.filter(ticket => ticket.ticketStatus === 0);
  const paidTickets = booking.tickets.filter(ticket => ticket.ticketStatus === 1);

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col lg={8}>
          {/* Header */}
          <div className="text-center mb-4">
            <h1 className="mb-3">
              <i className="bi bi-credit-card me-2"></i>
              Thanh toán đặt chỗ
            </h1>
            <p className="text-muted">Hoàn tất thanh toán để xác nhận đặt chỗ của bạn</p>
          </div>

          {/* Payment Status Alert */}
          <Alert variant="warning" className="mb-4">
            <div className="d-flex align-items-center">
              <i className="bi bi-clock-history me-3" style={{ fontSize: '1.5rem' }}></i>
              <div>
                <h6 className="mb-1">Thanh toán đang chờ xử lý</h6>
                <small>
                  Có {unpaidTickets.length} vé cần thanh toán
                  {paidTickets.length > 0 && ` (${paidTickets.length} vé đã thanh toán)`}
                </small>
              </div>
            </div>
          </Alert>

          {/* Booking Details */}
          <Card className="mb-4">
            <Card.Header className="bg-primary text-white">
              <div className="d-flex justify-content-between align-items-center">
                <h4 className="mb-0">
                  <i className="bi bi-ticket-detailed me-2"></i>
                  Chi tiết đặt chỗ
                </h4>
                <Badge bg="light" text="dark" className="fs-6">
                  Mã: {booking.confirmationCode}
                </Badge>
              </div>
            </Card.Header>
            <Card.Body>
              {/* Flight Information */}
              <div className="mb-4">
                <h5 className="text-primary mb-3">
                  <i className="bi bi-airplane me-2"></i>
                  Thông tin chuyến bay
                </h5>
                <Row className="g-3">
                  <Col sm={6}>
                    <strong>Chuyến bay:</strong>
                    <div className="fs-5 text-primary">{booking.flightInfo.flightCode}</div>
                  </Col>
                  <Col sm={6}>
                    <strong>Tuyến đường:</strong>
                    <div className="fs-5">
                      <i className="bi bi-geo-alt me-1"></i>
                      {booking.flightInfo.departureCity} 
                      <i className="bi bi-arrow-right mx-2"></i>
                      {booking.flightInfo.arrivalCity}
                    </div>
                  </Col>
                  <Col sm={6}>
                    <strong>Khởi hành:</strong>
                    <div>
                      <i className="bi bi-calendar-event me-1"></i>
                      {formatDateTime(booking.flightInfo.departureTime)}
                    </div>
                  </Col>
                  {booking.flightInfo.arrivalTime && (
                    <Col sm={6}>
                      <strong>Đến:</strong>
                      <div>
                        <i className="bi bi-calendar-check me-1"></i>
                        {formatDateTime(booking.flightInfo.arrivalTime)}
                      </div>
                    </Col>
                  )}
                </Row>
              </div>

              {/* Passenger Information */}
              <div className="mb-4">
                <h5 className="text-primary mb-3">
                  <i className="bi bi-people me-2"></i>
                  Thông tin hành khách ({booking.tickets.length} hành khách)
                </h5>
                <ListGroup>
                  {booking.tickets.map((ticket, index) => {
                    const isUnpaid = ticket.ticketStatus === 0;
                    return (
                      <ListGroup.Item 
                        key={index} 
                        className={`d-flex justify-content-between align-items-center ${isUnpaid ? 'border-warning' : 'border-success'}`}
                      >
                        <div>
                          <strong>
                            <i className="bi bi-person me-1"></i>
                            Hành khách {index + 1}: {booking.passengers[index]}
                          </strong>
                          <div className="text-muted">
                            <i className="bi bi-geo-alt me-1"></i>
                            Ghế: {ticket.seatNumber}
                            <Badge 
                              bg={isUnpaid ? "warning" : "success"} 
                              className="ms-2"
                            >
                              {isUnpaid ? "Chờ thanh toán" : "Đã thanh toán"}
                            </Badge>
                          </div>
                        </div>
                        <div className="text-end">
                          <Badge bg={isUnpaid ? "primary" : "secondary"} className="fs-6">
                            {formatCurrency(ticket.fare || 0)}
                          </Badge>
                        </div>
                      </ListGroup.Item>
                    );
                  })}
                </ListGroup>
              </div>

              {/* Payment Summary */}
              <div className="border-top pt-4">
                <Row className="align-items-center">
                  <Col>
                    <h5 className="mb-0">
                      <i className="bi bi-credit-card me-2"></i>
                      Số tiền cần thanh toán:
                    </h5>
                    <small className="text-muted">
                      {unpaidTickets.length} vé chưa thanh toán
                      {paidTickets.length > 0 && ` • ${paidTickets.length} vé đã thanh toán`}
                    </small>
                  </Col>
                  <Col xs="auto">
                    <div className="text-end">
                      <div className="fs-3 fw-bold text-success">
                        {formatCurrency(booking.totalAmount)}
                      </div>
                      {paidTickets.length > 0 && (
                        <small className="text-muted">
                          Tổng ban đầu: {formatCurrency(booking.tickets.reduce((sum, ticket) => sum + (ticket.fare || 0), 0))}
                        </small>
                      )}
                    </div>
                  </Col>
                </Row>
              </div>
            </Card.Body>
          </Card>

          {/* Payment Actions */}
          <Card>
            <Card.Body className="text-center p-4">
              <h5 className="mb-3">
                <i className="bi bi-shield-check me-2"></i>
                Sẵn sàng thanh toán?
              </h5>
              <p className="text-muted mb-4">
                Bạn sẽ được chuyển hướng đến cổng thanh toán VNPay an toàn để hoàn tất giao dịch.
                <br />
                <small>Thời gian tạo: 2025-06-11 05:14:08 UTC</small>
              </p>
              
              <div className="d-flex gap-3 justify-content-center flex-wrap">
                <Button
                  variant="outline-secondary"
                  onClick={() => navigate('/booking-lookup')}
                >
                  <i className="bi bi-search me-2"></i>
                  Quản lý đặt chỗ
                </Button>
                
                <Button
                  variant="outline-primary"
                  onClick={() => navigate('/')}
                >
                  <i className="bi bi-house me-2"></i>
                  Trang chủ
                </Button>
                
                <Button
                  variant="success"
                  size="lg"
                  onClick={handlePayment}
                  disabled={processingPayment || unpaidTickets.length === 0}
                  className="px-4"
                >
                  {processingPayment ? (
                    <>
                      <Spinner animation="border" size="sm" className="me-2" />
                      Đang xử lý...
                    </>
                  ) : (
                    <>
                      <i className="bi bi-credit-card me-2"></i>
                      Thanh toán {formatCurrency(booking.totalAmount)}
                    </>
                  )}
                </Button>
              </div>

              <div className="mt-4">
                <small className="text-muted">
                  <i className="bi bi-shield-check me-1 text-success"></i>
                  Thanh toán được bảo mật 256-bit SSL bởi VNPay
                </small>
              </div>
            </Card.Body>
          </Card>

          {/* Important Notice */}
          <Alert variant="info" className="mt-4">
            <Alert.Heading className="fs-6">
              <i className="bi bi-info-circle-fill me-2"></i>
              Lưu ý quan trọng
            </Alert.Heading>
            <ul className="mb-0 small">
              <li>Vui lòng hoàn tất thanh toán trước thời gian khởi hành ít nhất 2 giờ</li>
              <li>Sau khi thanh toán thành công, bạn sẽ nhận được email xác nhận tự động</li>
              <li>Vé điện tử sẽ được gửi qua email sau khi thanh toán thành công</li>
              <li>Nếu gặp vấn đề, vui lòng liên hệ hotline: 1900-1234 hoặc support@thinhuit.id.vn</li>
              <li>Giao dịch có hiệu lực trong 15 phút kể từ khi tạo</li>
            </ul>
          </Alert>
        </Col>
      </Row>

      {/* Error Modal */}
      <Modal show={showErrorModal} onHide={() => setShowErrorModal(false)} centered>
        <Modal.Header closeButton className="bg-danger text-white">
          <Modal.Title>
            <i className="bi bi-exclamation-triangle me-2"></i>
            {modalTitle}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4 text-center">
          <div className="mb-3">
            <i className="bi bi-x-circle text-danger" style={{ fontSize: '3rem' }}></i>
          </div>
          <p className="mb-0">{modalMessage}</p>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="danger"
            onClick={() => setShowErrorModal(false)}
          >
            <i className="bi bi-x me-2"></i>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default PaymentHandler;