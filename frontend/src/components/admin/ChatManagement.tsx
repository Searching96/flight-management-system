import React, { useState, useEffect, useRef } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert, Badge, ListGroup, Spinner } from 'react-bootstrap';
import { chatService, messageService } from '../../services';
import { Chatbox, Message } from '../../models/Chat';
import { useAuth } from '../../hooks/useAuth';

const ChatManagement: React.FC = () => {
  const { user } = useAuth();
  const [chatboxes, setChatboxes] = useState<Chatbox[]>([]);
  const [selectedChatbox, setSelectedChatbox] = useState<Chatbox | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [sendingMessage, setSendingMessage] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    loadChatboxes();
  }, []);

  useEffect(() => {
    if (selectedChatbox) {
      loadMessages(selectedChatbox.chatboxId!);
    }
  }, [selectedChatbox]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const loadChatboxes = async () => {
    try {
      setLoading(true);
      // Load all chatboxes for employee management
      const data = await chatService.getAllChatboxes();
      setChatboxes(data);
    } catch (err: any) {
      setError('Failed to load chatboxes');
    } finally {
      setLoading(false);
    }
  };

  const loadMessages = async (chatboxId: number) => {
    try {
      const data = await messageService.getMessagesByChatboxId(chatboxId);
      // Map messages to include isFromCustomer based on employeeId
      const formattedMessages = data.map(msg => ({
        ...msg,
        isFromCustomer: !msg.employeeId // If employeeId is null, it's from customer
      }));
      setMessages(formattedMessages);
      // Mark as read when loading messages
      await chatService.markChatboxAsRead(chatboxId);
    } catch (err: any) {
      setError('Failed to load messages');
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
      
      // Create employee message using messageService
      await messageService.createEmployeeMessage(
        selectedChatbox.chatboxId!,
        newMessage.trim()
      );
      
      // Reload messages to get the updated list
      await loadMessages(selectedChatbox.chatboxId!);
      setNewMessage('');
      
    } catch (error) {
      console.error('Failed to send message:', error);
      setError('Failed to send message. Please try again.');
    } finally {
      setSendingMessage(false);
    }
  };

  const getChatboxStatusBadge = (chatbox: Chatbox) => {
    if (chatbox.unreadCount && chatbox.unreadCount > 0) {
      return <Badge bg="danger">{chatbox.unreadCount} new</Badge>;
    }
    return <Badge bg="success">Active</Badge>;
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

  if (loading) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8} className="text-center">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
            <p className="mt-3">Loading chat management...</p>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container fluid className="py-4">
      <Card className="mb-4">
        <Card.Header>
          <Card.Title as="h2" className="mb-0">💬 Chat Management</Card.Title>
          <p className="mb-0 text-muted">Manage customer support conversations</p>
        </Card.Header>
      </Card>

      {error && (
        <Alert variant="danger" className="mb-4">
          {error}
        </Alert>
      )}

      <Row style={{ height: '70vh' }}>
        {/* Chatbox List */}
        <Col md={4} className="border-end">
          <Card className="h-100">
            <Card.Header>
              <div className="d-flex justify-content-between align-items-center">
                <h5 className="mb-0">Customer Chats</h5>
                <Badge bg="info">{chatboxes.length} total</Badge>
              </div>
            </Card.Header>
            <Card.Body className="p-0" style={{ overflowY: 'auto' }}>
              <ListGroup variant="flush">
                {chatboxes.map(chatbox => (
                  <ListGroup.Item
                    key={chatbox.chatboxId}
                    action
                    active={selectedChatbox?.chatboxId === chatbox.chatboxId}
                    onClick={() => handleChatboxSelect(chatbox)}
                    className="d-flex justify-content-between align-items-start"
                  >
                    <div className="flex-grow-1">
                      <div className="d-flex justify-content-between align-items-center mb-1">
                        <strong>{chatbox.customerName || 'Customer'}</strong>
                        {getChatboxStatusBadge(chatbox)}
                      </div>
                      
                      {chatbox.lastMessageContent && (
                        <p className="mb-1 text-muted small">
                          {chatbox.lastMessageContent.length > 50 
                            ? chatbox.lastMessageContent.substring(0, 50) + '...'
                            : chatbox.lastMessageContent}
                        </p>
                      )}
                      
                      {chatbox.lastMessageTime && (
                        <small className="text-muted">
                          {new Date(chatbox.lastMessageTime).toLocaleString()}
                        </small>
                      )}
                    </div>
                  </ListGroup.Item>
                ))}
                {chatboxes.length === 0 && (
                  <ListGroup.Item>
                    <p className="text-muted text-center mb-0">No customer chats available</p>
                  </ListGroup.Item>
                )}
              </ListGroup>
            </Card.Body>
          </Card>
        </Col>

        {/* Chat Messages */}
        <Col md={8}>
          {selectedChatbox ? (
            <Card className="h-100 d-flex flex-column">
              <Card.Header>
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h5 className="mb-0">Chat with {selectedChatbox.customerName || 'Customer'}</h5>
                  </div>
                  {getChatboxStatusBadge(selectedChatbox)}
                </div>
              </Card.Header>

              {/* Messages Container */}
              <Card.Body className="flex-grow-1 d-flex flex-column p-0">
                <div className="flex-grow-1 p-3" style={{ overflowY: 'auto', maxHeight: 'calc(70vh - 200px)' }}>
                  {messages.map((message, index) => (
                    <div
                      key={index}
                      className={`mb-3 d-flex ${message.isFromCustomer ? 'justify-content-start' : 'justify-content-end'}`}
                    >
                      <div
                        className={`px-3 py-2 rounded ${
                          message.isFromCustomer 
                            ? 'bg-light text-dark' 
                            : 'bg-primary text-white'
                        }`}
                        style={{ maxWidth: '70%' }}
                      >
                        <div className="fw-bold small mb-1">
                          {message.employeeName || (message.isFromCustomer ? 'Customer' : 'Support')}
                        </div>
                        <div>{message.content}</div>
                        <div className={`small mt-1 ${message.isFromCustomer ? 'text-muted' : 'text-light'}`}>
                          {message.sendTime && new Date(message.sendTime).toLocaleTimeString()}
                        </div>
                      </div>
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
                    </div>
                  ))}
                  <div ref={messagesEndRef} />
                </div>

                {/* Message Input */}
                <div className="border-top p-3">
                  <Form onSubmit={sendMessage}>
                    <div className="d-flex gap-2">
                      <Form.Control
                        type="text"
                        placeholder="Type your response..."
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        disabled={sendingMessage}
                      />
                      <Button 
                        type="submit" 
                        variant="primary"
                        disabled={sendingMessage || !newMessage.trim()}
                      >
                        {sendingMessage ? (
                          <>
                            <Spinner animation="border" size="sm" className="me-1" />
                            Sending...
                          </>
                        ) : (
                          'Send'
                        )}
                      </Button>
                    </div>
                  </Form>
                </div>
              </Card.Body>
            </Card>
          ) : (
            <Card className="h-100 d-flex align-items-center justify-content-center">
              <Card.Body className="text-center">
                <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>💬</div>
                <h5>Select a chat to start responding</h5>
                <p className="text-muted">Choose a customer conversation from the list to begin providing support.</p>
              </Card.Body>
            </Card>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default ChatManagement;
