package com.surofu.exporteru.infrastructure.persistence.translation;

public record YandexTranslation(
        String text,
        String detectedLanguageCode
) implements Translation {

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getDetectedLanguageCode() {
        return detectedLanguageCode;
    }
}