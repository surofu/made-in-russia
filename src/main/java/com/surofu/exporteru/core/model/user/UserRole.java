package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.exception.InvalidRoleException;

public enum UserRole {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_VENDOR;

    public String getName() {
        String name = name().split("_")[1].toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static UserRole of(String role) throws InvalidRoleException {
        for (UserRole userRole : values()) {
            if (userRole.name().equalsIgnoreCase(role) || userRole.name().equalsIgnoreCase("ROLE_" + role)) {
                return userRole;
            }
        }

        throw new InvalidRoleException(role);
    }
}
