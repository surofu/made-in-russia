package com.surofu.madeinrussia.core.model.user;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;

    public String getName() {
        return name().split("_")[1];
    }
}
