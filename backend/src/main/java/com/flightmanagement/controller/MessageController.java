package com.flightmanagement.controller;

import com.flightmanagement.dto.MessageDto;
import com.flightmanagement.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @GetMapping
    public ResponseEntity<List<MessageDto>> getAllMessages() {
        List<MessageDto> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Integer id) {
        MessageDto message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }
    
    @PostMapping
    public ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto messageDto) {
        MessageDto createdMessage = messageService.createMessage(messageDto);
        return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/chatbox/{chatboxId}")
    public ResponseEntity<List<MessageDto>> getMessagesByChatboxId(@PathVariable Integer chatboxId) {
        List<MessageDto> messages = messageService.getMessagesByChatboxId(chatboxId);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/customer")
    public ResponseEntity<MessageDto> createCustomerMessage(@RequestBody Map<String, Object> request) {
        try {
            Integer chatboxId = (Integer) request.get("chatboxId");
            String content = (String) request.get("content");
            
            MessageDto message = messageService.createCustomerMessage(chatboxId, content);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating customer message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/employee")
    public ResponseEntity<MessageDto> createEmployeeMessage(@RequestBody Map<String, Object> request) {
        try {
            Integer chatboxId = (Integer) request.get("chatboxId");
            String content = (String) request.get("content");
            
            MessageDto message = messageService.createEmployeeMessage(chatboxId, content);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating employee message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
