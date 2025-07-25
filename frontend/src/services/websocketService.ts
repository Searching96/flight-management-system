interface WebSocketMessage {
  type: string;
  chatboxId?: string;
  userId?: string;
  userType?: string;
  userName?: string;
  timestamp?: number;
}

interface TypingUser {
  userId: string;
  userType: string;
  userName: string;
}

class WebSocketService {
  private socket: WebSocket | null = null;
  private reconnectInterval: number = 5000;
  private maxReconnectAttempts: number = 5;
  private reconnectAttempts: number = 0;
  private listeners: Map<string, Function[]> = new Map();
  private isConnected: boolean = false;

  connect(chatboxId: string, userId: string, userType: 'employee' | 'customer', userName: string) {
    try {
      // Try different WebSocket endpoints
      const endpoints = [
        'ws://localhost:8080/ws/chat/websocket',
        'ws://localhost:8080/ws/chat',
        'ws://localhost:8080/websocket',
        'ws://localhost:8080/chat'
      ];
      
      this.tryConnect(endpoints, 0, chatboxId, userId, userType, userName);
    } catch (error) {
      console.error('Error connecting to WebSocket:', error);
    }
  }

  private tryConnect(endpoints: string[], index: number, chatboxId: string, userId: string, userType: 'employee' | 'customer', userName: string) {
    if (index >= endpoints.length) {
      console.error('All WebSocket endpoints failed');
      return;
    }

    const endpoint = endpoints[index];
    console.log(`Trying WebSocket endpoint: ${endpoint}`);
    
    this.socket = new WebSocket(endpoint);

    this.socket.onopen = () => {
      console.log(`WebSocket connected to: ${endpoint}`);
      this.isConnected = true;
      this.reconnectAttempts = 0;

      // Join the chat room
      this.send({
        type: 'join_chat',
        chatboxId,
        userId,
        userType,
        userName
      });
    };

    this.socket.onerror = (error) => {
      console.log(`WebSocket failed for ${endpoint}, trying next...`);
      this.tryConnect(endpoints, index + 1, chatboxId, userId, userType, userName);
    };

    this.socket.onmessage = (event) => {
      try {
        const message: WebSocketMessage = JSON.parse(event.data);
        this.handleMessage(message);
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    };

    this.socket.onclose = () => {
      console.log('WebSocket disconnected');
      this.isConnected = false;
      this.attemptReconnect(chatboxId, userId, userType, userName);
    };
  }

  private attemptReconnect(chatboxId: string, userId: string, userType: 'employee' | 'customer', userName: string) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
      
      setTimeout(() => {
        this.connect(chatboxId, userId, userType, userName);
      }, this.reconnectInterval);
    }
  }

  private handleMessage(message: WebSocketMessage) {
    const listeners = this.listeners.get(message.type) || [];
    listeners.forEach(listener => listener(message));
  }

  send(message: WebSocketMessage) {
    if (this.socket && this.isConnected) {
      this.socket.send(JSON.stringify(message));
    } else {
      console.warn('WebSocket not connected, cannot send message');
    }
  }

  startTyping(chatboxId: string, userId: string, userType: 'employee' | 'customer', userName: string) {
    this.send({
      type: 'typing_start',
      chatboxId,
      userId,
      userType,
      userName
    });
  }

  stopTyping(chatboxId: string, userId: string, userType: 'employee' | 'customer', userName: string) {
    this.send({
      type: 'typing_stop',
      chatboxId,
      userId,
      userType,
      userName
    });
  }

  notifyNewMessage(chatboxId: string, userId: string, userType: 'employee' | 'customer') {
    this.send({
      type: 'new_message',
      chatboxId,
      userId,
      userType
    });
  }

  onTypingStart(callback: (data: TypingUser) => void) {
    this.addEventListener('typing_start', callback);
  }

  onTypingStop(callback: (data: TypingUser) => void) {
    this.addEventListener('typing_stop', callback);
  }

  onNewMessage(callback: (data: any) => void) {
    this.addEventListener('new_message', callback);
  }

  private addEventListener(type: string, callback: Function) {
    if (!this.listeners.has(type)) {
      this.listeners.set(type, []);
    }
    this.listeners.get(type)!.push(callback);
  }

  removeEventListener(type: string, callback: Function) {
    const listeners = this.listeners.get(type);
    if (listeners) {
      const index = listeners.indexOf(callback);
      if (index > -1) {
        listeners.splice(index, 1);
      }
    }
  }

  disconnect() {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
      this.isConnected = false;
    }
    this.listeners.clear();
  }
}

export const webSocketService = new WebSocketService();
