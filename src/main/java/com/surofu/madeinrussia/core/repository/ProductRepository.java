package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.media.ProductMedia;
import com.surofu.madeinrussia.infrastructure.persistence.product.ProductView;
import com.surofu.madeinrussia.infrastructure.persistence.product.ProductWithTranslationsView;
import com.surofu.madeinrussia.infrastructure.persistence.product.SearchHintView;
import com.surofu.madeinrussia.infrastructure.persistence.product.SimilarProductView;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> getProductById(Long productId);

    Optional<Product> getProductById(Long productId, List<ApproveStatus> approveStatuses);

    Optional<Product> getProductByIdWithAnyApproveStatus(Long productId);

    Optional<Category> getProductCategoryByProductId(Long productId);

    List<DeliveryMethod> getProductDeliveryMethodsByProductId(Long productId);

    Optional<List<ProductMedia>> getProductMediaByProductId(Long productId);


    void save(Product product);

    Optional<Long> firstNotExists(List<Long> productIds);

    List<Product> findAllByIds(List<Long> productIds);

    boolean existsById(Long productId);

    List<SearchHintView> findHintViews(String searchTerm, Long vendorId, Locale locale);

    void delete(Product product);

    void deleteByUserId(Long userId);

    // View

    Optional<ProductView> getProductViewByIdAndLangAndApproveStatuses(Long productId, String lang, List<ApproveStatus> approveStatuses);

    Optional<ProductView> getProductViewByArticleAndLang(String article, String lang);

    List<SimilarProductView> getAllSimilarProductViewsByProductIdAndLang(Long id, String lang);

    Optional<ProductWithTranslationsView> getProductWithTranslationsByProductIdAndLang(Long id, String lang);

    void flush();
}
