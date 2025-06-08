import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { DOMAIN_URL_DEFAULT } from './config';

class ApiClient {
  private baseUrl = `${DOMAIN_URL_DEFAULT}/api`;
  private client: AxiosInstance;
  private authContext: any;

  constructor() {
    this.client = axios.create({
      baseURL: this.baseUrl,
      withCredentials: true, // For session cookies
    });

    this.setupInterceptors();
  }

  setupInterceptors() {
    // Request interceptor - simplified to just include credentials
    this.client.interceptors.request.use(
      (config) => {
        // Get user info from localStorage (simplified approach)
        const userAccount = localStorage.getItem('userAccount');

        if (userAccount) {
          // Just for development - add user type as a header
          const user = JSON.parse(userAccount);
          config.headers['X-User-Type'] = user.accountType === 1 ? 'CUSTOMER' : 'EMPLOYEE';
        }

        // Ensure CORS headers are properly handled
        config.withCredentials = true;

        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor - simplified error handling
    this.client.interceptors.response.use(
      (response: AxiosResponse) => response.data,
      (error) => {
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
      }
    );
  }

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

  setAuthContext(authContext: any) {
    this.authContext = authContext;
  }
}

export const apiClient = new ApiClient();
