import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Row,
  Col,
  Card,
  Form,
  Button,
  Alert,
  Spinner,
  Badge,
  ListGroup,
  Modal,
} from "react-bootstrap";
import { BookingConfirmation } from "../../services/bookingConfirmationService";
import {
  ticketService,
  flightService,
  passengerService,
  flightTicketClassService,
} from "../../services";

const BookingLookup: React.FC = () => {
  const navigate = useNavigate();
  const [searchData, setSearchData] = useState({
    confirmationCode: "",
  });
  const [booking, setBooking] = useState<BookingConfirmation | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [isPaid, setIsPaid] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [modalMessage, setModalMessage] = useState("");
  const [modalTitle, setModalTitle] = useState("");

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!searchData.confirmationCode.trim()) {
      setError("Vui lòng nhập mã xác nhận");
      return;
    }

    setLoading(true);
    setError("");
    setBooking(null);

    try {
      const tickets = await ticketService.getTicketsOnConfirmationCode(
        searchData.confirmationCode
      );

      if (!tickets || tickets.data.length === 0) {
        setError("Không tìm thấy đặt chỗ với mã xác nhận này");
        return;
      }

      const firstTicket = tickets.data[0];
      const flight = await flightService.getFlightById(firstTicket.flightId!);

      const passengerNames = await Promise.all(
        tickets.data.map(async (ticket) => {
          try {
            const passenger = await passengerService.getPassengerById(
              ticket.passengerId!
            );
            return passenger.data.passengerName;
          } catch (error) {
            console.error(
              `Error getting passenger ${ticket.passengerId}:`,
              error
            );
            return `Passenger ${ticket.passengerId}`;
          }
        })
      );

      setIsPaid(tickets.data.every((ticket) => ticket.ticketStatus === 1));

      const bookingData: BookingConfirmation = {
        confirmationCode: searchData.confirmationCode,
        bookingDate: new Date().toISOString(),
        tickets: tickets.data,
        passengers: passengerNames,
        totalAmount: tickets.data.reduce(
          (sum, ticket) => sum + (ticket.fare || 0),
          0
        ),
        flightInfo: {
          flightCode: flight.data.flightCode || "",
          departureTime: flight.data.departureTime || "",
          arrivalTime: flight.data.arrivalTime || "",
          departureCity: flight.data.departureCityName || "",
          arrivalCity: flight.data.arrivalCityName || "",
        },
      };

      setBooking(bookingData);
    } catch (err: any) {
      console.error("Error looking up booking:", err);
      setError(
        "Không thể tìm thấy đặt chỗ. Vui lòng kiểm tra mã xác nhận và thử lại."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async () => {
    setShowCancelModal(true);
  };

  const confirmCancelBooking = async () => {
    if (!booking) return;

    setShowCancelModal(false);

    try {
      for (const ticket of booking.tickets) {
        if (ticket.ticketId) {
          await ticketService.deleteTicket(ticket.ticketId);
          await flightTicketClassService.updateRemainingTickets(
            ticket.flightId!,
            ticket.ticketClassId!,
            -1
          );
        }
      }

      setModalTitle("Thành công");
      setModalMessage("Đặt chỗ và tất cả vé đã được hủy thành công.");
      setShowSuccessModal(true);
      setBooking(null);
      setSearchData({ confirmationCode: "" });
    } catch (err: any) {
      console.error("Error canceling booking:", err);
      setModalTitle("Lỗi");
      setModalMessage(
        "Không thể hủy đặt chỗ: " + (err.message || "Lỗi không xác định")
      );
      setShowErrorModal(true);
    }
  };

  const handlePrintBooking = () => {
    const style = document.createElement("style");
    style.innerHTML = `
      @media print {
        /* Remove all margins and padding from page */
        @page {
          margin: 0 !important;
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
          padding: 15px;
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
        
        /* Print styling for badges */
        .badge {
          background-color: #333 !important;
          color: white !important;
          border: 1px solid #333 !important;
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
        }
        
        .badge.bg-success {
          background-color: #28a745 !important;
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
        }
        
        .badge.bg-warning {
          background-color: #ffc107 !important;
          color: #000 !important;
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
        }
        
        .badge.bg-primary {
          background-color: #007bff !important;
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
        }
        
        /* Print styling for cards */
        .card-header {
          background-color: #f8f9fa !important;
          border-bottom: 1px solid #333 !important;
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
        }
        
        .card-header.bg-success {
          background-color: #28a745 !important;
          color: white !important;
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
        }
        
        .card-header.bg-warning {
          background-color: #ffc107 !important;
          color: #000 !important;
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
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
    if (!booking) return;

    navigate("/payment/" + booking.confirmationCode);
  };

  const handleCloseCancelModal = () => {
    setShowCancelModal(false);
  };

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col lg={8}>
          {/* Header */}
          <div className="text-center mb-5 no-print">
            <h1 className="mb-3">Quản lý đặt chỗ của bạn</h1>
            <p className="text-muted">
              Nhập mã xác nhận đặt chỗ để xem và quản lý việc đặt chỗ của bạn
            </p>
          </div>

          {/* Search Form */}
          <Card className="mb-4 no-print">
            <Card.Header>
              <h4 className="mb-0">Tìm đặt chỗ của bạn</h4>
            </Card.Header>
            <Card.Body>
              <Form onSubmit={handleSearch}>
                <Row>
                  <Col md={12}>
                    <Form.Group className="mb-3">
                      <Form.Label className="w-100 text-center fw-bold fs-4">
                        Mã xác nhận *
                      </Form.Label>
                      <Form.Control
                        type="text"
                        value={searchData.confirmationCode}
                        onChange={(e) =>
                          setSearchData((prev) => ({
                            ...prev,
                            confirmationCode: e.target.value.toUpperCase(),
                          }))
                        }
                        placeholder="FMS-YYYYMMDD-XXXX"
                        required
                      />
                      <Form.Text className="text-muted">
                        Định dạng: FMS-YYYYMMDD-XXXX (ví dụ: FMS-20240527-A1B2)
                      </Form.Text>
                    </Form.Group>
                  </Col>
                </Row>

                {error && (
                  <Alert variant="danger" className="mb-3">
                    {error}
                  </Alert>
                )}

                <div className="d-flex gap-3">
                  <Button type="submit" variant="primary" disabled={loading}>
                    {loading ? (
                      <>
                        <Spinner
                          animation="border"
                          size="sm"
                          className="me-2"
                        />
                        Đang tìm kiếm...
                      </>
                    ) : (
                      "Tìm kiếm đặt chỗ"
                    )}
                  </Button>
                  <Button
                    variant="outline-secondary"
                    onClick={() => navigate("/")}
                  >
                    Về trang chủ
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>

          {/* Booking Details - This is the print area */}
          {booking && (
            <div className="print-area">
              <Card className="mb-4">
                <Card.Header
                  className={isPaid ? "bg-success text-white" : "bg-warning"}
                >
                  <div className="d-flex justify-content-between align-items-center">
                    <h4 className="mb-0">Đã tìm thấy đặt chỗ</h4>
                    <div className="d-flex gap-2 align-items-center">
                      <Badge
                        bg={isPaid ? "success" : "warning"}
                        className="fs-6 py-2 px-3"
                      >
                        {isPaid ? (
                          <>
                            <i className="bi bi-check-circle me-2"></i>
                            Đã thanh toán
                          </>
                        ) : (
                          <>
                            <i className="bi bi-hourglass-split me-2"></i>
                            Chờ thanh toán
                          </>
                        )}
                      </Badge>
                      <Badge bg="light" text="dark" className="fs-6">
                        Mã: {booking.confirmationCode}
                      </Badge>
                    </div>
                  </div>
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
                        <strong>Tuyến bay:</strong>
                        <div>
                          {booking.flightInfo.departureCity} →{" "}
                          {booking.flightInfo.arrivalCity}
                        </div>
                      </Col>
                      <Col sm={6}>
                        <strong>Khởi hành:</strong>
                        <div>
                          {new Date(
                            booking.flightInfo.departureTime
                          ).toLocaleString("vi-VN")}
                        </div>
                      </Col>
                      {booking.flightInfo.arrivalTime && (
                        <Col sm={6}>
                          <strong>Đến:</strong>
                          <div>
                            {new Date(
                              booking.flightInfo.arrivalTime
                            ).toLocaleString("vi-VN")}
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
                        <ListGroup.Item
                          key={index}
                          className="d-flex justify-content-between align-items-center"
                        >
                          <div>
                            <strong>
                              Hành khách {index + 1}:{" "}
                              {booking.passengers[index]}
                            </strong>
                            <div className="text-muted d-flex align-items-center">
                              Ghế: {ticket.seatNumber}
                              <Badge
                                bg={
                                  ticket.ticketStatus === 1
                                    ? "success"
                                    : "warning"
                                }
                                className="ms-2"
                              >
                                {ticket.ticketStatus === 1
                                  ? "Đã thanh toán"
                                  : "Chờ thanh toán"}
                              </Badge>
                            </div>
                          </div>
                          <Badge bg="primary" className="fs-6">
                            {ticket.fare?.toLocaleString("vi-VN")} VND
                          </Badge>
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
                        <div>
                          {new Date(booking.bookingDate).toLocaleDateString(
                            "vi-VN"
                          )}
                        </div>
                      </Col>
                      <Col sm={6}>
                        <strong>Tổng số hành khách:</strong>
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
                      {!isPaid && (
                        <Col sm={6}>
                          <strong>Cần thanh toán trước:</strong>
                          <div>
                            <Badge bg="danger">
                              {new Date(
                                booking.flightInfo.departureTime
                              ).toLocaleDateString("vi-VN")}
                            </Badge>
                          </div>
                        </Col>
                      )}
                      <Col xs={12}>
                        <div className="border-top pt-3">
                          <Row>
                            <Col>
                              <strong className="fs-5">Tổng số tiền:</strong>
                            </Col>
                            <Col className="text-end">
                              <strong className="fs-4 text-primary">
                                {booking.totalAmount.toLocaleString("vi-VN")}{" "}
                                VND
                              </strong>
                            </Col>
                          </Row>
                        </div>
                      </Col>
                    </Row>
                  </div>
                </Card.Body>

                {/* Booking Actions - Hidden during print */}
                <Card.Footer className="bg-light no-print">
                  <Row className="g-2">
                    <Col xs={12} md={isPaid ? 6 : 4}>
                      <Button
                        onClick={handlePrintBooking}
                        variant="outline-secondary"
                        className="w-100 mb-2"
                      >
                        <i className="bi bi-printer me-2"></i>
                        In đặt chỗ
                      </Button>
                    </Col>
                    {!isPaid && (
                      <Col xs={12} md={4}>
                        <Button
                          onClick={handlePayment}
                          variant="success"
                          className="w-100 mb-2"
                        >
                          <i className="bi bi-credit-card me-2"></i>
                          Thanh toán ngay
                        </Button>
                      </Col>
                    )}
                    <Col xs={12} md={isPaid ? 6 : 4}>
                      <Button
                        variant="outline-primary"
                        onClick={() => navigate("/")}
                        className="w-100 mb-2"
                      >
                        <i className="bi bi-house me-2"></i>
                        Trang chủ
                      </Button>
                    </Col>
                    {/* {isPaid && (
                      <Col xs={12} md={4}>
                        <Button
                          onClick={handleCancelBooking}
                          variant="danger"
                          className="w-100 mb-2"
                        >
                          <i className="bi bi-x-circle me-2"></i>
                          Hủy đặt chỗ
                        </Button>
                      </Col>
                    )} */}
                  </Row>

                  {!isPaid && (
                    <Alert variant="info" className="mb-0 mt-3">
                      <i className="bi bi-info-circle-fill me-2"></i>
                      <strong>Quan trọng:</strong> Đặt chỗ này cần thanh toán để
                      được xác nhận. Các đặt chỗ chưa thanh toán có thể bị hủy
                      tự động 24 giờ trước khi khởi hành.
                    </Alert>
                  )}
                </Card.Footer>
              </Card>
            </div>
          )}

          {/* Help Section */}
          <Card className="bg-light no-print">
            <Card.Header>
              <h5 className="mb-0">Cần trợ giúp?</h5>
            </Card.Header>
            <Card.Body>
              <ul className="mb-0">
                <li>Đảm bảo bạn nhập mã xác nhận chính xác như đã hiển thị</li>
                <li>Định dạng mã xác nhận là: FMS-YYYYMMDD-XXXX</li>
                <li>
                  Nếu không tìm thấy đặt chỗ của bạn, hãy liên hệ dịch vụ khách
                  hàng
                </li>
                <li>
                  Các đặt chỗ khách được lưu trữ cục bộ cho tối đa 10 đặt chỗ
                  gần đây
                </li>
              </ul>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Cancel Confirmation Modal */}
      <Modal
        show={showCancelModal}
        onHide={handleCloseCancelModal}
        centered
        className="no-print"
      >
        <Modal.Header closeButton className="bg-danger text-white">
          <Modal.Title>
            <i className="bi bi-exclamation-triangle me-2"></i>
            Hủy đặt chỗ
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          <div className="text-center mb-3">
            <i
              className="bi bi-exclamation-circle text-danger"
              style={{ fontSize: "3rem" }}
            ></i>
          </div>
          <h5 className="text-center mb-3">
            Bạn có chắc chắn muốn hủy đặt chỗ này không?
          </h5>
          <p className="text-center text-muted mb-0">
            Hành động này không thể hoàn tác. Tất cả vé cho đặt chỗ này sẽ bị
            hủy vĩnh viễn.
          </p>
          {booking && (
            <div className="mt-3 p-3 bg-light rounded">
              <div className="text-center">
                <strong>Đặt chỗ: {booking.confirmationCode}</strong>
                <br />
                <span className="text-muted">
                  {booking.flightInfo.flightCode} - {booking.tickets.length}{" "}
                  hành khách
                </span>
                <br />
                <span className="text-primary fw-bold">
                  {booking.totalAmount.toLocaleString("vi-VN")} VND
                </span>
              </div>
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseCancelModal}>
            Giữ đặt chỗ
          </Button>
          <Button variant="danger" onClick={confirmCancelBooking}>
            <i className="bi bi-trash me-2"></i>
            Có, hủy đặt chỗ
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Success Modal */}
      <Modal
        show={showSuccessModal}
        onHide={() => setShowSuccessModal(false)}
        centered
      >
        <Modal.Header closeButton className="bg-success text-white">
          <Modal.Title>
            <i className="bi bi-check-circle me-2"></i>
            {modalTitle}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4 text-center">
          <div className="mb-3">
            <i
              className="bi bi-check-circle text-success"
              style={{ fontSize: "3rem" }}
            ></i>
          </div>
          <p className="mb-0">{modalMessage}</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="success" onClick={() => setShowSuccessModal(false)}>
            <i className="bi bi-check me-2"></i>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Error Modal */}
      <Modal
        show={showErrorModal}
        onHide={() => setShowErrorModal(false)}
        centered
      >
        <Modal.Header closeButton className="bg-danger text-white">
          <Modal.Title>
            <i className="bi bi-exclamation-triangle me-2"></i>
            {modalTitle}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4 text-center">
          <div className="mb-3">
            <i
              className="bi bi-x-circle text-danger"
              style={{ fontSize: "3rem" }}
            ></i>
          </div>
          <p className="mb-0">{modalMessage}</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="danger" onClick={() => setShowErrorModal(false)}>
            <i className="bi bi-x me-2"></i>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default BookingLookup;
