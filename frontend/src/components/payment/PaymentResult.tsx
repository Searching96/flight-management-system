import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Alert, Button, Spinner } from 'react-bootstrap';
import { paymentService, ticketService } from '../../services';
import { PaymentReturnResponse } from '../../models';

const PaymentResult: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [paymentStatus, setPaymentStatus] = useState<'success' | 'pending' | 'failed'>('pending');
  const [processingError, setProcessingError] = useState<string | null>(null);
  const [transactionDetails, setTransactionDetails] = useState<PaymentReturnResponse>();

  function hexToAscii(hex: string): string {
    let ascii = '';
    for (let i = 0; i < hex.length; i += 2) {
      ascii += String.fromCharCode(parseInt(hex.substr(i, 2), 16));
    }
    return ascii;
  }

  const handlePayment = async () => {
    try {
      const response = await paymentService.createPayment(transactionDetails!.data.vnp_TxnRef);
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


  useEffect(() => {
    const processPaymentResult = async () => {
      try {
        setLoading(true);

        // Extract query parameters
        const queryParams = new URLSearchParams(location.search);
        const responseCode = queryParams.get('vnp_ResponseCode');

        // Process payment return
        const paymentResult = await paymentService.processPaymentReturn(location.search);

        // Store transaction details for display
        setTransactionDetails(paymentResult);

        // Get Vietnamese response message
        const responseMessage = paymentResult.signatureValid ? getResponseMessage(responseCode || '') : '';

        // Check payment result
        console.log(paymentResult.signatureValid, responseCode);
        if (paymentResult.signatureValid && (responseCode === "00" || responseCode === "01")) {
          setPaymentStatus('success');

          // } else if (responseCode === "24" || responseCode === "11") {
          //   setPaymentStatus('pending');
          //   setProcessingError(responseMessage);
        } else {
          setPaymentStatus('failed');
          setProcessingError(responseMessage);
        }
      } catch (error) {
        console.error('Error processing payment result:', error);
        setPaymentStatus('failed');
        setProcessingError('Không thể xử lý kết quả thanh toán. Vui lòng liên hệ hỗ trợ khách hàng.');
      } finally {
        setLoading(false);
      }
    };

    // Helper function to translate response codes
    const getResponseMessage = (responseCode: string): string => {
      return {
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
      }[responseCode] || "Lỗi không xác định";
    };

    processPaymentResult();
  }, [location.search]);

  // Format payment date from YYYYMMDDHHMMSS to readable format
  const formatPaymentDate = (dateString: string | null) => {
    if (!dateString) return 'N/A';

    try {
      const year = dateString.substring(0, 4);
      const month = dateString.substring(4, 6);
      const day = dateString.substring(6, 8);
      const hour = dateString.substring(8, 10);
      const minute = dateString.substring(10, 12);
      const second = dateString.substring(12, 14);

      return `${day}/${month}/${year} ${hour}:${minute}:${second}`;
    } catch (e) {
      return dateString;
    }
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-3">Đang xử lý kết quả thanh toán...</p>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col md={8}>
          <Card>
            <Card.Header className={`text-white ${paymentStatus === 'success' ? 'bg-success' : paymentStatus === 'pending' ? 'bg-warning' : 'bg-danger'}`}>
              <h4 className="mb-0">
                {paymentStatus === 'success' && '✅ Thanh toán thành công'}
                {paymentStatus === 'pending' && '⏳ Đang xử lý thanh toán'}
                {paymentStatus === 'failed' && '❌ Thanh toán thất bại'}
              </h4>
            </Card.Header>

            <Card.Body className="p-4">
              {processingError && (
                <Alert variant={paymentStatus === 'pending' ? 'warning' : 'danger'} className="mb-4">
                  {processingError}
                </Alert>
              )}

              {transactionDetails!.data && (
                <div>
                  <h5 className="mb-3">Chi tiết giao dịch</h5>
                  <Row className="mb-4">
                    <Col xs={6} className="fw-bold">Mã đơn hàng:</Col>
                    <Col xs={6}>{transactionDetails!.data.vnp_TxnRef || 'N/A'}</Col>

                    <Col xs={6} className="fw-bold">Số tiền:</Col>
                    <Col xs={6}>{Number(transactionDetails!.data.vnp_Amount).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</Col>

                    <Col xs={6} className="fw-bold">Phương thức thanh toán:</Col>
                    <Col xs={6}>{transactionDetails!.data.vnp_CardType || 'N/A'}</Col>

                    <Col xs={6} className="fw-bold">Ngân hàng:</Col>
                    <Col xs={6}>{transactionDetails!.data.vnp_BankCode || 'N/A'}</Col>

                    <Col xs={6} className="fw-bold">Thời gian thanh toán:</Col>
                    <Col xs={6}>{formatPaymentDate(transactionDetails!.data.vnp_PayDate)}</Col>

                    <Col xs={6} className="fw-bold">Thông tin đơn hàng:</Col>
                    <Col xs={6}>{transactionDetails!.data.vnp_OrderInfo || 'N/A'}</Col>
                  </Row>
                </div>
              )}

              {paymentStatus === 'success' && (
                <Alert variant="success">
                  <Alert.Heading>Thanh toán thành công</Alert.Heading>
                  <p>Thanh toán của bạn đã được xử lý thành công và đặt vé của bạn đã được xác nhận.</p>
                </Alert>
              )}

              {paymentStatus === 'pending' && (
                <Alert variant="warning">
                  <Alert.Heading>Đang xử lý thanh toán</Alert.Heading>
                  <p>Thanh toán của bạn đang được xử lý. Vui lòng không gửi lại yêu cầu thanh toán. Bạn sẽ nhận được xác nhận khi thanh toán hoàn tất.</p>
                </Alert>
              )}

              {paymentStatus === 'failed' && (
                <Alert variant="danger">
                  <Alert.Heading>Thanh toán thất bại</Alert.Heading>
                  <p>Không thể xử lý thanh toán của bạn. Vui lòng thử lại hoặc liên hệ bộ phận hỗ trợ khách hàng.</p>
                </Alert>
              )}
            </Card.Body>

            <Card.Footer className="d-flex justify-content-between">
              <Button variant="outline-secondary" onClick={() => navigate('/booking-lookup')}>
                Xem đặt vé của tôi
              </Button>
              <Button variant="primary" onClick={() => navigate('/')}>
                Quay lại trang chủ
              </Button>
              {paymentStatus === 'failed' && (
                <Button variant="success" onClick={() => handlePayment()}>
                  Thử thanh toán lại
                </Button>
              )}
            </Card.Footer>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default PaymentResult;
