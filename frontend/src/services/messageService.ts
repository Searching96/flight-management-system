import { apiClient } from "./api";
import { Message } from "../models/Chat";
import type { ApiResponse } from "../models/ApiResponse";

export class MessageService {
  private readonly baseUrl = "/messages";

  // Message CRUD operations
  async getAllMessages(): Promise<ApiResponse<Message[]>> {
    return apiClient.get(this.baseUrl);
  }

  async getMessageById(id: number): Promise<ApiResponse<Message>> {
    return apiClient.get(`${this.baseUrl}/${id}`);
  }

  async getSentMessages(): Promise<ApiResponse<Message[]>> {
    return apiClient.get(`${this.baseUrl}/sent`);
  }

  async getUnreadMessages(): Promise<ApiResponse<Message[]>> {
    return apiClient.get(`${this.baseUrl}/unread`);
  }

  async getUnreadCount(): Promise<ApiResponse<number>> {
    return apiClient.get(`${this.baseUrl}/unread/count`);
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
    return apiClient.get(`${this.baseUrl}/chatbox/${chatboxId}`);
  }

  async createMessage(messageData: any): Promise<Message> {
    return apiClient.post(this.baseUrl, messageData);
  }

  async createCustomerMessage(
    chatboxId: number,
    content: string
  ): Promise<Message> {
    return apiClient.post(`${this.baseUrl}/customer`, {
      chatboxId,
      content,
    });
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
    return apiClient.post(`${this.baseUrl}/employee`, {
      chatboxId,
      employeeId,
      content,
    });
  }
}

export const messageService = new MessageService();
