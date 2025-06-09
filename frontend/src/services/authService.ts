// services/authService.ts
import { apiClient } from './api';
import { LoginRequest, AuthResponse, UserDetails } from '../models';

class AuthService {
  async login(credentials: LoginRequest): Promise<UserDetails> {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    localStorage.setItem('accessToken', response.accessToken);
    localStorage.setItem('refreshToken', response.refreshToken);
    localStorage.setItem('user', JSON.stringify(response.userDetails));
    return response.userDetails;
  }

  // Add silent refresh method
  async silentRefresh(): Promise<void> {
    await apiClient.post('/auth/refresh');
  }

  // Refresh access token
  async refreshToken(): Promise<AuthResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) throw new Error('No refresh token available');

    try {
      const response = await apiClient.post<AuthResponse>('/auth/refresh', { refreshToken });
      this.setAuthData(response);
      return response;
    } catch (error) {
      this.clearAuthData();
      throw error;
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
    apiClient.post('/auth/logout');
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
}

export const authService = new AuthService();