import React, { createContext, useContext, useState, useEffect } from 'react';
import { Account, LoginRequest, RegisterRequest } from '../models';
import { accountService } from '../services';

interface AuthContextType {
  user: Account | null;
  login: (email: string, password: string) => Promise<void>;
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

  const login = async (email: string, password: string) => {
    try {
      setLoading(true);
      const response = await accountService.login({ email, password });
      
      // Map the response to match frontend Account interface
      const userAccount: Account = {
        accountId: response.user.id,
        accountName: response.user.accountName,
        email: response.user.email,
        accountType: response.user.accountType
      };
      
      setUser(userAccount);
      localStorage.setItem('authToken', response.token);
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

  const logout = () => {
    localStorage.removeItem('userAccount');
    setUser(null);
  };

  const value: AuthContextType = {
    user,
    loading,
    login,
    register,
    logout
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
