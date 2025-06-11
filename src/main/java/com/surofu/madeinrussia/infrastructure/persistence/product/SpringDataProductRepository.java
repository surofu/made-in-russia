package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.id = :productId")
    @EntityGraph(attributePaths = {"category", "media", "characteristics", "faq"})
    Optional<Product> getProductById(@Param("productId") Long productId);

    @Query("select p.category from Product p where p.id = :productId")
    Optional<Category> getProductCategoryByProductId(@Param("productId") Long productId);

    @Query("select p.deliveryMethods from Product p where p.id = :productId")
    Optional<List<DeliveryMethod>> getProductDeliveryMethodsByProductId(@Param("productId") Long productId);

    @Query("select m from ProductMedia m where m.product.id = :productId")
    Optional<List<ProductMedia>> getProductMediaByProductId(@Param("productId") Long productId);

    @Query("select c from ProductCharacteristic c where c.product.id = :productId")
    Optional<List<ProductCharacteristic>> getProductCharacteristicsByProductId(@Param("productId") Long productId);

    @Query("select faq from ProductFaq faq where faq.product.id = :productId")
    Optional<List<ProductFaq>> getProductFaqByProductId(@Param("productId") Long productId);
}
