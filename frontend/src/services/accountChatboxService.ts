import { apiClient } from "./api";
import type { AccountChatbox } from "../models/AccountChatBox";
import type { ApiResponse } from "../models";

export class AccountChatboxService {
  private readonly baseUrl = "/account-chatbox";

  async updateLastVisitTime(
    accountId: number,
    chatboxId: number
  ): Promise<void> {
    return apiClient.put(
      `${this.baseUrl}/account/${accountId}/chatbox/${chatboxId}/visit`
    );
  }

  async getUnreadMessageCount(
    accountId: number,
    chatboxId: number
  ): Promise<number> {
    const response = await apiClient.get<ApiResponse<number>>(
      `${this.baseUrl}/account/${accountId}/chatbox/${chatboxId}/unread-count`
    );
    return response.data;
  }

  async getUnreadCountsForAllChatboxes(
    accountId: number
  ): Promise<Record<number, number>> {
    const response = await apiClient.get<ApiResponse<Record<number, number>>>(`${this.baseUrl}/account/${accountId}/unread-counts`);
    return response.data;
  }

  async getAccountChatboxesByAccountId(
    accountId: number
  ): Promise<AccountChatbox[]> {
    const response = await apiClient.get<ApiResponse<AccountChatbox[]>>(`${this.baseUrl}/account/${accountId}`);
    return response.data;
  }
}

export const accountChatboxService = new AccountChatboxService();
