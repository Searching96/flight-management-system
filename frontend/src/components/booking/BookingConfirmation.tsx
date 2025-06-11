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
  const isPaid = booking.tickets.every(ticket => ticket.ticketStatus === 1); // Check if booking status is paid (1)

  const handlePrint = () => {
    window.print();
  };

  const handlePayment = async () => {
    try {
      const response = await paymentService.createPayment(booking.confirmationCode);
      // Use window.location.href for a full page redirect to the payment URL
      if (response && response.data) {
        console.log('Redirecting to payment URL:', response.data);
        window.location.href = response.data;
      } else {
        alert('Invalid payment URL received. Please try again.');
      }
    } catch (error) {
      console.error('Payment creation failed:', error);
      alert('Failed to create payment. Please try again later.');
    }
  }

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
                <Alert variant="success" className="mb-0">
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
            <Card.Body className="text-center">
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
              <div className="mb-4">
                <h5 className="text-primary mb-3">Thông tin chuyến bay</h5>
                <Row className="g-3">
                  <Col sm={6}>
                    <strong>Chuyến bay:</strong>
                    <div>{booking.flightInfo.flightCode}</div>
                  </Col>
                  <Col sm={6}>
                    <strong>Tuyến đường:</strong>
                    <div>{booking.flightInfo.departureCity} → {booking.flightInfo.arrivalCity}</div>
                  </Col>
                  <Col sm={6}>
                    <strong>Khởi hành:</strong>
                    <div>
                      {new Date(booking.flightInfo.departureTime).toLocaleDateString()} lúc{' '}
                      {new Date(booking.flightInfo.departureTime).toLocaleTimeString()}
                    </div>
                  </Col>
                  {booking.flightInfo.arrivalTime && (
                    <Col sm={6}>
                      <strong>Đến:</strong>
                      <div>
                        {new Date(booking.flightInfo.arrivalTime).toLocaleDateString()} lúc{' '}
                        {new Date(booking.flightInfo.arrivalTime).toLocaleTimeString()}
                      </div>
                    </Col>
                  )}
                </Row>
              </div>

              {/* Passenger Information */}
              <div className="mb-4">
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
              <div className="mb-0">
                <h5 className="text-primary mb-3">Tóm tắt đặt chỗ</h5>
                <Row className="g-3">
                  <Col sm={6}>
                    <strong>Ngày đặt:</strong>
                    <div>{new Date(booking.bookingDate).toLocaleDateString()}</div>
                  </Col>
                  <Col sm={6}>
                    <strong>Tổng hành khách:</strong>
                    <div>{booking.tickets.length}</div>
                  </Col>
                  <Col sm={6}>
                    <strong>Trạng thái thanh toán:</strong>
                    <div>
                      <Badge bg={isPaid ? "success" : "warning"}>
                        {isPaid ? "Đã thanh toán" : "Chờ thanh toán"}
                      </Badge>
                    </div>
                  </Col>
                  <Col xs={12}>
                    <div className="border-top pt-3">
                      <Row>
                        <Col>
                          <strong className="fs-5">Tổng tiền:</strong>
                        </Col>
                        <Col className="text-end">
                          <strong className="fs-4 text-primary">${booking.totalAmount}</strong>
                        </Col>
                      </Row>
                    </div>
                  </Col>
                </Row>
              </div>
            </Card.Body>
          </Card>

          {/* Action Buttons */}
          <Card className="mb-4">
            <Card.Body>
              <Row className="g-3">
                <Col xs={12} lg={isPaid ? 4 : 3}>
                  <Button
                    onClick={handlePrint}
                    variant="outline-secondary"
                    className="w-100 mb-2"
                  >
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

          {/* Next Steps */}
          <Card className="bg-light">
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
