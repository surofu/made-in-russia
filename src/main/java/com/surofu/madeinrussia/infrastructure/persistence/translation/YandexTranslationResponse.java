package com.surofu.madeinrussia.infrastructure.persistence.translation;

public record YandexTranslationResponse(
        YandexTranslation[] translations
) implements TranslationResponse {

    @Override
    public YandexTranslation[] getTranslations() {
        return translations;
    }
}