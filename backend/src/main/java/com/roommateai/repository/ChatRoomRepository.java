package com.roommateai.repository;

import com.roommateai.model.ChatRoom;
import com.roommateai.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Chat Room Repository
 * Data access layer for ChatRoom entity
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    /**
     * Find chat room by Firebase room ID
     */
    ChatRoom findByFirebaseRoomId(String firebaseRoomId);
    
    /**
     * Find chat room by match
     */
    ChatRoom findByMatch(Match match);
    
    /**
     * Find chat room by match ID
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.match.id = :matchId")
    ChatRoom findByMatchId(@Param("matchId") Long matchId);
    
    /**
     * Find active chat rooms
     */
    List<ChatRoom> findByIsActive(Boolean isActive);
    
    /**
     * Find chat rooms by user (either user1 or user2 in the match)
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "(cr.match.user1.id = :userId OR cr.match.user2.id = :userId) AND cr.isActive = true")
    List<ChatRoom> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find chat rooms with unread messages
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.isActive = true AND cr.lastMessageAt IS NOT NULL")
    List<ChatRoom> findChatRoomsWithMessages();
    
    /**
     * Count active chat rooms for a user
     */
    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE " +
           "(cr.match.user1.id = :userId OR cr.match.user2.id = :userId) AND cr.isActive = true")
    Long countActiveChatRoomsByUserId(@Param("userId") Long userId);
}
