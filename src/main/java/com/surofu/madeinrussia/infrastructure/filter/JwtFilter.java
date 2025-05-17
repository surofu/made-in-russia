package com.surofu.madeinrussia.infrastructure.filter;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.service.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final AsyncSessionApplicationService asyncSessionApplicationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("JWT Filter started");

        String authorizationHeader = request.getHeader("Authorization");

        for (String headerName : Collections.list(request.getHeaderNames())) {
            log.info("header: {}: {}", headerName, request.getHeader(headerName));
        }

        log.info("authorizationHeader: {}", authorizationHeader);

        UserEmail userEmail = null;
        String accessToken = null;

        log.info("Start get access token");
        log.info("Get access token if param 1 is true: {}", authorizationHeader != null);

        if (authorizationHeader != null) {
            log.info("Get access token if param 2 is true: {}", authorizationHeader.startsWith("Bearer "));
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);

            log.info("access token: {}", accessToken);

            try {
                userEmail = jwtUtils.extractUserEmailFromAccessToken(accessToken);
                log.info("Email found: {}", userEmail);
            } catch (ExpiredJwtException ex) {
                log.warn("Jwt has been expired", ex);
            } catch (SignatureException ex) {
                log.warn("Jwt bad signature", ex);
            } catch (Exception ex) {
                log.warn("Jwt could not be parsed", ex);
            }
        }

        log.info("End get access token with email: {}", userEmail);

        SessionInfo sessionInfo = SessionInfo.of(request);

        log.info("Current session user agent: {}", sessionInfo.getUserAgent().toString());
        log.info("Current session ip address: {}", sessionInfo.getIpAddress());
        log.info("Current session device id: {}", sessionInfo.getDeviceId());

        log.info("if 1 param is true: {}", userEmail != null);
        log.info("if 2 param is true: {}", SecurityContextHolder.getContext().getAuthentication() == null);
        log.info("1 if statement is true: {}", userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserRole userRole = jwtUtils.extractUserRoleFromAccessToken(accessToken);
            log.info("Role: {}", userRole);
            List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(userRole.toString()));
            log.info("Authority list: {}", authorityList);

            try {
                log.info("Start try get SecurityUser");
                SecurityUser securityUser = (SecurityUser) userService.loadUserByUsername(userEmail.toString());
                log.info("End try get SecurityUser");
                log.info("SecurityUser user email: {}", securityUser.getUser().getEmail().toString());

                log.info("Start creating token");
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        securityUser,
                        null,
                        authorityList
                );
                log.info("End creating token");

                SecurityContextHolder.getContext().setAuthentication(token);
                log.info("End saving to context");

                // Update Access Token
                accessToken = jwtUtils.generateAccessToken(securityUser);
                response.setHeader("Authorization", "Bearer " + accessToken);

                log.info("Start saveOrUpdateSessionFromHttpRequest");
                asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser)
                        .exceptionally(ex -> {
                            log.error("Error while saving session", ex);
                            return null;
                        });
            } catch (UsernameNotFoundException ex) {
                log.debug("User with email '{}' not found", userEmail, ex);
            }
        }

        log.info("JWT Filter finished");

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        String[] whiteList = {
                "/api/v1/auth",
                "/api/v1/products",
                "/api/v1/categories",
                "/api/v1/delivery-methods",
                "/api/v1/me/current-session/refresh",
        };

        boolean shouldNotFilter = false;

        for (String pathElement : whiteList) {
            if (path.startsWith(pathElement)) {
                shouldNotFilter = true;
                break;
            }
        }

        return shouldNotFilter;
    }
}
