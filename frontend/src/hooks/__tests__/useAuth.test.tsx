import React from 'react'
import { renderHook, act } from '@testing-library/react'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { AuthProvider, useAuth } from '../useAuth'
import { accountService } from '../../services'
import { LoginResponse } from '../../models/Account'

// Mock the account service
vi.mock('../../services', () => ({
  accountService: {
    login: vi.fn(),
    register: vi.fn(),
  },
}))

const mockedAccountService = vi.mocked(accountService)

describe('useAuth', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // localStorage is already mocked in setup.ts
  })

  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <AuthProvider>{children}</AuthProvider>
  )

  it('should initialize with no user when no stored data', () => {
    // Arrange & Act
    const { result } = renderHook(() => useAuth(), { wrapper })

    // Assert
    expect(result.current.user).toBeNull()
    expect(result.current.loading).toBe(false)
  })

  it('should restore user from localStorage on initialization', () => {
    // Arrange
    const storedUser = {
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
    }
    vi.mocked(localStorage.getItem).mockReturnValue(JSON.stringify(storedUser))

    // Act
    const { result } = renderHook(() => useAuth(), { wrapper })

    // Assert
    expect(result.current.user).toEqual(storedUser)
    expect(localStorage.getItem).toHaveBeenCalledWith('userAccount')
  })

  it('should login successfully and store user data', async () => {
    // Arrange
    const mockLoginResponse = {
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
      token: 'mock-jwt-token',
    }
    mockedAccountService.login.mockResolvedValueOnce(mockLoginResponse)

    const { result } = renderHook(() => useAuth(), { wrapper })

    // Act
    await act(async () => {
      await result.current.login('john@example.com', 'password123')
    })

    // Assert
    expect(result.current.user).toEqual({
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
    })
    expect(localStorage.setItem).toHaveBeenCalledWith('authToken', 'mock-jwt-token')
    expect(localStorage.setItem).toHaveBeenCalledWith(
      'userAccount',
      JSON.stringify({
        accountId: 1,
        accountName: 'John Doe',
        email: 'john@example.com',
        accountType: 2,
      })
    )
  })
  it('should handle login without token', async () => {
    // Arrange
    const mockLoginResponse = {
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
      token: undefined,
    }
    mockedAccountService.login.mockResolvedValueOnce(mockLoginResponse)

    const { result } = renderHook(() => useAuth(), { wrapper })

    // Act
    await act(async () => {
      await result.current.login('john@example.com', 'password123')
    })

    // Assert
    expect(result.current.user).toEqual({
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
    })
    expect(localStorage.setItem).not.toHaveBeenCalledWith('authToken', expect.anything())
  })

  it('should handle login failure', async () => {
    // Arrange
    const errorMessage = 'Invalid credentials'
    mockedAccountService.login.mockRejectedValueOnce(new Error(errorMessage))

    const { result } = renderHook(() => useAuth(), { wrapper })

    // Act & Assert
    await act(async () => {
      await expect(result.current.login('john@example.com', 'wrongpassword')).rejects.toThrow(errorMessage)
    })

    expect(result.current.user).toBeNull()
    expect(localStorage.setItem).not.toHaveBeenCalled()
  })

  it('should register successfully', async () => {
    // Arrange
    const mockRegisterRequest = {
      accountName: 'Jane Doe',
      email: 'jane@example.com',
      password: 'password123',
      phoneNumber: '+1234567890',
      citizenId: '123456789',
      accountType: 2,
    }
    mockedAccountService.register.mockResolvedValueOnce(mockRegisterRequest)

    const { result } = renderHook(() => useAuth(), { wrapper })

    // Act
    await act(async () => {
      await result.current.register(mockRegisterRequest)
    })

    // Assert
    expect(mockedAccountService.register).toHaveBeenCalledWith(mockRegisterRequest)
  })

  it('should logout and clear stored data', () => {
    // Arrange
    const storedUser = {
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
    }
    vi.mocked(localStorage.getItem).mockReturnValue(JSON.stringify(storedUser))

    const { result } = renderHook(() => useAuth(), { wrapper })

    // Act
    act(() => {
      result.current.logout()
    })

    // Assert
    expect(result.current.user).toBeNull()
    expect(localStorage.removeItem).toHaveBeenCalledWith('userAccount')
    expect(localStorage.removeItem).toHaveBeenCalledWith('authToken')
  })

  it('should handle invalid stored user data', () => {
    // Arrange
    vi.mocked(localStorage.getItem).mockReturnValue('invalid-json')

    // Act
    const { result } = renderHook(() => useAuth(), { wrapper })

    // Assert
    expect(result.current.user).toBeNull()
    expect(result.current.loading).toBe(false)
  })

  it('should provide correct user role information', () => {
    // Arrange
    const adminUser = {
      accountId: 1,
      accountName: 'Admin User',
      email: 'admin@example.com',
      accountType: 1,
    }
    vi.mocked(localStorage.getItem).mockReturnValue(JSON.stringify(adminUser))

    // Act
    const { result } = renderHook(() => useAuth(), { wrapper })

    // Assert
    expect(result.current.user?.accountType).toBe(1)
  })

  it('should handle edge cases during authentication', async () => {
    // Arrange
    const mockLoginResponse = {
      accountId: 1,
      accountName: 'Test User',
      email: 'test@example.com',
      accountType: 2,
      token: '', // Empty token
    }
    mockedAccountService.login.mockResolvedValueOnce(mockLoginResponse)

    const { result } = renderHook(() => useAuth(), { wrapper })

    // Act
    await act(async () => {
      await result.current.login('test@example.com', 'password123')
    })

    // Assert
    expect(result.current.user).toEqual({
      accountId: 1,
      accountName: 'Test User',
      email: 'test@example.com',
      accountType: 2,
    })
    // Empty token should not be stored
    expect(localStorage.setItem).not.toHaveBeenCalledWith('authToken', '')
  })

  it('should handle network errors during login', async () => {
    // Arrange
    const networkError = new Error('Network Error')
    networkError.name = 'NetworkError'
    mockedAccountService.login.mockRejectedValueOnce(networkError)

    const { result } = renderHook(() => useAuth(), { wrapper })

    // Act & Assert
    await act(async () => {
      await expect(result.current.login('test@example.com', 'password123')).rejects.toThrow('Network Error')
    })

    expect(result.current.user).toBeNull()
    expect(result.current.loading).toBe(false)
  })

  it('should handle malformed JSON in localStorage gracefully', () => {
    // Arrange
    vi.mocked(localStorage.getItem).mockReturnValue('{"invalid":json}')

    // Act
    const { result } = renderHook(() => useAuth(), { wrapper })

    // Assert
    expect(result.current.user).toBeNull()
    expect(result.current.loading).toBe(false)
  })

  it('should handle registration errors', async () => {
    // Arrange
    const mockRegisterRequest = {
      accountName: 'Jane Doe',
      email: 'jane@example.com',
      password: 'password123',
      phoneNumber: '+1234567890',
      citizenId: '123456789',
      accountType: 2,
    }
    const registrationError = new Error('Email already exists')
    mockedAccountService.register.mockRejectedValueOnce(registrationError)

    const { result } = renderHook(() => useAuth(), { wrapper })

    // Act & Assert
    await act(async () => {
      await expect(result.current.register(mockRegisterRequest)).rejects.toThrow('Email already exists')
    })

    expect(mockedAccountService.register).toHaveBeenCalledWith(mockRegisterRequest)
  })
  it('should handle localStorage access errors gracefully', () => {
    // Arrange
    vi.mocked(localStorage.getItem).mockImplementation(() => {
      throw new Error('localStorage not available');
    });

    // Act
    const { result } = renderHook(() => useAuth(), { wrapper });

    // Assert
    expect(result.current.user).toBeNull();
    expect(result.current.loading).toBe(false);
  });

  it('should handle partial user data in localStorage', () => {
    // Arrange
    const partialUser = {
      accountId: 1,
      accountName: 'John Doe',
      // Missing email and accountType
    };
    vi.mocked(localStorage.getItem).mockReturnValue(JSON.stringify(partialUser));

    // Act
    const { result } = renderHook(() => useAuth(), { wrapper });

    // Assert
    expect(result.current.user).toEqual(partialUser);
  });

  it('should handle logout when not logged in', () => {
    // Arrange
    const { result } = renderHook(() => useAuth(), { wrapper });

    // Act
    act(() => {
      result.current.logout();
    });

    // Assert
    expect(result.current.user).toBeNull();
    expect(localStorage.removeItem).toHaveBeenCalledWith('userAccount');
    expect(localStorage.removeItem).toHaveBeenCalledWith('authToken');
  });

  it('should maintain user state across multiple operations', async () => {
    // Arrange
    const mockLoginResponse = {
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
      token: 'mock-jwt-token',
    };
    mockedAccountService.login.mockResolvedValueOnce(mockLoginResponse);

    const { result } = renderHook(() => useAuth(), { wrapper });

    // Act - Login
    await act(async () => {
      await result.current.login('john@example.com', 'password123');
    });

    // Assert user is logged in
    expect(result.current.user).toBeTruthy();
    expect(result.current.user?.id).toBe(1);

    // Act - Logout
    act(() => {
      result.current.logout();
    });

    // Assert user is logged out
    expect(result.current.user).toBeNull();
  });
 it('should handle concurrent login attempts', async () => {
  // Arrange
  const mockLoginResponse = {
    accountId: 1,
    accountName: 'John Doe',
    email: 'john@example.com',
    accountType: 2,
    token: 'mock-jwt-token',
  };
  
  let resolveLogin: (value: LoginResponse) => void;
  const loginPromise = new Promise<LoginResponse>((resolve) => {
    resolveLogin = resolve;
  });
  
  mockedAccountService.login.mockReturnValue(loginPromise);

  const { result } = renderHook(() => useAuth(), { wrapper });

    // Act - Start two concurrent login attempts
    const loginPromise1 = act(async () => {
      await result.current.login('john@example.com', 'password123');
    });
    
    const loginPromise2 = act(async () => {
      await result.current.login('john@example.com', 'password123');
    });

    // Resolve the mock login
    resolveLogin!(mockLoginResponse);

    // Wait for both to complete
    await Promise.all([loginPromise1, loginPromise2]);

    // Assert
    expect(result.current.user).toEqual({
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
    });
  });

  it('should handle authentication state during loading', async () => {
    // Arrange
    let resolveLogin: (value: any) => void;
    const loginPromise = new Promise((resolve) => {
      resolveLogin = resolve;
    });
    
    mockedAccountService.login.mockReturnValue(loginPromise);

    const { result } = renderHook(() => useAuth(), { wrapper });

    // Act - Start login
    const loginCall = act(async () => {
      await result.current.login('john@example.com', 'password123');
    });

    // Assert - Should be in loading state during login
    expect(result.current.loading).toBe(true);

    // Resolve login
    resolveLogin!({
      accountId: 1,
      accountName: 'John Doe',
      email: 'john@example.com',
      accountType: 2,
      token: 'mock-jwt-token',
    });

    await loginCall;

    // Assert - Should not be loading after login completes
    expect(result.current.loading).toBe(false);
  });

  it('should validate accountType values', () => {
    // Arrange - Admin user
    const adminUser = {
      accountId: 1,
      accountName: 'Admin User',
      email: 'admin@example.com',
      accountType: 1, // Admin
    };
    vi.mocked(localStorage.getItem).mockReturnValue(JSON.stringify(adminUser));

    const { result: adminResult } = renderHook(() => useAuth(), { wrapper });

    // Assert admin user
    expect(adminResult.current.user?.accountType).toBe(1);

    // Arrange - Customer user
    const customerUser = {
      accountId: 2,
      accountName: 'Customer User',
      email: 'customer@example.com',
      accountType: 2, // Customer
    };

    // Cleanup and re-render with customer
    vi.clearAllMocks();
    vi.mocked(localStorage.getItem).mockReturnValue(JSON.stringify(customerUser));

    const { result: customerResult } = renderHook(() => useAuth(), { wrapper });

    // Assert customer user
    expect(customerResult.current.user?.accountType).toBe(2);
  });
});