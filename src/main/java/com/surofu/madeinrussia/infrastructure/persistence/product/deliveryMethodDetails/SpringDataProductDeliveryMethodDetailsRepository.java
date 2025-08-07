package com.surofu.madeinrussia.infrastructure.persistence.product.deliveryMethodDetails;

import com.surofu.madeinrussia.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductDeliveryMethodDetailsRepository extends JpaRepository<ProductDeliveryMethodDetails, Long> {

    @Query(value = """
            select
            d.id,
            coalesce(
                d.name_translations -> :lang,
                d.name
            ) as name,
            coalesce(
                d.value_translations -> :lang,
                d.value
            ) as value,
            d.creation_date,
            d.last_modification_date
            from product_delivery_method_details d
            where d.product_id = :productId
            """, nativeQuery = true)
    List<ProductDeliveryMethodDetailsView> findAllViewsByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

    @Query(value = """
            select
            d.id,
            coalesce(
                d.name_translations -> :lang,
                d.name
            ) as name,
            d.name_translations::text,
            coalesce(
                d.value_translations -> :lang,
                d.value
            ) as value,
            d.value_translations::text,
            d.creation_date,
            d.last_modification_date
            from product_delivery_method_details d
            where d.product_id = :productId
            """, nativeQuery = true)
    List<ProductDeliveryMethodDetailsWithTranslationsView> findAllViewsWithTranslationsByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);
}
