package com.surofu.madeinrussia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Schema(
        name = "ProductPage",
        description = "Represents a page of products"
)
public final class GetProductPageDto extends PageImpl<ProductDto> implements Serializable {
    public GetProductPageDto(List<ProductDto> content, Pageable pageable, Long total) {
        super(content, pageable, total);
    }
    public GetProductPageDto(List<ProductDto> content) {
        super(content);
    }
}
