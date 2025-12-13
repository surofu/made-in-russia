package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.infrastructure.persistence.product.faq.ProductFaqView;
import com.surofu.exporteru.infrastructure.persistence.product.faq.ProductFaqWithTranslationsView;
import java.util.Collection;
import java.util.List;

public interface ProductFaqRepository {
  List<ProductFaqView> findAllViewsByProductIdAndLang(Long productId, String lang);

  List<ProductFaqWithTranslationsView> findAllWithTranslationsByProductIdAndLang(Long productId,
                                                                                 String lang);

  List<ProductFaq> getAllByProductId(Long id);

  void deleteAll(Collection<ProductFaq> oldProductFaq);

  void saveAll(Collection<ProductFaq> productFaqSet);

  void flush();
}
