package com.flightmanagement.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> typingUsers = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToChatbox = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("WebSocket connection established: " + sessionId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String sessionId = session.getId();
        
        try {
            JsonNode data = objectMapper.readTree(message.getPayload().toString());
            String type = data.get("type").asText();
            
            switch (type) {
                case "join_chat":
                    handleJoinChat(sessionId, data);
                    break;
                case "typing_start":
                    handleTypingStart(sessionId, data);
                    break;
                case "typing_stop":
                    handleTypingStop(sessionId, data);
                    break;
                case "new_message":
                    handleNewMessage(sessionId, data);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error handling WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket transport error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String sessionId = session.getId();
        String chatboxId = sessionToChatbox.get(sessionId);
        String userId = sessionToUser.get(sessionId);
        
        // Clean up typing status when user disconnects
        if (chatboxId != null && userId != null) {
            handleTypingStop(sessionId, createTypingData(chatboxId, userId));
        }
        
        // Remove session
        sessions.remove(sessionId);
        sessionToChatbox.remove(sessionId);
        sessionToUser.remove(sessionId);
        
        System.out.println("WebSocket connection closed: " + sessionId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void handleJoinChat(String sessionId, JsonNode data) {
        String chatboxId = data.get("chatboxId").asText();
        String userId = data.get("userId").asText();
        String userType = data.get("userType").asText(); // "employee" or "customer"
        
        sessionToChatbox.put(sessionId, chatboxId);
        sessionToUser.put(sessionId, userId + ":" + userType);
        
        System.out.println("User " + userId + " (" + userType + ") joined chat " + chatboxId);
    }

    private void handleTypingStart(String sessionId, JsonNode data) {
        String chatboxId = data.get("chatboxId").asText();
        String userId = data.get("userId").asText();
        String userType = data.get("userType").asText();
        String userName = data.get("userName").asText();
        
        String userKey = userId + ":" + userType;
        
        typingUsers.computeIfAbsent(chatboxId, k -> ConcurrentHashMap.newKeySet()).add(userKey);
        
        // Broadcast typing status to other users in the same chat (except sender)
        broadcastToChat(chatboxId, createTypingEvent("typing_start", userId, userType, userName), sessionId);
        
        System.out.println("User " + userName + " (" + userType + ") started typing in chat " + chatboxId);
    }

    private void handleTypingStop(String sessionId, JsonNode data) {
        String chatboxId = data.get("chatboxId").asText();
        String userId = data.get("userId").asText();
        String userType = data.get("userType").asText();
        String userName = data.get("userName").asText();
        
        String userKey = userId + ":" + userType;
        
        Set<String> typing = typingUsers.get(chatboxId);
        if (typing != null) {
            typing.remove(userKey);
            if (typing.isEmpty()) {
                typingUsers.remove(chatboxId);
            }
        }
        
        // Broadcast typing stop to other users in the same chat (except sender)
        broadcastToChat(chatboxId, createTypingEvent("typing_stop", userId, userType, userName), sessionId);
        
        System.out.println("User " + userName + " (" + userType + ") stopped typing in chat " + chatboxId);
    }

    private void handleNewMessage(String sessionId, JsonNode data) {
        String chatboxId = data.get("chatboxId").asText();
        String userId = data.get("userId").asText();
        String userType = data.get("userType").asText();
        
        // Stop typing when user sends message
        handleTypingStop(sessionId, data);
        
        // Broadcast new message event to other users in the same chat
        broadcastToChat(chatboxId, createMessageEvent("new_message", chatboxId), sessionId);
    }

    private void broadcastToChat(String chatboxId, String message, String excludeSessionId) {
        sessions.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(excludeSessionId))
            .filter(entry -> chatboxId.equals(sessionToChatbox.get(entry.getKey())))
            .forEach(entry -> {
                try {
                    entry.getValue().sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("Error broadcasting message: " + e.getMessage());
                }
            });
    }

    private String createTypingEvent(String type, String userId, String userType, String userName) {
        try {
            Map<String, Object> event = Map.of(
                "type", type,
                "userId", userId,
                "userType", userType,
                "userName", userName,
                "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            System.err.println("Error creating typing event: " + e.getMessage());
            return "{}";
        }
    }

    private String createMessageEvent(String type, String chatboxId) {
        try {
            Map<String, Object> event = Map.of(
                "type", type,
                "chatboxId", chatboxId,
                "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            System.err.println("Error creating message event: " + e.getMessage());
            return "{}";
        }
    }

    private JsonNode createTypingData(String chatboxId, String userInfo) {
        try {
            String[] parts = userInfo.split(":");
            String userId = parts[0];
            String userType = parts.length > 1 ? parts[1] : "unknown";
            
            Map<String, Object> data = Map.of(
                "chatboxId", chatboxId,
                "userId", userId,
                "userType", userType,
                "userName", "User"
            );
            return objectMapper.valueToTree(data);
        } catch (Exception e) {
            return objectMapper.createObjectNode();
        }
    }
}
