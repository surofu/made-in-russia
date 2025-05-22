package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.view.ProductSummaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ProductSummaryViewRepository {
    Page<ProductSummaryView> getProductSummaryViewPage(Specification<ProductSummaryView> specification, Pageable pageable);

    Optional<ProductSummaryView> getProductSummaryViewById(Long id);
}
