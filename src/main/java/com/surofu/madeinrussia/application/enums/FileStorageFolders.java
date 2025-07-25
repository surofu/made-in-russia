package com.surofu.madeinrussia.application.enums;

public enum FileStorageFolders {
    PRODUCT_IMAGES("product-images"),
    PRODUCT_VIDEOS("product-videos"),
    VENDOR_IMAGES("vendor-images"),
    VENDOR_VIDEOS("vendor-videos"),
    USERS_AVATARS("users-avatars");

    private final String value;

    FileStorageFolders(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }
}
