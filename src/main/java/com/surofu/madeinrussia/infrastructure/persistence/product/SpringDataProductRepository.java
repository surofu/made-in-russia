package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productCharacteristic.ProductCharacteristic;
import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            select p from Product p
            join fetch p.category
            where p.id = :productId
            """)
    Optional<Product> getProductById(@Param("productId") Long productId);

    @Query("select p.category from Product p where p.id = :productId")
    Optional<Category> getProductCategoryByProductId(@Param("productId") Long productId);

    @Query("select p.deliveryMethods from Product p where p.id = :productId")
    List<DeliveryMethod> getProductDeliveryMethodsByProductId(@Param("productId") Long productId);

    @Query("select m from ProductMedia m where m.product.id = :productId")
    Optional<List<ProductMedia>> getProductMediaByProductId(@Param("productId") Long productId);

    @Query("select c from ProductCharacteristic c where c.product.id = :productId")
    Optional<List<ProductCharacteristic>> getProductCharacteristicsByProductId(@Param("productId") Long productId);

    @Query("select faq from ProductFaq faq where faq.product.id = :productId")
    Optional<List<ProductFaq>> getProductFaqByProductId(@Param("productId") Long productId);

    @Query("select p.user from Product p where p.id = :productId")
    Optional<User> getProductUserByProductId(@Param("productId") Long productId);

    @Query(value = """
            SELECT input.id 
            FROM unnest(?1) WITH ORDINALITY AS input(id, ord)
            LEFT JOIN products p ON p.id = input.id
            WHERE p.id IS NULL
            ORDER BY input.ord
            LIMIT 1
            """, nativeQuery = true)
    Optional<Long> firstNotExists(Long[] productIdsArray);

    @Query(value = """
            SELECT
                                                 CASE
                                                     WHEN COUNT(r.rating) = 0 THEN NULL
                                                     ELSE CAST(ROUND(
                                                         CASE
                                                             WHEN AVG(r.rating) < 1.0 THEN 1.0
                                                             WHEN AVG(r.rating) > 5.0 THEN 5.0
                                                             ELSE AVG(r.rating)
                                                         END, 1) AS DOUBLE PRECISION)
                                                 END
                                             FROM product_reviews r
                                             WHERE r.product_id = id
            """, nativeQuery = true)
    Optional<Double> getProductRatingById(@Param("productId") Long productId);
}
