package com.surofu.madeinrussia.infrastructure.persistence.product.productPrice;

import com.surofu.madeinrussia.core.model.product.productPrice.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    List<ProductPriceView> findAllByProduct_Id(Long productId);
}
