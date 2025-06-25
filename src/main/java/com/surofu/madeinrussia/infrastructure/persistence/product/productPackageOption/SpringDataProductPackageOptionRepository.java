package com.surofu.madeinrussia.infrastructure.persistence.product.productPackageOption;

import com.surofu.madeinrussia.core.model.product.productPackageOption.ProductPackageOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductPackageOptionRepository extends JpaRepository<ProductPackageOption, Long> {
    List<ProductPackageOption> findAllByProduct_Id(Long productId);
}
