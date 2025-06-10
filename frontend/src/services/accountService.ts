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

  async verifyCurrentPassword(accountId: number, currentPassword: string): Promise<boolean> {
    return apiClient.post(`${this.baseUrl}/${accountId}/verify-password`, { currentPassword });
  }

  async resetPassword(data: { accountId: number; currentPassword: string; newPassword: string }): Promise<void> {
    return apiClient.post(`${this.baseUrl}/${data.accountId}/reset-password`, {
      currentPassword: data.currentPassword,
      newPassword: data.newPassword
    });
  }
}

export const accountService = new AccountService();
