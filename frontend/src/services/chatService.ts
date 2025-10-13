import { apiClient } from "./api";
import { Chatbox, Message } from "../models";
import type { ApiResponse } from "../models/ApiResponse";

export class ChatService {
  private readonly chatboxUrl = "/chatboxes";
  private readonly messageUrl = "/messages";

  // Chatbox operations
  async getAllChatboxes(): Promise<ApiResponse<Chatbox[]>> {
    return apiClient.get(this.chatboxUrl);
  }

  async getAllChatboxesSortedByCustomerTime(): Promise<ApiResponse<Chatbox[]>> {
    return apiClient.get(`${this.chatboxUrl}/sorted-by-customer-time`);
  }

  async getAllChatboxesSortedByEmployeeSupportCount(): Promise<
    ApiResponse<Chatbox[]>
  > {
    return apiClient.get(`${this.chatboxUrl}/sorted-by-employee-support`);
  }

  async getAllChatboxesSortedByRecentActivity(): Promise<
    ApiResponse<Chatbox[]>
  > {
    return apiClient.get(`${this.chatboxUrl}/sorted-by-recent-activity`);
  }

  async getChatboxById(id: number): Promise<ApiResponse<Chatbox>> {
    return apiClient.get(`${this.chatboxUrl}/${id}`);
  }

  async deleteChatbox(id: number): Promise<ApiResponse<void>> {
    return apiClient.delete(`${this.chatboxUrl}/${id}`);
  }

  // Message operations
  async getMessagesByChatboxId(
    chatboxId: number
  ): Promise<ApiResponse<Message[]>> {
    return apiClient.get(`${this.messageUrl}/chatbox/${chatboxId}`);
  }

  async createCustomerMessage(
    chatboxId: number,
    content: string
  ): Promise<ApiResponse<Message>> {
    return apiClient.post(`${this.messageUrl}/customer`, {
      chatboxId,
      content,
    });
  }

  async getChatboxByCustomerId(
    customerId: number
  ): Promise<ApiResponse<Chatbox>> {
    return apiClient.get(`${this.chatboxUrl}/customer/${customerId}/chatbox`);
  }

  // Employee chat management methods
  async getChatboxesByEmployeeId(
    employeeId: number
  ): Promise<ApiResponse<Chatbox[]>> {
    return apiClient.get(`/api/chatboxes/employee/${employeeId}`);
  }

  async createEmployeeMessage(
    chatboxId: number,
    content: string
  ): Promise<ApiResponse<Message>> {
    // Note: employeeId should be passed from component
    return apiClient.post(`${this.messageUrl}/employee`, {
      chatboxId,
      content,
    });
  }
}

export const chatService = new ChatService();
