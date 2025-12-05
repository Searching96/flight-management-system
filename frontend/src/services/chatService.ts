import { apiClient } from "./api";
import { Chatbox, Message } from "../models";
import type { ApiResponse } from "../models/ApiResponse";

export class ChatService {
  private readonly chatboxUrl = "/chatboxes";
  private readonly messageUrl = "/messages";

  // Chatbox operations
  async getAllChatboxes(): Promise<Chatbox[]> {
    const response = await apiClient.get<ApiResponse<Chatbox[]>>(this.chatboxUrl);
    return response.data;
  }

  async getAllChatboxesSortedByCustomerTime(): Promise<Chatbox[]> {
    const response = await apiClient.get<ApiResponse<Chatbox[]>>(`${this.chatboxUrl}/sorted-by-customer-time`);
    return response.data;
  }

  async getAllChatboxesSortedByEmployeeSupportCount(): Promise<Chatbox[]> {
    const response = await apiClient.get<ApiResponse<Chatbox[]>>(`${this.chatboxUrl}/sorted-by-employee-support`);
    return response.data;
  }

  async getAllChatboxesSortedByRecentActivity(): Promise<Chatbox[]> {
    const response = await apiClient.get<ApiResponse<Chatbox[]>>(`${this.chatboxUrl}/sorted-by-recent-activity`);
    return response.data;
  }

  async getChatboxById(id: number): Promise<Chatbox> {
    const response = await apiClient.get<ApiResponse<Chatbox>>(`${this.chatboxUrl}/${id}`);
    return response.data;
  }

  async deleteChatbox(id: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`${this.chatboxUrl}/${id}`);
  }

  // Message operations
  async getMessagesByChatboxId(chatboxId: number): Promise<Message[]> {
    const response = await apiClient.get<ApiResponse<Message[]>>(`${this.messageUrl}/chatbox/${chatboxId}`);
    return response.data;
  }

  async createCustomerMessage(
    chatboxId: number,
    content: string
  ): Promise<Message> {
    const response = await apiClient.post<ApiResponse<Message>>(`${this.messageUrl}/customer`, {
      chatboxId,
      content,
    });
    return response.data;
  }

  async getChatboxByCustomerId(customerId: number): Promise<Chatbox> {
    const response = await apiClient.get<ApiResponse<Chatbox>>(`${this.chatboxUrl}/customer/${customerId}/chatbox`);
    return response.data;
  }

  // Employee chat management methods
  async getChatboxesByEmployeeId(employeeId: number): Promise<Chatbox[]> {
    const response = await apiClient.get<ApiResponse<Chatbox[]>>(`/api/chatboxes/employee/${employeeId}`);
    return response.data;
  }

  async createEmployeeMessage(
    chatboxId: number,
    content: string
  ): Promise<Message> {
    // Note: employeeId should be passed from component
    const response = await apiClient.post<ApiResponse<Message>>(`${this.messageUrl}/employee`, {
      chatboxId,
      content,
    });
    return response.data;
  }
}

export const chatService = new ChatService();
