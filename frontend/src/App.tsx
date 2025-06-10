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
import TestForm from './components/test/TestForm';
import CustomerSupport from './components/admin/CustomerSupport';
import DebugLogin from './components/debug/DebugLogin';
import PaymentResult from './components/payment/PaymentResult';
import ForgetPasswordForm from './components/auth/ForgetPasswordForm';
import ResetPasswordForm from './components/auth/ResetPasswordForm';

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
            <Route path="/flights" element={<FlightSearch />} />
            {/* Guest booking routes - public access */}
            <Route path="/booking-confirmation" element={<BookingConfirmation />} />
            <Route path="/booking-lookup" element={<BookingLookup />} />
            <Route path="/booking" element={<BookingForm />} />
            <Route path="/test" element={<TestForm />} />
            <Route path="/payment/result" element={<PaymentResult />} />

            {/* Debug Route */}
            <Route path="/debug/log-me-in/:accountName" element={<DebugLogin />} />

            {/* Protected Routes */}
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute requiredAccountType='Customer'>
                  <Dashboard />
                </ProtectedRoute>
              }
            />
            {/* Admin Routes */}
            <Route
              path="/admin/*"
              element={
                <ProtectedRoute requiredAccountType='Employee'>
                  <AdminPanel />
                </ProtectedRoute>
              }
            />
            <Route path="/admin/chat" element={
              <ProtectedRoute requiredAccountType='Employee'>
                <CustomerSupport />
              </ProtectedRoute>
            } />
            <Route path="/admin/customer-support" element={
              <ProtectedRoute requiredAccountType='Employee'>
                <CustomerSupport />
              </ProtectedRoute>
            } />

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
