package com.surofu.madeinrussia.infrastructure.persistence.faq;

import com.surofu.madeinrussia.core.model.faq.Faq;
import com.surofu.madeinrussia.core.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaFaqRepository implements FaqRepository {

    private final SpringDataFaqRepository repository;

    @Override
    public List<FaqView> getAllViewsByLang(String lang) {
        return repository.findAllViewsByLang(lang);
    }

    @Override
    public Optional<FaqView> getViewByIdAndLang(Long id, String lang) {
        return repository.findViewByIdAndLang(id, lang);
    }

    @Override
    public Optional<FaqWithTranslationsView> getViewWithTranslationsByIdAndLang(Long id, String lang) {
        return repository.findViewWithTranslationsByIdAndLang(id, lang);
    }

    @Override
    public Optional<Faq> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void save(Faq faq) {
        repository.save(faq);
    }

    @Override
    public void delete(Faq faq) {
        repository.delete(faq);
    }


}
