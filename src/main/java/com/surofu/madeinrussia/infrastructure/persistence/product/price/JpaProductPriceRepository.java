package com.surofu.madeinrussia.infrastructure.persistence.product.price;

import com.surofu.madeinrussia.core.model.product.price.ProductPrice;
import com.surofu.madeinrussia.core.repository.ProductPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class JpaProductPriceRepository implements ProductPriceRepository {

    private final SpringDataProductPriceRepository repository;

    @Override
    public List<ProductPriceView> findAllViewsByProductId(Long productId, Locale locale) {
        return repository.findAllByProductIdAndLang(productId, locale.getLanguage());
    }

    @Override
    public List<ProductPrice> getAllByProductId(Long id) {
        return repository.findAllByProductId(id);
    }

    @Override
    public void deleteAll(Collection<ProductPrice> oldProductPrices) {
        repository.deleteAll(oldProductPrices);
    }

    @Override
    public void saveAll(Collection<ProductPrice> productPriceList) {
        repository.saveAll(productPriceList);
    }
}

