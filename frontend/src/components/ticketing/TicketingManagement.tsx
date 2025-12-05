import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Table,
  Badge,
  Button,
  Form,
  InputGroup,
  Spinner,
  Alert,
  Modal,
} from "react-bootstrap";
import {
  ticketService,
  flightService,
  passengerService,
  flightTicketClassService,
} from "../../services";

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
  ticketStatus: "PAID" | "UNPAID";
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
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState<"ALL" | "PAID" | "UNPAID">(
    "ALL"
  );
  const [dateFilter, setDateFilter] = useState<
    "ALL" | "UPCOMING" | "TODAY" | "PAST"
  >("ALL");
  const [selectedTicket, setSelectedTicket] = useState<TicketInfo | null>(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [cancelLoading, setCancelLoading] = useState(false);
  const [showCashPaymentModal, setShowCashPaymentModal] = useState(false);
  const [cashPaymentLoading, setCashPaymentLoading] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    fetchAllTickets();
  }, []);

  useEffect(() => {
    filterTickets();
  }, [tickets, searchTerm, statusFilter, dateFilter]);

  const fetchAllTickets = async () => {
    try {
      setLoading(true);
      setError(null);

      // Get all tickets
      const allTickets = await ticketService.getAllTickets();

      console.log("Fetched tickets:", allTickets);

      // Process each ticket to get complete information
      const ticketInfoPromises = allTickets.map(async (ticket: any) => {
        try {
          // Validate ticket object
          if (
            !ticket ||
            !ticket.ticketId ||
            !ticket.flightId ||
            !ticket.passengerId
          ) {
            console.warn("Invalid ticket object:", ticket);
            return null;
          }

          // Get flight information
          const flight = await flightService.getFlightById(ticket.flightId);

          // Get passenger information
          const passenger = await passengerService.getPassengerById(
            ticket.passengerId
          );

          // Validate flight and passenger data
          if (!flight || !passenger) {
            console.warn(
              "Missing flight or passenger data for ticket:",
              ticket.ticketId
            );
            return null;
          }

          // Determine payment status based on paymentTime or other criteria
          const ticketStatus: "PAID" | "UNPAID" = ticket.paymentTime
            ? "PAID"
            : "UNPAID";

          // Determine ticket class based on seat number prefix
          const getTicketClassFromSeat = (seatNumber: string) => {
            if (!seatNumber) return "Economy";
            const firstChar = seatNumber.charAt(0).toUpperCase();
            switch (firstChar) {
              case "A":
                return "Economy";
              case "B":
                return "Business";
              case "C":
                return "First Class";
              default:
                return "Economy";
            }
          };

          const seatNumber = ticket.seatNumber || "A1";
          const ticketClassName = getTicketClassFromSeat(seatNumber);

          return {
            ticketId: ticket.ticketId,
            flightCode: flight.flightCode || "N/A",
            departureTime: flight.departureTime,
            arrivalTime: flight.arrivalTime,
            departureAirport: flight.departureAirportName || "N/A",
            arrivalAirport: flight.arrivalAirportName || "N/A",
            passengerCitizenId: passenger.citizenId || "N/A",
            passengerName: passenger.passengerName || "N/A",
            phoneNumber: passenger.phoneNumber || "N/A",
            paymentTime: ticket.paymentTime,
            ticketStatus,
            fare: ticket.fare || 0,
            seatNumber: seatNumber,
            confirmationCode: ticket.confirmationCode || "N/A",
            ticketClassName: ticketClassName,
          } as TicketInfo;
        } catch (err) {
          console.error(`Error processing ticket ${ticket?.ticketId}:`, err);
          return null;
        }
      });

      const processedTickets = await Promise.all(ticketInfoPromises);
      const validTickets = processedTickets.filter(
        (ticket): ticket is TicketInfo => ticket !== null
      );

      console.log("Processed tickets:", validTickets);
      setTickets(validTickets);
    } catch (err: any) {
      console.error("Error fetching tickets:", err);
      setError(`Failed to load tickets: ${err.message || "Unknown error"}`);
      setTickets([]); // Set empty array on error
    } finally {
      setLoading(false);
    }
  };

  const filterTickets = () => {
    let filtered = tickets;

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(
        (ticket) =>
          ticket.flightCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
          ticket.passengerName
            .toLowerCase()
            .includes(searchTerm.toLowerCase()) ||
          ticket.passengerCitizenId
            .toLowerCase()
            .includes(searchTerm.toLowerCase()) ||
          ticket.confirmationCode
            .toLowerCase()
            .includes(searchTerm.toLowerCase()) ||
          ticket.phoneNumber.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Filter by payment status
    if (statusFilter !== "ALL") {
      filtered = filtered.filter(
        (ticket) => ticket.ticketStatus === statusFilter
      );
    }

    // Filter by flight date
    if (dateFilter !== "ALL") {
      const now = new Date();
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      const tomorrow = new Date(today);
      tomorrow.setDate(tomorrow.getDate() + 1);

      filtered = filtered.filter((ticket) => {
        const flightDate = new Date(ticket.departureTime);
        const flightDateOnly = new Date(
          flightDate.getFullYear(),
          flightDate.getMonth(),
          flightDate.getDate()
        );

        switch (dateFilter) {
          case "UPCOMING":
            return flightDateOnly >= tomorrow;
          case "TODAY":
            return flightDateOnly.getTime() === today.getTime();
          case "PAST":
            return flightDateOnly < today;
          default:
            return true;
        }
      });
    }

    setFilteredTickets(filtered);
  };

  const formatDateTime = (dateString: string) => {
    return new Date(dateString).toLocaleString("vi-VN", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  const handleShowDetails = (ticket: TicketInfo) => {
    setSelectedTicket(ticket);
    setShowDetailsModal(true);
  };

  const getStatusBadge = (status: "PAID" | "UNPAID") => {
    return (
      <Badge bg={status === "PAID" ? "success" : "warning"}>
        {status === "PAID" ? "Đã thanh toán" : "Chưa thanh toán"}
      </Badge>
    );
  };

  const getSeatBadgeColor = (seatNumber: string) => {
    if (!seatNumber) return "#6c757d"; // default gray
    const firstChar = seatNumber.charAt(0).toUpperCase();
    switch (firstChar) {
      case "A":
        return "#3498db"; // blue
      case "B":
        return "#f39c12"; // orange
      case "C":
        return "#e74c3c"; // red
      default:
        return "#6c757d"; // default gray
    }
  };

  const getDateStatusBadge = (departureTime: string) => {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const flightDate = new Date(departureTime);
    const flightDateOnly = new Date(
      flightDate.getFullYear(),
      flightDate.getMonth(),
      flightDate.getDate()
    );

    if (flightDateOnly.getTime() === today.getTime()) {
      return (
        <Badge bg="info" className="ms-1">
          Hôm nay
        </Badge>
      );
    } else if (flightDateOnly < today) {
      return (
        <Badge bg="secondary" className="ms-1">
          Đã bay
        </Badge>
      );
    } else {
      const diffTime = flightDateOnly.getTime() - today.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      if (diffDays === 1) {
        return (
          <Badge bg="warning" className="ms-1">
            Ngày mai
          </Badge>
        );
      } else if (diffDays <= 7) {
        return (
          <Badge bg="primary" className="ms-1">
            {diffDays} ngày nữa
          </Badge>
        );
      }
    }
    return null;
  };

  const handleCancelTicket = () => {
    setShowCancelModal(true);
  };

  const confirmCancelTicket = async () => {
    if (!selectedTicket) return;

    setCancelLoading(true);
    try {
      // Delete the ticket
      const ticketData = await ticketService.getTicketById(
        selectedTicket.ticketId
      );
      if (
        ticketData &&
        ticketData.ticketClassId &&
        ticketData.flightId
      ) {
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

      // Show success message
      setSuccessMessage("Hủy vé thành công");
      setShowSuccessModal(true);
    } catch (err: any) {
      console.error("Error canceling ticket:", err);
      setErrorMessage(
        "Không thể hủy vé: " + (err.message || "Lỗi không xác định")
      );
      setShowErrorModal(true);
    } finally {
      setCancelLoading(false);
    }
  };

  const handleCashPayment = () => {
    setShowCashPaymentModal(true);
    setShowDetailsModal(false);
  };

  const confirmCashPayment = async () => {
    if (!selectedTicket) return;

    setCashPaymentLoading(true);
    try {
      // Update ticket payment status
      const ticketData = await ticketService.payTicket(selectedTicket.ticketId);
      if (ticketData) {
        // Update ticket with payment information
        const updatedTicket = {
          ...ticketData,
          paymentTime: new Date().toISOString(),
          ticketStatus: 1, // 1 for PAID
        };

        await ticketService.updateTicket(
          selectedTicket.ticketId,
          updatedTicket
        );
        await fetchAllTickets();
      }

      // Close modals
      setShowCashPaymentModal(false);
      setShowDetailsModal(false);
      setSelectedTicket(null);

      // Show success message
      setSuccessMessage(
        "Thanh toán bằng tiền mặt đã được ghi nhận thành công!"
      );
      setShowSuccessModal(true);
    } catch (err: any) {
      console.error("Error processing cash payment:", err);
      setErrorMessage(
        "Không thể xử lý thanh toán: " + (err.message || "Lỗi không xác định")
      );
      setShowErrorModal(true);
    } finally {
      setCashPaymentLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Card>
              <Card.Body className="text-center py-5">
                <Spinner
                  animation="border"
                  variant="primary"
                  className="mb-3"
                />
                <p className="mb-0">Đang tải vé...</p>
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
                  Quản lý vé máy bay
                </h4>
                <Badge bg="light" text="dark" className="fs-6">
                  {filteredTickets.length} vé
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
                        placeholder="Tìm kiếm theo mã chuyến bay, tên hành khách, CCCD, hoặc mã xác nhận..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                      />
                    </InputGroup>
                  </Col>
                  <Col md={2}>
                    <Form.Select
                      value={statusFilter}
                      onChange={(e) =>
                        setStatusFilter(
                          e.target.value as "ALL" | "PAID" | "UNPAID"
                        )
                      }
                    >
                      <option value="ALL">Tất cả trạng thái</option>
                      <option value="PAID">Đã thanh toán</option>
                      <option value="UNPAID">Chưa thanh toán</option>
                    </Form.Select>
                  </Col>
                  <Col md={2}>
                    <Form.Select
                      value={dateFilter}
                      onChange={(e) =>
                        setDateFilter(
                          e.target.value as
                            | "ALL"
                            | "UPCOMING"
                            | "TODAY"
                            | "PAST"
                        )
                      }
                    >
                      <option value="ALL">Tất cả thời gian</option>
                      <option value="UPCOMING">Chuyến bay sắp tới</option>
                      <option value="TODAY">Chuyến bay hôm nay</option>
                      <option value="PAST">Chuyến bay đã qua</option>
                    </Form.Select>
                  </Col>
                  <Col md={2}>
                    <Button
                      variant="outline-primary"
                      onClick={fetchAllTickets}
                      className="w-100"
                    >
                      <i className="bi bi-arrow-clockwise me-1"></i>
                      Làm mới
                    </Button>
                  </Col>
                </Row>
              </div>

              {/* Tickets Table */}
              <div className="table-responsive">
                <Table hover className="mb-0">
                  <thead className="table-dark">
                    <tr>
                      <th>Mã chuyến bay</th>
                      <th>Tuyến đường</th>
                      <th>Khởi hành</th>
                      <th>Hành khách</th>
                      <th>Liên hệ</th>
                      <th>Trạng thái</th>
                      <th>Giá vé</th>
                      <th>Ghế</th>
                      <th>Thao tác</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredTickets.length === 0 ? (
                      <tr>
                        <td colSpan={9} className="text-center py-4 text-muted">
                          <i className="bi bi-inbox display-6 d-block mb-2"></i>
                          Không tìm thấy vé nào
                        </td>
                      </tr>
                    ) : (
                      filteredTickets.map((ticket) => (
                        <tr key={ticket.ticketId}>
                          <td>
                            <strong className="text-primary">
                              {ticket.flightCode}
                            </strong>
                            <br />
                            <small className="text-muted">
                              {ticket.ticketClassName}
                            </small>
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
                              <strong>Dep:</strong>{" "}
                              {formatDateTime(ticket.departureTime)}
                              {getDateStatusBadge(ticket.departureTime)}
                              <br />
                              <strong>Arr:</strong>{" "}
                              {formatDateTime(ticket.arrivalTime)}
                            </div>
                          </td>
                          <td>
                            <div>
                              <strong>{ticket.passengerName}</strong>
                              <br />
                              <small className="text-muted">
                                ID: {ticket.passengerCitizenId}
                              </small>
                            </div>
                          </td>
                          <td>
                            <small>{ticket.phoneNumber || "N/A"}</small>
                          </td>
                          <td>
                            {getStatusBadge(ticket.ticketStatus)}
                            {ticket.paymentTime && (
                              <div className="small text-muted mt-1">
                                Đã thanh toán:{" "}
                                {formatDateTime(ticket.paymentTime)}
                              </div>
                            )}
                          </td>
                          <td>
                            <strong>{formatCurrency(ticket.fare)}</strong>
                          </td>
                          <td>
                            <Badge
                              style={{
                                backgroundColor: getSeatBadgeColor(
                                  ticket.seatNumber
                                ),
                                border: "none",
                              }}
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
      <Modal
        show={showDetailsModal}
        onHide={() => setShowDetailsModal(false)}
        size="lg"
      >
        <Modal.Header closeButton>
          <Modal.Title>
            <i className="bi bi-ticket-detailed me-2"></i>
            Chi tiết vé
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedTicket && (
            <Row>
              <Col md={6}>
                <Card className="h-100">
                  <Card.Header>
                    <h6 className="mb-0">Thông tin chuyến bay</h6>
                  </Card.Header>
                  <Card.Body>
                    <dl className="row mb-0">
                      <dt className="col-sm-4">Mã chuyến bay:</dt>
                      <dd className="col-sm-8">{selectedTicket.flightCode}</dd>

                      <dt className="col-sm-4">Hạng vé:</dt>
                      <dd className="col-sm-8">
                        {selectedTicket.ticketClassName}
                      </dd>

                      <dt className="col-sm-4">Tuyến đường:</dt>
                      <dd className="col-sm-8">
                        {selectedTicket.departureAirport} →{" "}
                        {selectedTicket.arrivalAirport}
                      </dd>

                      <dt className="col-sm-4">Khởi hành:</dt>
                      <dd className="col-sm-8">
                        {formatDateTime(selectedTicket.departureTime)}
                      </dd>

                      <dt className="col-sm-4">Đến nơi:</dt>
                      <dd className="col-sm-8">
                        {formatDateTime(selectedTicket.arrivalTime)}
                      </dd>

                      <dt className="col-sm-4">Ghế:</dt>
                      <dd className="col-sm-8">
                        <Badge
                          style={{
                            backgroundColor: getSeatBadgeColor(
                              selectedTicket.seatNumber
                            ),
                            border: "none",
                          }}
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
                    <h6 className="mb-0">Hành khách & Thanh toán</h6>
                  </Card.Header>
                  <Card.Body>
                    <dl className="row mb-0">
                      <dt className="col-sm-5">Tên:</dt>
                      <dd className="col-sm-7">
                        {selectedTicket.passengerName}
                      </dd>

                      <dt className="col-sm-5">CCCD:</dt>
                      <dd className="col-sm-7">
                        {selectedTicket.passengerCitizenId}
                      </dd>

                      <dt className="col-sm-5">Điện thoại:</dt>
                      <dd className="col-sm-7">
                        {selectedTicket.phoneNumber || "Chưa có"}
                      </dd>

                      <dt className="col-sm-5">Mã xác nhận:</dt>
                      <dd className="col-sm-7">
                        <code>{selectedTicket.confirmationCode}</code>
                      </dd>

                      <dt className="col-sm-5">Trạng thái:</dt>
                      <dd className="col-sm-7">
                        {getStatusBadge(selectedTicket.ticketStatus)}
                      </dd>

                      <dt className="col-sm-5">Giá vé:</dt>
                      <dd className="col-sm-7">
                        <strong>{formatCurrency(selectedTicket.fare)}</strong>
                      </dd>

                      <dt className="col-sm-5">Thời gian thanh toán:</dt>
                      <dd className="col-sm-7">
                        {selectedTicket.paymentTime
                          ? formatDateTime(selectedTicket.paymentTime)
                          : "Chưa thanh toán"}
                      </dd>
                    </dl>
                  </Card.Body>
                </Card>
              </Col>
            </Row>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowDetailsModal(false)}
          >
            Đóng
          </Button>
          {selectedTicket &&
            selectedTicket.ticketStatus === "UNPAID" &&
            new Date(selectedTicket.departureTime) > new Date() && (
              <Button variant="success" onClick={handleCashPayment}>
                <i className="bi bi-cash-coin me-2"></i>
                Thanh toán tiền mặt tại quầy
              </Button>
            )}
          {selectedTicket &&
            new Date(selectedTicket.departureTime) > new Date() && (
              <Button variant="danger" onClick={handleCancelTicket}>
                <i className="bi bi-x-circle me-2"></i>
                Hủy vé
              </Button>
            )}
        </Modal.Footer>
      </Modal>

      {/* Cash Payment Confirmation Modal */}
      <Modal
        show={showCashPaymentModal}
        onHide={() => !cashPaymentLoading && setShowCashPaymentModal(false)}
        centered
      >
        <Modal.Header closeButton className="bg-success text-white">
          <Modal.Title>
            <i className="bi bi-cash-coin me-2"></i>
            Xác nhận thanh toán tiền mặt
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          <div className="text-center mb-3">
            <i
              className="bi bi-cash-stack text-success"
              style={{ fontSize: "3rem" }}
            ></i>
          </div>
          <h5 className="text-center mb-3">
            Xác nhận đã nhận thanh toán tiền mặt?
          </h5>
          <p className="text-center text-muted mb-3">
            Hành động này sẽ đánh dấu vé đã được thanh toán bằng tiền mặt tại
            quầy.
          </p>
          {selectedTicket && (
            <div className="p-3 bg-light rounded mb-3">
              <div className="text-center">
                <strong>Mã vé: {selectedTicket.ticketId}</strong>
                <br />
                <span className="text-muted">
                  {selectedTicket.flightCode} - Ghế: {selectedTicket.seatNumber}
                </span>
                <br />
                <span className="text-muted">
                  {selectedTicket.passengerName}
                </span>
                <br />
                <div className="mt-2">
                  <span className="h5 text-success fw-bold">
                    {formatCurrency(selectedTicket.fare)}
                  </span>
                </div>
              </div>
            </div>
          )}
          <Alert variant="info" className="mb-0">
            <div className="d-flex align-items-start">
              <i className="bi bi-info-circle-fill me-2 mt-1"></i>
              <div>
                <strong>Lưu ý:</strong>
                <ul className="mb-0 mt-1">
                  <li>Đảm bảo đã nhận đủ số tiền từ khách hàng</li>
                  <li>Đã kiểm tra và trả lại tiền thối (nếu có)</li>
                  <li>Đã cung cấp hóa đơn cho khách hàng</li>
                </ul>
              </div>
            </div>
          </Alert>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowCashPaymentModal(false)}
            disabled={cashPaymentLoading}
          >
            Hủy
          </Button>
          <Button
            variant="success"
            onClick={confirmCashPayment}
            disabled={cashPaymentLoading}
          >
            {cashPaymentLoading ? (
              <>
                <Spinner animation="border" size="sm" className="me-2" />
                Đang xử lý...
              </>
            ) : (
              <>
                <i className="bi bi-check-circle me-2"></i>
                Xác nhận đã thanh toán
              </>
            )}
          </Button>
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
            Hủy vé
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
            Bạn có chắc chắn muốn hủy vé này không?
          </h5>
          <p className="text-center text-muted mb-0">
            Hành động này không thể hoàn tác. Vé sẽ bị hủy vĩnh viễn.
          </p>
          {selectedTicket && (
            <div className="mt-3 p-3 bg-light rounded">
              <div className="text-center">
                <strong>Mã vé: {selectedTicket.ticketId}</strong>
                <br />
                <span className="text-muted">
                  {selectedTicket.flightCode} - {selectedTicket.passengerName}
                </span>
                <br />
                <span className="text-muted">
                  Ghế: {selectedTicket.seatNumber}
                </span>
                <br />
                <span className="text-primary fw-bold">
                  {formatCurrency(selectedTicket.fare)}
                </span>
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
            Giữ vé
          </Button>
          <Button
            variant="danger"
            onClick={confirmCancelTicket}
            disabled={cancelLoading}
          >
            {cancelLoading ? (
              <>
                <Spinner animation="border" size="sm" className="me-2" />
                Đang hủy...
              </>
            ) : (
              <>
                <i className="bi bi-trash me-2"></i>
                Có, hủy vé
              </>
            )}
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
            Thành công
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4 text-center">
          <div className="mb-3">
            <i
              className="bi bi-check-circle text-success"
              style={{ fontSize: "3rem" }}
            ></i>
          </div>
          <h5 className="mb-3">{successMessage}</h5>
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
            Lỗi
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4 text-center">
          <div className="mb-3">
            <i
              className="bi bi-x-circle text-danger"
              style={{ fontSize: "3rem" }}
            ></i>
          </div>
          <h5 className="mb-3">Có lỗi xảy ra</h5>
          <p className="text-muted mb-0">{errorMessage}</p>
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

export default TicketingManagement;
