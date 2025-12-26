package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.dto.chat.*;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.service.chat.ChatMessageService;
import com.surofu.exporteru.application.service.chat.ChatService;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.infrastructure.persistence.translation.TranslationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST контроллер для работы с чатами
 */
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat API for product discussions")
public class ChatRestController {

    private final ChatService chatService;
    private final ChatMessageService messageService;
    private final TranslationRepository translationRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create or get chat for product")
    public ChatDTO createChat(
            @Valid @RequestBody CreateChatRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUser().getId();
        return chatService.createOrGetChat(request.getProductId(), userId);
    }

    @PostMapping("/vendor")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create or get chat with vendor")
    public ChatDTO createVendorChat(
            @Valid @RequestBody CreateVendorChatRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUser().getId();
        return chatService.createOrGetVendorChat(request.getVendorId(), userId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get user chats")
    public ChatListResponse getUserChats(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long userId = securityUser.getUser().getId();
        return chatService.getUserChats(userId, pageable);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all chats for seller's product")
    public ChatListResponse getProductChats(
            @PathVariable Long productId,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long userId = securityUser.getUser().getId();
        return chatService.getProductChats(productId, userId, pageable);
    }

    @GetMapping("/{chatId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get chat details")
    public ChatDTO getChatDetails(
            @PathVariable Long chatId,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUser().getId();
        return chatService.getChatDetails(chatId, userId);
    }

    @GetMapping("/{chatId}/messages")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get chat messages")
    public MessageListResponse getChatMessages(
            @PathVariable Long chatId,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = securityUser.getUser().getId();
        return messageService.getChatMessages(chatId, userId, pageable);
    }

    @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Send message")
    public ChatMessageDTO sendMessage(
            @Valid @ModelAttribute SendMessageRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Message must have either content or attachments");
        }
        Long userId = securityUser.getUser().getId();
        return messageService.sendMessage(request, userId);
    }

    @PostMapping("/messages/{messageId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Mark message as read")
    public void markMessageAsRead(
            @PathVariable Long messageId,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUser().getId();
        messageService.markAsRead(messageId, userId);
    }

    @GetMapping("/{chatId}/unread-count")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get unread message count")
    public Map<String, Long> getUnreadCount(
            @PathVariable Long chatId,
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUser().getId();
        Long count = messageService.getUnreadCount(chatId, userId);
        return Map.of("unreadCount", count);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get total unread message count across all chats")
    public Map<String, Long> getTotalUnreadCount(
            @Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUser().getId();
        Long count = messageService.getTotalUnreadCount(userId);
        return Map.of("unreadCount", count);
    }

    @PostMapping("/messages/translate")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Translate message text to target language")
    public TranslateMessageResponse translateMessage(
            @Valid @RequestBody TranslateMessageRequest request
    ) {
        String text = request.getText();
        String targetLanguage = request.getTargetLanguage();

        TranslationResponse response = switch (targetLanguage) {
            case "en" -> translationRepository.translateToEn(text);
            case "ru" -> translationRepository.translateToRu(text);
            case "zh" -> translationRepository.translateToZh(text);
            case "hi" -> translationRepository.translateToHi(text);
            default -> throw new IllegalArgumentException("Unsupported language: " + targetLanguage);
        };

        var translation = response.getTranslations()[0];
        return new TranslateMessageResponse(
                translation.getText(),
                targetLanguage,
                translation.getDetectedLanguageCode()
        );
    }
}