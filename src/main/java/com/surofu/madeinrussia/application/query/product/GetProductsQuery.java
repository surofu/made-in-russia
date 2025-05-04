package com.surofu.madeinrussia.application.query.product;

import java.math.BigDecimal;

public record GetProductsQuery(
        int page,
        int size,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
