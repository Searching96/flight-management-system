import { apiClient } from './api';
import { } from '../models/LoginResponse';
import { Account, LoginRequest, RegisterRequest, LoginResponse } from '../models/Account';

export class AccountService {
  private readonly baseUrl = '/auth';

  async login(loginRequest: LoginRequest): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>(`${this.baseUrl}/login`, loginRequest);
    return response;
  }

  async register(registerRequest: RegisterRequest): Promise<Account> {
    return apiClient.post(`${this.baseUrl}/register`, registerRequest);
  }

  async getAccountById(id: number): Promise<Account> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getAccountByEmail(email: string): Promise<Account> {
    return apiClient.get(`${this.baseUrl}/email/${email}`);
  }
}

export const accountService = new AccountService();
