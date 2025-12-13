package com.surofu.exporteru.application.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO для списка чатов
 */
@Getter
@Setter
@Builder
public class ChatListResponse {
    private List<ChatDTO> chats;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
    private Boolean hasMore;
}