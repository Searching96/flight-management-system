import React, { useState } from 'react';
import { Container, Row, Col, Card, Nav, Alert, Button } from 'react-bootstrap';
import FlightManagement from '../admin/FlightManagement';
import AirportManagement from '../admin/AirportManagement';
import PlaneManagement from '../admin/PlaneManagement';
import TicketClassManagement from '../admin/TicketClassManagement';
import EmployeeManagement from '../admin/EmployeeManagement';
import { usePermissions } from '../../hooks/useAuth';
import Statistics from '../Statistics/Statistics';
import ParameterSettings from '../admin/ParameterSettings';

type SupportTab = 'overview' | 'flights' | 'airports' | 'planes' | 'ticket-classes' | 'employees' | 'reports' | 'parameters';

export const SupportSearch: React.FC = () => {
  const permissions = usePermissions();
  const [activeTab, setActiveTab] = useState<SupportTab>('overview');

  // Redirect if user doesn't have support permissions
  if (!permissions.canViewAdmin()) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>Truy cập bị từ chối</Alert.Heading>
              <p>Bạn không có quyền truy cập trang hỗ trợ.</p>
              <p className="text-muted">Phần này chỉ dành cho nhân viên hỗ trợ.</p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  const renderContent = () => {
    switch (activeTab) {


      case 'airports':
        return permissions.canViewAirportManagement() ? (
          <AirportManagement
            showAddModal={false}
            onCloseAddModal={() => { }}
            readOnly={true}
          />
        ) : <AccessDeniedAlert section="Thông tin sân bay" />;

      // case 'planes':
      //   return permissions.canViewPlaneManagement() ? (
      //     <PlaneManagement
      //       showAddModal={false}
      //       onCloseAddModal={() => {}}
      //       readOnly={true}
      //     />
      //   ) : <AccessDeniedAlert section="Thông tin máy bay" />;

      // case 'ticket-classes':
      //   return permissions.canViewTicketClassManagement() ? (
      //     <TicketClassManagement
      //       showAddModal={false}
      //       onCloseAddModal={() => {}}
      //       readOnly={true}
      //     />
      //   ) : <AccessDeniedAlert section="Thông tin hạng vé" />;

      case 'parameters':
        return permissions.canViewParameterSettings() ? (
          <ParameterSettings readOnly={true} />
        ) : <AccessDeniedAlert section="Thông tin tham số" />;

      default:
      case 'flights':
        return permissions.canViewFlightManagement() ? (
          <FlightManagement
            showAddModal={false}
            onCloseAddModal={() => { }}
            readOnly={true}
          />
        ) : <AccessDeniedAlert section="Thông tin chuyến bay" />;
      // case 'overview':
      //   return <SupportOverview onNavigate={setActiveTab} permissions={permissions} />;
    }
  };

  return (
    <Container fluid className="py-4">
      <Row>
        <Col>
          <div className="text-center mb-4">
            <h1 className="mb-2">Trang hỗ trợ khách hàng</h1>
            <p className="text-muted">Tra cứu thông tin chuyến bay, sân bay và hỗ trợ khách hàng</p>
          </div>

          <Nav variant="pills" className="justify-content-center mb-4 flex-wrap">
            {/* <Nav.Item>
              <Nav.Link
                active={activeTab === 'overview'}
                onClick={() => setActiveTab('overview')}
              >
                📊 Tổng quan
              </Nav.Link>
            </Nav.Item> */}

            {permissions.canViewFlightManagement() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === 'flights'}
                  onClick={() => setActiveTab('flights')}
                >
                  ✈️ Chuyến bay
                </Nav.Link>
              </Nav.Item>
            )}

            {permissions.canViewAirportManagement() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === 'airports'}
                  onClick={() => setActiveTab('airports')}
                >
                  🏢 Sân bay
                </Nav.Link>
              </Nav.Item>
            )}

            {/* {permissions.canViewPlaneManagement() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === 'planes'}
                  onClick={() => setActiveTab('planes')}
                >
                  🛩️ Đội máy bay
                </Nav.Link>
              </Nav.Item>
            )}

            {permissions.canViewTicketClassManagement() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === 'ticket-classes'}
                  onClick={() => setActiveTab('ticket-classes')}
                >
                  🎟️ Hạng vé
                </Nav.Link>
              </Nav.Item>
            )} */}

            {/* {permissions.canViewEmployeeManagement() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === 'employees'}
                  onClick={() => setActiveTab('employees')}
                >
                  👥 Nhân viên
                </Nav.Link>
              </Nav.Item>
            )}

            {permissions.canViewReports() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === 'reports'}
                  onClick={() => setActiveTab('reports')}
                >
                  📊 Báo cáo
                </Nav.Link>
              </Nav.Item>
            )} */}
            {permissions.canViewParameterSettings() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === 'parameters'}
                  onClick={() => setActiveTab('parameters')}
                >
                  ⚙️ Tham số hệ thống
                </Nav.Link>
              </Nav.Item>
            )}
          </Nav>

          <div>
            {renderContent()}
          </div>
        </Col>
      </Row>
    </Container>
  );
};

// Access Denied Alert Component
const AccessDeniedAlert: React.FC<{ section: string }> = ({ section }) => (
  <Alert variant="warning" className="text-center">
    <Alert.Heading>Không đủ quyền</Alert.Heading>
    <p>Bạn không có quyền truy cập <strong>{section}</strong>.</p>
    <p className="text-muted mb-0">Liên hệ quản trị viên hệ thống nếu bạn cho rằng đây là lỗi.</p>
  </Alert>
);

// Support Overview Component - Read-only version
const SupportOverview: React.FC<{
  onNavigate: (tab: SupportTab) => void;
  permissions: ReturnType<typeof usePermissions>;
}> = ({ onNavigate, permissions }) => {
  return (
    <Container fluid>
      {/* Statistics Cards */}
      <Row className="mb-4">
        <Col lg={3} md={6} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <div className="mb-2" style={{ fontSize: '2rem' }}>✈️</div>
              <Card.Title className="h5 text-muted">Tổng chuyến bay</Card.Title>
              <Card.Text className="h3 mb-0 text-primary">156</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={3} md={6} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <div className="mb-2" style={{ fontSize: '2rem' }}>🏢</div>
              <Card.Title className="h5 text-muted">Sân bay hoạt động</Card.Title>
              <Card.Text className="h3 mb-0 text-info">23</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={3} md={6} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <div className="mb-2" style={{ fontSize: '2rem' }}>👥</div>
              <Card.Title className="h5 text-muted">Tổng hành khách</Card.Title>
              <Card.Text className="h3 mb-0 text-success">8,432</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={3} md={6} className="mb-3">
          <Card className="h-100 text-center">
            <Card.Body>
              <div className="mb-2" style={{ fontSize: '2rem' }}>🔍</div>
              <Card.Title className="h5 text-muted">Tra cứu hôm nay</Card.Title>
              <Card.Text className="h3 mb-0 text-warning">142</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        {/* Quick Search Actions */}
        <Col lg={6} className="mb-4">
          <Card className="h-100">
            <Card.Header>
              <Card.Title className="h4 mb-0">Tra cứu nhanh</Card.Title>
            </Card.Header>
            <Card.Body>
              <Row>
                {permissions.canViewFlightManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-primary"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('flights')}
                    >
                      <span className="me-2">🔍</span>
                      Tra cứu chuyến bay
                    </Button>
                  </Col>
                )}

                {permissions.canViewAirportManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-info"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('airports')}
                    >
                      <span className="me-2">🏢</span>
                      Thông tin sân bay
                    </Button>
                  </Col>
                )}

                {permissions.canViewPlaneManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-warning"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('planes')}
                    >
                      <span className="me-2">🛩️</span>
                      Thông tin máy bay
                    </Button>
                  </Col>
                )}

                {permissions.canViewTicketClassManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-dark"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('ticket-classes')}
                    >
                      <span className="me-2">🎟️</span>
                      Thông tin hạng vé
                    </Button>
                  </Col>
                )}

                {permissions.canViewReports() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-success"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('reports')}
                    >
                      <span className="me-2">📊</span>
                      Xem báo cáo
                    </Button>
                  </Col>
                )}

                {permissions.canViewEmployeeManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-secondary"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('employees')}
                    >
                      <span className="me-2">👥</span>
                      Thông tin nhân viên
                    </Button>
                  </Col>
                )}
              </Row>
            </Card.Body>
          </Card>
        </Col>

        {/* Recent Support Activity */}
        <Col lg={6} className="mb-4">
          <Card className="h-100">
            <Card.Header>
              <Card.Title className="h4 mb-0">Hoạt động hỗ trợ gần đây</Card.Title>
            </Card.Header>
            <Card.Body>
              <div className="d-flex align-items-center mb-3 pb-3 border-bottom">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>🔍</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">Tra cứu chuyến bay FL001</div>
                  <small className="text-muted">30 phút trước</small>
                </div>
              </div>
              <div className="d-flex align-items-center mb-3 pb-3 border-bottom">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>🎟️</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">Hỗ trợ khách hàng về vé</div>
                  <small className="text-muted">1 giờ trước</small>
                </div>
              </div>
              <div className="d-flex align-items-center">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>📞</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">Xử lý khiếu nại khách hàng</div>
                  <small className="text-muted">2 giờ trước</small>
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default SupportSearch;
