package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Page<Product> getProductPage(Specification<Product> specification, Pageable pageable);

    Optional<Product> getProductById(Long productId);

    Optional<Category> getProductCategoryByProductId(Long productId);

    Optional<List<DeliveryMethod>> getProductDeliveryMethodsByProductId(Long productId);

    Optional<List<ProductMedia>> getProductMediaByProductId(Long productId);

    Optional<List<ProductCharacteristic>> getProductCharacteristicsByProductId(Long productId);

    Optional<Page<ProductReview>> getProductReviewsByProductId(Long productId, Specification<ProductReview> specification, Pageable pageable);
}
