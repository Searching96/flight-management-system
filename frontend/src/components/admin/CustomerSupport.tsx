import React, { useState, useEffect, useRef } from "react";
import {
  Container,
  Row,
  Col,
  Form,
  Button,
  Spinner,
  InputGroup,
} from "react-bootstrap";
import { chatService, messageService } from "../../services";
import { Chatbox } from "../../models/Chat";
import { useAuth } from "../../hooks/useAuth";
import { webSocketService } from "../../services/websocketService";
import { accountChatboxService } from "../../services/accountChatboxService";
import "./CustomerSupport.css";

interface FormattedMessage {
  messageId?: number;
  chatboxId: number;
  employeeId?: number;
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

const CustomerSupport: React.FC = () => {
  const { user } = useAuth();
  const [chatboxes, setChatboxes] = useState<Chatbox[]>([]);
  const [unreadCounts, setUnreadCounts] = useState<Record<number, number>>({});
  const [selectedChatbox, setSelectedChatbox] = useState<Chatbox | null>(null);
  const [messages, setMessages] = useState<FormattedMessage[]>([]);
  const [newMessage, setNewMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [chatboxListLoading, setChatboxListLoading] = useState(false);
  const [error, setError] = useState("");
  const [sendingMessage, setSendingMessage] = useState(false);
  const [sortOption, setSortOption] = useState("Thời điểm yêu cầu tư vấn");
  const [isNearBottom, setIsNearBottom] = useState(true);
  const [typingUsers, setTypingUsers] = useState<TypingUser[]>([]);
  const [isTyping, setIsTyping] = useState(false);
  const [showSidebar, setShowSidebar] = useState(true);
  const [isMobile, setIsMobile] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const messagesContainerRef = useRef<HTMLDivElement>(null);
  const pollingIntervalRef = useRef<ReturnType<typeof setInterval> | null>(
    null
  );
  const chatboxPollingIntervalRef = useRef<ReturnType<
    typeof setInterval
  > | null>(null);
  const typingTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    loadChatboxes();
    startChatboxPolling();
    
    // Check if mobile on mount and add resize listener
    const checkMobile = () => {
      const mobile = window.innerWidth < 768;
      setIsMobile(mobile);
      // On mobile, hide sidebar when chat is selected
      if (mobile && selectedChatbox) {
        setShowSidebar(false);
      }
    };
    
    checkMobile();
    window.addEventListener('resize', checkMobile);
    
    return () => {
      stopChatboxPolling();
      window.removeEventListener('resize', checkMobile);
    };
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
        behavior: "smooth",
      });
    }
  };

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const container = e.currentTarget;
    const isAtBottom =
      container.scrollHeight - container.scrollTop <=
      container.clientHeight + 50; // 50px threshold
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
      if (sortOption === "Thời điểm yêu cầu tư vấn") {
        data = await chatService.getAllChatboxesSortedByCustomerTime();
      } else if (sortOption === "Số lượng nhân viên đã hỗ trợ") {
        data = await chatService.getAllChatboxesSortedByEmployeeSupportCount();
      } else if (sortOption === "Hoạt động gần đây") {
        data = await chatService.getAllChatboxesSortedByRecentActivity();
      } else {
        // For other options, use the default API for now
        data = await chatService.getAllChatboxes();
      }

      setChatboxes(data);
      console.log("Loaded chatboxes:", data);

      // Load unread counts for employee
      if (user?.id) {
        try {
          console.log("Loading unread counts for user:", user.id);
          const unreadData =
            await accountChatboxService.getUnreadCountsForAllChatboxes(user.id);

          // Set unread count to 0 for currently selected chatbox
          if (
            selectedChatbox?.chatboxId &&
            unreadData[selectedChatbox.chatboxId]
          ) {
            unreadData[selectedChatbox.chatboxId] = 0;
          }

          console.log("Unread counts received:", unreadData);
          setUnreadCounts(unreadData);
        } catch (error) {
          console.error("Failed to load unread counts:", error);
          // Set empty unread counts on error
          setUnreadCounts({});
        }
      }
    } catch (err: any) {
      setError("Failed to load chatboxes");
      console.error("Error loading chatboxes:", err);
    } finally {
      setLoading(false);
      setChatboxListLoading(false);
    }
  };

  const loadMessages = async (chatboxId: number) => {
    try {
      const data = await messageService.getMessagesByChatboxId(chatboxId);
      const formattedMessages = data.map((msg) => ({
        ...msg,
        isFromCustomer: !msg.employeeId,
      }));
      setMessages(formattedMessages);
    } catch (error) {
      console.error("Error loading messages:", error);
      setError("Failed to load messages");
    }
  };

  const startPolling = () => {
    if (pollingIntervalRef.current) return;

    pollingIntervalRef.current = setInterval(async () => {
      if (selectedChatbox?.chatboxId) {
        try {
          const data = await messageService.getMessagesByChatboxId(
            selectedChatbox.chatboxId
          );
          const formattedMessages = data.map((msg) => ({
            ...msg,
            isFromCustomer: !msg.employeeId,
          }));
          // Only update if there are actually new messages
          if (JSON.stringify(formattedMessages) !== JSON.stringify(messages)) {
            setMessages(formattedMessages);

            // Update last visit time when new messages arrive in selected chatbox
            if (user?.id) {
              try {
                await accountChatboxService.updateLastVisitTime(
                  user.id,
                  selectedChatbox.chatboxId
                );
                console.log("Last visit time updated due to new messages");
              } catch (error) {
                console.error(
                  "Failed to update last visit time after new messages:",
                  error
                );
              }
            }
          }
        } catch (error) {
          console.error("Failed to poll messages:", error);
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
        if (sortOption === "Thời điểm yêu cầu tư vấn") {
          data = await chatService.getAllChatboxesSortedByCustomerTime();
        } else if (sortOption === "Số lượng nhân viên đã hỗ trợ") {
          data = await chatService.getAllChatboxesSortedByEmployeeSupportCount();
        } else if (sortOption === "Hoạt động gần đây") {
          data = await chatService.getAllChatboxesSortedByRecentActivity();
        } else {
          // Default fallback
          data = await chatService.getAllChatboxes();
        }

        // Only update if there are actually changes
        if (JSON.stringify(data) !== JSON.stringify(chatboxes)) {
          setChatboxes(data);
        }

        // Always poll unread counts during chatbox polling
        if (user?.id) {
          try {
            const unreadData =
              await accountChatboxService.getUnreadCountsForAllChatboxes(
                user.id
              );

            // Set unread count to 0 for currently selected chatbox
            if (
              selectedChatbox?.chatboxId &&
              unreadData[selectedChatbox.chatboxId]
            ) {
              unreadData[selectedChatbox.chatboxId] = 0;
            }

            // Always update unread counts to ensure real-time updates
            setUnreadCounts((prev) => {
              // Only log if there are actual changes
              if (JSON.stringify(unreadData) !== JSON.stringify(prev)) {
                console.log(
                  "Unread counts updated during polling:",
                  unreadData
                );
              }
              return unreadData;
            });
          } catch (error) {
            console.error("Failed to poll unread counts:", error);
          }
        }
      } catch (error) {
        console.error("Failed to poll chatboxes:", error);
      }
    }, 2000); // Poll every 2 seconds for more responsive updates
  };

  const stopChatboxPolling = () => {
    if (chatboxPollingIntervalRef.current) {
      clearInterval(chatboxPollingIntervalRef.current);
      chatboxPollingIntervalRef.current = null;
    }
  };

  const handleChatboxSelect = async (chatbox: Chatbox) => {
    setSelectedChatbox(chatbox);
    setError("");
    
    // On mobile, hide sidebar when chat is selected
    if (isMobile) {
      setShowSidebar(false);
    }

    // Immediately set unread count to 0 for UI responsiveness
    setUnreadCounts((prev) => ({
      ...prev,
      [chatbox.chatboxId!]: 0,
    }));

    // Update last visit time when selecting chatbox
    if (user?.id && chatbox.chatboxId) {
      try {
        console.log(
          `Updating last visit time for user ${user.id} and chatbox ${chatbox.chatboxId}`
        );
        await accountChatboxService.updateLastVisitTime(
          user.id,
          chatbox.chatboxId
        );
        console.log("Last visit time updated successfully");
      } catch (error) {
        console.error("Failed to update last visit time:", error);
      }
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setNewMessage(value);

    if (!selectedChatbox || !user?.id) return;

    // Start typing indicator
    if (!isTyping && value.trim()) {
      setIsTyping(true);
      webSocketService.startTyping(
        selectedChatbox.chatboxId!.toString(),
        user.id.toString(),
        "employee",
        user.accountName || "Employee"
      );
    }

    // Reset typing timeout
    if (typingTimeoutRef.current) {
      clearTimeout(typingTimeoutRef.current);
      typingTimeoutRef.current = null;
    }

    // Stop typing after 2 seconds of inactivity
    typingTimeoutRef.current = setTimeout(() => {
      if (isTyping && selectedChatbox && user?.id) {
        setIsTyping(false);
        webSocketService.stopTyping(
          selectedChatbox.chatboxId!.toString(),
          user.id.toString(),
          "employee",
          user.accountName || "Employee"
        );
      }
    }, 2000);
  };

  const sendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !selectedChatbox || !user?.id) return;

    try {
      setSendingMessage(true);

      // Stop typing immediately when sending
      if (isTyping) {
        setIsTyping(false);
        webSocketService.stopTyping(
          selectedChatbox.chatboxId!.toString(),
          user.id.toString(),
          "employee",
          user.accountName || "Employee"
        );
        if (typingTimeoutRef.current) {
          clearTimeout(typingTimeoutRef.current);
          typingTimeoutRef.current = null;
        }
      }

      await messageService.createEmployeeMessage(
        selectedChatbox.chatboxId!,
        user.id,
        newMessage.trim()
      );

      // Notify WebSocket about new message
      webSocketService.notifyNewMessage(
        selectedChatbox.chatboxId!.toString(),
        user.id.toString(),
        "employee"
      );

      await loadMessages(selectedChatbox.chatboxId!);
      setNewMessage("");
    } catch (error) {
      console.error("Failed to send message:", error);
      setError("Failed to send message. Please try again.");
    } finally {
      setSendingMessage(false);
    }
  };

  useEffect(() => {
    if (selectedChatbox && user?.id) {
      loadMessages(selectedChatbox.chatboxId!);
      startPolling();
      setIsNearBottom(true);

      // Connect to WebSocket for this chat
      webSocketService.connect(
        selectedChatbox.chatboxId!.toString(),
        user.id.toString(),
        "employee",
        user.accountName || "Employee"
      );

      // Set up WebSocket event listeners
      const handleTypingStart = (data: TypingUser) => {
        if (data.userType === "customer") {
          setTypingUsers((prev) => {
            const exists = prev.some(
              (u) => u.userId === data.userId && u.userType === data.userType
            );
            if (!exists) {
              return [...prev, data];
            }
            return prev;
          });
        }
      };

      const handleTypingStop = (data: TypingUser) => {
        if (data.userType === "customer") {
          setTypingUsers((prev) =>
            prev.filter(
              (u) => !(u.userId === data.userId && u.userType === data.userType)
            )
          );
        }
      };

      const handleNewMessage = () => {
        // Reload messages when new message arrives
        loadMessages(selectedChatbox.chatboxId!);

        // Update last visit time immediately for selected chatbox
        if (user?.id && selectedChatbox?.chatboxId) {
          accountChatboxService
            .updateLastVisitTime(user.id, selectedChatbox.chatboxId)
            .then(() => {
              console.log(
                "Last visit time updated for selected chatbox after new message"
              );

              // Keep unread count at 0 for selected chatbox
              setUnreadCounts((prev) => ({
                ...prev,
                [selectedChatbox.chatboxId!]: 0,
              }));
            })
            .catch((error) =>
              console.error(
                "Failed to update last visit time for selected chatbox:",
                error
              )
            );
        }

        // Also reload unread counts for all other chatboxes when new message arrives
        if (user?.id) {
          setTimeout(() => {
            accountChatboxService
              .getUnreadCountsForAllChatboxes(user.id!)
              .then((unreadData) => {
                // Always set unread count to 0 for currently selected chatbox
                if (selectedChatbox?.chatboxId) {
                  unreadData[selectedChatbox.chatboxId] = 0;
                }

                console.log(
                  "Unread counts updated after new message:",
                  unreadData
                );
                setUnreadCounts(unreadData);
              })
              .catch((error) =>
                console.error(
                  "Failed to reload unread counts after new message:",
                  error
                )
              );
          }, 200); // Reduce delay to make it more responsive
        }
      };

      webSocketService.onTypingStart(handleTypingStart);
      webSocketService.onTypingStop(handleTypingStop);
      webSocketService.onNewMessage(handleNewMessage);

      return () => {
        webSocketService.removeEventListener("typing_start", handleTypingStart);
        webSocketService.removeEventListener("typing_stop", handleTypingStop);
        webSocketService.removeEventListener("new_message", handleNewMessage);
      };
    } else {
      stopPolling();
      webSocketService.disconnect();
      setTypingUsers([]);
    }
    return () => stopPolling();
  }, [selectedChatbox, user]);

  const getAvatarLetter = (name?: string, isFromCustomer?: boolean) => {
    if (name && name.trim()) {
      const words = name.trim().split(" ");
      if (words.length >= 2) {
        const lastTwo = words.slice(-2);
        return (lastTwo[0].charAt(0) + lastTwo[1].charAt(0)).toUpperCase();
      } else {
        return words[0].charAt(0).toUpperCase();
      }
    }
    return isFromCustomer ? "KH" : "NV";
  };

  const getAvatarColor = (name?: string, isFromCustomer?: boolean) => {
    // Define a palette of 255 basic colors
    const colorPalette = [
      "#FF0000",
      "#00FF00",
      "#0000FF",
      "#FFFF00",
      "#FF00FF",
      "#00FFFF",
      "#800000",
      "#008000",
      "#000080",
      "#808000",
      "#800080",
      "#008080",
      "#C0C0C0",
      "#808080",
      "#9999FF",
      "#993366",
      "#FFFFCC",
      "#CCFFFF",
      "#660066",
      "#FF8080",
      "#0066CC",
      "#CCCCFF",
      "#000080",
      "#FF00FF",
      "#FFFF00",
      "#00FFFF",
      "#800080",
      "#800000",
      "#008080",
      "#0000FF",
      "#00CCFF",
      "#CCFFFF",
      "#CCFFCC",
      "#FFFF99",
      "#99CCFF",
      "#FF99CC",
      "#CC99FF",
      "#FFCC99",
      "#3366FF",
      "#33CCCC",
      "#99CC00",
      "#FFCC00",
      "#FF9900",
      "#FF6600",
      "#666699",
      "#969696",
      "#003366",
      "#339966",
      "#003300",
      "#333300",
      "#993300",
      "#993366",
      "#333399",
      "#333333",
      "#FFF",
      "#000",
      "#FF6B6B",
      "#4ECDC4",
      "#45B7D1",
      "#96CEB4",
      "#FFEAA7",
      "#DDA0DD",
      "#98D8C8",
      "#F7DC6F",
      "#BB8FCE",
      "#85C1E9",
      "#F8C471",
      "#82E0AA",
      "#F1948A",
      "#85C1E9",
      "#F4D03F",
      "#AED6F1",
      "#A9DFBF",
      "#F9E79F",
      "#D7BDE2",
      "#A3E4D7",
      "#FAD7A0",
      "#D5A6BD",
      "#AED6F1",
      "#A9CCE3",
      "#F7DC6F",
      "#A9DFBF",
      "#F1948A",
      "#85C1E9",
      "#F4D03F",
      "#AED6F1",
      "#A9DFBF",
      "#F9E79F",
      "#E74C3C",
      "#3498DB",
      "#2ECC71",
      "#F39C12",
      "#9B59B6",
      "#1ABC9C",
      "#E67E22",
      "#34495E",
      "#16A085",
      "#27AE60",
      "#2980B9",
      "#8E44AD",
      "#2C3E50",
      "#F1C40F",
      "#E67E22",
      "#95A5A6",
      "#D35400",
      "#C0392B",
      "#BDC3C7",
      "#7F8C8D",
      "#FF5733",
      "#FF8D1A",
      "#FFC300",
      "#DAF7A6",
      "#581845",
      "#900C3F",
      "#C70039",
      "#FF5733",
      "#FFC300",
      "#DAF7A6",
      "#28B463",
      "#2874A6",
      "#D4AC0D",
      "#CA6F1E",
      "#BA4A00",
      "#A93226",
      "#922B21",
      "#7D3C98",
      "#6C3483",
      "#5B2C6F",
      "#2E86AB",
      "#A23B72",
      "#F18F01",
      "#C73E1D",
      "#7209B7",
      "#2D1B69",
      "#F0544F",
      "#7FB069",
      "#C8C8A9",
      "#83AF9B",
      "#FC4445",
      "#3FEEE6",
      "#55A3FF",
      "#F19066",
      "#F5D547",
      "#C06C84",
      "#6C5B7B",
      "#C8A2C8",
      "#355C7D",
      "#F67280",
      "#C06C84",
      "#F8B195",
      "#C02942",
      "#542437",
      "#53777A",
      "#ECD078",
      "#D95B43",
      "#C02942",
      "#542437",
      "#53777A",
      "#A8E6CF",
      "#88D8A3",
      "#FFD3A5",
      "#FD9853",
      "#FF8A80",
      "#C8E6C9",
      "#B39DDB",
      "#90CAF9",
      "#A5D6A7",
      "#FFAB91",
      "#CE93D8",
      "#80CBC4",
      "#81C784",
      "#AED581",
      "#DCE775",
      "#FFF176",
      "#FFD54F",
      "#FFCC02",
      "#FF8F65",
      "#FF6E40",
      "#FF5722",
      "#795548",
      "#9E9E9E",
      "#607D8B",
      "#263238",
      "#37474F",
      "#455A64",
      "#546E7A",
      "#78909C",
      "#90A4AE",
      "#B0BEC5",
      "#CFD8DC",
      "#ECEFF1",
      "#FAFAFA",
      "#F5F5F5",
      "#EEEEEE",
      "#E0E0E0",
      "#BDBDBD",
      "#9E9E9E",
      "#757575",
      "#616161",
      "#424242",
      "#212121",
      "#FF8A65",
      "#FF7043",
      "#FF5722",
      "#F4511E",
      "#E64A19",
      "#D84315",
      "#BF360C",
      "#FF6F00",
      "#FF8F00",
      "#FFA000",
      "#FFB300",
      "#FFC107",
      "#FFCA28",
      "#FFD54F",
      "#FFECB3",
      "#827717",
      "#9E9D24",
      "#AFB42B",
      "#C0CA33",
      "#CDDC39",
      "#D4E157",
      "#DCE775",
      "#F0F4C3",
      "#33691E",
      "#558B2F",
      "#689F38",
      "#7CB342",
      "#8BC34A",
      "#9CCC65",
      "#AED581",
      "#DCEDC8",
      "#00695C",
      "#00796B",
      "#00897B",
      "#009688",
      "#26A69A",
      "#4DB6AC",
      "#80CBC4",
      "#B2DFDB",
      "#006064",
      "#0097A7",
      "#00ACC1",
      "#00BCD4",
      "#26C6DA",
      "#4DD0E1",
      "#80DEEA",
      "#B2EBF2",
      "#01579B",
      "#0277BD",
      "#0288D1",
      "#039BE5",
      "#03A9F4",
      "#29B6F6",
      "#4FC3F7",
      "#B3E5FC",
      "#1A237E",
      "#303F9F",
      "#3F51B5",
      "#5C6BC0",
      "#7986CB",
      "#9FA8DA",
      "#C5CAE9",
      "#E8EAF6",
      "#4A148C",
      "#6A1B9A",
      "#7B1FA2",
      "#8E24AA",
      "#9C27B0",
      "#AB47BC",
      "#BA68C8",
      "#E1BEE7",
      "#880E4F",
      "#AD1457",
      "#C2185B",
      "#D81B60",
      "#E91E63",
      "#EC407A",
      "#F06292",
      "#F8BBD9",
      "#BF360C",
      "#D84315",
      "#E64A19",
      "#F4511E",
      "#FF5722",
      "#FF7043",
      "#FF8A65",
      "#FFCCBC",
    ];

    // Generate hash from name for consistent color assignment
    let hash = 0;
    const str = name || (isFromCustomer ? "Customer" : "Employee");
    for (let i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }

    // Use hash to pick a color from palette
    const colorIndex = Math.abs(hash) % colorPalette.length;
    return colorPalette[colorIndex];
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    // Check if it's today
    if (date.toDateString() === today.toDateString()) {
      return "Hôm nay";
    }

    // Check if it's yesterday
    if (date.toDateString() === yesterday.toDateString()) {
      return "Hôm qua";
    }

    // Format as dd/mm/yyyy for other dates
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  const groupMessagesByDate = (messages: FormattedMessage[]) => {
    const groups: { [key: string]: FormattedMessage[] } = {};

    messages.forEach((message) => {
      const date = new Date(message.sendTime).toDateString();
      if (!groups[date]) {
        groups[date] = [];
      }
      groups[date].push(message);
    });

    return groups;
  };

  const getLastMessagePrefix = (chatbox: Chatbox) => {
    if (!chatbox.lastMessageContent) return "";

    // If message is from customer, no prefix
    if (chatbox.isLastMessageFromCustomer) {
      return "";
    }

    // If message is from current user (employee), show "Bạn: "
    if (chatbox.lastMessageEmployeeId === user?.id) {
      return "Bạn: ";
    }

    // If message is from another employee, show their name (max 2 last words)
    // Using placeholder data for now
    const senderName = chatbox.lastMessageSenderName || "Nhân viên ABC";
    const words = senderName.trim().split(" ");
    if (words.length >= 2) {
      const lastTwoWords = words.slice(-2).join(" ");
      return `${lastTwoWords}: `;
    } else {
      return `${words[0]}: `;
    }
  };

  // Filter chatboxes to only show those that have messages
  const filteredChatboxes = chatboxes.filter(
    (chatbox) =>
      chatbox.lastMessageContent && chatbox.lastMessageContent.trim() !== ""
  );

  const truncateMessage = (message: string, maxLength: number = 15) => {
    if (!message) return "";
    if (message.length <= maxLength) return message;
    return message.substring(0, maxLength) + "...";
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={8} className="text-center">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Đang tải...</span>
            </Spinner>
            <p className="mt-3">Đang tải hỗ trợ khách hàng...</p>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <div style={{ height: "90vh", display: "flex", flexDirection: "column" }}>
      {/* Header */}
      <div className="bg-white border-bottom px-3 px-md-4 pb-2">
        <div className="d-flex align-items-center justify-content-between">
          <h4 className="mb-0 d-flex align-items-center">
            {isMobile && (
              <Button
                variant="link"
                className="p-0 me-2 text-dark"
                onClick={() => setShowSidebar(!showSidebar)}
                style={{ fontSize: "1.5rem", textDecoration: "none" }}
              >
                <i className={`bi ${showSidebar ? 'bi-x' : 'bi-list'}`}></i>
              </Button>
            )}
            <i className="bi bi-headset me-2 text-primary"></i>
            <span className="d-none d-sm-inline">Hỗ trợ khách hàng</span>
            <span className="d-inline d-sm-none">Hỗ trợ KH</span>
          </h4>
          {isMobile && selectedChatbox && (
            <div className="d-flex align-items-center">
              <div
                className="rounded-circle text-white d-flex align-items-center justify-content-center me-2"
                style={{
                  width: "32px",
                  height: "32px",
                  fontSize: "12px",
                  fontWeight: "bold",
                  backgroundColor: getAvatarColor(selectedChatbox.customerName, true),
                }}
              >
                {getAvatarLetter(selectedChatbox.customerName, true)}
              </div>
              <h6 className="mb-0">{selectedChatbox.customerName || "Khách hàng"}</h6>
            </div>
          )}
        </div>
      </div>

      <div className="flex-grow-1 d-flex position-relative" style={{ overflow: "hidden" }}>
        {/* Sidebar - Chat List */}
        <div
          className={`bg-white border-end sidebar-chat-list ${isMobile && !showSidebar ? 'd-none' : ''}`}
          style={{
            width: isMobile ? "100%" : "325px",
            maxWidth: isMobile ? "100%" : "325px",
            display: "flex",
            flexDirection: "column",
            position: isMobile ? "absolute" : "relative",
            top: 0,
            left: 0,
            bottom: 0,
            zIndex: isMobile ? 1000 : "auto",
          }}
        >
          {/* Sort */}
          <div className="px-3 pb-2 border-bottom">
            <Form.Label className="small text-muted mb-2">
              Sắp xếp theo
            </Form.Label>
            <Form.Select
              value={sortOption}
              onChange={(e) => setSortOption(e.target.value)}
              size="sm"
            >
              <option value="Thời điểm yêu cầu tư vấn">
                Thời điểm yêu cầu tư vấn
              </option>
              <option value="Số lượng nhân viên đã hỗ trợ">
                Số lượng nhân viên đã hỗ trợ
              </option>
              <option value="Hoạt động gần đây">Hoạt động gần đây</option>
            </Form.Select>
          </div>

          {/* Chat List */}
          <div className="flex-grow-1" style={{ overflowY: "auto" }}>
            {chatboxListLoading ? (
              <div
                className="d-flex justify-content-center align-items-center"
                style={{ height: "200px" }}
              >
                <div className="text-center">
                  <Spinner animation="border" variant="primary" />
                  <p className="mt-2 text-muted small">Đang tải...</p>
                </div>
              </div>
            ) : (
              <>
                {filteredChatboxes.map((chatbox) => (
                  <div
                    key={chatbox.chatboxId}
                    className={`d-flex align-items-center p-2 p-md-3 border-bottom cursor-pointer chat-list-item ${
                      selectedChatbox?.chatboxId === chatbox.chatboxId
                        ? "bg-light"
                        : ""
                    }`}
                    style={{ cursor: "pointer" }}
                    onClick={() => handleChatboxSelect(chatbox)}
                    onMouseEnter={(e) => {
                      if (selectedChatbox?.chatboxId !== chatbox.chatboxId) {
                        e.currentTarget.style.backgroundColor = "#f8f9fa";
                      }
                    }}
                    onMouseLeave={(e) => {
                      if (selectedChatbox?.chatboxId !== chatbox.chatboxId) {
                        e.currentTarget.style.backgroundColor = "";
                      }
                    }}
                  >
                    {/* Avatar */}
                    <div
                      className="rounded-circle text-white d-flex align-items-center justify-content-center flex-shrink-0 me-2 me-md-3"
                      style={{
                        width: isMobile ? "44px" : "50px",
                        height: isMobile ? "44px" : "50px",
                        fontSize: isMobile ? "12px" : "14px",
                        fontWeight: "bold",
                        backgroundColor: getAvatarColor(
                          chatbox.customerName,
                          true
                        ),
                      }}
                    >
                      {getAvatarLetter(chatbox.customerName, true)}
                    </div>

                    {/* Chat Info */}
                    <div className="flex-grow-1" style={{ minWidth: 0 }}>
                      <div className="d-flex justify-content-between align-items-center mb-1">
                        <h6 className="mb-0 text-truncate" style={{ fontSize: isMobile ? "0.95rem" : "1rem" }}>
                          {chatbox.customerName || "Khách hàng"}
                        </h6>
                        <small className="text-muted" style={{ fontSize: isMobile ? "0.7rem" : "0.875rem" }}>
                          {chatbox.lastMessageTime
                            ? formatTime(chatbox.lastMessageTime)
                            : "14:30"}
                        </small>
                      </div>

                      {chatbox.lastMessageContent && (
                        <div className="d-flex justify-content-between align-items-center">
                          <p
                            className={`mb-0 text-muted text-truncate flex-grow-1 me-2 ${
                              (unreadCounts[chatbox.chatboxId!] || 0) > 0
                                ? "fw-bold"
                                : ""
                            }`}
                            style={{ fontSize: isMobile ? "0.8rem" : "0.875rem" }}
                          >
                            {getLastMessagePrefix(chatbox)}
                            {truncateMessage(
                              chatbox.lastMessageContent ||
                                "Tin nhắn mẫu từ khách hàng"
                            )}
                          </p>
                          {(() => {
                            const unreadCount =
                              unreadCounts[chatbox.chatboxId!] || 0;
                            return unreadCount > 0 ? (
                              <span
                                className="badge bg-danger rounded-pill flex-shrink-0"
                                style={{ fontSize: "0.7rem" }}
                              >
                                {unreadCount}
                              </span>
                            ) : null;
                          })()}
                        </div>
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
        <div className="flex-grow-1 d-flex flex-column" style={{ minWidth: 0 }}>
          {selectedChatbox ? (
            <>
              {/* Chat Header - Hide on mobile as it's in main header */}
              <div className={`bg-white border-bottom px-3 px-md-4 py-1 align-items-center ${isMobile ? 'd-none' : 'd-flex'}`}>
                <div
                  className="rounded-circle text-white d-flex align-items-center justify-content-center me-3"
                  style={{
                    width: "40px",
                    height: "40px",
                    fontSize: "14px",
                    fontWeight: "bold",
                    backgroundColor: getAvatarColor(
                      selectedChatbox.customerName,
                      true
                    ),
                  }}
                >
                  {getAvatarLetter(selectedChatbox.customerName, true)}
                </div>
                <div>
                  <h6 className="mb-0">
                    {selectedChatbox.customerName || "Khách hàng"}
                  </h6>
                </div>
              </div>

              {/* Messages */}
              <div
                ref={messagesContainerRef}
                className="flex-grow-1 p-2 p-md-3 messages-container"
                style={{ overflowY: "auto", backgroundColor: "#f5f5f5" }}
                onScroll={handleScroll}
              >
                {(() => {
                  const messageGroups = groupMessagesByDate(messages);
                  const sortedDates = Object.keys(messageGroups).sort(
                    (a, b) => new Date(a).getTime() - new Date(b).getTime()
                  );

                  return sortedDates.map((dateKey) => (
                    <div key={dateKey}>
                      {/* Date Separator */}
                      <div className="d-flex justify-content-center my-2 my-md-3">
                        <div
                          className="px-2 px-md-3 py-1 bg-white rounded-pill text-muted"
                          style={{ border: "1px solid #e0e0e0", fontSize: isMobile ? "0.75rem" : "0.875rem" }}
                        >
                          {formatDate(messageGroups[dateKey][0].sendTime)}
                        </div>
                      </div>

                      {/* Messages for this date */}
                      {messageGroups[dateKey].map((message, index) => {
                        const isCurrentUser =
                          !message.isFromCustomer &&
                          message.employeeId === user?.id;
                        const shouldShowOnRight = isCurrentUser;

                        return (
                          <div
                            key={message.messageId || `${dateKey}-${index}`}
                            className={`mb-2 mb-md-3 d-flex ${
                              shouldShowOnRight
                                ? "justify-content-end"
                                : "justify-content-start"
                            }`}
                          >
                            {!shouldShowOnRight && (
                              <div
                                className="rounded-circle text-white d-flex align-items-center justify-content-center me-2 flex-shrink-0 chat-avatar"
                                style={{
                                  width: isMobile ? "28px" : "32px",
                                  height: isMobile ? "28px" : "32px",
                                  fontSize: isMobile ? "11px" : "12px",
                                  fontWeight: "bold",
                                  backgroundColor: message.isFromCustomer
                                    ? getAvatarColor(
                                        selectedChatbox.customerName,
                                        true
                                      )
                                    : getAvatarColor(
                                        message.employeeName,
                                        false
                                      ),
                                }}
                              >
                                {message.isFromCustomer
                                  ? getAvatarLetter(
                                      selectedChatbox.customerName,
                                      true
                                    )
                                  : getAvatarLetter(
                                      message.employeeName,
                                      false
                                    )}
                              </div>
                            )}

                            <div className="message-bubble" style={{ maxWidth: isMobile ? "80%" : "70%" }}>
                              <div
                                className={`p-2 rounded-3 ${
                                  isCurrentUser
                                    ? "text-white"
                                    : message.isFromCustomer
                                    ? "bg-white text-dark"
                                    : "text-dark"
                                }`}
                                style={{
                                  backgroundColor: isCurrentUser
                                    ? "#0084ff"
                                    : message.isFromCustomer
                                    ? "#ffffff"
                                    : "#e9ecef",
                                  borderRadius: shouldShowOnRight
                                    ? "18px 18px 4px 18px"
                                    : "18px 18px 18px 4px",
                                  fontSize: isMobile ? "0.9rem" : "1rem",
                                }}
                              >
                                {!isCurrentUser && (
                                  <div className="fw-bold text-muted" style={{ fontSize: isMobile ? "0.75rem" : "0.875rem" }}>
                                    {message.isFromCustomer
                                      ? selectedChatbox.customerName
                                      : message.employeeName}
                                  </div>
                                )}
                                <div style={{ wordBreak: "break-word" }}>{message.content}</div>
                                <div
                                  className={`text-xs mt-1 ${
                                    isCurrentUser ? "text-light" : "text-muted"
                                  }`}
                                  style={{ fontSize: isMobile ? "0.65rem" : "0.7rem" }}
                                >
                                  {formatTime(message.sendTime)}
                                </div>
                              </div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  ));
                })()}

                {/* Typing Indicator */}
                {typingUsers.length > 0 && (
                  <div className="mb-3 d-flex justify-content-start">
                    <div
                      className="rounded-circle text-white d-flex align-items-center justify-content-center me-2 flex-shrink-0"
                      style={{
                        width: "32px",
                        height: "32px",
                        fontSize: "12px",
                        fontWeight: "bold",
                        backgroundColor: getAvatarColor(
                          selectedChatbox?.customerName,
                          true
                        ),
                      }}
                    >
                      {getAvatarLetter(selectedChatbox?.customerName, true)}
                    </div>

                    <div style={{ maxWidth: "70%" }}>
                      <div
                        className="p-2 rounded-3 bg-white text-dark d-flex align-items-center"
                        style={{ borderRadius: "18px 18px 18px 4px" }}
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
                  </div>
                )}

                <div ref={messagesEndRef} />
              </div>

              {/* Message Input */}
              <div className="bg-white border-top p-2 p-md-3 message-input-area">
                <Form onSubmit={sendMessage}>
                  <InputGroup size={isMobile ? "sm" : undefined}>
                    <Form.Control
                      type="text"
                      placeholder={isMobile ? "Nhập tin..." : "Nhập tin nhắn..."}
                      value={newMessage}
                      onChange={handleInputChange}
                      disabled={sendingMessage}
                      style={{
                        borderRadius: "20px 0 0 20px",
                        border: "1px solid #ddd",
                        outline: "none",
                        boxShadow: "none",
                        fontSize: isMobile ? "0.9rem" : "1rem",
                      }}
                      onFocus={(e) => {
                        e.target.style.borderColor = "#ddd";
                        e.target.style.boxShadow = "none";
                      }}
                    />
                    <Button
                      type="submit"
                      variant="primary"
                      disabled={sendingMessage || !newMessage.trim()}
                      style={{
                        borderRadius: "0 20px 20px 0",
                        minWidth: isMobile ? "50px" : "60px",
                        fontSize: isMobile ? "0.9rem" : "1rem",
                      }}
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
                <i
                  className="bi bi-chat-square-dots"
                  style={{ fontSize: "4rem" }}
                ></i>
                <h5 className="mt-3">Chọn một hội thoại để bắt đầu</h5>
                <p>
                  Chọn một khách hàng từ danh sách bên trái để xem và trả lời
                  tin nhắn
                </p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CustomerSupport;
