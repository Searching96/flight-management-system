import { apiClient } from './api';
import {
  Employee,
  UpdateEmployeeRequest,
} from '../models';

import { API_URL } from './config';

class EmployeeService {
  private readonly baseUrl = API_URL.EMPLOYEES;

  async getCurrentEmployee(): Promise<Employee> {
    console.log('Getting current employee info at 2025-06-11 08:01:57 UTC by thinh0704hcm');
    return apiClient.get(`${this.baseUrl}/current`);
  }

  async getAllEmployees(): Promise<Employee[]> {
    console.log('Getting all employees at 2025-06-11 08:01:57 UTC by thinh0704hcm');
    return apiClient.get(this.baseUrl);
  }

  async getEmployeeById(id: number): Promise<Employee> {
    console.log(`Getting employee by ID: ${id} at 2025-06-11 08:01:57 UTC by thinh0704hcm`);
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async updateEmployee(id: number, employeeData: UpdateEmployeeRequest): Promise<Employee> {
    console.log(`Updating employee ID: ${id} at 2025-06-11 08:01:57 UTC by thinh0704hcm`);
    return apiClient.put(`${this.baseUrl}/${id}`, employeeData);
  }

  async deleteEmployee(id: number): Promise<void> {
    console.log(`Deleting employee ID: ${id} at 2025-06-11 08:01:57 UTC by thinh0704hcm`);
    return apiClient.delete(`${this.baseUrl}/${id}`);
  }

  async activateEmployee(id: number): Promise<Employee> {
    console.log(`Activating employee ID: ${id} at 2025-06-11 08:01:57 UTC by thinh0704hcm`);
    return apiClient.patch(`${this.baseUrl}/${id}/activate`);
  }

  async deactivateEmployee(id: number): Promise<Employee> {
    console.log(`Deactivating employee ID: ${id} at 2025-06-11 08:01:57 UTC by thinh0704hcm`);
    return apiClient.patch(`${this.baseUrl}/${id}/deactivate`);
  }

  async updateEmployeeRole(id: number, newRole: number): Promise<Employee> {
    console.log(`Updating role for employee ID: ${id} to role: ${newRole} at 2025-06-11 08:01:57 UTC by thinh0704hcm`);
    return apiClient.put(`${this.baseUrl}/${id}/role?newRole=${newRole}`);
  }
}

export const employeeService = new EmployeeService();
export default employeeService;