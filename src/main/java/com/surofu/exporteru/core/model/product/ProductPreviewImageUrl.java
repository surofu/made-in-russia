package com.surofu.exporteru.core.model.product;

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
public final class ProductPreviewImageUrl implements Serializable {
  @Column(name = "preview_image_url", nullable = false, columnDefinition = "text")
  private String value;

  public ProductPreviewImageUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Ссылка на изображение превью товара не может быть пустым");
    }
    if (url.length() >= 20_000) {
      throw new IllegalArgumentException(
          "Ссылка на изображение превью товара не может быть больше 20,000 символов");
    }
    this.value = url;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductPreviewImageUrl that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
