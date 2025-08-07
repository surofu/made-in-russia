package com.surofu.madeinrussia.application.enums;

public enum FileStorageFolders {
    ADVERTISEMENT_IMAGES("advertisement-images"),
    PRODUCT_IMAGES("product-images"),
    PRODUCT_VIDEOS("product-videos"),
    PRODUCT_REVIEW_IMAGES("product-review-images"),
    PRODUCT_REVIEW_VIDEOS("product-review-videos"),
    VENDOR_IMAGES("vendor-images"),
    VENDOR_VIDEOS("vendor-videos"),
    USERS_AVATARS("users-avatars"),
    CATEGORY_IMAGES("category-images");

    private final String value;

    FileStorageFolders(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }
}
