import React, { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { LoginRequest, AuthResponse, UserDetails, RegisterRequest } from '../models';
import { authService } from '../services/authService';

// Define the context type
interface AuthContextType {
  user: UserDetails | null;
  loading: boolean;
  login: (loginRequest: LoginRequest) => Promise<void>;
  register: (registerRequest: RegisterRequest) => Promise<void>;
  logout: () => void;
  refresh: () => Promise<void>;
}

// Create the context
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Define the provider component
interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<UserDetails | null>(() => {
    const storedUser = localStorage.getItem('user');
    return storedUser ? JSON.parse(storedUser) : null;
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
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (loginRequest: LoginRequest) => {
    setLoading(true);
    try {
      const response: UserDetails = await authService.login(loginRequest);
      setUser(response);
      setLoading(false);
    } catch (err) {
      setLoading(false);
      throw err;
    }
  }, []);

  const register = useCallback(async (registerRequest: RegisterRequest) => {
    setLoading(true);
    try {
      const response: UserDetails = await authService.register(registerRequest);
      setUser(response);
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
      const response: AuthResponse = await authService.refreshToken();
      setUser(response.userDetails);
      setLoading(false);
    } catch (err) {
      setLoading(false);
      setUser(null);
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
  return {
    canViewAdmin: user?.accountTypeName === 'Employee',
    canManageFlights: user?.accountTypeName === 'Employee',
    canBookTickets: user?.accountTypeName === 'Customer',
    canViewOwnBookings: !!user,
    canManageEmployees: user?.accountTypeName === 'Employee',
    canManageCustomers: user?.accountTypeName === 'Employee',
    canViewReports: user?.accountTypeName === 'Employee',
  };
};

