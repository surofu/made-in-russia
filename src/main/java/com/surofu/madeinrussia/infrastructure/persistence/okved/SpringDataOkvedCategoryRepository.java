package com.surofu.madeinrussia.infrastructure.persistence.okved;

import com.surofu.madeinrussia.core.model.okved.OkvedCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataOkvedCategoryRepository extends JpaRepository<OkvedCategory, Long> {
    List<OkvedCategory> findByCategory_Id(Long categoryId);
}
