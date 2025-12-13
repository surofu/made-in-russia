package com.surofu.exporteru.application.dto.chat;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Request DTO для отправки сообщения
 */
@Getter
@Setter
public class SendMessageRequest {

    private Long chatId;

    private Long productId;

    @Size(max = 4000, message = "Message is too long")
    private String content;

    private List<MultipartFile> attachments;

    public boolean isValid() {
        boolean hasTarget = chatId != null || productId != null;
        boolean hasContent = (content != null && !content.trim().isEmpty()) ||
                            (attachments != null && !attachments.isEmpty());
        return hasTarget && hasContent;
    }
}