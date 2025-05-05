package com.surofu.madeinrussia.application.query.product;

import java.math.BigDecimal;
import java.util.List;

public record GetProductsQuery(
        int page,
        int size,
        List<Long> deliveryMethodIds,
        List<Long> categoryIds,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
