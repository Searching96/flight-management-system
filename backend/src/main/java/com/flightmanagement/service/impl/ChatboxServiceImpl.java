package com.flightmanagement.service.impl;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.entity.Chatbox;
import com.flightmanagement.mapper.ChatboxMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.ChatboxRepository;
import com.flightmanagement.repository.MessageRepository;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.service.ChatboxService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatboxServiceImpl implements ChatboxService {

    private final ChatboxRepository chatboxRepository;

    private final ChatboxMapper chatboxMapper;

    private final MessageRepository messageRepository;

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public ChatboxServiceImpl(ChatboxRepository chatboxRepository, ChatboxMapper chatboxMapper,
                              MessageRepository messageRepository, AccountRepository accountRepository,
                              CustomerRepository customerRepository) {
        this.chatboxRepository = chatboxRepository;
        this.chatboxMapper = chatboxMapper;
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<ChatboxDto> getAllChatboxes() {
        List<Chatbox> chatboxes = chatboxRepository.findAllActive();
        return chatboxes.stream()
            .map(this::enrichChatboxWithMessageInfo)
            .collect(Collectors.toList());
    }

    @Override
    public List<ChatboxDto> getAllChatboxesSortedByCustomerMessageTime() {
        List<Chatbox> chatboxes = chatboxRepository.findAllActive();
        
        // Get all chatboxes with enriched message info
        List<ChatboxDto> enrichedChatboxes = chatboxes.stream()
            .map(this::enrichChatboxWithCustomerMessageTime)
            .collect(Collectors.toList());
        
        // Sort by latest customer message time (most recent first)
        return enrichedChatboxes.stream()
            .sorted((c1, c2) -> {
                LocalDateTime time1 = c1.getLastCustomerMessageTime();
                LocalDateTime time2 = c2.getLastCustomerMessageTime();
                
                // If both have customer messages, sort by time (newest first)
                if (time1 != null && time2 != null) {
                    return time2.compareTo(time1);
                }
                // If only one has customer messages, prioritize it
                if (time1 != null) return -1;
                if (time2 != null) return 1;
                
                // If neither has customer messages, sort by chatbox creation (newest first)
                return c2.getChatboxId().compareTo(c1.getChatboxId());
            })
            .collect(Collectors.toList());
    }

    private ChatboxDto enrichChatboxWithCustomerMessageTime(Chatbox chatbox) {
        ChatboxDto dto = enrichChatboxWithMessageInfo(chatbox);
        
        // Get the latest customer message time specifically
        messageRepository.findLatestCustomerMessageByChatboxId(chatbox.getChatboxId())
            .ifPresent(customerMessage -> {
                dto.setLastCustomerMessageTime(customerMessage.getSendTime());
            });
        
        return dto;
    }

    private ChatboxDto enrichChatboxWithMessageInfo(Chatbox chatbox) {
        ChatboxDto dto = chatboxMapper.toDto(chatbox);

        // Get last message with detailed info
        messageRepository.findTopByChatboxIdOrderBySendTimeDesc(chatbox.getChatboxId())
            .ifPresent(lastMessage -> {
                dto.setLastMessageContent(lastMessage.getContent());
                dto.setLastMessageTime(lastMessage.getSendTime());
                dto.setIsLastMessageFromCustomer(lastMessage.getEmployeeId() == null);
                dto.setLastMessageEmployeeId(lastMessage.getEmployeeId());

                // Set sender name
                if (lastMessage.getEmployeeId() != null) {
                    // Message from employee
                    String employeeName = accountRepository.findById(lastMessage.getEmployeeId())
                            .map(account -> account.getAccountName())
                            .orElse("Employee");
                    dto.setLastMessageSenderName(employeeName);
                } else {
                    // Message from customer
                    dto.setLastMessageSenderName(dto.getCustomerName());
                }
            });

        return dto;
    }

    @Override
    public ChatboxDto getChatboxById(Integer id) {
        Chatbox chatbox = chatboxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chatbox not found with id: " + id));
        return chatboxMapper.toDto(chatbox);
    }

    @Override
    public ChatboxDto createChatbox(ChatboxDto chatboxDto) {
        System.out.println("=== ChatboxServiceImpl.createChatbox START ===");
        System.out.println("Creating chatbox with customerId: " + chatboxDto.getCustomerId());

        if (chatboxDto.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        Chatbox chatbox = chatboxMapper.toEntity(chatboxDto);

        // Đảm bảo customerId không bị null
        if (chatbox.getCustomerId() == null) {
            System.out.println("WARNING: customerId is null after mapping, setting manually...");
            chatbox.setCustomerId(chatboxDto.getCustomerId());
        }

        System.out.println("Entity before save - customerId: " + chatbox.getCustomerId());

        chatbox.setDeletedAt(null);
        Chatbox savedChatbox = chatboxRepository.save(chatbox);

        System.out.println("Chatbox saved with ID: " + savedChatbox.getChatboxId());
        System.out.println("=== ChatboxServiceImpl.createChatbox END ===");

        return chatboxMapper.toDto(savedChatbox);
    }

    @Override
    public void deleteChatbox(Integer id) {
        Chatbox chatbox = chatboxRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Chatbox not found with id: " + id));

        chatbox.setDeletedAt(LocalDateTime.now());
        chatboxRepository.save(chatbox);
    }

    @Override
    public ChatboxDto getChatboxByCustomerId(Integer customerId) {
        System.out.println("Getting chatbox for customer ID: " + customerId);

        // Get existing chatbox (should be only one per customer)
        List<Chatbox> chatboxes = chatboxRepository.findByCustomerId(customerId);
        if (!chatboxes.isEmpty()) {
            System.out.println("Found existing chatbox: " + chatboxes.get(0).getChatboxId());
            return chatboxMapper.toDto(chatboxes.get(0));
        }

        // If no chatbox found, create a new one
        System.out.println("No chatbox found, creating new one for customer: " + customerId);
        return createChatboxWithCustomerId(customerId);
    }

    private ChatboxDto createChatboxWithCustomerId(Integer customerId) {
        System.out.println("=== ChatboxServiceImpl.createChatboxWithCustomerId START ===");
        System.out.println("Creating new chatbox for customer ID: " + customerId);

        if (customerId == null) {
            throw new IllegalArgumentException("Cannot create chatbox - customer ID is null");
        }

        try {
            // Kiểm tra khách hàng có tồn tại không
            boolean customerExists = customerRepository.findById(customerId).isPresent();
            if (!customerExists) {
                throw new RuntimeException("Customer not found with ID: " + customerId);
            }

            ChatboxDto chatboxDto = new ChatboxDto();
            chatboxDto.setCustomerId(customerId);
            System.out.println("Created ChatboxDto with customerId: " + chatboxDto.getCustomerId());

            ChatboxDto result = createChatbox(chatboxDto);

            System.out.println("=== ChatboxServiceImpl.createChatboxWithCustomerId END ===");
            return result;
        } catch (Exception e) {
            System.err.println("=== ERROR in createChatboxWithCustomerId ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ERROR in createChatboxWithCustomerId ===");
            throw e;
        }
    }

    @Override
    public List<ChatboxDto> getChatboxesByCustomerId(Integer customerId) {
        List<Chatbox> chatboxes = chatboxRepository.findByCustomerId(customerId);
        return chatboxMapper.toDtoList(chatboxes);
    }

    @Override
    public List<ChatboxDto> getAllChatboxesSortedByRecentActivity() {
        System.out.println("=== ChatboxServiceImpl.getAllChatboxesSortedByRecentActivity START ===");
        
        try {
            List<Chatbox> chatboxes = chatboxRepository.findAllActive();
            System.out.println("Found " + chatboxes.size() + " active chatboxes");
            
            // Enrich each chatbox with message info
            List<ChatboxDto> enrichedChatboxes = chatboxes.stream()
                .map(this::enrichChatboxWithMessageInfo)
                .collect(Collectors.toList());
            
            // Sort by last message time (most recent first)
            enrichedChatboxes.sort((a, b) -> {
                LocalDateTime timeA = a.getLastMessageTime();
                LocalDateTime timeB = b.getLastMessageTime();
                
                // If both have messages, sort by time (newest first)
                if (timeA != null && timeB != null) {
                    return timeB.compareTo(timeA);
                }
                // If only one has messages, prioritize it
                if (timeA != null) return -1;
                if (timeB != null) return 1;
                
                // If neither has messages, sort by chatbox ID (newest first)
                return b.getChatboxId().compareTo(a.getChatboxId());
            });
            
            System.out.println("=== Sorting results by recent activity ===");
            for (ChatboxDto dto : enrichedChatboxes) {
                System.out.println("Chatbox " + dto.getChatboxId() + 
                    " - Last message time: " + dto.getLastMessageTime() +
                    " - Customer: " + dto.getCustomerName());
            }
            
            System.out.println("=== ChatboxServiceImpl.getAllChatboxesSortedByRecentActivity END ===");
            return enrichedChatboxes;
            
        } catch (Exception e) {
            System.err.println("=== ERROR in getAllChatboxesSortedByRecentActivity ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            throw e;
        }
    }

    @Override
    public List<ChatboxDto> getAllChatboxesSortedByEmployeeSupportCount() {
        System.out.println("=== ChatboxServiceImpl.getAllChatboxesSortedByEmployeeSupportCount START ===");
        
        try {
            List<Chatbox> chatboxes = chatboxRepository.findAllActive();
            System.out.println("Found " + chatboxes.size() + " active chatboxes");
            
            // Enrich each chatbox with employee support count and customer message info
            List<ChatboxDto> enrichedChatboxes = chatboxes.stream()
                .map(this::enrichChatboxWithEmployeeSupportInfo)
                .collect(Collectors.toList());
            
            // Sort by employee support count (ascending: 0, 1, 2...), then by last customer message time (descending: newest first)
            enrichedChatboxes.sort((a, b) -> {
                // Primary sort: employee support count (fewer employees = higher priority)
                int employeeCountA = a.getEmployeeSupportCount() != null ? a.getEmployeeSupportCount() : 0;
                int employeeCountB = b.getEmployeeSupportCount() != null ? b.getEmployeeSupportCount() : 0;
                
                int employeeCountCompare = Integer.compare(employeeCountA, employeeCountB);
                if (employeeCountCompare != 0) {
                    return employeeCountCompare; // 0 employee support comes first, then 1, then 2...
                }
                
                // Secondary sort: most recent customer message first when employee count is equal
                LocalDateTime timeA = a.getLastCustomerMessageTime();
                LocalDateTime timeB = b.getLastCustomerMessageTime();
                
                if (timeA == null && timeB == null) {
                    return 0; // Both have no customer messages, order doesn't matter
                }
                if (timeA == null) {
                    return 1; // B has customer message, prioritize it
                }
                if (timeB == null) {
                    return -1; // A has customer message, prioritize it
                }
                return timeB.compareTo(timeA); // Most recent customer message first
            });
            
            System.out.println("=== Sorting results ===");
            for (ChatboxDto dto : enrichedChatboxes) {
                System.out.println("Chatbox " + dto.getChatboxId() + 
                    " - Employee support: " + dto.getEmployeeSupportCount() + 
                    " - Last customer msg: " + dto.getLastCustomerMessageTime() +
                    " - Customer: " + dto.getCustomerName());
            }
            
            System.out.println("=== ChatboxServiceImpl.getAllChatboxesSortedByEmployeeSupportCount END ===");
            return enrichedChatboxes;
            
        } catch (Exception e) {
            System.err.println("=== ERROR in getAllChatboxesSortedByEmployeeSupportCount ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            throw e;
        }
    }
    
    private ChatboxDto enrichChatboxWithEmployeeSupportInfo(Chatbox chatbox) {
        ChatboxDto dto = enrichChatboxWithMessageInfo(chatbox);
        
        // Count unique employees who have sent messages in this chatbox
        try {
            List<Integer> uniqueEmployeeIds = messageRepository.findDistinctEmployeeIdsByChatboxId(chatbox.getChatboxId());
            dto.setEmployeeSupportCount(uniqueEmployeeIds.size());
            
            System.out.println("Chatbox " + chatbox.getChatboxId() + " - Found " + uniqueEmployeeIds.size() + " unique employees: " + uniqueEmployeeIds);
        } catch (Exception e) {
            System.err.println("Error counting employees for chatbox " + chatbox.getChatboxId() + ": " + e.getMessage());
            dto.setEmployeeSupportCount(0);
        }
        
        // Get the latest customer message time specifically
        messageRepository.findLatestCustomerMessageByChatboxId(chatbox.getChatboxId())
            .ifPresent(customerMessage -> {
                dto.setLastCustomerMessageTime(customerMessage.getSendTime());
            });
        
        System.out.println("Chatbox " + chatbox.getChatboxId() + 
            " - Employee support count: " + dto.getEmployeeSupportCount() + 
            " - Last customer message: " + dto.getLastCustomerMessageTime());
        
        return dto;
    }
}