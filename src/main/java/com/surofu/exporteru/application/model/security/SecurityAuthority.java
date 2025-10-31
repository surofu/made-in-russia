package com.surofu.exporteru.application.model.security;

import com.surofu.exporteru.core.model.user.UserRole;
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
