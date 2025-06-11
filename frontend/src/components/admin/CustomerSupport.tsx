import React, { useState, useEffect, useRef } from 'react';
import { Container, Row, Col, Card, Form, Button, Spinner, InputGroup } from 'react-bootstrap';
import { chatService, messageService } from '../../services';
import { Chatbox, Message } from '../../models/Chat';
import { useAuth } from '../../hooks/useAuth';

interface FormattedMessage {
  messageId?: number;
  chatboxId: number;
  employeeId?: number;
  content: string;
  sendTime: string;
  employeeName?: string;
  isFromCustomer: boolean;
}

const CustomerSupport: React.FC = () => {
  const { user } = useAuth();
  const [chatboxes, setChatboxes] = useState<Chatbox[]>([]);
  const [selectedChatbox, setSelectedChatbox] = useState<Chatbox | null>(null);
  const [messages, setMessages] = useState<FormattedMessage[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [chatboxListLoading, setChatboxListLoading] = useState(false); // New state for chatbox list loading
  const [error, setError] = useState('');
  const [sendingMessage, setSendingMessage] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortOption, setSortOption] = useState('Thời điểm yêu cầu tư vấn');
  const [isNearBottom, setIsNearBottom] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const messagesContainerRef = useRef<HTMLDivElement>(null);
  const pollingIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const chatboxPollingIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    loadChatboxes();
    startChatboxPolling();
    return () => stopChatboxPolling();
  }, []);

  // Add this useEffect to reload when sort option changes
  useEffect(() => {
    loadChatboxes();
    // Restart chatbox polling with new sort option
    stopChatboxPolling();
    startChatboxPolling();
  }, [sortOption]);

  useEffect(() => {
    if (selectedChatbox) {
      loadMessages(selectedChatbox.chatboxId!);
      startPolling();
      // Reset to bottom when selecting new chat
      setIsNearBottom(true);
    } else {
      stopPolling();
    }
    return () => stopPolling();
  }, [selectedChatbox]);

  useEffect(() => {
    // Only auto-scroll messages container if user is near bottom of chat
    if (messages.length > 0 && isNearBottom && selectedChatbox) {
      const timer = setTimeout(() => {
        scrollToBottom();
      }, 100);
      return () => clearTimeout(timer);
    }
  }, [messages.length, isNearBottom, selectedChatbox]);

  const scrollToBottom = () => {
    if (messagesContainerRef.current) {
      messagesContainerRef.current.scrollTo({
        top: messagesContainerRef.current.scrollHeight,
        behavior: 'smooth'
      });
    }
  };

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const container = e.currentTarget;
    const isAtBottom = container.scrollHeight - container.scrollTop <= container.clientHeight + 50; // 50px threshold
    setIsNearBottom(isAtBottom);
  };

  const loadChatboxes = async () => {
    try {
      // Only show full page loading on initial load
      if (chatboxes.length === 0) {
        setLoading(true);
      } else {
        setChatboxListLoading(true);
      }
      
      let data: Chatbox[];
      
      // Load chatboxes based on sort option
      if (sortOption === 'Thời điểm yêu cầu tư vấn') {
        data = await chatService.getAllChatboxesSortedByCustomerTime();
      } else if (sortOption === 'Số lượng nhân viên đã hỗ trợ') {
        data = await chatService.getAllChatboxesSortedByEmployeeSupportCount();
      } else if (sortOption === 'Hoạt động gần đây') {
        data = await chatService.getAllChatboxesSortedByRecentActivity();
      } else {
        // For other options, use the default API for now
        data = await chatService.getAllChatboxes();
      }
      
      setChatboxes(data);
    } catch (err: any) {
      setError('Failed to load chatboxes');
    } finally {
      setLoading(false);
      setChatboxListLoading(false);
    }
  };

  const loadMessages = async (chatboxId: number) => {
    try {
      const data = await messageService.getMessagesByChatboxId(chatboxId);
      const formattedMessages = data.map(msg => ({
        ...msg,
        isFromCustomer: !msg.employeeId
      }));
      setMessages(formattedMessages);
    } catch (err: any) {
      setError('Failed to load messages');
    }
  };

  const startPolling = () => {
    if (pollingIntervalRef.current) return;
    
    pollingIntervalRef.current = setInterval(async () => {
      if (selectedChatbox?.chatboxId) {
        try {
          const data = await messageService.getMessagesByChatboxId(selectedChatbox.chatboxId);
          const formattedMessages = data.map(msg => ({
            ...msg,
            isFromCustomer: !msg.employeeId
          }));
          // Only update if there are actually new messages
          if (JSON.stringify(formattedMessages) !== JSON.stringify(messages)) {
            setMessages(formattedMessages);
          }
        } catch (error) {
          console.error('Failed to poll messages:', error);
        }
      }
    }, 200);
  };

  const stopPolling = () => {
    if (pollingIntervalRef.current) {
      clearInterval(pollingIntervalRef.current);
      pollingIntervalRef.current = null;
    }
  };

  const startChatboxPolling = () => {
    if (chatboxPollingIntervalRef.current) return;
    
    chatboxPollingIntervalRef.current = setInterval(async () => {
      try {
        let data: Chatbox[];
        
        // Use the same sorting logic for polling - make sure all options are covered
        if (sortOption === 'Thời điểm yêu cầu tư vấn') {
          data = await chatService.getAllChatboxesSortedByCustomerTime();
        } else if (sortOption === 'Số lượng nhân viên đã hỗ trợ') {
          data = await chatService.getAllChatboxesSortedByEmployeeSupportCount();
        } else if (sortOption === 'Hoạt động gần đây') {
          data = await chatService.getAllChatboxesSortedByRecentActivity();
        } else {
          // Default fallback
          data = await chatService.getAllChatboxes();
        }
        
        // Only update if there are actually changes
        if (JSON.stringify(data) !== JSON.stringify(chatboxes)) {
          setChatboxes(data);
        }
      } catch (error) {
        console.error('Failed to poll chatboxes:', error);
      }
    }, 1000); // Poll every 1 second for chatbox list
  };

  const stopChatboxPolling = () => {
    if (chatboxPollingIntervalRef.current) {
      clearInterval(chatboxPollingIntervalRef.current);
      chatboxPollingIntervalRef.current = null;
    }
  };

  const handleChatboxSelect = (chatbox: Chatbox) => {
    setSelectedChatbox(chatbox);
    setError('');
  };

  const sendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !selectedChatbox || !user) return;

    try {
      setSendingMessage(true);
      
      await messageService.createEmployeeMessage(
        selectedChatbox.chatboxId!,
        user.id!,
        newMessage.trim()
      );
      
      await loadMessages(selectedChatbox.chatboxId!);
      setNewMessage('');
      
    } catch (error) {
      console.error('Failed to send message:', error);
      setError('Failed to send message. Please try again.');
    } finally {
      setSendingMessage(false);
    }
  };

  const getAvatarLetter = (name?: string, isFromCustomer?: boolean) => {
    if (name && name.trim()) {
      const words = name.trim().split(' ');
      if (words.length >= 2) {
        const lastTwo = words.slice(-2);
        return (lastTwo[0].charAt(0) + lastTwo[1].charAt(0)).toUpperCase();
      } else {
        return words[0].charAt(0).toUpperCase();
      }
    }
    return isFromCustomer ? 'KH' : 'NV';
  };

  const getAvatarColor = (name?: string, isFromCustomer?: boolean) => {
    if (isFromCustomer) {
      return '#0084ff';
    }
    
    let hash = 0;
    const str = name || 'Employee';
    for (let i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    
    const hue = Math.abs(hash) % 360;
    return `hsl(${hue}, 60%, 50%)`;
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = (now.getTime() - date.getTime()) / (1000 * 60 * 60);
    
    if (diffInHours < 24) {
      return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    } else {
      const diffInDays = Math.floor(diffInHours / 24);
      return `${diffInDays} ngày`;
    }
  };

  const getLastMessagePrefix = (chatbox: Chatbox) => {
    if (!chatbox.lastMessageContent) return '';
    
    // If message is from customer, no prefix
    if (chatbox.isLastMessageFromCustomer) {
      return '';
    }
    
    // If message is from current user (employee), show "Bạn: "
    if (chatbox.lastMessageEmployeeId === user?.id) {
      return 'Bạn: ';
    }
    
    // If message is from another employee, show their name (max 2 last words)
    // Using placeholder data for now
    const senderName = chatbox.lastMessageSenderName || 'Nhân viên ABC';
    const words = senderName.trim().split(' ');
    if (words.length >= 2) {
      const lastTwoWords = words.slice(-2).join(' ');
      return `${lastTwoWords}: `;
    } else {
      return `${words[0]}: `;
    }
  };

  const filteredChatboxes = chatboxes.filter(chatbox =>
    chatbox.customerName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    chatbox.lastMessageContent?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8} className="text-center">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
            <p className="mt-3">Loading customer support...</p>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <div style={{ height: '90vh', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <div className="bg-white border-bottom px-4 pb-2">
        <h4 className="mb-0 d-flex align-items-center">
          <i className="bi bi-headset me-2 text-primary"></i>
          Customer Support
        </h4>
      </div>

      <div className="flex-grow-1 d-flex" style={{ overflow: 'hidden' }}>
        {/* Sidebar - Chat List */}
        <div className="bg-white border-end" style={{ width: '350px', display: 'flex', flexDirection: 'column' }}>
          {/* Sort */}
          <div className="px-3 pb-2 border-bottom">
            <Form.Label className="small text-muted mb-2">Sắp xếp theo</Form.Label>
            <Form.Select
              value={sortOption}
              onChange={(e) => setSortOption(e.target.value)}
              size="sm"
            >
              <option value="Thời điểm yêu cầu tư vấn">Thời điểm yêu cầu tư vấn</option>
              <option value="Số lượng nhân viên đã hỗ trợ">Số lượng nhân viên đã hỗ trợ</option>
              <option value="Hoạt động gần đây">Hoạt động gần đây</option>
            </Form.Select>
          </div>

          {/* Chat List */}
          <div className="flex-grow-1" style={{ overflowY: 'auto' }}>
            {chatboxListLoading ? (
              <div className="d-flex justify-content-center align-items-center" style={{ height: '200px' }}>
                <div className="text-center">
                  <Spinner animation="border" variant="primary" />
                  <p className="mt-2 text-muted small">Đang tải...</p>
                </div>
              </div>
            ) : (
              <>
                {filteredChatboxes.map(chatbox => (
                  <div
                    key={chatbox.chatboxId}
                    className={`d-flex align-items-center p-3 border-bottom cursor-pointer ${
                      selectedChatbox?.chatboxId === chatbox.chatboxId ? 'bg-light' : ''
                    }`}
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleChatboxSelect(chatbox)}
                    onMouseEnter={(e) => {
                      if (selectedChatbox?.chatboxId !== chatbox.chatboxId) {
                        e.currentTarget.style.backgroundColor = '#f8f9fa';
                      }
                    }}
                    onMouseLeave={(e) => {
                      if (selectedChatbox?.chatboxId !== chatbox.chatboxId) {
                        e.currentTarget.style.backgroundColor = '';
                      }
                    }}
                  >
                    {/* Avatar */}
                    <div 
                      className="rounded-circle text-white d-flex align-items-center justify-content-center flex-shrink-0 me-3"
                      style={{
                        width: '50px',
                        height: '50px',
                        fontSize: '14px',
                        fontWeight: 'bold',
                        backgroundColor: getAvatarColor(chatbox.customerName, true)
                      }}
                    >
                      {getAvatarLetter(chatbox.customerName, true)}
                    </div>

                    {/* Chat Info */}
                    <div className="flex-grow-1 min-width-0">
                      <div className="d-flex justify-content-between align-items-center mb-1">
                        <h6 className="mb-0 text-truncate">{chatbox.customerName || 'Khách hàng'}</h6>
                        <small className="text-muted flex-shrink-0 ms-2">
                          {chatbox.lastMessageTime ? formatTime(chatbox.lastMessageTime) : '14:30'}
                        </small>
                      </div>
                      
                      {(chatbox.lastMessageContent || 'Tin nhắn mẫu từ khách hàng') && (
                        <p className="mb-0 text-muted small text-truncate">
                          {getLastMessagePrefix(chatbox)}{chatbox.lastMessageContent || 'Tin nhắn mẫu từ khách hàng'}
                        </p>
                      )}
                      
                      {/* Removed unread count badge */}
                    </div>
                  </div>
                ))}
                
                {filteredChatboxes.length === 0 && (
                  <div className="text-center p-4 text-muted">
                    <i className="bi bi-chat-square-dots fs-1 mb-3 d-block"></i>
                    Không có hội thoại nào
                  </div>
                )}
              </>
            )}
          </div>
        </div>

        {/* Main Chat Area */}
        <div className="flex-grow-1 d-flex flex-column">
          {selectedChatbox ? (
            <>
              {/* Chat Header */}
              <div className="bg-white border-bottom px-4 py-1 d-flex align-items-center">
                <div 
                  className="rounded-circle text-white d-flex align-items-center justify-content-center me-3"
                  style={{
                    width: '40px',
                    height: '40px',
                    fontSize: '14px',
                    fontWeight: 'bold',
                    backgroundColor: getAvatarColor(selectedChatbox.customerName, true)
                  }}
                >
                  {getAvatarLetter(selectedChatbox.customerName, true)}
                </div>
                <div>
                  <h6 className="mb-0">{selectedChatbox.customerName || 'Khách hàng'}</h6>
                  <small className="text-success">
                    <i className="bi bi-circle-fill me-1" style={{ fontSize: '8px' }}></i>
                    Đang hoạt động
                  </small>
                </div>
              </div>

              {/* Messages */}
              <div 
                ref={messagesContainerRef}
                className="flex-grow-1 p-3" 
                style={{ overflowY: 'auto', backgroundColor: '#f5f5f5' }}
                onScroll={handleScroll}
              >
                {messages.map((message, index) => {
                  const isCurrentUser = !message.isFromCustomer && message.employeeId === user?.id;
                  const shouldShowOnRight = isCurrentUser;
                  
                  return (
                    <div
                      key={message.messageId || index}
                      className={`mb-3 d-flex ${shouldShowOnRight ? 'justify-content-end' : 'justify-content-start'}`}
                    >
                      {!shouldShowOnRight && (
                        <div 
                          className="rounded-circle text-white d-flex align-items-center justify-content-center me-2 flex-shrink-0"
                          style={{
                            width: '32px',
                            height: '32px',
                            fontSize: '12px',
                            fontWeight: 'bold',
                            backgroundColor: message.isFromCustomer 
                              ? getAvatarColor(selectedChatbox.customerName, true)
                              : getAvatarColor(message.employeeName, false)
                          }}
                        >
                          {message.isFromCustomer 
                            ? getAvatarLetter(selectedChatbox.customerName, true)
                            : getAvatarLetter(message.employeeName, false)
                          }
                        </div>
                      )}
                      
                      <div style={{ maxWidth: '70%' }}>
                        <div 
                          className={`p-2 rounded-3 ${
                            isCurrentUser 
                              ? 'text-white'
                              : message.isFromCustomer
                                ? 'bg-white text-dark'
                                : 'text-dark'
                          }`}
                          style={{ 
                            backgroundColor: isCurrentUser 
                              ? '#0084ff'
                              : message.isFromCustomer 
                                ? '#ffffff'
                                : '#e9ecef',
                            borderRadius: shouldShowOnRight ? '18px 18px 4px 18px' : '18px 18px 18px 4px'
                          }}
                        >
                          {!isCurrentUser && (
                            <div className="small fw-bold text-muted">
                              {message.isFromCustomer ? selectedChatbox.customerName : message.employeeName}
                            </div>
                          )}
                          <div>{message.content}</div>
                          <div className={`text-xs mt-1 ${isCurrentUser ? 'text-light' : 'text-muted'}`} style={{ fontSize: '0.7rem' }}>
                            {formatTime(message.sendTime)}
                          </div>
                        </div>
                      </div>

                      {shouldShowOnRight && (
                        <div 
                          className="rounded-circle text-white d-flex align-items-center justify-content-center ms-2 flex-shrink-0"
                          style={{
                            width: '32px',
                            height: '32px',
                            fontSize: '12px',
                            fontWeight: 'bold',
                            backgroundColor: getAvatarColor(user?.accountName, false)
                          }}
                        >
                          {getAvatarLetter(user?.accountName, false)}
                        </div>
                      )}
                    </div>
                  );
                })}
                <div ref={messagesEndRef} />
              </div>

              {/* Message Input */}
              <div className="bg-white border-top p-3">
                <Form onSubmit={sendMessage}>
                  <InputGroup>
                    <Form.Control
                      type="text"
                      placeholder="Nhập tin nhắn..."
                      value={newMessage}
                      onChange={(e) => setNewMessage(e.target.value)}
                      disabled={sendingMessage}
                      style={{ 
                        borderRadius: '20px 0 0 20px', 
                        border: '1px solid #ddd',
                        outline: 'none',
                        boxShadow: 'none'
                      }}
                      onFocus={(e) => {
                        e.target.style.borderColor = '#ddd';
                        e.target.style.boxShadow = 'none';
                      }}
                    />
                    <Button 
                      type="submit" 
                      variant="primary"
                      disabled={sendingMessage || !newMessage.trim()}
                      style={{ borderRadius: '0 20px 20px 0', minWidth: '60px' }}
                    >
                      {sendingMessage ? (
                        <Spinner animation="border" size="sm" />
                      ) : (
                        <i className="bi bi-send-fill"></i>
                      )}
                    </Button>
                  </InputGroup>
                </Form>
                {error && (
                  <div className="text-danger small mt-2">
                    <i className="bi bi-exclamation-triangle me-1"></i>
                    {error}
                  </div>
                )}
              </div>
            </>
          ) : (
            <div className="flex-grow-1 d-flex align-items-center justify-content-center text-muted">
              <div className="text-center">
                <i className="bi bi-chat-square-dots" style={{ fontSize: '4rem' }}></i>
                <h5 className="mt-3">Chọn một hội thoại để bắt đầu</h5>
                <p>Chọn một khách hàng từ danh sách bên trái để xem và trả lời tin nhắn</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CustomerSupport;
