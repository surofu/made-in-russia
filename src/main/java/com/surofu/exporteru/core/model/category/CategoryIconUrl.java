package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;

@Getter
@Embeddable
public final class CategoryIconUrl implements Serializable {
  @Column(name = "icon_url")
  private final String value;

  public CategoryIconUrl(String url) {
    if (url != null && url.length() > 20_000) {
      throw new LocalizedValidationException("validation.category.image_url.max_length");
    }

    this.value = url;
  }

  public CategoryIconUrl() {
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
      if (!(o instanceof CategoryIconUrl categoryIconUrl)) {
          return false;
      }
    return Objects.equals(value, categoryIconUrl.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
