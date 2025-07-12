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

    @Query(value = """
        SELECT
            c.id,
            COALESCE(
                c.name_translations -> :lang,
                c.name
            ) as name,
            c.slug,
            c.parent_category_id,
            (
                SELECT COUNT(*) FROM categories cc
                WHERE cc.parent_category_id = c.id
            ) as children_count,
            c.image_url,
            c.creation_date,
            c.last_modification_date
        FROM categories c
        ORDER BY c.id
        """, nativeQuery = true)
    List<CategoryView> findAllCategoryViewsByLang(@Param("lang") String lang);

    @Override
    @Query("""
            select c from Category c
            join fetch c.children
            where c.parent is null
            order by c.id
            """)
    List<Category> findAll();

    @Query("""
                select c from Category c
                where c.slug.value like 'l2_%'
                    or c.slug.value like 'l1_%'
                order by c.id
            """)
    List<Category> findAllL1AndL2();

    @Query(value = """
                select
                c.id,
                coalesce(
                    c.name_translations -> :lang,
                    c.name
                ) as name,
                c.slug,
                c.parent_category_id,
                (
                    select count(*) from categories cc
                    where c.id = cc.parent_category_id
                ) as children_count,
                c.image_url,
                c.creation_date,
                c.last_modification_date
                from categories c
                where c.parent_category_id is null
                or c.slug like 'l2_%'
                order by c.id
            """, nativeQuery = true)
    List<CategoryView> findAllViewsL1AndL2ByLang(@Param("lang") String lang);

    @Override
    @Query("select c from Category c where c.id = :id")
    @EntityGraph(attributePaths = "children")
    Optional<Category> findById(@Param("id") Long id);

    @Query(value = """
            WITH RECURSIVE category_tree AS (
                SELECT
                    id,
                    parent_category_id,
                    name_translations,
                    name,
                    slug,
                    image_url,
                    creation_date,
                    last_modification_date
                FROM categories
                WHERE id = :id
            
                UNION ALL
            
                SELECT 
                    c.id,
                    c.parent_category_id,
                    c.name_translations,
                    c.name,
                    c.slug,
                    c.image_url,
                    c.creation_date,
                    c.last_modification_date
                FROM categories c
                JOIN category_tree ct ON c.parent_category_id = ct.id
            )
            SELECT
                ct.id,
                COALESCE(
                    ct.name_translations -> :lang,
                    ct.name
                ) as name,
                ct.slug,
                ct.parent_category_id,
                (
                    SELECT COUNT(*) FROM categories cc
                    WHERE cc.parent_category_id = ct.id
                ) as children_count,
                ct.image_url,
                ct.creation_date,
                ct.last_modification_date
            FROM category_tree ct
            ORDER BY ct.id
            """, nativeQuery = true)
    List<CategoryView> findCategoryWithChildrenViewByIdAndLang(@Param("id") Long id, @Param("lang") String lang);


    @Query(value = """
            WITH RECURSIVE category_tree AS (
                SELECT
                    id,
                    parent_category_id,
                    name_translations,
                    name,
                    slug,
                    image_url,
                    creation_date,
                    last_modification_date
                FROM categories
                WHERE slug = :slug
            
                UNION ALL
            
                SELECT 
                    c.id,
                    c.parent_category_id,
                    c.name_translations,
                    c.name,
                    c.slug,
                    c.image_url,
                    c.creation_date,
                    c.last_modification_date
                FROM categories c
                JOIN category_tree ct ON c.parent_category_id = ct.id
            )
            SELECT
                ct.id,
                COALESCE(
                    ct.name_translations -> :lang,
                    ct.name
                ) as name,
                ct.slug,
                ct.parent_category_id,
                (
                    SELECT COUNT(*) FROM categories cc
                    WHERE cc.parent_category_id = ct.id
                ) as children_count,
                ct.image_url,
                ct.creation_date,
                ct.last_modification_date
            FROM category_tree ct
            ORDER BY ct.id
            """, nativeQuery = true)
    List<CategoryView> findCategoryWithChildrenViewBySlugAndLang(@Param("slug") String slug, @Param("lang") String lang);

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

    @Query(value = """
    select
    c.id,
    c.parent_category_id,
    coalesce(
        c.name_translations -> :lang,
        c.name
    ) as name,
    (
            select count(*)
            from categories cc
            where c.id = cc.parent_category_id
    ) as children_count,
    c.slug,
    c.image_url,
    c.creation_date,
    c.last_modification_date
    from categories c
    where c.id = :id
    """, nativeQuery = true)
    Optional<CategoryView> findViewByIdAndLang(@Param("id") Long id, @Param("lang") String lang);
}
