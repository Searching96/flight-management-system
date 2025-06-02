export interface Chatbox {
  chatboxId?: number;
  customerId?: number;
  customerName?: string;
  employeeId?: number;
  employeeName?: string;
  lastMessageTime?: string;
  lastMessageContent?: string;
  unreadCount?: number;
}

export interface CreateTestChatboxRequest {
  customerId: string;
  employeeId: string;
}

export interface Message {
  messageId?: number;
  chatboxId?: number;
  messageType: number; // 1: customer to employee, 2: employee to customer
  content: string;
  sendTime?: string;
  senderName?: string;
  isFromCustomer?: boolean;
}

export interface SendMessageRequest {
  chatboxId: number;
  content: string;
  messageType: number;
}
