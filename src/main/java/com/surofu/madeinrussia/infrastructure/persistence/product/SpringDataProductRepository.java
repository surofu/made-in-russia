package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p")
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAllWithCategoryAndDeliveryMethods(Specification<Product> specification, Pageable pageable);
}
