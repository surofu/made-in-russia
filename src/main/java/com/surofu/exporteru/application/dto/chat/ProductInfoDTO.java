package com.surofu.exporteru.application.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO с краткой информацией о товаре для чата
 */
@Getter
@Setter
@Builder
public class ProductInfoDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
}