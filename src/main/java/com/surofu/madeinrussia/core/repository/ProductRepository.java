package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
