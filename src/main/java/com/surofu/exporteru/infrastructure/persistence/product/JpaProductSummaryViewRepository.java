package com.surofu.exporteru.infrastructure.persistence.product;

import com.surofu.exporteru.core.repository.ProductSummaryViewRepository;
import com.surofu.exporteru.core.repository.specification.ProductSummarySpecifications;
import com.surofu.exporteru.core.view.ProductSummaryView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JpaProductSummaryViewRepository implements ProductSummaryViewRepository {
    private final SpringDataProductSummaryViewRepository repository;

    @Override
    public Page<ProductSummaryView> getProductSummaryViewPage(Specification<ProductSummaryView> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Override
    public List<ProductSummaryView> getProductSummaryViewByIds(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Optional<ProductSummaryView> getProductSummaryViewById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ProductSummaryView> getProductSummaryViewByInUserFavoritesWithUserId(Long userId) {
        Specification<ProductSummaryView> specification = ProductSummarySpecifications.inUserFavorite(userId);
        Page<ProductSummaryView> page = repository.findAll(specification, Pageable.unpaged());
        return page.getContent();
    }
}
