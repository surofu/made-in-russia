package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.advertisement.Advertisement;
import com.surofu.madeinrussia.infrastructure.persistence.advertisement.AdvertisementView;
import com.surofu.madeinrussia.infrastructure.persistence.advertisement.AdvertisementWithTranslationsView;

import java.util.List;
import java.util.Optional;

public interface AdvertisementRepository {

    Optional<Advertisement> getById(Long id);

    List<AdvertisementView> getAllViewsByLang(String lang);

    Optional<AdvertisementView> getViewByIdAndLang(Long id, String lang);

    Optional<AdvertisementWithTranslationsView> getViewWithTranslationsByIdAndLang(Long id, String lang);

    void save(Advertisement advertisement);

    void delete(Advertisement advertisement);
}
