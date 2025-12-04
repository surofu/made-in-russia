package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.core.model.product.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductForReview")
public class ProductForReviewDto implements Serializable {
    private String title;
    private String previewImageUrl;

    public static ProductForReviewDto of(Product product) {
        if (product == null) {
            return null;
        }
        return ProductForReviewDto.builder()
                .title(product.getTitle().getLocalizedValue())
                .previewImageUrl(product.getPreviewImageUrl().toString())
                .build();
    }
}
