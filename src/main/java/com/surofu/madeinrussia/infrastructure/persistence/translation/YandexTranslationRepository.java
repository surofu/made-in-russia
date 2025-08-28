package com.surofu.madeinrussia.infrastructure.persistence.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.exception.EmptyTranslationException;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

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
    public HstoreTranslationDto expand(HstoreTranslationDto dto) throws EmptyTranslationException, IOException {
        if (dto == null) {
            throw new EmptyTranslationException("Empty translation dto");
        }

        String en = StringUtils.trimToNull(dto.textEn());
        String ru = StringUtils.trimToNull(dto.textRu());
        String zh = StringUtils.trimToNull(dto.textZh());

        if (en == null && ru == null && zh == null) {
            throw new EmptyTranslationException("Empty translation dto");
        }

        if (en == null) {
            if (ru != null) {
                en = translateToEn(ru).getTranslations()[0].getText();
            } else {
                en = translateToEn(zh).getTranslations()[0].getText();
            }
        }

        if (ru == null) {
            if (en != null) {
                ru = translateToRu(en).getTranslations()[0].getText();
            } else {
                ru = translateToRu(zh).getTranslations()[0].getText();
            }
        }

        if (zh == null) {
            if (ru != null) {
                zh = translateToZh(ru).getTranslations()[0].getText();
            } else {
                zh = translateToZh(en).getTranslations()[0].getText();
            }
        }

        return new HstoreTranslationDto(en, ru, zh);
    }

    @Override
    public HstoreTranslationDto expand(String text) throws EmptyTranslationException, IOException {
        TranslationResponse en = translateToEn(text);
        TranslationResponse ru = translateToRu(text);
        TranslationResponse zh = translateToZh(text);
        return new HstoreTranslationDto(en.getTranslations()[0].getText(), ru.getTranslations()[0].getText(), zh.getTranslations()[0].getText());
    }

    @Override
    public List<HstoreTranslationDto> expand(String... texts) throws EmptyTranslationException, IOException {
        TranslationResponse en = translateToEn(texts);
        TranslationResponse ru = translateToRu(texts);
        TranslationResponse zh = translateToZh(texts);

        List<HstoreTranslationDto> dtoList = new ArrayList<>();

        for (int i = 0; i < texts.length; i++) {
            dtoList.add(new HstoreTranslationDto(
                    en.getTranslations()[i].getText(),
                    ru.getTranslations()[i].getText(),
                    zh.getTranslations()[i].getText()
            ));
        }

        return dtoList;
    }

    @Override
    public Map<String, List<HstoreTranslationDto>> expandStrings(Map<String, List<String>> map) throws IOException {
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> stringList = new ArrayList<>(map.values().stream().mapToInt(List::size).sum());
        map.values().forEach(stringList::addAll);

        TranslationResponse responseEn = translate("en", stringList.toArray(new String[0]));
        TranslationResponse responseRu = translate("ru", stringList.toArray(new String[0]));
        TranslationResponse responseZh = translate("zh", stringList.toArray(new String[0]));
        Map<String, List<HstoreTranslationDto>> result = new HashMap<>();

        AtomicInteger index = new AtomicInteger();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            entry.getValue().forEach(unused -> {
                String en = responseEn.getTranslations()[index.get()].getText();
                String ru = responseRu.getTranslations()[index.get()].getText();
                String zh = responseZh.getTranslations()[index.get()].getText();
                HstoreTranslationDto dto = new HstoreTranslationDto(en, ru, zh);
                result.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(dto);
                index.addAndGet(1);
            });
        }

        return result;
    }

    @Override
    public Map<String, HstoreTranslationDto> expand(Map<String, HstoreTranslationDto> map)
            throws EmptyTranslationException, ExecutionException {

        if (map == null) {
            throw new EmptyTranslationException("Input map cannot be null");
        }
        if (map.isEmpty()) {
            throw new EmptyTranslationException("Input map cannot be empty");
        }

        // Копируем исходные данные в результат
        Map<String, HstoreTranslationDto> result = new ConcurrentHashMap<>(map);
        TranslationMap translationMap = new TranslationMap();

        // Анализ и подготовка переводов
        for (Map.Entry<String, HstoreTranslationDto> entry : map.entrySet()) {
            String key = entry.getKey();
            HstoreTranslationDto dto = entry.getValue();

            String en = StringUtils.trimToNull(dto.textEn());
            String ru = StringUtils.trimToNull(dto.textRu());
            String zh = StringUtils.trimToNull(dto.textZh());

            if (en == null && ru == null && zh == null) {
                throw new EmptyTranslationException(key);
            }

            // Заполняем только отсутствующие переводы
            if (en == null) {
                if (ru != null) {
                    translationMap.put(key, LanguageCode.RU, LanguageCode.EN, ru);
                } else {
                    translationMap.put(key, LanguageCode.ZH, LanguageCode.EN, zh);
                }
            }

            if (ru == null) {
                if (en != null) {
                    translationMap.put(key, LanguageCode.EN, LanguageCode.RU, en);
                } else {
                    translationMap.put(key, LanguageCode.ZH, LanguageCode.RU, zh);
                }
            }

            if (zh == null) {
                if (en != null) {
                    translationMap.put(key, LanguageCode.EN, LanguageCode.ZH, en);
                } else {
                    translationMap.put(key, LanguageCode.RU, LanguageCode.ZH, ru);
                }
            }
        }

        // Параллельное выполнение переводов
        try {
            CompletableFuture.allOf(
                    translateAsync(LanguageCode.EN, translationMap.get(LanguageCode.RU, LanguageCode.EN), result),
                    translateAsync(LanguageCode.EN, translationMap.get(LanguageCode.ZH, LanguageCode.EN), result)
            ).join();
        } catch (Exception e) {
            throw new ExecutionException("Failed to execute translations (Part 1)", e.getCause());
        }

        try {
            CompletableFuture.allOf(
                    translateAsync(LanguageCode.RU, translationMap.get(LanguageCode.EN, LanguageCode.RU), result),
                    translateAsync(LanguageCode.RU, translationMap.get(LanguageCode.ZH, LanguageCode.RU), result)
            ).join();
        } catch (Exception e) {
            throw new ExecutionException("Failed to execute translations (Part 2)", e.getCause());
        }

        try {
            CompletableFuture.allOf(
                    translateAsync(LanguageCode.ZH, translationMap.get(LanguageCode.EN, LanguageCode.ZH), result),
                    translateAsync(LanguageCode.ZH, translationMap.get(LanguageCode.RU, LanguageCode.ZH), result)
            ).join();

            return result;
        } catch (Exception e) {
            throw new ExecutionException("Failed to execute translations (Part 3)", e.getCause());
        }
    }

    private TranslationResponse translate(String language, String... texts) throws IOException {
        if (texts == null || texts.length == 0) {
            return new YandexTranslationResponse(new YandexTranslation[]{});
        }

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

    private CompletableFuture<Void> translateAsync(LanguageCode language, Map<String, String> map, Map<String, HstoreTranslationDto> result) {
        return CompletableFuture.supplyAsync(() -> {
            if (map == null || map.isEmpty()) {
                return null;
            }

            try {
                String[] strings = map.values().toArray(String[]::new);
                TranslationResponse response = translate(language.name().toLowerCase(), strings);

                int index = 0;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    HstoreTranslationDto existing = result.get(key); // Берем существующую запись
                    Translation translation = response.getTranslations()[index++];

                    result.put(key, updateTranslation(existing, language, translation.getText()));
                }
            } catch (Exception e) {
                throw new CompletionException(e);
            }
            return null;
        }, appTaskExecutor);
    }

    private HstoreTranslationDto updateTranslation(HstoreTranslationDto existing, LanguageCode language, String text) {
        return switch (language) {
            case EN -> new HstoreTranslationDto(text, existing.textRu(), existing.textZh());
            case RU -> new HstoreTranslationDto(existing.textEn(), text, existing.textZh());
            case ZH -> new HstoreTranslationDto(existing.textEn(), existing.textRu(), text);
        };
    }

    private enum LanguageCode {
        EN, RU, ZH
    }

    private record TranslationRequest(
            String targetLanguageCode,
            String[] texts,
            String folderId,
            String textType
    ) {
    }

    private static class TranslationMap {
        private final Map<String, Map<LanguageCode, Map<LanguageCode, String>>> map;

        public TranslationMap() {
            map = new HashMap<>();
        }

        public void put(String key, LanguageCode from, LanguageCode to, String text) {
            map.computeIfAbsent(key, k -> new HashMap<>())
                    .computeIfAbsent(from, f -> new HashMap<>())
                    .put(to, text);
        }

        public Map<String, String> get(LanguageCode from, LanguageCode to) {
            Map<String, String> resultMap = new HashMap<>();

            for (var entry : map.entrySet()) {
                Map<LanguageCode, String> translations = entry.getValue().get(from);
                if (translations == null) continue;

                String text = translations.get(to);
                if (text == null) continue;

                resultMap.put(entry.getKey(), text);
            }

            return resultMap;
        }
    }
}
