package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.core.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductForReviewDto implements Serializable {
    private String title;

    private String previewImageUrl;

    public static ProductForReviewDto of(Product product) {
        if (product == null) {
            return null;
        }

        return ProductForReviewDto.builder()
                .title(product.getTitle().toString())
                .previewImageUrl(product.getPreviewImageUrl().toString())
                .build();
    }
}
