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
import AdminPanel from './components/admin/AdminPanel';
import ChatWidget from './components/chat/ChatWidget';
import './App.css';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Layout>
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginForm />} />
              <Route path="/register" element={<RegisterForm />} />
              <Route path="/search" element={<FlightSearch />} />
              <Route path="/flights" element={<FlightSearch />} />

              {/* Protected Routes */}
              <Route 
                path="/dashboard" 
                element={
                  <ProtectedRoute>
                    <Dashboard />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/book/:flightId" 
                element={
                  <ProtectedRoute>
                    <BookingForm />
                  </ProtectedRoute>
                } 
              />
              
              {/* Admin Routes */}
              <Route 
                path="/admin" 
                element={
                  <ProtectedRoute requiredAccountType={1}>
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
        </div>
      </Router>
    </AuthProvider>
  );
};

export default App;
