import { apiClient } from './api';
import { Chatbox, Message } from '../models';

export class ChatService {
  private readonly chatboxUrl = '/chatboxes';
  private readonly messageUrl = '/messages';

  // Chatbox operations
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
}

export const chatService = new ChatService();
