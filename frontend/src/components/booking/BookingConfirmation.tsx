import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Button, Alert, Badge, ListGroup } from 'react-bootstrap';
import { BookingConfirmation as BookingConfirmationType } from '../../services/bookingConfirmationService';
import { paymentService } from '../../services';

const BookingConfirmation: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  //const { user } = useAuth();

  const { confirmationCode, confirmationData, message } = location.state || {};

  if (!confirmationCode || !confirmationData) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Card>
              <Card.Body className="text-center py-5">
                <Alert variant="danger" className="mb-4">
                  No booking confirmation data found. Please check your booking details.
                </Alert>
                <Button onClick={() => navigate('/')} variant="primary">
                  Return to Home
                </Button>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  const booking: BookingConfirmationType = confirmationData;
  const isPaid = booking.tickets.every(ticket => ticket.ticketStatus === 1);

  const handlePrint = () => {
    const style = document.createElement('style');
    style.innerHTML = `
    @media print {
      /* Remove all margins and padding from page */
      @page {
        margin: 0 !important;
        size: A4;
      }
      
      /* Remove body margins */
      body {
        margin: 0 !important;
        padding: 0 !important;
      }
      
      /* Hide everything except print area */
      body * {
        visibility: hidden;
      }
      
      .print-area, .print-area * {
        visibility: visible;
      }
      
      .print-area {
        position: absolute;
        left: 0;
        top: 0;
        width: 100vw;
        margin: 0;
        padding: 15px; /* Only internal padding for content */
        box-sizing: border-box;
      }
      
      .no-print {
        display: none !important;
      }
      
      .print-header {
        display: block !important;
        text-align: center;
        margin-bottom: 20px;
        border-bottom: 2px solid #333;
        padding-bottom: 10px;
      }
      
      /* Remove Bootstrap container padding/margins */
      .container, .container-fluid {
        padding: 0 !important;
        margin: 0 !important;
        max-width: none !important;
        width: 100% !important;
      }
      
      /* Remove card margins */
      .card {
        margin: 0 0 15px 0 !important;
        box-shadow: none !important;
      }
    }
  `;

    document.head.appendChild(style);

    window.print();

    // Clean up
    setTimeout(() => {
      document.head.removeChild(style);

    }, 1000);
  };

  const handlePayment = async () => {
    navigate('/payment/' + booking.confirmationCode);
  };

  // const handleScoreUpdate = async () => {
  //   if (!user) return;

  //   try {
  //     // Assuming updateScore is a method in customerService to update the user's score
  //     await customerService.updateScore(user.id, booking.totalAmount);
  //     alert('Điểm thưởng của bạn đã được cập nhật!');
  //   } catch (error) {
  //     console.error('Failed to update score:', error);
  //     alert('Không thể cập nhật điểm thưởng. Vui lòng thử lại sau.');
  //   }
  // }

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col lg={8}>
          {/* Print Area - This will be the only thing printed */}
          <div className="print-area">
            {/* Success Header */}
            <Card className="mb-4 border-success">
              <Card.Body className="text-center py-4">
                <div className="display-1 text-success mb-3">✅</div>
                <h1 className="text-success mb-3">Đặt chỗ thành công!</h1>
                <div className="mb-3">
                  <Badge bg={isPaid ? "success" : "warning"} className="fs-5 px-3 py-2">
                    {isPaid ?
                      <><i className="bi bi-check-circle me-2"></i>Đã thanh toán</> :
                      <><i className="bi bi-clock-history me-2"></i>Chờ thanh toán</>
                    }
                  </Badge>
                </div>
                {message && (
                  <Alert variant="success" className="mb-0 no-print">
                    {message}
                  </Alert>
                )}
              </Card.Body>
            </Card>

            {/* Confirmation Code */}
            <Card className="mb-4 border-primary">
              <Card.Header className="bg-primary text-white">
                <h4 className="mb-0">Mã xác nhận của bạn</h4>
              </Card.Header>
              <Card.Body className="text-center print-section">
                <div className="bg-light p-4 rounded mb-3">
                  <h2 className="text-primary fw-bold mb-0 font-monospace">
                    {booking.confirmationCode}
                  </h2>
                </div>
                <Alert variant="warning" className="mb-0">
                  <strong>⚠️ Quan trọng:</strong> Vui lòng lưu mã xác nhận này.
                  Bạn sẽ cần nó để truy xuất hoặc quản lý đặt chỗ sau này.
                </Alert>
              </Card.Body>
            </Card>

            {/* Booking Details */}
            <Card className="mb-4">
              <Card.Header>
                <h4 className="mb-0">Chi tiết đặt chỗ</h4>
              </Card.Header>
              <Card.Body>
                {/* Flight Information */}
                <div className="mb-4 print-section">
                  <h5 className="text-primary mb-3">Thông tin chuyến bay</h5>
                  <div className="print-row">
                    <strong>Chuyến bay:</strong>
                    <span>{booking.flightInfo.flightCode}</span>
                  </div>
                  <div className="print-row">
                    <strong>Tuyến đường:</strong>
                    <span>{booking.flightInfo.departureCity} → {booking.flightInfo.arrivalCity}</span>
                  </div>
                  <div className="print-row">
                    <strong>Khởi hành:</strong>
                    <span>
                      {new Date(booking.flightInfo.departureTime).toLocaleDateString()} lúc{' '}
                      {new Date(booking.flightInfo.departureTime).toLocaleTimeString()}
                    </span>
                  </div>
                  {booking.flightInfo.arrivalTime && (
                    <div className="print-row">
                      <strong>Đến:</strong>
                      <span>
                        {new Date(booking.flightInfo.arrivalTime).toLocaleDateString()} lúc{' '}
                        {new Date(booking.flightInfo.arrivalTime).toLocaleTimeString()}
                      </span>
                    </div>
                  )}
                </div>

                {/* Passenger Information */}
                <div className="mb-4 print-section">
                  <h5 className="text-primary mb-3">Thông tin hành khách</h5>
                  <ListGroup>
                    {booking.tickets.map((ticket, index) => (
                      <ListGroup.Item key={index} className="d-flex justify-content-between align-items-center">
                        <div>
                          <strong>
                            Hành khách {index + 1}: {booking.passengers && booking.passengers[index]}
                          </strong>
                          <div className="text-muted">
                            Ghế: {ticket.seatNumber}{' '}
                            {ticket.ticketStatus !== undefined && (
                              <Badge bg={ticket.ticketStatus === 1 ? "success" : "warning"} className="ms-2">
                                {ticket.ticketStatus === 1 ? "Đã thanh toán" : "Chờ thanh toán"}
                              </Badge>
                            )}
                          </div>
                        </div>
                        <Badge bg="primary" className="fs-6">${ticket.fare}</Badge>
                      </ListGroup.Item>
                    ))}
                  </ListGroup>
                </div>

                {/* Booking Summary */}
                <div className="mb-0 print-section">
                  <h5 className="text-primary mb-3">Tóm tắt đặt chỗ</h5>
                  <div className="print-row">
                    <strong>Ngày đặt:</strong>
                    <span>{new Date(booking.bookingDate).toLocaleDateString()}</span>
                  </div>
                  <div className="print-row">
                    <strong>Tổng hành khách:</strong>
                    <span>{booking.tickets.length}</span>
                  </div>
                  <div className="print-row">
                    <strong>Trạng thái thanh toán:</strong>
                    <span>
                      {isPaid ? "Đã thanh toán" : "Chờ thanh toán"}
                    </span>
                  </div>
                  <div className="print-total print-row">
                    <strong>Tổng tiền:</strong>
                    <strong>${booking.totalAmount}</strong>
                  </div>
                </div>
              </Card.Body>
            </Card>

          </div>

          {/* Action Buttons - Hidden during print */}
          <Card className="mb-4 no-print">
            <Card.Body>
              <Row className="g-3">
                <Col xs={12} lg={isPaid ? 4 : 3}>
                  <Button
                    onClick={handlePrint}
                    variant="outline-secondary"
                    className="w-100 mb-2"
                  >
                    <i className="bi bi-printer me-2"></i>
                    In phiếu đặt chỗ
                  </Button>
                </Col>
                <Col xs={12} lg={isPaid ? 4 : 3}>
                  <Button
                    onClick={() => navigate('/booking-lookup')}
                    variant="primary"
                    className="w-100 mb-2"
                  >
                    Quản lý đặt chỗ
                  </Button>
                </Col>
                <Col xs={12} lg={isPaid ? 4 : 3}>
                  <Button
                    onClick={() => navigate('/')}
                    variant="primary"
                    className="w-100 mb-2"
                  >
                    Đặt vé khác
                  </Button>
                </Col>
                {!isPaid && (
                  <Col xs={12} lg={3}>
                    <Button
                      onClick={() => handlePayment()}
                      variant="success"
                      className="w-100 mb-2"
                    >
                      <i className="bi bi-credit-card me-2"></i>
                      Thanh toán
                    </Button>
                  </Col>
                )}
              </Row>
            </Card.Body>
          </Card>

          {/* Next Steps - Hidden during print */}
          <Card className="bg-light no-print">
            <Card.Header>
              <h4 className="mb-0">Tiếp theo là gì?</h4>
            </Card.Header>
            <Card.Body>
              <ul className="mb-0">
                <li>Lưu mã xác nhận của bạn: <strong className="text-primary">{booking.confirmationCode}</strong></li>
                <li>Có mặt tại sân bay ít nhất 2 tiếng trước giờ khởi hành</li>
                <li>Mang theo giấy tờ tùy thân hợp lệ và mã xác nhận</li>
                <li>Bạn có thể quản lý đặt chỗ bằng cách sử dụng nút "Quản lý đặt chỗ" ở trên</li>
              </ul>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default BookingConfirmation;