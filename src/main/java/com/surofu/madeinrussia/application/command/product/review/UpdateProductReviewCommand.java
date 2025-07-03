package com.surofu.madeinrussia.application.command.product.review;

public record UpdateProductReviewCommand(
    String text,
    Integer rating
) {
}
