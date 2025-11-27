package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.infrastructure.persistence.product.ProductForReviewView;
import com.surofu.exporteru.infrastructure.persistence.product.ProductView;
import com.surofu.exporteru.infrastructure.persistence.product.ProductWithTranslationsView;
import com.surofu.exporteru.infrastructure.persistence.product.SearchHintView;
import com.surofu.exporteru.infrastructure.persistence.product.SimilarProductView;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ProductRepository {
  Optional<Product> getById(Long productId);

  Optional<Product> getProductByIdApproved(Long productId);

  Product getReferenceById(Long productId);

  Optional<Product> getProductByIdWithAnyApproveStatus(Long productId);

  Optional<Category> getProductCategoryByProductId(Long productId);

  List<DeliveryMethod> getProductDeliveryMethodsByProductId(Long productId);

  Optional<List<ProductMedia>> getProductMediaByProductId(Long productId);

  Product save(Product product);

  Optional<Long> firstNotExists(List<Long> productIds);

  List<Product> findAllByIds(List<Long> productIds);

  boolean existsById(Long productId);

  List<SearchHintView> findHintViews(String searchTerm, Long vendorId, Locale locale);

  void delete(Product product);

  void deleteByUserId(Long userId);

  boolean existsInFavorite(Long userId, Long productId);

  boolean existsWithUserId(Long productId, Long userid);

  // View

  Optional<ProductView> getProductViewByIdAndLangAndApproveStatuses(Long productId, String lang,
                                                                    List<ApproveStatus> approveStatuses);

  Optional<ProductView> getProductViewByArticleAndLang(String article, String lang);

  List<SimilarProductView> getAllSimilarProductViewsByProductIdAndLang(Long id, String lang);

  Optional<ProductWithTranslationsView> getProductWithTranslationsByProductIdAndLang(Long id,
                                                                                     String lang);

  List<ProductForReviewView> getProductForReviewViewsByLang(String lang);

  void flush();

  Optional<Product> getProductWithUserById(Long productId);

  Optional<Product> findByIdWithPrices(Long id);

  Optional<Product> getByIdWithDependencies(Long productId);
}
