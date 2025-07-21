package com.surofu.madeinrussia.application.dto.page;

import com.surofu.madeinrussia.application.dto.product.ProductReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Schema(
        name = "ProductReviewPage",
        description = "Represents a page of product reviews"
)
public final class GetProductReviewPageDto extends PageImpl<ProductReviewDto> implements Serializable {
    public GetProductReviewPageDto(List<ProductReviewDto> content, Pageable pageable, Long total) {
        super(content, pageable, total);
    }
    public GetProductReviewPageDto(List<ProductReviewDto> content) {
        super(content);
    }
}
