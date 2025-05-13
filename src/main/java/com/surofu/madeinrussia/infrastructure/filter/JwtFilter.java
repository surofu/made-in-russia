package com.surofu.madeinrussia.infrastructure.filter;

import com.surofu.madeinrussia.application.security.SecurityUser;
import com.surofu.madeinrussia.application.utils.IpAddressUtils;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.application.utils.SessionUtils;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final SessionUtils sessionUtils;
    private final IpAddressUtils ipAddressUtils;
    private final UserService userService;
    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String accessToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);

            try {
                email = jwtUtils.extractEmailFromAccessToken(accessToken);
            } catch (ExpiredJwtException ex) {
                log.warn("Jwt has been expired", ex);
            } catch (SignatureException ex) {
                log.warn("Jwt bad signature", ex);
            } catch (Exception ex) {
                log.warn("Jwt could not be parsed", ex);
            }
        }

        String userAgent = request.getHeader("User-Agent");
        String ipAddress = ipAddressUtils.getClientIpAddressFromHttpRequest(request);

        SessionDeviceId deviceId = sessionUtils.getDeviceId(userAgent, ipAddress);
        Optional<Session> currentSession = sessionRepository.getSessionByDeviceId(deviceId);

        if (currentSession.isPresent() && email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String role = jwtUtils.extractRoleFromAccessToken(accessToken);
            List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(role));

            try {
                SecurityUser securityUser = (SecurityUser) userService.loadUserByUsername(email);
                securityUser.setSession(currentSession);

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        securityUser,
                        null,
                        authorityList
                );

                SecurityContextHolder.getContext().setAuthentication(token);

                // Update Access Token
                accessToken = jwtUtils.generateAccessToken(securityUser);
                response.setHeader("Authorization", "Bearer " + accessToken);
            } catch (UsernameNotFoundException ex) {
                log.debug("User with email '{}' not found", email, ex);
            }
        }

        filterChain.doFilter(request, response);
    }
}
