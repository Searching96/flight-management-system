import React, { useState, useEffect, useRef } from 'react';
import { Card, Form, Button, Spinner, Badge, OverlayTrigger, Tooltip } from 'react-bootstrap';
import { useAuth } from '../../hooks/useAuth';
import { chatService } from '../../services/chatService';
import { webSocketService } from '../../services/websocketService';
import { accountChatboxService } from '../../services/accountChatboxService';
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

interface TypingUser {
  userId: string;
  userType: string;
  userName: string;
}

const ChatWidget: React.FC = () => {
  const { user } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const [bubblePosition, setBubblePosition] = useState({ x: 0, y: 0 });
  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({ x: 0, y: 0 });
  const [hasDragged, setHasDragged] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [chatbox, setChatbox] = useState<Chatbox | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [shouldAutoScroll, setShouldAutoScroll] = useState(true);
  const [typingUsers, setTypingUsers] = useState<TypingUser[]>([]);
  const [isTyping, setIsTyping] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const messagesContainerRef = useRef<HTMLDivElement>(null);
  const pollingIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const unreadPollingIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const typingTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const continuousUpdateIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

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

  useEffect(() => {
    if (isOpen && chatbox?.chatboxId && user) {
      // Update last visit time and reset unread count when opening chat
      updateLastVisitTime();
      setUnreadCount(0); // Immediately reset unread count
      
      // Start continuous last visit time updates when chat is open
      startContinuousLastVisitTimeUpdates();
      
      startPolling();

      // Connect to WebSocket
      webSocketService.connect(
        chatbox.chatboxId.toString(),
        user.id!.toString(),
        'customer',
        user.accountName || 'Customer'
      );

      // Set up WebSocket event listeners
      const handleTypingStart = (data: TypingUser) => {
        if (data.userType === 'employee') {
          setTypingUsers(prev => {
            const exists = prev.some(u => u.userId === data.userId && u.userType === data.userType);
            if (!exists) {
              return [...prev, data];
            }
            return prev;
          });
        }
      };

      const handleTypingStop = (data: TypingUser) => {
        if (data.userType === 'employee') {
          setTypingUsers(prev => prev.filter(u => !(u.userId === data.userId && u.userType === data.userType)));
        }
      };

      const handleNewMessage = () => {
        // Reload messages when new message arrives
        if (chatbox?.chatboxId) {
          loadMessages(chatbox.chatboxId);
          
          // Update last visit time if chat is open, otherwise update unread count
          if (isOpen) {
            updateLastVisitTime();
          } else if (user?.id) {
            loadUnreadCount(chatbox.chatboxId);
          }
        }
      };

      webSocketService.onTypingStart(handleTypingStart);
      webSocketService.onTypingStop(handleTypingStop);
      webSocketService.onNewMessage(handleNewMessage);

      return () => {
        webSocketService.removeEventListener('typing_start', handleTypingStart);
        webSocketService.removeEventListener('typing_stop', handleTypingStop);
        webSocketService.removeEventListener('new_message', handleNewMessage);
        
        // Stop continuous updates when chat is closed
        stopContinuousLastVisitTimeUpdates();
      };
    } else {
      stopPolling();
      webSocketService.disconnect();
      setTypingUsers([]);
      
      // Stop continuous updates when chat is closed
      stopContinuousLastVisitTimeUpdates();
    }

    return () => {
      stopPolling();
      stopContinuousLastVisitTimeUpdates();
    };
  }, [isOpen, chatbox, user]);

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
        setMessages(formattedMessages);
        
        // Load unread count
        await loadUnreadCount(chatboxData.chatboxId);
      }
    } catch (error) {
      console.error('Failed to initialize chat:', error);
      setError('Failed to load chat. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const loadUnreadCount = async (chatboxId: number) => {
    if (!user?.id) return;
    
    try {
      console.log('Loading unread count for user:', user.id, 'chatbox:', chatboxId);
      const count = await accountChatboxService.getUnreadMessageCount(user.id, chatboxId);
      console.log('Unread count received:', count);
      setUnreadCount(count);
    } catch (error) {
      console.error('Failed to load unread count:', error);
      setUnreadCount(0);
    }
  };

  const startUnreadPolling = () => {
    if (unreadPollingIntervalRef.current) return;
    
    console.log('Starting unread count polling');
    unreadPollingIntervalRef.current = setInterval(async () => {
      if (chatbox?.chatboxId && user?.id && !isOpen) {
        console.log('Polling unread count - chatbox:', chatbox.chatboxId, 'user:', user.id);
        await loadUnreadCount(chatbox.chatboxId);
      }
    }, 2000); // Poll every 2 seconds
  };

  const stopUnreadPolling = () => {
    if (unreadPollingIntervalRef.current) {
      console.log('Stopping unread count polling');
      clearInterval(unreadPollingIntervalRef.current);
      unreadPollingIntervalRef.current = null;
    }
  };

  const startContinuousLastVisitTimeUpdates = () => {
    if (continuousUpdateIntervalRef.current) return;
    
    console.log('Starting continuous last visit time updates');
    continuousUpdateIntervalRef.current = setInterval(async () => {
      if (isOpen && chatbox?.chatboxId && user?.id) {
        try {
          console.log('Continuous update - updating last visit time for user:', user.id, 'chatbox:', chatbox.chatboxId);
          await accountChatboxService.updateLastVisitTime(user.id, chatbox.chatboxId);
          console.log('Continuous last visit time update successful');
          
          // Keep unread count at 0 while chat is open
          setUnreadCount(0);
        } catch (error) {
          console.error('Failed to update last visit time during continuous update:', error);
          // Don't show error to user for background updates, just log it
        }
      }
    }, 30000); // Update every 30 seconds while chat is open
  };

  const stopContinuousLastVisitTimeUpdates = () => {
    if (continuousUpdateIntervalRef.current) {
      console.log('Stopping continuous last visit time updates');
      clearInterval(continuousUpdateIntervalRef.current);
      continuousUpdateIntervalRef.current = null;
    }
  };

  const updateLastVisitTime = async () => {
    if (!user?.id || !chatbox?.chatboxId) return;
    
    try {
      console.log('Updating last visit time for user:', user.id, 'chatbox:', chatbox.chatboxId);
      await accountChatboxService.updateLastVisitTime(user.id, chatbox.chatboxId);
      console.log('Last visit time updated, resetting unread count to 0');
      setUnreadCount(0); // Reset unread count when updating visit time
    } catch (error) {
      console.error('Failed to update last visit time:', error);
    }
  };

  const loadMessages = async (chatboxId: number) => {
    try {
      const messages = await chatService.getMessagesByChatboxId(chatboxId);
      const formattedMessages: Message[] = messages.map(msg => ({
        messageId: msg.messageId,
        chatboxId: msg.chatboxId || chatboxId,
        content: msg.content,
        sendTime: msg.sendTime || new Date().toISOString(),
        employeeName: msg.employeeName,
        isFromCustomer: !msg.employeeId // If employeeId is null, it's from customer
      }));
      setMessages(formattedMessages);
    } catch (error) {
      console.error('Failed to load messages:', error);
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
          
          // Only update if there are actually new messages
          if (JSON.stringify(formattedMessages) !== JSON.stringify(messages)) {
            setMessages(formattedMessages);
            
            // Update unread count if chat is not open
            if (!isOpen && user?.id) {
              console.log('Chat is closed, updating unread count due to new messages');
              await loadUnreadCount(chatbox.chatboxId);
            }
          }
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

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setNewMessage(value);

    if (!chatbox || !user) return;

    // Start typing indicator
    if (!isTyping && value.trim()) {
      setIsTyping(true);
      webSocketService.startTyping(
        chatbox.chatboxId!.toString(),
        user.id!.toString(),
        'customer',
        user.accountName || 'Customer'
      );
    }

    // Reset typing timeout
    if (typingTimeoutRef.current) {
      clearTimeout(typingTimeoutRef.current);
      typingTimeoutRef.current = null;
    }

    // Stop typing after 2 seconds of inactivity
    typingTimeoutRef.current = setTimeout(() => {
      if (isTyping) {
        setIsTyping(false);
        webSocketService.stopTyping(
          chatbox.chatboxId!.toString(),
          user.id!.toString(),
          'customer',
          user.accountName || 'Customer'
        );
      }
    }, 2000);
  };

  const sendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !chatbox?.chatboxId || !user) return;

    try {
      // Stop typing immediately when sending
      if (isTyping) {
        setIsTyping(false);
        webSocketService.stopTyping(
          chatbox.chatboxId.toString(),
          user.id!.toString(),
          'customer',
          user.accountName || 'Customer'
        );
        if (typingTimeoutRef.current) {
          clearTimeout(typingTimeoutRef.current);
          typingTimeoutRef.current = null;
        }
      }

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
      setShouldAutoScroll(true);
      
      // Send message to API
      await chatService.createCustomerMessage(chatbox.chatboxId, messageContent);

      // Notify WebSocket about new message
      webSocketService.notifyNewMessage(
        chatbox.chatboxId.toString(),
        user.id!.toString(),
        'customer'
      );
      
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

  const handleMouseDown = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsDragging(true);
    setHasDragged(false);
    
    // Get current bubble position relative to viewport
    const rect = e.currentTarget.getBoundingClientRect();
    const currentX = bubblePosition.x || rect.left;
    const currentY = bubblePosition.y || rect.top;
    
    setDragStart({
      x: e.clientX - currentX,
      y: e.clientY - currentY
    });
  };

  const handleMouseMove = (e: MouseEvent) => {
    if (!isDragging) return;
    
    const newX = e.clientX - dragStart.x;
    const newY = e.clientY - dragStart.y;
    
    // Calculate distance moved to detect if it's a drag
    const dragDistance = Math.sqrt(
      Math.pow(newX - bubblePosition.x, 2) + Math.pow(newY - bubblePosition.y, 2)
    );
    
    if (dragDistance > 5) { // If moved more than 5px, consider it a drag
      setHasDragged(true);
    }
    
    // Keep bubble within viewport bounds
    const maxX = window.innerWidth - 80; // 60px bubble + 20px margin
    const maxY = window.innerHeight - 80;
    
    setBubblePosition({
      x: Math.max(20, Math.min(newX, maxX)),
      y: Math.max(20, Math.min(newY, maxY))
    });
  };

  const handleMouseUp = () => {
    setIsDragging(false);
    // Reset hasDragged after a small delay to prevent immediate click
    setTimeout(() => setHasDragged(false), 100);
  };

  const handleBubbleClick = (e: React.MouseEvent) => {
    e.preventDefault();
    // Only open chat if it wasn't a drag operation
    if (!hasDragged && !isDragging) {
      setIsOpen(true);
      // Immediately reset unread count when opening chat
      if (unreadCount > 0) {
        setUnreadCount(0);
      }
    }
  };

  useEffect(() => {
    if (isDragging) {
      document.addEventListener('mousemove', handleMouseMove);
      document.addEventListener('mouseup', handleMouseUp);
      return () => {
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);
      };
    }
  }, [isDragging, dragStart]);

  // Add polling for unread count when chat is closed
  useEffect(() => {
    if (!isOpen && chatbox?.chatboxId && user?.id) {
      console.log('Chat is closed, starting unread count polling');
      startUnreadPolling();
    } else {
      console.log('Chat is open or no chatbox, stopping unread count polling');
      stopUnreadPolling();
    }

    return () => stopUnreadPolling();
  }, [isOpen, chatbox, user]);

  // Initialize chatbox even when widget is closed to get unread counts
  useEffect(() => {
    if (user && !chatbox) {
      console.log('User logged in but no chatbox, initializing...');
      initializeChat();
    }
  }, [user]);

  if (!user) {
    return (
      <>
        {/* Floating Chat Bubble */}
        {!isOpen && (
          <div 
            className="position-fixed"
            style={{ 
              zIndex: 1050,
              left: bubblePosition.x || 'auto',
              top: bubblePosition.y || 'auto',
              right: bubblePosition.x ? 'auto' : '16px',
              bottom: bubblePosition.y ? 'auto' : '16px'
            }}
          >
            <OverlayTrigger
              placement="left"
              overlay={
                <Tooltip id="chat-bubble-tooltip" style={{ textAlign: 'center', lineHeight: '1.4', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  Chat hỗ trợ khách hàng<br />
                  Có thể kéo thả để di chuyển
                </Tooltip>
              }
            >
              <div
                className="btn btn-primary rounded-circle shadow-lg d-flex align-items-center justify-content-center"
                style={{ 
                  width: '60px', 
                  height: '60px',
                  cursor: isDragging ? 'grabbing' : 'grab',
                  transition: isDragging ? 'none' : 'transform 0.2s ease-in-out',
                  userSelect: 'none'
                }}
                onMouseDown={handleMouseDown}
                onClick={handleBubbleClick}
                onMouseEnter={(e) => !isDragging && (e.currentTarget.style.transform = 'scale(1.1)')}
                onMouseLeave={(e) => !isDragging && (e.currentTarget.style.transform = 'scale(1)')}
              >
                <i className="bi bi-chat-dots fs-4 text-white"></i>
              </div>
            </OverlayTrigger>
          </div>
        )}

        {/* Chat Window */}
        {isOpen && (
          <div 
            className="position-fixed bottom-0 end-0 m-3"
            style={{ zIndex: 1049, width: '350px' }}
          >
            <Card className="shadow-lg border-0">
              <Card.Header 
                className="bg-primary text-white d-flex justify-content-between align-items-center"
              >
                <div className="d-flex align-items-center">
                  <i className="bi bi-chat-dots me-2"></i>
                  <span className="fw-bold">Hỗ trợ tư vấn khách hàng</span>
                </div>
                <Button
                  variant="link"
                  className="text-white p-0"
                  onClick={() => setIsOpen(false)}
                >
                  <i className="bi bi-x-lg"></i>
                </Button>
              </Card.Header>

              <div>
                <div 
                  className="p-3 bg-light text-center"
                  style={{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
                >
                  <div>
                    <i className="bi bi-person-lock fs-1 text-muted mb-3 d-block"></i>
                    <h6 className="text-muted">Vui lòng đăng nhập để nhận được hỗ trợ tư vấn</h6>
                    <p className="small text-muted mt-2">
                      Bạn cần đăng nhập vào tài khoản để có thể sử dụng dịch vụ chat hỗ trợ
                    </p>
                  </div>
                </div>

                <Card.Footer className="p-2">
                  <div className="d-flex gap-2">
                    <Form.Control
                      type="text"
                      placeholder="Vui lòng đăng nhập để chat..."
                      disabled
                      size="sm"
                    />
                    <Button 
                      variant="primary"
                      size="sm"
                      disabled
                    >
                      <i className="bi bi-send"></i>
                    </Button>
                  </div>
                </Card.Footer>
              </div>
            </Card>
          </div>
        )}
      </>
    );
  }

  // Only show chat widget for customers
  if (user.accountTypeName !== "Customer") {
    return null;
  }

  return (
    <>
      {/* Floating Chat Bubble */}
      {!isOpen && (
        <div 
          className="position-fixed"
          style={{ 
            zIndex: 1050,
            left: bubblePosition.x || 'auto',
            top: bubblePosition.y || 'auto',
            right: bubblePosition.x ? 'auto' : '16px',
            bottom: bubblePosition.y ? 'auto' : '16px'
          }}
        >
          <OverlayTrigger
            placement="left"
            overlay={
              <Tooltip id="chat-bubble-tooltip-logged-in" style={{ textAlign: 'center', lineHeight: '1.4', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                Chat hỗ trợ khách hàng<br />
                Có thể kéo thả để di chuyển<br />
                <small className="text-muted">Bấm để mở chat</small>
              </Tooltip>
            }
          >
            <div
              className="btn btn-primary rounded-circle shadow-lg d-flex align-items-center justify-content-center position-relative"
              style={{ 
                width: '60px', 
                height: '60px',
                cursor: isDragging ? 'grabbing' : 'grab',
                transition: isDragging ? 'none' : 'transform 0.2s ease-in-out',
                userSelect: 'none'
              }}
              onMouseDown={handleMouseDown}
              onClick={handleBubbleClick}
              onMouseEnter={(e) => !isDragging && (e.currentTarget.style.transform = 'scale(1.1)')}
              onMouseLeave={(e) => !isDragging && (e.currentTarget.style.transform = 'scale(1)')}
            >
              <i className="bi bi-chat-dots fs-4 text-white"></i>
              {/* Unread indicator */}
              {unreadCount > 0 && (
                <span 
                  className="position-absolute badge rounded-pill bg-danger"
                  style={{ 
                    fontSize: '0.6rem',
                    top: '8px',
                    right: '8px',
                    transform: 'translate(50%, -50%)'
                  }}
                >
                  {unreadCount}
                  <span className="visually-hidden">new messages</span>
                </span>
              )}
            </div>
          </OverlayTrigger>
        </div>
      )}

      {/* Chat Window */}
      {isOpen && (
        <div 
          className="position-fixed bottom-0 end-0 m-3"
          style={{ zIndex: 1049, width: '350px' }}
        >
          <Card className="shadow-lg border-0">
            <Card.Header 
              className="bg-primary text-white d-flex justify-content-between align-items-center"
            >
              <div className="d-flex align-items-center">
                <i className="bi bi-chat-dots me-2"></i>
                <span className="fw-bold">Hỗ trợ tư vấn khách hàng</span>
              </div>
              <Button
                variant="link"
                className="text-white p-0"
                onClick={() => setIsOpen(false)}
              >
                <i className="bi bi-x-lg"></i>
              </Button>
            </Card.Header>

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
                            </div>
                          ))}
                        </div>
                      ));
                    })()}

                    {/* Typing Indicator */}
                    {typingUsers.length > 0 && (
                      <div className="mb-3 d-flex justify-content-start">
                        <div 
                          className="me-2 rounded-circle text-white d-flex align-items-center justify-content-center flex-shrink-0"
                          style={{ 
                            width: '32px', 
                            height: '32px', 
                            fontSize: '12px', 
                            fontWeight: 'bold',
                            backgroundColor: getAvatarColor(typingUsers[0].userName, false)
                          }}
                        >
                          {getAvatarLetter(typingUsers[0].userName, false)}
                        </div>
                        
                        <div 
                          className="p-2 rounded-3 bg-white border d-flex align-items-center"
                          style={{ 
                            maxWidth: '70%',
                            borderRadius: '18px 18px 18px 4px'
                          }}
                        >
                          <div className="typing-dots me-2">
                            <span></span>
                            <span></span>
                            <span></span>
                          </div>
                          <small className="text-muted">
                            {typingUsers[0].userName} đang soạn tin nhắn...
                          </small>
                        </div>
                      </div>
                    )}

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
                      onChange={handleInputChange}
                      placeholder="Type your message..."
                      disabled={loading || !chatbox}
                      size="sm"
                      onKeyDown={(e) => {
                        if (e.key === 'Enter' && !e.shiftKey) {
                          e.preventDefault();
                          sendMessage(e);
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
          </Card>
        </div>
      )}
    </>
  );
};

export default ChatWidget;
