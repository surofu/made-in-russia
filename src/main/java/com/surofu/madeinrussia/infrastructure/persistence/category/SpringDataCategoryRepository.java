package com.surofu.madeinrussia.infrastructure.persistence.category;

import com.surofu.madeinrussia.core.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataCategoryRepository extends JpaRepository<Category, Long> {

    @Override
    @Query("select c from Category c join fetch c.children order by c.id")
    List<Category> findAll();

    @Override
    @Query("select c from Category c join fetch c.children where c.id = :id")
    Optional<Category> findById(@Param("id") Long id);
}
