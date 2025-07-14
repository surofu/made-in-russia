package com.surofu.madeinrussia.infrastructure.persistence.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class YandexTranslationRepository implements TranslationRepository {

    private final ObjectMapper objectMapper;

    @Value("${app.yandex.translate.uri}")
    private String uri;
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
                folderId
        );
        String requestBody = objectMapper.writeValueAsString(translationRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .version(HttpClient.Version.HTTP_2)
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Api-Key %s", apiSecret))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HttpClient
                .newBuilder()
                .proxy(ProxySelector.getDefault())
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), YandexTranslationResponse.class);
    }

    private record TranslationRequest(
            String targetLanguageCode,
            String[] texts,
            String folderId
    ) {
    }
}
