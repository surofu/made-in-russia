package com.surofu.exporteru.infrastructure.persistence.localization;

import com.surofu.exporteru.core.model.localization.WebLocalization;
import com.surofu.exporteru.core.repository.WebLocalizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaWebLocalizationRepository implements WebLocalizationRepository {

    private final SpringDataWebLocalizationRepository repository;

    @Override
    public List<WebLocalization> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<WebLocalization> getByLanguageCode(String languageCode) {
        return repository.findByLanguageCode(languageCode);
    }

    @Override
    public void save(WebLocalization webLocalization) {
        repository.save(webLocalization);
    }

    @Override
    public void delete(WebLocalization webLocalization) {
        repository.delete(webLocalization);
    }
}
