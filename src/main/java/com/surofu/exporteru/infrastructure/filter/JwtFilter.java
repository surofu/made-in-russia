package com.surofu.exporteru.infrastructure.filter;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.application.utils.JwtUtils;
import com.surofu.exporteru.core.model.session.Session;
import com.surofu.exporteru.core.model.session.SessionDeviceId;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.repository.SessionRepository;
import com.surofu.exporteru.core.service.user.UserService;
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
    private final SessionRepository sessionRepository;

    @Value("${app.session.secret}")
    private String sessionSecret;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String xInternalRequestHeader = request.getHeader("X-Internal-Request");

        UserEmail userEmail = null;
        String accessToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);

            try {
                userEmail = jwtUtils.extractUserEmailFromAccessToken(accessToken);
            } catch (Exception ignored) {
            }
        }
        SessionInfo sessionInfo = SessionInfo.of(request);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserRole userRole = jwtUtils.extractUserRoleFromAccessToken(accessToken);
            List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(userRole.toString()));

            try {
                SecurityUser securityUser = (SecurityUser) userService.loadUserByUsername(userEmail.toString());

                if (securityUser == null || !securityUser.isEnabled()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Optional<Session> currentSession = sessionRepository.getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionInfo.getDeviceId());

                // Todo: Just for testing
                if (true || currentSession.isPresent() || sessionSecret.equals(xInternalRequestHeader)) {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            securityUser,
                            null,
                            authorityList
                    );

                    SecurityContextHolder.getContext().setAuthentication(token);

                    // Update Access Token
                    accessToken = jwtUtils.generateAccessToken(securityUser);
                    response.setHeader("Authorization", "Bearer " + accessToken);

//                    SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
//                    Session oldSession = sessionRepository
//                            .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
//                            .orElse(new Session());
//                    Session session = Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);
//
//                    try {
//                        sessionRepository.save(session);
//                    } catch (Exception ex) {
//                        log.error("Error while saving session: {}", ex.getMessage(), ex);
//                    }
                }
            } catch (UsernameNotFoundException ex) {
                log.warn("User with email '{}' not found", userEmail);
            }
        }

        log.debug("JWT Filter finished");

        filterChain.doFilter(request, response);
    }
}
