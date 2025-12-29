package com.surofu.exporteru.application.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for translating chat message
 */
@Data
public class TranslateMessageRequest {

    @NotBlank(message = "Text is required")
    @Size(max = 4000, message = "Text must not exceed 4000 characters")
    private String text;

    @NotBlank(message = "Target language is required")
    @Pattern(regexp = "^(en|ru|zh|hi)$", message = "Target language must be one of: en, ru, zh, hi")
    private String targetLanguage;


    @Pattern(regexp = "^(en|ru|zh|hi)?$", message = "Source language must be one of: en, ru, zh, hi")
    private String sourceLanguage;
}