package com.surofu.exporteru.application.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для чата
 */
@Getter
@Setter
@Builder
public class ChatDTO {
    private Long id;
    private ProductInfoDTO product;
    private VendorInfoDTO vendorInfo;
    private Boolean isVendorChat;
    private List<ChatParticipantDTO> participants;
    private ChatMessageDTO lastMessage;
    private Long unreadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}