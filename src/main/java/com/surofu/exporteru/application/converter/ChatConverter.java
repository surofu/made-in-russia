package com.surofu.exporteru.application.converter;

import com.surofu.exporteru.application.dto.chat.*;
import com.surofu.exporteru.core.model.chat.*;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatMessageRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.MessageReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Конвертер для преобразования Entity чата в DTO и обратно
 */
@Component
@RequiredArgsConstructor
public class ChatConverter {

    private final MessageReadStatusRepository readStatusRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * Конвертировать Chat entity в ChatDTO
     */
    public ChatDTO toDTO(Chat chat, Long currentUserId) {
        return ChatDTO.builder()
                .id(chat.getId())
                .product(toProductInfoDTO(chat.getProduct()))
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
                .name(product.getTitle() != null ? product.getTitle().getValue() : null)
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
                .userName(user.getLogin() != null ? user.getLogin().getValue() : null)
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
            isRead = readStatusRepository.existsByMessageId(message.getId());
        } else {
            isRead = readStatusRepository.existsByMessageIdAndUserId(message.getId(), currentUserId);
        }

        return ChatMessageDTO.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(sender.getId())
                .senderName(sender.getLogin() != null ? sender.getLogin().getValue() : null)
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
}