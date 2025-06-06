// import { apiClient } from './api';
// import { 
//   Message, 
//   SendMessageRequest
// } from '../models';

// export class MessageService {
//   private readonly baseUrl = '/messages';

//   // Message CRUD operations
//   async getAllMessages(): Promise<Message[]> {
//     return apiClient.get(this.baseUrl);
//   }

//   async getMessageById(id: number): Promise<Message> {
//     return apiClient.get(`${this.baseUrl}/${id}`);
//   }

//   async getMyMessages(filters?: MessageFilter): Promise<Message[]> {
//     return apiClient.get(`${this.baseUrl}/my-messages`, { params: filters });
//   }

//   async getSentMessages(): Promise<Message[]> {
//     return apiClient.get(`${this.baseUrl}/sent`);
//   }

//   async getUnreadMessages(): Promise<Message[]> {
//     return apiClient.get(`${this.baseUrl}/unread`);
//   }

//   async getUnreadCount(): Promise<number> {
//     const response = await apiClient.get(`${this.baseUrl}/unread/count`);
//     return response.count;
//   }

//   async sendMessage(messageData: SendMessageRequest): Promise<Message> {
//     return apiClient.post(this.baseUrl, messageData);
//   }

//   async markAsRead(messageId: number): Promise<void> {
//     return apiClient.patch(`${this.baseUrl}/${messageId}/read`);
//   }

//   async markAllAsRead(): Promise<void> {
//     return apiClient.patch(`${this.baseUrl}/read-all`);
//   }

//   async deleteMessage(messageId: number): Promise<void> {
//     return apiClient.delete(`${this.baseUrl}/${messageId}`);
//   }

//   // Notification methods
//   async sendFlightUpdateNotification(flightId: number, updateType: string, message: string): Promise<void> {
//     return apiClient.post(`${this.baseUrl}/flight-update`, {
//       flightId,
//       updateType,
//       message,
//       messageType: 'FLIGHT_UPDATE',
//       priority: 'HIGH'
//     });
//   }

//   async sendSystemAlert(message: string, priority: 'HIGH' | 'URGENT' = 'HIGH'): Promise<void> {
//     return apiClient.post(`${this.baseUrl}/system-alert`, {
//       receiverType: 'ALL',
//       subject: 'System Alert',
//       content: message,
//       messageType: 'ALERT',
//       priority
//     });
//   }

//   async sendWelcomeMessage(userId: number, userType: 'EMPLOYEE' | 'PASSENGER'): Promise<void> {
//     return apiClient.post(`${this.baseUrl}/welcome`, {
//       receiverId: userId,
//       receiverType: userType
//     });
//   }

//   async sendBookingConfirmation(passengerId: number, bookingDetails: any): Promise<void> {
//     return apiClient.post(`${this.baseUrl}/booking-confirmation`, {
//       receiverId: passengerId,
//       receiverType: 'PASSENGER',
//       bookingDetails
//     });
//   }

//   // Notification settings
//   async getNotificationSettings(): Promise<NotificationSettings> {
//     return apiClient.get(`${this.baseUrl}/settings`);
//   }

//   async updateNotificationSettings(settings: Partial<NotificationSettings>): Promise<NotificationSettings> {
//     return apiClient.patch(`${this.baseUrl}/settings`, settings);
//   }

//   // Real-time messaging (WebSocket support)
//   async subscribeToNotifications(callback: (message: Message) => void): Promise<() => void> {
//     // This would implement WebSocket connection for real-time notifications
//     // Return unsubscribe function
//     const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
//     const eventSource = new EventSource(`${baseURL}${this.baseUrl}/subscribe`);
    
//     eventSource.onmessage = (event) => {
//       const message: Message = JSON.parse(event.data);
//       callback(message);
//     };

//     return () => {
//       eventSource.close();
//     };
//   }

//   // Bulk operations
//   async sendBulkMessage(messageData: Omit<SendMessageRequest, 'receiverId'>, receiverIds: number[]): Promise<Message[]> {
//     return apiClient.post(`${this.baseUrl}/bulk`, {
//       ...messageData,
//       receiverIds
//     });
//   }

//   async deleteMultipleMessages(messageIds: number[]): Promise<void> {
//     return apiClient.delete(`${this.baseUrl}/bulk`, {
//       data: { messageIds }
//     });
//   }

//   async markMultipleAsRead(messageIds: number[]): Promise<void> {
//     return apiClient.patch(`${this.baseUrl}/bulk/read`, {
//       messageIds
//     });
//   }

//   // Search and filter
//   async searchMessages(query: string, filters?: MessageFilter): Promise<Message[]> {
//     return apiClient.get(`${this.baseUrl}/search`, {
//       params: { q: query, ...filters }
//     });
//   }

//   async getMessagesByType(messageType: string): Promise<Message[]> {
//     return apiClient.get(`${this.baseUrl}/type/${messageType}`);
//   }

//   async getMessagesByPriority(priority: string): Promise<Message[]> {
//     return apiClient.get(`${this.baseUrl}/priority/${priority}`);
//   }
// }

// export const messageService = new MessageService();
