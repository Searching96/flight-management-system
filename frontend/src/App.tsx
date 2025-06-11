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

// Management Components
import FlightManagement from './components/admin/FlightManagement';
import PlaneManagement from './components/admin/PlaneManagement';
import TicketClassManagement from './components/admin/TicketClassManagement';
import ParameterSettings from './components/admin/ParameterSettings';
// import TicketingPanel from './components/employee/TicketingPanel';
// import AccountingPanel from './components/employee/AccountingPanel';

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

            {/* Employee Management Routes */}
            {/* Flight Management - EMPLOYEE_TICKETING and EMPLOYEE_ADMIN */}
            <Route
              path="/flights"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_TICKETING', 'EMPLOYEE_FLIGHT_OPERATIONS', 'EMPLOYEE_ADMIN']}>
                  <FlightManagement />
                </ProtectedRoute>
              }
            />

            {/* Plane Management - EMPLOYEE_ADMIN only */}
            <Route
              path="/planes"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_ADMIN', 'EMPLOYEE_FLIGHT_OPERATIONS']}>
                  <PlaneManagement />
                </ProtectedRoute>
              }
            />

            {/* Ticket Class Management - EMPLOYEE_TICKETING and EMPLOYEE_ADMIN */}
            <Route
              path="/ticket-classes"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_TICKETING', 'EMPLOYEE_ADMIN', 'EMPLOYEE_FLIGHT_OPERATIONS']}>
                  <TicketClassManagement />
                </ProtectedRoute>
              }
            />

            {/* Department Routes */}
            {/* Customer Support - EMPLOYEE_SUPPORT */}
            <Route
              path="/customer-support"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_SUPPORT']}>
                  <CustomerSupport />
                </ProtectedRoute>
              }
            />
            {/* Regulations Routes */}
            {/* Regulations Management - EMPLOYEE_FLIGHT_OPERATIONS and EMPLOYEE_ADMIN */}
            <Route
              path="/regulations"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_ADMIN', 'EMPLOYEE_FLIGHT_OPERATIONS']}>
                  <ParameterSettings />
                </ProtectedRoute>
              }
            />

            {/* Ticketing Panel - EMPLOYEE_TICKETING and EMPLOYEE_ADMIN */}
            {/* <Route
              path="/ticketing"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_TICKETING', 'EMPLOYEE_ADMIN']}>
                  <TicketingPanel />
                </ProtectedRoute>
              }
            /> */}

            {/* Accounting Panel - EMPLOYEE_ACCOUNTING and EMPLOYEE_ADMIN */}
            {/* <Route
              path="/accounting"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_ACCOUNTING', 'EMPLOYEE_ADMIN']}>
                  <AccountingPanel />
                </ProtectedRoute>
              }
            /> */}

            {/* Admin Panel - EMPLOYEE_ADMIN only */}
            <Route
              path="/admin/*"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_ADMIN']}>
                  <AdminPanel />
                </ProtectedRoute>
              }
            />

            {/* Legacy Admin Routes (for backward compatibility) */}
            <Route
              path="/admin/customer-support"
              element={
                <ProtectedRoute requiredAccountType='Employee' requiredRoles={['EMPLOYEE_SUPPORT']}>
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