package com.surofu.exporteru.infrastructure.persistence.chat;

import com.surofu.exporteru.core.model.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {


    @Query("SELECT m FROM ChatMessage m " +
           "WHERE m.chat.id = :chatId AND m.isDeleted = false " +
           "ORDER BY m.createdAt DESC")
    Page<ChatMessage> findByChatIdOrderByCreatedAtDesc(@Param("chatId") Long chatId, Pageable pageable);


    @Query("SELECT COUNT(m) FROM ChatMessage m " +
           "WHERE m.chat.id = :chatId " +
           "AND m.sender.id != :userId " +
           "AND m.isDeleted = false " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM MessageReadStatus mrs " +
           "  WHERE mrs.message = m AND mrs.user.id = :userId" +
           ")")
    Long countUnreadMessagesByChatIdAndUserId(@Param("chatId") Long chatId,
                                               @Param("userId") Long userId);

    /**
     * Find latest message in a chat
     */
    @Query("SELECT m FROM ChatMessage m " +
           "WHERE m.chat.id = :chatId AND m.isDeleted = false " +
           "ORDER BY m.createdAt DESC " +
           "LIMIT 1")
    ChatMessage findLatestByChatId(@Param("chatId") Long chatId);

    /**
     * Count messages in a chat
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m " +
           "WHERE m.chat.id = :chatId AND m.isDeleted = false")
    Long countByChatId(@Param("chatId") Long chatId);

    /**
     * Count all unread messages for a user across all chats
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m " +
           "JOIN ChatParticipant cp ON cp.chat = m.chat AND cp.user.id = :userId " +
           "WHERE m.sender.id != :userId " +
           "AND m.isDeleted = false " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM MessageReadStatus mrs " +
           "  WHERE mrs.message = m AND mrs.user.id = :userId" +
           ")")
    Long countTotalUnreadMessagesByUserId(@Param("userId") Long userId);

}