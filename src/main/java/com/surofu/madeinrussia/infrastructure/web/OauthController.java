package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.components.telegrambot.TelegramBot;
import com.surofu.madeinrussia.application.dto.auth.LoginSuccessDto;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.model.telegram.TelegramUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping("api/v1/oauth2")
@RequiredArgsConstructor
@Tag(name = "OAuth2 Authentication", description = "Endpoints for OAuth2 authentication flows")
public class OauthController {
    private final TelegramBot telegramBot;
    private final LocalizationManager localizationManager;

    @GetMapping("google")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Initiate Google OAuth2 flow",
            description = "Redirects to Google's OAuth2 authorization endpoint to start the authentication flow",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirect to Google's authorization page"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error if redirection fails"
                    )
            }
    )
    public void google(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/v1/oauth2/authorize/google");
    }

    @PostMapping("telegram/callback")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Processing telegram authorization",
            description = "Start the authentication flow",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Returning authenticated user jwt",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginSuccessDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "202",
                            description = "User not authenticated -> auth with telegram bot"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error if redirection fails"
                    )
            }
    )
    public ResponseEntity<?> callbackGet(@RequestBody TelegramUser user, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        telegramBot.register(user, SessionInfo.of(request), locale);
        return ResponseEntity.ok().build();
    }
}
