package com.surofu.madeinrussia.infrastructure.persistence.product;

public interface SearchHintView {
    Long getProductId();

    String getProductTitle();

    String getProductImage();

    Long getCategoryId();

    String getCategoryName();

    String getCategoryImage();
}
