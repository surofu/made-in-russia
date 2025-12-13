package com.surofu.exporteru.infrastructure.websocket;

import com.surofu.exporteru.application.dto.chat.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки сообщений через WebSocket
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketMessageSender {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Отправить сообщение всем участникам чата
     */
    public void sendMessageToChat(Long chatId, ChatMessageDTO message) {
        String destination = "/topic/chat/" + chatId;
        messagingTemplate.convertAndSend(destination, message);
        log.debug("Sent message {} to chat {} via WebSocket", message.getId(), chatId);
    }

    /**
     * Отправить уведомление конкретному пользователю
     */
    public void sendNotificationToUser(Long userId, Object notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
        log.debug("Sent notification to user {} via WebSocket", userId);
    }

}