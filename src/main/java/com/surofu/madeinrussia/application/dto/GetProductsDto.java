package com.surofu.madeinrussia.application.dto;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class GetProductsDto extends PageImpl<ProductDto> {
    public GetProductsDto(List<ProductDto> content, Pageable pageable, Long total) {
        super(content, pageable, total);
    }

    public GetProductsDto(List<ProductDto> content) {
        super(content);
    }
}
