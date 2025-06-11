import { apiClient } from './api';

export interface AccountChatboxDto {
  accountId: number;
  chatboxId: number;
  lastVisitTime: string;
  unreadCount?: number;
}

export class AccountChatboxService {
  private readonly baseUrl = '/account-chatbox';

  async updateLastVisitTime(accountId: number, chatboxId: number): Promise<void> {
    return apiClient.put(`${this.baseUrl}/account/${accountId}/chatbox/${chatboxId}/visit`);
  }

  async getUnreadMessageCount(accountId: number, chatboxId: number): Promise<number> {
    return apiClient.get(`${this.baseUrl}/account/${accountId}/chatbox/${chatboxId}/unread-count`);
  }

  async getUnreadCountsForAllChatboxes(accountId: number): Promise<Record<number, number>> {
    return apiClient.get(`${this.baseUrl}/account/${accountId}/unread-counts`);
  }

  async getAccountChatboxesByAccountId(accountId: number): Promise<AccountChatboxDto[]> {
    return apiClient.get(`${this.baseUrl}/account/${accountId}`);
  }
}

export const accountChatboxService = new AccountChatboxService();
