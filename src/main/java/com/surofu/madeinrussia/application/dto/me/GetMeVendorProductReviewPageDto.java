package com.surofu.madeinrussia.application.dto.me;

import com.surofu.madeinrussia.application.dto.product.ProductReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Schema(
        name = "MeVendorProductReviewPage",
        description = "Represents a page of all reviews in my products (if i am vendor)"
)
public class GetMeVendorProductReviewPageDto extends PageImpl<ProductReviewDto> implements Serializable {
    public GetMeVendorProductReviewPageDto(List<ProductReviewDto> content, Pageable pageable, Long total) {
        super(content, pageable, total);
    }
    public GetMeVendorProductReviewPageDto(List<ProductReviewDto> content) {
        super(content);
    }
}
