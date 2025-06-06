// import { apiClient } from './api';
// import { Chatbox, Message, SendMessageRequest } from '../models';

// export class ChatService {
//   private readonly chatboxUrl = '/chatboxes';
//   private readonly messageUrl = '/messages';

//   // Chatbox operations
//   async getAllChatboxes(): Promise<Chatbox[]> {
//     return apiClient.get(this.chatboxUrl);
//   }

//   async getChatboxById(id: number): Promise<Chatbox> {
//     return apiClient.get(`${this.chatboxUrl}/${id}`);
//   }

//   async getChatboxesByCustomerId(customerId: number): Promise<Chatbox[]> {
//     return apiClient.get(`${this.chatboxUrl}/customer/${customerId}`);
//   }

//   async getChatboxesByEmployeeId(employeeId: number): Promise<Chatbox[]> {
//     return apiClient.get(`${this.chatboxUrl}/employee/${employeeId}`);
//   }

//   async getOrCreateChatbox(customerId: number, employeeId: number): Promise<Chatbox> {
//     return apiClient.post(`${this.chatboxUrl}/get-or-create`, { customerId, employeeId });
//   }

//   async deleteChatbox(id: number): Promise<void> {
//     return apiClient.delete(`${this.chatboxUrl}/${id}`);
//   }

//   // Message operations
//   async getMessagesByChatboxId(chatboxId: number): Promise<Message[]> {
//     return apiClient.get(`${this.messageUrl}/chatbox/${chatboxId}`);
//   }

//   async getRecentMessages(chatboxId: number, limit: number = 20): Promise<Message[]> {
//     return apiClient.get(`${this.messageUrl}/chatbox/${chatboxId}/recent`, { limit });
//   }

//   async sendMessage(messageData: SendMessageRequest): Promise<Message> {
//     return apiClient.post(this.messageUrl, messageData);
//   }

//   async sendQuickMessage(chatboxId: number, content: string, messageType: number): Promise<Message> {
//     return apiClient.post(`${this.messageUrl}/send`, { chatboxId, content, messageType });
//   }
// }

// export const chatService = new ChatService();
