package com.surofu.madeinrussia.infrastructure.filter;

import com.surofu.madeinrussia.application.utils.JwtUtils;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserService userService;

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

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String role = jwtUtils.extractRoleFromAccessToken(accessToken);
            List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(role));

            try {
                UserDetails userDetails = userService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorityList
                );

                SecurityContextHolder.getContext().setAuthentication(token);

                // Update Access Token
                accessToken = jwtUtils.generateAccessToken(userDetails);
                response.setHeader("Authorization", "Bearer " + accessToken);
            } catch (UsernameNotFoundException ex) {
                log.debug("User with email '{}' not found", email, ex);
            }
        }

        filterChain.doFilter(request, response);
    }
}
