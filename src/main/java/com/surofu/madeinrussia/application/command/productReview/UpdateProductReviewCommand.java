package com.surofu.madeinrussia.application.command.productReview;

public record UpdateProductReviewCommand(
    String text,
    Integer rating
) {
}
