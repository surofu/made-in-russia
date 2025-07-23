package com.surofu.madeinrussia.infrastructure.persistence.advertisement;

import com.surofu.madeinrussia.core.model.advertisement.Advertisement;
import com.surofu.madeinrussia.core.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaAdvertisementRepository implements AdvertisementRepository {

    private final SpringDataAdvertisementRepository repository;

    @Override
    public Optional<Advertisement> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<AdvertisementView> getAllViewsByLang(String lang) {
        return repository.findAllViewsByLang(lang);
    }

    @Override
    public Optional<AdvertisementView> getViewByIdAndLang(Long id, String lang) {
        return repository.findViewByIdAndLang(id, lang);
    }

    @Override
    public Optional<AdvertisementWithTranslationsView> getViewWithTranslationsByIdAndLang(Long id, String lang) {
        return repository.findViewWithTranslationsByIdAndLang(id, lang);
    }

    @Override
    public void save(Advertisement advertisement) {
        repository.save(advertisement);
    }

    @Override
    public void delete(Advertisement advertisement) {
        repository.delete(advertisement);
    }
}
