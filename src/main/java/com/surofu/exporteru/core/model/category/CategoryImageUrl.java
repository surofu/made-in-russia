package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;

@Getter
@Embeddable
public final class CategoryImageUrl implements Serializable {

  @Column(name = "image_url")
  private final String value;

  public CategoryImageUrl(String url) {
    if (url != null && url.length() > 20_000) {
      throw new LocalizedValidationException("validation.category.image_url.max_length");
    }

    this.value = url;
  }

  public CategoryImageUrl() {
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
    if (!(o instanceof CategoryImageUrl categoryImageUrl)) {
      return false;
    }
    return Objects.equals(value, categoryImageUrl.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
