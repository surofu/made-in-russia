package com.surofu.exporteru.application.dto.vendor;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.surofu.exporteru.application.dto.product.ProductReviewDto;
import com.surofu.exporteru.core.model.product.review.ProductReview;
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

    private Double averageRating = 0.0;

    public static VendorReviewPageDto of(Page<ProductReview> page, Double averageRating) {
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