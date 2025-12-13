package com.surofu.exporteru.infrastructure.persistence.product.price;

import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.repository.ProductPriceRepository;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

  @Override
  public void flush() {
    repository.flush();
  }
}

