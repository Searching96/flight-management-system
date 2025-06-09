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
        System.out.println("Getting chatbox for customer ID: " + customerId);
        
        if (customerId == null) {
            System.err.println("Customer ID is null");
            return ResponseEntity.badRequest().build();
        }
        
        try {
            ChatboxDto chatbox = chatboxService.getChatboxByCustomerId(customerId);
            return ResponseEntity.ok(chatbox);
        } catch (Exception e) {
            System.err.println("Error getting chatbox for customer " + customerId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
