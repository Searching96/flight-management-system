import { apiClient } from './api';
import {
  Employee,
  LoginRequest,
  LoginResponse,
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  ChangePasswordRequest
} from '../models';


import { API_URL } from './config';

class EmployeeService {
  private readonly baseUrl = API_URL.EMPLOYEES;

  // Authentication
  async login(loginData: LoginRequest): Promise<LoginResponse> {
    const response = await apiClient.post('/auth/employee/login', loginData);
    if (response.token) {
      localStorage.setItem('authToken', response.token);
    }
    return response;
  }

  async logout(): Promise<void> {
    localStorage.removeItem('authToken');
    await apiClient.post('/auth/logout');
  }

  async getCurrentEmployee(): Promise<Employee> {
    return apiClient.get(`${this.baseUrl}/current`);
  }

  // CRUD Operations
  async getAllEmployees(): Promise<Employee[]> {
    return apiClient.get(this.baseUrl);
  }

  async getEmployeeById(id: number): Promise<Employee> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async createEmployee(employeeData: CreateEmployeeRequest): Promise<Employee> {
    return apiClient.post(this.baseUrl, employeeData);
  }

  async updateEmployee(id: number, employeeData: UpdateEmployeeRequest): Promise<Employee> {
    return apiClient.put(`${this.baseUrl}/${id}`, employeeData);
  }

  async deleteEmployee(id: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async activateEmployee(id: number): Promise<Employee> {
    return apiClient.patch(`${this.baseUrl}/${id}/activate`);
  }

  async deactivateEmployee(id: number): Promise<Employee> {
    return apiClient.patch(`${this.baseUrl}/${id}/deactivate`);
  }

  // Password management
  async changePassword(passwordData: ChangePasswordRequest): Promise<void> {
    return apiClient.patch(`${this.baseUrl}/change-password`, passwordData);
  }

  async resetPassword(employeeId: number): Promise<string> {
    const response = await apiClient.patch(`${this.baseUrl}/${employeeId}/reset-password`);
    return response.temporaryPassword;
  }

  // Search and filter
  async searchEmployees(searchTerm: string): Promise<Employee[]> {
    return apiClient.get(`${this.baseUrl}/search`, { params: { q: searchTerm } });
  }

  async getEmployeesByDepartment(department: string): Promise<Employee[]> {
    return apiClient.get(`${this.baseUrl}/department/${department}`);
  }

  async getEmployeesByPosition(position: string): Promise<Employee[]> {
    return apiClient.get(`${this.baseUrl}/position/${position}`);
  }

  // Utility methods
  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }
}

export const employeeService = new EmployeeService();
export default employeeService;
