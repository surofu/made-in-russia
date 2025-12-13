package com.surofu.exporteru.application.command.product.review;

public record UpdateProductReviewCommand(
    String text,
    Integer rating
) {
}
