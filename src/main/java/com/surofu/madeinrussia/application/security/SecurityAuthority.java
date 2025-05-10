package com.surofu.madeinrussia.application.security;

import com.surofu.madeinrussia.core.model.userRole.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class SecurityAuthority implements GrantedAuthority {
    private final UserRole role;

    @Override
    public String getAuthority() {
        return role.name();
    }
}
