package com.surofu.exporteru.infrastructure.persistence.chat;

import com.surofu.exporteru.core.model.chat.MessageReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {

    boolean existsByMessageIdAndUserId(Long messageId, Long userId);

    boolean existsByMessageId(Long messageId);


    @Query("SELECT CASE WHEN COUNT(mrs) > 0 THEN true ELSE false END " +
           "FROM MessageReadStatus mrs " +
           "JOIN ChatParticipant cp ON cp.user = mrs.user AND cp.chat = mrs.message.chat " +
           "WHERE mrs.message.id = :messageId AND cp.role != com.surofu.exporteru.core.model.chat.ChatRole.ADMIN")
    boolean existsByMessageIdAndReaderIsNotAdmin(@Param("messageId") Long messageId);

    /**
     * Вставляет запись о прочтении сообщения, игнорируя дубликаты.
     * Использует ON CONFLICT DO NOTHING для избежания DataIntegrityViolationException.
     */
    @Modifying
    @Query(value = "INSERT INTO message_read_status (message_id, user_id, read_at) " +
                   "VALUES (:messageId, :userId, NOW()) " +
                   "ON CONFLICT (message_id, user_id) DO NOTHING",
           nativeQuery = true)
    void insertIgnoreDuplicate(@Param("messageId") Long messageId, @Param("userId") Long userId);

}