package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.core.model.localization.WebLocalization;
import com.surofu.madeinrussia.core.repository.WebLocalizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncWebLocalizationService {

    private final WebLocalizationRepository repository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(WebLocalization webLocalization) {
        try {
            repository.save(webLocalization);
        } catch (Exception e) {
            log.error("Error while saving web localization: {}", e.getMessage());
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(WebLocalization webLocalization) {
        try {
            repository.delete(webLocalization);
        } catch (Exception e) {
            log.error("Error while deleting web localization: {}", e.getMessage());
        }
    }
}
