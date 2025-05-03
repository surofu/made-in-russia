package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.core.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
                SELECT p FROM Product p
                JOIN FETCH p.deliveryMethod
                ORDER BY p.id
            """)
    Page<Product> findAllWithDeliveryMethodAsDto(Pageable pageable);

    @Query("""
                SELECT p FROM Product p
                JOIN FETCH p.deliveryMethod
                WHERE p.id = :id
            """)
    Optional<Product> findByIdAsDto(Long id);
}
