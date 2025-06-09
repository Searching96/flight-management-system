export interface Chatbox {
  chatboxId?: number;
  customerId: number;
  customerName?: string;
  lastMessageContent?: string;
  lastMessageTime?: string;
  unreadCount?: number;
  deletedAt?: string;
}

export interface CreateTestChatboxRequest {
  customerId: number;
}

export interface Message {
  messageId?: number;
  chatboxId?: number;
  employeeId?: number; // null = from customer, not null = from employee
  content: string;
  sendTime?: string;
  senderName?: string; // Employee name from backend or customer name
  isFromCustomer?: boolean; // Derived field for UI
}

export interface SendMessageRequest {
  chatboxId: number;
  content: string;
  employeeId?: number; // null for customer messages, employee ID for employee messages
}
