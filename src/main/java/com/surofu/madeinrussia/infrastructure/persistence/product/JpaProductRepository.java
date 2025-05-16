package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository repository;

    @Override
    public Page<Product> getAllProductsWithCategoryAndDeliveryMethods(Specification<Product> specification, Pageable pageable) {
        return repository.findAllWithCategoryAndDeliveryMethods(specification, pageable);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }
}