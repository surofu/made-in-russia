package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;

@Getter
@Embeddable
public final class CategorySlug implements Serializable {

  @Column(name = "slug", unique = true, nullable = false)
  private final String value;

  public CategorySlug(String slug) {
    if (slug == null || slug.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.category.slug.empty");
    }

    if (slug.length() > 255) {
      throw new LocalizedValidationException("validation.category.slug.max_length");
    }

    this.value = slug;
  }

  public CategorySlug(String slug, int level) {
    this("l%d_%s".formatted(level, slug));
  }

  public CategorySlug() {
    this.value = null;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CategorySlug categorySlug)) {
      return false;
    }
    return Objects.equals(value, categorySlug.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
