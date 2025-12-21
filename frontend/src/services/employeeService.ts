import { apiClient } from "./api";
import { Employee, UpdateEmployeeRequest } from "../models";
import { API_URL } from "./config";
import type { ApiResponse } from "../models/ApiResponse";
import type { PaginatedResponse } from "../models";

class EmployeeService {
  private readonly baseUrl = API_URL.EMPLOYEES;

  async getCurrentEmployee(): Promise<ApiResponse<Employee>> {
    return apiClient.get(`${this.baseUrl}/current`);
  }

  async getAllEmployees(): Promise<ApiResponse<Employee[]>> {
    return apiClient.get(this.baseUrl);
  }

  async getAllEmployeesPaged(
    page: number = 0,
    size: number = 10
  ): Promise<ApiResponse<PaginatedResponse<Employee>>> {
    return apiClient.get(`${this.baseUrl}?page=${page}&size=${size}`);
  }

  async getEmployeeById(id: number): Promise<ApiResponse<Employee>> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async updateEmployee(
    id: number,
    employeeData: UpdateEmployeeRequest
  ): Promise<ApiResponse<Employee>> {
    return apiClient.put(`${this.baseUrl}/${id}`, employeeData);
  }

  async deleteEmployee(id: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async activateEmployee(id: number): Promise<ApiResponse<Employee>> {
    return apiClient.patch(`${this.baseUrl}/${id}/activate`);
  }

  async deactivateEmployee(id: number): Promise<ApiResponse<Employee>> {
    return apiClient.patch(`${this.baseUrl}/${id}/deactivate`);
  }

  async updateEmployeeRole(
    id: number,
    newRole: number
  ): Promise<ApiResponse<Employee>> {
    return apiClient.put(`${this.baseUrl}/${id}/role?newRole=${newRole}`);
  }
}

export const employeeService = new EmployeeService();
export default employeeService;
