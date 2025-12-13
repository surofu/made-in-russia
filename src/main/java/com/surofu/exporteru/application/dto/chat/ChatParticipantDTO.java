package com.surofu.exporteru.application.dto.chat;

import com.surofu.exporteru.core.model.chat.ChatRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO для участника чата
 */
@Getter
@Setter
@Builder
public class ChatParticipantDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private ChatRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;
}