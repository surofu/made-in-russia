package com.surofu.exporteru.application.command.product.review;

public record CreateProductReviewCommand(
    String text,
    Integer rating
) {
}
