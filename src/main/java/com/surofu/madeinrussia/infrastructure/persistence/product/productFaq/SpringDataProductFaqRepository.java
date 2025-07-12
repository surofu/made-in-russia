package com.surofu.madeinrussia.infrastructure.persistence.product.productFaq;

import com.surofu.madeinrussia.core.model.product.productFaq.ProductFaq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductFaqRepository extends JpaRepository<ProductFaq, Long> {
    List<ProductFaqView> findAllByProduct_Id(Long productId);
}
