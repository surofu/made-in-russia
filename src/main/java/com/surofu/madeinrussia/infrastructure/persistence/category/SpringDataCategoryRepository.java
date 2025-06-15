package com.surofu.madeinrussia.infrastructure.persistence.category;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.category.CategorySlug;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataCategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent")
    List<Category> findAllWithParent();

    @Override
    @Query("""
            select c from Category c
            join fetch c.children
            where c.parent is null
            order by c.id
            """)
    List<Category> findAll();

    @Override
    @Query("select c from Category c where c.id = :id")
    @EntityGraph(attributePaths = "children")
    Optional<Category> findById(@Param("id") Long id);

    @Query("select c from Category c where c.slug = :slug")
    @EntityGraph(attributePaths = "children")
    Optional<Category> findBySlug(@Param("slug") CategorySlug slug);

    @Query(value = """
            with recursive category_tree as (
                select c.id from categories c where c.id in (:ids)
                    union all
                    select child.id from categories child
                    join category_tree parent on parent.id = child.parent_category_id
            )
            select * from category_tree
            """, nativeQuery = true)
    List<Long> findAllIdsByIdWithAllChildren(@Param("ids") Iterable<Long> ids);
}
