package com.surofu.exporteru.application.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO для списка сообщений
 */
@Getter
@Setter
@Builder
public class MessageListResponse {
    private List<ChatMessageDTO> messages;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
    private Boolean hasMore;
}