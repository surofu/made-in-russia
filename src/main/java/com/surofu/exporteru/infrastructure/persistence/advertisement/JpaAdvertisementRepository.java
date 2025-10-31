package com.surofu.exporteru.infrastructure.persistence.advertisement;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
import com.surofu.exporteru.core.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
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
    public List<AdvertisementView> getAllViewsByLang(String lang, String text, String sort, String direction) {
        List<AdvertisementView> result = repository.findAllViewsByLang(lang, text, sort);

        if (direction.equals("desc")) {
            Collections.reverse(result);
        }

        return result;
    }

    @Override
    public List<AdvertisementWithTranslationsView> getAllViewsWithTranslationsByLang(String lang) {
        return repository.findAllWithTranslationsViewsByLang(lang);
    }

    @Override
    public List<AdvertisementWithTranslationsView> getAllViewsWithTranslationsByLang(String lang, String text, String sort, String direction) {
        List<AdvertisementWithTranslationsView> result = repository.findAllWithTranslationsViewsByLang(lang, text, sort);

        if (direction.equals("desc")) {
            Collections.reverse(result);
        }

        return result;
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
