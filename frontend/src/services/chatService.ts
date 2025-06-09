import { apiClient } from './api';
import { Chatbox, CreateTestChatboxRequest, Message, SendMessageRequest } from '../models';

export class ChatService {
  private readonly chatboxUrl = '/chatboxes';
  private readonly messageUrl = '/messages';

  // Chatbox operations
  async createChatboxTest(chatboxData: CreateTestChatboxRequest): Promise<CreateTestChatboxRequest> {
    return apiClient.post(`${this.chatboxUrl}/test`, chatboxData);
  }

  async getAllChatboxes(): Promise<Chatbox[]> {
    return apiClient.get(this.chatboxUrl);
  }

  async getChatboxById(id: number): Promise<Chatbox> {
    return apiClient.get(`${this.chatboxUrl}/${id}`);
  }

  async deleteChatbox(id: number): Promise<void> {
    return apiClient.delete(`${this.chatboxUrl}/${id}`);
  }

  // Message operations
  async getMessagesByChatboxId(chatboxId: number): Promise<Message[]> {
    return apiClient.get(`${this.messageUrl}/chatbox/${chatboxId}`);
  }

  // async getRecentMessages(chatboxId: number, limit: number = 20): Promise<Message[]> {
  //   return apiClient.get(`${this.messageUrl}/chatbox/${chatboxId}/recent`, { limit });
  // }

  async sendMessage(messageData: SendMessageRequest): Promise<Message> {
    return apiClient.post(this.messageUrl, messageData);
  }

  async sendQuickMessage(chatboxId: number, content: string, messageType: number): Promise<Message> {
    return apiClient.post(`${this.messageUrl}/send`, { chatboxId, content, messageType });
  }

  async createCustomerMessage(chatboxId: number, content: string): Promise<Message> {
    return apiClient.post(`${this.messageUrl}/customer`, { 
      chatboxId, 
      content 
    });
  }

  async getChatboxByCustomerId(customerId: number): Promise<Chatbox> {
    return apiClient.get(`${this.chatboxUrl}/customer/${customerId}/chatbox`);
  }

  // Employee chat management methods
  async getChatboxesByEmployeeId(employeeId: number): Promise<Chatbox[]> {
    return apiClient.get(`/api/chatboxes/employee/${employeeId}`);
  }

  async createEmployeeMessage(chatboxId: number, content: string): Promise<Message> {
    // Note: employeeId should be passed from component
    return apiClient.post(`${this.messageUrl}/employee`, { 
      chatboxId, 
      content 
    });
  }

  async markChatboxAsRead(chatboxId: number): Promise<void> {
    return apiClient.patch(`${this.chatboxUrl}/${chatboxId}/read`);
  }

  async getUnassignedChatboxes(): Promise<Chatbox[]> {
    return apiClient.get('/api/chatboxes/unassigned');
  }
}

export const chatService = new ChatService();
