package com.flightmanagement.controller;

import com.flightmanagement.dto.ChatboxDto;
import com.flightmanagement.entity.ApiResponse;
import com.flightmanagement.service.ChatboxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatboxes")
@Tag(name = "Chatbox", description = "Operations related to chatboxes")
public class ChatboxController {
    
    private final ChatboxService chatboxService;

    public ChatboxController(ChatboxService chatboxService) {
        this.chatboxService = chatboxService;
    }
    
    @Operation(summary = "Get all chatboxes")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatboxDto>>> getAllChatboxes() {
        List<ChatboxDto> chatboxes = chatboxService.getAllChatboxes();

        ApiResponse<List<ChatboxDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Chatboxes retrieved successfully",
                chatboxes,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Get all chatboxes sorted by customer message time")
    @GetMapping("/sorted-by-customer-time")
    public ResponseEntity<ApiResponse<List<ChatboxDto>>> getAllChatboxesSortedByCustomerMessageTime() {
        List<ChatboxDto> chatboxes = chatboxService.getAllChatboxesSortedByCustomerMessageTime();

        ApiResponse<List<ChatboxDto>> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Chatboxes sorted by customer message time retrieved successfully",
                chatboxes,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Get chatbox by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatboxDto>> getChatboxById(@PathVariable Integer id) {
        ChatboxDto chatbox = chatboxService.getChatboxById(id);

        ApiResponse<ChatboxDto> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                "Chatbox retrieved successfully",
                chatbox,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Create a new chatbox")
    @PostMapping
    public ResponseEntity<ApiResponse<ChatboxDto>> createChatbox(@RequestBody ChatboxDto chatboxDto) {
        ChatboxDto createdChatbox = chatboxService.createChatbox(chatboxDto);

        ApiResponse<ChatboxDto> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED,
                "Chatbox created successfully",
                createdChatbox,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    @Operation(summary = "Delete a chatbox by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChatbox(@PathVariable Integer id) {
        chatboxService.deleteChatbox(id);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.NO_CONTENT,
                "Chatbox deleted successfully",
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
    
    @Operation(summary = "Get chatbox by customer ID")
    @GetMapping("/customer/{customerId}/chatbox")
    public ResponseEntity<ApiResponse<ChatboxDto>> getChatboxByCustomerId(@PathVariable Integer customerId) {
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

            ApiResponse<ChatboxDto> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Chatbox retrieved successfully",
                    chatbox,
                    null
            );
            System.out.println("Successfully retrieved chatbox: " + (chatbox != null ? chatbox.getChatboxId() : "null"));
            System.out.println("=== ChatboxController.getChatboxByCustomerId END (Success) ===");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            System.err.println("=== ERROR in ChatboxController.getChatboxByCustomerId ===");
            System.err.println("Error getting chatbox for customer " + customerId + ": " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            System.err.println("=== END ERROR ===");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get all chatboxes sorted by employee support count")
    @GetMapping("/sorted-by-employee-support")
    public ResponseEntity<ApiResponse<List<ChatboxDto>>> getAllChatboxesSortedByEmployeeSupportCount() {
        System.out.println("=== ChatboxController.getAllChatboxesSortedByEmployeeSupportCount START ===");
        try {
            List<ChatboxDto> chatboxes = chatboxService.getAllChatboxesSortedByEmployeeSupportCount();
            ApiResponse<List<ChatboxDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Chatboxes sorted by employee support count retrieved successfully",
                    chatboxes,
                    null
            );
            System.out.println("Successfully retrieved " + chatboxes.size() + " chatboxes sorted by employee support count");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            System.err.println("Error in getAllChatboxesSortedByEmployeeSupportCount: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Get all chatboxes sorted by recent activity")
    @GetMapping("/sorted-by-recent-activity")
    public ResponseEntity<ApiResponse<List<ChatboxDto>>> getAllChatboxesSortedByRecentActivity() {
        System.out.println("=== ChatboxController.getAllChatboxesSortedByRecentActivity START ===");
        try {
            List<ChatboxDto> chatboxes = chatboxService.getAllChatboxesSortedByRecentActivity();

            ApiResponse<List<ChatboxDto>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK,
                    "Chatboxes sorted by recent activity retrieved successfully",
                    chatboxes,
                    null
            );
            System.out.println("Successfully retrieved " + chatboxes.size() + " chatboxes sorted by recent activity");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            System.err.println("Error in getAllChatboxesSortedByRecentActivity: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
