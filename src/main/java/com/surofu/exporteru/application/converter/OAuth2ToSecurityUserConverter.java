package com.surofu.exporteru.application.converter;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2ToSecurityUserConverter {

    private final UserService userService;

    public SecurityUser convert(OAuth2User oauth2User) throws UsernameNotFoundException {
        String email = oauth2User.getAttribute("email");
        return (SecurityUser) userService.loadUserByUsername(email);
    }
}
