import React from 'react';
import { Navbar, Nav, Container, Dropdown, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth, usePermissions } from '../../hooks/useAuth';

/**
 * Header Component with Role-Based Navigation (Vietnamese)
 * Last updated: 2025-06-11 09:00:54 UTC by thinh0704hcm
 * 
 * CORRECTED Navigation Structure by Employee Type (Vietnamese):
 * 1. EMPLOYEE_FLIGHT_SCHEDULING - "Quản lý lịch bay": Flight Management only
 * 2. EMPLOYEE_TICKETING - "Danh sách vé, Tìm kiếm chuyến bay, Quản lý đặt chỗ": Search + Booking management (NO ticket classes)
 * 3. EMPLOYEE_SUPPORT - "Chăm sóc khách hàng", "Tra cứu thông tin", "Quản lý khách hàng": Customer Support + Booking lookup
 * 4. EMPLOYEE_ACCOUNTING - "Kế toán": Temporary admin access
 * 5. EMPLOYEE_FLIGHT_OPERATIONS - "Quản lý máy bay", "Quản lý hạng vé", "Quản sân bay", "Quản lý tham số": Aircraft, Ticket Classes, Regulations
 * 6. EMPLOYEE_HUMAN_RESOURCES - "Quản lý nhân viên": Employee Management
 * 7. EMPLOYEE_ADMINISTRATOR - Admin Panel only (full access)
 */
const Header: React.FC = () => {
  const { user, logout } = useAuth();
  const permissions = usePermissions();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  // Helper function to get employee type for display in Vietnamese
  const getEmployeeTypeDisplay = (role: string | undefined) => {
    if (!role) return '';
    
    const typeMap: Record<string, string> = {
      'EMPLOYEE_FLIGHT_SCHEDULING': 'Quản lý lịch bay',
      'EMPLOYEE_TICKETING': 'Nhân viên bán vé',
      'EMPLOYEE_SUPPORT': 'Chăm sóc khách hàng',
      'EMPLOYEE_ACCOUNTING': 'Kế toán',
      'EMPLOYEE_FLIGHT_OPERATIONS': 'Quản lý dịch vụ',
      'EMPLOYEE_HUMAN_RESOURCES': 'Quản lý nhân sự',
      'EMPLOYEE_ADMINISTRATOR': 'Quản trị viên'
    };
    
    return typeMap[role] || role.replace('EMPLOYEE_', '').replace('_', ' ');
  };

  return (
    <Navbar bg="white" expand="lg" sticky="top" className="shadow-sm border-bottom">
      <Container>
        <Navbar.Brand as={Link} to="/" className="fw-bold text-primary fs-4 text-decoration-none">
          <i className="bi bi-airplane me-2"></i>
          FlightMS
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/" className="text-decoration-none">Trang chủ</Nav.Link>
            
            {/* Public links (when not logged in) - Vietnamese */}
            {!user && (
              <>
                <Nav.Link as={Link} to="/search" className="text-decoration-none">Tìm chuyến bay</Nav.Link>
                <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">Quản lý đặt chỗ</Nav.Link>
              </>
            )}
            
            {/* Customer links - Vietnamese */}
            {user && permissions.isCustomer() && (
              <>
                <Nav.Link as={Link} to="/search" className="text-decoration-none">Tìm chuyến bay</Nav.Link>
                <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">Quản lý đặt chỗ</Nav.Link>
                <Nav.Link as={Link} to="/dashboard" className="text-decoration-none">Bảng điều khiển</Nav.Link>
              </>
            )}
            
            {/* Employee links - Role-based navigation - Vietnamese 2025-06-11 09:00:54 UTC by thinh0704hcm */}
            {user && permissions.isEmployee() && (
              <>
                {/* ============================================= */}
                {/* EMPLOYEE TYPE 1: FLIGHT SCHEDULING EMPLOYEE */}
                {/* "Quản lý lịch bay" */}
                {/* ============================================= */}
                {permissions.hasRole('EMPLOYEE_FLIGHT_SCHEDULING') && (
                  <Nav.Link as={Link} to="/flights" className="text-decoration-none">✈️ Quản lý lịch bay</Nav.Link>
                )}
                
                {/* ============================================= */}
                {/* EMPLOYEE TYPE 2: TICKETING EMPLOYEE */}
                {/* "Danh sách vé, Tìm kiếm chuyến bay, Quản lý đặt chỗ" */}
                {/* CORRECTED: REMOVED Ticket Classes (belongs to Type 5) */}
                {/* ============================================= */}
                {permissions.hasRole('EMPLOYEE_TICKETING') && (
                  <>
                    <Nav.Link as={Link} to="/search" className="text-decoration-none">🔍 Tìm chuyến bay</Nav.Link>
                    <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">📋 Quản lý đặt chỗ</Nav.Link>
                    {/* Future: Ticketing Panel for Tickets List */}
                    {/* <Nav.Link as={Link} to="/ticketing" className="text-decoration-none">🎫 Danh sách vé</Nav.Link> */}
                  </>
                )}
                
                {/* ============================================= */}
                {/* EMPLOYEE TYPE 3: CUSTOMER SUPPORT EMPLOYEE */}
                {/* "Chăm sóc khách hàng", "Tra cứu thông tin", "Quản lý khách hàng" */}
                {/* ============================================= */}
                {permissions.hasRole('EMPLOYEE_SUPPORT') && (
                  <>
                    <Nav.Link as={Link} to="/customer-support" className="text-decoration-none">🎧 Chăm sóc khách hàng</Nav.Link>
                    <Nav.Link as={Link} to="/booking-lookup" className="text-decoration-none">🔍 Tra cứu thông tin</Nav.Link>
                  </>
                )}
                
                {/* ============================================= */}
                {/* EMPLOYEE TYPE 4: ACCOUNTING EMPLOYEE */}
                {/* "Kế toán" */}
                {/* ============================================= */}
                {permissions.hasRole('EMPLOYEE_ACCOUNTING') && (
                  <>
                    {/* Future: Accounting Panel */}
                    {/* <Nav.Link as={Link} to="/accounting" className="text-decoration-none">💰 Kế toán</Nav.Link> */}
                    <Nav.Link as={Link} to="/admin" className="text-decoration-none">💰 Kế toán</Nav.Link>
                  </>
                )}
                
                {/* ============================================= */}
                {/* EMPLOYEE TYPE 5: SERVICE MANAGEMENT (FLIGHT OPERATIONS) */}
                {/* "Quản lý máy bay", "Quản lý hạng vé", "Quản sân bay", "Quản lý tham số" */}
                {/* ============================================= */}
                {permissions.hasRole('EMPLOYEE_FLIGHT_OPERATIONS') && (
                  <>
                    <Nav.Link as={Link} to="/airports" className="text-decoration-none">🏢 Quản lý sân bay</Nav.Link>
                    <Nav.Link as={Link} to="/planes" className="text-decoration-none">🛩️ Quản lý máy bay</Nav.Link>
                    <Nav.Link as={Link} to="/ticket-classes" className="text-decoration-none">🎟️ Quản lý hạng vé</Nav.Link>
                    <Nav.Link as={Link} to="/regulations" className="text-decoration-none">📜 Quản lý tham số</Nav.Link>
                  </>
                )}
                
                {/* ============================================= */}
                {/* EMPLOYEE TYPE 6: HUMAN RESOURCES EMPLOYEE */}
                {/* "Quản lý nhân viên" */}
                {/* ============================================= */}
                {permissions.hasRole('EMPLOYEE_HUMAN_RESOURCES') && (
                  <>
                    <Nav.Link as={Link} to="/employee-management" className="text-decoration-none">👥 Quản lý nhân viên</Nav.Link>
                  </>
                )}
                
                {/* ============================================= */}
                {/* EMPLOYEE TYPE 7: ADMINISTRATOR - ADMIN PANEL ONLY */}
                {/* Full access through Admin Panel */}
                {/* ============================================= */}
                {permissions.hasRole('EMPLOYEE_ADMINISTRATOR') && (
                  <Nav.Link as={Link} to="/admin" className="text-decoration-none">⚙️ Quản trị hệ thống</Nav.Link>
                )}
              </>
            )}
          </Nav>
          
          <Nav className="ms-auto">
            {user ? (
              <Dropdown align="end" className='d-flex align-items-center'>
                <Dropdown.Toggle variant="outline-primary" id="dropdown-basic">
                  <i className="bi bi-person-circle me-1"></i>
                  {user.accountName}
                  <small className="ms-1 text-muted">({getEmployeeTypeDisplay(user.role)})</small>
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  <Dropdown.Item as={Link} to="/profile/edit" className="text-decoration-none">
                    <i className="bi bi-person-gear me-2"></i>
                    Chỉnh sửa hồ sơ
                  </Dropdown.Item>
                  <Dropdown.Divider />
                  <Dropdown.Item onClick={handleLogout}>
                    <i className="bi bi-box-arrow-right me-2"></i>
                    Đăng xuất
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            ) : (
              <div className="d-flex gap-2">
                <Button as={Link as any} to="/login" className="me-2" variant='outline-primary'>
                  Đăng nhập
                </Button>
                <Button as={Link as any} to="/register">
                  Đăng ký
                </Button>
              </div>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;