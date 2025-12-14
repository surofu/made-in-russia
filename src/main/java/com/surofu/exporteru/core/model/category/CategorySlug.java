package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategorySlug implements Serializable {
  @Column(name = "slug", unique = true, nullable = false)
  private String value;

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

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CategorySlug that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
