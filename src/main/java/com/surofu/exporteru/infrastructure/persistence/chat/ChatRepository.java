package com.surofu.exporteru.infrastructure.persistence.chat;

import com.surofu.exporteru.core.model.chat.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {


    @Query("SELECT DISTINCT c FROM Chat c " +
           "WHERE c.product.id = :productId " +
           "AND c.isActive = true " +
           "ORDER BY c.updatedAt DESC")
    List<Chat> findAllByProductId(@Param("productId") Long productId);



    @Query("SELECT DISTINCT c FROM Chat c " +
           "WHERE c.product.id = :productId " +
           "AND c.isActive = true " +
           "ORDER BY c.updatedAt DESC")
    Page<Chat> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Chat c " +
           "JOIN c.participants p " +
           "WHERE c.product.id = :productId " +
           "AND p.user.id = :buyerId " +
           "AND p.role = 'BUYER' " +
           "AND c.isActive = true")
    Optional<Chat> findByProductIdAndBuyerId(@Param("productId") Long productId,
                                              @Param("buyerId") Long buyerId);


    @Query("SELECT DISTINCT c FROM Chat c " +
           "JOIN c.participants p " +
           "WHERE p.user.id = :userId " +
           "AND c.isActive = true " +
           "ORDER BY c.updatedAt DESC")
    Page<Chat> findByParticipantsUserId(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT DISTINCT c FROM Chat c " +
           "JOIN c.participants p " +
           "WHERE p.user.id = :userId " +
           "AND c.isActive = true " +
           "AND EXISTS (SELECT 1 FROM ChatMessage m WHERE m.chat = c AND m.isDeleted = false) " +
           "ORDER BY c.updatedAt DESC")
    Page<Chat> findByParticipantsUserIdWithMessages(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Chat c " +
           "JOIN c.participants p " +
           "WHERE c.id = :chatId AND p.user.id = :userId AND c.isActive = true")
    Optional<Chat> findByIdAndParticipantUserId(@Param("chatId") Long chatId,
                                                  @Param("userId") Long userId);


    @Query("SELECT c FROM Chat c " +
           "LEFT JOIN FETCH c.participants p " +
           "LEFT JOIN FETCH p.user " +
           "WHERE c.id = :chatId")
    Optional<Chat> findByIdWithParticipants(@Param("chatId") Long chatId);
}