package com.surofu.exporteru.application.service.chat;

import com.surofu.exporteru.application.converter.ChatConverter;
import com.surofu.exporteru.application.dto.chat.ChatDTO;
import com.surofu.exporteru.application.dto.chat.ChatMessageDTO;
import com.surofu.exporteru.application.dto.chat.MessageListResponse;
import com.surofu.exporteru.application.dto.chat.SendMessageRequest;
import com.surofu.exporteru.application.exception.AccessDeniedException;
import com.surofu.exporteru.core.model.chat.*;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatMessageRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatParticipantRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.MessageReadStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.surofu.exporteru.infrastructure.websocket.WebSocketMessageSender;

/**
 * Сервис для работы с сообщениями чата
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    /**
     * Результат операции markAsRead с информацией для broadcast
     */
    @lombok.Value
    public static class MarkAsReadResult {
        Long chatId;
        boolean isAdmin;
    }

    private final ChatMessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository participantRepository;
    private final MessageAttachmentService attachmentService;
    private final MessageReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChatConverter chatConverter;
    private final WebSocketMessageSender webSocketSender;

    private final ChatService chatService;

    /**
     * Отправить сообщение
     */
    @Transactional
    public ChatMessageDTO sendMessage(SendMessageRequest request, Long senderId) {
        Chat chat;

        if (request.getChatId() != null) {
            chat = chatRepository.findByIdAndParticipantUserId(request.getChatId(), senderId)
                    .orElseThrow(() -> new AccessDeniedException("Access denied to this chat"));
        }
        else if (request.getProductId() != null) {
            ChatDTO chatDTO = chatService.createOrGetChat(request.getProductId(), senderId);
            chat = chatRepository.findById(chatDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Chat not found with id: " + chatDTO.getId()));
        }
        else {
            throw new IllegalArgumentException("Either chatId or productId must be provided");
        }

        User sender = userRepository.getById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + senderId));

        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(request.getContent());
        ChatMessage savedMessage = messageRepository.save(message);

        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            List<MessageAttachment> attachments = attachmentService.saveAttachments(savedMessage, request.getAttachments());
            savedMessage.getAttachments().addAll(attachments);
        }

        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);

        ChatMessageDTO messageDTO = chatConverter.toMessageDTO(savedMessage, senderId);

        webSocketSender.sendMessageToChat(chat.getId(), messageDTO);

        final Long messageId = savedMessage.getId();
        final String messageContent = savedMessage.getContent();
        chat.getParticipants().stream()
                .filter(participant -> !participant.getUser().getId().equals(senderId))
                .forEach(participant -> {
                    webSocketSender.sendNotificationToUser(
                            participant.getUser().getId(),
                            java.util.Map.of(
                                    "type", "NEW_MESSAGE",
                                    "messageId", messageId,
                                    "chatId", chat.getId(),
                                    "senderId", senderId,
                                    "senderName", sender.getLogin() != null ? sender.getLogin().getValue() : "User",
                                    "content", messageContent,
                                    "timestamp", System.currentTimeMillis()
                            )
                    );
                    log.debug("Sent new message notification to user {}", participant.getUser().getId());
                });

        return messageDTO;
    }

    /**
     * Получить историю сообщений
     */
    @Transactional(readOnly = true)
    public MessageListResponse getChatMessages(Long chatId, Long userId, Pageable pageable) {
        if (!participantRepository.existsByChatIdAndUserId(chatId, userId)) {
            throw new AccessDeniedException("Access denied to this chat");
        }

        Page<ChatMessage> messagesPage = messageRepository
                .findByChatIdOrderByCreatedAtDesc(chatId, pageable);

        List<ChatMessageDTO> messages = messagesPage.getContent().stream()
                .map(msg -> chatConverter.toMessageDTO(msg, userId))
                .collect(Collectors.toList());

        return MessageListResponse.builder()
                .messages(messages)
                .totalPages(messagesPage.getTotalPages())
                .totalElements(messagesPage.getTotalElements())
                .currentPage(messagesPage.getNumber())
                .hasMore(messagesPage.hasNext())
                .build();
    }

    /**
     * Получить сообщение по ID
     */
    @Transactional(readOnly = true)
    public ChatMessage getMessage(Long messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        User user = userRepository.getById(userId).orElse(null);
        if (user == null) {
            log.warn("User not found {}", userId);
            return;
        }

        // Если пользователь - ADMIN, не помечаем сообщение как прочитанное
        // чтобы не влиять на unreadCount у продавца и покупателя
        if (user.getRole() == UserRole.ROLE_ADMIN) {
            log.debug("Admin {} reading message {}, skipping mark as read", userId, messageId);
            return;
        }

        ChatMessage message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            log.warn("Message not found {}", messageId);
            return;
        }

        // Атомарно вставляем запись, игнорируя дубликаты
        readStatusRepository.insertIgnoreDuplicate(messageId, userId);

        ChatParticipant participant = participantRepository
                .findByChatIdAndUserId(message.getChat().getId(), userId)
                .orElse(null);

        if (participant == null) {
            log.warn("Participant not found");
            return;
        }

        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);

        webSocketSender.sendNotificationToUser(
                message.getSender().getId(),
                Map.of(
                        "type", "MESSAGE_READ",
                        "messageId", messageId,
                        "chatId", message.getChat().getId(),
                        "readBy", userId,
                        "timestamp", System.currentTimeMillis()
                )
        );
    }

    /**
     * Отметить сообщение как прочитанное и вернуть информацию для broadcast.
     * Используется WebSocket контроллером.
     * @return null если операция не удалась, иначе информацию о chatId и роли пользователя
     */
    @Transactional
    public MarkAsReadResult markAsReadAndGetBroadcastInfo(Long messageId, Long userId) {
        User user = userRepository.getById(userId).orElse(null);
        if (user == null) {
            log.warn("User not found with id: {}", userId);
            return null;
        }

        ChatMessage message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            log.warn("Message not found with id: {}", messageId);
            return null;
        }

        Long chatId = message.getChat().getId();
        boolean isAdmin = user.getRole() == UserRole.ROLE_ADMIN;

        // Если админ - не создаём запись о прочтении, просто возвращаем результат
        if (isAdmin) {
            log.debug("Admin {} reading message {}, skipping mark as read", userId, messageId);
            return new MarkAsReadResult(chatId, true);
        }

        // Атомарно вставляем запись, игнорируя дубликаты
        readStatusRepository.insertIgnoreDuplicate(messageId, userId);

        ChatParticipant participant = participantRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElse(null);
        if (participant == null) {
            log.warn("Participant not found for chat {} and user {}", chatId, userId);
            return null;
        }

        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);

        // Отправляем уведомление отправителю
        webSocketSender.sendNotificationToUser(
                message.getSender().getId(),
                Map.of(
                        "type", "MESSAGE_READ",
                        "messageId", messageId,
                        "chatId", chatId,
                        "readBy", userId,
                        "timestamp", System.currentTimeMillis()
                )
        );
        log.debug("Sent read notification to sender {} for message {}", message.getSender().getId(), messageId);

        return new MarkAsReadResult(chatId, false);
    }

    /**
     * Получить количество непрочитанных сообщений в чате
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long chatId, Long userId) {
        return messageRepository.countUnreadMessagesByChatIdAndUserId(chatId, userId);
    }

    /**
     * Получить общее количество непрочитанных сообщений пользователя по всем чатам
     */
    @Transactional(readOnly = true)
    public Long getTotalUnreadCount(Long userId) {
        return messageRepository.countTotalUnreadMessagesByUserId(userId);
    }

}