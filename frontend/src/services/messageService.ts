import { apiClient } from "./api";
import { Message } from "../models/Chat";
import type { ApiResponse } from "../models/ApiResponse";

export class MessageService {
  private readonly baseUrl = "/messages";

  // Message CRUD operations
  async getAllMessages(): Promise<Message[]> {
    const response = await apiClient.get<ApiResponse<Message[]>>(this.baseUrl);
    return response.data;
  }

  async getMessageById(id: number): Promise<Message> {
    const response = await apiClient.get<ApiResponse<Message>>(`${this.baseUrl}/${id}`);
    return response.data;
  }

  async getSentMessages(): Promise<Message[]> {
    const response = await apiClient.get<ApiResponse<Message[]>>(`${this.baseUrl}/sent`);
    return response.data;
  }

  async getUnreadMessages(): Promise<Message[]> {
    const response = await apiClient.get<ApiResponse<Message[]>>(`${this.baseUrl}/unread`);
    return response.data;
  }

  async getUnreadCount(): Promise<number> {
    const response = await apiClient.get<ApiResponse<number>>(`${this.baseUrl}/unread/count`);
    return response.data;
  }

  async markAsRead(messageId: number): Promise<void> {
    return apiClient.patch(`${this.baseUrl}/${messageId}/read`);
  }

  async markAllAsRead(): Promise<void> {
    return apiClient.patch(`${this.baseUrl}/read-all`);
  }

  async deleteMessage(messageId: number): Promise<void> {
    return apiClient.delete(`${this.baseUrl}/${messageId}`);
  }

  // Notification methods
  async sendFlightUpdateNotification(
    flightId: number,
    updateType: string,
    message: string
  ): Promise<void> {
    return apiClient.post(`${this.baseUrl}/flight-update`, {
      flightId,
      updateType,
      message,
      messageType: "FLIGHT_UPDATE",
      priority: "HIGH",
    });
  }

  async sendSystemAlert(
    message: string,
    priority: "HIGH" | "URGENT" = "HIGH"
  ): Promise<void> {
    return apiClient.post(`${this.baseUrl}/system-alert`, {
      receiverType: "ALL",
      subject: "System Alert",
      content: message,
      messageType: "ALERT",
      priority,
    });
  }

  async sendWelcomeMessage(
    userId: number,
    userType: "EMPLOYEE" | "PASSENGER"
  ): Promise<void> {
    return apiClient.post(`${this.baseUrl}/welcome`, {
      receiverId: userId,
      receiverType: userType,
    });
  }

  async sendBookingConfirmation(
    passengerId: number,
    bookingDetails: any
  ): Promise<void> {
    return apiClient.post(`${this.baseUrl}/booking-confirmation`, {
      receiverId: passengerId,
      receiverType: "PASSENGER",
      bookingDetails,
    });
  }

  async getMessagesByChatboxId(chatboxId: number): Promise<Message[]> {
    const response = await apiClient.get<ApiResponse<Message[]>>(`${this.baseUrl}/chatbox/${chatboxId}`);
    return response.data;
  }

  async createMessage(messageData: any): Promise<Message> {
    const response = await apiClient.post<ApiResponse<Message>>(this.baseUrl, messageData);
    return response.data;
  }

  async createCustomerMessage(
    chatboxId: number,
    content: string
  ): Promise<Message> {
    const response = await apiClient.post<ApiResponse<Message>>(`${this.baseUrl}/customer`, {
      chatboxId,
      content,
    });
    return response.data;
  }

  async createEmployeeMessage(
    chatboxId: number,
    employeeId: number,
    content: string
  ): Promise<Message> {
    console.log("Creating employee message:", {
      chatboxId,
      employeeId,
      content,
    });
    const response = await apiClient.post<ApiResponse<Message>>(`${this.baseUrl}/employee`, {
      chatboxId,
      employeeId,
      content,
    });
    return response.data;
  }
}

export const messageService = new MessageService();
