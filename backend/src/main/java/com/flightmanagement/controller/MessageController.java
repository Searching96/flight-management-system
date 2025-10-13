package com.flightmanagement.controller;

import com.flightmanagement.dto.MessageDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Operations related to messages")
public class MessageController {
    
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @Operation(summary = "Get messages by chatbox ID")
    @GetMapping("/chatbox/{chatboxId}")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getMessagesByChatboxId(@PathVariable Integer chatboxId) {
        List<MessageDto> messages = messageService.getMessagesByChatboxId(chatboxId);
        ApiResponse<List<MessageDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Messages retrieved successfully",
                messages,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Create a message from an employee")
    @PostMapping("/employee")
    public ResponseEntity<ApiResponse<MessageDto>> createEmployeeMessage(@RequestBody Map<String, Object> requestBody) {
        Integer chatboxId = (Integer) requestBody.get("chatboxId");
        Integer employeeId = (Integer) requestBody.get("employeeId");
        String content = (String) requestBody.get("content");
        
        MessageDto message = messageService.createEmployeeMessage(chatboxId, employeeId, content);
        ApiResponse<MessageDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Message created successfully",
                message,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    @Operation(summary = "Create a message from a customer")
    @PostMapping("/customer")
    public ResponseEntity<ApiResponse<MessageDto>> createCustomerMessage(@RequestBody Map<String, Object> requestBody) {
        Integer chatboxId = (Integer) requestBody.get("chatboxId");
        String content = (String) requestBody.get("content");
        
        MessageDto message = messageService.createCustomerMessage(chatboxId, content);
        ApiResponse<MessageDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Message created successfully",
                message,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
