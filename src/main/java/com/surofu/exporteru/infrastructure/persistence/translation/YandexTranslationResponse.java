package com.surofu.exporteru.infrastructure.persistence.translation;

public record YandexTranslationResponse(
        YandexTranslation[] translations
) implements TranslationResponse {

    @Override
    public YandexTranslation[] getTranslations() {
        return translations;
    }
}