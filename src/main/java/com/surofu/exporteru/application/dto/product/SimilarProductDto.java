package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.infrastructure.persistence.product.SimilarProductView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SimilarProduct")
public final class SimilarProductDto implements Serializable {
  private Long id;
  private String title;
  private String imageUrl;

  public static SimilarProductDto of(Product product) {
    return SimilarProductDto.builder()
        .id(product.getId())
        .title(product.getTitle().getLocalizedValue())
        .imageUrl(product.getPreviewImageUrl().getValue())
        .build();
  }

  public static SimilarProductDto of(SimilarProductView view) {
    return SimilarProductDto.builder()
        .id(view.getId())
        .title(view.getTitle())
        .imageUrl(view.getPreviewImageUrl())
        .build();
  }
}
