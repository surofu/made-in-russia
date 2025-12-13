package com.surofu.exporteru.infrastructure.persistence.localization;

import com.surofu.exporteru.core.model.localization.WebLocalization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataWebLocalizationRepository extends JpaRepository<WebLocalization, Long> {
    Optional<WebLocalization> findByLanguageCode(String languageCode);
}
