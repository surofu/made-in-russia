package com.surofu.madeinrussia.application.command.product;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record CreateProductPriceCommand(
    Integer quantityFrom,
    Integer quantityTo,
    String currency,
    String unit,
    BigDecimal price,
    BigDecimal discount,
    Integer minimumOrderQuantity,
    ZonedDateTime discountExpirationDate
) {
}
