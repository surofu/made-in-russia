package com.surofu.exporteru.infrastructure.persistence.product.media;

import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.core.repository.ProductMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaProductMediaRepository implements ProductMediaRepository {

    private final SpringDataProductMediaRepository repository;

    @Override
    public List<ProductMediaView> getAllViewsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductMediaWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(Long productId, String lang) {
        return repository.findAllWithTranslationsByProductIdAndLang(productId, lang);
    }

    @Override
    public List<ProductMedia> getAllByProductId(Long id) {
        return repository.findAllByProductId(id);
    }

    @Override
    public void deleteAll(Collection<ProductMedia> oldProductMedia) {
        repository.deleteAll(oldProductMedia);
    }

    @Override
    public void saveAll(Collection<ProductMedia> productMediaList) {
        repository.saveAll(productMediaList);
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
