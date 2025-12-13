package com.surofu.exporteru.application.model.security;

import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.password.UserPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class SecurityUser implements UserDetails {
    private User user;
    private UserPassword userPassword;
    private SessionInfo sessionInfo;

    @Override
    public String getUsername() {
        return user.getEmail().getValue();
    }

    @Override
    public String getPassword() {
        return userPassword.getPassword().getValue();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SecurityAuthority(user.getRole()));
    }

    @Override
    public boolean isEnabled() {
        return user.getIsEnabled().getValue();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}
