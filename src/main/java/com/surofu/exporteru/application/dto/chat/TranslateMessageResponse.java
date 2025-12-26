package com.surofu.exporteru.application.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for translated chat message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateMessageResponse {

    private String translatedText;
    private String targetLanguage;
    private String detectedSourceLanguage;
}