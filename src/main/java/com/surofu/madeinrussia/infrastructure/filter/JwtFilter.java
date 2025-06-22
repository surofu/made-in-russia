package com.surofu.madeinrussia.infrastructure.filter;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import com.surofu.madeinrussia.core.service.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final AsyncSessionApplicationService asyncSessionApplicationService;
    private final SessionRepository sessionRepository;

    @Value("${app.session.secret}")
    private String sessionSecret;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("JWT Filter started");

        String authorizationHeader = request.getHeader("Authorization");
        String xInternalRequestHeader = request.getHeader("X-Internal-Request");

        for (String headerName : Collections.list(request.getHeaderNames())) {
            log.debug("header: {}: {}", headerName, request.getHeader(headerName));
        }

        log.debug("authorizationHeader: {}", authorizationHeader);

        UserEmail userEmail = null;
        String accessToken = null;

        log.debug("Start get access token");
        log.debug("Get access token if param 1 is true: {}", authorizationHeader != null);

        if (authorizationHeader != null) {
            log.debug("Get access token if param 2 is true: {}", authorizationHeader.startsWith("Bearer "));
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);

            log.debug("access token: {}", accessToken);

            try {
                userEmail = jwtUtils.extractUserEmailFromAccessToken(accessToken);
                log.debug("Email found: {}", userEmail);
            } catch (ExpiredJwtException ex) {
                log.warn("Jwt has been expired", ex);
            } catch (SignatureException ex) {
                log.warn("Jwt bad signature", ex);
            } catch (Exception ex) {
                log.warn("Jwt could not be parsed", ex);
            }
        }

        log.debug("End get access token with email: {}", userEmail);

        SessionInfo sessionInfo = SessionInfo.of(request);

        log.debug("Current session user agent: {}", sessionInfo.getUserAgent().toString());
        log.debug("Current session ip address: {}", sessionInfo.getIpAddress());
        log.debug("Current session device id: {}", sessionInfo.getDeviceId());

        log.debug("if 1 param is true: {}", userEmail != null);
        log.debug("if 2 param is true: {}", SecurityContextHolder.getContext().getAuthentication() == null);
        log.debug("1 if statement is true: {}", userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserRole userRole = jwtUtils.extractUserRoleFromAccessToken(accessToken);
            log.debug("Role: {}", userRole);
            List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(userRole.toString()));
            log.debug("Authority list: {}", authorityList);

            try {
                log.debug("Start try get SecurityUser");
                SecurityUser securityUser = (SecurityUser) userService.loadUserByUsername(userEmail.toString());
                log.debug("End try get SecurityUser");
                log.debug("SecurityUser user email: {}", securityUser.getUser().getEmail().toString());

                Optional<Session> currentSession = sessionRepository.getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionInfo.getDeviceId());

                if (currentSession.isPresent() || sessionSecret.equals(xInternalRequestHeader)) {
                    log.debug("Start creating token");
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            securityUser,
                            null,
                            authorityList
                    );
                    log.debug("End creating token");

                    SecurityContextHolder.getContext().setAuthentication(token);
                    log.debug("End saving to context");

                    // Update Access Token
                    accessToken = jwtUtils.generateAccessToken(securityUser);
                    response.setHeader("Authorization", "Bearer " + accessToken);

                    log.debug("Start saveOrUpdateSessionFromHttpRequest");
                    asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser)
                            .exceptionally(ex -> {
                                log.error("Error while saving session", ex);
                                return null;
                            });
                } else {
                    log.warn("""
                            Session not found by:
                            user id: {}
                            device id: {}
                            ip address: {}
                            browser: {}
                            """, securityUser.getUser().getId(), sessionInfo.getDeviceId().toString(), sessionInfo.getIpAddress(), sessionInfo.getUserAgent().getBrowser().getName());
                }
            } catch (UsernameNotFoundException ex) {
                log.warn("User with email '{}' not found", userEmail, ex);
            }
        }

        log.debug("JWT Filter finished");

        filterChain.doFilter(request, response);
    }
}
