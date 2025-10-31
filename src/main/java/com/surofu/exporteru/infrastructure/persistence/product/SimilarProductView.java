package com.surofu.exporteru.infrastructure.persistence.product;

public interface SimilarProductView {
    Long getId();

    String getTitle();

    String getPreviewImageUrl();

    Integer getMinimumOrderQuantity();
}
