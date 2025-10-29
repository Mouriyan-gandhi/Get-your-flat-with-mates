package com.roommateai.controller;

import com.roommateai.model.User;
import com.roommateai.service.AuthService;
import com.roommateai.service.FirebaseChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat Controller
 * REST endpoints for real-time chat functionality
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private FirebaseChatService chatService;

    @Autowired
    private AuthService authService;

    /**
     * Create a chat room for matched users
     */
    @PostMapping("/rooms")
    public ResponseEntity<?> createChatRoom(@RequestParam Long matchId,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            String roomId = chatService.createChatRoom(matchId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("roomId", roomId);
            response.put("message", "Chat room created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create chat room");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Send a message to a chat room
     */
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable String roomId,
                                        @RequestBody Map<String, String> request,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            String messageText = request.get("message");
            if (messageText == null || messageText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
            }

            String messageId = chatService.sendMessage(roomId, user.getId(), messageText);
            
            Map<String, Object> response = new HashMap<>();
            response.put("messageId", messageId);
            response.put("message", "Message sent successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to send message");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get messages from a chat room
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable String roomId,
                                        @RequestParam(defaultValue = "50") int limit,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<Map<String, Object>> messages = chatService.getMessages(roomId, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("messages", messages);
            response.put("count", messages.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get messages");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Mark messages as read
     */
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable String roomId,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            chatService.markMessagesAsRead(roomId, user.getId());
            
            return ResponseEntity.ok(Map.of("message", "Messages marked as read"));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to mark messages as read");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get chat room information
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> getChatRoomInfo(@PathVariable String roomId,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Map<String, Object> roomInfo = chatService.getChatRoomInfo(roomId);
            
            if (roomInfo == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(roomInfo);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get chat room info");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get active chat rooms for a user
     */
    @GetMapping("/rooms")
    public ResponseEntity<?> getActiveChatRooms(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<Map<String, Object>> chatRooms = chatService.getActiveChatRooms(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("chatRooms", chatRooms);
            response.put("count", chatRooms.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get active chat rooms");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get unread message count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadMessageCount(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            int unreadCount = chatService.getUnreadMessageCount(user.getId());
            
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get unread message count");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Set typing indicator
     */
    @PostMapping("/rooms/{roomId}/typing")
    public ResponseEntity<?> setTypingIndicator(@PathVariable String roomId,
                                              @RequestBody Map<String, Boolean> request,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            Boolean isTyping = request.get("isTyping");
            if (isTyping == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "isTyping parameter is required"));
            }

            chatService.setTypingIndicator(roomId, user.getId(), isTyping);
            
            return ResponseEntity.ok(Map.of("message", "Typing indicator updated"));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to set typing indicator");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get typing indicators for a room
     */
    @GetMapping("/rooms/{roomId}/typing")
    public ResponseEntity<?> getTypingIndicators(@PathVariable String roomId,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            List<Map<String, Object>> typingUsers = chatService.getTypingIndicators(roomId, user.getId());
            
            return ResponseEntity.ok(Map.of("typingUsers", typingUsers));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get typing indicators");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a chat room
     */
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable String roomId,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            User user = authService.validateToken(authHeader.substring(7));
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
            }

            chatService.deleteChatRoom(roomId);
            
            return ResponseEntity.ok(Map.of("message", "Chat room deleted successfully"));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete chat room");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
