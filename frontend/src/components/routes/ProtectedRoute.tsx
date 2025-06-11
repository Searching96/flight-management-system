import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredAccountType?: string;  // e.g. "Customer", "Employee"
  requiredRoles?: string[]; // e.g. ["EMPLOYEE_TICKETING", "EMPLOYEE_ADMINISTRATOR"]
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredAccountType, // e.g. "Customer", "Employee"
  requiredRoles
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
  if ((requiredAccountType && user.accountTypeName !== requiredAccountType) ||
      (requiredRoles && !requiredRoles.includes(user.role))) {
    // Redirect to appropriate dashboard if wrong account type
    // accountTypeName: Customer, Employee (corrected to match backend database)
    return (
      <Navigate 
        to="/" 
        state={{ message: 'Insufficient permissions' }} 
        replace 
      />
    );
  }

  return <>{children}</>;
};

export default ProtectedRoute;
