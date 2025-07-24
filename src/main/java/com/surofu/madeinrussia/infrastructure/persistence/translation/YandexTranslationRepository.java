package com.surofu.madeinrussia.infrastructure.persistence.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    public TranslationResponse translateToEn(String... texts) throws IOException, InterruptedException {
        return translate("en", texts);
    }

    @Override
    public TranslationResponse translateToRu(String... texts) throws IOException, InterruptedException {
        return translate("ru", texts);
    }

    @Override
    public TranslationResponse translateToZh(String... texts) throws IOException, InterruptedException {
        return translate("zh", texts);
    }

    @Override
    public HstoreTranslationDto expend(HstoreTranslationDto dto) throws EmptyTranslationException, IOException, InterruptedException {
        String en = dto.textEn();
        String ru = dto.textRu();
        String zh = dto.textZh();

        if ((en == null || en.isEmpty()) && (ru == null || ru.isEmpty()) && (zh == null || zh.isEmpty())) {
            throw new EmptyTranslationException();
        }

        if (en == null || en.isEmpty()) {
            if (ru != null && !ru.isEmpty()) {
                TranslationResponse translationResponse = translateToEn(ru);
                en = translationResponse.getTranslations()[0].getText();
            } else {
                TranslationResponse translationResponse = translateToEn(zh);
                en = translationResponse.getTranslations()[0].getText();
            }
        }

        if (ru == null || ru.isEmpty()) {
            if (en != null && !en.isEmpty()) {
                TranslationResponse translationResponse = translateToRu(en);
                ru = translationResponse.getTranslations()[0].getText();
            } else {
                TranslationResponse translationResponse = translateToRu(zh);
                ru = translationResponse.getTranslations()[0].getText();
            }
        }

        if (zh == null || zh.isEmpty()) {
            if (ru != null && !ru.isEmpty()) {
                TranslationResponse translationResponse = translateToZh(ru);
                zh = translationResponse.getTranslations()[0].getText();
            } else {
                TranslationResponse translationResponse = translateToZh(en);
                zh = translationResponse.getTranslations()[0].getText();
            }
        }

        return new HstoreTranslationDto(en, ru, zh);
    }

    @Override
    public Map<String, HstoreTranslationDto> expend(Map<String, HstoreTranslationDto> map) throws EmptyTranslationException, InterruptedException, ExecutionException {
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

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<TranslationResponse> futureEn = executor.submit(() ->
                translateToEn(forTranslateEnStrings));
        Future<TranslationResponse> futureRu = executor.submit(() ->
                translateToRu(forTranslateRuStrings));
        Future<TranslationResponse> futureZh = executor.submit(() ->
                translateToZh(forTranslateZhStrings));

        TranslationResponse translationResponseEn, translationResponseRu, translationResponseZh;

        translationResponseEn = forTranslateEnStrings.length > 0 ? futureEn.get() : new YandexTranslationResponse(new YandexTranslation[]{});
        translationResponseRu = forTranslateRuStrings.length > 0 ? futureRu.get() : new YandexTranslationResponse(new YandexTranslation[]{});
        translationResponseZh = forTranslateZhStrings.length > 0 ? futureZh.get() : new YandexTranslationResponse(new YandexTranslation[]{});

        executor.shutdown();

        List<String> resultListEn = Arrays.stream(translationResponseEn.getTranslations()).map(Translation::getText).toList();
        List<String> resultListRu = Arrays.stream(translationResponseRu.getTranslations()).map(Translation::getText).toList();
        List<String> resultListZh = Arrays.stream(translationResponseZh.getTranslations()).map(Translation::getText).toList();

        Map<String, HstoreTranslationDto> result = new HashMap<>(map);

        for (int i = 0; i < resultListEn.size(); i++) {
            String resultEn = resultListEn.get(i);
            String key = forTranslateEn.get(i).getKey();

            result.put(key, new HstoreTranslationDto(
                    resultEn,
                    result.get(key).textRu(),
                    result.get(key).textZh()
            ));
        }

        for (int i = 0; i < resultListRu.size(); i++) {
            String resultRu = resultListRu.get(i);
            String key = forTranslateRu.get(i).getKey();

            result.put(key, new HstoreTranslationDto(
                    result.get(key).textEn(),
                    resultRu,
                    result.get(key).textZh()
            ));
        }

        for (int i = 0; i < resultListZh.size(); i++) {
            String resultZh = resultListZh.get(i);
            String key = forTranslateZh.get(i).getKey();

            result.put(key, new HstoreTranslationDto(
                    result.get(key).textEn(),
                    result.get(key).textRu(),
                    resultZh
            ));
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
