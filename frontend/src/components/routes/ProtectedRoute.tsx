import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredAccountType?: number; // 1 for customer, 2 for employee (matches backend)
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requiredAccountType 
}) => {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="loading">
        <div>Checking authentication...</div>
      </div>
    );
  }

  if (!user) {
    // Redirect to login with return path
    return (
      <Navigate 
        to="/login" 
        state={{ 
          message: 'Please sign in to access this page',
          returnTo: location.pathname 
        }} 
        replace 
      />
    );
  }
  if (requiredAccountType && user.accountType !== requiredAccountType) {
    // Redirect to appropriate dashboard if wrong account type
    // accountType: 1 = customer, 2 = employee (corrected to match backend database)
    return (
      <Navigate 
        to={user.accountType === 2 ? '/admin' : '/dashboard'} 
        state={{ 
          message: 'You do not have permission to access that page' 
        }} 
        replace 
      />
    );
  }

  return <>{children}</>;
};

export default ProtectedRoute;
