package com.surofu.madeinrussia.infrastructure.persistence.product;

import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA implementation of {@link ProductRepository} interface.
 * Provides database access operations for {@link Product} entities using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository repository;

    /**
     * Retrieves paginated products matching the given specification.
     *
     * @param specification The criteria to filter products (can be {@code null})
     * @param pageable The pagination information (page number, size, sorting)
     * @return A {@link Page} of products matching the criteria
     * @throws org.springframework.dao.DataAccessException if there is a database access problem
     * @apiNote Uses Spring Data JPA's {@link Specification} for dynamic query construction
     */
    @Override
    public Page<Product> findAll(Specification<Product> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param id The product ID to search for (must not be {@code null})
     * @return An {@link Optional} containing the found product, or empty if not found
     * @throws IllegalArgumentException if the id is {@code null}
     * @throws org.springframework.dao.DataAccessException if there is a database access problem
     */
    @Override
    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }
}