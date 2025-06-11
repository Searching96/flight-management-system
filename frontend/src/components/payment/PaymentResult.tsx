import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Alert, Button, Spinner, Badge } from 'react-bootstrap';
import { paymentService } from '../../services';
import { PaymentReturnResponse } from '../../models';

const PaymentResult: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [paymentStatus, setPaymentStatus] = useState<'success' | 'pending' | 'failed'>('pending');
  const [processingError, setProcessingError] = useState<string | null>(null);
  const [transactionDetails, setTransactionDetails] = useState<PaymentReturnResponse>();

  // Extract confirmation code from txnRef (handles HHMMSS prefix format)
  const extractConfirmationCode = (txnRef: string): string => {
    try {
      // Check if txnRef starts with exactly 6 digits (HHMMSS format)
      if (txnRef && txnRef.length > 6 && /^\d{6}/.test(txnRef.substring(0, 6))) {
        // New format: HHMMSS + hex
        const timePart = txnRef.substring(0, 6);
        const hexPart = txnRef.substring(6);

        // Log for debugging
        console.log(`Trích xuất thời gian: ${timePart} (${timePart.substring(0, 2)}:${timePart.substring(2, 4)}:${timePart.substring(4, 6)}), hex: ${hexPart}`);

        // Convert hex back to confirmation code
        const bytes = new Uint8Array(hexPart.match(/.{1,2}/g)!.map(byte => parseInt(byte, 16)));
        return new TextDecoder().decode(bytes);
      }

      return 'N/A';
    } catch (error) {
      console.error('Lỗi trích xuất mã xác nhận từ txnRef:', txnRef, error);
      return txnRef || 'N/A'; // Return as-is for manual handling
    }
  };

  const handlePayment = async () => {
    try {
      if (!transactionDetails?.data?.vnp_TxnRef) {
        alert('Không tìm thấy thông tin giao dịch. Vui lòng thử lại.');
        return;
      }

      // Extract confirmation code from the txnRef
      const confirmationCode = extractConfirmationCode(transactionDetails.data.vnp_TxnRef);

      console.log(`Thử thanh toán lại tại 2025-06-11 05:14:08 UTC bởi thinh0704hcm cho mã: ${confirmationCode}`);

      const response = await paymentService.createPayment(confirmationCode);
      if (response && response.data) {
        console.log('Chuyển hướng đến URL thanh toán:', response.data);
        window.location.href = response.data;
      } else {
        alert('URL thanh toán không hợp lệ. Vui lòng thử lại.');
      }
    } catch (error) {
      console.error('Tạo thanh toán thất bại:', error);
      alert('Không thể tạo thanh toán. Vui lòng thử lại sau.');
    }
  };

  useEffect(() => {
    const processPaymentResult = async () => {
      try {
        setLoading(true);

        if (location.search === '') {
          console.log('Không có tham số truy vấn, chuyển hướng về trang chủ');
          navigate('/');
          return;
        }

        // Extract query parameters
        const queryParams = new URLSearchParams(location.search);
        const responseCode = queryParams.get('vnp_ResponseCode');
        const txnRef = queryParams.get('vnp_TxnRef');

        console.log(`Xử lý kết quả thanh toán tại 2025-06-11 05:14:08 UTC - Response Code: ${responseCode}, TxnRef: ${txnRef}`);

        // Process payment return
        const paymentResult = await paymentService.processPaymentReturn(location.search);

        // Store transaction details for display
        setTransactionDetails(paymentResult);

        // Get Vietnamese response message
        const responseMessage = paymentResult.signatureValid ? getResponseMessage(responseCode || '') : 'Chữ ký không hợp lệ';

        // Check payment result
        if (paymentResult.signatureValid && (responseCode === "00" || responseCode === "01")) {
          setPaymentStatus('success');
          console.log('Thanh toán thành công');
        } else {
          setPaymentStatus('failed');
          setProcessingError(responseMessage);
          console.log('Thanh toán thất bại:', responseMessage);
        }
      } catch (error) {
        console.error('Lỗi xử lý kết quả thanh toán:', error);
        setPaymentStatus('failed');
        setProcessingError('Không thể xử lý kết quả thanh toán. Vui lòng liên hệ bộ phận hỗ trợ khách hàng.');
      } finally {
        setLoading(false);
      }
    };

    // Helper function to translate response codes
    const getResponseMessage = (responseCode: string): string => {
      const messages: { [key: string]: string } = {
        "00": "Giao dịch thành công",
        "01": "Đơn hàng đã được xác nhận",
        "02": "Giao dịch thất bại",
        "09": "Thẻ/Tài khoản hết hạn mức",
        "10": "Không đủ hạn mức",
        "11": "Đã hết hạn chờ thanh toán",
        "12": "Thẻ bị khóa",
        "13": "OTP không đúng",
        "24": "Giao dịch không thành công",
        "51": "Tài khoản không đủ số dư",
        "65": "Tài khoản vượt quá giới hạn giao dịch trong ngày",
        "75": "Ngân hàng đang bảo trì",
        "79": "Nhập sai mật khẩu quá số lần quy định"
      };
      return messages[responseCode] || "Lỗi không xác định";
    };

    processPaymentResult();
  }, [location.search, navigate]);

  // Format payment date from YYYYMMDDHHMMSS to readable format
  const formatPaymentDate = (dateString: string | null | undefined): string => {
    if (!dateString) return 'Chưa có';

    try {
      const year = dateString.substring(0, 4);
      const month = dateString.substring(4, 6);
      const day = dateString.substring(6, 8);
      const hour = dateString.substring(8, 10);
      const minute = dateString.substring(10, 12);
      const second = dateString.substring(12, 14);

      return `${day}/${month}/${year} ${hour}:${minute}:${second}`;
    } catch (e) {
      console.error('Lỗi định dạng ngày:', e);
      return dateString;
    }
  };

  // Format txnRef to show time component
  const formatTxnRef = (txnRef: string): string => {
    if (txnRef && txnRef.length > 6 && /^\d{6}/.test(txnRef.substring(0, 6))) {
      const timePart = txnRef.substring(0, 6);
      const hexPart = txnRef.substring(6);
      const timeFormatted = `${timePart.substring(0, 2)}:${timePart.substring(2, 4)}:${timePart.substring(4, 6)}`;
      return `${timeFormatted}-${hexPart}`;
    }
    return txnRef || 'N/A';
  };

  // Format currency
  const formatCurrency = (amount: number): string => {
    return amount.toLocaleString('vi-VN', {
      style: 'currency',
      currency: 'VND'
    });
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-3">Đang xử lý kết quả thanh toán...</p>
        <p className="text-muted small">Xử lý tại: 2025-06-11 05:14:08 UTC bởi thinh0704hcm</p>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col md={8}>
          <Card>
            <Card.Header className={`text-white ${paymentStatus === 'success' ? 'bg-success' : paymentStatus === 'pending' ? 'bg-warning' : 'bg-danger'}`}>
              <div className="d-flex align-items-center justify-content-between">
                <h4 className="mb-0">
                  {paymentStatus === 'success' && (
                    <>
                      <i className="bi bi-check-circle-fill me-2"></i>
                      Thanh toán thành công
                    </>
                  )}
                  {paymentStatus === 'pending' && (
                    <>
                      <i className="bi bi-clock-history me-2"></i>
                      Đang xử lý thanh toán
                    </>
                  )}
                  {paymentStatus === 'failed' && (
                    <>
                      <i className="bi bi-x-circle-fill me-2"></i>
                      Thanh toán thất bại
                    </>
                  )}
                </h4>
                <Badge bg="light" text="dark">
                  {new Date().toLocaleString('vi-VN')}
                </Badge>
              </div>
            </Card.Header>

            <Card.Body className="p-4">
              {processingError && (
                <Alert variant={paymentStatus === 'pending' ? 'warning' : 'danger'} className="mb-4">
                  <Alert.Heading className="fs-6">
                    <i className="bi bi-exclamation-triangle-fill me-2"></i>
                    Chi tiết lỗi
                  </Alert.Heading>
                  {processingError}
                </Alert>
              )}

              {transactionDetails?.data && (
                <div>
                  <h5 className="mb-3">
                    <i className="bi bi-receipt me-2"></i>
                    Chi tiết giao dịch
                  </h5>
                  <Row className="mb-4 g-3">
                    <Col xs={12} sm={6} className="fw-bold">Mã giao dịch:</Col>
                    <Col xs={12} sm={6} className="font-monospace small text-break">
                      {formatTxnRef(transactionDetails.data.vnp_TxnRef || '')}
                    </Col>

                    <Col xs={12} sm={6} className="fw-bold">Mã xác nhận đặt chỗ:</Col>
                    <Col xs={12} sm={6}>
                      <Badge bg="primary" className="fs-6">
                        {extractConfirmationCode(transactionDetails.data.vnp_TxnRef || '')}
                      </Badge>
                    </Col>

                    <Col xs={12} sm={6} className="fw-bold">Số tiền:</Col>
                    <Col xs={12} sm={6} className="fs-5 text-success fw-bold">
                      {transactionDetails.data.vnp_Amount
                        ? formatCurrency(Number(transactionDetails.data.vnp_Amount) / 100)
                        : 'Chưa có'
                      }
                    </Col>

                    <Col xs={12} sm={6} className="fw-bold">Phương thức thanh toán:</Col>
                    <Col xs={12} sm={6}>
                      <i className="bi bi-credit-card me-1"></i>
                      {transactionDetails.data.vnp_CardType || 'Chưa có'}
                    </Col>

                    <Col xs={12} sm={6} className="fw-bold">Ngân hàng:</Col>
                    <Col xs={12} sm={6}>
                      <i className="bi bi-bank me-1"></i>
                      {transactionDetails.data.vnp_BankCode || 'Chưa có'}
                    </Col>

                    <Col xs={12} sm={6} className="fw-bold">Thời gian thanh toán:</Col>
                    <Col xs={12} sm={6}>
                      <i className="bi bi-calendar-event me-1"></i>
                      {formatPaymentDate(transactionDetails.data.vnp_PayDate)}
                    </Col>

                    <Col xs={12} sm={6} className="fw-bold">Mã giao dịch ngân hàng:</Col>
                    <Col xs={12} sm={6} className="font-monospace small">
                      {transactionDetails.data.vnp_TransactionNo || 'Chưa có'}
                    </Col>

                    <Col xs={12} sm={6} className="fw-bold">Thông tin đơn hàng:</Col>
                    <Col xs={12} sm={6} className="text-break">
                      {transactionDetails.data.vnp_OrderInfo || 'Chưa có'}
                    </Col>
                  </Row>
                </div>
              )}

              {paymentStatus === 'success' && (
                <Alert variant="success">
                  <Alert.Heading>
                    <i className="bi bi-check-circle-fill me-2"></i>
                    Thanh toán thành công
                  </Alert.Heading>
                  <p className="mb-2">Cảm ơn bạn! Thanh toán của bạn đã được xử lý thành công và đặt vé của bạn đã được xác nhận.</p>
                  <hr />
                  <p className="mb-0">
                    <i className="bi bi-envelope me-1"></i>
                    Email xác nhận và vé điện tử sẽ được gửi đến hộp thư của bạn trong vài phút tới.
                  </p>
                </Alert>
              )}

              {paymentStatus === 'pending' && (
                <Alert variant="warning">
                  <Alert.Heading>
                    <i className="bi bi-clock-history me-2"></i>
                    Đang xử lý thanh toán
                  </Alert.Heading>
                  <p className="mb-0">Thanh toán của bạn đang được xử lý. Vui lòng không gửi lại yêu cầu thanh toán. Bạn sẽ nhận được xác nhận khi thanh toán hoàn tất.</p>
                </Alert>
              )}

              {paymentStatus === 'failed' && (
                <Alert variant="danger">
                  <Alert.Heading>
                    <i className="bi bi-x-circle-fill me-2"></i>
                    Thanh toán thất bại
                  </Alert.Heading>
                  <p className="mb-2">Rất tiếc, không thể xử lý thanh toán của bạn.</p>
                  <hr />
                  <p className="mb-0">
                    <strong>Bạn có thể:</strong>
                  </p>
                  <ul className="mb-0 mt-2">
                    <li>Thử thanh toán lại với phương thức khác</li>
                    <li>Kiểm tra thông tin thẻ và số dư tài khoản</li>
                    <li>Liên hệ ngân hàng của bạn để được hỗ trợ</li>
                    <li>Liên hệ hotline: 1900-1234 nếu cần hỗ trợ</li>
                  </ul>
                </Alert>
              )}
            </Card.Body>

            <Card.Footer className="d-flex flex-wrap gap-2 justify-content-between">
              <div className="d-flex gap-2">
                <Button variant="outline-secondary" onClick={() => navigate('/booking-lookup')}>
                  <i className="bi bi-search me-2"></i>
                  Quản lý đặt vé
                </Button>
                <Button variant="primary" onClick={() => navigate('/')}>
                  <i className="bi bi-house me-2"></i>
                  Trang chủ
                </Button>
              </div>

              {paymentStatus === 'failed' && (
                <Button variant="success" onClick={handlePayment}>
                  <i className="bi bi-arrow-clockwise me-2"></i>
                  Thử thanh toán lại
                </Button>
              )}
            </Card.Footer>
          </Card>

          {/* Additional Information */}
          {paymentStatus === 'success' && (
            <Alert variant="info" className="mt-4">
              <Alert.Heading className="fs-6">
                <i className="bi bi-info-circle-fill me-2"></i>
                Thông tin hữu ích
              </Alert.Heading>
              <ul className="mb-0 small">
                <li>Vé điện tử của bạn đã được gửi qua email</li>
                <li>Vui lòng có mặt tại sân bay ít nhất 2 giờ trước giờ khởi hành</li>
                <li>Mang theo giấy tờ tùy thân hợp lệ khi làm thủ tục</li>
                <li>Bạn có thể in vé tại nhà hoặc sử dụng vé điện tử trên điện thoại</li>
              </ul>
            </Alert>
          )}

          {paymentStatus === 'failed' && (
            <Alert variant="warning" className="mt-4">
              <Alert.Heading className="fs-6">
                <i className="bi bi-exclamation-triangle-fill me-2"></i>
                Cần hỗ trợ?
              </Alert.Heading>
              <p className="mb-2 small">
                Nếu bạn gặp khó khăn trong việc thanh toán, đội ngũ hỗ trợ của chúng tôi luôn sẵn sàng giúp đỡ:
              </p>
              <ul className="mb-0 small">
                <li><strong>Hotline:</strong> 1900-1234 (24/7)</li>
                <li><strong>Email:</strong> support@thinhuit.id.vn</li>
                <li><strong>Chat trực tuyến:</strong> Góc dưới phải màn hình</li>
              </ul>
            </Alert>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default PaymentResult;