package com.surofu.madeinrussia.application.command.productReview;

public record CreateProductReviewCommand(
    String text,
    Integer rating
) {
}
