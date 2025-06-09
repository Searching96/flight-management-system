import axios, { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { AuthResponse } from '../models';
import { DOMAIN_URL_DEFAULT } from './config';

class ApiClient {
  private baseUrl = `${DOMAIN_URL_DEFAULT}/api`;
  private client: AxiosInstance;
  private isRefreshing = false;
  private refreshSubscribers: ((token: string) => void)[] = [];

  constructor() {
    this.client = axios.create({
      baseURL: this.baseUrl,
      withCredentials: true,
    });

    this.setupInterceptors();
  }

  private setAuthData(data: AuthResponse) {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('user', JSON.stringify(data.userDetails));
  }

  private clearAuth() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }

  // Update response interceptor
  private setupInterceptors() {
    // Request interceptor
    this.client.interceptors.request.use(
      (config) => {
        const accessToken = localStorage.getItem('accessToken');
        if (accessToken) {
          config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    this.client.interceptors.response.use(
      (response) => response.data,
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const newToken = await this.handleTokenRefresh();
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return this.client(originalRequest);
          } catch (refreshError) {
            this.clearAuth();
            window.location.href = '/login';
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  }

  private async handleTokenRefresh() {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) throw new Error('No refresh token available');

      const response = await axios.post<AuthResponse>(
        `${this.baseUrl}/auth/refresh`,
        { refreshToken }
      );

      this.setAuthData(response.data);
      return response.data.accessToken;
    } catch (error) {
      this.clearAuth();
      throw error;
    }
  }

  // Existing HTTP methods
  async get<T = any>(url: string, options?: { params?: any }): Promise<T> {
    return this.client.get(url, options);
  }

  async post<T = any>(url: string, data?: any): Promise<T> {
    return this.client.post(url, data);
  }

  async put<T = any>(url: string, data?: any): Promise<T> {
    return this.client.put(url, data);
  }

  async patch<T = any>(url: string, data?: any): Promise<T> {
    return this.client.patch(url, data);
  }

  async delete<T = any>(url: string, config?: any): Promise<T> {
    return this.client.delete(url, config);
  }
}

export const apiClient = new ApiClient();
