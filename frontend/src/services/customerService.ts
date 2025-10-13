import { apiClient } from "./api";
import { ApiResponse, Customer } from "../models";

export class CustomerService {
  private readonly customerUrl = "/customers";

  // Customer operations
  async getAllCustomers(): Promise<ApiResponse<Customer[]>> {
    return apiClient.get(this.customerUrl);
  }

  async getCustomerScore(customerId: number): Promise<ApiResponse<number>> {
    return apiClient.get(`${this.customerUrl}/${customerId}/score`);
  }

  async getCustomerById(customerId: number): Promise<ApiResponse<Customer>> {
    return apiClient.get(`${this.customerUrl}/${customerId}`);
  }

  async getCustomerByEmail(email: string): Promise<ApiResponse<Customer>> {
    return apiClient.get(`${this.customerUrl}/email/${email}`);
  }

  async createCustomer(
    customer: Omit<Customer, "customerId">
  ): Promise<ApiResponse<Customer>> {
    return apiClient.post(this.customerUrl, customer);
  }

  async updateCustomer(
    id: number,
    customer: Partial<Customer>
  ): Promise<ApiResponse<Customer>> {
    return apiClient.put(`${this.customerUrl}/${id}`, customer);
  }

  async updateCustomerScore(
    id: number,
    score: number
  ): Promise<ApiResponse<void>> {
    return apiClient.put(`${this.customerUrl}/${id}/score/${score}`);
  }

  async deleteCustomer(id: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.customerUrl}/${id}`);
  }

  async searchCustomers(query: string): Promise<ApiResponse<Customer[]>> {
    try {
      const response = await apiClient.get(`${this.customerUrl}/search`, {
        params: { q: query },
      });
      return response.data;
    } catch (error) {
      console.error("Error searching customers:", error);
      throw error;
    }
  }
}

export const customerService = new CustomerService();
