package com.surofu.madeinrussia.core.model.media;

public enum MediaType {
    IMAGE,
    VIDEO;

    public String getName() {
        return name().toLowerCase();
    }
}
