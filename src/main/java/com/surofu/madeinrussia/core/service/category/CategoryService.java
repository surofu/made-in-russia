package com.surofu.madeinrussia.core.service.category;

import com.surofu.madeinrussia.core.service.category.operation.GetAllCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryBySlug;

public interface CategoryService {
    GetAllCategories.Result getAllCategories(GetAllCategories operation);

    GetCategories.Result getCategories(GetCategories operation);

    GetCategoryById.Result getCategoryById(GetCategoryById operation);

    GetCategoryBySlug.Result getCategoryBySlug(GetCategoryBySlug operation);
}
