package com.surofu.exporteru.infrastructure.persistence.product;

import com.surofu.exporteru.core.view.ProductSummaryView;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataProductSummaryViewRepository extends JpaRepository<ProductSummaryView, Long>,
        JpaSpecificationExecutor<ProductSummaryView> {

    @NotNull Page<ProductSummaryView> findAll(Specification<ProductSummaryView> specification, @NotNull Pageable pageable);
}
