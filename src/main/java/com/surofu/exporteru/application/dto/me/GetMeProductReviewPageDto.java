package com.surofu.exporteru.application.dto.me;

import com.surofu.exporteru.application.dto.product.ProductReviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Schema(
        name = "MeProductReviewPage",
        description = "Represents a page of my product reviews"
)
public class GetMeProductReviewPageDto extends PageImpl<ProductReviewDto> implements Serializable {
    public GetMeProductReviewPageDto(List<ProductReviewDto> content, Pageable pageable, Long total) {
        super(content, pageable, total);
    }
    public GetMeProductReviewPageDto(List<ProductReviewDto> content) {
        super(content);
    }
}
