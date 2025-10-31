package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.CompanyCacheManager;
import com.surofu.exporteru.application.cache.CompanyFirstNameCacheManager;
import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.core.model.okved.OkvedCompany;
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.company.CompanyService;
import com.surofu.exporteru.core.service.company.operation.GetCompaniesByCategorySlug;
import com.surofu.exporteru.infrastructure.persistence.okved.OkvedCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyApplicationService implements CompanyService {

    private final CategoryRepository categoryRepository;
    private final OkvedCompanyRepository okvedCompanyRepository;
    private final TaskExecutor appTaskExecutor;
    private final TranslationRepository translationRepository;
    private final CompanyFirstNameCacheManager companyFirstNameCacheManager;
    private final CompanyCacheManager companyCacheManager;

    private final Semaphore dbOperationSemaphore = new Semaphore(20); // Ограничиваем до 20 параллельных операций

    @Override
    @Transactional(readOnly = true)
    public GetCompaniesByCategorySlug.Result getByCategorySlug(GetCompaniesByCategorySlug operation) {
        // Check cache
        if (companyCacheManager.contains(operation.getCategorySlug().toString(), operation.getLocale())) {
            return GetCompaniesByCategorySlug.Result.success(companyCacheManager.get(operation.getCategorySlug().toString(), operation.getLocale()));
        }

        // Act
        List<String> okvedCategoryIds = categoryRepository.getOkvedCategoryIdsBySlug(operation.getCategorySlug());

        List<CompletableFuture<List<OkvedCompany>>> futures = okvedCategoryIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> {
                    try {
                        dbOperationSemaphore.acquire(); // Блокируем если достигли лимита
                        return okvedCompanyRepository.findByOkvedId(id);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Operation interrupted", e);
                    } finally {
                        dbOperationSemaphore.release(); // Освобождаем
                    }
                }, appTaskExecutor))
                .toList();
        if (Locale.forLanguageTag("ru").equals(operation.getLocale())) {
            List<OkvedCompany> okvedCompanyList = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .toList();

            if (!okvedCompanyList.isEmpty()) {
                companyCacheManager.set(operation.getCategorySlug().toString(), operation.getLocale(), okvedCompanyList);
            }

            return GetCompaniesByCategorySlug.Result.success(okvedCompanyList);
        }

        List<OkvedCompany> okvedCompanyList = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .map(c -> {
                    Pattern pattern = Pattern.compile("\"(.*?)\"$");
                    Matcher matcher = pattern.matcher(c.name());

                    if (matcher.find()) {
                        String firstPart = c.name().split("\"")[0];
                        String translatedFirstPart;

                        if (companyFirstNameCacheManager.contains(firstPart)) {
                            HstoreTranslationDto cachedTranslations = companyFirstNameCacheManager.get(firstPart);

                            translatedFirstPart = switch (operation.getLocale().getLanguage()) {
                                case "en" -> cachedTranslations.textEn();
                                case "ru" -> cachedTranslations.textRu();
                                case "zh" -> cachedTranslations.textZh();
                                default -> cachedTranslations.textEn();
                            };
                        } else {
                            try {
                                if (firstPart == null || StringUtils.trimToNull(firstPart) == null) {
                                    translatedFirstPart = firstPart;
                                } else {
                                    HstoreTranslationDto translationResponse = translationRepository.expand(firstPart);
                                    translatedFirstPart = switch (operation.getLocale().getLanguage()) {
                                        case "en" -> translationResponse.textEn();
                                        case "ru" -> translationResponse.textRu();
                                        case "zh" -> translationResponse.textZh();
                                        default -> translationResponse.textEn();
                                    };
                                    companyFirstNameCacheManager.set(firstPart, translationResponse);
                                }
                            } catch (Exception e) {
                                log.warn(e.getMessage());
                                translatedFirstPart = firstPart;
                            }
                        }

                        String transliteratedName = TransliterationManager.transliterate(matcher.group(1));
                        String result = "%s\"%s\"".formatted(translatedFirstPart, transliteratedName).toUpperCase();

                        return new OkvedCompany(
                                result,
                                c.inn(),
                                c.ageInYears()
                        );
                    }

                    return new OkvedCompany(
                            TransliterationManager.transliterate(c.name()),
                            c.inn(),
                            c.ageInYears()
                    );
                })
                .toList();

        if (!okvedCompanyList.isEmpty()) {
            companyCacheManager.set(operation.getCategorySlug().toString(), operation.getLocale(), okvedCompanyList);
        }

        return GetCompaniesByCategorySlug.Result.success(okvedCompanyList);
    }
}
