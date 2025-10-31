package com.surofu.exporteru.core.service.category;

import com.surofu.exporteru.core.service.category.operation.*;

public interface CategoryService {
    GetAllCategories.Result getAllCategories(GetAllCategories operation);

    GetCategories.Result getCategories(GetCategories operation);

    GetCategoryById.Result getCategoryById(GetCategoryById operation);

    GetCategoryBySlug.Result getCategoryBySlug(GetCategoryBySlug operation);

    CreateCategory.Result createCategory(CreateCategory operation);

    UpdateCategoryById.Result updateCategoryById(UpdateCategoryById operation);

    DeleteCategoryById.Result deleteCategoryById(DeleteCategoryById operation);
}
