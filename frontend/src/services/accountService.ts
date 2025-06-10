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

  async updateAccount(id: number, account: Partial<Account>): Promise<Account> {
    return apiClient.put(`${this.baseUrl}/${id}`, account);
  }
}

export const accountService = new AccountService();
