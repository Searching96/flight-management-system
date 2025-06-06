import { apiClient } from './api';
import {  } from '../models/LoginResponse';
import { Account, RegisterRequest, LoginResponse } from '../models/Account';

export class AccountService {
  private readonly baseUrl = '/accounts';  async login(credentials: { email: string; password: string }): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>(`${this.baseUrl}/login`, credentials);
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
