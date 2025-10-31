package com.surofu.exporteru.infrastructure.persistence.product.price;

import com.surofu.exporteru.core.model.product.price.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductPriceRepository extends JpaRepository<ProductPrice, Long> {

    @Query(value = """
            select
            p.id as id,
            p.quantity_from as quantityFrom,
            p.quantity_to as quantityTo,
            p.currency as currency,
            coalesce(
                p.unit_translations -> :lang,
                p.quantity_unit
            ) as unit,
            p.original_price as originalPrice,
            p.discount as discount,
            (p.original_price * (1 - p.discount / 100)) as discountedPrice,
            p.creation_date as creationDate,
            p.last_modification_date as lastModificationDate
            from product_prices p
            where p.product_id = :productId
            """, nativeQuery = true)
    List<ProductPriceView> findAllByProductIdAndLang(@Param("productId") Long productId, @Param("lang") String lang);

    List<ProductPrice> findAllByProductId(Long id);
}
