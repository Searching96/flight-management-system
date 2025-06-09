import { apiClient } from './api';
import { } from '../models/LoginResponse';
import { Account } from '../models/Account';

export class AccountService {
  private readonly baseUrl = '/accounts';

  async getAccountById(id: number): Promise<Account> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getAccountByEmail(email: string): Promise<Account> {
    return apiClient.get(`${this.baseUrl}/email/${email}`);
  }
}

export const accountService = new AccountService();
