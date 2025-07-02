package com.surofu.madeinrussia.infrastructure.persistence.view;

public interface SearchHintView {
    Long getProductId();

    String getProductTitle();

    String getProductImage();

    Long getCategoryId();

    String getCategoryName();

    String getCategoryImage();
}
