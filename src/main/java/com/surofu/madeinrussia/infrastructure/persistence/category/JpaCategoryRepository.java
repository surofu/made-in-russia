package com.surofu.madeinrussia.infrastructure.persistence.category;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link CategoryRepository} interface.
 * Provides database access operations for {@link Category} entities using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class JpaCategoryRepository implements CategoryRepository {

    private final SpringDataCategoryRepository repository;

    /**
     * Retrieves all categories from the database.
     *
     * @return A list of all categories ordered by their natural ordering
     * @throws org.springframework.dao.DataAccessException if there is a database access problem
     * @apiNote Consider implementing pagination for large category sets
     * @see Category
     */
    @Override
    public List<Category> getCategories() {
        return repository.findAll();
    }

    /**
     * Finds a category by its unique identifier.
     *
     * @param id The category ID to search for (must not be {@code null})
     * @return An {@link Optional} containing the found category, or empty if not found
     * @throws IllegalArgumentException if the id is {@code null}
     * @throws org.springframework.dao.DataAccessException if there is a database access problem
     * @see Category
     */
    @Override
    public Optional<Category> getCategoryById(Long id) {
        return repository.findById(id);
    }
}