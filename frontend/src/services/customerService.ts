import { apiClient } from './api';
import { Customer, Employee } from '../models';

export class CustomerService {
  private readonly customerUrl = '/customers';
  private readonly employeeUrl = '/employees';

  // Customer operations
  async getAllCustomers(): Promise<Customer[]> {
    try {
      const response = await apiClient.get(this.customerUrl);
      return response.data;
    } catch (error) {
      console.error('Error fetching customers:', error);
      throw error;
    }
  }

  async getCustomerScore(customerId: number): Promise<number> {
    return apiClient.get(`${this.customerUrl}/${customerId}/score`);
  }

  async getCustomerById(customerId: number): Promise<Customer> {
    return apiClient.get(`${this.customerUrl}/${customerId}`);
  }

  async getCustomerByEmail(email: string): Promise<Customer> {
    return apiClient.get(`${this.customerUrl}/email/${email}`);
  }

  async createCustomer(customer: Omit<Customer, 'customerId'>): Promise<Customer> {
    return apiClient.post(this.customerUrl, customer);
  }

  async updateCustomer(id: number, customer: Partial<Customer>): Promise<Customer> {
    return apiClient.put(`${this.customerUrl}/${id}`, customer);
  }

  async updateCustomerScore(id: number, score: number): Promise<void> {
    return apiClient.put(`${this.customerUrl}/${id}/score/${score}`);
  }

  async deleteCustomer(id: number): Promise<void> {
    return apiClient.delete(`${this.customerUrl}/${id}`);
  }

  async searchCustomers(query: string): Promise<Customer[]> {
    try {
      const response = await apiClient.get(`${this.customerUrl}/search`, {
        params: { q: query }
      });
      return response.data;
    } catch (error) {
      console.error('Error searching customers:', error);
      throw error;
    }
  }

  // Employee operations
  async getAllEmployees(): Promise<Employee[]> {
    return apiClient.get(this.employeeUrl);
  }

  async getEmployeeById(id: number): Promise<Employee> {
    return apiClient.get(`${this.employeeUrl}/${id}`);
  }

  async getEmployeeByEmail(email: string): Promise<Employee> {
    return apiClient.get(`${this.employeeUrl}/email/${email}`);
  }

  async getEmployeesByType(type: number): Promise<Employee[]> {
    return apiClient.get(`${this.employeeUrl}/type/${type}`);
  }

  async createEmployee(employee: Omit<Employee, 'employeeId'>): Promise<Employee> {
    return apiClient.post(this.employeeUrl, employee);
  }

  async updateEmployee(id: number, employee: Partial<Employee>): Promise<Employee> {
    return apiClient.put(`${this.employeeUrl}/${id}`, employee);
  }

  async deleteEmployee(id: number): Promise<void> {
    return apiClient.delete(`${this.employeeUrl}/${id}`);
  }
}

export const customerService = new CustomerService();
