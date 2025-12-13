package com.surofu.exporteru.core.model.media;

public enum MediaType {
    IMAGE,
    VIDEO;

    public String getName() {
        return name().toLowerCase();
    }
}
