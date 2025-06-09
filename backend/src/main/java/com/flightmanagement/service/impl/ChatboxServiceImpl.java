package com.flightmanagement.service.impl;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.entity.Chatbox;
import com.flightmanagement.mapper.ChatboxMapper;
import com.flightmanagement.repository.AccountRepository;
import com.flightmanagement.repository.ChatboxRepository;
import com.flightmanagement.repository.MessageRepository;
import com.flightmanagement.repository.CustomerRepository;
import com.flightmanagement.service.ChatboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatboxServiceImpl implements ChatboxService {
    
    @Autowired
    private ChatboxRepository chatboxRepository;
    
    @Autowired
    private ChatboxMapper chatboxMapper;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<ChatboxDto> getAllChatboxes() {
        List<Chatbox> chatboxes = chatboxRepository.findAllActive();
        return chatboxes.stream()
                .map(this::enrichChatboxWithMessageInfo)
                .collect(Collectors.toList());
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
}
