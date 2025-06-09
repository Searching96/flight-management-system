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
    
    @GetMapping("/chatbox/{chatboxId}")
    public ResponseEntity<List<MessageDto>> getMessagesByChatboxId(@PathVariable Integer chatboxId) {
        List<MessageDto> messages = messageService.getMessagesByChatboxId(chatboxId);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/employee")
    public ResponseEntity<MessageDto> createEmployeeMessage(@RequestBody Map<String, Object> requestBody) {
        Integer chatboxId = (Integer) requestBody.get("chatboxId");
        Integer employeeId = (Integer) requestBody.get("employeeId");
        String content = (String) requestBody.get("content");
        
        MessageDto message = messageService.createEmployeeMessage(chatboxId, employeeId, content);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
    
    @PostMapping("/customer")
    public ResponseEntity<MessageDto> createCustomerMessage(@RequestBody Map<String, Object> requestBody) {
        Integer chatboxId = (Integer) requestBody.get("chatboxId");
        String content = (String) requestBody.get("content");
        
        MessageDto message = messageService.createCustomerMessage(chatboxId, content);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
}
