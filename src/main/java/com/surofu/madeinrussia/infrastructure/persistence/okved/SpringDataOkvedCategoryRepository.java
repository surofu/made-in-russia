package com.surofu.madeinrussia.infrastructure.persistence.okved;

import com.surofu.madeinrussia.core.model.okved.OkvedCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataOkvedCategoryRepository extends JpaRepository<OkvedCategory, Long> {
    List<OkvedCategory> findByCategory_Id(Long categoryId);

    @Query("""
    select c from OkvedCategory c
    where c.category.id in (:ids)
    """)
    List<OkvedCategory> findByCategory_Ids(@Param("ids") List<Long> ids);
}
