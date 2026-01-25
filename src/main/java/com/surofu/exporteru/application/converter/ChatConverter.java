package com.surofu.exporteru.application.converter;

import com.surofu.exporteru.application.dto.chat.*;
import com.surofu.exporteru.core.model.chat.*;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatMessageRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.MessageReadStatusRepository;
import com.surofu.exporteru.infrastructure.persistence.translation.TranslationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Конвертер для преобразования Entity чата в DTO и обратно
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatConverter {

    private final MessageReadStatusRepository readStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TranslationRepository translationRepository;

    private static final Map<String, String> translationCache = new ConcurrentHashMap<>();

    /**
     * Конвертировать Chat entity в ChatDTO
     */
    public ChatDTO toDTO(Chat chat, Long currentUserId) {
        Boolean isVendorChat = chat.getIsVendorChat() != null && chat.getIsVendorChat();
        VendorInfoDTO vendorInfo = null;

        if (isVendorChat) {
            vendorInfo = chat.getParticipants().stream()
                    .filter(p -> p.getRole() == ChatRole.SELLER)
                    .findFirst()
                    .map(p -> toVendorInfoDTO(p.getUser()))
                    .orElse(null);
        }

        return ChatDTO.builder()
                .id(chat.getId())
                .product(toProductInfoDTO(chat.getProduct()))
                .vendorInfo(vendorInfo)
                .isVendorChat(isVendorChat)
                .participants(chat.getParticipants().stream()
                        .map(this::toParticipantDTO)
                        .collect(Collectors.toList()))
                .lastMessage(getLastMessage(chat, currentUserId))
                .unreadCount(countUnreadMessages(chat.getId(), currentUserId))
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .build();
    }

    /**
     * Конвертировать Product в ProductInfoDTO
     */
    public ProductInfoDTO toProductInfoDTO(Product product) {
        String imageUrl = product.getMedia() != null && !product.getMedia().isEmpty()
                ? product.getMedia().stream()
                .findFirst()
                .map(media -> media.getUrl() != null ? media.getUrl().getValue() : null)
                .orElse(null)
                : null;

        BigDecimal price = product.getPrices() != null && !product.getPrices().isEmpty()
                ? product.getPrices().stream()
                .findFirst()
                .map(p -> p.getDiscountedPrice() != null && p.getDiscountedPrice().getValue() != null
                        ? p.getDiscountedPrice().getValue()
                        : (p.getOriginalPrice() != null ? p.getOriginalPrice().getValue() : null))
                .orElse(null)
                : null;

        return ProductInfoDTO.builder()
                .id(product.getId())
                .name(product.getTitle() != null ? product.getTitle().getLocalizedValue() : null)
                .price(price)
                .imageUrl(imageUrl)
                .build();
    }

    /**
     * Конвертировать ChatParticipant в ChatParticipantDTO
     */
    public ChatParticipantDTO toParticipantDTO(ChatParticipant participant) {
        User user = participant.getUser();

        return ChatParticipantDTO.builder()
                .id(participant.getId())
                .userId(user.getId())
                .userName(getTranslatedUserName(user.getLogin()))
                .userAvatar(user.getAvatar() != null ? user.getAvatar().getUrl() : null)
                .role(participant.getRole())
                .joinedAt(participant.getJoinedAt())
                .lastReadAt(participant.getLastReadAt())
                .build();
    }

    /**
     * Конвертировать ChatMessage в ChatMessageDTO
     */
    public ChatMessageDTO toMessageDTO(ChatMessage message, Long currentUserId) {
        User sender = message.getSender();

        boolean isRead;
        if (sender.getId().equals(currentUserId)) {
            // Для отправителя: проверяем прочитал ли РЕАЛЬНЫЙ получатель (не админ)
            isRead = readStatusRepository.existsByMessageIdAndReaderIsNotAdmin(message.getId());
        } else {
            isRead = readStatusRepository.existsByMessageIdAndUserId(message.getId(), currentUserId);
        }

        return ChatMessageDTO.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(sender.getId())
                .senderName(getTranslatedUserName(sender.getLogin()))
                .senderAvatar(sender.getAvatar() != null ? sender.getAvatar().getUrl() : null)
                .content(message.getContent())
                .attachments(message.getAttachments() != null
                        ? message.getAttachments().stream()
                        .map(this::toAttachmentDTO)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .isRead(isRead)
                .isSystem(message.getIsSystem())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    /**
     * Конвертировать MessageAttachment в MessageAttachmentDTO
     */
    public MessageAttachmentDTO toAttachmentDTO(MessageAttachment attachment) {
        return MessageAttachmentDTO.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .fileSize(attachment.getFileSize())
                .mimeType(attachment.getMimeType())
                .build();
    }

    /**
     * Конвертировать User в VendorInfoDTO для vendor chat
     */
    public VendorInfoDTO toVendorInfoDTO(User user) {
        return VendorInfoDTO.builder()
                .id(user.getId())
                .name(getTranslatedUserName(user.getLogin()))
                .avatarUrl(user.getAvatar() != null ? user.getAvatar().getUrl() : null)
                .build();
    }

    /**
     * Получить последнее сообщение чата
     */
    private ChatMessageDTO getLastMessage(Chat chat, Long currentUserId) {
        ChatMessage lastMessage = chatMessageRepository.findLatestByChatId(chat.getId());
        return lastMessage != null ? toMessageDTO(lastMessage, currentUserId) : null;
    }

    /**
     * Подсчитать количество непрочитанных сообщений
     */
    private Long countUnreadMessages(Long chatId, Long userId) {
        return chatMessageRepository.countUnreadMessagesByChatIdAndUserId(chatId, userId);
    }

    /**
     * Получить переведённое имя пользователя.
     * Если перевод для текущей локали отсутствует - переводит через API.
     * Результаты кэшируются для избежания повторных запросов.
     */
    private String getTranslatedUserName(UserLogin login) {
        if (login == null) {
            return null;
        }

        String originalValue = login.getValue();
        if (originalValue == null || originalValue.isEmpty()) {
            return null;
        }

        Locale locale = LocaleContextHolder.getLocale();
        String targetLanguage = locale.getLanguage();

        Map<String, String> transliteration = login.getTransliteration();
        if (transliteration != null && !transliteration.isEmpty()) {
            String existingTranslation = transliteration.get(targetLanguage);
            if (existingTranslation != null && !existingTranslation.isEmpty()) {
                return existingTranslation;
            }
        }

        if ("ru".equals(targetLanguage) && isCyrillic(originalValue)) {
            return originalValue;
        }

        String cacheKey = targetLanguage + ":" + originalValue;
        String cachedTranslation = translationCache.get(cacheKey);
        if (cachedTranslation != null) {
            return cachedTranslation;
        }

        try {
            TranslationResponse response = translationRepository.translate(targetLanguage, null, originalValue);
            if (response != null && response.getTranslations() != null && response.getTranslations().length > 0) {
                String translated = response.getTranslations()[0].getText();
                if (translated != null && !translated.isEmpty()) {
                    translationCache.put(cacheKey, translated);
                    return translated;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to translate user name '{}' to '{}': {}", originalValue, targetLanguage, e.getMessage());
        }

        translationCache.put(cacheKey, originalValue);
        return originalValue;
    }

    /**
     * Проверить, содержит ли строка кириллические символы
     */
    private boolean isCyrillic(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (c >= 0x0400 && c <= 0x04FF) {
                return true;
            }
        }
        return false;
    }
}