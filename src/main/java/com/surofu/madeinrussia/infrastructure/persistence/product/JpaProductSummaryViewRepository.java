package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.view.ProductSummaryView;
import com.surofu.madeinrussia.core.repository.ProductSummaryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductSummaryViewRepository implements ProductSummaryViewRepository {
    private final SpringDataProductSummaryViewRepository repository;

    @Override
    public Page<ProductSummaryView> getProductSummaryViewPage(Specification<ProductSummaryView> specification, Pageable pageable) {
        return repository.getProductSummaryViewPage(specification, pageable);
    }

    @Override
    public Optional<ProductSummaryView> getProductSummaryViewById(Long id) {
        return repository.findById(id);
    }
}
