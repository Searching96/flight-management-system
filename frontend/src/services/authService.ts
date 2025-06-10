// services/authService.ts
import { apiClient } from './api';
import { LoginRequest, AuthResponse, UserDetails, RegisterRequest } from '../models';

class AuthService {
  async login(credentials: LoginRequest): Promise<void> {
    try {
      const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
      if (!response.userDetails) {
        throw new Error('Invalid response from server');
      }
      else this.setAuthData(response);
    } catch (error) {
      // Optionally transform or log the error
      // console.error('Login failed:', error);
      throw new Error('Login failed. Please check your credentials.');
    }
  }

  async register(userData: RegisterRequest): Promise<void> {
    try {
      const response = await apiClient.post<AuthResponse>('/auth/register', userData);
      if (!response.userDetails) {
        throw new Error('Invalid response from server');
      }
      else this.setAuthData(response);
    } catch (error) {
      // console.error('Registration failed:', error);
      throw new Error('Registration failed. Email may already be in use.');
    }
  }


  // Refresh access token
  async refreshToken(): Promise<void> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) throw new Error('No refresh token available');

    try {
      // Send as { refreshToken: refreshToken } if backend expects it
      const response = await apiClient.post<AuthResponse>(
        '/auth/refresh',
        { token: refreshToken }
      );
      if (!response.userDetails) {
        throw new Error('Invalid response from server');
      }
      else this.setAuthData(response);
    } catch (error) {
      this.clearAuthData();
      // console.error('Token refresh failed:', error);
      throw new Error('Session expired. Please log in again.');
    }
  }


  // Get current user details
  getCurrentUser(): UserDetails | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  // Logout user
  logout(): void {
    this.clearAuthData();
    // Optional: Add API call to invalidate token on server
  }

  // Store auth data in localStorage
  private setAuthData(data: AuthResponse): void {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('user', JSON.stringify(data.userDetails));
  }

  // Clear auth data from localStorage
  private clearAuthData(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }

  async validateResetToken(passwordResetToken: string): Promise<boolean> {
    try {
      const response = await apiClient.post<boolean>('/auth/validate',
        { token: passwordResetToken });

      return response;

    } catch (error) {
      // console.error('Token validation failed:', error);
      return false;
    }
  }

  async forgetPassword(email: string): Promise<void> {
    await apiClient.post('/auth/forget-password', { email })
      .then(() => {
        // console.log('Password reset email sent successfully');
      })
      .catch(error => {
        // console.error('Error sending password reset email:', error);
        throw error;
      });
  }

  async resetPassword(token: string, newPassword: string): Promise<void> {
    try {
      const response = await apiClient.post<AuthResponse>(
        '/auth/reset-password',
        { token, newPassword }
      );
      if (!response.userDetails) {
        throw new Error('Invalid response from server');
      }
      else this.setAuthData(response); // Only if the backend returns auth data
    } catch (error) {
      console.error('Password reset failed:', error);
      throw new Error('Password reset failed. Invalid or expired token.');
    }
  }

}

export const authService = new AuthService();