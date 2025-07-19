package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.okved.OkvedCategory;

import java.util.List;

public interface OkvedCategoryRepository {
    List<OkvedCategory> getById(Long categoryId);
}
