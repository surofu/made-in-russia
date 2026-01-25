package com.surofu.exporteru.infrastructure.websocket;

import com.surofu.exporteru.application.dto.chat.ChatMessageDTO;
import com.surofu.exporteru.application.dto.chat.SendMessageRequest;
import com.surofu.exporteru.application.service.chat.ChatMessageService;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket контроллер для обработки сообщений чата в реальном времени
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatMessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    /**
     * WebSocket endpoint для отправки сообщений
     * Клиент отправляет: /app/chat/{chatId}/send
     * Сообщение доставляется на: /topic/chat/{chatId}
     */
    @MessageMapping("/chat/{chatId}/send")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageDTO handleChatMessage(
            @DestinationVariable Long chatId,
            @Payload SendMessageRequest request,
            Principal principal
    ) {
        Long userId = parseUserId(principal);
        if (userId == null) {
            log.error("Cannot send message: user not authenticated for chat {}", chatId);
            return null;
        }
        log.debug("Received WebSocket message from user {} in chat {}", userId, chatId);

        return messageService.sendMessage(request, userId);
    }

    @MessageMapping("/chat/{chatId}/typing")
    public void handleTyping(
            @DestinationVariable Long chatId,
            Principal principal
    ) {
        Long senderId = parseUserId(principal);
        if (senderId == null) {
            log.error("TYPING EVENT FAILED: User not authenticated for chat {}", chatId);
            return;
        }

        log.info("TYPING EVENT: User {} is typing in chat {}", senderId, chatId);

        String userName = userRepository.getById(senderId)
                .map(user -> user.getLogin() != null ? user.getLogin().getValue() : "User")
                .orElse("User");

        Map<String, Object> typingData = Map.of(
                "userId", senderId,
                "userName", userName,
                "chatId", chatId,
                "isTyping", true,
                "timestamp", System.currentTimeMillis()
        );

        chatRepository.findByIdWithParticipants(chatId).ifPresent(chat -> {
            chat.getParticipants().stream()
                    .map(p -> p.getUser().getId())
                    .filter(participantId -> !participantId.equals(senderId))
                    .forEach(participantId -> {
                        messagingTemplate.convertAndSendToUser(
                                participantId.toString(),
                                "/queue/typing",
                                typingData
                        );
                        log.debug("Sent typing indicator to user {} for chat {}", participantId, chatId);
                    });
        });

        log.info("Sent typing indicator for chat {} from user {} to other participants", chatId, senderId);
    }

    /**
     * WebSocket endpoint для отметки сообщения как прочитанного
     * Клиент отправляет: /app/chat/message/{messageId}/read
     */
    @MessageMapping("/chat/message/{messageId}/read")
    public void handleMarkAsRead(
            @DestinationVariable Long messageId,
            Principal principal
    ) {
        Long userId = parseUserId(principal);
        if (userId == null) {
            log.error("Cannot mark message as read: user not authenticated for message {}", messageId);
            return;
        }
        log.debug("Marking message {} as read by user {}", messageId, userId);

        // Используем новый метод который возвращает информацию для broadcast
        var result = messageService.markAsReadAndGetBroadcastInfo(messageId, userId);
        if (result == null) {
            return;
        }

        // Если админ - не отправляем broadcast
        if (result.isAdmin()) {
            log.debug("Admin {} read message {}, skipping broadcast to preserve other participants' unread status",
                    userId, messageId);
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/chat/" + result.getChatId() + "/read",
                Map.of("userId", userId, "messageId", messageId, "timestamp", System.currentTimeMillis())
        );
    }

    /**
     * Парсит ID пользователя из Principal.
     * Возвращает null если пользователь не аутентифицирован или principal содержит невалидное значение.
     */
    private Long parseUserId(Principal principal) {
        if (principal == null) {
            return null;
        }
        String name = principal.getName();
        if (name == null || name.isEmpty() || "anonymous".equalsIgnoreCase(name)) {
            return null;
        }
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format in principal: {}", name);
            return null;
        }
    }
}