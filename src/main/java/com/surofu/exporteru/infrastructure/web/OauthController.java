package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.components.telegrambot.TelegramBot;
import com.surofu.exporteru.application.dto.auth.LoginSuccessDto;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.telegram.TelegramUser;
import com.surofu.exporteru.infrastructure.config.telegrambot.ChinaTelegramBotConfig;
import com.surofu.exporteru.infrastructure.config.telegrambot.EnglishTelegramBotConfig;
import com.surofu.exporteru.infrastructure.config.telegrambot.RussianTelegramBotConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("api/v1/oauth2")
@RequiredArgsConstructor
@Tag(name = "OAuth2 Authentication", description = "Endpoints for OAuth2 authentication flows")
public class OauthController {
  private final TelegramBot russianTelegramBot;
  private final TelegramBot englishTelegramBot;
  private final TelegramBot chinaTelegramBot;
  private final RussianTelegramBotConfig russianTelegramBotConfig;
  private final EnglishTelegramBotConfig englishTelegramBotConfig;
  private final ChinaTelegramBotConfig chinaTelegramBotConfig;

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
  public ResponseEntity<?> callbackGet(@RequestBody @Valid TelegramUser user,
                                       HttpServletRequest request) {
    Locale locale = LocaleContextHolder.getLocale();
    TelegramBot telegramBot = getTelegramBot(locale);

    if (telegramBot != null) {
      SessionInfo sessionInfo = SessionInfo.of(request);
      telegramBot.authorize(user, sessionInfo, locale);
    } else {
      log.warn("No active telegram bot found for locale: {}", locale.getLanguage());
    }

    return ResponseEntity.ok().build();
  }

  private TelegramBot getTelegramBot(Locale locale) {
    String language = locale.getLanguage();

    if (language.equals("ru") && russianTelegramBotConfig.getEnable()) {
      return russianTelegramBot;
    }

    if (language.equals("en") && englishTelegramBotConfig.getEnable()) {
      return englishTelegramBot;
    }

    if (language.equals("zh") && chinaTelegramBotConfig.getEnable()) {
      return chinaTelegramBot;
    }

    if (englishTelegramBotConfig.getEnable()) {
      return englishTelegramBot;
    }

    return null;
  }
}
