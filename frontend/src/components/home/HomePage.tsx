import React from 'react';
import { Link } from 'react-router-dom';
import { Container, Row, Col, Card, Badge } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';

const HomePage: React.FC = () => {
  const { user } = useAuth();
  const features = [
    {
      icon: 'bi-search',
      title: 'Tìm kiếm thông minh',
      description: 'Tìm chuyến bay với công cụ tìm kiếm thông minh so sánh giá cả và lịch trình từ nhiều hãng hàng không.'
    },
    {
      icon: 'bi-shield-check',
      title: 'Đặt vé an toàn',
      description: 'Đặt vé với sự tin tưởng bằng hệ thống thanh toán bảo mật và xác nhận tức thì.'
    },
    {
      icon: 'bi-phone',
      title: 'Tối ưu di động',
      description: 'Truy cập đặt chỗ của bạn mọi lúc mọi nơi với thiết kế tương thích mọi thiết bị.'
    },
    {
      icon: 'bi-currency-dollar',
      title: 'Giá tốt nhất',
      description: 'Chúng tôi đảm bảo giá cạnh tranh và có chính sách đối chiếu giá cho ưu đãi tốt nhất.'
    }
  ];

  const popularDestinations = [
    { city: 'Hà Nội', country: 'Việt Nam', icon: 'bi-building' },
    { city: 'TP. Hồ Chí Minh', country: 'Việt Nam', icon: 'bi-geo-alt' },
    { city: 'Đà Nẵng', country: 'Việt Nam', icon: 'bi-water' },
    { city: 'Bangkok', country: 'Thái Lan', icon: 'bi-sun' },
    { city: 'Singapore', country: 'Singapore', icon: 'bi-heart' },
    { city: 'Seoul', country: 'Hàn Quốc', icon: 'bi-clock-history' }
  ];
  return (
    <div>
      {/* Hero Section */}
      <div className="bg-primary text-white py-5">
        <Container>
          <Row className="align-items-center min-vh-50">
            <Col lg={6}>
              <h1 className="display-4 fw-bold mb-4">Tìm chuyến bay hoàn hảo</h1>
              <p className="lead mb-4">
                Khám phá những điểm đến tuyệt vời với giá cả không thể chối từ và trải nghiệm đặt vé liền mạch
              </p>              <div className="d-flex gap-3 flex-wrap">
                <Link to="/search" className="btn btn-light btn-lg text-decoration-none">
                  <i className="bi bi-search me-2"></i>
                  Tìm chuyến bay
                </Link>
                {!user && (
                  <Link to="/register" className="btn btn-outline-light btn-lg text-decoration-none">
                    <i className="bi bi-person-plus me-2"></i>
                    Đăng ký miễn phí
                  </Link>
                )}
              </div>
            </Col>
            <Col lg={6} className="text-center">
              <div className="position-relative">
                <i 
                  className="bi bi-airplane text-white opacity-75" 
                  style={{ fontSize: '8rem', animation: 'float 3s ease-in-out infinite' }}
                ></i>
              </div>
            </Col>
          </Row>
        </Container>
      </div>

      {/* Features Section */}
      <Container className="py-5">
        <Row>
          <Col>
            <h2 className="text-center mb-5 display-5 fw-bold">Tại sao chọn FlightMS?</h2>
          </Col>
        </Row>
        <Row className="g-4">
          {features.map((feature, index) => (
            <Col key={index} md={6} lg={3}>
              <Card className="h-100 text-center border-0 shadow-sm">
                <Card.Body className="p-4">
                  <div className="mb-3">
                    <i 
                      className={`${feature.icon} text-primary`} 
                      style={{ fontSize: '3rem' }}
                    ></i>
                  </div>
                  <Card.Title className="h4 mb-3">{feature.title}</Card.Title>
                  <Card.Text className="text-muted">
                    {feature.description}
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>

      {/* Popular Destinations */}
      <div className="bg-light py-5">
        <Container>
          <Row>
            <Col>
              <h2 className="text-center mb-5 display-5 fw-bold">Điểm đến phổ biến</h2>
            </Col>
          </Row>
          <Row className="g-4">
            {popularDestinations.map((destination, index) => (
              <Col key={index} md={6} lg={4}>
                <Card className="h-100 border-0 shadow-sm overflow-hidden">
                  <Card.Body className="p-4 text-center">
                    <div className="mb-3">
                      <i 
                        className={`${destination.icon} text-primary`} 
                        style={{ fontSize: '3rem' }}
                      ></i>
                    </div>
                    <Card.Title className="h4 mb-2">{destination.city}</Card.Title>
                    <Card.Text className="text-muted">
                      <Badge bg="secondary" className="fs-6">
                        {destination.country}
                      </Badge>
                    </Card.Text>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        </Container>
      </div>

      {/* CTA Section */}
      <div className="bg-dark text-white py-5">
        <Container>
          <Row>
            <Col lg={8} className="mx-auto text-center">
              <h2 className="display-5 fw-bold mb-3">Sẵn sàng bắt đầu hành trình?</h2>
              <p className="lead mb-4">
                Tham gia cùng hàng ngàn du khách hài lòng tin tưởng FlightMS cho nhu cầu du lịch của họ
              </p>              <div className="d-flex gap-3 justify-content-center flex-wrap">
                <Link to="/search" className="btn btn-primary btn-lg text-decoration-none">
                  <i className="bi bi-ticket me-2"></i>
                  Đặt chuyến bay
                </Link>
                {user ? (
                  <Link to="/dashboard" className="btn btn-outline-light btn-lg text-decoration-none">
                    <i className="bi bi-calendar-check me-2"></i>
                    Xem đặt chỗ của tôi
                  </Link>
                ) : (
                  <Link to="/register" className="btn btn-outline-light btn-lg text-decoration-none">
                    <i className="bi bi-person-plus me-2"></i>
                    Tạo tài khoản
                  </Link>
                )}
              </div>
            </Col>
          </Row>
        </Container>
      </div>

      <style>{`
        @keyframes float {
          0%, 100% { transform: translateY(0px); }
          50% { transform: translateY(-20px); }
        }
        .min-vh-50 { min-height: 50vh; }
      `}</style>
    </div>
  );
};

export default HomePage;
