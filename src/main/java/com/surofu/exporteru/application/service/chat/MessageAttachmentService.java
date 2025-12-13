package com.surofu.exporteru.application.service.chat;

import com.surofu.exporteru.core.model.chat.ChatMessage;
import com.surofu.exporteru.core.model.chat.MessageAttachment;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.MessageAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с прикрепленными файлами сообщений
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageAttachmentService {

    private final MessageAttachmentRepository attachmentRepository;
    private final FileStorageRepository fileStorageRepository;

    private static final String CHAT_ATTACHMENTS_FOLDER = "chat-attachments";

    /**
     * Сохранить вложения сообщения
     */
    @Transactional
    public List<MessageAttachment> saveAttachments(ChatMessage message, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        List<MessageAttachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                MessageAttachment attachment = saveAttachment(message, file);
                attachments.add(attachment);
            } catch (Exception e) {
                log.error("Failed to save attachment: {}", file.getOriginalFilename(), e);
            }
        }

        return attachments;
    }

    /**
     * Сохранить одно вложение
     */
    private MessageAttachment saveAttachment(ChatMessage message, MultipartFile file) throws Exception {
        String fileUrl = fileStorageRepository.uploadImageToFolder(file, CHAT_ATTACHMENTS_FOLDER);

        MessageAttachment attachment = new MessageAttachment();
        attachment.setMessage(message);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileUrl(fileUrl);
        attachment.setFileSize(file.getSize());
        attachment.setMimeType(file.getContentType());

        return attachmentRepository.save(attachment);
    }

}
