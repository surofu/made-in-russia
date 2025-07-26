package com.surofu.madeinrussia.infrastructure.persistence.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class YandexTranslationRepository implements TranslationRepository {

    private final RestClient yandexTranslatorRestClient;
    private final ObjectMapper objectMapper;
    private final TaskExecutor appTaskExecutor;

    @Value("${app.yandex.translate.folder-id}")
    private String folderId;
    @Value("${app.yandex.translate.secret}")
    private String apiSecret;

    @Override
    public TranslationResponse translateToEn(String... texts) throws IOException {
        return translate("en", texts);
    }

    @Override
    public TranslationResponse translateToRu(String... texts) throws IOException {
        return translate("ru", texts);
    }

    @Override
    public TranslationResponse translateToZh(String... texts) throws IOException {
        return translate("zh", texts);
    }

    @Override
    public Map<String, HstoreTranslationDto> expend(Map<String, HstoreTranslationDto> map) throws EmptyTranslationException, InterruptedException, ExecutionException {
        if (map == null || map.isEmpty()) {
            throw new EmptyTranslationException();
        }

        List<Map.Entry<String, String>> forTranslateEn = new ArrayList<>();
        List<Map.Entry<String, String>> forTranslateRu = new ArrayList<>();
        List<Map.Entry<String, String>> forTranslateZh = new ArrayList<>();

        for (Map.Entry<String, HstoreTranslationDto> entry : map.entrySet()) {
            String en = entry.getValue().textEn();
            String ru = entry.getValue().textRu();
            String zh = entry.getValue().textZh();

            if ((en == null || en.isEmpty()) && (ru == null || ru.isEmpty()) && (zh == null || zh.isEmpty())) {
                throw new EmptyTranslationException(entry.getKey());
            }

            if (en == null || en.isEmpty()) {
                forTranslateEn.add(Map.entry(entry.getKey(),
                        ru != null && !ru.isEmpty() ? ru : zh));
            }

            if (ru == null || ru.isEmpty()) {
                forTranslateRu.add(Map.entry(entry.getKey(),
                        en != null && !en.isEmpty() ? en : zh));
            }

            if (zh == null || zh.isEmpty()) {
                forTranslateZh.add(Map.entry(entry.getKey(),
                        ru != null && !ru.isEmpty() ? ru : en));
            }
        }

        String[] forTranslateEnStrings = forTranslateEn.stream().map(Map.Entry::getValue).toArray(String[]::new);
        String[] forTranslateRuStrings = forTranslateRu.stream().map(Map.Entry::getValue).toArray(String[]::new);
        String[] forTranslateZhStrings = forTranslateZh.stream().map(Map.Entry::getValue).toArray(String[]::new);

        Map<String, HstoreTranslationDto> result = new ConcurrentHashMap<>(map);

        CompletableFuture<Void> enFuture = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        if (forTranslateEnStrings.length == 0) {
                            return null;
                        }

                        TranslationResponse response = translateToEn(forTranslateEnStrings); // Исправлено на translateToEn
                        List<String> list = Arrays.stream(response.getTranslations()).map(Translation::getText).toList();

                        for (int i = 0; i < list.size(); i++) {
                            String resultText = list.get(i);
                            String key = forTranslateEn.get(i).getKey();
                            result.put(key, new HstoreTranslationDto(
                                    resultText,
                                    result.get(key).textRu(),
                                    result.get(key).textZh()
                            ));
                        }
                        return null;
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                },
                appTaskExecutor
        );

        CompletableFuture<Void> ruFuture = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        if (forTranslateRuStrings.length == 0) {
                            return null;
                        }

                        TranslationResponse response = translateToRu(forTranslateRuStrings); // Исправлено на translateToRu
                        List<String> list = Arrays.stream(response.getTranslations()).map(Translation::getText).toList();

                        for (int i = 0; i < list.size(); i++) {
                            String resultText = list.get(i);
                            String key = forTranslateRu.get(i).getKey();
                            result.put(key, new HstoreTranslationDto(
                                    result.get(key).textEn(),
                                    resultText,
                                    result.get(key).textZh()
                            ));
                        }
                        return null;
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                },
                appTaskExecutor
        );

        CompletableFuture<Void> zhFuture = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        if (forTranslateZhStrings.length == 0) {
                            return null;
                        }

                        TranslationResponse response = translateToZh(forTranslateZhStrings);
                        List<String> list = Arrays.stream(response.getTranslations()).map(Translation::getText).toList();

                        for (int i = 0; i < list.size(); i++) {
                            String resultText = list.get(i);
                            String key = forTranslateZh.get(i).getKey();
                            result.put(key, new HstoreTranslationDto(
                                    result.get(key).textEn(),
                                    result.get(key).textRu(),
                                    resultText
                            ));
                        }
                        return null;
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                },
                appTaskExecutor
        );

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(enFuture, ruFuture, zhFuture);

        try {
            allFutures.join();
            enFuture.get();
            ruFuture.get();
            zhFuture.get();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }

        return result;
    }

    private TranslationResponse translate(String language, String... texts) throws IOException {
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
