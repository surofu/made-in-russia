package com.surofu.madeinrussia.infrastructure.persistence.product.productMedia;

import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductMediaRepository extends JpaRepository<ProductMedia, Long> {
    List<ProductMedia> findAllByProduct_Id(Long productId);
}
