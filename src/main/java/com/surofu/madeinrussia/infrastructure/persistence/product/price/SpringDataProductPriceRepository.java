package com.surofu.madeinrussia.infrastructure.persistence.product.price;

import com.surofu.madeinrussia.core.model.product.price.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    List<ProductPriceView> findAllByProduct_Id(Long productId);
}
