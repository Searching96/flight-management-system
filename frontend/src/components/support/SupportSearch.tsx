import React, { useState } from "react";
import { Container, Row, Col, Nav, Alert } from "react-bootstrap";
import FlightManagement from "../admin/FlightManagement";
import AirportManagement from "../admin/AirportManagement";
import { usePermissions } from "../../hooks/useAuth";
import ParameterSettings from "../admin/ParameterSettings";

type SupportTab =
  | "overview"
  | "flights"
  | "airports"
  | "planes"
  | "ticket-classes"
  | "employees"
  | "reports"
  | "parameters";

export const SupportSearch: React.FC = () => {
  const permissions = usePermissions();
  const [activeTab, setActiveTab] = useState<SupportTab>("overview");

  // Redirect if user doesn't have support permissions
  if (!permissions.canViewAdmin()) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8}>
            <Alert variant="danger" className="text-center">
              <Alert.Heading>Truy cập bị từ chối</Alert.Heading>
              <p>Bạn không có quyền truy cập trang hỗ trợ.</p>
              <p className="text-muted">
                Phần này chỉ dành cho nhân viên hỗ trợ.
              </p>
            </Alert>
          </Col>
        </Row>
      </Container>
    );
  }

  const renderContent = () => {
    switch (activeTab) {
      case "airports":
        return permissions.canViewAirportManagement() ? (
          <AirportManagement
            showAddModal={false}
            onCloseAddModal={() => {}}
            readOnly={true}
          />
        ) : (
          <AccessDeniedAlert section="Thông tin sân bay" />
        );

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

      case "parameters":
        return permissions.canViewParameterSettings() ? (
          <ParameterSettings readOnly={true} />
        ) : (
          <AccessDeniedAlert section="Thông tin tham số" />
        );

      default:
      case "flights":
        return permissions.canViewFlightManagement() ? (
          <FlightManagement
            showAddModal={false}
            onCloseAddModal={() => {}}
            readOnly={true}
          />
        ) : (
          <AccessDeniedAlert section="Thông tin chuyến bay" />
        );
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
            <p className="text-muted">
              Tra cứu thông tin chuyến bay, sân bay và hỗ trợ khách hàng
            </p>
          </div>

          <Nav
            variant="pills"
            className="justify-content-center mb-4 flex-wrap"
          >
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
                  active={activeTab === "flights"}
                  onClick={() => setActiveTab("flights")}
                >
                  ✈️ Chuyến bay
                </Nav.Link>
              </Nav.Item>
            )}

            {permissions.canViewAirportManagement() && (
              <Nav.Item>
                <Nav.Link
                  active={activeTab === "airports"}
                  onClick={() => setActiveTab("airports")}
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
                  active={activeTab === "parameters"}
                  onClick={() => setActiveTab("parameters")}
                >
                  ⚙️ Tham số hệ thống
                </Nav.Link>
              </Nav.Item>
            )}
          </Nav>

          <div>{renderContent()}</div>
        </Col>
      </Row>
    </Container>
  );
};

// Access Denied Alert Component
const AccessDeniedAlert: React.FC<{ section: string }> = ({ section }) => (
  <Alert variant="warning" className="text-center">
    <Alert.Heading>Không đủ quyền</Alert.Heading>
    <p>
      Bạn không có quyền truy cập <strong>{section}</strong>.
    </p>
    <p className="text-muted mb-0">
      Liên hệ quản trị viên hệ thống nếu bạn cho rằng đây là lỗi.
    </p>
  </Alert>
);

// Support Overview Component - Read-only version

export default SupportSearch;
