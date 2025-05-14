package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ProductRepository {
    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    Optional<Product> findById(Long id);
}
