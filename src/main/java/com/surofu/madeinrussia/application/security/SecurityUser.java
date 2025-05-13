package com.surofu.madeinrussia.application.security;

import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
public class SecurityUser implements UserDetails {
    private User user;
    private UserPassword userPassword;
    private Optional<Session> session;

    @Override
    public String getUsername() {
        return user.getEmail().getEmail();
    }

    @Override
    public String getPassword() {
        return userPassword.getPassword().getPassword();
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
