package com.surofu.madeinrussia.application.dto.product;

import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.infrastructure.persistence.product.SimilarProductView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a product similar to the current one")
public final class SimilarProductDto implements Serializable {

    @Schema(description = "Unique identifier of the similar product",
            example = "12345",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "Title/name of the similar product",
            example = "iPhone 14 Pro",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "URL of the product's preview image",
            example = "https://example.com/images/iphone14pro.jpg",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageUrl;

    @Schema(hidden = true)
    public static SimilarProductDto of(Product product) {
        return SimilarProductDto.builder()
                .id(product.getId())
                .title(product.getTitle().toString())
                .imageUrl(product.getPreviewImageUrl().toString())
                .build();
    }

    @Schema(hidden = true)
    public static SimilarProductDto of(SimilarProductView view) {
        return SimilarProductDto.builder()
                .id(view.getId())
                .title(view.getTitle())
                .imageUrl(view.getPreviewImageUrl())
                .build();
    }
}
