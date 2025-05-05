package com.surofu.madeinrussia.core.service.category;

import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;

public interface CategoryService {
    GetCategories.Result getCategories();
    GetCategoryById.Result getCategoryById(GetCategoryById operation);
}
