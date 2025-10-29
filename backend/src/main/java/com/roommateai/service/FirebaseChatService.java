package com.roommateai.service;

import com.google.cloud.firestore.*;
import com.roommateai.model.ChatRoom;
import com.roommateai.model.Match;
import com.roommateai.repository.ChatRoomRepository;
import com.roommateai.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Firebase Chat Service
 * Handles real-time chat functionality using Firebase Firestore
 */
@Service
public class FirebaseChatService {

    @Autowired
    private FirebaseFirestore firestore;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MatchRepository matchRepository;

    /**
     * Create a chat room for matched users
     */
    public String createChatRoom(Long matchId) {
        try {
            Match match = matchRepository.findById(matchId).orElse(null);
            if (match == null) {
                throw new RuntimeException("Match not found");
            }

            // Check if chat room already exists
            ChatRoom existingChatRoom = chatRoomRepository.findByMatchId(matchId);
            if (existingChatRoom != null) {
                return existingChatRoom.getFirebaseRoomId();
            }

            // Create Firebase chat room
            String firebaseRoomId = "chat_" + matchId + "_" + System.currentTimeMillis();
            
            Map<String, Object> roomData = new HashMap<>();
            roomData.put("matchId", matchId);
            roomData.put("user1Id", match.getUser1().getId());
            roomData.put("user2Id", match.getUser2().getId());
            roomData.put("user1Name", match.getUser1().getName());
            roomData.put("user2Name", match.getUser2().getName());
            roomData.put("createdAt", new Date());
            roomData.put("isActive", true);

            // Create room document in Firestore
            DocumentReference roomRef = firestore.collection("chatRooms").document(firebaseRoomId);
            roomRef.set(roomData).get();

            // Create messages subcollection
            roomRef.collection("messages").document("placeholder").set(Map.of("placeholder", true)).get();

            // Save chat room metadata to MySQL
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setMatch(match);
            chatRoom.setFirebaseRoomId(firebaseRoomId);
            chatRoom.setIsActive(true);
            chatRoomRepository.save(chatRoom);

            return firebaseRoomId;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to create chat room", e);
        }
    }

    /**
     * Send a message to a chat room
     */
    public String sendMessage(String roomId, Long senderId, String messageText) {
        try {
            // Create message document
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("senderId", senderId);
            messageData.put("message", messageText);
            messageData.put("timestamp", new Date());
            messageData.put("isRead", false);

            // Add message to Firestore
            DocumentReference messageRef = firestore.collection("chatRooms")
                    .document(roomId)
                    .collection("messages")
                    .document();
            
            messageRef.set(messageData).get();

            // Update last message in room document
            firestore.collection("chatRooms").document(roomId)
                    .update("lastMessage", messageText, "lastMessageAt", new Date()).get();

            // Update MySQL chat room metadata
            ChatRoom chatRoom = chatRoomRepository.findByFirebaseRoomId(roomId);
            if (chatRoom != null) {
                chatRoom.setLastMessage(messageText);
                chatRoom.setLastMessageAt(LocalDateTime.now());
                chatRoomRepository.save(chatRoom);
            }

            return messageRef.getId();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    /**
     * Get messages from a chat room
     */
    public List<Map<String, Object>> getMessages(String roomId, int limit) {
        try {
            Query query = firestore.collection("chatRooms")
                    .document(roomId)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(limit);

            QuerySnapshot snapshot = query.get().get();
            List<Map<String, Object>> messages = new ArrayList<>();

            for (DocumentSnapshot document : snapshot.getDocuments()) {
                Map<String, Object> messageData = document.getData();
                messageData.put("id", document.getId());
                
                // Convert Firestore timestamp to LocalDateTime
                if (messageData.get("timestamp") instanceof com.google.cloud.Timestamp) {
                    com.google.cloud.Timestamp timestamp = (com.google.cloud.Timestamp) messageData.get("timestamp");
                    messageData.put("timestamp", timestamp.toDate());
                }
                
                messages.add(messageData);
            }

            Collections.reverse(messages); // Reverse to get chronological order
            return messages;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get messages", e);
        }
    }

    /**
     * Mark messages as read
     */
    public void markMessagesAsRead(String roomId, Long userId) {
        try {
            Query query = firestore.collection("chatRooms")
                    .document(roomId)
                    .collection("messages")
                    .whereEqualTo("isRead", false)
                    .whereNotEqualTo("senderId", userId);

            QuerySnapshot snapshot = query.get().get();
            
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                document.getReference().update("isRead", true).get();
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to mark messages as read", e);
        }
    }

    /**
     * Get chat room information
     */
    public Map<String, Object> getChatRoomInfo(String roomId) {
        try {
            DocumentSnapshot document = firestore.collection("chatRooms")
                    .document(roomId)
                    .get()
                    .get();

            if (document.exists()) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId());
                return data;
            }

            return null;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get chat room info", e);
        }
    }

    /**
     * Get active chat rooms for a user
     */
    public List<Map<String, Object>> getActiveChatRooms(Long userId) {
        try {
            Query query = firestore.collection("chatRooms")
                    .whereEqualTo("isActive", true)
                    .whereArrayContains("participants", userId);

            QuerySnapshot snapshot = query.get().get();
            List<Map<String, Object>> chatRooms = new ArrayList<>();

            for (DocumentSnapshot document : snapshot.getDocuments()) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId());
                chatRooms.add(data);
            }

            return chatRooms;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get active chat rooms", e);
        }
    }

    /**
     * Delete a chat room
     */
    public void deleteChatRoom(String roomId) {
        try {
            // Delete from Firestore
            firestore.collection("chatRooms").document(roomId).delete().get();

            // Update MySQL metadata
            ChatRoom chatRoom = chatRoomRepository.findByFirebaseRoomId(roomId);
            if (chatRoom != null) {
                chatRoom.setIsActive(false);
                chatRoomRepository.save(chatRoom);
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete chat room", e);
        }
    }

    /**
     * Get unread message count for a user
     */
    public int getUnreadMessageCount(Long userId) {
        try {
            int totalUnread = 0;
            
            // Get all active chat rooms for the user
            List<Map<String, Object>> chatRooms = getActiveChatRooms(userId);
            
            for (Map<String, Object> room : chatRooms) {
                String roomId = (String) room.get("id");
                
                Query query = firestore.collection("chatRooms")
                        .document(roomId)
                        .collection("messages")
                        .whereEqualTo("isRead", false)
                        .whereNotEqualTo("senderId", userId);

                QuerySnapshot snapshot = query.get().get();
                totalUnread += snapshot.size();
            }

            return totalUnread;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get unread message count", e);
        }
    }

    /**
     * Add typing indicator
     */
    public void setTypingIndicator(String roomId, Long userId, boolean isTyping) {
        try {
            Map<String, Object> typingData = new HashMap<>();
            typingData.put("userId", userId);
            typingData.put("isTyping", isTyping);
            typingData.put("timestamp", new Date());

            firestore.collection("chatRooms")
                    .document(roomId)
                    .collection("typing")
                    .document(userId.toString())
                    .set(typingData).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to set typing indicator", e);
        }
    }

    /**
     * Get typing indicators for a room
     */
    public List<Map<String, Object>> getTypingIndicators(String roomId, Long currentUserId) {
        try {
            Query query = firestore.collection("chatRooms")
                    .document(roomId)
                    .collection("typing")
                    .whereEqualTo("isTyping", true)
                    .whereNotEqualTo("userId", currentUserId);

            QuerySnapshot snapshot = query.get().get();
            List<Map<String, Object>> typingUsers = new ArrayList<>();

            for (DocumentSnapshot document : snapshot.getDocuments()) {
                Map<String, Object> data = document.getData();
                typingUsers.add(data);
            }

            return typingUsers;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get typing indicators", e);
        }
    }
}
