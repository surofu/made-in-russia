package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.ProductArticleCode;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.infrastructure.persistence.product.SearchHintView;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> getProductById(Long productId);

    Optional<Product> getProductByArticleCode(ProductArticleCode articleCode);

    Optional<Category> getProductCategoryByProductId(Long productId);

    List<DeliveryMethod> getProductDeliveryMethodsByProductId(Long productId);

    Optional<List<ProductMedia>> getProductMediaByProductId(Long productId);

    Optional<List<ProductCharacteristic>> getProductCharacteristicsByProductId(Long productId);

    Optional<List<ProductFaq>> getProductFaqByProductId(Long productId);

    void save(Product product);

    Optional<Long> firstNotExists(List<Long> productIds);

    List<Product> findAllByIds(List<Long> productIds);

    Optional<Double> getProductRating(Long productId);

    boolean existsById(Long productId);

    List<SearchHintView> findHintViews(String searchTerm);
}
