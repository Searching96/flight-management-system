import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../../hooks/useAuth';
import './ChatWidget.css';

interface Message {
  messageId?: number;
  chatboxId: number;
  content: string;
  sendTime: string;
  senderName?: string;
  isFromCustomer: boolean;
}

interface Chatbox {
  chatboxId?: number;
  customerId: number;
  employeeId: number;
  status: string;
}

const ChatWidget: React.FC = () => {
  const { user } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [chatboxId, setChatboxId] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isOpen && user && !chatboxId) {
      initializeChat();
    }
  }, [isOpen, user]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const initializeChat = async () => {
    try {
      setLoading(true);
      // Create a mock chatbox for demo purposes
      const mockChatbox: Chatbox = {
        chatboxId: Date.now(),
        customerId: user!.accountId!,
        employeeId: 1,
        status: 'ACTIVE'
      };
      
      setChatboxId(mockChatbox.chatboxId!);
      
      // Add welcome message
      const welcomeMessage: Message = {
        messageId: Date.now(),
        chatboxId: mockChatbox.chatboxId!,
        content: "Hello! How can we help you today?",
        sendTime: new Date().toISOString(),
        senderName: "Support Agent",
        isFromCustomer: false
      };
      
      setMessages([welcomeMessage]);
    } catch (error) {
      console.error('Failed to initialize chat:', error);
    } finally {
      setLoading(false);
    }
  };

  const sendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !chatboxId) return;

    try {
      // Add user message
      const userMessage: Message = {
        messageId: Date.now(),
        chatboxId,
        content: newMessage.trim(),
        sendTime: new Date().toISOString(),
        senderName: user?.accountName,
        isFromCustomer: true
      };
      
      setMessages(prev => [...prev, userMessage]);
      setNewMessage('');
      
      // Simulate employee response for demo
      setTimeout(() => {
        const autoResponse: Message = {
          messageId: Date.now() + 1,
          chatboxId,
          content: "Thank you for your message. Our team will assist you shortly.",
          sendTime: new Date().toISOString(),
          senderName: "Support Agent",
          isFromCustomer: false
        };
        setMessages(prev => [...prev, autoResponse]);
      }, 1000);
      
    } catch (error) {
      console.error('Failed to send message:', error);
    }
  };

  if (!user) {
    return null;
  }

  return (
    <div className={`chat-widget ${isOpen ? 'open' : ''}`}>
      <div className="chat-header" onClick={() => setIsOpen(!isOpen)}>
        <span className="chat-icon">💬</span>
        <span className="chat-title">Support Chat</span>
        <span className="toggle-icon">{isOpen ? '−' : '+'}</span>
      </div>

      {isOpen && (
        <div className="chat-body">
          <div className="messages-container">
            {loading ? (
              <div className="loading">Loading chat...</div>
            ) : (
              <>
                {messages.map(message => (
                  <div
                    key={message.messageId}
                    className={`message ${message.isFromCustomer ? 'customer' : 'employee'}`}
                  >
                    <div className="message-content">{message.content}</div>
                    <div className="message-time">
                      {new Date(message.sendTime).toLocaleTimeString()}
                    </div>
                  </div>
                ))}
                <div ref={messagesEndRef} />
              </>
            )}
          </div>

          <form onSubmit={sendMessage} className="message-input">
            <input
              type="text"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              placeholder="Type your message..."
              disabled={loading}
            />
            <button type="submit" disabled={!newMessage.trim() || loading}>
              Send
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default ChatWidget;
