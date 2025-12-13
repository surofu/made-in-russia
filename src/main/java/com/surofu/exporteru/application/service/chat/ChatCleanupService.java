package com.surofu.exporteru.application.service.chat;

import com.surofu.exporteru.core.model.chat.Chat;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatMessageRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для автоматической очистки пустых чатов
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCleanupService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository messageRepository;

    /**
     * Удалить старые пустые чаты
     * Запускается каждые 5 минут
     */
    @Scheduled(fixedDelay = 300000) // 5 минут
    @Transactional
    public void cleanupEmptyChats() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        List<Chat> allChats = chatRepository.findAll();

        List<Chat> emptyChats = allChats.stream()
                .filter(chat -> {
                    boolean isOld = chat.getCreatedAt().isBefore(tenMinutesAgo);

                    Long messageCount = messageRepository.countByChatId(chat.getId());
                    boolean hasNoMessages = messageCount == 0;

                    return hasNoMessages && isOld;
                })
                .collect(Collectors.toList());

        if (!emptyChats.isEmpty()) {
            chatRepository.deleteAll(emptyChats);
            log.info("Deleted {} empty chats older than 10 minutes", emptyChats.size());
        } else {
            log.debug("No empty chats found for cleanup");
        }
    }
}
