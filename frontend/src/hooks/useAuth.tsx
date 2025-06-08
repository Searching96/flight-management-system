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
  // Store the access token in memory instead of localStorage
  const [accessToken, setAccessToken] = useState<string | null>(null);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  // Function to get the access token for API calls
  // const getAccessToken = () => accessToken;

  const checkAuthStatus = async () => {
    try {
      // Check for stored user account first
      const storedUser = sessionStorage.getItem('userAccount');
      if (storedUser) {
        const userData = JSON.parse(storedUser);
        setUser(userData);
        
        // // Try to refresh the token if we have stored user data
        // try {
        //   const refreshResult = await accountService.refreshToken();
        //   if (refreshResult?.token) {
        //     setAccessToken(refreshResult.token);
        //   }
        // } catch (refreshError) {
        //   // If refresh fails, clear user data and require re-login
        //   sessionStorage.removeItem('userAccount');
        //   setUser(null);
        // }
      }
    } catch (error) {
      sessionStorage.removeItem('userAccount');
      setUser(null);
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
      
      // Store token in memory instead of localStorage
      if (response.token) {
        setAccessToken(response.token);
      }
      
      // Store minimal user information in sessionStorage instead of localStorage
      // (sessionStorage is cleared when the tab is closed)
      sessionStorage.setItem('userAccount', JSON.stringify(userAccount));
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
      if (accessToken) {
        await logout();
      }
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      // Clear memory and storage regardless of API result
      sessionStorage.removeItem('userAccount');
      setAccessToken(null);
      setUser(null);
    }
  };

  const value: AuthContextType = {
    user,
    loading,
    login,
    register,
    logout,
    // getAccessToken  // Add this function to the context
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

// Fix permission checking to match database schema
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
