import React from 'react';
import { Container, Row, Col, Nav } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Footer: React.FC = () => {
  return (
    <footer className="bg-dark text-light py-5 mt-auto">
      <Container>
        <Row>
          <Col lg={4} md={6} className="mb-4">
            <h5 className="mb-3">Hệ thống quản lý chuyến bay</h5>
            <p className="text-light mb-3">Đối tác đáng tin cậy cho việc đặt vé và quản lý chuyến bay của bạn.</p>
            <div className="d-flex gap-3">
              <a href="#" className="text-light fs-4" aria-label="Facebook">
                <i className="bi bi-facebook"></i>
              </a>
              <a href="#" className="text-light fs-4" aria-label="Twitter">
                <i className="bi bi-twitter"></i>
              </a>
              <a href="#" className="text-light fs-4" aria-label="Instagram">
                <i className="bi bi-instagram"></i>
              </a>
              <a href="#" className="text-light fs-4" aria-label="LinkedIn">
                <i className="bi bi-linkedin"></i>
              </a>
            </div>
          </Col>
          
          <Col lg={2} md={6} className="mb-4">
            <h6 className="mb-3">Liên kết nhanh</h6>
            <Nav className="flex-column">
              <Nav.Link as={Link} to="/search" className="text-light text-decoration-none p-1">
                Tìm chuyến bay
              </Nav.Link>
              <Nav.Link as={Link} to="/booking-lookup" className="text-light text-decoration-none p-1">
                Tra cứu đặt chỗ
              </Nav.Link>
              <Nav.Link as={Link} to="/support" className="text-light text-decoration-none p-1">
                Hỗ trợ khách hàng
              </Nav.Link>
              <Nav.Link as={Link} to="/about" className="text-light text-decoration-none p-1">
                Về chúng tôi
              </Nav.Link>
            </Nav>
          </Col>
          
          <Col lg={3} md={6} className="mb-4">
            <h6 className="mb-3">Hỗ trợ</h6>
            <Nav className="flex-column">
              <Nav.Link href="/help" className="text-light text-decoration-none p-1">
                Trung tâm trợ giúp
              </Nav.Link>
              <Nav.Link href="/contact" className="text-light text-decoration-none p-1">
                Liên hệ
              </Nav.Link>
              <Nav.Link href="/faq" className="text-light text-decoration-none p-1">
                Câu hỏi thường gặp
              </Nav.Link>
              <Nav.Link href="/terms" className="text-light text-decoration-none p-1">
                Điều khoản dịch vụ
              </Nav.Link>
            </Nav>
          </Col>
          
          <Col lg={3} md={6} className="mb-4">
            <h6 className="mb-3">Thông tin liên hệ</h6>
            <ul className="list-unstyled">
              <li className="mb-2 text-light">
                <i className="bi bi-telephone me-2"></i>+84 (028) 123-4567
              </li>
              <li className="mb-2 text-light">
                <i className="bi bi-envelope me-2"></i>customer-support@thinhuit.id.vn
              </li>
              <li className="mb-2 text-light">
                <i className="bi bi-geo-alt me-2"></i>123 Đường Hàng Không, TP.HCM
              </li>
              <li className="mb-2 text-light">
                <i className="bi bi-clock me-2"></i>Hỗ trợ khách hàng 24/7
              </li>
            </ul>
          </Col>
        </Row>

        <hr className="my-4" />
          <Row>
          <Col className="text-center">
            <p className="text-light mb-0">&copy; {new Date().getFullYear()} Hệ thống quản lý chuyến bay. Bảo lưu mọi quyền.</p>
          </Col>
        </Row>
      </Container>
    </footer>
  );
};

export default Footer;
