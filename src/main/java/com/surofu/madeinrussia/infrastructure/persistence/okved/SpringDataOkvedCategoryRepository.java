package com.surofu.madeinrussia.infrastructure.persistence.okved;

import com.surofu.madeinrussia.core.model.okved.OkvedCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataOkvedCategoryRepository extends JpaRepository<OkvedCategory, Long> {
    List<OkvedCategory> findByCategoryId(Long categoryId);

    @Query(value = """
            select
            c.category_id as categoryId,
            c.okved_id as okvedId
            from categories_okved c
            """, nativeQuery = true)
    List<OkvedCategoryView> findAllViews();
}
