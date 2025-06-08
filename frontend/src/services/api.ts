import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { DOMAIN_URL_DEFAULT } from './config';

class ApiClient {
  private baseUrl = `${DOMAIN_URL_DEFAULT}/api`;
  private client: AxiosInstance;
  private authContext: any = null;

  constructor() {
    this.client = axios.create({
      baseURL: this.baseUrl,
      withCredentials: true, // Enable cookies for all requests
    });

    this.setupInterceptors();
  }

  // Set the auth context after the app is initialized
  setAuthContext(context: any) {
    this.authContext = context;
  }

  setupInterceptors() {
    // Request interceptor - Add auth token to requests
    this.client.interceptors.request.use(
      (config) => {
        // Get token from authContext instead of localStorage
        const token = this.authContext?.getAccessToken?.() || null;
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor
    this.client.interceptors.response.use(
      (response: AxiosResponse) => response.data,
      (error) => {
        console.error('API Error:', error.response?.data || error.message);
        if (error.response?.status === 401) {
          localStorage.removeItem('authToken');
          window.location.href = '/login';
        }
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
}

export const apiClient = new ApiClient();
