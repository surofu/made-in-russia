package com.surofu.exporteru.application.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для прикрепленного файла
 */
@Getter
@Setter
@Builder
public class MessageAttachmentDTO {
    private Long id;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String mimeType;
}