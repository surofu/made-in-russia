package com.surofu.madeinrussia.infrastructure.persistence.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class YandexTranslationRepository implements TranslationRepository {

    private final RestClient yandexTranslatorRestClient;
    private final ObjectMapper objectMapper;

    @Value("${app.yandex.translate.folder-id}")
    private String folderId;
    @Value("${app.yandex.translate.secret}")
    private String apiSecret;

    @Override
    public TranslationResponse translateToEn(String ...texts) throws IOException, InterruptedException {
        return translate("en", texts);
    }

    @Override
    public TranslationResponse translateToRu(String ...texts) throws IOException, InterruptedException {
        return translate("ru", texts);
    }

    @Override
    public TranslationResponse translateToZh(String ...texts) throws IOException, InterruptedException {
        return translate("zh", texts);
    }

    private TranslationResponse translate(String language, String ...texts) throws IOException, InterruptedException {
        TranslationRequest translationRequest = new TranslationRequest(
                language,
                texts,
                folderId,
                "html"
        );

        String requestBody = objectMapper.writeValueAsString(translationRequest);

        return yandexTranslatorRestClient.post()
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Api-Key %s", apiSecret))
                .body(requestBody)
                .retrieve()
                .body(YandexTranslationResponse.class);
    }

    private record TranslationRequest(
            String targetLanguageCode,
            String[] texts,
            String folderId,
            String textType
    ) {
    }
}
