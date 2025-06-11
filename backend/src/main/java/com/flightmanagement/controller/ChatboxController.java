package com.flightmanagement.controller;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.service.ChatboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatboxes")
public class ChatboxController {
    
    @Autowired
    private ChatboxService chatboxService;
    
    @GetMapping
    public ResponseEntity<List<ChatboxDto>> getAllChatboxes() {
        List<ChatboxDto> chatboxes = chatboxService.getAllChatboxes();
        return ResponseEntity.ok(chatboxes);
    }
    
    @GetMapping("/sorted-by-customer-time")
    public ResponseEntity<List<ChatboxDto>> getAllChatboxesSortedByCustomerMessageTime() {
        List<ChatboxDto> chatboxes = chatboxService.getAllChatboxesSortedByCustomerMessageTime();
        return ResponseEntity.ok(chatboxes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChatboxDto> getChatboxById(@PathVariable Integer id) {
        ChatboxDto chatbox = chatboxService.getChatboxById(id);
        return ResponseEntity.ok(chatbox);
    }
    
    @PostMapping
    public ResponseEntity<ChatboxDto> createChatbox(@RequestBody ChatboxDto chatboxDto) {
        ChatboxDto createdChatbox = chatboxService.createChatbox(chatboxDto);
        return new ResponseEntity<>(createdChatbox, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatbox(@PathVariable Integer id) {
        chatboxService.deleteChatbox(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/customer/{customerId}/chatbox")
    public ResponseEntity<ChatboxDto> getChatboxByCustomerId(@PathVariable Integer customerId) {
        System.out.println("=== ChatboxController.getChatboxByCustomerId START ===");
        System.out.println("Endpoint /customer/{customerId}/chatbox was called");
        System.out.println("Getting chatbox for customer ID: " + customerId);
        System.out.println("Request received at: " + java.time.LocalDateTime.now());
        
        if (customerId == null) {
            System.err.println("Customer ID is null");
            System.out.println("=== ChatboxController.getChatboxByCustomerId END (Bad Request) ===");
            return ResponseEntity.badRequest().build();
        }
        
        try {
            System.out.println("Calling chatboxService.getChatboxByCustomerId...");
            ChatboxDto chatbox = chatboxService.getChatboxByCustomerId(customerId);
            System.out.println("Successfully retrieved chatbox: " + (chatbox != null ? chatbox.getChatboxId() : "null"));
            System.out.println("=== ChatboxController.getChatboxByCustomerId END (Success) ===");
            return ResponseEntity.ok(chatbox);
        } catch (Exception e) {
            System.err.println("=== ERROR in ChatboxController.getChatboxByCustomerId ===");
            System.err.println("Error getting chatbox for customer " + customerId + ": " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/sorted-by-employee-support")
    public ResponseEntity<List<ChatboxDto>> getAllChatboxesSortedByEmployeeSupportCount() {
        System.out.println("=== ChatboxController.getAllChatboxesSortedByEmployeeSupportCount START ===");
        try {
            List<ChatboxDto> chatboxes = chatboxService.getAllChatboxesSortedByEmployeeSupportCount();
            System.out.println("Successfully retrieved " + chatboxes.size() + " chatboxes sorted by employee support count");
            return ResponseEntity.ok(chatboxes);
        } catch (Exception e) {
            System.err.println("Error in getAllChatboxesSortedByEmployeeSupportCount: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/sorted-by-recent-activity")
    public ResponseEntity<List<ChatboxDto>> getAllChatboxesSortedByRecentActivity() {
        System.out.println("=== ChatboxController.getAllChatboxesSortedByRecentActivity START ===");
        try {
            List<ChatboxDto> chatboxes = chatboxService.getAllChatboxesSortedByRecentActivity();
            System.out.println("Successfully retrieved " + chatboxes.size() + " chatboxes sorted by recent activity");
            return ResponseEntity.ok(chatboxes);
        } catch (Exception e) {
            System.err.println("Error in getAllChatboxesSortedByRecentActivity: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
