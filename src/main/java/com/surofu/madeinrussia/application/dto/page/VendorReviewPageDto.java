package com.surofu.madeinrussia.application.dto.page;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.surofu.madeinrussia.application.dto.ProductReviewDto;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;

@Getter
@Setter
public final class VendorReviewPageDto implements Serializable {

    @JsonUnwrapped
    private PageImpl<ProductReviewDto> page;

    private double averageRating = 0;

    public static VendorReviewPageDto of(Page<ProductReview> page, double averageRating) {
        VendorReviewPageDto dto = new VendorReviewPageDto();
        dto.setPage(new PageImpl<>(
                page.getContent().stream().map(ProductReviewDto::of).toList(),
                page.getPageable(),
                page.getTotalElements()
        ));
        dto.setAverageRating(averageRating);
        return dto;
    }
}