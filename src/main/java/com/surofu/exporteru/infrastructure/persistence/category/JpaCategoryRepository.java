package com.surofu.exporteru.infrastructure.persistence.category;

import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.category.CategorySlug;
import com.surofu.exporteru.core.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCategoryRepository implements CategoryRepository {

  private final SpringDataCategoryRepository repository;

  @Override
  public List<Category> getAll() {
    return repository.findAllBy();
  }

  @Override
  public List<Category> getCategoryL1AndL2() {
    return repository.findAllL1AndL2();
  }

  @Override
  public Optional<Category> getBySlug(CategorySlug slug) {
    return repository.findBySlug_Value(slug.getValue());
  }

  @Override
  public Optional<Category> getById(Long id) {
    return repository.findById(id);
  }

  @Override
  public List<Long> getCategoriesIdsByIds(List<Long> ids) {
    return repository.findAllIdsByIdWithAllChildren(ids);
  }

  @Override
  public void save(Category category) {
    repository.save(category);
  }

  @Override
  public Optional<Category> getCategoryBySlugWithChildren(CategorySlug slug) {
    return repository.findBySlugBy(slug);
  }

  @Override
  public Boolean existsBySlug(CategorySlug slug) {
    return repository.existsBySlug(slug);
  }

  @Override
  public Boolean existsById(Long categoryId) {
    return repository.existsById(categoryId);
  }

  @Override
  public void delete(Category category) {
    repository.delete(category);
  }

  // View

  @Override
  public List<CategoryView> getCategoryViewWithChildrenBySlugAndLang(String slug, String lang) {
    return repository.findCategoryWithChildrenViewBySlugAndLang(slug, lang);
  }

  @Override
  public List<CategoryWithProductsCountView> getCategoriesWithProductsCount() {
    return repository.findCategoriesWithProductsCount();
  }
}