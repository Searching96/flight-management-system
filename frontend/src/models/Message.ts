export interface Message {
  messageId: number;
  senderId?: number;
  senderType: 'EMPLOYEE' | 'PASSENGER' | 'SYSTEM';
  receiverId?: number;
  receiverType: 'EMPLOYEE' | 'PASSENGER' | 'ALL';
  subject: string;
  content: string;
  messageType: 'NOTIFICATION' | 'ALERT' | 'INFO' | 'WARNING' | 'FLIGHT_UPDATE';
  priority: 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT';
  isRead: boolean;
  sentAt: string;
  readAt?: string;
  relatedEntityType?: string;
  relatedEntityId?: number;
  deletedAt?: string;
}

export interface CreateMessageRequest {
  receiverId?: number;
  receiverType: 'EMPLOYEE' | 'PASSENGER' | 'ALL';
  subject: string;
  content: string;
  messageType: 'NOTIFICATION' | 'ALERT' | 'INFO' | 'WARNING' | 'FLIGHT_UPDATE';
  priority?: 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT';
  relatedEntityType?: string;
  relatedEntityId?: number;
}

export interface MessageFilter {
  messageType?: string;
  priority?: string;
  isRead?: boolean;
  senderType?: string;
  dateFrom?: string;
  dateTo?: string;
}

export interface NotificationSettings {
  emailNotifications: boolean;
  pushNotifications: boolean;
  flightUpdates: boolean;
  promotionalMessages: boolean;
  systemAlerts: boolean;
}
