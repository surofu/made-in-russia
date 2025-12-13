package com.surofu.exporteru.application.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Schema(name = "ProductSummaryPage")
public final class GetProductSummaryViewPageDto extends PageImpl<ProductSummaryViewDto> implements Serializable {
    public GetProductSummaryViewPageDto(List<ProductSummaryViewDto> content, Pageable pageable, Long total) {
        super(content, pageable, total);
    }
    public GetProductSummaryViewPageDto(List<ProductSummaryViewDto> content) {
        super(content);
    }
}
