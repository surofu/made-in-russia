package com.surofu.madeinrussia.infrastructure.persistence.product.media;

import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductMediaRepository extends JpaRepository<ProductMedia, Long> {
    List<ProductMediaView> findAllByProduct_IdOrderByPositionAsc(Long productId);
}
