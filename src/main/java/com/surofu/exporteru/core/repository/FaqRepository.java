package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.faq.Faq;
import com.surofu.exporteru.infrastructure.persistence.faq.FaqView;
import com.surofu.exporteru.infrastructure.persistence.faq.FaqWithTranslationsView;

import java.util.List;
import java.util.Optional;

public interface FaqRepository {
    List<FaqView> getAllViewsByLang(String lang);

    Optional<Faq> getById(Long id);

    Optional<FaqView> getViewByIdAndLang(Long id, String lang);

    Optional<FaqWithTranslationsView> getViewWithTranslationsByIdAndLang(Long id, String lang);

    Faq save(Faq faq);

    void delete(Faq faq);
}
