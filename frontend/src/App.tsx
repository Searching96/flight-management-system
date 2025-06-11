import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './hooks/useAuth';
import Layout from './components/layout/Layout';
import ProtectedRoute from './components/routes/ProtectedRoute';
import HomePage from './components/home/HomePage';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';
import FlightSearch from './components/flights/FlightSearch';
import Dashboard from './components/dashboard/Dashboard';
import BookingForm from './components/booking/BookingForm';
import BookingConfirmation from './components/booking/BookingConfirmation';
import BookingLookup from './components/booking/BookingLookup';
import AdminPanel from './components/admin/AdminPanel';
import ChatWidget from './components/chat/ChatWidget';
import CustomerSupport from './components/admin/CustomerSupport';
import DebugLogin from './components/debug/DebugLogin';
import EditProfile from './components/profile/EditProfile';
import ResetPassword from './components/profile/ResetPassword';
import PaymentResult from './components/payment/PaymentResult';
import ForgetPasswordForm from './components/auth/ForgetPasswordForm';
import ResetPasswordForm from './components/auth/ResetPasswordForm';
import PaymentHandler from './components/payment/PaymentHandler';
import { TicketingManagement } from './components/ticketing';

// Management Components
import FlightManagement from './components/admin/FlightManagement';
import PlaneManagement from './components/admin/PlaneManagement';
import TicketClassManagement from './components/admin/TicketClassManagement';
import ParameterSettings from './components/admin/ParameterSettings';
import AirportManagement from './components/admin/AirportManagement';
import EmployeeManagement from './components/admin/EmployeeManagement';
// import TicketingPanel from './components/employee/TicketingPanel';
// import AccountingPanel from './components/employee/AccountingPanel';

/**
 * Main App Component with Role-Based Access Control
 * Last updated: 2025-06-11 08:51:48 UTC by thinh0704hcm
 * 
 * CORRECTED Employee Role Structure:
 * 1. EMPLOYEE_FLIGHT_SCHEDULING - Nhân viên quản lý lịch bay: "Quản lý lịch bay"
 * 2. EMPLOYEE_TICKETING - Nhân viên bán vé: Tickets List, Search Flights, Manage Booking
 * 3. EMPLOYEE_SUPPORT - Nhân viên chăm sóc khách: "Chăm sóc khách hàng", "Tra cứu thông tin", "Quản lý khách hàng"
 * 4. EMPLOYEE_ACCOUNTING - Nhân viên kế toán: Accounting
 * 5. EMPLOYEE_FLIGHT_OPERATIONS - Nhân viên quản lý dịch vụ: "Quản lý máy bay", "Quản lý hạng vé", "Quản sân bay", "Quản lý tham số"
 * 6. EMPLOYEE_HUMAN_RESOURCES - Nhân viên quản lý nhân sự: "Quản lý nhân viên"
 * 7. EMPLOYEE_ADMINISTRATOR - Quản trị viên: Full AdminPanel access
 */
const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <Layout>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginForm />} />
            <Route path="/register" element={<RegisterForm />} />
            <Route path="/forget-password" element={<ForgetPasswordForm />} />
            <Route path="/reset-password/:token" element={<ResetPasswordForm />} />
            <Route path="/search" element={<FlightSearch />} />

            {/* Guest booking routes - public access */}
            <Route path="/booking-confirmation" element={<BookingConfirmation />} />
            <Route path="/booking-lookup" element={<BookingLookup />} />
            <Route path="/booking" element={<BookingForm />} />
            <Route path="/profile/edit" element={<EditProfile />} />
            <Route path="/profile/reset-password" element={<ResetPassword />} />

            {/* Payment routes */}
            <Route path="/payment/:confirmationCode" element={<PaymentHandler />} />
            <Route path="/payment-result" element={<PaymentResult />} />

            {/* Debug Route */}
            <Route path="/debug/log-me-in/:accountName" element={<DebugLogin />} />

            {/* Customer Protected Routes */}
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute requiredAccountType='Customer'>
                  <Dashboard />
                </ProtectedRoute>
              }
            />

            {/* Employee Management Routes - CORRECTED Permissions 2025-06-11 08:51:48 UTC by thinh0704hcm */}

            {/* Flight Management - EMPLOYEE_FLIGHT_SCHEDULING (Type 1) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            <Route
              path="/flights"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_FLIGHT_SCHEDULING', 'EMPLOYEE_ADMINISTRATOR']}>
                  <FlightManagement />
                </ProtectedRoute>
              }
            />

            {/* Plane Management - EMPLOYEE_FLIGHT_OPERATIONS (Type 5) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            <Route
              path="/planes"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_FLIGHT_OPERATIONS', 'EMPLOYEE_ADMINISTRATOR']}>
                  <PlaneManagement />
                </ProtectedRoute>
              }
            />

            {/* CORRECTED: Ticket Class Management - EMPLOYEE_FLIGHT_OPERATIONS (Type 5) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            {/* Previously incorrectly included EMPLOYEE_TICKETING */}
            <Route
              path="/ticket-classes"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_FLIGHT_OPERATIONS', 'EMPLOYEE_ADMINISTRATOR']}>
                  <TicketClassManagement />
                </ProtectedRoute>
              }
            />

            {/* Ticketing Management - EMPLOYEE_TICKETING and EMPLOYEE_ADMIN */}
            <Route
              path="/ticketing"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_TICKETING', 'EMPLOYEE_ADMIN']}>
                  <TicketingManagement />
                </ProtectedRoute>
              }
            />

            {/* Department Routes */}

            {/* Customer Support - EMPLOYEE_SUPPORT (Type 3) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            <Route
              path="/customer-support"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_SUPPORT', 'EMPLOYEE_ADMINISTRATOR']}>
                  <CustomerSupport />
                </ProtectedRoute>
              }
            />

            {/* Regulations Routes - Parameter Settings - EMPLOYEE_FLIGHT_OPERATIONS (Type 5) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            <Route
              path="/regulations"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_FLIGHT_OPERATIONS', 'EMPLOYEE_ADMINISTRATOR']}>
                  <ParameterSettings />
                </ProtectedRoute>
              }
            />

            {/* Airport Management - EMPLOYEE_FLIGHT_OPERATIONS (Type 5) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            <Route
              path="/airports"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_FLIGHT_OPERATIONS', 'EMPLOYEE_ADMINISTRATOR']}>
                  <AirportManagement />
                </ProtectedRoute>
              }
            />

            {/* Ticketing Panel - EMPLOYEE_TICKETING (Type 2) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            {/* Uncommented for future implementation */}
            {/* <Route
              path="/ticketing"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_TICKETING', 'EMPLOYEE_ADMINISTRATOR']}>
                  <TicketingPanel />
                </ProtectedRoute>
              }
            /> */}

            {/* Accounting Panel - EMPLOYEE_ACCOUNTING (Type 4) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            {/* Uncommented for future implementation */}
            {/* <Route
              path="/accounting"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_ACCOUNTING', 'EMPLOYEE_ADMINISTRATOR']}>
                  <AccountingPanel />
                </ProtectedRoute>
              }
            /> */}

            {/* Admin Panel - EMPLOYEE_ADMINISTRATOR (Type 7) only */}
            <Route
              path="/admin/*"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_ADMINISTRATOR']}>
                  <AdminPanel />
                </ProtectedRoute>
              }
            />

            {/* ADDED: Human Resources routes - EMPLOYEE_HUMAN_RESOURCES (Type 6) and EMPLOYEE_ADMINISTRATOR (Type 7) */}
            <Route
              path="/employees"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_HUMAN_RESOURCES', 'EMPLOYEE_ADMINISTRATOR']}>
                  <EmployeeManagement />
                </ProtectedRoute>
              }
            />

            {/* Legacy Admin Routes (for backward compatibility) */}
            <Route
              path="/admin/customer-support"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_SUPPORT', 'EMPLOYEE_ADMINISTRATOR']}>
                  <CustomerSupport />
                </ProtectedRoute>
              }
            />

            {/* Fallback Route */}
            <Route path="*" element={<div>Page not found</div>} />
          </Routes>
        </Layout>

        {/* Chat Widget - Available on all pages for logged-in users */}
        <ChatWidget />
      </Router>
    </AuthProvider>
  );
};

export default App;