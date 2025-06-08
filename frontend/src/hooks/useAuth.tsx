import React, { createContext, useContext, useState, useEffect } from 'react';
import { Account, LoginRequest, RegisterRequest } from '../models';
import { accountService } from '../services';

interface AuthContextType {
  user: Account | null;
  login: (loginRequest: LoginRequest) => Promise<void>;
  register: (registerRequest: RegisterRequest) => Promise<void>;
  logout: () => void;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<Account | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      // Check for stored user account first
      const storedUser = localStorage.getItem('userAccount');
      if (storedUser) {
        const userData = JSON.parse(storedUser);
        setUser(userData);
      }
    } catch (error) {
      localStorage.removeItem('userAccount');
    } finally {
      setLoading(false);
    }
  };
  
  const login = async (loginRequest: LoginRequest) => {
    try {
      setLoading(true);
      const response = await accountService.login(loginRequest);
      
      // Map the response to match frontend Account interface
      const userAccount: Account = {
        accountId: response.accountId,
        accountName: response.accountName,
        email: response.email,
        accountType: response.accountType
      };
      
      setUser(userAccount);
      
      // Store user info (simplified - no tokens)
      localStorage.setItem('userAccount', JSON.stringify(userAccount));
    } catch (error) {
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const register = async (registerRequest: RegisterRequest) => {
    await accountService.register(registerRequest);
  };

  const logout = async () => {
    try {
      // Call logout endpoint if we have a token
      // if (accessToken) {
        await logout();
      // }
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      // Clear memory and storage regardless of API result
      localStorage.removeItem('userAccount');
      // setAccessToken(null);
      setUser(null);
    }
  };

  const value: AuthContextType = {
    user,
    loading,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

// Simplified permission checking to only use accountType
export const usePermissions = () => {
  const { user } = useAuth();
  
  return {
    canViewAdmin: user?.accountType === 2,        // Employee per database schema
    canManageFlights: user?.accountType === 2,    // Employee per database schema
    canBookTickets: user?.accountType === 1,      // Customer per database schema
    canViewOwnBookings: !!user,
    canManageEmployees: user?.accountType === 2,
    canManageCustomers: user?.accountType === 2,
    canViewReports: user?.accountType === 2
  };
};
