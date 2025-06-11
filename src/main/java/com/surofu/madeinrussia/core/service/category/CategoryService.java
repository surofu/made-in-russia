package com.surofu.madeinrussia.core.service.category;

import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryBySlug;

public interface CategoryService {
    GetCategories.Result getCategories();

    GetCategoryById.Result getCategoryById(GetCategoryById operation);

    GetCategoryBySlug.Result getCategoryBySlug(GetCategoryBySlug operation);
}
