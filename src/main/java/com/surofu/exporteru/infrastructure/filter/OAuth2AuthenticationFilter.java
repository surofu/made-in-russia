package com.surofu.exporteru.infrastructure.filter;

import com.surofu.exporteru.application.converter.OAuth2ToSecurityUserConverter;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.utils.JwtUtils;
import com.surofu.exporteru.core.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final OAuth2ToSecurityUserConverter oauth2UserConverter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();

            try {
                SecurityUser securityUser = oauth2UserConverter.convert(oauth2User);

                if (securityUser != null) {
                    UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                            securityUser,
                            authentication.getCredentials(),
                            authentication.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(newAuth);

                    String accessToken = jwtUtils.generateAccessToken(securityUser);
                    response.setHeader("Authorization", "Bearer " + accessToken);

                    log.debug("OAuth2 user converted to SecurityUser: {}", securityUser.getUsername());
                }
            } catch (Exception e) {
                log.error("Error converting OAuth2 user to SecurityUser: {}", e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/");
    }
}
