import { apiClient } from "./api";
import { ApiResponse, Customer } from "../models";

export class CustomerService {
  private readonly customerUrl = "/customers";

  // Customer operations
  async getAllCustomers(): Promise<Customer[]> {
    const response = await apiClient.get<ApiResponse<Customer[]>>(this.customerUrl);
    return response.data;
  }

  async getCustomerScore(customerId: number): Promise<number> {
    const response = await apiClient.get<ApiResponse<number>>(`${this.customerUrl}/${customerId}/score`);
    return response.data;
  }

  async getCustomerById(customerId: number): Promise<Customer> {
    const response = await apiClient.get<ApiResponse<Customer>>(`${this.customerUrl}/${customerId}`);
    return response.data;
  }

  async getCustomerByEmail(email: string): Promise<Customer> {
    const response = await apiClient.get<ApiResponse<Customer>>(`${this.customerUrl}/email/${email}`);
    return response.data;
  }

  async createCustomer(
    customer: Omit<Customer, "customerId">
  ): Promise<Customer> {
    const response = await apiClient.post<ApiResponse<Customer>>(this.customerUrl, customer);
    return response.data;
  }

  async updateCustomer(
    id: number,
    customer: Partial<Customer>
  ): Promise<Customer> {
    const response = await apiClient.put<ApiResponse<Customer>>(`${this.customerUrl}/${id}`, customer);
    return response.data;
  }

  async updateCustomerScore(
    id: number,
    score: number
  ): Promise<void> {
    await apiClient.put<ApiResponse<void>>(`${this.customerUrl}/${id}/score/${score}`);
  }

  async deleteCustomer(id: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`${this.customerUrl}/${id}`);
  }

  async searchCustomers(query: string): Promise<Customer[]> {
    try {
      const response = await apiClient.get<ApiResponse<Customer[]>>(`${this.customerUrl}/search`, {
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
