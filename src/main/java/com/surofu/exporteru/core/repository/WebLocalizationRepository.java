package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.localization.WebLocalization;

import java.util.List;
import java.util.Optional;

public interface WebLocalizationRepository {
    List<WebLocalization> getAll();

    Optional<WebLocalization> getByLanguageCode(String languageCode);

    void save(WebLocalization webLocalization);

    void delete(WebLocalization webLocalization);
}
