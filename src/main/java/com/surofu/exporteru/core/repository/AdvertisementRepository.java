package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
import com.surofu.exporteru.infrastructure.persistence.advertisement.AdvertisementView;
import com.surofu.exporteru.infrastructure.persistence.advertisement.AdvertisementWithTranslationsView;

import java.util.List;
import java.util.Optional;

public interface AdvertisementRepository {

    Optional<Advertisement> getById(Long id);

    List<AdvertisementView> getAllViewsByLang(String lang);

    List<AdvertisementView> getAllViewsByLang(String lang, String text, String sort, String direction);

    List<AdvertisementWithTranslationsView> getAllViewsWithTranslationsByLang(String lang);

    List<AdvertisementWithTranslationsView> getAllViewsWithTranslationsByLang(String lang, String text, String sort, String direction);

    Optional<AdvertisementView> getViewByIdAndLang(Long id, String lang);

    Optional<AdvertisementWithTranslationsView> getViewWithTranslationsByIdAndLang(Long id, String lang);

    void save(Advertisement advertisement);

    void delete(Advertisement advertisement);
}
