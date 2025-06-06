package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.view.ProductSummaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataProductSummaryViewRepository extends JpaRepository<ProductSummaryView, Long> {

    Page<ProductSummaryView> findAll(Specification<ProductSummaryView> specification, Pageable pageable);
}
