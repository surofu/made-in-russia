package com.surofu.madeinrussia.core.model.user;

public enum UserRole {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_VENDOR;

    public String getName() {
        String name = name().split("_")[1].toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
