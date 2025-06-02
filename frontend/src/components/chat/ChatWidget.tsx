import React, { useState, useEffect, useRef } from 'react';
import { Card, Form, Button, Spinner, Badge } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';

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
    <div 
      className={`position-fixed bottom-0 end-0 m-3 ${isOpen ? '' : ''}`}
      style={{ zIndex: 1050, width: isOpen ? '350px' : 'auto' }}
    >
      <Card className="shadow-lg border-0">
        <Card.Header 
          className="bg-primary text-white d-flex justify-content-between align-items-center"
          style={{ cursor: 'pointer' }}
          onClick={() => setIsOpen(!isOpen)}
        >
          <div className="d-flex align-items-center">
            <i className="bi bi-chat-dots me-2"></i>
            <span className="fw-bold">Support Chat</span>
          </div>
          <div className="d-flex align-items-center">
            <Badge bg="success" className="me-2">Online</Badge>
            <i className={`bi ${isOpen ? 'bi-dash' : 'bi-plus'}`}></i>
          </div>
        </Card.Header>

        {isOpen && (
          <div>
            <div 
              className="p-3 bg-light"
              style={{ height: '300px', overflowY: 'auto' }}
            >
              {loading ? (
                <div className="text-center py-5">
                  <Spinner animation="border" size="sm" className="me-2" />
                  Loading chat...
                </div>
              ) : (
                <>
                  {messages.map(message => (
                    <div
                      key={message.messageId}
                      className={`mb-3 d-flex ${message.isFromCustomer ? 'justify-content-end' : 'justify-content-start'}`}
                    >
                      <div 
                        className={`p-2 rounded max-width-75 ${
                          message.isFromCustomer 
                            ? 'bg-primary text-white' 
                            : 'bg-white border'
                        }`}
                        style={{ maxWidth: '75%' }}
                      >
                        <div className="small">{message.content}</div>
                        <div className={`text-xs mt-1 ${message.isFromCustomer ? 'text-light' : 'text-muted'}`} style={{ fontSize: '0.7rem' }}>
                          {new Date(message.sendTime).toLocaleTimeString()}
                        </div>
                      </div>
                    </div>
                  ))}
                  <div ref={messagesEndRef} />
                </>
              )}
            </div>

            <Card.Footer className="p-2">
              <Form onSubmit={sendMessage}>
                <div className="d-flex gap-2">
                  <Form.Control
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder="Type your message..."
                    disabled={loading}
                    size="sm"
                  />
                  <Button 
                    type="submit" 
                    variant="primary"
                    size="sm"
                    disabled={!newMessage.trim() || loading}
                  >
                    <i className="bi bi-send"></i>
                  </Button>
                </div>
              </Form>
            </Card.Footer>
          </div>
        )}
      </Card>
    </div>
  );
};

export default ChatWidget;
