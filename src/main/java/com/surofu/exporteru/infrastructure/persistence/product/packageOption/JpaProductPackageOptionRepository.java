package com.surofu.exporteru.infrastructure.persistence.product.packageOption;

import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import com.surofu.exporteru.core.repository.ProductPackageOptionsRepository;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaProductPackageOptionRepository implements ProductPackageOptionsRepository {

  private final SpringDataProductPackageOptionRepository repository;

  @Override
  public List<ProductPackageOptionView> getAllViewsByProductIdAndLang(Long productId, String lang) {
    return repository.findAllViewsByProductIdAndLang(productId, lang);
  }

  public List<ProductPackageOptionWithTranslationsView> getAllViewsWithTranslationsByProductIdAndLang(
      Long productId, String lang) {
    return repository.findAllViewsWithTranslationsByProductIdAndLang(productId, lang);
  }

  @Override
  public List<ProductPackageOption> getAllByProductId(Long id) {
    return repository.findAllByProductId(id);
  }

  @Override
  public void deleteAll(Collection<ProductPackageOption> oldProductPackageOptions) {
    repository.deleteAll(oldProductPackageOptions);
  }

  @Override
  public void saveAll(Collection<ProductPackageOption> productPackageOptionSet) {
    repository.saveAll(productPackageOptionSet);
  }

  @Override
  public void flush() {
    repository.flush();
  }
}
