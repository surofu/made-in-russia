package com.surofu.exporteru.infrastructure.persistence.category;

import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.category.CategorySlug;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataCategoryRepository extends JpaRepository<Category, Long> {

  @Query("select c from Category c join fetch c.children where c.parent is null")
  List<Category> findAllL1AndL2();

  @Override
  @Query("select c from Category c where c.id = :id")
  @EntityGraph(attributePaths = "children")
  @NotNull
  Optional<Category> findById(@Param("id") @NotNull Long id);

  @Query(value = """
      WITH RECURSIVE category_tree AS (
          SELECT
              c.id,
              c.parent_category_id,
              c.name_translations,
              c.name,
              c.label_translations,
              c.label,
              c.title_translations,
              c.title,
              c.description,
              c.description_translations,
              c.meta_description,
              c.meta_description_translations,
              c.slug,
              c.image_url,
              c.icon_url,
              c.creation_date,
              c.last_modification_date
          FROM categories c
          WHERE slug = :slug
      
          UNION ALL
      
          SELECT
              c.id,
              c.parent_category_id,
              c.name_translations,
              c.name,
              c.label_translations,
              c.label,
              c.title_translations,
              c.title,
              c.description,
              c.description_translations,
              c.meta_description,
              c.meta_description_translations,
              c.slug,
              c.image_url,
              c.icon_url,
              c.creation_date,
              c.last_modification_date
          FROM categories c
          JOIN category_tree ct ON c.parent_category_id = ct.id
      )
      SELECT
                        ct.id,
                        COALESCE(
                            ct.name_translations::jsonb ->> :lang,
                            ct.name
                        ) as name,
                        COALESCE(
                            ct.label_translations::jsonb ->> :lang,
                            ct.label
                        ) as label,
                        COALESCE(
                            ct.title_translations::jsonb ->> :lang,
                            ct.title
                        ) as title,
                        COALESCE(
                            ct.description_translations::jsonb ->> :lang,
                            ct.description
                        ) as description,
                        COALESCE(
                            ct.meta_description_translations::jsonb ->> :lang,
                            ct.meta_description
                        ) as metaDescription,
                        ct.slug,
                        ct.parent_category_id,
                        (
                            SELECT COUNT(*) FROM categories cc
                            WHERE cc.parent_category_id = ct.id
                        ) as children_count,
                        ct.image_url,
                        ct.icon_url,
                        ct.creation_date,
                        ct.last_modification_date
                    FROM category_tree ct
                    ORDER BY ct.id
      """, nativeQuery = true)
  List<CategoryView> findCategoryWithChildrenViewBySlugAndLang(@Param("slug") String slug,
                                                               @Param("lang") String lang);

  @Query("""
      select c from Category c
      join fetch c.parent
      join fetch c.children cc
      join fetch cc.children ccc
      join fetch ccc.children
      where c.slug = :slug
      """)
  @EntityGraph(attributePaths = {"children", "children.children", "children.children.children"})
  Optional<Category> findBySlugBy(@Param("slug") CategorySlug slug);

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

  @Query("select count(c) > 0 from Category c where c.slug.value = :#{#slug.value}")
  Boolean existsBySlug(@Param("slug") CategorySlug slug);

  @EntityGraph(attributePaths = {"parent", "children"})
  List<Category> findAllBy();

  Optional<Category> findBySlug_Value(String slugValue);
}
