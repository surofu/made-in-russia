package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.RefreshMeCurrentSessionCommand;
import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.application.query.me.GetMeCurrentSessionQuery;
import com.surofu.madeinrussia.application.query.me.GetMeQuery;
import com.surofu.madeinrussia.application.query.me.GetMeSessionsQuery;
import com.surofu.madeinrussia.application.security.SecurityUser;
import com.surofu.madeinrussia.application.utils.IpAddressUtils;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.GetMe;
import com.surofu.madeinrussia.core.service.me.operation.GetMeCurrentSession;
import com.surofu.madeinrussia.core.service.me.operation.GetMeSessions;
import com.surofu.madeinrussia.core.service.me.operation.RefreshMeCurrentSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/me")
@Tag(
        name = "User Profile",
        description = "API for accessing current user information and session management"
)
public class MeRestController {
    private final MeService meService;
    private final IpAddressUtils ipAddressUtils;

    private final GetMe.Result.Processor<ResponseEntity<?>> getMeByJwtProcessor;
    private final GetMeSessions.Result.Processor<ResponseEntity<?>> getMeSessionsProcessor;
    private final GetMeCurrentSession.Result.Processor<ResponseEntity<?>> getMeCurrentSessionProcessor;
    private final RefreshMeCurrentSession.Result.Processor<ResponseEntity<?>> refreshMeCurrentSessionProcessor;

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get current user profile",
            description = """
                    Returns complete profile information for the authenticated user.
                    Requires USER role.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user profile",
                            content = @Content(
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions"
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getMeByJwt(@AuthenticationPrincipal SecurityUser securityUser) {
        GetMeQuery query = new GetMeQuery(securityUser);
        GetMe operation = GetMe.of(query);
        return meService.getMeByJwt(operation).process(getMeByJwtProcessor);
    }

    @GetMapping("sessions")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get user sessions",
            description = """
                    Returns list of all active sessions for the authenticated user.
                    Includes device and location information.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved sessions",
                            content = @Content(
                                    schema = @Schema(implementation = List.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions"
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getMeSessions(@AuthenticationPrincipal SecurityUser securityUser) {
        GetMeSessionsQuery query = new GetMeSessionsQuery(securityUser);
        GetMeSessions operation = GetMeSessions.of(query);
        return meService.getMeSessions(operation).process(getMeSessionsProcessor);
    }

    @GetMapping("current-session")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get current session details",
            description = """
                    Returns detailed information about the current authentication session.
                    Includes IP address, device info and last activity.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved session",
                            content = @Content(
                                    schema = @Schema(implementation = SessionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions"
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getMeCurrentSession(@AuthenticationPrincipal SecurityUser securityUser) {
        GetMeCurrentSessionQuery query = new GetMeCurrentSessionQuery(securityUser);
        GetMeCurrentSession operation = GetMeCurrentSession.of(query);
        return meService.getMeCurrentSession(operation).process(getMeCurrentSessionProcessor);
    }

    @PatchMapping("current-session/refresh")
    @Operation(
            summary = "Refresh current session",
            description = """
                    Extends the expiration time of the current session.
                    Requires valid refresh token.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Session refreshed successfully",
                            content = @Content(
                                    schema = @Schema(implementation = TokenDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid refresh token",
                            content = @Content(
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or expired refresh token",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> refreshMeCurrentSessionRefresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshMeCurrentSessionCommand.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                                    }"""
                                    )
                            }
                    )
            )
            @Valid @RequestBody RefreshMeCurrentSessionCommand refreshMeCurrentSessionCommand,
            HttpServletRequest request
    ) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = ipAddressUtils.getClientIpAddressFromHttpRequest(request);

        RefreshMeCurrentSession operation = RefreshMeCurrentSession.of(refreshMeCurrentSessionCommand, userAgent, ipAddress);
        return meService.refreshMeCurrentSession(operation).process(refreshMeCurrentSessionProcessor);
    }
}