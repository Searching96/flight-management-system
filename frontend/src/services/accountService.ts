import { apiClient } from './api';
import { LoginResponseDto } from '../models/LoginResponse';
import { Account, LoginRequest, RegisterRequest } from '../models/Account';

export class AccountService {
  private readonly baseUrl = '/accounts';

  async login(credentials: { email: string; password: string }): Promise<LoginResponseDto> {
    const response = await apiClient.post<LoginResponseDto>(`${this.baseUrl}/login`, credentials);
    return {
      token: response.token || '',
      user: {
        id: response.accountId!,
        email: response.email!,
        accountName: response.accountName!,
        accountType: response.accountType!
      }
    };
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
