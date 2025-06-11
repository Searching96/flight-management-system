import React, { useState, useEffect, useRef } from 'react';
import { Card, Form, Button, Spinner, Badge } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';
import { chatService } from '../../services/chatService';
import { Chatbox, Message as ChatMessage, SendMessageRequest } from '../../models/Chat';

interface Message {
  messageId?: number;
  chatboxId: number;
  employeeId?: number; // null = from customer, not null = from employee
  content: string;
  sendTime: string;
  employeeName?: string;
  isFromCustomer: boolean;
}

const ChatWidget: React.FC = () => {
  const { user } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [chatbox, setChatbox] = useState<Chatbox | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [shouldAutoScroll, setShouldAutoScroll] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const messagesContainerRef = useRef<HTMLDivElement>(null);
  const pollingIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    if (isOpen && user && !chatbox) {
      initializeChat();
    }
  }, [isOpen, user]);

  useEffect(() => {
    if (shouldAutoScroll) {
      scrollToBottom();
    }
  }, [messages, shouldAutoScroll]);

  // Real-time polling for messages
  useEffect(() => {
    if (isOpen && chatbox?.chatboxId) {
      startPolling();
    } else {
      stopPolling();
    }

    return () => stopPolling();
  }, [isOpen, chatbox]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const container = e.currentTarget;
    const isAtBottom = container.scrollHeight - container.scrollTop <= container.clientHeight + 50; // 50px threshold
    setShouldAutoScroll(isAtBottom);
  };

  const initializeChat = async () => {
    if (!user?.id) return;
    
    try {
      setLoading(true);
      setError(null);
      
      // Get or create chatbox for customer
      const chatboxData = await chatService.getChatboxByCustomerId(user.id);
      setChatbox(chatboxData);
      
      // Load existing messages if chatbox exists
      if (chatboxData.chatboxId) {
        const existingMessages = await chatService.getMessagesByChatboxId(chatboxData.chatboxId);
        const formattedMessages: Message[] = existingMessages.map(msg => ({
          messageId: msg.messageId,
          chatboxId: msg.chatboxId || chatboxData.chatboxId!,
          content: msg.content,
          sendTime: msg.sendTime || new Date().toISOString(),
          employeeName: msg.employeeName,
          isFromCustomer: !msg.employeeId // If employeeId is null, it's from customer
        }));
        setMessages(formattedMessages); // Cache trong state
      }
    } catch (error) {
      console.error('Failed to initialize chat:', error);
      setError('Failed to load chat. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const sendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !chatbox?.chatboxId || !user) return;

    try {
      // Add user message to UI immediately
      const userMessage: Message = {
        messageId: Date.now(),
        chatboxId: chatbox.chatboxId,
        content: newMessage.trim(),
        sendTime: new Date().toISOString(),
        employeeName: undefined,
        isFromCustomer: true
      };
      
      setMessages(prev => [...prev, userMessage]);
      const messageContent = newMessage.trim();
      setNewMessage('');
      setShouldAutoScroll(true); // Always scroll when user sends message
      
      // Send message to API using createCustomerMessage
      await chatService.createCustomerMessage(chatbox.chatboxId, messageContent);
    } catch (error) {
      console.error('Failed to send message:', error);
      setError('Failed to send message. Please try again.');
    }
  };

  const startPolling = () => {
    if (pollingIntervalRef.current) return;
    
    pollingIntervalRef.current = setInterval(async () => {
      if (chatbox?.chatboxId) {
        try {
          const messages = await chatService.getMessagesByChatboxId(chatbox.chatboxId);
          const formattedMessages: Message[] = messages.map(msg => ({
            messageId: msg.messageId,
            chatboxId: msg.chatboxId || chatbox.chatboxId!,
            content: msg.content,
            sendTime: msg.sendTime || new Date().toISOString(),
            employeeName: msg.employeeName,
            isFromCustomer: !msg.employeeId // If employeeId is null, it's from customer
          }));
          setMessages(formattedMessages);
        } catch (error) {
          console.error('Failed to poll messages:', error);
        }
      }
    }, 200); // Poll every 0.2 seconds
  };

  const stopPolling = () => {
    if (pollingIntervalRef.current) {
      clearInterval(pollingIntervalRef.current);
      pollingIntervalRef.current = null;
    }
  };

  const handleSendMessage = async (messageContent: string) => {
    if (!messageContent.trim() || !chatbox?.chatboxId) return;

    try {
      setShouldAutoScroll(true); // Always scroll when user sends message
      await chatService.createCustomerMessage(chatbox.chatboxId, messageContent.trim());
      setNewMessage('');
      
      // Immediately reload messages after sending
      const messages = await chatService.getMessagesByChatboxId(chatbox.chatboxId);
      const formattedMessages: Message[] = messages.map(msg => ({
        messageId: msg.messageId,
        chatboxId: msg.chatboxId || chatbox.chatboxId!,
        content: msg.content,
        sendTime: msg.sendTime || new Date().toISOString(),
        employeeName: msg.employeeName,
        isFromCustomer: !msg.employeeId
      }));
      setMessages(formattedMessages);
    } catch (error) {
      console.error('Failed to send message:', error);
      setError('Failed to send message. Please try again.');
    }
  };

  const getAvatarLetter = (employeeName?: string, isFromCustomer?: boolean) => {
    if (employeeName && employeeName.trim()) {
      const words = employeeName.trim().split(' ');
      if (words.length >= 2) {
        // Lấy 2 từ cuối
        const lastTwo = words.slice(-2);
        return (lastTwo[0].charAt(0) + lastTwo[1].charAt(0)).toUpperCase();
      } else {
        return words[0].charAt(0).toUpperCase();
      }
    }
    return isFromCustomer ? 'C' : 'S';
  };

  const getAvatarColor = (employeeName?: string, isFromCustomer?: boolean) => {
    if (!employeeName && isFromCustomer) {
      return '#007bff'; // Primary color for customer
    }
    
    // Generate color based on name
    let hash = 0;
    const name = employeeName || 'Support';
    for (let i = 0; i < name.length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    
    // Convert to HSL for better color distribution
    const hue = Math.abs(hash) % 360;
    return `hsl(${hue}, 60%, 50%)`;
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    
    // Check if it's today
    if (date.toDateString() === today.toDateString()) {
      return 'Hôm nay';
    }
    
    // Check if it's yesterday
    if (date.toDateString() === yesterday.toDateString()) {
      return 'Hôm qua';
    }
    
    // Format as dd/mm/yyyy for other dates
    return date.toLocaleDateString('vi-VN', { 
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric' 
    });
  };

  const groupMessagesByDate = (messages: Message[]) => {
    const groups: { [key: string]: Message[] } = {};
    
    messages.forEach(message => {
      const date = new Date(message.sendTime).toDateString();
      if (!groups[date]) {
        groups[date] = [];
      }
      groups[date].push(message);
    });
    
    return groups;
  };

  if (!user) {
    return null;
  }

  // Only show chat widget for customers
  if (user.accountTypeName !== "Customer") {
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
            <i className={`bi ${isOpen ? 'bi-dash' : 'bi-plus'}`}></i>
          </div>
        </Card.Header>

        {isOpen && (
          <div>
            <div 
              className="p-3 bg-light"
              style={{ height: '300px', overflowY: 'auto' }}
              ref={messagesContainerRef}
              onScroll={handleScroll}
            >
              {loading ? (
                <div className="text-center py-5">
                  <Spinner animation="border" size="sm" className="me-2" />
                  Loading chat...
                </div>
              ) : error ? (
                <div className="text-center py-5 text-danger">
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  {error}
                  <br />
                  <Button 
                    variant="outline-primary" 
                    size="sm" 
                    className="mt-2"
                    onClick={initializeChat}
                  >
                    Retry
                  </Button>
                </div>
              ) : (
                <>
                  {(() => {
                    const messageGroups = groupMessagesByDate(messages);
                    const sortedDates = Object.keys(messageGroups).sort(
                      (a, b) => new Date(a).getTime() - new Date(b).getTime()
                    );

                    return sortedDates.map(dateKey => (
                      <div key={dateKey}>
                        {/* Date Separator */}
                        <div className="d-flex justify-content-center my-3">
                          <div 
                            className="px-3 py-1 bg-white rounded-pill text-muted small"
                            style={{ border: '1px solid #e0e0e0' }}
                          >
                            {formatDate(messageGroups[dateKey][0].sendTime)}
                          </div>
                        </div>

                        {/* Messages for this date */}
                        {messageGroups[dateKey].map((message, index) => (
                          <div
                            key={message.messageId || `${dateKey}-${index}`}
                            className={`mb-3 d-flex ${message.isFromCustomer ? 'justify-content-end' : 'justify-content-start'}`}
                          >
                            {!message.isFromCustomer && (
                              <div 
                                className="me-2 rounded-circle text-white d-flex align-items-center justify-content-center flex-shrink-0"
                                style={{ 
                                  width: '32px', 
                                  height: '32px', 
                                  fontSize: '12px', 
                                  fontWeight: 'bold',
                                  backgroundColor: getAvatarColor(message.employeeName, message.isFromCustomer)
                                }}
                              >
                                {getAvatarLetter(message.employeeName, message.isFromCustomer)}
                              </div>
                            )}
                            
                            <div 
                              className={`p-2 rounded-3 ${
                                message.isFromCustomer 
                                  ? 'bg-primary text-white' 
                                  : 'bg-white border'
                              }`}
                              style={{ 
                                maxWidth: '70%',
                                borderRadius: message.isFromCustomer ? '18px 18px 4px 18px' : '18px 18px 18px 4px'
                              }}
                            >
                              {!message.isFromCustomer && message.employeeName && (
                                <div className="small fw-bold text-muted">
                                  {message.employeeName}
                                </div>
                              )}
                              <div className="small">{message.content}</div>
                              <div className={`text-xs mt-1 ${message.isFromCustomer ? 'text-light' : 'text-muted'}`} style={{ fontSize: '0.7rem' }}>
                                {formatTime(message.sendTime)}
                              </div>
                            </div>

                            {message.isFromCustomer && (
                              <div 
                                className="ms-2 rounded-circle text-white d-flex align-items-center justify-content-center flex-shrink-0"
                                style={{ 
                                  width: '32px', 
                                  height: '32px', 
                                  fontSize: '12px', 
                                  fontWeight: 'bold',
                                  backgroundColor: getAvatarColor(undefined, true)
                                }}
                              >
                                {getAvatarLetter(undefined, true)}
                              </div>
                            )}
                          </div>
                        ))}
                      </div>
                    ));
                  })()}
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
                    disabled={loading || !chatbox}
                    size="sm"
                    onKeyDown={(e) => {
                      if (e.key === 'Enter' && !e.shiftKey) {
                        e.preventDefault();
                        handleSendMessage(newMessage);
                      }
                    }}
                  />
                  <Button 
                    type="submit" 
                    variant="primary"
                    size="sm"
                    disabled={!newMessage.trim() || loading || !chatbox}
                  >
                    <i className="bi bi-send"></i>
                  </Button>
                </div>
              </Form>
              {error && (
                <div className="text-danger small mt-1">
                  {error}
                </div>
              )}
            </Card.Footer>
          </div>
        )}
      </Card>
    </div>
  );
};

export default ChatWidget;
