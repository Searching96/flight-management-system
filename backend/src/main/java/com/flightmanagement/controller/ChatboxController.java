package com.flightmanagement.controller;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.dto.ChatboxTestDto;
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

    @PostMapping("/test")
    public ResponseEntity<ChatboxDto> createChatboxTest(@RequestBody ChatboxTestDto chatboxDto) {
        ChatboxDto createdChatbox = chatboxService.createChatboxTest(chatboxDto);
        return new ResponseEntity<>(createdChatbox, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatbox(@PathVariable Integer id) {
        chatboxService.deleteChatbox(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ChatboxDto>> getChatboxesByCustomerId(@PathVariable Integer customerId) {
        List<ChatboxDto> chatboxes = chatboxService.getChatboxesByCustomerId(customerId);
        return ResponseEntity.ok(chatboxes);
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ChatboxDto>> getChatboxesByEmployeeId(@PathVariable Integer employeeId) {
        List<ChatboxDto> chatboxes = chatboxService.getChatboxesByEmployeeId(employeeId);
        return ResponseEntity.ok(chatboxes);
    }
    
    @GetMapping("/customer/{customerId}/employee/{employeeId}")
    public ResponseEntity<ChatboxDto> getChatboxByCustomerAndEmployee(@PathVariable Integer customerId, @PathVariable Integer employeeId) {
        ChatboxDto chatbox = chatboxService.getChatboxByCustomerAndEmployee(customerId, employeeId);
        return ResponseEntity.ok(chatbox);
    }
    
    @PostMapping("/get-or-create")
    public ResponseEntity<ChatboxDto> getOrCreateChatbox(@RequestParam Integer customerId, @RequestParam Integer employeeId) {
        ChatboxDto chatbox = chatboxService.getOrCreateChatbox(customerId, employeeId);
        return ResponseEntity.ok(chatbox);
    }
}
