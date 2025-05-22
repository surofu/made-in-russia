package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p")
    @EntityGraph(attributePaths = {"category", "media", "characteristics"})
    Page<Product> getProductPage(Specification<Product> specification, Pageable pageable);

    @Query("select p from Product p where p.id = :productId")
    @EntityGraph(attributePaths = {"category", "media", "characteristics"})
    Optional<Product> getProductById(Long productId);

    @Query("select p.category from Product p where p.id = :productId")
    Optional<Category> getProductCategoryByProductId(Long productId);

    @Query("select p.deliveryMethods from Product p where p.id = :productId")
    Optional<List<DeliveryMethod>> getProductDeliveryMethodsByProductId(Long productId);

    @Query("select p.media from Product p where p.id = :productId")
    Optional<List<ProductMedia>> getProductMediaByProductId(Long productId);

    @Query("select p.characteristics from Product p where p.id = :productId")
    Optional<List<ProductCharacteristic>> getProductCharacteristicsByProductId(Long productId);

    @Query("select p.reviews from Product p where p.id = :productId")
    Optional<Page<ProductReview>> getProductReviewsByProductId(Long productId, Specification<ProductReview> specification, Pageable pageable);
}
