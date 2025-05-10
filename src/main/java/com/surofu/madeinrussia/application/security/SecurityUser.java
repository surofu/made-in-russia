package com.surofu.madeinrussia.application.security;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class SecurityUser implements UserDetails {
    private User user;
    private UserPassword userPassword;

    @Override
    public String getUsername() {
        return user.getEmail().getEmail();
    }

    @Override
    public String getPassword() {
        return userPassword.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SecurityAuthority(user.getRole()));
    }

    @Override
    public boolean isEnabled() {
        return true;
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
