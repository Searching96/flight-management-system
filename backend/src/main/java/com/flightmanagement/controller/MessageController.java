package com.flightmanagement.controller;

import com.flightmanagement.dto.MessageDto;
import com.flightmanagement.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@RequestParam Integer chatboxId, 
                                                 @RequestParam String content, 
                                                 @RequestParam Integer messageType) {
        MessageDto sentMessage = messageService.sendMessage(chatboxId, content, messageType);
        return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
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
    
    @GetMapping("/chatbox/{chatboxId}/recent")
    public ResponseEntity<List<MessageDto>> getRecentMessagesByChatboxId(@PathVariable Integer chatboxId, 
                                                                        @RequestParam(defaultValue = "20") int limit) {
        List<MessageDto> messages = messageService.getRecentMessagesByChatboxId(chatboxId, limit);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/type/{messageType}")
    public ResponseEntity<List<MessageDto>> getMessagesByType(@PathVariable Integer messageType) {
        List<MessageDto> messages = messageService.getMessagesByType(messageType);
        return ResponseEntity.ok(messages);
    }
}
