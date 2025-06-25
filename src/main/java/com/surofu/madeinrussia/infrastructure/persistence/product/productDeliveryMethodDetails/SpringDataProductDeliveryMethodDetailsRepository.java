package com.surofu.madeinrussia.infrastructure.persistence.product.productDeliveryMethodDetails;

import com.surofu.madeinrussia.core.model.product.productDeliveryMethodDetails.ProductDeliveryMethodDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductDeliveryMethodDetailsRepository extends JpaRepository<ProductDeliveryMethodDetails, Long> {
    List<ProductDeliveryMethodDetails> findAllByProduct_Id(Long productId);
}
