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

const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <Layout>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginForm />} />
            <Route path="/register" element={<RegisterForm />} />            <Route path="/search" element={<FlightSearch />} />
            <Route path="/flights" element={<FlightSearch />} />            {/* Guest booking routes - public access */}
            <Route path="/booking-confirmation" element={<BookingConfirmation />} />
            <Route path="/booking-lookup" element={<BookingLookup />} />
            <Route path="/booking" element={<BookingForm />} />

            {/* Protected Routes */}
            <Route 
              path="/dashboard" 
              element={
                <ProtectedRoute requiredAccountType={1}>
                  <Dashboard />
                </ProtectedRoute>
              } 
            />
            {/* Admin Routes */}
            <Route 
              path="/admin/*" 
              element={
                <ProtectedRoute requiredAccountType={2}>
                  <AdminPanel />
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
