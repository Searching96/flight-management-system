import React, { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { LoginRequest, UserDetails, RegisterRequest } from '../models';
import { authService } from '../services/authService';

// Define the context type
interface AuthContextType {
  user: UserDetails | null;
  loading: boolean;
  login: (loginRequest: LoginRequest) => Promise<void>;
  register: (registerRequest: RegisterRequest) => Promise<void>;
  logout: () => void;
  refresh: () => Promise<void>;
  forgetPassword?: (email: string) => Promise<void>;
  resetPassword?: (token: string, newPassword: string) => Promise<void>;
}

// Create the context
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Define the provider component
interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<UserDetails | null>(() => {
    const storedUser = authService.getCurrentUser();
    return storedUser ? storedUser : null;
  });

  // Add periodic token refresh
  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        await authService.refreshToken();
      } catch (error) {
        setUser(null);
      }
    }, 300000); // Refresh every 5 minutes

    return () => clearInterval(interval);
  }, []);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = authService.getCurrentUser();
    if (storedUser) {
      setUser(storedUser);
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (loginRequest: LoginRequest) => {
    setLoading(true);
    try {
      await authService.login(loginRequest);
      setUser(authService.getCurrentUser());
      setLoading(false);
    } catch (err) {
      setLoading(false);
      throw err;
    }
  }, []);

  const register = useCallback(async (registerRequest: RegisterRequest) => {
    setLoading(true);
    try {
      await authService.register(registerRequest);
      setUser(authService.getCurrentUser());
      setLoading(false);
    } catch (err) {
      setLoading(false);
      throw err;
    }
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      await authService.refreshToken();
      setUser(authService.getCurrentUser());
      setLoading(false);
    } catch (err) {
      setLoading(false);
      setUser(null);
      throw err;
    }
  }, []);


  const forgetPassword = useCallback(async (email: string) => {
    setLoading(true);
    try {
      await authService.forgetPassword(email);
      setLoading(false);
    } catch (err) {
      setLoading(false);
      throw err;
    }
  }, []);

  const resetPassword = useCallback(async (token: string, newPassword: string) => {
    setLoading(true);
    try {
      await authService.resetPassword(token, newPassword);
      setLoading(false);
    } catch (err) {
      setLoading(false);
      throw err;
    }
  }, []);

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    refresh,
    forgetPassword,
    resetPassword
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Custom hook to use the auth context
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

// In useAuth.tsx
export const usePermissions = () => {
  const { user } = useAuth();

  const hasRole = (role: string) => {
    if (!user) return false;
    return user.role === role;
  };

  const hasAnyRole = (roles: string[]) => {
    return roles.some(role => hasRole(role));
  };

  return {
    // Customer permissions
    canSearchFlights: () => !user || hasRole('CUSTOMER') || user?.accountTypeName === "Employee", // All employees can search
    canManageBookings: () => !user || hasRole('CUSTOMER') || hasAnyRole(['EMPLOYEE_SUPPORT', 'EMPLOYEE_TICKETING', 'EMPLOYEE_ADMIN']),
    canViewDashboard: () => hasRole('CUSTOMER'),

    // Employee permissions - Admin Panel sections
    canViewAdmin: () => hasRole('EMPLOYEE_ADMIN'),
    canViewFlightManagement: () => hasAnyRole(['EMPLOYEE_ADMIN', 'EMPLOYEE_TICKETING', 'EMPLOYEE_FLIGHT_OPERATIONS', 'EMPLOYEE_FLIGHT_SCHEDULING']),
    canViewAirportManagement: () => hasAnyRole(['EMPLOYEE_ADMIN', 'EMPLOYEE_FLIGHT_OPERATIONS']),
    canViewPlaneManagement: () => hasAnyRole(['EMPLOYEE_ADMIN', 'EMPLOYEE_FLIGHT_OPERATIONS']),
    canViewTicketClassManagement: () => hasAnyRole(['EMPLOYEE_ADMIN', 'EMPLOYEE_TICKETING', 'EMPLOYEE_FLIGHT_OPERATIONS']),
    canViewEmployeeManagement: () => hasRole('EMPLOYEE_ADMIN'),
    canViewParameterSettings: () => hasAnyRole(['EMPLOYEE_ADMIN', 'EMPLOYEE_FLIGHT_OPERATIONS']),
    canViewReports: () => hasAnyRole(['EMPLOYEE_ADMIN', 'EMPLOYEE_ACCOUNTING']),

    // Support permissions
    canViewCustomerSupport: () => hasAnyRole(['EMPLOYEE_SUPPORT']),
    canViewTicketing: () => hasAnyRole(['EMPLOYEE_TICKETING', 'EMPLOYEE_ADMIN']),
    canViewAccounting: () => hasAnyRole(['EMPLOYEE_ACCOUNTING', 'EMPLOYEE_ADMIN']),

    // General checks
    isCustomer: () => hasRole('CUSTOMER'),
    isEmployee: () => user?.accountTypeName === "Employee",
    isAdmin: () => hasRole('EMPLOYEE_ADMIN'),

    // Specific role checks
    hasRole,
    hasAnyRole
  };
};

