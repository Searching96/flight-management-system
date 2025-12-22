// components/admin/AdminPanel.tsx
import React, { useState } from 'react';
import { Container, Row, Col, Card, Nav, Alert, Button, Modal } from 'react-bootstrap';
import FlightManagement from '../flight_scheduler/FlightManagement';
import AirportManagement from '../operation_admin/AirportManagement';
import ParameterSettings from './ParameterSettings';
import PlaneManagement from '../operation_admin/PlaneManagement';
import TicketClassManagement from '../operation_admin/TicketClassManagement';
import EmployeeManagement from './EmployeeManagement';
import { usePermissions } from '../../../hooks/useAuth';
import Statistics from '../../statistics/Statistics';

type AdminTab = 'overview' | 'flights' | 'airports' | 'planes' | 'ticket-classes' | 'parameters' | 'employees' | 'reports';

export const AdminPanel: React.FC = () => {
  const permissions = usePermissions();
  const [activeTab, setActiveTab] = useState<AdminTab>('overview');
  const [showFeatureModal, setShowFeatureModal] = useState(false);

  // Add state for managing quick action modals
  const [quickActionModals, setQuickActionModals] = useState({
    showFlightModal: false,
    showAirportModal: false,
    showTicketClassModal: false,
    showPlaneModal: false,
    showTicketModal: false
  });

  // Redirect if user doesn't have admin permissions
  if (!permissions.canViewAdmin()) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>Truy cập bị từ chối</Alert.Heading>
              <p>Bạn không có quyền truy cập trang quản trị.</p>
              <p className="text-muted">Phần này chỉ dành cho Quản trị viên hệ thống.</p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  const renderContent = () => {
    switch (activeTab) {
      case 'flights':
        return permissions.canViewFlightManagement() ? (
          <FlightManagement
            showAddModal={quickActionModals.showFlightModal}
            onCloseAddModal={() => setQuickActionModals(prev => ({ ...prev, showFlightModal: false }))}
          />
        ) : <AccessDeniedAlert section="Quản lý chuyến bay" />;

      case 'airports':
        return permissions.canViewAirportManagement() ? (
          <AirportManagement
            showAddModal={quickActionModals.showAirportModal}
            onCloseAddModal={() => setQuickActionModals(prev => ({ ...prev, showAirportModal: false }))}
          />
        ) : <AccessDeniedAlert section="Quản lý sân bay" />;

      case 'planes':
        return permissions.canViewPlaneManagement() ? (
          <PlaneManagement
            showAddModal={quickActionModals.showPlaneModal}
            onCloseAddModal={() => setQuickActionModals(prev => ({ ...prev, showPlaneModal: false }))}
          />
        ) : <AccessDeniedAlert section="Quản lý đội máy bay" />;

      case 'ticket-classes':
        return permissions.canViewTicketClassManagement() ? (
          <TicketClassManagement
            showAddModal={quickActionModals.showTicketClassModal}
            onCloseAddModal={() => setQuickActionModals(prev => ({ ...prev, showTicketClassModal: false }))}
          />
        ) : <AccessDeniedAlert section="Quản lý hạng vé" />;

      case 'parameters':
        return permissions.canViewParameterSettings() ? (
          <ParameterSettings />
        ) : <AccessDeniedAlert section="Tham số hệ thống" />;

      case 'employees':
        return permissions.canViewEmployeeManagement() ? (
          <EmployeeManagement />
        ) : <AccessDeniedAlert section="Quản lý nhân viên" />;

      case 'reports':
        return permissions.canViewReports() ? (
          <Statistics />
        ) : <AccessDeniedAlert section="Báo cáo" />;

      default:
      case 'overview':
        return <AdminOverview onNavigate={handleQuickAction} permissions={permissions} onShowFeatureModal={() => setShowFeatureModal(true)} />;
    }
  };

  const handleQuickAction = (action: AdminTab | 'add-flight' | 'add-airport' | 'add-plane' | 'add-ticket-class' | 'add-ticket') => {
    switch (action) {
      case 'add-flight':
        if (permissions.canViewFlightManagement()) {
          setActiveTab('flights');
          setQuickActionModals(prev => ({ ...prev, showFlightModal: true }));
        }
        break;
      case 'add-airport':
        if (permissions.canViewAirportManagement()) {
          setActiveTab('airports');
          setQuickActionModals(prev => ({ ...prev, showAirportModal: true }));
        }
        break;
      case 'add-plane':
        if (permissions.canViewPlaneManagement()) {
          setActiveTab('planes');
          setQuickActionModals(prev => ({ ...prev, showPlaneModal: true }));
        }
        break;
      case 'add-ticket-class':
        if (permissions.canViewTicketClassManagement()) {
          setActiveTab('ticket-classes');
          setQuickActionModals(prev => ({ ...prev, showTicketClassModal: true }));
        }
        break;
      case 'add-ticket':
        setQuickActionModals(prev => ({ ...prev, showTicketModal: true }));
        break;
      default:
        setActiveTab(action);
        break;
    }
  };

  return (
    <Container fluid className="py-4">
      <Row>
        <Col>
          <div className="text-center mb-4">
            <h1 className="mb-2">Trang quản trị</h1>
            <p className="text-muted">Quản lý chuyến bay, sân bay và cài đặt hệ thống</p>
          </div>

          <Nav variant="pills" className="justify-content-center mb-4 flex-wrap">
            <Nav.Item>
              <Nav.Link
                active={activeTab === 'overview'}
                onClick={() => setActiveTab('overview')}
              >
                📊 Tổng quan
              </Nav.Link>
            </Nav.Item>

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

            {permissions.canViewPlaneManagement() && (
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
            )}

            {permissions.canViewEmployeeManagement() && (
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
            )}

            {permissions.canViewParameterSettings() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === 'parameters'}
                  onClick={() => setActiveTab('parameters')}
                >
                  ⚙️ Cài đặt
                </Nav.Link>
              </Nav.Item>
            )}
          </Nav>

          <div>
            {renderContent()}
          </div>
        </Col>
      </Row>

      {/* Feature Modal */}
      <Modal show={showFeatureModal} onHide={() => setShowFeatureModal(false)} centered>
        <Modal.Header closeButton className="bg-info text-white">
          <Modal.Title>
            <i className="bi bi-info-circle me-2"></i>
            Thông báo
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4 text-center">
          <div className="mb-3">
            <i className="bi bi-clock text-info" style={{ fontSize: '3rem' }}></i>
          </div>
          <h5 className="mb-3">Tính năng báo cáo sẽ có sớm!</h5>
          <p className="text-muted mb-0">
            Chúng tôi đang phát triển tính năng báo cáo. Vui lòng quay lại sau.
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="info"
            onClick={() => setShowFeatureModal(false)}
          >
            <i className="bi bi-check me-2"></i>
            Đã hiểu
          </Button>
        </Modal.Footer>
      </Modal>
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

// Admin Overview Component with Permission-based Quick Actions
const AdminOverview: React.FC<{
  onNavigate: (action: AdminTab | 'add-flight' | 'add-airport' | 'add-plane' | 'add-ticket-class' | 'add-ticket') => void;
  permissions: ReturnType<typeof usePermissions>;
  onShowFeatureModal: () => void;
}> = ({ onNavigate, permissions, onShowFeatureModal }) => {
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
              <div className="mb-2" style={{ fontSize: '2rem' }}>💰</div>
              <Card.Title className="h5 text-muted">Doanh thu</Card.Title>
              <Card.Text className="h3 mb-0 text-warning">2.1 tỷ VND</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        {/* Quick Actions - Permission-based */}
        <Col lg={6} className="mb-4">
          <Card className="h-100">
            <Card.Header>
              <Card.Title className="h4 mb-0">Thao tác nhanh</Card.Title>
            </Card.Header>
            <Card.Body>
              <Row>
                {permissions.canViewFlightManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-primary"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('add-flight')}
                    >
                      <span className="me-2">➕</span>
                      Thêm chuyến bay mới
                    </Button>
                  </Col>
                )}

                {permissions.canViewAirportManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-info"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('add-airport')}
                    >
                      <span className="me-2">🏢</span>
                      Thêm sân bay mới
                    </Button>
                  </Col>
                )}

                {permissions.canViewPlaneManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-warning"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('add-plane')}
                    >
                      <span className="me-2">🛩️</span>
                      Thêm máy bay mới
                    </Button>
                  </Col>
                )}

                {permissions.canViewTicketClassManagement() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-dark"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('add-ticket-class')}
                    >
                      <span className="me-2">🎟️</span>
                      Thêm hạng vé
                    </Button>
                  </Col>
                )}

                {permissions.canViewReports() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-success"
                      className="w-100 text-start"
                      size="lg"
                      onClick={onShowFeatureModal}
                    >
                      <span className="me-2">📊</span>
                      Xem báo cáo
                    </Button>
                  </Col>
                )}

                {permissions.canViewParameterSettings() && (
                  <Col md={6} className="mb-3">
                    <Button
                      variant="outline-secondary"
                      className="w-100 text-start"
                      size="lg"
                      onClick={() => onNavigate('parameters')}
                    >
                      <span className="me-2">⚙️</span>
                      Cài đặt hệ thống
                    </Button>
                  </Col>
                )}
              </Row>
            </Card.Body>
          </Card>
        </Col>

        {/* Recent Activity */}
        <Col lg={6} className="mb-4">
          <Card className="h-100">
            <Card.Header>
              <Card.Title className="h4 mb-0">Hoạt động gần đây</Card.Title>
            </Card.Header>
            <Card.Body>
              <div className="d-flex align-items-center mb-3 pb-3 border-bottom">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>✈️</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">Chuyến bay mới FL001 đã được thêm</div>
                  <small className="text-muted">2 giờ trước</small>
                </div>
              </div>
              <div className="d-flex align-items-center mb-3 pb-3 border-bottom">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>🏢</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">Sân bay LAX đã được cập nhật</div>
                  <small className="text-muted">4 giờ trước</small>
                </div>
              </div>
              <div className="d-flex align-items-center">
                <div className="me-3" style={{ fontSize: '1.5rem' }}>⚙️</div>
                <div className="flex-grow-1">
                  <div className="fw-medium">Tham số hệ thống đã được cập nhật</div>
                  <small className="text-muted">1 ngày trước</small>
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default AdminPanel;