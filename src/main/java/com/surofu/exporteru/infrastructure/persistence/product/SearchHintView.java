package com.surofu.exporteru.infrastructure.persistence.product;

public interface SearchHintView {
    Long getProductId();

    String getProductTitle();

    String getProductImage();

    Long getCategoryId();

    String getCategoryName();

    String getCategorySlug();

    String getCategoryImage();
}
