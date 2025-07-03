package com.surofu.madeinrussia.application.command.product.review;

public record CreateProductReviewCommand(
    String text,
    Integer rating
) {
}
