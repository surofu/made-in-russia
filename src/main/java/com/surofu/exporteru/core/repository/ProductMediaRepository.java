package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.infrastructure.persistence.product.media.ProductMediaView;
import com.surofu.exporteru.infrastructure.persistence.product.media.ProductMediaWithTranslationsView;

import java.util.Collection;
import java.util.List;

public interface ProductMediaRepository {
    List<ProductMediaView> getAllViewsByProductIdAndLang(Long productId, String lang);

    List<ProductMediaWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang);

    List<ProductMedia> getAllByProductId(Long id);

    void deleteAll(Collection<ProductMedia> oldProductMedia);

    void saveAll(Collection<ProductMedia> productMediaList);

    void flush();

    void deleteAllById(List<Long> mediaIdsToDelete);
}
