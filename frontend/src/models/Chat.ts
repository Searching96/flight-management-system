export interface Chatbox {
  chatboxId?: number;
  customerId: number;
  customerName?: string;
  employeeId: number;
  employeeName?: string;
  lastMessageTime?: string;
  unreadCount?: number;
}

export interface Message {
  messageId?: number;
  chatboxId: number;
  messageType: number;
  content: string;
  sendTime: string;
  senderName?: string;
  isFromCustomer?: boolean;
}

export interface SendMessageRequest {
  chatboxId: number;
  content: string;
  messageType: number;
}

export enum MessageType {
  CUSTOMER = 1,
  EMPLOYEE = 2
}
