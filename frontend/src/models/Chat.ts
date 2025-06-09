export interface Chatbox {
  chatboxId?: number;
  customerId: number;
  customerName?: string;
  lastMessageContent?: string;
  lastMessageTime?: string;
  isLastMessageFromCustomer?: boolean;
  lastMessageEmployeeId?: number;
  lastMessageSenderName?: string;
  unreadCount?: number;
  deletedAt?: string;
}

export interface CreateTestChatboxRequest {
  customerId: number;
}

export interface Message {
  messageId?: number;
  chatboxId: number;
  employeeId?: number;
  employeeName?: string;  // Employee name for display
  content: string;
  sendTime: string;
  isFromCustomer?: boolean;
  deletedAt?: string;
}

export interface SendMessageRequest {
  chatboxId: number;
  content: string;
  employeeId?: number; // null for customer messages, employee ID for employee messages
}
