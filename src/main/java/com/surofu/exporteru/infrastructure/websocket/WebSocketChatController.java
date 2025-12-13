package com.surofu.exporteru.infrastructure.websocket;

import com.surofu.exporteru.application.dto.chat.ChatMessageDTO;
import com.surofu.exporteru.application.dto.chat.SendMessageRequest;
import com.surofu.exporteru.application.service.chat.ChatMessageService;
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
        Long userId = Long.parseLong(principal.getName());
        log.debug("Received WebSocket message from user {} in chat {}", userId, chatId);

        return messageService.sendMessage(request, userId);
    }

    @MessageMapping("/chat/{chatId}/typing")
    @SendTo("/topic/chat/{chatId}/typing")
    public Map<String, Object> handleTyping(
            @DestinationVariable Long chatId,
            Principal principal
    ) {
        if (principal == null) {
            log.error("TYPING EVENT FAILED: Principal is null for chat {}", chatId);
            return Map.of(
                    "error", "Not authenticated",
                    "timestamp", System.currentTimeMillis()
            );
        }
        
        Long userId = Long.parseLong(principal.getName());
        log.info(" TYPING EVENT: User {} is typing in chat {}", userId, chatId);

        String userName = userRepository.getById(userId)
                .map(user -> user.getLogin() != null ? user.getLogin().getValue() : "User")
                .orElse("User");

        Map<String, Object> response = Map.of(
                "userId", userId,
                "userName", userName,
                "isTyping", true,
                "timestamp", System.currentTimeMillis()
        );

        log.info("Sending typing indicator to /topic/chat/{}/typing: {}", chatId, response);
        return response;
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
        Long userId = Long.parseLong(principal.getName());
        log.debug("Marking message {} as read by user {}", messageId, userId);


        var message = messageService.getMessage(messageId);
        if (message == null) {
            log.warn("Message {} not found", messageId);
            return;
        }

        messageService.markAsRead(messageId, userId);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getChat().getId() + "/read",
                Map.of("userId", userId, "messageId", messageId, "timestamp", System.currentTimeMillis())
        );
    }
}