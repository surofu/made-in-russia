package com.surofu.exporteru.application.components;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.utils.JwtUtils;
import com.surofu.exporteru.core.model.session.Session;
import com.surofu.exporteru.core.model.session.SessionDeviceId;
import com.surofu.exporteru.core.repository.SessionRepository;
import com.surofu.exporteru.core.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2Handler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final SessionRepository sessionRepository;

    @Value("${app.frontend.oauth.google.redirect.success}")
    private String successRedirectUri;

    @Value("${app.frontend.oauth.google.redirect.error}")
    private String errorRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauthUser = oauthToken.getPrincipal();
            Map<String, Object> attributes = oauthUser.getAttributes();

            String email = (String) attributes.get("email");
            String picture = (String) attributes.get("picture");

            SecurityUser securityUser;

            try {
                securityUser = (SecurityUser) userService.loadUserByUsername(email);
            } catch (Exception e) {
                String redirectUrl = String.format("%s?email=%s&picture=%s",
                        errorRedirectUri,
                        URLEncoder.encode(email, StandardCharsets.UTF_8),
                        URLEncoder.encode(picture, StandardCharsets.UTF_8)
                );
                response.sendRedirect(redirectUrl);
                return;
            }

            String accessToken = jwtUtils.generateAccessToken(securityUser);
            String refreshToken = jwtUtils.generateRefreshToken(securityUser);

            try {
                SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
                Session oldSession = sessionRepository
                        .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
                        .orElse(new Session());
                Session session = Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);
                sessionRepository.save(session);

                String uriWithTokens = "%s?accessToken=%s&refreshToken=%s"
                        .formatted(successRedirectUri, accessToken, refreshToken);
                response.sendRedirect(uriWithTokens);
            } catch (Exception e) {
                log.error("An error occurred while saving session", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Something went wrong");
            }
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unsupported authentication type");
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.sendRedirect(errorRedirectUri + "?error=" + exception.getMessage());
    }
}
